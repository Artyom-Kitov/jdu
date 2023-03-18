package ru.nsu.fit.akitov.jdu.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.fit.akitov.jdu.Arguments;

import static java.nio.file.Files.list;

public final class JduBuilder {
  private final Set<Path> visited;
  private final Logger logger;

  public JduBuilder() {
    visited = new HashSet<>();
    this.logger = LogManager.getLogger("ru.nsu.fit.akitov.jdu.model.JduBuilder");
  }

  /**
   * Builds a {@code JduFile} for the {@code Path} specified in {@code Arguments}.
   * <p>If the {@code Path} is a directory, builds recursively for subdirectories.</p>
   * <p>If the {@code Path} is a symbolic link and {@code showSymlinks} is true, builds recursively for target.</p>
   * <p>Logs an error if no such file or directory exists or something unexpected happened.</p>
   * <p>If the path is a regular file and couldn't get its size, logs a warning.</p>
   * <p>If something went wrong while reading a directory, logs a warning and returns null.</p>
   * @param args command line arguments to specify what to build.
   * @return the resulting {@code JduFile} or null if no such file or directory exists
   * or something unexpected happened.
   */
  public JduFile build(Arguments args) {
    return build(args.fileName(), args, 0);
  }

  private JduFile build(Path path, Arguments args, int depth) {
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

  private JduFile buildSymlink(Path path, Arguments args, int depth) {
    JduFile target = null;
    boolean accessible = true;
    if (args.showSymlinks() && depth + 1 <= args.depth() && visited.add(path)) {
      try {
        Path p = Files.readSymbolicLink(path);
        target = build(p, args, depth + 1);
      } catch (IOException e) {
        logger.warn("Can't show symlink '" + path + "'");
        // CR: show e in log
        // CR: remove accessible field
        accessible = false;
      }
    }
    return new JduSymlink(path, depth, accessible, target);
  }

  /*

  foo
    slink

 depth = 2

 foo
    slink


 depth = 4

  foo
    slink
      foo
        slink
   */

  private JduFile buildDirectory(Path path, Arguments args, int depth) {
    List<JduFile> content = new ArrayList<>();
    boolean accessible = true;
    try (Stream<Path> contentStream = list(path)) {
      Path[] contentArray = contentStream.toArray(Path[]::new);
      for (Path p : contentArray) {
        JduFile file = build(p, args, depth + 1);
        if (file != null) {
          content.add(file);
        }
      }
    } catch (IOException e) {
      logger.warn("Couldn't read directory '" + path + "'");
      // CR: set children to null?
      accessible = false;
    }
    return new JduDirectory(path, depth, accessible, content);
  }

  private JduFile buildRegularFile(Path path, int depth) {
    long byteSize = 0;
    boolean accessible = true;
    try {
      byteSize = Files.size(path);
    } catch (IOException exception) {
      logger.warn("Couldn't get the actual size of file '" + path + "'");
      // CR: log exception.getMessage()
      accessible = false;
    }
    return new JduRegularFile(path, depth, accessible, byteSize);
  }
}
