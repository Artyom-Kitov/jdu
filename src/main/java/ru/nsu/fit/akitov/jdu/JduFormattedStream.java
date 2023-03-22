package ru.nsu.fit.akitov.jdu;

import ru.nsu.fit.akitov.jdu.model.JduDirectory;
import ru.nsu.fit.akitov.jdu.model.JduFile;
import ru.nsu.fit.akitov.jdu.model.JduRegularFile;
import ru.nsu.fit.akitov.jdu.model.JduSymlink;

import java.io.PrintStream;
import java.util.Comparator;

/**
 * A wrapper over {@code PrintStream}, which prints a {@code JduFile} in a tree-like representation.
 * Implements {@code JduVisitor}.
 */
public class JduFormattedStream {
  private final Printer printer;

  /**
   * @param stream   {@code PrintStream} to which everything is printed.
   * @param maxDepth max output recursion depth.
   * @param limit    print {@code limit} files in each directory and subdirectory.
   */
  public JduFormattedStream(PrintStream stream, int maxDepth, int limit) {
    printer = new Printer(stream, maxDepth, limit);
  }

  /**
   * Prints the given {@code JduFile} name, its size and children recursively (if built)
   * into {@code PrintStream}.
   * <p>Examples:</p>
   * <li>
   *   Output for a 4-KiB file foo:
   *   <pre>
   *    foo [4.000 KiB]
   *   </pre>
   * </li>
   * <li>
   *   Output for a directory:
   *   <pre>
   *     /foo [20.000 B]
   *       /bar [4.000 B]
   *         f1 [3.000 B]
   *         f2 [1.000 B]
   *       /baz [16.000 B]
   *         f3 [16.000 B]
   *   </pre>
   * </li>
   * <li>
   *   Output for a symlink:
   *   <pre>
   *     link [symlink]
   *       foo [421.000 B]
   *   </pre>
   * </li>
   */
  public void print(JduFile file) {
    file.accept(printer);
  }

  private static class Printer implements JduVisitor {
    private final PrintStream stream;
    private final int maxDepth;
    private final int limit;
    private int currentDepth;
    private static final Comparator<? super JduFile> comparator = (a, b) -> -Long.compare(a.getByteSize(), b.getByteSize());

    public Printer(PrintStream stream, int maxDepth, int limit) {
      this.stream = stream;
      this.maxDepth = maxDepth;
      this.limit = limit;
      this.currentDepth = 0;
    }

    private static String getSizeSuffix(JduFile file) {
      if (!file.isAccessible()) {
        return " [unknown]";
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

    @Override
    public void visit(JduRegularFile regularFile) {
      stream.println("  ".repeat(currentDepth) + regularFile.getPath().getFileName() + getSizeSuffix(regularFile));
    }

    @Override
    public void visit(JduSymlink symlink) {
      stream.println("  ".repeat(currentDepth) + symlink.getPath().getFileName() + " [symlink]");
      if (currentDepth < maxDepth && symlink.getTarget() != null) {
        currentDepth++;
        symlink.getTarget().accept(this);
        currentDepth--;
      }
    }

    @Override
    public void visit(JduDirectory directory) {
      stream.println("  ".repeat(currentDepth) + "/" + directory.getPath().getFileName() + getSizeSuffix(directory));
      if (currentDepth == maxDepth || !directory.isAccessible()) {
        return;
      }
      directory.getChildren().sort(comparator);
      currentDepth++;
      for (int i = 0; i < directory.getChildren().size() && i < limit; i++) {
        directory.getChildren().get(i).accept(this);
      }
      currentDepth--;
    }
  }
}
