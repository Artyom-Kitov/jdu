package ru.nsu.fit.akitov.jdu.core;

import org.junit.Rule;
import ru.nsu.fit.akitov.jdu.Arguments;
import ru.nsu.fit.akitov.jdu.model.JduBuilder;

import java.nio.file.FileSystem;

public abstract class JduTest {
  @Rule
  public final FileSystemRule fileSystemRule = new FileSystemRule();

  @Rule
  public final ArgumentsBuilderRule argumentsBuilderRule = new ArgumentsBuilderRule();

  @Rule
  public final JduBuilderRule jduBuilderRule = new JduBuilderRule();

  protected FileSystem fileSystem() {
    return fileSystemRule.getFileSystem();
  }

  protected Arguments.Builder argumentsBuilder() {
    return argumentsBuilderRule.getArgumentsBuilder();
  }

  protected JduBuilder jduBuilder() {
    return jduBuilderRule.getJduBuilder();
  }
}
