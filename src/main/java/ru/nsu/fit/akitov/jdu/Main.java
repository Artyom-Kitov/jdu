package ru.nsu.fit.akitov.jdu;

import ru.nsu.fit.akitov.jdu.model.JduBuilder;
import ru.nsu.fit.akitov.jdu.model.JduFile;

public class Main {
  public static void main(String[] args) {
    Arguments arguments;
    try {
      arguments = Arguments.Builder.buildFromStrings(args);
    } catch (JduException e) {
      System.err.println("Error: " + e.getMessage());
      System.err.println(usage());
      return;
    } catch (Exception exception) {
      System.err.println("Something unknown happened, please try again or contact a developer");
      exception.printStackTrace();
      return;
    }

    JduFile file = JduBuilder.build(arguments.fileName(), arguments.showSymlinks());
    JduPrinter printer = new JduPrinter(System.out, arguments.depth(), arguments.limit());
    printer.print(file);
  }

  private static String usage() {
    return """
            Usage: ./jdu [OPTIONS] [FILE]
            Summarize disk usage of a file, recursively for directories.
                          
            Possible options:
              --depth n   Max recursion depth (8 by default).
              -L          Show symlinks.
              --limit n   Show n heaviest files/directories in every directory.""";
  }
}
