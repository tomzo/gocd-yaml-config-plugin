package cd.go.plugin.config.yaml;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class PluginSettingsTest {
    @Test
    public void shouldGetFilePattern() {
        PluginSettings pluginSettings = new PluginSettings("file-pattern");

        assertThat(pluginSettings.getFilePattern(), is(equalTo("file-pattern")));
    }

}