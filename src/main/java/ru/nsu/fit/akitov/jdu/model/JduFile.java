package ru.nsu.fit.akitov.jdu.model;

import java.nio.file.Path;
import java.util.Objects;

import ru.nsu.fit.akitov.jdu.JduVisitor;

/**
 *
 * CR:
 * - hierarchy
 * - differences
 *
 * An object that could be used to get some information about
 * a file, a directory or a symlink.
 * Created using {@code JduBuilder} only.
 */

//interface DuFile {
//  Path path();
//}
//
//record JduDir implements DuFile(Path path) {}

public abstract sealed class JduFile permits JduDirectory, JduRegularFile, JduSymlink {
  protected final Path path;
  protected final int depth;
  protected final long byteSize;

  protected JduFile(Path path, int depth, long byteSize) {
    this.path = path;
    this.depth = depth;
    this.byteSize = byteSize;
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

  public abstract boolean isAccessible();

  public abstract void accept(JduVisitor visitor);

  @Override
  public boolean equals(Object o) {
    // CR: o.getClass()
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
