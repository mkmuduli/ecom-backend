#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/.."

docker-compose up -d --wait

for database in ecom_product_db ecom_inventory_db ecom_payment_db ecom_order_db ecom_auth_db; do
    exists="$(docker exec shop-postgres psql -U postgres -d postgres -tAc \
        "SELECT 1 FROM pg_database WHERE datname = '${database}'")"
    if [[ "${exists//[[:space:]]/}" != "1" ]]; then
        docker exec shop-postgres createdb -U postgres "${database}"
    fi
done

echo "PostgreSQL, Redis, Kafka, and the service databases are ready."
