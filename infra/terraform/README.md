# Pluto AWS Terraform Deployment

This Terraform stack provisions AWS infrastructure for deploying the full Pluto platform:

- VPC with public/private subnets, IGW, NAT
- ECS Fargate cluster for all services
- ECR repositories for application images
- ALB routing for frontend and API traffic
- RDS PostgreSQL
- ElastiCache Redis
- RabbitMQ (containerized on ECS)
- S3 bucket for submission diagram storage
- Cloud Map service discovery for internal service-to-service communication

## Services deployed

- client
- gateway-service
- auth-service
- problem-service
- submission-service
- user-profile-service
- discussion-service
- evaluation-service
- rabbitmq

## Important assumptions

1. **Container images already exist** in ECR (or are otherwise pullable).
2. **Client image serves static assets** on `client_container_port` (default `80`).
3. `submission-service` currently expects explicit `AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY` environment variables in application code.
4. `auth-service`, `gateway-service`, and `discussion-service` read JWT values from Java system properties; this stack sets them via `JAVA_TOOL_OPTIONS`.
5. `evaluation-service` depends on an Ollama endpoint (`ollama_base_url`).

## Usage

```bash
cd /home/runner/work/pluto/pluto/infra/terraform
cp terraform.tfvars.example terraform.tfvars
# edit terraform.tfvars with real values

terraform init
terraform plan
terraform apply
```

## Post-deploy

- Use `public_app_url` output as the frontend URL.
- Point `cors_allowed_origin` to the frontend URL/domain.
- Push image updates and roll ECS services as needed.

## Destroy

```bash
terraform destroy
```
