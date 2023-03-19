package ru.nsu.fit.akitov.jdu.model;

import ru.nsu.fit.akitov.jdu.JduVisitor;

import java.nio.file.Path;

public final class JduSymlink extends JduFile {
  private final JduFile target;

  JduSymlink(Path path, JduFile target) {
    super(path, 0);
    this.target = target;
  }

  public JduFile getTarget() {
    return target;
  }

  @Override
  public boolean isAccessible() {
    return target != null;
  }

  @Override
  public void accept(JduVisitor visitor) {
    visitor.visit(this);
  }
}
