package ru.nsu.fit.akitov.jdu.model;

import ru.nsu.fit.akitov.jdu.JduVisitor;

import java.nio.file.Path;

public abstract class JduFile {
  protected Path path;
  protected int depth;
  protected long byteSize;

  protected JduFile(Path path, int depth) {
    this.path = path;
    this.depth = depth;
  }

  public Path getPath() {
    return path;
  }

  public long getByteSize() {
    return byteSize;
  }

  public int getDepth() {
    return depth;
  }

  public abstract void accept(JduVisitor stream);
}
