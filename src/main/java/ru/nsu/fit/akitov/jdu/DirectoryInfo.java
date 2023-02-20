package ru.nsu.fit.akitov.jdu;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

public class DirectoryInfo extends PathInfo {
  private final ArrayList<PathInfo> contentInfo = new ArrayList<>();
  private String str;

  public DirectoryInfo(Path path, int depth) {
    this.path = path;
    this.depth = depth;

    if (SYMLINKS && Files.isSymbolicLink(path)) {
      return;
    }

    Path[] contentList;
    try {
      contentList = Files.list(path).toArray(Path[]::new);
    } catch (IOException exception) {
      return;
    }

    for (Path p : contentList) {
      try {
        PathInfo info = FileCreator.create(p, depth + 1);
        if (contentInfo.size() == 0 || info.byteSize > contentInfo.get(0).byteSize) {
          contentInfo.add(0, info);
        } else {
          contentInfo.add(info);
        }
      } catch (IOException exception) {}
    }

    for (PathInfo info : contentInfo) {
      this.byteSize += info.byteSize;
    }
  }

  @Override
  public String toString() {
    if (str != null) {
      return str;
    }
    StringBuilder result = new StringBuilder();
    result.append("  ".repeat(depth)).append("/").append(path.getFileName().toString());
    result.append(getSizeSuffix());

    if (depth + 1 > MAX_DEPTH) {
      return result.toString();
    }

    for (int i = 0; i < contentInfo.size() && i < getNMax(); i++) {
      result.append("\n");
      result.append(contentInfo.get(i).toString());
    }
    str = result.toString();
    return str;
  }
}
