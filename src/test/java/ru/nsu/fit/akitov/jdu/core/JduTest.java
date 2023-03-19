package ru.nsu.fit.akitov.jdu.core;

import org.junit.Rule;

import java.nio.file.FileSystem;

public abstract class JduTest {
  @Rule
  public final FileSystemRule fileSystemRule = new FileSystemRule();

  protected FileSystem fileSystem() {
    return fileSystemRule.getFileSystem();
  }
}
