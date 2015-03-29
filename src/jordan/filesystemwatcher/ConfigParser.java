package jordan.filesystemwatcher;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.Collection;
import java.util.LinkedList;

import jordan.filesystemwatcher.config.model.FilterOptions;
import jordan.filesystemwatcher.config.model.WatcherOptions;

/**
 * Created by Jordan on 2/28/2015.
 */
public class ConfigParser extends DefaultHandler {
    private String tempVal;

    private WatcherOptions tmpWatcher;
    private FilterOptions  tmpFilter;
    private String         tmpCommand;

    private Collection<WatcherOptions> watchers = null;

    public Collection<WatcherOptions> getWatchers() {
        return watchers;
    }

    public ConfigParser(String configFile) {
        if(!parse(configFile)) {
            System.err.println("ConfigParser: unable to parse config file " + configFile);
        }
    }

    protected boolean parse(String configFile) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            parser.parse(configFile, this);
        }
        catch(Exception e) {
            System.err.println("ConfigParser.parse: exception occured! " + e.getMessage());
            return false;
        }
        return true;
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        //reset
        tempVal = "";

        if(qName.equalsIgnoreCase("Watchers")) {
            watchers = new LinkedList<WatcherOptions>();
        }

        if(qName.equalsIgnoreCase("Watch")) {
            tmpWatcher = new WatcherOptions();
            tmpWatcher.setDirectory(attributes.getValue("directory"));
            tmpWatcher.setRecursive(Boolean.parseBoolean(attributes.getValue("recursive")));
        }

        if(qName.equalsIgnoreCase("Filter")) {
            tmpFilter = new FilterOptions();
            tmpFilter.setExtention(attributes.getValue("extention"));
        }
    }


    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch,start,length);
    }

    public void endElement(String uri, String localName,
                           String qName) throws SAXException {

        if(qName.equalsIgnoreCase("Watchers")) {
            // nothing to do here
        }
        else if(qName.equalsIgnoreCase("Watch")) {
            watchers.add(tmpWatcher);
        }
        else if(qName.equalsIgnoreCase("Filter")) {
            tmpWatcher.getFilters().add(tmpFilter);
        }
        else if(qName.equalsIgnoreCase("Command")) {
            tmpCommand = tempVal;
            tmpFilter.getCommands().add(tmpCommand);
        }
        else {
            System.out.println("WARN - tag '" + qName + "' not parsed - not implemented!");
        }
    }

    @Override
    public String toString() {
        if(watchers == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for(WatcherOptions w : watchers) {
            sb.append(w.toString());
        }
        return sb.toString();
    }
}
