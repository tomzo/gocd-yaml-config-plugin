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

    @Expose
    @SerializedName("supports_list_config_files")
    private boolean supportsListConfigFiles;

    @Expose
    @SerializedName("supports_user_defined_properties")
    private boolean supportsUserDefinedProperties;

    public Capabilities() {
        this.supportsPipelineExport = true;
        this.supportsParseContent = true;
        this.supportsListConfigFiles = true;
        this.supportsUserDefinedProperties = false;
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

    public boolean isSupportsListConfigFiles() {
        return supportsListConfigFiles;
    }

    public void setSupportsListConfigFiles(boolean supportsListConfigFiles) {
        this.supportsListConfigFiles = supportsListConfigFiles;
    }

    public boolean isSupportsUserDefinedProperties() {
        return supportsUserDefinedProperties;
    }

    public void setSupportsUserDefinedProperties(boolean supportsUserDefinedProperties) {
        this.supportsUserDefinedProperties = supportsUserDefinedProperties;
    }
}
