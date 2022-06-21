package com.myorg;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import software.amazon.awscdk.services.iam.IManagedPolicy;
import software.amazon.awscdk.services.iam.IPrincipal;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.OpenIdConnectProvider;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.WebIdentityPrincipal;
import software.amazon.awscdk.Stack;
import software.constructs.Construct;

public class JavaCdk01BaselineStack extends Stack {
  public JavaCdk01BaselineStack(final Construct scope, final String id) {
    this(scope, id, null);
  }

  @lombok.Builder
  public JavaCdk01BaselineStack(final Construct scope, final String id, final JavaCdk01BaselineStackProps props) {
    super(scope, id, props);

    String issuerUrl = "https://token.actions.githubusercontent.com";
    List<String> audiences = Arrays.asList("sts.amazonaws.com");
    List<IManagedPolicy> policies = Arrays.asList(ManagedPolicy.fromAwsManagedPolicyName("AdministratorAccess"));

    OpenIdConnectProvider githubProvider = OpenIdConnectProvider.Builder.create(this, "GitHubProvider")
        .url(issuerUrl)
        .clientIds(audiences)
        .build();

    Role.Builder.create(this, "GitHubActionsRole")
        .description("Assumed by GitHub (aws-actions/configure-aws-credentials)")
        .assumedBy(
            new WebIdentityPrincipal(githubProvider.getOpenIdConnectProviderArn(),
                Map.of(
                    "StringEquals", Map.of(
                        "token.actions.githubusercontent.com:aud", audiences.get(0),
                        "token.actions.githubusercontent.com:sub", props.getSub()))))
        .managedPolicies(policies)
        .build();
  }
}
