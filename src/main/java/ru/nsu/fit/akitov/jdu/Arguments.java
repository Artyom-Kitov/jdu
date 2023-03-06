package ru.nsu.fit.akitov.jdu;

import java.nio.file.*;

public record Arguments(int depth, boolean showSymlinks, int limit, Path fileName) {
  public static class ArgumentsBuilder {
    private int depth = PathInfo.getMaxDepth();
    private boolean showSymlinks = false;
    private int limit = PathInfo.getNMax();
    private Path fileName = Path.of(".");
    public ArgumentsBuilder setDepth(int depth) {
      this.depth = depth;
      return this;
    }

    public ArgumentsBuilder setSymlinksDisplay(boolean b) {
      showSymlinks = b;
      return this;
    }

    public ArgumentsBuilder setLimit(int limit) {
      this.limit = limit;
      return this;
    }

    public ArgumentsBuilder setFileName(Path fileName) {
      this.fileName = fileName;
      return this;
    }

    public Arguments build() {
      return new Arguments(depth, showSymlinks, limit, fileName);
    }
  }
}
