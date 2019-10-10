package jordan.filesystemwatcher.tests;

import jordan.filesystemwatcher.config.ConfigParser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by Jordan on 4/4/2015.
 */
public class ConfigParserTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testBadFile() throws Exception {
        exception.expect(Exception.class);
        ConfigParser parser = new ConfigParser("nil");
    }
}
