package org.example;

import java.nio.file.*;

public abstract class PathInfo {
  protected Path path;
  protected int depth;
  protected long byteSize;
  protected boolean accessible = true;

  protected static int MAX_DEPTH = 8;
  protected static int N_MAX = 1024;
  protected static boolean SYMLINKS = false;

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

  public static void showSymlinks() {
    SYMLINKS = true;
  }

  String getSizeSuffix() {
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
    return " [" + size + suffix + "]";
  }
}
