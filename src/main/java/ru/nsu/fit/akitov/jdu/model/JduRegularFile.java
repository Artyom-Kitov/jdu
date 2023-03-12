package ru.nsu.fit.akitov.jdu.model;

import ru.nsu.fit.akitov.jdu.JduVisitor;

import java.nio.file.Path;

public final class JduRegularFile extends JduFile {
  JduRegularFile(Path path, int depth, boolean accessible, long byteSize) {
    super(path, depth, accessible);
    this.byteSize = byteSize;
  }

  @Override
  public void accept(JduVisitor visitor) {
    visitor.visit(this);
  }
}
