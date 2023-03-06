package ru.nsu.fit.akitov.jdu;

import java.io.IOException;
import java.nio.file.*;

public abstract class PathInfo {
  protected Path path;
  protected int depth;
  protected long byteSize;
  protected boolean accessible = true;

  private static int MAX_DEPTH = 8;
  private static int N_MAX = 1024;
  private static boolean SYMLINKS_SHOWN = false;

  protected PathInfo(Path path, int depth) {
    this.path = path;
    this.depth = depth;
  }

  public static int getMaxDepth() {
    return MAX_DEPTH;
  }
  public static void setMaxDepth(int depth) {
    MAX_DEPTH = depth;
  }

  public static int getNMax() {
    return N_MAX;
  }

  public static void setNMax(int nMax) {
    N_MAX = nMax;
  }

  public static boolean symlinksShown() {
    return SYMLINKS_SHOWN;
  }

  public static void showSymlinks() {
    SYMLINKS_SHOWN = true;
  }

  /**
   * Returns an object with all information about a file or a directory.
   * @param path path to a file or a directory.
   * @return the resulting PathInfo.
   * @throws IOException if no file or directory exists.
   */
  public static PathInfo of(Path path) throws IOException {
    return of(path, 0);
  }

  protected static PathInfo of(Path path, int depth) throws IOException {
    PathInfo result;
    if (Files.isSymbolicLink(path)) {
      result = new SymlinkInfo(path, depth);
    } else if (Files.isDirectory(path)) {
      result = new DirectoryInfo(path, depth);
    } else if (Files.isRegularFile(path)) {
      result = new FileInfo(path, depth);
    } else {
      // AssertionError ||  IllegalStateException
      throw new IOException("no such file or directory");
    }
    return result;
  }

  protected String getSizeSuffix() {
    if (!accessible) {
      return " [inaccessible]";
    }
    float size = byteSize;
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
}
