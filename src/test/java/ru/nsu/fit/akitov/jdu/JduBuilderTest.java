package ru.nsu.fit.akitov.jdu;

import org.junit.Test;
import ru.nsu.fit.akitov.jdu.core.JduTest;
import ru.nsu.fit.akitov.jdu.model.*;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class JduBuilderTest extends JduTest {
  @Test
  public void directory() throws IOException {
    FileSystem fs = fileSystem();
    Path tmp = fs.getPath("tmp");
    Files.createDirectory(tmp);

    JduFile file = JduBuilder.build(tmp, false);
    assertTrue(file instanceof JduDirectory);
    assertTrue(file.isAccessible());
    assertEquals(0, file.getByteSize());

    Path dir1 = fs.getPath("dir1");
    Files.createDirectory(dir1);
    Path dir2 = fs.getPath("dir1/dir2");
    Files.createDirectory(dir2);
    Path dir3 = fs.getPath("dir1/dir2/dir3");
    Files.createDirectory(dir3);
    Path f = fs.getPath("dir1/dir2/dir3/f");
    Files.createFile(f);
    Files.write(f, new byte[4096]);
    Path g = fs.getPath("dir1/g");
    Files.createFile(g);
    Files.write(g, new byte[1024]);

    JduDirectory jduDir1 = (JduDirectory) JduBuilder.build(dir1, false);
    JduFile jduDir2 = JduBuilder.build(dir2, false);
    JduFile jduG = JduBuilder.build(g, false);
    assertEquals(5120, jduDir1.getByteSize());
    assertEquals(List.of(jduDir2, jduG), jduDir1.getChildren());
  }

  @Test
  public void regularFile() throws IOException {
    FileSystem fs = fileSystem();
    Path tmp = fs.getPath("tmp");
    Files.createFile(tmp);

    JduFile file = JduBuilder.build(tmp, false);
    assertTrue(file instanceof JduRegularFile);
    assertTrue(file.isAccessible());
    assertEquals(0, file.getByteSize());

    Path f = fs.getPath("f");
    Files.createFile(f);
    Files.write(f, new byte[1024]);

    file = JduBuilder.build(f, false);
    assertEquals(1024, file.getByteSize());
  }

  @Test
  public void symlinkToFile() throws IOException {
    FileSystem fs = fileSystem();
    Path target = fs.getPath("f");
    Files.createFile(target);
    Path link = fs.getPath("link");
    Files.createSymbolicLink(link, target);

    JduFile file = JduBuilder.build(link, true);
    assertTrue(file instanceof JduSymlink);
    assertTrue(file.isAccessible());
    assertEquals(0, file.getByteSize());
    JduSymlink symlink = (JduSymlink) file;
    assertEquals(target, symlink.getTarget().getPath());
  }

  @Test
  public void symlinkToDirectory() throws IOException {
    FileSystem fs = fileSystem();
    Path target = fs.getPath("d");
    Files.createDirectory(target);
    Path link = fs.getPath("link");
    Files.createSymbolicLink(link, target);

    JduFile file = JduBuilder.build(link, true);
    assertTrue(file.isAccessible());
    assertEquals(0, file.getByteSize());
    JduSymlink symlink = (JduSymlink) file;
    assertEquals(target, symlink.getTarget().getPath());
  }

  @Test
  public void loopSymlink() throws IOException {
    FileSystem fs = fileSystem();
    Path d = fs.getPath("d");
    Files.createDirectory(d);
    Path link = fs.getPath("d/link");
    Files.createSymbolicLink(link, fs.getPath("d"));

    JduSymlink jduLink = (JduSymlink) JduBuilder.build(link, true);
    JduDirectory dir = (JduDirectory) jduLink.getTarget();
    assertEquals(jduLink, dir.getChildren().get(0));
  }

  @Test
  public void complexLoopSymlink() throws IOException {
    FileSystem fs = fileSystem();
    Path d1 = fs.getPath("d1");
    Files.createDirectory(d1);
    Path d2 = fs.getPath("d2");
    Files.createDirectory(d2);
    Path link1 = d1.resolve("link1");
    Files.createSymbolicLink(link1, d2);
    Path link2 = d2.resolve("link2");
    Files.createSymbolicLink(link2, d1);

    JduDirectory jd1 = (JduDirectory) JduBuilder.build(d1, true);
    JduSymlink jlink1 = (JduSymlink) jd1.getChildren().get(0);
    assertEquals(fs.getPath("d2"), jlink1.getTarget().getPath());
    JduDirectory jd2 = (JduDirectory) jlink1.getTarget();
    JduSymlink jlink2 = (JduSymlink) jd2.getChildren().get(0);
    assertEquals(fs.getPath("d1"), jlink2.getTarget().getPath());
  }
}
