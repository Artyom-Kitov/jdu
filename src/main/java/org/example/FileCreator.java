package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileCreator {
  private FileCreator() {}
  public static PathInfo create(Path path, int depth) throws IOException {
    if (Files.isSymbolicLink(path)) {
      return new SymlinkInfo(path, depth);
    }
    if (Files.isDirectory(path)) {
      return new DirectoryInfo(path, depth);
    }
    if (Files.isRegularFile(path)) {
      return new FileInfo(path, depth);
    }
    throw new IOException("no such file or directory");
  }
}
