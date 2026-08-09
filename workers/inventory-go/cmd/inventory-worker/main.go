package main

import (
	"dev/muliroz/inventory-worker/internal/database"
	"fmt"
	"log"
	"os"
)

func main() {
	log.SetPrefix("main: ")
	log.SetFlags(0)

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