#!/bin/bash
set -e

if [ -n "$DATABASES_TO_CREATE" ]; then
    echo "Iniciando..."

    for db_info in $DATABASES_TO_CREATE; do
        db_name=$(echo $db_info | cut -d: -f1)
        db_user=$(echo $db_info | cut -d: -f2)
        db_pass=$(echo $db_info | cut -d: -f3)

        echo "Banco: $db_name | Usuário: $db_user" 

        psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
            CREATE USER $db_user WITH ENCRYPTED PASSWORD '$db_pass';
            CREATE DATABASE $db_name OWNER $db_user;
EOSQL

        psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
            GRANT ALL ON SCHEMA public TO $db_user;
EOSQL
    done
    echo "Sucesso"
fi