package jordan.filesystemwatcher.config;

import jordan.filesystemwatcher.config.xml.Watch;
import jordan.filesystemwatcher.config.xml.Watchers;

import java.util.List;

/**
 * Created by Jordan on 2/28/2015.
 */
public class ConfigParser extends GenericXMLParser<Watchers> {
    private List<Watch> watchers = null;

    public List<Watch> getWatchers() {
        return watchers;
    }

    public ConfigParser(String configFile) {
        super(configFile, Watchers.class);

        watchers = getParsedObject() != null ? getParsedObject().getWatch() : null;
    }

    @Override
    public String toString() {
        if (watchers == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (Watch w : watchers) {
            sb.append(w.toString());
        }
        return sb.toString();
    }
}