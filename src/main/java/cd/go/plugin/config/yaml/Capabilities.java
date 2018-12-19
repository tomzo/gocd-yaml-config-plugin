package cd.go.plugin.config.yaml;

public class Capabilities {
    private boolean supportsPipelineExport;
    private boolean supportsParseContent;

    public Capabilities() {
        this.supportsPipelineExport = true;
        this.supportsParseContent = true;
    }

    public boolean isSupportsPipelineExport() {
        return supportsPipelineExport;
    }

    public void setSupportsPipelineExport(boolean supportsPipelineExport) {
        this.supportsPipelineExport = supportsPipelineExport;
    }

    public boolean isSupportsParseContent() {
        return supportsParseContent;
    }

    public void setSupportsParseContent(boolean supportsParseContent) {
        this.supportsParseContent = supportsParseContent;
    }
}
