package ru.nsu.fit.akitov.jdu;

import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;

public class DirectoryInfo extends PathInfo {
  // Cross CR: change LinkedList to ArrayList
  private final List<PathInfo> contentInfo = new LinkedList<>();
  private String str;

  // Cross CR: too much responsibility for constructor (?)
  public DirectoryInfo(Path path, int depth) {
    this.path = path;
    this.depth = depth;

    try {
      Path[] contentList = Files.list(path).toArray(Path[]::new);
      for (Path p : contentList) {
        PathInfo info = PathInfo.of(p, depth + 1);
        // Cross CR : make proper sort after filling  contentInfo
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

  // Cross CR: Create separate method to build string (?)
  @Override
  public String toString() {
    if (str != null) {
      return str;
    }
    StringBuilder result = new StringBuilder();
    result.append("  ".repeat(depth)).append("/").append(path.getFileName().toString());
    result.append(getSizeSuffix());

    // Cross CR: equal to (depth == PathInfo.getMaxDepth()) but less informative
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
