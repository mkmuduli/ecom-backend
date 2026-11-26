# Kubernetes deployment

These manifests deploy the five Spring services, API gateway, PostgreSQL,
Redis, and a single-node Kafka broker into the `ecommerce` namespace.

The namespace and application Secret must exist before deploying. Keep the
Secret out of source control. For a fresh development cluster, create it with:

```sh
kubectl apply -f k8s/namespace.yaml
kubectl -n ecommerce create secret generic ecom-secrets \\
  --from-literal=POSTGRES_PASSWORD=postgres \\
  --from-literal=JWT_SECRET="$(openssl rand -base64 48)"
```

For an existing cluster, preserve its `POSTGRES_PASSWORD` value and add a
`JWT_SECRET` through your secret manager or `kubectl patch`. The PostgreSQL
password must match the value used by the database and service pods.

The application images are published by this repository's GitHub Actions
workflow as `ghcr.io/<repository-owner>/<service>-service:latest`. Confirm the
images exist and are readable by your cluster; set `imagePullSecrets` on the
pods if the packages are private.

Apply the remaining manifests with:

```sh
kubectl apply -k k8s/
kubectl -n ecommerce get pods,services
```

The gateway Service uses `LoadBalancer`. On a local cluster without a load
balancer implementation, access it with:

```sh
kubectl -n ecommerce port-forward service/api-gateway 8080:80
```

PostgreSQL uses a 5 GiB PVC and a StatefulSet, so its data survives pod restarts
and node reboots on the same host. Its initialization script creates the app
databases only when the data directory is first created; an existing volume
will not be modified.
The included Kafka and database configurations are single-replica development
defaults, not highly available production configurations.
