package ru.nsu.fit.akitov.jdu;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.HashSet;

public class SymlinkInfo extends PathInfo {
  private PathInfo targetInfo;

  private static final Set<Path> visited = new HashSet<>();

  public SymlinkInfo(Path path, int depth) {
    super(path, depth);
    this.byteSize = 0;

    if (PathInfo.symlinksShown() && depth + 1 <= PathInfo.getMaxDepth() && !visited.contains(path)) {
      visited.add(path);
      try {
        Path target = Files.readSymbolicLink(path);
        targetInfo = PathInfo.of(target, depth + 1);
      } catch (IOException exception) {}
    }
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("  ".repeat(depth)).append(path.getFileName()).append(" - symlink");
    if (targetInfo != null) {
      str.append("\n").append(targetInfo);
    }
    return str.toString();
  }
}
