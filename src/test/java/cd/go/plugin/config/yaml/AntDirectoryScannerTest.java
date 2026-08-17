package cd.go.plugin.config.yaml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AntDirectoryScannerTest {
    @TempDir
    public Path tempDir;

    private AntDirectoryScanner scanner;

    @BeforeEach
    public void setUp() {
        scanner = new AntDirectoryScanner();
    }

    @Test
    public void shouldMatchSimplePattern() throws Exception {
        Files.createFile(tempDir.resolve("abc.xml"));
        Files.createFile(tempDir.resolve("def.xml"));
        Files.createFile(tempDir.resolve("ghi.txt"));

        String[] xmlFilesOnly = scanner.getFilesMatchingPattern(tempDir.toFile(), "*.xml");

        assertThat(xmlFilesOnly.length, is(2));
        assertThat(asList(xmlFilesOnly), hasItems("abc.xml", "def.xml"));
    }

    @Test
    public void shouldMatchPatternInDirectory() throws Exception {
        Files.createDirectories(tempDir.resolve("1").resolve("a"));
        Files.createDirectories(tempDir.resolve("2").resolve("d"));
        Files.createDirectories(tempDir.resolve("3"));
        Files.createDirectories(tempDir.resolve("4"));

        Files.createFile(tempDir.resolve("1/a/abc.xml"));
        Files.createFile(tempDir.resolve("2/d/def.xml"));
        Files.createFile(tempDir.resolve("3/ghi.xml"));
        Files.createFile(tempDir.resolve("4/jkl.txt"));
        Files.createFile(tempDir.resolve("mno.txt"));
        Files.createFile(tempDir.resolve("pqr.xml"));

        String[] xmlFilesOnly = scanner.getFilesMatchingPattern(tempDir.toFile(), "**/*.xml");

        assertThat(xmlFilesOnly.length, is(4));
        assertThat(asList(xmlFilesOnly), hasItems("1/a/abc.xml", "2/d/def.xml", "3/ghi.xml", "pqr.xml"));
    }

    @Test
    public void shouldIgnoreSpacesAroundThePattern() throws Exception {
        Files.createFile(tempDir.resolve("def.xml"));
        Files.createFile(tempDir.resolve("ghi.txt"));

        String[] xmlFilesOnly = scanner.getFilesMatchingPattern(tempDir.toFile(), " *.xml ");

        assertThat(xmlFilesOnly.length, is(1));
        assertThat(asList(xmlFilesOnly), hasItems("def.xml"));
    }

    @Test
    public void shouldAcceptMultiplePatternsSeparatedByComma() throws Exception {
        Files.createDirectories(tempDir.resolve("1").resolve("a"));
        Files.createDirectories(tempDir.resolve("2").resolve("d"));
        Files.createDirectories(tempDir.resolve("3"));
        Files.createDirectories(tempDir.resolve("4"));


        Files.createFile(tempDir.resolve("1/a/abc.xml"));
        Files.createFile(tempDir.resolve("2/d/def.gif"));
        Files.createFile(tempDir.resolve("3/ghi.xml"));
        Files.createFile(tempDir.resolve("4/jkl.txt"));
        Files.createFile(tempDir.resolve("mno.jpg"));
        Files.createFile(tempDir.resolve("4/pqr.jpg"));
        Files.createFile(tempDir.resolve("stu.xml"));

        String[] xmlFilesOnly = scanner.getFilesMatchingPattern(tempDir.toFile(), "**/*.xml, **/*.gif, *.jpg");

        assertThat(xmlFilesOnly.length, is(5));
        assertThat(asList(xmlFilesOnly), hasItems("1/a/abc.xml", "2/d/def.gif", "3/ghi.xml", "mno.jpg", "stu.xml"));
    }

    @Test
    public void shouldNotScanVersionControlMetadataDirectories() throws Exception {
        Files.createDirectories(tempDir.resolve(".git"));
        Files.createDirectories(tempDir.resolve(".svn"));
        Files.createFile(tempDir.resolve(".git/config.xml"));
        Files.createFile(tempDir.resolve(".svn/entries.xml"));
        Files.createFile(tempDir.resolve("real.xml"));

        String[] matches = scanner.getFilesMatchingPattern(tempDir.toFile(), "**/*.xml");

        assertThat(matches.length, is(1));
        assertThat(asList(matches), hasItems("real.xml"));
    }

    @Test
    public void shouldFollowSymlinkedDirectoriesResolvingInsideBaseDirectory() throws Exception {
        Files.createDirectories(tempDir.resolve("real"));
        Files.createFile(tempDir.resolve("real/inside.xml"));
        Files.createFile(tempDir.resolve("top.xml"));
        Files.createSymbolicLink(tempDir.resolve("linkdir"), tempDir.resolve("real"));

        String[] matches = scanner.getFilesMatchingPattern(tempDir.toFile(), "**/*.xml");

        assertThat(matches.length, is(3));
        assertThat(asList(matches), hasItems("real/inside.xml", "linkdir/inside.xml", "top.xml"));
    }

    @Test
    public void shouldNotIncludeFilesUnderSymlinkedDirectoriesResolvingOutsideBaseDirectory(@TempDir Path outsideDir) throws Exception {
        Files.createFile(outsideDir.resolve("secret.xml"));
        Files.createFile(tempDir.resolve("real.xml"));
        Files.createSymbolicLink(tempDir.resolve("escapedir"), outsideDir);

        String[] matches = scanner.getFilesMatchingPattern(tempDir.toFile(), "**/*.xml");

        assertThat(matches.length, is(1));
        assertThat(asList(matches), hasItems("real.xml"));
    }

    @Test
    public void shouldIncludeSymlinkedFilesResolvingInsideBaseDirectory() throws Exception {
        Files.createFile(tempDir.resolve("real.xml"));
        Files.createSymbolicLink(tempDir.resolve("link.xml"), tempDir.resolve("real.xml"));

        String[] matches = scanner.getFilesMatchingPattern(tempDir.toFile(), "*.xml");

        assertThat(matches.length, is(2));
        assertThat(asList(matches), hasItems("real.xml", "link.xml"));
    }

    @Test
    public void shouldNotIncludeSymlinkedFilesResolvingOutsideBaseDirectory(@TempDir Path outsideDir) throws Exception {
        Files.createFile(outsideDir.resolve("secret.xml"));
        Files.createFile(tempDir.resolve("real.xml"));
        Files.createSymbolicLink(tempDir.resolve("escape.xml"), outsideDir.resolve("secret.xml"));
        Files.createSymbolicLink(tempDir.resolve("broken.xml"), tempDir.resolve("does-not-exist.xml"));

        String[] matches = scanner.getFilesMatchingPattern(tempDir.toFile(), "*.xml");

        assertThat(matches.length, is(1));
        assertThat(asList(matches), hasItems("real.xml"));
    }
}