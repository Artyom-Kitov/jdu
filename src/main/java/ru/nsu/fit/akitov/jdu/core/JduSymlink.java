package ru.nsu.fit.akitov.jdu.core;

import ru.nsu.fit.akitov.jdu.Arguments;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.HashSet;

public final class JduSymlink extends JduFile {
  JduFile target;

  private static final Set<Path> visited = new HashSet<>();

  JduSymlink(Path path, Arguments args, int depth) {
    super(path, args, depth);
    this.byteSize = 0;

    if (args.showSymlinks() && depth + 1 <= args.depth() && !visited.contains(path)) {
      visited.add(path);
      try {
        Path p = Files.readSymbolicLink(path);
        target = JduBuilder.build(p, args, depth + 1);
      } catch (IOException exception) {
        throw new IllegalStateException();
      }
    }
  }

  @Override
  public void print(Printer printer) {
    printer.print(this);
  }
}
