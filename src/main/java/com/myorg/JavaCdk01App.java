package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

// import java.util.Arrays;

public class JavaCdk01App {
  public static void main(final String[] args) {
    App app = new App();

    new JavaCdk01Stack(app, "JavaCdk01Stack", StackProps.builder()
        // Uncomment the next block to specialize this stack for the AWS Account
        // and Region that are implied by the current CLI configuration.
        // .env(Environment.builder()
        // .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
        // .region(System.getenv("CDK_DEFAULT_REGION"))
        // .build())

        .env(Environment.builder()
            .account("744190369095")
            .region("eu-central-1")
            .build())

        // For more information, see
        // https://docs.aws.amazon.com/cdk/latest/guide/environments.html
        .build());

    app.synth();
  }
}
