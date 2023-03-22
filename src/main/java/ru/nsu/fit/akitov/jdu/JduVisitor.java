package ru.nsu.fit.akitov.jdu;

import ru.nsu.fit.akitov.jdu.model.JduDirectory;
import ru.nsu.fit.akitov.jdu.model.JduRegularFile;
import ru.nsu.fit.akitov.jdu.model.JduSymlink;

public interface JduVisitor {

  void visit(JduRegularFile regularFile);

  void visit(JduSymlink symlink);

  void visit(JduDirectory directory);
}
