package cd.go.plugin.config.yaml;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class AntDirectoryScannerTest {
    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private AntDirectoryScanner scanner;

    @Before
    public void setUp() throws Exception {
        scanner = new AntDirectoryScanner();
    }

    @Test
    public void shouldMatchSimplePattern() throws Exception {
        tempDir.newFile("abc.xml");
        tempDir.newFile("def.xml");
        tempDir.newFile("ghi.txt");

        String[] xmlFilesOnly = scanner.getFilesMatchingPattern(tempDir.getRoot(), "*.xml");

        assertThat(xmlFilesOnly.length, is(2));
        assertThat(asList(xmlFilesOnly), hasItems("abc.xml", "def.xml"));
    }

    @Test
    public void shouldMatchPatternInDirectory() throws Exception {
        List<String> expectedItems = Arrays.asList(
                "1/a/abc.xml",
                "2/d/def.xml",
                "3/ghi.xml",
                "pqr.xml"
        );

        tempDir.newFolder("1", "a");
        tempDir.newFolder("2", "d");
        tempDir.newFolder("3");
        tempDir.newFolder("4");

        expectedItems.forEach(item -> {
            try {
                tempDir.newFile(item);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        tempDir.newFile("4/jkl.txt");
        tempDir.newFile("mno.txt");

        List<String> xmlFilesOnly = Arrays.asList(scanner.getFilesMatchingPattern(tempDir.getRoot(), "**/*.xml"));

        assertThat(xmlFilesOnly.size(), is(4));
        assertThat(
                asList(xmlFilesOnly.stream().map(s -> TestUtils.normalizePath(s)).collect(Collectors.toList()))
                , hasItems(expectedItems.stream().map(s -> TestUtils.normalizePath(s)).collect(Collectors.toList())));
    }

    @Test
    public void shouldIgnoreSpacesAroundThePattern() throws Exception {
        tempDir.newFile("def.xml");
        tempDir.newFile("ghi.txt");

        String[] xmlFilesOnly = scanner.getFilesMatchingPattern(tempDir.getRoot(), " *.xml ");

        assertThat(xmlFilesOnly.length, is(1));
        assertThat(asList(xmlFilesOnly), hasItems("def.xml"));
    }

    @Test
    public void shouldAcceptMultiplePatternsSeparatedByComma() throws Exception {
        List<String> expectedItems = Arrays.asList(
                "1/a/abc.xml",
                "2/d/def.gif",
                "3/ghi.xml",
                "mno.jpg",
                "stu.xml"
        );

        tempDir.newFolder("1", "a");
        tempDir.newFolder("2", "d");
        tempDir.newFolder("3");
        tempDir.newFolder("4");

        expectedItems.forEach(item -> {
            try {
                tempDir.newFile(item);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        tempDir.newFile("4/jkl.txt");

        List<String> xmlFilesOnly = Arrays.asList(scanner.getFilesMatchingPattern(tempDir.getRoot(), "**/*.xml, **/*.gif, *.jpg"));

        assertThat(xmlFilesOnly.size(), is(5));
//        assertThat(asList(xmlFilesOnly), hasItems("1/a/abc.xml", "2/d/def.gif", "3/ghi.xml", "mno.jpg", "stu.xml"));
        assertThat(
                asList(xmlFilesOnly.stream().map(s -> TestUtils.normalizePath(s)).collect(Collectors.toList()))
                , hasItems(expectedItems.stream().map(s -> TestUtils.normalizePath(s)).collect(Collectors.toList())));
    }
}