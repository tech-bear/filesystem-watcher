package jordan.filesystemwatcher.event;

/**
 * Created by Jordan-admin on 9/2/2015.
 */
public interface FilesystemEventGenerator <FSEvent extends FilesystemEvent> {
    void addEventListener(FilesystemEventListener<FSEvent> listener);
    void removeEventListener(FilesystemEventListener<FSEvent> listener);
    int  countEventListeners();
    void broadcastEvent(FSEvent event);
}
