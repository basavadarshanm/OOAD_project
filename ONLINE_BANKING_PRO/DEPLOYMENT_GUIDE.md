# Deployment & DevOps Guide - Online Banking System

**Version:** 1.0.0  
**Target Environments:** Development, Staging, Production  
**Infrastructure:** AWS, Docker, Kubernetes  
**Status:** Production-Ready

---

## Table of Contents

1. [Quick Start Deployment](#quick-start-deployment)
2. [Docker Setup](#docker-setup)
3. [Kubernetes Deployment](#kubernetes-deployment)
4. [CI/CD Pipeline](#cicd-pipeline)
5. [Infrastructure as Code (Terraform)](#infrastructure-as-code-terraform)
6. [Monitoring & Alerting](#monitoring--alerting)
7. [Backup & Disaster Recovery](#backup--disaster-recovery)
8. [Security Hardening](#security-hardening)
9. [Scaling & Performance](#scaling--performance)
10. [Troubleshooting](#troubleshooting)

---

## Quick Start Deployment

### Local Docker Compose

```bash
# 1. Clone repository
git clone https://github.com/yourorg/online-banking.git
cd online-banking

# 2. Build images
docker-compose build

# 3. Start all services
docker-compose up -d

# 4. Verify services
docker-compose ps

# Expected output:
# NAME                  STATUS              PORTS
# banking-backend       Up 2 minutes        0.0.0.0:8080->8080/tcp
# banking-frontend      Up 2 minutes        0.0.0.0:3000->3000/tcp
# postgres-db           Up 2 minutes        5432/tcp
# redis-cache           Up 2 minutes        6379/tcp
# rabbitmq-broker       Up 2 minutes        15672->15672/tcp

# 5. Access services
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080/api/v1
# Swagger Docs: http://localhost:8080/swagger-ui.html
# RabbitMQ Management: http://localhost:15672

# 6. Stop services
docker-compose down
```

---

## Docker Setup

### Backend Dockerfile

**Dockerfile (Backend)**

```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Install security updates
RUN apk update && apk add --no-cache ca-certificates

# Create non-root user for security
RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser

# Copy built application
COPY --from=builder /build/target/banking-service-*.jar app.jar
COPY --from=builder /build/src/main/resources/application-prod.yml .

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run as non-root user
USER appuser

# Start application
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

**Build & Push Backend:**
```bash
# Build image
docker build -t banking-backend:1.0.0 -f Dockerfile .

# Tag for registry
docker tag banking-backend:1.0.0 myregistry.azurecr.io/banking-backend:1.0.0

# Push to container registry
docker push myregistry.azurecr.io/banking-backend:1.0.0

# Run locally
docker run -d \
  --name banking-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/banking \
  -e SPRING_DATASOURCE_USERNAME=banking_user \
  -e SPRING_DATASOURCE_PASSWORD=secure_password \
  myregistry.azurecr.io/banking-backend:1.0.0
```

### Frontend Dockerfile

**Dockerfile (Frontend)**

```dockerfile
# Build stage
FROM node:18-alpine AS builder
WORKDIR /build
COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

# Serve stage
FROM nginx:alpine
COPY --from=builder /build/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
HEALTHCHECK --interval=30s CMD wget --quiet --tries=1 --spider http://localhost/health || exit 1

CMD ["nginx", "-g", "daemon off;"]
```

**nginx.conf (Frontend Proxy)**

```nginx
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;

events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log main;
    sendfile on;
    tcp_nopush on;
    keepalive_timeout 65;
    gzip on;
    gzip_types text/plain text/css text/javascript application/json;

    server {
        listen 80;
        server_name _;
        
        location / {
            root /usr/share/nginx/html;
            try_files $uri $uri/ /index.html;
        }

        location /api {
            proxy_pass http://backend:8080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }
    }
}
```

### Docker Compose Configuration

**docker-compose.yml**

```yaml
version: '3.8'

services:
  # PostgreSQL Database
  postgres:
    image: postgres:15-alpine
    container_name: postgres-db
    environment:
      POSTGRES_DB: banking_prod
      POSTGRES_USER: banking_user
      POSTGRES_PASSWORD: secure_db_password
      POSTGRES_INITDB_ARGS: "-c shared_buffers=256MB -c max_connections=200"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init-db.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U banking_user"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - banking-network

  # Redis Cache
  redis:
    image: redis:7-alpine
    container_name: redis-cache
    command: redis-server --appendonly yes --requirepass redis_password
    volumes:
      - redis_data:/data
    ports:
      - "6379:6379"
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - banking-network

  # RabbitMQ Message Broker
  rabbitmq:
    image: rabbitmq:3.12-management-alpine
    container_name: rabbitmq-broker
    environment:
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin_password
      RABBITMQ_DEFAULT_VHOST: /
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq
    ports:
      - "5672:5672"
      - "15672:15672"
    healthcheck:
      test: ["CMD", "rabbitmq-diagnostics", "-q", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - banking-network

  # Backend Service
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: banking-backend
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/banking_prod
      SPRING_DATASOURCE_USERNAME: banking_user
      SPRING_DATASOURCE_PASSWORD: secure_db_password
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_PASSWORD: redis_password
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_RABBITMQ_USERNAME: admin
      SPRING_RABBITMQ_PASSWORD: admin_password
      JWT_SECRET: ${JWT_SECRET:-your-secret-key-min-32-chars}
      JWT_EXPIRATION: 3600
      LOG_LEVEL: INFO
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
      rabbitmq:
        condition: service_healthy
    ports:
      - "8080:8080"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 3s
      retries: 3
      start_period: 30s
    networks:
      - banking-network

  # Frontend Service
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: banking-frontend
    environment:
      REACT_APP_API_URL: http://backend:8080/api/v1
      REACT_APP_SOCKET_URL: http://backend:8080
    depends_on:
      - backend
    ports:
      - "3000:3000"
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost/health"]
      interval: 30s
      timeout: 3s
      retries: 3
    networks:
      - banking-network

volumes:
  postgres_data:
  redis_data:
  rabbitmq_data:

networks:
  banking-network:
    driver: bridge
```

---

## Kubernetes Deployment

### Kubernetes Service Files

**k8s/namespace.yaml**
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: banking-system
  labels:
    name: banking-system
```

**k8s/backend-deployment.yaml**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: banking-backend
  namespace: banking-system
  labels:
    app: banking-backend
    tier: backend
spec:
  replicas: 3
  minReadySeconds: 30
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 1
  selector:
    matchLabels:
      app: banking-backend
  template:
    metadata:
      labels:
        app: banking-backend
        tier: backend
    spec:
      serviceAccountName: banking-backend
      securityContext:
        runAsNonRoot: true
        runAsUser: 1000
        fsGroup: 1000

      containers:
      - name: backend
        image: myregistry.azurecr.io/banking-backend:1.0.0
        imagePullPolicy: Always
        
        ports:
        - containerPort: 8080
          name: http
          protocol: TCP

        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "k8s"
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            configMapKeyRef:
              name: db-config
              key: url
        - name: SPRING_DATASOURCE_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: username
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: password
        - name: SPRING_REDIS_HOST
          valueFrom:
            configMapKeyRef:
              name: cache-config
              key: redis-host
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: jwt-secret
              key: secret
        - name: LOG_LEVEL
          value: "INFO"
        - name: JVM_OPTS
          value: "-Xms512m -Xmx2g -XX:+UseG1GC"

        resources:
          requests:
            cpu: 500m
            memory: 512Mi
          limits:
            cpu: 2000m
            memory: 2Gi

        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3

        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 20
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 2

        volumeMounts:
        - name: config
          mountPath: /app/config
          readOnly: true

      volumes:
      - name: config
        configMap:
          name: app-config

      affinity:
        podAntiAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
          - weight: 100
            podAffinityTerm:
              labelSelector:
                matchExpressions:
                - key: app
                  operator: In
                  values:
                  - banking-backend
              topologyKey: kubernetes.io/hostname
```

**k8s/backend-service.yaml**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: banking-backend
  namespace: banking-system
  labels:
    app: banking-backend
spec:
  type: ClusterIP
  selector:
    app: banking-backend
  ports:
  - port: 8080
    targetPort: 8080
    protocol: TCP
    name: http
  sessionAffinity: ClientIP
  sessionAffinityConfig:
    clientIP:
      timeoutSeconds: 900
```

**k8s/frontend-deployment.yaml**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: banking-frontend
  namespace: banking-system
spec:
  replicas: 2
  selector:
    matchLabels:
      app: banking-frontend
  template:
    metadata:
      labels:
        app: banking-frontend
    spec:
      containers:
      - name: frontend
        image: myregistry.azurecr.io/banking-frontend:1.0.0
        ports:
        - containerPort: 80
        
        env:
        - name: REACT_APP_API_URL
          value: "https://api.banking.example.com/api/v1"

        resources:
          requests:
            cpu: 100m
            memory: 128Mi
          limits:
            cpu: 500m
            memory: 512Mi

        livenessProbe:
          httpGet:
            path: /health
            port: 80
          initialDelaySeconds: 10
          periodSeconds: 10
```

**k8s/ingress.yaml**
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: banking-ingress
  namespace: banking-system
  annotations:
    kubernetes.io/ingress.class: nginx
    cert-manager.io/cluster-issuer: letsencrypt-prod
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/rate-limit: "100"
spec:
  tls:
  - hosts:
    - api.banking.example.com
    - banking.example.com
    secretName: banking-tls
  rules:
  - host: api.banking.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: banking-backend
            port:
              number: 8080
  - host: banking.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: banking-frontend
            port:
              number: 80
```

**Deploy to Kubernetes:**
```bash
# Create namespace
kubectl apply -f k8s/namespace.yaml

# Create secrets and configs
kubectl create secret generic db-credentials \
  --from-literal=username=banking_user \
  --from-literal=password=secure_password \
  -n banking-system

kubectl create secret generic jwt-secret \
  --from-literal=secret=your-secret-key-min-32-chars \
  -n banking-system

kubectl create configmap db-config \
  --from-literal=url=jdbc:postgresql://postgres:5432/banking_prod \
  -n banking-system

# Deploy services
kubectl apply -f k8s/backend-deployment.yaml
kubectl apply -f k8s/backend-service.yaml
kubectl apply -f k8s/frontend-deployment.yaml
kubectl apply -f k8s/ingress.yaml

# Verify deployment
kubectl get deployments -n banking-system
kubectl get pods -n banking-system
kubectl get svc -n banking-system

# Scale backend
kubectl scale deployment banking-backend --replicas=5 -n banking-system

# View logs
kubectl logs -f deployment/banking-backend -n banking-system

# Port forward for local testing
kubectl port-forward svc/banking-backend 8080:8080 -n banking-system
```

---

## CI/CD Pipeline

### GitHub Actions Workflow

**.github/workflows/deploy.yml**

```yaml
name: Build & Deploy

on:
  push:
    branches: [main, develop]
    tags: 
      - 'v*'
  pull_request:
    branches: [develop]

env:
  REGISTRY: myregistry.azurecr.io
  IMAGE_NAME_BACKEND: banking-backend
  IMAGE_NAME_FRONTEND: banking-frontend

jobs:
  build:
    runs-on: ubuntu-latest
    
    permissions:
      contents: read
      packages: write

    outputs:
      backend-version: ${{ steps.build-backend.outputs.version }}
      frontend-version: ${{ steps.build-frontend.outputs.version }}

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 21
      uses: actions/setup-java@v3
      with:
        java-version: '21'
        distribution: 'temurin'
        cache: maven

    - name: Set up Node
      uses: actions/setup-node@v3
      with:
        node-version: '18'
        cache: npm
        cache-dependency-path: frontend/package-lock.json

    - name: Extract version
      id: extract-version
      run: echo "version=$(date +%s)" >> $GITHUB_OUTPUT

    # Build Backend
    - name: Build Backend
      id: build-backend
      run: |
        cd backend
        mvn clean package -DskipTests
        echo "version=${{ steps.extract-version.outputs.version }}" >> $GITHUB_OUTPUT
      
    # Build Frontend
    - name: Build Frontend
      id: build-frontend
      run: |
        cd frontend
        npm ci
        npm run build
        echo "version=${{ steps.extract-version.outputs.version }}" >> $GITHUB_OUTPUT

    # Login to ACR
    - name: Login to Azure Container Registry
      uses: azure/docker-login@v1
      with:
        login-server: ${{ env.REGISTRY }}
        username: ${{ secrets.ACR_USERNAME }}
        password: ${{ secrets.ACR_PASSWORD }}

    # Build Backend Image
    - name: Build Backend Docker Image
      run: |
        docker build backend -t ${{ env.REGISTRY }}/${{ env.IMAGE_NAME_BACKEND }}:${{ steps.extract-version.outputs.version }}
        docker tag ${{ env.REGISTRY }}/${{ env.IMAGE_NAME_BACKEND }}:${{ steps.extract-version.outputs.version }} ${{ env.REGISTRY }}/${{ env.IMAGE_NAME_BACKEND }}:latest

    # Build Frontend Image
    - name: Build Frontend Docker Image
      run: |
        docker build frontend -t ${{ env.REGISTRY }}/${{ env.IMAGE_NAME_FRONTEND }}:${{ steps.extract-version.outputs.version }}
        docker tag ${{ env.REGISTRY }}/${{ env.IMAGE_NAME_FRONTEND }}:${{ steps.extract-version.outputs.version }} ${{ env.REGISTRY }}/${{ env.IMAGE_NAME_FRONTEND }}:latest

    # Push images
    - name: Push Backend Image
      run: docker push ${{ env.REGISTRY }}/${{ env.IMAGE_NAME_BACKEND }}:${{ steps.extract-version.outputs.version }}

    - name: Push Frontend Image
      run: docker push ${{ env.REGISTRY }}/${{ env.IMAGE_NAME_FRONTEND }}:${{ steps.extract-version.outputs.version }}

  test:
    needs: build
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_DB: banking_test
          POSTGRES_PASSWORD: password
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 21
      uses: actions/setup-java@v3
      with:
        java-version: '21'
        distribution: 'temurin'
        cache: maven

    - name: Run Tests
      run: |
        cd backend
        mvn clean test

    - name: Upload Coverage
      uses: codecov/codecov-action@v3

  deploy-staging:
    needs: [build, test]
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/develop'

    steps:
    - name: Deploy to AKS Staging
      run: |
        echo "Deploying to staging..."
        # Add your deployment commands here
        # E.g., helm upgrade, kubectl apply, etc.

  deploy-production:
    needs: [build, test]
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main' && startsWith(github.ref, 'refs/tags/')

    steps:
    - name: Deploy to AKS Production
      run: |
        echo "Deploying to production..."
        # Add your deployment commands here
```

---

## Infrastructure as Code (Terraform)

### Terraform Configuration

**terraform/main.tf**

```hcl
terraform {
  required_version = ">= 1.0"
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.0"
    }
  }

  backend "azurerm" {
    resource_group_name  = "terraform-state"
    storage_account_name = "tfstate"
    container_name       = "tfstate"
    key                  = "banking.tfstate"
  }
}

provider "azurerm" {
  features {}
}

# Resource Group
resource "azurerm_resource_group" "banking" {
  name     = "rg-banking-${var.environment}"
  location = var.location
  tags     = var.tags
}

# Virtual Network
resource "azurerm_virtual_network" "banking" {
  name                = "vnet-banking-${var.environment}"
  address_space       = ["10.0.0.0/16"]
  location            = azurerm_resource_group.banking.location
  resource_group_name = azurerm_resource_group.banking.name
}

resource "azurerm_subnet" "aks" {
  name                 = "subnet-aks"
  resource_group_name  = azurerm_resource_group.banking.name
  virtual_network_name = azurerm_virtual_network.banking.name
  address_prefixes     = ["10.0.1.0/24"]
}

resource "azurerm_subnet" "database" {
  name                 = "subnet-database"
  resource_group_name  = azurerm_resource_group.banking.name
  virtual_network_name = azurerm_virtual_network.banking.name
  address_prefixes     = ["10.0.2.0/24"]
  service_endpoints    = ["Microsoft.Sql"]
}

# AKS Cluster
resource "azurerm_kubernetes_cluster" "banking" {
  name                = "aks-banking-${var.environment}"
  location            = azurerm_resource_group.banking.location
  resource_group_name = azurerm_resource_group.banking.name
  dns_prefix          = "banking-${var.environment}"
  kubernetes_version  = "1.27"

  default_node_pool {
    name       = "default"
    node_count = 3
    vm_size    = "Standard_D2s_v3"

    vnet_subnet_id = azurerm_subnet.aks.id

    enable_auto_scaling = true
    min_count           = 3
    max_count           = 10
  }

  identity {
    type = "SystemAssigned"
  }

  network_profile {
    network_plugin = "azure"
    service_cidr   = "10.1.0.0/16"
    dns_service_ip = "10.1.0.10"
  }

  tags = var.tags
}

# PostgreSQL Database
resource "azurerm_postgresql_server" "banking" {
  name                = "postgres-banking-${var.environment}"
  location            = azurerm_resource_group.banking.location
  resource_group_name = azurerm_resource_group.banking.name

  sku_name   = "B_Gen5_2"
  version    = "13"
  storage_mb = 51200

  backup_retention_days        = 30
  geo_redundant_backup_enabled = true
  auto_grow_enabled            = true

  ssl_enforcement_enabled = true
}

resource "azurerm_postgresql_database" "banking" {
  name                = "banking_db"
  resource_group_name = azurerm_resource_group.banking.name
  server_name         = azurerm_postgresql_server.banking.name
  charset             = "UTF8"
}

# Redis Cache
resource "azurerm_redis_cache" "banking" {
  name                = "redis-banking-${var.environment}"
  location            = azurerm_resource_group.banking.location
  resource_group_name = azurerm_resource_group.banking.name
  capacity            = 2
  family              = "C"
  sku_name            = "Standard"
  enable_non_ssl_port = false
  minimum_tls_version = "1.2"
}

# Container Registry
resource "azurerm_container_registry" "banking" {
  name                = "crbanking${replace(var.environment, "-", "")}"
  location            = azurerm_resource_group.banking.location
  resource_group_name = azurerm_resource_group.banking.name
  sku                 = "Standard"
  admin_enabled       = true
}

# Log Analytics Workspace
resource "azurerm_log_analytics_workspace" "banking" {
  name                = "log-banking-${var.environment}"
  location            = azurerm_resource_group.banking.location
  resource_group_name = azurerm_resource_group.banking.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
}

# Application Insights
resource "azurerm_application_insights" "banking" {
  name                = "ai-banking-${var.environment}"
  location            = azurerm_resource_group.banking.location
  resource_group_name = azurerm_resource_group.banking.name
  application_type    = "web"
  workspace_id        = azurerm_log_analytics_workspace.banking.id
}

output "aks_client_certificate" {
  value     = azurerm_kubernetes_cluster.banking.kube_config.0.client_certificate
  sensitive = true
}

output "kube_config" {
  value     = azurerm_kubernetes_cluster.banking.kube_config_raw
  sensitive = true
}
```

**terraform/variables.tf**

```hcl
variable "environment" {
  description = "Environment name (dev, staging, prod)"
  type        = string
  default     = "dev"
}

variable "location" {
  description = "Azure region"
  type        = string
  default     = "East US"
}

variable "tags" {
  description = "Common tags for resources"
  type        = map(string)
  default = {
    Project     = "Banking"
    Environment = "Production"
    ManagedBy   = "Terraform"
  }
}
```

**Deploy Infrastructure:**
```bash
# Initialize Terraform
terraform init

# Plan deployment
terraform plan -var="environment=prod" -out=tfplan

# Apply changes
terraform apply tfplan

# Get outputs
terraform output kube_config > kubeconfig.yaml
```

---

## Monitoring & Alerting

### Prometheus Configuration

**monitoring/prometheus.yml**

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

alerting:
  alertmanagers:
    - static_configs:
        - targets:
            - alertmanager:9093

rule_files:
  - /etc/prometheus/rules/*.yml

scrape_configs:
  - job_name: 'banking-backend'
    kubernetes_sd_configs:
      - role: pod
        namespaces:
          names:
            - banking-system
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_label_app]
        action: keep
        regex: banking-backend
      - source_labels: [__address__]
        action: replace
        target_label: instance
        replacement: '$1:8080/actuator/prometheus'

  - job_name: 'postgres'
    static_configs:
      - targets: ['postgres-exporter:9187']

  - job_name: 'redis'
    static_configs:
      - targets: ['redis-exporter:9121']
```

### Alert Rules

**monitoring/alerts.yml**

```yaml
groups:
  - name: banking-alerts
    interval: 30s
    rules:
      # API Response Time Alert
      - alert: HighAPIResponseTime
        expr: histogram_quantile(0.99, rate(http_request_duration_seconds_bucket[5m])) > 0.5
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High API response time detected"
          description: "API p99 response time is {{ $value }}s"

      # Error Rate Alert
      - alert: HighErrorRate
        expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.001
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "High error rate detected"
          description: "Error rate is {{ $value }}"

      # Database Connection Alert
      - alert: HighDatabaseConnections
        expr: pg_stat_activity_count > 150
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High database connections"
          description: "Active connections: {{ $value }}"

      # Memory Alert
      - alert: HighMemoryUsage
        expr: container_memory_usage_bytes / container_spec_memory_limit_bytes > 0.85
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High memory usage in pod {{ $labels.pod }}"
```

### Grafana Dashboards

```json
{
  "dashboard": {
    "title": "Banking System Metrics",
    "panels": [
      {
        "id": 1,
        "title": "API Response Time (p99)",
        "targets": [
          {
            "expr": "histogram_quantile(0.99, rate(http_request_duration_seconds_bucket[5m]))"
          }
        ]
      },
      {
        "id": 2,
        "title": "Transaction Success Rate",
        "targets": [
          {
            "expr": "rate(transactions_total{status='success'}[5m])"
          }
        ]
      },
      {
        "id": 3,
        "title": "Fraud Detection Alerts",
        "targets": [
          {
            "expr": "rate(fraud_alerts_total[5m])"
          }
        ]
      }
    ]
  }
}
```

---

## Backup & Disaster Recovery

### Backup Strategy

```bash
#!/bin/bash
# Daily PostgreSQL backup to Azure Blob Storage

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="banking_backup_${TIMESTAMP}.sql.gz"
BACKUP_PATH="/backups/${BACKUP_FILE}"

# Backup database
pg_dump -h $DB_HOST -U $DB_USER $DB_NAME | gzip > $BACKUP_PATH

# Upload to Azure Blob Storage
az storage blob upload \
  --account-name $STORAGE_ACCOUNT \
  --container-name backups \
  --file $BACKUP_PATH \
  --name $BACKUP_FILE

# Cleanup old backups (keep last 30 days)
find /backups -name "banking_backup_*.sql.gz" -mtime +30 -delete

# Verify backup
tar -tzf $BACKUP_PATH > /dev/null && echo "Backup successful" || echo "Backup failed"
```

### Disaster Recovery Test

```bash
# Monthly DR drill - restore to staging
docker exec postgres pg_restore -h localhost -U banking_user banking_db < backup_latest.sql

# Verify data integrity
psql -h localhost -U banking_user banking_db -c "SELECT COUNT(*) FROM users;"

# Run smoke tests
./run-smoke-tests.sh
```

---

## Security Hardening

### Network Security

```bash
# Kubernetes Network Policies
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: banking-network-policy
spec:
  podSelector:
    matchLabels:
      app: banking-backend
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          app: banking-frontend
    ports:
    - protocol: TCP
      port: 8080
  egress:
  - to:
    - podSelector:
        matchLabels:
          app: postgres
    ports:
    - protocol: TCP
      port: 5432
```

### Secret Management

```bash
# Using Azure Key Vault
az keyvault create --name banking-secrets --resource-group banking-prod

# Store secrets
az keyvault secret set --vault-name banking-secrets --name db-password --value <password>
az keyvault secret set --vault-name banking-secrets --name jwt-secret --value <jwt-secret>

# Retrieve in pod
az keyvault secret show --vault-name banking-secrets --name db-password
```

---

## Scaling & Performance

### Horizontal Pod Autoscaler

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: banking-backend-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: banking-backend
  minReplicas: 3
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
      - type: Percent
        value: 50
        periodSeconds: 60
    scaleUp:
      stabilizationWindowSeconds: 0
      policies:
      - type: Percent
        value: 100
        periodSeconds: 30
```

---

## Troubleshooting

### Common Issues

**Pod Pending State:**
```bash
kubectl describe pod banking-backend-xyz -n banking-system
# Check resource availability, node affinity, volume mounts
```

**Database Connection Errors:**
```bash
# Check database connectivity
kubectl exec -it banking-backend-xyz -n banking-system -- \
  psql -h postgres -U banking_user -d banking_db -c "SELECT 1"
```

**High Memory Usage:**
```bash
# Check JVM settings
docker exec banking-backend jps -m
# Adjust -Xmx flag if needed
```

---

**This deployment guide ensures production-ready infrastructure with high availability, security, and scalability.**
