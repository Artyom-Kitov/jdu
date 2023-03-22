package ru.nsu.fit.akitov.jdu.model;

import org.junit.Before;
import org.junit.Test;
import ru.nsu.fit.akitov.jdu.Arguments;
import ru.nsu.fit.akitov.jdu.JduFormattedStream;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;

public class JduFormattedStreamTests {
  private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
  private final PrintStream ps = new PrintStream(baos);

  @Before
  public void reset() {
    baos.reset();
  }

  @Test
  public void printFile() {
    JduFile file = new JduRegularFile(Path.of("tmp"), 8192);
    JduFormattedStream stream = new JduFormattedStream(ps, Arguments.DEFAULT_DEPTH, Arguments.DEFAULT_LIMIT);
    stream.print(file);
    assertEquals("tmp [8.000 KiB]\n", baos.toString());
  }

  @Test
  public void printDirectory() {
    JduDirectory d = new JduDirectory(Path.of("d"), 1024, null);
    JduDirectory d1 = new JduDirectory(Path.of("d/d1"), 0, null);
    JduDirectory d2 = new JduDirectory(Path.of("d/d2"), 1024, null);
    d.setChildren(Arrays.asList(d1, d2));
    JduDirectory d3 = new JduDirectory(Path.of("d/d1/d3"), 0, new ArrayList<>());
    d1.setChildren(Arrays.asList(d3));
    JduRegularFile f = new JduRegularFile(Path.of("d/d2/f"), 1024);
    d2.setChildren(Arrays.asList(f));

    JduFormattedStream stream = new JduFormattedStream(ps, Arguments.DEFAULT_DEPTH, Arguments.DEFAULT_LIMIT);
    stream.print(d);

    assertEquals("""
            /d [1.000 KiB]
              /d2 [1.000 KiB]
                f [1.000 KiB]
              /d1 [0.000 B]
                /d3 [0.000 B]
                 """, baos.toString());
  }


  @Test
  public void limitParameter() {
    JduDirectory d = new JduDirectory(Path.of("d"), 1024, null);
    JduDirectory d1 = new JduDirectory(Path.of("d/d1"), 0, null);
    JduDirectory d2 = new JduDirectory(Path.of("d/d2"), 1024, null);
    JduDirectory d3 = new JduDirectory(Path.of("d/d3"), 0, null);
    d.setChildren(Arrays.asList(d1, d2, d3));
    JduRegularFile f = new JduRegularFile(Path.of("d/d2/f"), 1024);
    d2.setChildren(Arrays.asList(f));

    JduFormattedStream stream = new JduFormattedStream(ps, Arguments.DEFAULT_DEPTH, 1);
    stream.print(d);

    assertEquals("/d [1.000 KiB]\n  /d2 [1.000 KiB]\n    f [1.000 KiB]\n", baos.toString());
  }

  @Test
  public void depthParameter() {
    JduDirectory d = new JduDirectory(Path.of("d"), 1024, null);
    JduDirectory d1 = new JduDirectory(Path.of("d/d1"), 0, new ArrayList<>());
    JduDirectory d2 = new JduDirectory(Path.of("d/d2"), 1024, null);
    JduDirectory d3 = new JduDirectory(Path.of("d/d3"), 0, new ArrayList<>());
    d.setChildren(Arrays.asList(d1, d2, d3));
    JduRegularFile f = new JduRegularFile(Path.of("d/d2/f"), 1024);
    d2.setChildren(Arrays.asList(f));

    JduFormattedStream stream = new JduFormattedStream(ps, 1, Arguments.DEFAULT_LIMIT);
    stream.print(d);

    assertEquals("/d [1.000 KiB]\n  /d2 [1.000 KiB]\n  /d1 [0.000 B]\n  /d3 [0.000 B]\n", baos.toString());
  }

  @Test
  public void symlinks() {
    JduDirectory root = new JduDirectory(Path.of("."), 0, null);
    JduDirectory d1 = new JduDirectory(Path.of("d1"), 0, null);
    JduDirectory d2 = new JduDirectory(Path.of("d2"), 0, new ArrayList<>());
    JduRegularFile f1 = new JduRegularFile(Path.of("d1/f1"), 0);
    JduSymlink link1 = new JduSymlink(Path.of("link1"), f1);
    JduSymlink link2 = new JduSymlink(Path.of("link2"), d2);

    root.setChildren(Arrays.asList(d1, d2, link1, link2));
    d1.setChildren(Arrays.asList(f1));

    JduFormattedStream stream = new JduFormattedStream(ps, Arguments.DEFAULT_DEPTH, Arguments.DEFAULT_LIMIT);
    stream.print(root);
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
  public void simpleLoop() {
    JduDirectory d = new JduDirectory(Path.of("d"), 0, null);
    JduSymlink link = new JduSymlink(Path.of("d/link"), d);
    d.setChildren(Arrays.asList(link));

    JduFormattedStream stream = new JduFormattedStream(ps, 8, Arguments.DEFAULT_LIMIT);
    stream.print(d);

    assertEquals("""
            /d [0.000 B]
              link [symlink]
                /d [0.000 B]
                  link [symlink]
                    /d [0.000 B]
                      link [symlink]
                        /d [0.000 B]
                          link [symlink]
                            /d [0.000 B]
            """, baos.toString());

    stream = new JduFormattedStream(ps, 2, 1024);
    baos.reset();
    stream.print(d);

    assertEquals("""
            /d [0.000 B]
              link [symlink]
                /d [0.000 B]
                """, baos.toString());
  }

  @Test
  public void complexLoop() {
    JduDirectory d1 = new JduDirectory(Path.of("d1"), 0, null);
    JduDirectory d2 = new JduDirectory(Path.of("d2"), 0, null);
    JduSymlink link1 = new JduSymlink(Path.of("d1/link1"), d2);
    JduSymlink link2 = new JduSymlink(Path.of("d2/link2"), d1);
    d1.setChildren(Arrays.asList(link1));
    d2.setChildren(Arrays.asList(link2));
    JduFormattedStream stream = new JduFormattedStream(ps, 8, 1024);
    stream.print(d1);

    assertEquals("""
            /d1 [0.000 B]
              link1 [symlink]
                /d2 [0.000 B]
                  link2 [symlink]
                    /d1 [0.000 B]
                      link1 [symlink]
                        /d2 [0.000 B]
                          link2 [symlink]
                            /d1 [0.000 B]
            """, baos.toString());
  }
}
