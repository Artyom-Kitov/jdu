package org.example;

import java.io.IOException;
import java.nio.file.*;

public class FileInfo extends PathInfo {
  public FileInfo(Path path, int depth) {
    this.path = path;
    this.depth = depth;
    try {
      this.byteSize = Files.size(path);
    } catch (IOException exception) {
      accessible = false;
    }
  }

  @Override
  public String toString() {
    return "  ".repeat(depth) + path.getFileName().toString() + getSizeSuffix();
  }
}
