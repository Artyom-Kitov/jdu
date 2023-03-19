package ru.nsu.fit.akitov.jdu;

import java.nio.file.Files;
import java.nio.file.Path;

public record Arguments(int depth, boolean showSymlinks, int limit, Path fileName) {
  public static final int DEFAULT_DEPTH = 16;
  public static final int DEFAULT_LIMIT = 1024;
  public static final Path DEFAULT_PATH = Path.of(".");

  public static class Builder {
    private int depth = DEFAULT_DEPTH;
    private boolean showSymlinks = false;
    private int limit = DEFAULT_LIMIT;
    private Path fileName = DEFAULT_PATH;

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
    public static Arguments get(String... args) throws JduException {
      Arguments.Builder builder = new Arguments.Builder();
      for (int i = 0; i < args.length; i++) {
        switch (args[i]) {
          case "--depth" -> {
            try {
              builder.setDepth(Integer.parseUnsignedInt(args[i + 1]));
              i++;
            } catch (Exception e) {
              throw new JduException("wrong depth parameter: a positive integer value expected");
            }
          }
          case "--limit" -> {
            try {
              builder.setLimit(Integer.parseUnsignedInt(args[i + 1]));
              i++;
            } catch (Exception e) {
              throw new JduException("wrong limit parameter: a positive integer value expected");
            }
          }
          case "-L" -> builder.setSymlinksDisplay(true);
          default -> {
            if (i == args.length - 1) {
              Path path = Path.of(args[i]);
              if (!Files.exists(path)) {
                throw new JduException("no such file or directory: " + path);
              }
              builder.setFileName(path);
            } else {
              throw new JduException("no such parameter '" + args[i] + "'");
            }
          }
        }
      }
      return builder.build();
    }

    public Arguments build() {
      return new Arguments(depth, showSymlinks, limit, fileName);
    }
  }
}
