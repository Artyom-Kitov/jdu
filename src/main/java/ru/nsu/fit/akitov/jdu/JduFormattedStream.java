package ru.nsu.fit.akitov.jdu;

import ru.nsu.fit.akitov.jdu.model.JduDirectory;
import ru.nsu.fit.akitov.jdu.model.JduFile;
import ru.nsu.fit.akitov.jdu.model.JduRegularFile;
import ru.nsu.fit.akitov.jdu.model.JduSymlink;

import java.io.PrintStream;

/**
 * A wrapper over {@code PrintStream}, which prints a {@code JduFile} in tree-like representation.
 * Implements {@code JduVisitor}.
 */
public class JduFormattedStream implements JduVisitor {
  private final PrintStream stream;
  private final int depth;
  private final int limit;

  /**
   * @param stream {@code PrintStream} to which everything is printed.
   * @param depth max output recursion depth.
   * @param limit print {@code limit} files in each directory.
   */
  public JduFormattedStream(PrintStream stream, int depth, int limit) {
    this.stream = stream;
    // CR: maxDepth, maxLimit
    this.depth = depth;
    this.limit = limit;
  }

  private static String getSizeSuffix(JduFile file) {
    if (!file.isAccessible()) {
      return " [inaccessible]";
    }
    float size = file.getByteSize();
    String suffix = " B";
    if (size / 1024 >= 1) {
      size /= 1024;
      suffix = " KiB";
    }
    if (size / 1024 >= 1) {
      size /= 1024;
      suffix = " MiB";
    }
    return " [" + String.format("%.3f", size) + suffix + "]";
  }

  /**
   * Prints the given {@code JduRegularFile} name and its size
   * to the {@code PrintStream} using {@code JduVisitor::visit}.
   * @param regularFile file to be printed.
   */
  @Override
  public void visit(JduRegularFile regularFile) {
    stream.println("  ".repeat(regularFile.getDepth()) + regularFile.getPath().getFileName() + getSizeSuffix(regularFile));
  }

  // CR: example
  /**
   * Prints the given {@code JduSymlink} name and its target recursively (if built)
   * to the {@code PrintStream} using {@code JduVisitor::visit}.
   * @param symlink symlink to be printed.
   */
  @Override
  public void visit(JduSymlink symlink) {
    stream.println("  ".repeat(symlink.getDepth()) + symlink.getPath().getFileName() + " [symlink]");
    if (symlink.getTarget() != null) {
      symlink.getTarget().accept(this);
    }
  }

  /**
   * Prints the given {@code JduDirectory} name, size and its content recursively
   * to the {@code PrintStream} using {@code JduVisitor::visit}.
   * @param directory directory to be printed.
   */
  @Override
  public void visit(JduDirectory directory) {
    stream.println("  ".repeat(directory.getDepth()) + "/" + directory.getPath().getFileName() + getSizeSuffix(directory));
    if (directory.getDepth() == depth) {
      return;
    }
    directory.getContent().sort((p1, p2) -> -Long.compare(p1.getByteSize(), p2.getByteSize()));
    for (int i = 0; i < directory.getContent().size() && i < limit; i++) {
      directory.getContent().get(i).accept(this);
    }
  }
}
