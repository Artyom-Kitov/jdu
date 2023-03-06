package ru.nsu.fit.akitov.jdu.core;

import ru.nsu.fit.akitov.jdu.Arguments;

import java.nio.file.*;

public abstract class JduFile {
  protected Path path;
  protected int depth;
  protected long byteSize;
  protected boolean accessible = true;
  protected Arguments args;

  protected JduFile(Path path, Arguments args, int depth) {
    this.path = path;
    this.args = args;
    this.depth = depth;
  }

  public abstract void print(Printer printer);
}
