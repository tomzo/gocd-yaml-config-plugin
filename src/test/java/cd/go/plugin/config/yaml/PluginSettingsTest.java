package cd.go.plugin.config.yaml;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class PluginSettingsTest {
    @Test
    public void shouldGetFilePattern() {
        PluginSettings pluginSettings = new PluginSettings("file-pattern", "generator-config-pattern");

        assertThat(pluginSettings.getFilePattern(), is(equalTo("file-pattern")));
        assertThat(pluginSettings.getGeneratorConfigPattern(), is(equalTo("generator-config-pattern")));
    }

}
