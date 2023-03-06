package ru.nsu.fit.akitov.jdu.core;

import ru.nsu.fit.akitov.jdu.Arguments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JduBuilder {
  public static JduFile build(Path path, Arguments args) {
    return build(path, args, 0);
  }

  static JduFile build(Path path, Arguments args, int depth) {
    JduFile result;
    if (Files.isSymbolicLink(path)) {
      result = new JduSymlink(path, args, depth);
    } else if (Files.isDirectory(path)) {
      try {
        List<JduFile> content = new ArrayList<>();
        Path[] contentArray = Files.list(path).toArray(Path[]::new);
        for (Path p : contentArray) {
          JduFile file = JduBuilder.build(p, args, depth + 1);
          content.add(file);
        }
        content.sort((p1, p2) -> -Long.compare(p1.byteSize, p2.byteSize));
        result = new JduDirectory(path, args, depth, content);
      } catch (IOException e) {
        throw new IllegalStateException();
      }
    } else if (Files.isRegularFile(path)) {
      result = new JduRegularFile(path, args, depth);
    } else {
      throw new IllegalStateException();
    }
    return result;
  }
}
