package ru.nsu.fit.akitov.jdu;

import static ru.nsu.fit.akitov.jdu.Arguments.ArgumentsBuilder;
import ru.nsu.fit.akitov.jdu.core.*;
import java.nio.file.*;

public class Main {
  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println(usage());
      return;
    }

    Arguments arguments;
    try {
      arguments = getArguments(args);
    } catch (JduException exception) {
      System.out.println("Error: " + exception.getMessage());
      return;
    } catch (Exception exception) {
      exception.printStackTrace();
      return;
    }

//    JduFile.setNMax(arguments.limit());
//    JduFile.setMaxDepth(arguments.depth());
//    if (arguments.showSymlinks()) {
//      JduFile.showSymlinks();
//    }

//      JduFile info = JduFile.of(arguments.fileName());
//      System.out.println(info);
    JduFile file = JduBuilder.build(arguments.fileName(), arguments);
    JduPrinter printer = new JduPrinter(System.out);
    file.print(printer);
  }

  private static String usage() {
    return """
              Usage: ./jdu [OPTIONS] [FILE]
              Summarize disk usage of a file, recursively for directories.
              
              Possible options:
                --depth n   Max recursion depth (8 by default).
                -L          Show symlinks.
                --limit n   Show n heaviest files/directories in every directory.
              """;
  }

  private static Arguments getArguments(String[] args) throws JduException {
    ArgumentsBuilder builder = new ArgumentsBuilder();
    for (int i = 0; i < args.length; i++) {
      switch (args[i]) {
        case "--depth" -> {
          try {
            builder.setDepth(Integer.parseInt(args[i + 1]));
            i++;
          } catch (NumberFormatException e) {
            throw new JduException("wrong depth parameter: an integer value expected");
          }
        }
        case "--limit" -> {
          try {
            builder.setLimit(Integer.parseInt(args[i + 1]));
            i++;
          } catch (NumberFormatException e) {
            throw new JduException("wrong limit parameter: an integer value expected");
          }
        }
        case "-L" -> builder.setSymlinksDisplay(true);
        default -> {
          if (i == args.length - 1) {
            Path path = Path.of(args[i]);
            if (!Files.exists(path)) {
              throw new JduException("no such file or directory");
            }
            builder.setFileName(path);
          } else {
            throw new JduException("no such parameter '" + args[i] + "'");
          }
        }
      }
    }
    return builder.build();
  }
}
