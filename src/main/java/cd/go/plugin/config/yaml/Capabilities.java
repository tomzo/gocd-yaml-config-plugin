package cd.go.plugin.config.yaml;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Capabilities {
    @Expose
    @SerializedName("supports_pipeline_export")
    private boolean supportsPipelineExport;
    @Expose
    @SerializedName("supports_parse_content")
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
