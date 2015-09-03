package jordan.filesystemwatcher.event;

/**
 * Created by Jordan-admin on 9/2/2015.
 */
public interface FilesystemEventListener <FSEvent extends FilesystemEvent> {
    void handleEvent(FSEvent event);
}
