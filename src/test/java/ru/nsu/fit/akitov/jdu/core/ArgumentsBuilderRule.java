package ru.nsu.fit.akitov.jdu.core;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import ru.nsu.fit.akitov.jdu.Arguments;
import ru.nsu.fit.akitov.jdu.model.JduBuilder;

public class ArgumentsBuilderRule implements TestRule {
  private Arguments.Builder argumentsBuilder;

  Arguments.Builder getArgumentsBuilder() {
    return argumentsBuilder;
  }

  @Override
  public Statement apply(final Statement base, Description description) {
    return new org.junit.runners.model.Statement() {
      @Override
      public void evaluate() throws Throwable {
        argumentsBuilder = new Arguments.Builder();
        base.evaluate();
      }
    };
  }
}
