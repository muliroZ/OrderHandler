package domain

import "errors"

var (
	ErrInsufficientStock = errors.New("insufficient stock")
	ErrItemNotFound = errors.New("item not found")
	ErrInvalidAmount = errors.New("invalid amount")
)