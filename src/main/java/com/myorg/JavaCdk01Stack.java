package com.myorg;

import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.CpuUtilizationScalingProps;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.ecs.ScalableTaskCount;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class JavaCdk01Stack extends Stack {
  public JavaCdk01Stack(final Construct scope, final String id) {
    this(scope, id, null);
  }

  public JavaCdk01Stack(final Construct scope, final String id, final StackProps props) {
    super(scope, id, props);

    // Create a VPC with a NAT-gateway, IGW, default routes and route tables, and a
    // private and public subnet in all availability zones.
    Vpc vpc = new Vpc(this, this.getStackName() + "-Vpc");

    // Create the ECS cluster.
    Cluster cluster = Cluster.Builder.create(this, this.getStackName() + "-ECS-Cluster").vpc(vpc).build();

    // Create a load-balanced Fargate service and make it public.
    ApplicationLoadBalancedFargateService fargateService = ApplicationLoadBalancedFargateService.Builder
        .create(this, this.getStackName() + "-ECS-Service")
        .cluster(cluster)
        .cpu(256) // this is the default; for a Spring boot app, 1024 is the minimum
        .desiredCount(1) // actually, the default is already 1
        .taskImageOptions(ApplicationLoadBalancedTaskImageOptions.builder()
            .image(ContainerImage.fromAsset("./app"))
            // .containerPort(8080) // in case you use Spring boot default port 8080
            .build())
        .publicLoadBalancer(true) // Default is false
        .assignPublicIp(false) // If set to true, it will associate the service to a public subnet
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
