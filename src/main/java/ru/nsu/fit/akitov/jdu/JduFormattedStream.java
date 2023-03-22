package ru.nsu.fit.akitov.jdu;

import ru.nsu.fit.akitov.jdu.model.JduDirectory;
import ru.nsu.fit.akitov.jdu.model.JduFile;
import ru.nsu.fit.akitov.jdu.model.JduRegularFile;
import ru.nsu.fit.akitov.jdu.model.JduSymlink;

import java.io.PrintStream;

/**
 * A wrapper over {@code PrintStream}, which prints a {@code JduFile} in a tree-like representation.
 * Implements {@code JduVisitor}.
 */
public class JduFormattedStream implements JduVisitor {

  private final PrintStream stream;
  private final int maxDepth;
  private final int limit;
  private int currentDepth;

  /**
   * @param stream   {@code PrintStream} to which everything is printed.
   * @param maxDepth max output recursion depth.
   * @param limit    print {@code limit} files in each directory and subdirectory.
   */
  public JduFormattedStream(PrintStream stream, int maxDepth, int limit) {
    this.stream = stream;
    this.maxDepth = maxDepth;
    this.limit = limit;
    this.currentDepth = 0;
  }

  private static String getSizeSuffix(JduFile file) {
    if (!file.isAccessible()) {
      // CR: better '[unknown]'
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
   * <p>
   * Prints the given {@code JduRegularFile} name and its size
   * to the {@code PrintStream} using {@code JduVisitor::visit}.
   * </p>
   * <p></p>
   * e.g. output for a 4-KiB file foo:
   * <pre>
   * foo [4.000 KiB]
   * </pre>
   */
  @Override
  public void visit(JduRegularFile regularFile) {
    stream.println("  ".repeat(currentDepth) + regularFile.getPath().getFileName() + getSizeSuffix(regularFile));
  }

  /**
   * <p>
   * Prints the given {@code JduSymlink} name and its target
   * recursively (if built) to the {@code PrintStream}
   * using {@code JduVisitor::visit}.
   * </p>
   * e.g.:
   * <pre>
   * /foo [16.000 B]
   *   f1 [16.000 B]
   *   link [symlink]
   *     f2 [8 KiB]
   * </pre>
   */
  @Override
  public void visit(JduSymlink symlink) {
    stream.println("  ".repeat(currentDepth) + symlink.getPath().getFileName() + " [symlink]");
    if (currentDepth < maxDepth && symlink.getTarget() != null) {
      currentDepth++;
      symlink.getTarget().accept(this);
      currentDepth--;
    }
  }

  /**
   * <p>
   * Prints the given {@code JduSymlink} name and its target recursively (if built)
   * to the {@code PrintStream} using {@code JduVisitor::visit}.
   * </p>
   * e.g.:
   * <pre>
   * /foo [20.000 B]
   *   /bar [4.000 B]
   *     f1 [3.000 B]
   *     f2 [1.000 B]
   *   /baz [16.000 B]
   *     f3 [16.000 B]
   * </pre>
   */
  @Override
  public void visit(JduDirectory directory) {
    stream.println("  ".repeat(currentDepth) + "/" + directory.getPath().getFileName() + getSizeSuffix(directory));
    if (currentDepth == maxDepth || !directory.isAccessible()) {
      return;
    }
    // CR: make comparator private static final field
    directory.getChildren().sort((a, b) -> -Long.compare(a.getByteSize(), b.getByteSize()));
    currentDepth++;
    for (int i = 0; i < directory.getChildren().size() && i < limit; i++) {
      directory.getChildren().get(i).accept(this);
    }
    currentDepth--;
  }
}
