package ru.nsu.fit.akitov.jdu.core;

import java.io.IOException;
import java.nio.file.*;

public final class JduRegularFile extends JduFile {
  JduRegularFile(Path path, int depth) {
    super(path, depth);
    try {
      this.byteSize = Files.size(path);
    } catch (IOException exception) {
      accessible = false;
    }
  }

  @Override
  public void print(JduPrintStream stream) {
    stream.print(this);
  }
}
