package org.example;

import java.io.IOException;
import java.nio.file.*;

public class SymlinkInfo extends PathInfo {
  PathInfo targetInfo;

  public SymlinkInfo(Path path, int depth) throws IOException {
    this.path = path;
    this.depth = depth;
    this.byteSize = Files.size(path);

    Path target = Files.readSymbolicLink(path);
    if (PathInfo.SYMLINKS && depth + 1 <= PathInfo.MAX_DEPTH) {
      targetInfo = FileCreator.create(target, depth + 1);
    }
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("  ".repeat(depth)).append("/").append(path.getFileName()).append(getSizeSuffix()).append(" - symlink");
    if (targetInfo != null) {
      str.append("\n").append(targetInfo);
    }
    return str.toString();
  }
}
