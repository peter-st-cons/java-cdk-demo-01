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
public class JavaCdk01BaselineStackProps implements StackProps {
  private String sub;
  private Environment environment;

  @Override
  public @Nullable Environment getEnv() {
    return this.environment;
  }
}
