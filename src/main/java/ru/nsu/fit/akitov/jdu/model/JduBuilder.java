package ru.nsu.fit.akitov.jdu.model;

import ru.nsu.fit.akitov.jdu.Arguments;
import ru.nsu.fit.akitov.jdu.JduException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.nio.file.Files.list;

public final class JduBuilder {
  private static final Set<Path> visited = new HashSet<>();
  private JduBuilder() {}

  public static JduFile build(Arguments args) throws JduException {
    return build(args.fileName(), args, 0);
  }

  static JduFile build(Path path, Arguments args, int depth) throws JduException {
    JduFile result;
    if (Files.isSymbolicLink(path)) {
      result = buildSymlink(path, args, depth);
    } else if (Files.isDirectory(path)) {
      result = buildDirectory(path, args, depth);
    } else if (Files.isRegularFile(path)) {
      result = buildRegularFile(path, depth);
    } else {
      // TODO: log "something unknown happened"
      throw new JduException("something unknown happened, please try again");
    }
    return result;
  }

  private static JduFile buildSymlink(Path path, Arguments args, int depth) throws JduException {
    JduFile target = null;
    Path real = null;
    try {
      real = path.toRealPath();
    } catch (IOException e) {
      // TODO: log "couldn't get real path of ... for some reason"
    }
    if (args.showSymlinks() && depth + 1 <= args.depth() && !visited.contains(real)) {
      visited.add(real);
      try {
        Path p = Files.readSymbolicLink(path);
        target = JduBuilder.build(p, args, depth + 1);
      } catch (IOException e) {
        // CR: log4j, continue working
        throw new JduException("can't show symlink \"" + path.getFileName() + "\"");
      }
    }
    return new JduSymlink(path, depth, target);
  }

  private static JduFile buildDirectory(Path path, Arguments args, int depth) throws JduException {
    try (var contentStream = list(path)) {
      Path[] contentArray = contentStream.toArray(Path[]::new);
      List<JduFile> content = new ArrayList<>();

      for (Path p : contentArray) {
        JduFile file = JduBuilder.build(p, args, depth + 1);
        content.add(file);
      }
      return new JduDirectory(path, depth, content);
    } catch (IOException e) {
      // TODO: log "can't read directory"
      throw new JduException("can't read directory \"" + path.getFileName() + "\"");
    }
  }

  private static JduFile buildRegularFile(Path path, int depth) {
    long byteSize = 0;
    try {
      byteSize = Files.size(path);
    } catch (IOException exception) {
      // TODO: log "couldn't get file size"
    }
    return new JduRegularFile(path, depth, byteSize);
  }
}
