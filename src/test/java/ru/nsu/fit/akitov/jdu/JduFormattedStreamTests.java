package ru.nsu.fit.akitov.jdu;

import org.junit.Before;
import org.junit.Test;
import ru.nsu.fit.akitov.jdu.core.JduTest;
import ru.nsu.fit.akitov.jdu.model.JduFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.TestCase.assertEquals;

public class JduFormattedStreamTests extends JduTest {
  private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
  private final PrintStream ps = new PrintStream(baos);

  @Before
  public void reset() {
    baos.reset();
  }

  @Test
  public void emptyFile() throws IOException {
    FileSystem fs = fileSystem();
    Path tmp = fs.getPath("tmp");
    Files.createFile(tmp);

    Arguments args = argumentsBuilder().setFileName(tmp).build();
    JduFile file = jduBuilder().build(args);

    JduFormattedStream stream = new JduFormattedStream(ps, args.depth(), args.limit());
    file.accept(stream);

    assertEquals("tmp [0.000 B]\n", baos.toString());
  }

  @Test
  public void emptyDirectory() throws IOException {
    FileSystem fs = fileSystem();
    Path tmp = fs.getPath("tmp");
    Files.createDirectory(tmp);

    Arguments args = argumentsBuilder().setFileName(tmp).build();
    JduFile file = jduBuilder().build(args);

    JduFormattedStream stream = new JduFormattedStream(ps, args.depth(), args.limit());
    file.accept(stream);

    assertEquals("/tmp [0.000 B]\n", baos.toString());
  }

  @Test
  public void limitParameter() throws IOException {
    FileSystem fs = fileSystem();
    Path d = fs.getPath("d");
    Files.createDirectory(d);
    Path d1 = d.resolve("d1");
    Files.createDirectory(d1);
    Path d2 = d.resolve("d2");
    Files.createDirectory(d2);
    Path f = d2.resolve("f");
    Files.createFile(f);
    Files.write(f, new byte[1024]);
    Path d3 = d1.resolve("d3");
    Files.createDirectory(d3);

    Arguments args = argumentsBuilder().setLimit(1).setFileName(d).build();
    JduFile directory = jduBuilder().build(args);

    JduFormattedStream stream = new JduFormattedStream(ps, args.depth(), args.limit());
    directory.accept(stream);

    assertEquals("/d [1.000 KiB]\n  /d2 [1.000 KiB]\n    f [1.000 KiB]\n", baos.toString());
  }

  @Test
  public void depthParameter() throws IOException {
    FileSystem fs = fileSystem();
    Path d = fs.getPath("d");
    Files.createDirectory(d);
    Path d1 = d.resolve("d1");
    Files.createDirectory(d1);
    Path d2 = d.resolve("d2");
    Files.createDirectory(d2);
    Path f = d2.resolve("f");
    Files.createFile(f);
    Files.write(f, new byte[1024]);
    Path d3 = d1.resolve("d3");
    Files.createDirectory(d3);

    Arguments args = argumentsBuilder().setDepth(1).setFileName(d).build();
    JduFile directory = jduBuilder().build(args);

    JduFormattedStream stream = new JduFormattedStream(ps, args.depth(), args.limit());
    directory.accept(stream);

    assertEquals("/d [1.000 KiB]\n  /d2 [1.000 KiB]\n  /d1 [0.000 B]\n", baos.toString());
  }

  @Test
  public void symlinksParameterEnabled() throws IOException {
    FileSystem fs = fileSystem();
    Path d1 = fs.getPath("d1");
    Files.createDirectory(d1);
    Path f1 = d1.resolve("f1");
    Files.createFile(f1);
    Path d2 = fs.getPath("d2");
    Files.createDirectory(d2);

    Path link1 = fs.getPath("link1");
    Files.createSymbolicLink(link1, f1);

    Path link2 = fs.getPath("link2");
    Files.createSymbolicLink(link2, d2);

    Arguments arguments = argumentsBuilder().setSymlinksDisplay(true).setFileName(fs.getPath(".")).build();
    JduFile file = jduBuilder().build(arguments);

    JduFormattedStream stream = new JduFormattedStream(ps, arguments.depth(), arguments.limit());
    file.accept(stream);

    assertEquals("""
                      /. [0.000 B]
                        /d1 [0.000 B]
                          f1 [0.000 B]
                        /d2 [0.000 B]
                        link1 [symlink]
                          f1 [0.000 B]
                        link2 [symlink]
                          /d2 [0.000 B]
                        """, baos.toString());

  }

  @Test
  public void symlinksParameterDisabled() throws IOException {
    FileSystem fs = fileSystem();
    Path d1 = fs.getPath("d1");
    Files.createDirectory(d1);
    Path f1 = d1.resolve("f1");
    Files.createFile(f1);
    Path d2 = fs.getPath("d2");
    Files.createDirectory(d2);

    Path link1 = fs.getPath("link1");
    Files.createSymbolicLink(link1, f1);

    Path link2 = fs.getPath("link2");
    Files.createSymbolicLink(link2, d2);

    Arguments arguments = argumentsBuilder().setSymlinksDisplay(false).setFileName(fs.getPath(".")).build();
    JduFile file = jduBuilder().build(arguments);

    JduFormattedStream stream = new JduFormattedStream(ps, arguments.depth(), arguments.limit());
    file.accept(stream);
    
    assertEquals("""
            /. [0.000 B]
              /d1 [0.000 B]
                f1 [0.000 B]
              /d2 [0.000 B]
              link1 [symlink]
              link2 [symlink]
              """, baos.toString());
  }
}
