package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;

public class JavaCdk01App {
  public static void main(final String[] args) {
    App app = new App();

    Environment environment = Environment.builder()
        .region("eu-central-1")
        .build();

    String dockerImage = System.getenv("DOCKER_IMAGE");

    JavaCdk01StackProps javaCdk01StackProps = JavaCdk01StackProps.builder()
        .environment(environment)
        .dockerImage(dockerImage)
        .build();

    JavaCdk01Stack.builder()
        .scope(app)
        .id("JavaCdkDemo01")
        .props(javaCdk01StackProps)
        .build();

    app.synth();
  }
}
