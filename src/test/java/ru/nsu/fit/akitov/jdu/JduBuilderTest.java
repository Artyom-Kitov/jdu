package ru.nsu.fit.akitov.jdu;

import org.junit.Test;
import ru.nsu.fit.akitov.jdu.core.JduTest;
import ru.nsu.fit.akitov.jdu.model.*;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static junit.framework.TestCase.*;

public class JduBuilderTest extends JduTest {
  private final JduBuilder jduBuilder = new JduBuilder();
  @Test
  public void emptyDirectory() throws IOException {
    FileSystem fs = fileSystem();
    Path tmp = fs.getPath("tmp");
    Files.createDirectory(tmp);

    Arguments.Builder builder = new Arguments.Builder();
    JduFile file = jduBuilder.build(builder.setFileName(tmp).build());
    assertTrue(file instanceof JduDirectory);
    assertTrue(file.isAccessible());
    assertEquals(0, file.getByteSize());
  }

  @Test
  public void emptyFile() throws IOException {
    FileSystem fs = fileSystem();
    Path tmp = fs.getPath("tmp");
    Files.createFile(tmp);

    Arguments.Builder builder = new Arguments.Builder();
    JduFile file = jduBuilder.build(builder.setFileName(tmp).build());
    assertTrue(file instanceof JduRegularFile);
    assertTrue(file.isAccessible());
    assertEquals(0, file.getByteSize());
  }

  @Test
  public void symlinkToFile() throws IOException {
    FileSystem fs = fileSystem();
    Path target = fs.getPath("f");
    Files.createFile(target);
    Path link = fs.getPath("tmp");
    Files.createSymbolicLink(link, target);

    Arguments.Builder builder = new Arguments.Builder();
    JduFile file = jduBuilder.build(builder.setFileName(link).setSymlinksDisplay(true).build());
    assertTrue(file instanceof JduSymlink);
    assertTrue(file.isAccessible());
    assertEquals(0, file.getByteSize());
    JduSymlink symlink = (JduSymlink)file;
    assertEquals(target, symlink.getTarget().getPath());
  }

  @Test
  public void fileSize() throws IOException {
    FileSystem fs = fileSystem();
    Path f = fs.getPath("f");
    Files.createFile(f);
    Files.write(f, new byte[1024]);

    Arguments.Builder builder = new Arguments.Builder();
    JduFile file = jduBuilder.build(builder.setFileName(f).build());
    assertEquals(1024, file.getByteSize());
  }

  @Test
  public void nonEmptyDirectory() throws IOException {
    FileSystem fs = fileSystem();
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

    Arguments.Builder builder = new Arguments.Builder();
    JduDirectory jduDir1 = (JduDirectory) jduBuilder.build(builder.setFileName(dir1).build());
    JduFile jduDir2 = jduBuilder.build(builder.setFileName(dir2).build());
    JduFile jduG = jduBuilder.build(builder.setFileName(g).build());
    assertEquals(5120, jduDir1.getByteSize());
    assertEquals(List.of(jduDir2, jduG), jduDir1.getContent());
  }

  @Test
  public void loopSymlink() throws IOException {
    FileSystem fs = fileSystem();
    Path d = fs.getPath("/d");
    Files.createDirectory(d);
    Path link = fs.getPath("/d/link");
    Files.createSymbolicLink(link, fs.getPath("/"));

    Arguments.Builder builder = new Arguments.Builder();
    JduSymlink jduLink = (JduSymlink) jduBuilder.build(builder.setFileName(link).setSymlinksDisplay(true).build());
    assertEquals(fs.getPath("/"), jduLink.getTarget().getPath());
    jduLink = (JduSymlink) jduBuilder.build(builder.build());
    assertNull(jduLink.getTarget());
  }
}
