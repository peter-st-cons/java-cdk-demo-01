package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class JavaCdk01Stack extends Stack {
  public JavaCdk01Stack(final Construct scope, final String id) {
    this(scope, id, null);
  }

  public JavaCdk01Stack(final Construct scope, final String id, final StackProps props) {
    super(scope, id, props);

    // The code that defines your stack goes here
  }
}
