package ru.nsu.fit.akitov.jdu.core;

import java.nio.file.*;
import java.util.List;

public final class JduDirectory extends JduFile {
  final List<JduFile> content;

  JduDirectory(Path path, int depth, List<JduFile> content) {
    super(path, depth);
    this.content = content;
    for (JduFile info : content) {
      this.byteSize += info.byteSize;
    }
  }

  @Override
  public void accept(JduPrintStream stream) {
    stream.print(this);
  }
}
