#!/usr/bin/env bash
set -euo pipefail

service_poms=(
    "web-service/product/pom.xml"
    "web-service/inventory/pom.xml"
    "web-service/payment/pom.xml"
    "web-service/order/pom.xml"
    "infra/ecom-api-gateway/pom.xml"
)

for pom in "${service_poms[@]}"; do
    # Match only the Maven process launched for this service by the VS Code tasks.
    pattern="${pom//\//\\/}.*spring-boot:run"
    if pkill -f -- "$pattern" 2>/dev/null; then
        echo "Stopped service task: $pom"
    else
        echo "No running service task found: $pom"
    fi
done
