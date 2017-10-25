package cd.go.plugin.config.yaml;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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
        tempDir.newFolder("1", "a");
        tempDir.newFolder("2", "d");
        tempDir.newFolder("3");
        tempDir.newFolder("4");

        tempDir.newFile("1/a/abc.xml");
        tempDir.newFile("2/d/def.xml");
        tempDir.newFile("3/ghi.xml");
        tempDir.newFile("4/jkl.txt");
        tempDir.newFile("mno.txt");
        tempDir.newFile("pqr.xml");

        String[] xmlFilesOnly = scanner.getFilesMatchingPattern(tempDir.getRoot(), "**/*.xml");

        assertThat(xmlFilesOnly.length, is(4));
        assertThat(asList(xmlFilesOnly), hasItems("1/a/abc.xml", "2/d/def.xml", "3/ghi.xml", "pqr.xml"));
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
        tempDir.newFolder("1", "a");
        tempDir.newFolder("2", "d");
        tempDir.newFolder("3");
        tempDir.newFolder("4");

        tempDir.newFile("1/a/abc.xml");
        tempDir.newFile("2/d/def.gif");
        tempDir.newFile("3/ghi.xml");
        tempDir.newFile("4/jkl.txt");
        tempDir.newFile("mno.jpg");
        tempDir.newFile("4/pqr.jpg");
        tempDir.newFile("stu.xml");

        String[] xmlFilesOnly = scanner.getFilesMatchingPattern(tempDir.getRoot(), "**/*.xml, **/*.gif, *.jpg");

        assertThat(xmlFilesOnly.length, is(5));
        assertThat(asList(xmlFilesOnly), hasItems("1/a/abc.xml", "2/d/def.gif", "3/ghi.xml", "mno.jpg", "stu.xml"));
    }
}