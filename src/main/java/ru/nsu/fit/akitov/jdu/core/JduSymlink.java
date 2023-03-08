package ru.nsu.fit.akitov.jdu.core;

import java.nio.file.*;
import java.util.Set;
import java.util.HashSet;

public final class JduSymlink extends JduFile {
  final JduFile target;

  static final Set<Path> visited = new HashSet<>();

  JduSymlink(Path path, int depth, JduFile target) {
    super(path, depth);
    this.byteSize = 0;
    this.target = target;
  }

  @Override
  public void print(JduPrintStream stream) {
    stream.print(this);
  }
}
