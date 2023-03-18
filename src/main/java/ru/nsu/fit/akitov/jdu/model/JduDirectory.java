package ru.nsu.fit.akitov.jdu.model;

import ru.nsu.fit.akitov.jdu.JduVisitor;

import java.nio.file.Path;
import java.util.List;

public final class JduDirectory extends JduFile {
  private final List<JduFile> content;

  JduDirectory(Path path, int depth, boolean accessible, List<JduFile> children) {
    super(path, depth, accessible);
    this.content = children;
    for (JduFile child : children) {
      this.byteSize += child.byteSize;
    }
  }

  public List<JduFile> getContent() {
    return content;
  }

  @Override
  public void accept(JduVisitor visitor) {
    visitor.visit(this);
  }
}
