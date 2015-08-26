package jordan.filesystemwatcher.event;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

/**
 * Created by Jordan-admin on 8/24/2015.
 */
public enum FilesystemEventType {
    ALL("all", StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY),
    CREATE("create", StandardWatchEventKinds.ENTRY_CREATE),
    DELETE("delete", StandardWatchEventKinds.ENTRY_DELETE),
    UPDATE("update", StandardWatchEventKinds.ENTRY_MODIFY);

    WatchEvent.Kind<Path>[] eventKind;
    String xmlName;

    FilesystemEventType(String xmlName, WatchEvent.Kind<Path> ... eventKind) {
        this.eventKind = eventKind;
        this.xmlName = xmlName;
    }

    public WatchEvent.Kind<Path>[] getEventKind() {
        return eventKind;
    }

    public String getXmlName() {
        return xmlName;
    }

    public static FilesystemEventType[] fromString(String xmlName) {
        String[] xmlVals = xmlName.split(Pattern.quote("|"));
        return fromString(xmlVals);
    }

    public static FilesystemEventType[] fromString(String ... xmlVals) {
        Collection<FilesystemEventType> types = new LinkedHashSet<>();

        for(String val : xmlVals) {
            if("all".equalsIgnoreCase(val)) {
                types.add(ALL);
            }
            else if("create".equalsIgnoreCase(val)) {
                types.add(CREATE);
            }
            else if("delete".equalsIgnoreCase(val)) {
                types.add(DELETE);
            }
            else if("update".equalsIgnoreCase(val)) {
                types.add(UPDATE);
            }
            else {
                System.err.println("Unknown type '" + val + "'");
            }
        }

        return types.toArray(new FilesystemEventType[0]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("FileSystemEventType: xml-name=");
        sb.append(getXmlName());

        sb.append(" | ").append("EVENT_KIND(S): ");
        sb.append(StringUtils.join(getEventKind(), ","));

        return sb.toString();
    }
}
