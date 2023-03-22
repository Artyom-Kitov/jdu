package ru.nsu.fit.akitov.jdu.model;

import ru.nsu.fit.akitov.jdu.JduVisitor;

import java.nio.file.Path;
import java.util.Objects;

/**
 * An object that could be used to get some information about
 * a file, a directory or a symlink.
 * <p/>
 * Has 3 inheritors:
 * // CR: use '<li></li>' instead
 * <p>- {@code JduRegularFile} represents a regular file.</p>
 * <p>- {@code JduDirectory} represents a directory of files.
 * Size is equal to sum of children sizes. Has a list of children.</p>
 * <p>- {@code JduSymlink} represents a symbolic link to a directory or a file.
 * Size is undefined and target JduFile size is not added to the parent directory.</p>
 * <p>Created using {@code JduBuilder} only.</p>
 */
public abstract sealed class JduFile permits JduDirectory, JduRegularFile, JduSymlink {

  protected final Path path;
  protected long byteSize;

  protected JduFile(Path path, long byteSize) {
    this.path = path;
    this.byteSize = byteSize;
  }

  public Path getPath() {
    return path;
  }

  public long getByteSize() {
    return byteSize;
  }

  public void setByteSize(long byteSize) {
    this.byteSize = byteSize;
  }

  public abstract boolean isAccessible();

  public abstract void accept(JduVisitor visitor);

  @Override
  public boolean equals(Object o) {
    if (o.getClass() != getClass()) {
      return false;
    }
    JduFile file = (JduFile) o;
    return Objects.equals(path, file.path);
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }
}
