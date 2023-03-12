package ru.nsu.fit.akitov.jdu.model;

import java.nio.file.Path;
import ru.nsu.fit.akitov.jdu.JduVisitor;

public abstract class JduFile {
  protected Path path;
  protected int depth;
  protected long byteSize;
  protected boolean accessible;

  protected JduFile(Path path, int depth, boolean accessible) {
    this.path = path;
    this.depth = depth;
    this.accessible = accessible;
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

  public boolean isAccessible() {
    return accessible;
  }

  public abstract void accept(JduVisitor stream);
}
