package ru.nsu.fit.akitov.jdu.core;

import ru.nsu.fit.akitov.jdu.Arguments;
import ru.nsu.fit.akitov.jdu.JduException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Files.list;

public final class JduBuilder {
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
      result = new JduRegularFile(path, depth);
    } else {
      throw new JduException("something unknown happened, please try again");
    }
    return result;
  }

  private static JduFile buildSymlink(Path path, Arguments args, int depth) throws JduException {
    JduFile target = null;
    if (args.showSymlinks() && depth + 1 <= args.depth() && !JduSymlink.visited.contains(path)) {
      JduSymlink.visited.add(path);
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
    try {
      List<JduFile> content = new ArrayList<>();
      Path[] contentArray = list(path).toArray(Path[]::new);
      for (Path p : contentArray) {
        JduFile file = JduBuilder.build(p, args, depth + 1);
        content.add(file);
      }
      // CR: move to print stage
      content.sort((p1, p2) -> -Long.compare(p1.byteSize, p2.byteSize));
      return new JduDirectory(path, depth, content);
    } catch (IOException e) {
      throw new JduException("can't read directory \"" + path.getFileName() + "\"");
    }
  }
}
