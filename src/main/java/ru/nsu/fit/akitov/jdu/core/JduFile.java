package ru.nsu.fit.akitov.jdu.core;

import java.nio.file.*;

// CR: move to separate package (model)
public abstract class JduFile {
  protected Path path;
  protected int depth;
  protected long byteSize;
  protected boolean accessible = true;

  protected JduFile(Path path, int depth) {
    this.path = path;
    this.depth = depth;
  }

  public abstract void accept(JduPrintStream stream);
}
