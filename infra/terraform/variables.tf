variable "project_name" {
  description = "Project prefix used in AWS resource names"
  type        = string
  default     = "pluto"
}

variable "aws_region" {
  description = "AWS region for deployment"
  type        = string
  default     = "us-east-1"
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.20.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "CIDR blocks for public subnets"
  type        = list(string)
  default     = ["10.20.0.0/20", "10.20.16.0/20"]
}

variable "private_subnet_cidrs" {
  description = "CIDR blocks for private subnets"
  type        = list(string)
  default     = ["10.20.32.0/20", "10.20.48.0/20"]
}

variable "db_name" {
  description = "PostgreSQL database name"
  type        = string
  default     = "pluto"
}

variable "db_username" {
  description = "PostgreSQL admin username"
  type        = string
  default     = "pluto"
}

variable "db_password" {
  description = "PostgreSQL admin password"
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "JWT secret used by auth, gateway and discussion services"
  type        = string
  sensitive   = true
}

variable "jwt_user_secret" {
  description = "Optional JWT user secret override for gateway"
  type        = string
  default     = ""
  sensitive   = true
}

variable "jwt_admin_secret" {
  description = "Optional JWT admin secret override for gateway"
  type        = string
  default     = ""
  sensitive   = true
}

variable "jwt_expires_in" {
  description = "JWT expiration string consumed by auth-service"
  type        = string
  default     = "15m"
}

variable "rabbitmq_username" {
  description = "RabbitMQ username"
  type        = string
  default     = "pluto"
}

variable "rabbitmq_password" {
  description = "RabbitMQ password"
  type        = string
  sensitive   = true
}

variable "service_discovery_namespace" {
  description = "Private DNS namespace for ECS service discovery"
  type        = string
  default     = "pluto.local"
}

variable "cors_allowed_origin" {
  description = "Allowed CORS origin for the gateway"
  type        = string
  default     = "*"
}

variable "s3_prefix" {
  description = "S3 object key prefix for submission diagrams"
  type        = string
  default     = "submissions"
}

variable "aws_access_key_id" {
  description = "Access key id consumed by submission-service S3 client"
  type        = string
  sensitive   = true
}

variable "aws_secret_access_key" {
  description = "Secret access key consumed by submission-service S3 client"
  type        = string
  sensitive   = true
}

variable "ollama_base_url" {
  description = "OLLAMA base URL used by evaluation-service"
  type        = string
  default     = "http://host.docker.internal:11434"
}

variable "ollama_model" {
  description = "OLLAMA model used by evaluation-service"
  type        = string
  default     = "llama3.1:8b"
}

variable "client_image" {
  description = "Full image URI for the client container"
  type        = string
}

variable "gateway_image" {
  description = "Full image URI for the gateway-service container"
  type        = string
}

variable "auth_image" {
  description = "Full image URI for the auth-service container"
  type        = string
}

variable "problem_image" {
  description = "Full image URI for the problem-service container"
  type        = string
}

variable "submission_image" {
  description = "Full image URI for the submission-service container"
  type        = string
}

variable "user_profile_image" {
  description = "Full image URI for the user-profile-service container"
  type        = string
}

variable "discussion_image" {
  description = "Full image URI for the discussion-service container"
  type        = string
}

variable "evaluation_image" {
  description = "Full image URI for the evaluation-service container"
  type        = string
}

variable "rabbitmq_image" {
  description = "Image URI for RabbitMQ"
  type        = string
  default     = "rabbitmq:3.13-management"
}

variable "client_container_port" {
  description = "Port exposed by the client container"
  type        = number
  default     = 80
}

variable "desired_count" {
  description = "Desired ECS task count per service"
  type        = number
  default     = 1
}

variable "db_instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t4g.micro"
}

variable "redis_node_type" {
  description = "ElastiCache node type"
  type        = string
  default     = "cache.t3.micro"
}
