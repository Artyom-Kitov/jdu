package ru.nsu.fit.akitov.jdu;

import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;

public class DirectoryInfo extends PathInfo {
  private final List<PathInfo> contentInfo = new LinkedList<>();
  private String str;

  public DirectoryInfo(Path path, int depth) {
    this.path = path;
    this.depth = depth;

    try {
      Path[] contentList = Files.list(path).toArray(Path[]::new);
      for (Path p : contentList) {
        PathInfo info = PathInfo.of(p, depth + 1);
        if (contentInfo.size() == 0 || info.byteSize > contentInfo.get(0).byteSize) {
          contentInfo.add(0, info);
        } else {
          contentInfo.add(info);
        }
      }
      for (PathInfo info : contentInfo) {
        this.byteSize += info.byteSize;
      }
    } catch (IOException exception) {}
  }

  @Override
  public String toString() {
    if (str != null) {
      return str;
    }
    StringBuilder result = new StringBuilder();
    result.append("  ".repeat(depth)).append("/").append(path.getFileName().toString());
    result.append(getSizeSuffix());

    if (depth + 1 > PathInfo.getMaxDepth()) {
      return result.toString();
    }

    for (int i = 0; i < contentInfo.size() && i < getNMax(); i++) {
      result.append("\n").append(contentInfo.get(i).toString());
    }
    str = result.toString();
    return str;
  }
}
