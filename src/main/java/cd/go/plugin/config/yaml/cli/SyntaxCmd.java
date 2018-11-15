package cd.go.plugin.config.yaml.cli;

import com.beust.jcommander.Parameter;

class SyntaxCmd {
    @Parameter(names = {"--quiet", "-q"}, description = "Silence output; validation indicated only by exit status")
    boolean quiet;

    @Parameter(names = {"--help", "-h"}, help = true, description = "Print this help message")
    boolean help;

    @Parameter(description = "file", required = true)
    String file;
}
