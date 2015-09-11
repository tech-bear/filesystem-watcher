package jordan.filesystemwatcher;

import jordan.filesystemwatcher.config.xml.Filter;
import jordan.filesystemwatcher.config.xml.Watch;
import jordan.filesystemwatcher.event.FilesystemEvent;
import jordan.filesystemwatcher.event.FilesystemEventType;
import jordan.filesystemwatcher.exceptions.WatchInvalidatedException;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;

/**
 * Created by Jordan-admin on 8/25/2015.
 */
public class WatchInstance implements Callable<Collection<FilesystemEvent>> {
    WatchService watchSvc;
    Watch watch;
    Path path;
    Filter filter;

    public WatchInstance(Watch watch, Path path, Filter filter) throws IOException {
        watchSvc = FileSystems.getDefault().newWatchService();
        this.watch = watch;
        this.path = path;
        this.filter = filter;

        for (FilesystemEventType type : FilesystemEventType.fromString(filter.getType())) {
            this.path.register(watchSvc, type.getEventKind());
        }
    }

    public WatchService getWatchSvc() {
        return watchSvc;
    }

    public Watch getWatch() {
        return watch;
    }

    public Filter getFilter() {
        return filter;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public Collection<FilesystemEvent> call() throws WatchInvalidatedException {
        Collection<FilesystemEvent> events = new LinkedList<>();

        WatchKey k;
        try {
            k = watchSvc.poll();
            if (k == null) {
                return events;
            }

            for (WatchEvent<?> event : k.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // This key is registered only
                // for ENTRY_CREATE events,
                // but an OVERFLOW event can
                // occur regardless if events
                // are lost or discarded.
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    System.out.println("OVERFLOW event triggered!");
                    continue;
                }

                // The filename is the
                // context of the event.
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;

                Path filename = Paths.get(getPath().toAbsolutePath().toString(), ev.context().toString());

                // don't bother processing directories
                if (filename.toFile().isFile()) {
                    //System.out.println(kind.name() + "\t(" + ev.count() + ")\t: " + filename + " | filter: " + filter.getExtension());

                    // if using the wildcard, all files / folders will be caught
                    boolean triggerEvent = "*".equalsIgnoreCase(filter.getExtension());

                    if (!triggerEvent) {
                        triggerEvent = ev.context().toString().endsWith("." + filter.getExtension());
                    }

                    if (triggerEvent) {
                        events.add(FilesystemEvent.createFilesystemEvent(filename, this));
                    }
                }
                boolean valid = k.reset();
                if (!valid) {
                    throw new WatchInvalidatedException(this, "Path " + getPath() + " was invalidated!");
                }
            }
        } catch (WatchInvalidatedException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }
}
