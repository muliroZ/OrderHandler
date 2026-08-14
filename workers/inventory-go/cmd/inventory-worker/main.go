package main

import (
	"context"
	"dev/muliroz/inventory-worker/internal/database"
	"dev/muliroz/inventory-worker/internal/domain"
	"dev/muliroz/inventory-worker/internal/kafka"
	"dev/muliroz/inventory-worker/internal/repository"
	"errors"
	"fmt"
	"log"
	"os"
	"os/signal"
	"sync"
	"syscall"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/twmb/franz-go/pkg/kgo"
)

func main() {
	ctx, stop := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
	defer stop()

	log.SetPrefix("main: ")
	log.SetFlags(0)

	// kafka
	brokers := []string{"kafka:29092"}

	producerClient, err := kgo.NewClient(
		kgo.SeedBrokers(brokers...),
	)
	if err != nil {
		log.Printf("Erro ao criar cliente do producer: %v", err)
	}
	defer producerClient.Close()

	consumerClient, err := kgo.NewClient(
		kgo.SeedBrokers(brokers...),
		kgo.ConsumerGroup("inventory-worker-group"),
		kgo.ConsumeTopics("orders-created", "orders-cancelled", "items-created"),
		kgo.DisableAutoCommit(),
	)
	if err != nil {
		log.Printf("Erro ao criar cliente do consumer: %v", err)
	}
	defer consumerClient.Close()

	// banco
	databaseURL := fmt.Sprintf(
		"postgres://%s:%s@%s:%s/%s?sslmode=disable",
		os.Getenv("DATABASE_USER"),
		os.Getenv("DATABASE_PASS"),
		os.Getenv("DATABASE_HOST"),
		os.Getenv("DATABASE_PORT"),
		os.Getenv("DATABASE_NAME"),
	)

	if err := database.RunMigrations(databaseURL); err != nil {
		log.Fatal(err)
	}

	dbPool, err := initDB(ctx, databaseURL)
	if err != nil {
		log.Fatalf("Erro ao conectar no PostgreSQL: %v", err)
	}
	defer dbPool.Close()

	log.Println("PostgreSQL conectado com sucesso.")

	repo := repository.NewPostgresRepository(dbPool)
	producer := kafka.NewEventProducer(producerClient)
	service := domain.NewInventoryService(repo, producer)
	consumer := kafka.NewOrderConsumer(service, consumerClient)

	var wg sync.WaitGroup

	wg.Go(func() {
		log.Println("Consumer iniciado e escutando tópicos...")
		if err := consumer.Start(ctx); err != nil && !errors.Is(err, context.Canceled) {
			log.Printf("Erro inesperado na execução do consumer: %v", err)
		}
	})

	<-ctx.Done()
	log.Println("Sinal de desligamento recebido. Aguardando conclusão dos processamentos em andamentos...")

	wg.Wait()
	log.Println("Aplicação finalizada com sucesso.")
}

func initDB(ctx context.Context, db_conn_str string) (*pgxpool.Pool, error) {
	config, err := pgxpool.ParseConfig(db_conn_str)
	if err != nil {
		return nil, err
	}

	config.MaxConns = 25
	config.MinConns = 5
	config.MaxConnLifetime = 1 * time.Hour
	config.MaxConnIdleTime = 15 * time.Minute

	pool, err := pgxpool.NewWithConfig(ctx, config)
	if err != nil {
		return nil, err
	}

	pingCtx, cancel := context.WithTimeout(ctx, 5*time.Second)
	defer cancel()

	if err := pool.Ping(pingCtx); err != nil {
		return nil, err
	}

	return pool, nil
}