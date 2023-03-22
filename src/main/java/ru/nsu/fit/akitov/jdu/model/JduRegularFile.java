package ru.nsu.fit.akitov.jdu.model;

import ru.nsu.fit.akitov.jdu.JduVisitor;

import java.nio.file.Path;

public final class JduRegularFile extends JduFile {

  JduRegularFile(Path path, long byteSize) {
    super(path, byteSize);
  }

  @Override
  public boolean isAccessible() {
    return byteSize != -1;
  }

  @Override
  public void accept(JduVisitor visitor) {
    visitor.visit(this);
  }
}
