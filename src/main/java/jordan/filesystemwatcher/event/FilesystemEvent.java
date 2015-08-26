package jordan.filesystemwatcher.event;

import jordan.filesystemwatcher.WatchInstance;
import org.joda.time.DateTime;

import java.nio.file.Path;

/**
 * Created by Jordan-admin on 8/24/2015.
 */
abstract public class FilesystemEvent {
    FilesystemEventType eventType;
    WatchInstance watcher;
    Path path;
    DateTime timestamp;

    protected FilesystemEvent(Path path, WatchInstance watcher, FilesystemEventType eventType, DateTime timestamp) {
        this.path    = path.toAbsolutePath();
        this.watcher = watcher;
        this.eventType = eventType;
        this.timestamp = timestamp;
    }

    final public FilesystemEventType getEventType() {
        return eventType;
    }

    final public WatchInstance getWatchInstance() {
        return watcher;
    }

    final public Path getPath() {
        return path;
    }

    final public DateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("FilesystemEvent: ");
        sb.append("filename = ").append(getPath());
        sb.append(" | ").append("timestamp = ").append(getTimestamp());
        sb.append(" | ").append("event type = ").append(getEventType());

        return sb.toString();
    }

    public static FilesystemEvent createFilesystemEvent(Path path, WatchInstance watchInstance) {
        FilesystemEvent evt = null;

        if(FilesystemEventType.CREATE.getXmlName().equalsIgnoreCase(watchInstance.getFilter().getType())) {
            evt = new FileCreatedEvent(path, watchInstance);
        }
        else if(FilesystemEventType.DELETE.getXmlName().equalsIgnoreCase(watchInstance.getFilter().getType())) {
            evt = new FileDeletedEvent(path, watchInstance);
        }
        else if(FilesystemEventType.UPDATE.getXmlName().equalsIgnoreCase(watchInstance.getFilter().getType())) {
            evt = new FileUpdatedEvent(path, watchInstance);
        }
        else {
            System.err.println("Unknown event type " + watchInstance.getFilter().getType());
        }

        return evt;
    }
}

class FileCreatedEvent extends FilesystemEvent {
    public FileCreatedEvent(Path path, WatchInstance watchInstance) {
        super(path, watchInstance, FilesystemEventType.CREATE, new DateTime());
    }
}

class FileDeletedEvent extends FilesystemEvent {
    public FileDeletedEvent(Path path, WatchInstance watchInstance) {
        super(path, watchInstance, FilesystemEventType.DELETE, new DateTime());
    }
}

class FileUpdatedEvent extends FilesystemEvent {
    public FileUpdatedEvent(Path path, WatchInstance watchInstance) {
        super(path, watchInstance, FilesystemEventType.UPDATE, new DateTime());
    }
}