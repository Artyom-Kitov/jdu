package ru.nsu.fit.akitov.jdu;

import java.io.IOException;
import java.nio.file.*;

public class Main {
  public static void main(String[] args) {
    Arguments arguments;
    try {
      arguments = getArguments(args);
    } catch (Exception exception) {
      System.out.println(usage());
      return;
    }

    PathInfo.setNMax(arguments.nMax());
    PathInfo.setMaxDepth(arguments.depth());
    if (arguments.links()) {
      PathInfo.showSymlinks();
    }

    // Cross CR: Not sure if it's ok to store all data in 1 big string (?)
    try {
      PathInfo info = PathInfo.of(arguments.fileName());
      System.out.println(info);
    } catch (IOException exception) {
      System.out.println("Error: " + exception.getMessage());
    }
  }
  private static String usage() {
    return """
              Usage: ./jdu [OPTIONS] [FILE]
              Summarize disk usage of a file, recursively for directories.
              
              Possible options:
                --depth n   Max recursion depth (8 by default).
                -L          Go by symlinks.
                --limit n   Show n heaviest files/directories in every directory.
              """;
  }

  private record Arguments(int depth, boolean links, int nMax, Path fileName) {}

  private static Arguments getArguments(String[] args) {
    int depth = PathInfo.getMaxDepth();
    boolean links = false;
    int nMax = PathInfo.getNMax();
    String path = ".";

    for (int i = 0; i < args.length; i++) {
      switch (args[i]) {
        case "--depth" -> {
          depth = Integer.parseInt(args[i + 1]);
          i++;
        }
        case "--limit" -> {
          nMax = Integer.parseInt(args[i + 1]);
          i++;
        }
        case "-L" -> links = true;
        default -> {
          if (i == args.length - 1) {
            path = args[i];
          } else {
            throw new IllegalArgumentException();
          }
        }
      }
    }
    return new Arguments(depth, links, nMax, Paths.get(path));
  }
}
