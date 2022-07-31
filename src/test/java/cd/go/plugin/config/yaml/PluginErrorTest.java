package cd.go.plugin.config.yaml;


import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PluginErrorTest {

    @Test
    public void shouldGetLocation() {
        PluginError pluginError = new PluginError(null, "location");
        assertThat(pluginError.getLocation(), is(equalTo("location")));
    }

    @Test
    public void shouldGetMessage() {
        PluginError pluginError = new PluginError("message", null);
        assertThat(pluginError.getMessage(), is(equalTo("message")));
    }
}