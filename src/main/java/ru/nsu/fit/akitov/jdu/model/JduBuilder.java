package ru.nsu.fit.akitov.jdu.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.fit.akitov.jdu.Arguments;

import static java.nio.file.Files.list;

public final class JduBuilder {
  private static final Set<Path> visited = new HashSet<>();
  private static final Logger logger = LogManager.getLogger("jdu");

  private JduBuilder() {}

  /**
   * Builds a JduFile for the path specified in args, recursively for symlinks and directories.
   * Returns null and logs an error if no such file or directory exists or something unexpected happened.
   * If the path is a regular file and couldn't get its size, logs a warning.
   * If something went wrong while reading a directory, logs a warning.
   * If args.showSymlinks() is true and couldn't get symlink target, logs a warning.
   * @param args command line arguments to specify what to build.
   * @return the resulting JduFile.
   */
  public static JduFile build(Arguments args) {
    return build(args.fileName(), args, 0);
  }

  private static JduFile build(Path path, Arguments args, int depth) {
    JduFile result = null;
    if (Files.isSymbolicLink(path)) {
      result = buildSymlink(path, args, depth);
    } else if (Files.isDirectory(path)) {
      result = buildDirectory(path, args, depth);
    } else if (Files.isRegularFile(path)) {
      result = buildRegularFile(path, depth);
    } else {
      logger.error("Something unknown happened while building '" + path + "', please try again");
    }
    return result;
  }

  private static JduFile buildSymlink(Path path, Arguments args, int depth) {
    JduFile target = null;
    Path real = null;
    boolean accessible = true;
    try {
      real = path.toRealPath();
    } catch (IOException e) {
      logger.info("Couldn't get the full path of '" + path + "'");
      accessible = false;
    }
    if (args.showSymlinks() && depth + 1 <= args.depth() && !visited.contains(real)) {
      visited.add(real);
      try {
        Path p = Files.readSymbolicLink(path);
        target = JduBuilder.build(p, args, depth + 1);
      } catch (IOException e) {
        logger.warn("Can't show symlink '" + path + "'");
        accessible = false;
      }
    }
    return new JduSymlink(path, depth, accessible, target);
  }

  private static JduFile buildDirectory(Path path, Arguments args, int depth) {
    List<JduFile> content = new ArrayList<>();
    boolean accessible = true;
    try (var contentStream = list(path)) {
      Path[] contentArray = contentStream.toArray(Path[]::new);
      for (Path p : contentArray) {
        JduFile file = JduBuilder.build(p, args, depth + 1);
        content.add(file);
      }
    } catch (IOException e) {
      logger.warn("Couldn't read directory '" + path + "'");
      accessible = false;
    }
    return new JduDirectory(path, depth, accessible, content);
  }

  private static JduFile buildRegularFile(Path path, int depth) {
    long byteSize = 0;
    boolean accessible = true;
    try {
      byteSize = Files.size(path);
    } catch (IOException exception) {
      logger.warn("Couldn't get the actual size of file '" + path + "'");
      accessible = false;
    }
    return new JduRegularFile(path, depth, accessible, byteSize);
  }
}
