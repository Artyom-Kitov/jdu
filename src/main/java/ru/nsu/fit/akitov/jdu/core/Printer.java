package ru.nsu.fit.akitov.jdu.core;

public interface Printer {
  void print(JduRegularFile regularFile);
  void print(JduSymlink symlink);
  void print(JduDirectory directory);
}
