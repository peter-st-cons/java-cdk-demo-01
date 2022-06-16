package com.myorg;

import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.CpuUtilizationScalingProps;
import software.amazon.awscdk.services.ecs.RepositoryImageProps;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.ecs.ScalableTaskCount;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.secretsmanager.ISecret;
import software.amazon.awscdk.services.secretsmanager.Secret;
import software.amazon.awscdk.services.secretsmanager.SecretAttributes;
import software.amazon.awscdk.Stack;
import software.constructs.Construct;

public class JavaCdk01Stack extends Stack {
  public JavaCdk01Stack(final Construct scope, final String id) {
    this(scope, id, null);
  }

  @lombok.Builder
  public JavaCdk01Stack(final Construct scope, final String id, final JavaCdk01StackProps props) {
    super(scope, id, props);

    String dockerImage = props.getDockerImage();

    // Create a VPC with a NAT-gateway, IGW, default routes and route tables, and a
    // private and public subnet in all availability zones.
    Vpc vpc = new Vpc(this, this.getStackName() + "-Vpc");

    // Create the ECS cluster.
    Cluster cluster = Cluster.Builder.create(this, this.getStackName() + "-ECS-Cluster").vpc(vpc).build();

    String gitHubContainerRegistrySecretPartialArn = "arn:aws:secretsmanager:" + this.getRegion()
        + ":" + this.getAccount() + ":secret:javademo/github-container-registry-token";

    // GitHub PAT to fetch docker images from container registry
    ISecret gitHubContainerRegistryCredentials = Secret.fromSecretAttributes(this,
        this.getStackName() + "-GitHubToken",
        SecretAttributes.builder()
            .secretPartialArn(gitHubContainerRegistrySecretPartialArn)
            .build());

    // Docker image hosted on ECR or pulled from GitHub container registry
    ContainerImage repositoryImage = dockerImage == null || dockerImage.isEmpty()
        ? ContainerImage.fromAsset("./app")
        : ContainerImage.fromRegistry(dockerImage, RepositoryImageProps.builder()
            .credentials(gitHubContainerRegistryCredentials)
            .build());

    // Create a load-balanced Fargate service and make it public.
    ApplicationLoadBalancedFargateService fargateService = ApplicationLoadBalancedFargateService.Builder
        .create(this, this.getStackName() + "-ECS-Service")
        .cluster(cluster)
        .cpu(256) // this is the default; for a Spring boot app, 1024 is the minimum
        .desiredCount(1) // actually, the default is already 1
        .taskImageOptions(ApplicationLoadBalancedTaskImageOptions.builder()
            .image(repositoryImage)
            .build())
        .publicLoadBalancer(true) // Default is false
        .assignPublicIp(true)
        .build();

    // Configure health check.
    fargateService.getTargetGroup().configureHealthCheck(HealthCheck.builder()
        .healthyHttpCodes("200") // Specify which http codes are considered healthy
        // The load balancer *requires* a healthcheck endpoint to determine the health.
        // You could e.g. use the Spring Actuator "/actuator/health".
        .path("/")
        .build());

    // Configure auto scaling capabilities for a "production" environment:
    String environment = System.getenv("ENV");

    if (environment != null && !environment.isEmpty() && environment.equals("production")) {
      // Configure the service auto scaling
      ScalableTaskCount scalableTask = fargateService.getService().autoScaleTaskCount(EnableScalingProps.builder()
          .minCapacity(2)
          .maxCapacity(6)
          .build());

      // Scale based on the CPU utilization
      scalableTask.scaleOnCpuUtilization(this.getStackName() + "-Scaling", CpuUtilizationScalingProps.builder()
          .targetUtilizationPercent(50) // Scale when the CPU utilization is at 50%
          .build());
    }
  }
}
