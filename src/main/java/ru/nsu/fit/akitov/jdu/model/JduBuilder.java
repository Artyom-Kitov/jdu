package ru.nsu.fit.akitov.jdu.model;

import org.apache.logging.log4j.LogManager;
import ru.nsu.fit.akitov.jdu.Arguments;
import ru.nsu.fit.akitov.jdu.JduException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import static java.nio.file.Files.list;

public final class JduBuilder {
  private static final Set<Path> visited = new HashSet<>();
  private static final Logger logger = LogManager.getLogger("jdu");
  private JduBuilder() {}

  public static JduFile build(Arguments args) throws JduException {
    return build(args.fileName(), args, 0);
  }

  static JduFile build(Path path, Arguments args, int depth) throws JduException {
    JduFile result = null;
    if (Files.isSymbolicLink(path)) {
      result = buildSymlink(path, args, depth);
    } else if (Files.isDirectory(path)) {
      result = buildDirectory(path, args, depth);
    } else if (Files.isRegularFile(path)) {
      result = buildRegularFile(path, depth);
    } else {
      logger.info("Something unknown happened while building '" + path + "', please try again");
    }
    return result;
  }

  private static JduFile buildSymlink(Path path, Arguments args, int depth) throws JduException {
    JduFile target = null;
    Path real = null;
    try {
      real = path.toRealPath();
    } catch (IOException e) {
      logger.info("Couldn't get the full path of '" + path + "'");
    }
    if (args.showSymlinks() && depth + 1 <= args.depth() && !visited.contains(real)) {
      visited.add(real);
      try {
        Path p = Files.readSymbolicLink(path);
        target = JduBuilder.build(p, args, depth + 1);
      } catch (IOException e) {
        logger.info("Can't show symlink '" + path + "'");
      }
    }
    return new JduSymlink(path, depth, target);
  }

  private static JduFile buildDirectory(Path path, Arguments args, int depth) throws JduException {
    List<JduFile> content = new ArrayList<>();
    try (var contentStream = list(path)) {
      Path[] contentArray = contentStream.toArray(Path[]::new);
      for (Path p : contentArray) {
        JduFile file = JduBuilder.build(p, args, depth + 1);
        content.add(file);
      }
    } catch (IOException e) {
      logger.info("Can't read directory '" + path + "'");
    }
    return new JduDirectory(path, depth, content);
  }

  private static JduFile buildRegularFile(Path path, int depth) {
    long byteSize = 0;
    try {
      byteSize = Files.size(path);
    } catch (IOException exception) {
      logger.info("Can't get size of file '" + path + "'");
    }
    return new JduRegularFile(path, depth, byteSize);
  }
}
