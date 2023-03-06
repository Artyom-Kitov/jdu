package ru.nsu.fit.akitov.jdu.core;

import ru.nsu.fit.akitov.jdu.Arguments;

import java.io.IOException;
import java.nio.file.*;

public final class JduRegularFile extends JduFile {
  JduRegularFile(Path path, Arguments args, int depth) {
    super(path, args, depth);
    try {
      this.byteSize = Files.size(path);
    } catch (IOException exception) {
      accessible = false;
    }
  }

  @Override
  public void print(Printer printer) {
    printer.print(this);
  }
}
