package ru.nsu.fit.akitov.jdu.model;

import ru.nsu.fit.akitov.jdu.JduVisitor;

import java.nio.file.Path;
import java.util.List;

public final class JduDirectory extends JduFile {
  private final List<JduFile> content;

  JduDirectory(Path path, int depth, List<JduFile> content) {
    super(path, depth);
    this.content = content;
    for (JduFile info : content) {
      this.byteSize += info.byteSize;
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
