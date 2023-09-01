package cd.go.plugin.config.yaml.cli;

import cd.go.plugin.config.yaml.JsonConfigCollection;
import cd.go.plugin.config.yaml.YamlConfigParser;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.gson.JsonObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class YamlPluginCli {
    public static void main(String[] args) {
        RootCmd root = new RootCmd();
        SyntaxCmd syntax = new SyntaxCmd();

        JCommander cmd = JCommander.newBuilder().
                programName("yaml-cli").
                addObject(root).
                addCommand("syntax", syntax).
                build();

        try {
            cmd.parse(args);

            if (root.help) {
                printUsageAndExit(0, cmd, cmd.getParsedCommand());
            }

            if (syntax.help) {
                printUsageAndExit(0, cmd, cmd.getParsedCommand());
            }

            if (null == syntax.file) {
                printUsageAndExit(1, cmd, cmd.getParsedCommand());
            }
        } catch (ParameterException e) {
            error(e.getMessage());
            printUsageAndExit(1, cmd, cmd.getParsedCommand());
        }

        YamlConfigParser parser = new YamlConfigParser();
        JsonConfigCollection collection = new JsonConfigCollection();
        parser.parseStream(collection, getFileAsStream(syntax.file), getLocation(syntax.file));

        if (collection.getErrors().size() > 0) {
            JsonObject result = collection.getJsonObject();
            result.remove("environments");
            result.remove("pipelines");
            result.addProperty("valid", false);
            die(1, result.toString());
        } else {
            die(0, "{\"valid\":true}");
        }
    }

    private static String getLocation(String file) {
        return "-".equals(file) ? "<STDIN>" : file;
    }

    private static InputStream getFileAsStream(String file) {
        InputStream s = null;
        try {
            s = "-".equals(file) ? System.in : new FileInputStream(file);
        } catch (FileNotFoundException e) {
            die(1, e.getMessage());
        }
        return s;
    }

    private static void echo(String message, Object... items) {
        System.out.printf(message + "%n", items);
    }

    private static void error(String message, Object... items) {
        System.err.printf(message + "%n", items);
    }

    private static void die(int exitCode, String message, Object... items) {
        if (exitCode != 0) {
            error(message, items);
        } else {
            echo(message, items);
        }
        System.exit(exitCode);
    }

    private static void printUsageAndExit(int exitCode, JCommander cmd, String command) {
        StringBuilder out = new StringBuilder();
        if (null == command) {
            cmd.getUsageFormatter().usage(out);
        } else {
            cmd.getUsageFormatter().usage(command, out);
        }
        die(exitCode, out.toString());
    }
}
