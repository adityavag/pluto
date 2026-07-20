output "alb_dns_name" {
  description = "Public DNS name of the application load balancer"
  value       = aws_lb.main.dns_name
}

output "public_app_url" {
  description = "Public base URL for Pluto"
  value       = "http://${aws_lb.main.dns_name}"
}

output "s3_submissions_bucket" {
  description = "S3 bucket used for submission diagrams"
  value       = aws_s3_bucket.submissions.id
}

output "rds_endpoint" {
  description = "RDS endpoint"
  value       = aws_db_instance.postgres.address
}

output "redis_endpoint" {
  description = "Redis endpoint"
  value       = aws_elasticache_cluster.redis.cache_nodes[0].address
}

output "service_discovery_namespace" {
  description = "Cloud Map namespace used for internal service discovery"
  value       = aws_service_discovery_private_dns_namespace.main.name
}

output "ecr_repositories" {
  description = "ECR repository URLs for Pluto services"
  value = {
    for name, repo in aws_ecr_repository.apps : name => repo.repository_url
  }
}
