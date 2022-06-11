package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

// import java.util.Arrays;

public class JavaCdk01App {
  public static void main(final String[] args) {
    App app = new App();

    new JavaCdk01Stack(app, "JavaCdkDemo01", StackProps.builder()
        .env(Environment.builder()
            .account(System.getenv("AWS_ACCOUNT_ID"))
            // .account("744190369095")
            .region("eu-central-1")
            .build())

        .build());

    app.synth();
  }
}
