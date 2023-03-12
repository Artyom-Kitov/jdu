package ru.nsu.fit.akitov.jdu;

import java.nio.file.Path;

public record Arguments(int depth, boolean showSymlinks, int limit, Path fileName) {
  public static class Builder {
    private int depth = 16;
    private boolean showSymlinks = false;
    private int limit = 1024;
    private Path fileName = Path.of(".");

    public Builder setDepth(int depth) {
      this.depth = depth;
      return this;
    }

    public Builder setSymlinksDisplay(boolean b) {
      showSymlinks = b;
      return this;
    }

    public Builder setLimit(int limit) {
      this.limit = limit;
      return this;
    }

    public Builder setFileName(Path fileName) {
      this.fileName = fileName;
      return this;
    }

    public Arguments build() {
      return new Arguments(depth, showSymlinks, limit, fileName);
    }
  }
}
