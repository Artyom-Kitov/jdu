package ru.nsu.fit.akitov.jdu.model;

import java.nio.file.Path;
import java.util.Objects;

import ru.nsu.fit.akitov.jdu.JduVisitor;

/**
 * An object that could be used to get some information about
 * a file, a directory or a symlink.
 * <br/>
 * Created using {@code JduBuilder} only.
 */
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

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof JduFile file)) {
      return false;
    }
    return Objects.equals(path, file.path);
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }
}
