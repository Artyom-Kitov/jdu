package ru.nsu.fit.akitov.jdu.core;

import java.io.PrintStream;

public class JduFormattedStream implements JduPrintStream {
  private final PrintStream stream;
  private final int depth;
  private final int limit;

  public JduFormattedStream(PrintStream stream, int depth, int limit) {
    this.stream = stream;
    this.depth = depth;
    this.limit = limit;
  }

  private String getSizeSuffix(JduFile file) {
    if (!file.accessible) {
      return " [inaccessible]";
    }
    float size = file.byteSize;
    String suffix = " B";
    if (size / 1024 >= 1) {
      size /= 1024;
      suffix = " KiB";
    }
    if (size / 1024 >= 1) {
      size /= 1024;
      suffix = " MiB";
    }
    return " [" + String.format("%.3f", size) + suffix + "]";
  }

  @Override
  public void print(JduRegularFile regularFile) {
    stream.println("  ".repeat(regularFile.depth) + regularFile.path.getFileName() + getSizeSuffix(regularFile));
  }

  @Override
  public void print(JduSymlink symlink) {
    stream.println("  ".repeat(symlink.depth) + symlink.path.getFileName() + " [symlink]");
    if (symlink.target != null) {
      symlink.target.print(this);
    }
  }

  @Override
  public void print(JduDirectory directory) {
    stream.println("  ".repeat(directory.depth) + "/" + directory.path.getFileName() + getSizeSuffix(directory));
    if (directory.depth == depth) {
      return;
    }
    for (int i = 0; i < directory.content.size() && i < limit; i++) {
      directory.content.get(i).print(this);
    }
  }
}
