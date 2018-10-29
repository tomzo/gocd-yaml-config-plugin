package cd.go.plugin.config.yaml;

public class Capabilities {
    private boolean supportsPipelineExport;

    public Capabilities() {
        this.supportsPipelineExport = true;
    }

    public boolean isSupportsPipelineExport() {
        return supportsPipelineExport;
    }

    public void setSupportsPipelineExport(boolean supportsPipelineExport) {
        this.supportsPipelineExport = supportsPipelineExport;
    }
}
