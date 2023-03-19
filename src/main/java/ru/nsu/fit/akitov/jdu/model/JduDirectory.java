package ru.nsu.fit.akitov.jdu.model;

import ru.nsu.fit.akitov.jdu.JduVisitor;

import java.nio.file.Path;
import java.util.List;

public final class JduDirectory extends JduFile {
  private List<JduFile> children;

  JduDirectory(Path path, long byteSize, List<JduFile> children) {
    super(path, byteSize);
    this.children = children;
  }

  public List<JduFile> getChildren() {
    return children;
  }

  public void setChildren(List<JduFile> children) {
    this.children = children;
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
