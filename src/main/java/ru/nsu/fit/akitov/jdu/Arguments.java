package ru.nsu.fit.akitov.jdu;

import java.nio.file.Files;
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
    public static Arguments getFromArray(String[] args) throws JduException {
      Arguments.Builder builder = new Arguments.Builder();
      for (int i = 0; i < args.length; i++) {
        switch (args[i]) {
          case "--depth" -> {
            try {
              builder.setDepth(Integer.parseInt(args[i + 1]));
              i++;
            } catch (NumberFormatException e) {
              throw new JduException("wrong depth parameter: an integer value expected");
            }
          }
          case "--limit" -> {
            try {
              builder.setLimit(Integer.parseInt(args[i + 1]));
              i++;
            } catch (NumberFormatException e) {
              throw new JduException("wrong limit parameter: an integer value expected");
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
