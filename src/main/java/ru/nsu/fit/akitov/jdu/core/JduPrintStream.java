package ru.nsu.fit.akitov.jdu.core;

public interface JduPrintStream {
  void print(JduRegularFile regularFile);
  void print(JduSymlink symlink);
  void print(JduDirectory directory);
}
