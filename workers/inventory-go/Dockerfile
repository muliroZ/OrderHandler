FROM golang:1.26.5-alpine AS builder
WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . .

RUN CGO_ENABLED=0 GOOS=linux go build -o /worker ./cmd/inventory-worker

FROM alpine:3.22
WORKDIR /app

COPY --from=builder /worker /app/worker
COPY migrations /app/migrations

CMD [ "/app/worker" ]