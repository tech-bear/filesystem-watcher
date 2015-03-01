package jordan.filesystemwatcher;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Jordan on 2/28/2015.
 */
public class ConfigParser {
    class FilterOptions {
        private String extention;
        private Collection<String> commands;
    }

    class WatcherOptions {
        private String  directory;
        private boolean recursive;
        private Collection<FilterOptions> filters;
    }

    private Collection<WatcherOptions> watchers;

    public ConfigParser(String configFile) {
        if(!parse(configFile)) {
            System.err.println("ConfigParser: unable to parse config file " + configFile);
        }
    }

    protected boolean parse(String configFile) {
            watchers = new LinkedList<WatcherOptions>();
        try {

        }
        catch(Exception e) {
            System.err.println("ConfigParser.parse: exception occured! " + e.getMessage());
            return false;
        }
        return true;
    }
}
