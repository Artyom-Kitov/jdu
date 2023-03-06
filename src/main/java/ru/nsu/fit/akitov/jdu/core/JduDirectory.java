package ru.nsu.fit.akitov.jdu.core;

import ru.nsu.fit.akitov.jdu.Arguments;

import java.nio.file.*;
import java.util.List;

public final class JduDirectory extends JduFile {
  final List<JduFile> content;

  JduDirectory(Path path, Arguments args, int depth, List<JduFile> content) {
    super(path, args, depth);
    this.content = content;
    for (JduFile info : content) {
      this.byteSize += info.byteSize;
    }
  }

  @Override
  public void print(Printer printer) {
    printer.print(this);
  }

  // Cross CR: Create separate method to build string (?)
//  @Override
//  public String toString() {
//    if (str != null) {
//      return str;
//    }
//    StringBuilder result = new StringBuilder();
//    result.append("  ".repeat(depth)).append("/").append(path.getFileName().toString());
//    result.append(getSizeSuffix());
//
//    if (depth == JduFile.getMaxDepth()) {
//      return result.toString();
//    }
//
//    for (int i = 0; i < contentInfo.size() && i < getNMax(); i++) {
//      result.append("\n").append(contentInfo.get(i).toString());
//    }
//    str = result.toString();
//    return str;
//  }
}
