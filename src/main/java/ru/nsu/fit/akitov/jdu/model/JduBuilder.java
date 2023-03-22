package ru.nsu.fit.akitov.jdu.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.nio.file.Files.list;

public final class JduBuilder {

  private final Map<Path, JduFile> built;
  private final Logger logger;
  private final boolean buildSymlinks;

  private JduBuilder(boolean buildSymlinks) {
    built = new HashMap<>();
    this.logger = LogManager.getLogger("ru.nsu.fit.akitov.jdu.model.JduBuilder");
    this.buildSymlinks = buildSymlinks;
  }

  /**
   * Builds a {@code JduFile} for {@code path}.
   * <p>If {@code path} is a directory, builds recursively for all files and subdirectories to their very ends.</p>
   * <p>If {@code path} is a symbolic link and {@code buildSymlinks} is true, builds recursively for target.
   * If the target {@code JduFile} is built, refers to it, otherwise builds it as a file or a directory.</p>
   * <p>Logs an error if no such file or directory exists or something unexpected happened.</p>
   * <p>If {@code path} is a regular file and couldn't get its size, logs a warning.</p>
   * <p>If something went wrong while reading {@code path}, logs a warning and returns null.</p>
   *
   * @param path          {@code Path} to build from.
   * @param buildSymlinks build symlinks targets or not.
   * @return the resulting {@code JduFile} or null if no such file or directory exists
   * or something unexpected happened.
   */
  public static JduFile build(Path path, boolean buildSymlinks) {
    JduBuilder builder = new JduBuilder(buildSymlinks);
    return builder.buildImpl(path);
  }

  private JduFile buildImpl(Path path) {
    JduFile result = null;
    if (Files.isSymbolicLink(path)) {
      result = buildSymlink(path);
    } else if (Files.isDirectory(path)) {
      result = buildDirectory(path);
    } else if (Files.isRegularFile(path)) {
      result = buildRegularFile(path);
    } else {
      logger.error("Something unknown happened while building '" + path.toAbsolutePath() + "', skipping");
    }
    if (result != null) {
      built.put(path, result);
    }
    return result;
  }

  private JduFile buildSymlink(Path path) {
    if (!buildSymlinks) {
      return new JduSymlink(path, null);
    }
    JduFile target = null;
    try {
      Path p = Files.readSymbolicLink(path);
      if (!Files.exists(p)) {
        return new JduSymlink(path, null);
      }
      target = built.get(p);
      if (target == null) {
        target = buildImpl(p);
        built.put(p, target);
      }
    } catch (IOException e) {
      logger.warn("Couldn't read symlink: " + e.getMessage());
    }
    return new JduSymlink(path, target);
  }

  private JduFile buildDirectory(Path path) {
    JduDirectory result = new JduDirectory(path, 0, null);
    built.put(path, result);

    List<JduFile> children = new ArrayList<>();
    long byteSize = 0;
    try (Stream<Path> contentStream = list(path)) {
      Path[] contentArray = contentStream.toArray(Path[]::new);
      for (Path p : contentArray) {
        JduFile file = built.get(p);
        if (file == null) {
          file = buildImpl(p);
        }
        if (file != null) {
          children.add(file);
          byteSize += file.getByteSize();
        }
      }
    } catch (IOException e) {
      logger.warn("Couldn't read directory: " + e.getMessage());
      children = null;
    }
    result.setByteSize(byteSize);
    result.setChildren(children);
    return result;
  }

  private JduFile buildRegularFile(Path path) {
    long byteSize;
    try {
      byteSize = Files.size(path);
    } catch (IOException e) {
      logger.warn("Couldn't get the actual size of file: " + e.getMessage());
      byteSize = -1;
    }
    return new JduRegularFile(path, byteSize);
  }
}
