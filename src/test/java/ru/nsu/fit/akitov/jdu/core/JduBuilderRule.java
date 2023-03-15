package ru.nsu.fit.akitov.jdu.core;

import com.google.common.jimfs.Jimfs;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import ru.nsu.fit.akitov.jdu.model.JduBuilder;

public class JduBuilderRule implements TestRule {
  private JduBuilder jduBuilder;

  JduBuilder getJduBuilder() {
    return jduBuilder;
  }

  @Override
  public Statement apply(final Statement base, Description description) {
    return new org.junit.runners.model.Statement() {
      @Override
      public void evaluate() throws Throwable {
        jduBuilder = new JduBuilder();
        base.evaluate();
      }
    };
  }
}
