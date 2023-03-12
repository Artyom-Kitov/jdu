package ru.nsu.fit.akitov.jdu.core;

import java.nio.file.FileSystem;

import com.google.common.jimfs.Jimfs;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

class FileSystemRule implements TestRule {
  private FileSystem fileSystem;

  FileSystem getFileSystem() {
    return fileSystem;
  }

  @Override
  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        fileSystem = Jimfs.newFileSystem();
        try {
          base.evaluate();
        } finally {
          fileSystem.close();
        }
      }
    };
  }
}
