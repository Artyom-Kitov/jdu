package ru.nsu.fit.akitov.jdu;

import org.junit.Test;

import static junit.framework.TestCase.*;

public class ArgumentsBuilderTests {
  @Test
  public void defaultParams() throws JduException {
    Arguments args = Arguments.Builder.buildFromStrings();
    assertEquals(Arguments.DEFAULT_DEPTH, args.depth());
    assertEquals(Arguments.DEFAULT_LIMIT, args.limit());
    assertFalse(args.showSymlinks());
    assertEquals(Arguments.DEFAULT_PATH, args.fileName());
  }

  @Test(expected = JduException.class)
  public void unknownFileName() throws JduException {
    Arguments.Builder.buildFromStrings("f");
  }

  @Test(expected = JduException.class)
  public void depth() throws JduException {
    Arguments args = Arguments.Builder.buildFromStrings("--depth", "42");
    assertEquals(42, args.depth());
    assertEquals(Arguments.DEFAULT_LIMIT, args.limit());
    assertFalse(args.showSymlinks());
    assertEquals(Arguments.DEFAULT_PATH, args.fileName());

    Arguments.Builder.buildFromStrings("--depth");
  }

  @Test(expected = JduException.class)
  public void limit() throws JduException {
    Arguments args = Arguments.Builder.buildFromStrings("--limit", "42");
    assertEquals(Arguments.DEFAULT_DEPTH, args.depth());
    assertEquals(42, args.limit());
    assertFalse(args.showSymlinks());
    assertEquals(Arguments.DEFAULT_PATH, args.fileName());

    Arguments.Builder.buildFromStrings("--limit");
  }

  @Test
  public void symlinks() throws JduException {
    Arguments args = Arguments.Builder.buildFromStrings("-L");
    assertEquals(Arguments.DEFAULT_DEPTH, args.depth());
    assertEquals(Arguments.DEFAULT_LIMIT, args.limit());
    assertTrue(args.showSymlinks());
    assertEquals(Arguments.DEFAULT_PATH, args.fileName());
  }

  @Test
  public void anyOrder() throws JduException {
    Arguments args1 = Arguments.Builder.buildFromStrings("--depth", "12", "--limit", "8", "-L");
    Arguments args2 = Arguments.Builder.buildFromStrings("--limit", "8", "--depth", "12", "-L");
    Arguments args3 = Arguments.Builder.buildFromStrings("--depth", "12", "-L", "--limit", "8");
    Arguments args4 = Arguments.Builder.buildFromStrings("-L", "--limit", "8", "--depth", "12");
    assertEquals(args1, args2);
    assertEquals(args2, args3);
    assertEquals(args3, args4);
  }

  @Test(expected = JduException.class)
  public void fileNameAtMiddle() throws JduException {
    Arguments.Builder.buildFromStrings("--depth", "12", "file.txt", "--limit", "8", "-L");
  }
}
