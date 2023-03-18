package ru.nsu.fit.akitov.jdu.model;

import ru.nsu.fit.akitov.jdu.JduVisitor;

import java.nio.file.Path;
import java.util.List;

public final class JduDirectory extends JduFile {
  private final List<JduFile> children;

  JduDirectory(Path path, int depth, long byteSize, List<JduFile> children) {
    super(path, depth, byteSize);
    this.children = children;
  }

  public List<JduFile> getChildren() {
    return children;
  }

  @Override
  public boolean isAccessible() {
    return children != null;
  }

  @Override
  public void accept(JduVisitor visitor) {
    visitor.visit(this);
  }
}
