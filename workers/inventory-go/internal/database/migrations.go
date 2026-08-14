package database

import (
	"log"

	"github.com/golang-migrate/migrate/v4"
	_ "github.com/golang-migrate/migrate/v4/database/postgres"
	_ "github.com/golang-migrate/migrate/v4/source/file"
)

func RunMigrations(databaseURL string) error {
	log.SetPrefix("database: ")
	log.SetFlags(0)

	migrations, err := migrate.New(
		"file://migrations",
		databaseURL,
	)
	if err != nil {
		log.Fatalf("creating migrator: %v", err)
	}

	err = migrations.Up()
	if err != nil && err != migrate.ErrNoChange {
		log.Fatalf("running migrations: %v", err)
	}

	return nil
}