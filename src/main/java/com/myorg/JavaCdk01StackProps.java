package com.myorg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class JavaCdk01StackProps implements StackProps {

  private String dockerImage;
  private Environment environment;

  @Override
  public @Nullable Environment getEnv() {
    return this.environment;
  }
}
