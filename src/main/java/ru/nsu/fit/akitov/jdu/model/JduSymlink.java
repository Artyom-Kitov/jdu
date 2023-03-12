package ru.nsu.fit.akitov.jdu.model;

import ru.nsu.fit.akitov.jdu.JduVisitor;

import java.nio.file.Path;

public final class JduSymlink extends JduFile {
  private final JduFile target;

  JduSymlink(Path path, int depth, boolean accessible, JduFile target) {
    super(path, depth, accessible);
    this.byteSize = 0;
    this.target = target;
  }

  public JduFile getTarget() {
    return target;
  }

  @Override
  public void accept(JduVisitor visitor) {
    visitor.visit(this);
  }
}
