package ru.nsu.fit.akitov.jdu;

import java.io.IOException;
import java.nio.file.*;

public final class FileInfo extends PathInfo {
   protected FileInfo(Path path, int depth) {
     super(path, depth);
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
