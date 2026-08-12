package main

import (
	"dev/muliroz/inventory-worker/internal/database"
	"fmt"
	"log"
	"os"

	"github.com/twmb/franz-go/pkg/kgo"
)

func main() {
	log.SetPrefix("main: ")
	log.SetFlags(0)

	brokers := []string{"kafka:29096"}

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
		kgo.ConsumeTopics("orders-created", "orders-cancelled"),
		kgo.DisableAutoCommit(),
	)
	if err != nil {
		log.Printf("Erro ao criar cliente do consumer: %v", err)
	}
	defer consumerClient.Close()

	databaseURL := fmt.Sprintf(
		"postgres://%s:%s@%s:%s/%s?sslmode=disabled",
		os.Getenv("DATABASE_USER"),
		os.Getenv("DATABASE_PASS"),
		os.Getenv("DATABASE_HOST"),
		os.Getenv("DATABASE_PORT"),
		os.Getenv("DATABASE_NAME"),
	)

	if err := database.RunMigrations(databaseURL); err != nil {
		log.Fatal(err)
	}
}