package cd.go.plugin.config.yaml;

import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.request.DefaultGoPluginApiRequest;
import com.thoughtworks.go.plugin.api.request.GoApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class YamlConfigPluginTest {
    private YamlConfigPlugin plugin;
    private GoApplicationAccessor goAccessor;

    @Before
    public void SetUp() throws IOException {
        plugin = new YamlConfigPlugin();
        goAccessor = mock(GoApplicationAccessor.class);
        plugin.initializeGoApplicationAccessor(goAccessor);
        GoApiResponse settingsResponse = DefaultGoApiResponse.success("{}");
        when(goAccessor.submit(any(GoApiRequest.class))).thenReturn(settingsResponse);

        File emptyDir = new File("emptyDir");
        FileUtils.deleteDirectory(emptyDir);
        FileUtils.forceMkdir(emptyDir);
    }

    @Test
    public void shouldRespondSuccessToGetConfigurationRequest()  throws UnhandledRequestTypeException
    {
        DefaultGoPluginApiRequest getConfigRequest = new DefaultGoPluginApiRequest("configrepo","1.0","go.plugin-settings.get-configuration");

        GoPluginApiResponse response = plugin.handle(getConfigRequest);
        assertThat(response.responseCode(), is(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE));
    }
}