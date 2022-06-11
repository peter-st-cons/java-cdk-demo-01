package com.myorg;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;

public class JavaCdk01Stack extends Stack {
    public JavaCdk01Stack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public JavaCdk01Stack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // The code that defines your stack goes here
    }
}
