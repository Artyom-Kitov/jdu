package ru.nsu.fit.akitov.jdu;

import ru.nsu.fit.akitov.jdu.model.JduDirectory;
import ru.nsu.fit.akitov.jdu.model.JduFile;
import ru.nsu.fit.akitov.jdu.model.JduRegularFile;
import ru.nsu.fit.akitov.jdu.model.JduSymlink;

import java.io.PrintStream;

public class JduFormattedStream implements JduVisitor {
  private final PrintStream stream;
  private final int depth;
  private final int limit;

  public JduFormattedStream(PrintStream stream, int depth, int limit) {
    this.stream = stream;
    this.depth = depth;
    this.limit = limit;
  }

  private static String getSizeSuffix(JduFile file) {
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
    stream.println("  ".repeat(regularFile.getDepth()) + regularFile.getPath().getFileName() + getSizeSuffix(regularFile));
  }

  @Override
  public void visit(JduSymlink symlink) {
    stream.println("  ".repeat(symlink.getDepth()) + symlink.getPath().getFileName() + " [symlink]");
    if (symlink.getTarget() != null) {
      symlink.getTarget().accept(this);
    }
  }

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
