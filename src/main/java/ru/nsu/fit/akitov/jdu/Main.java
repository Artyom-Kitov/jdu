package ru.nsu.fit.akitov.jdu;

import static ru.nsu.fit.akitov.jdu.Arguments.ArgumentsBuilder;

import ru.nsu.fit.akitov.jdu.model.JduBuilder;
import ru.nsu.fit.akitov.jdu.model.JduFile;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
  public static void main(String[] args) {
    Arguments arguments;
    try {
      arguments = getArguments(args);
    } catch (JduException e) {
      System.err.println("Error: " + e.getMessage());
      System.err.println(usage());
      return;
    } catch (Exception exception) {
      System.out.println("Something unknown happened, please try again or contact a developer");
      exception.printStackTrace();
      return;
    }

    try {
      JduFile file = JduBuilder.build(arguments);
      JduFormattedStream printer = new JduFormattedStream(System.out, arguments.depth(), arguments.limit());
      file.accept(printer);
    } catch (JduException e) {
      System.out.println("Error: " + e.getMessage());
    }
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
              throw new JduException("no such file or directory: " + path);
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
