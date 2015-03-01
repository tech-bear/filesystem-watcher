package jordan.filesystemwatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;

public class Main {

    public static void main(String[] args) {
        // write your code here
        System.out.println("Hello world");

        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path p = Paths.get("C:\\");
            WatchKey key = p.register(
                    watcher,
                    ENTRY_CREATE,
                    ENTRY_MODIFY,
                    ENTRY_DELETE );

            while (true) {
                WatchKey k = null;
                try {
                    k = watcher.poll();
                    if (k == null) {
                        //Thread.sleep(10 * 1000);
                        continue;
                    }

                    for (WatchEvent<?> event : k.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        // This key is registered only
                        // for ENTRY_CREATE events,
                        // but an OVERFLOW event can
                        // occur regardless if events
                        // are lost or discarded.
                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }

                        // The filename is the
                        // context of the event.
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path filename = ev.context();

                        System.out.println(kind.name() + "\t: " + filename);
/*
                        // Verify that the new
                        //  file is a text file.
                        try {
                            // Resolve the filename against the directory.
                            // If the filename is "test" and the directory is "foo",
                            // the resolved name is "test/foo".
                            Path child = dir.resolve(filename);
                            if (!Files.probeContentType(child).equals("text/plain")) {
                                System.err.format("New file '%s'" +
                                        " is not a plain text file.%n", filename);
                                continue;
                            }
                        } catch (IOException x) {
                            System.err.println(x);
                            continue;
                        }

                        // Email the file to the
                        //  specified email alias.
                        System.out.format("Emailing file %s%n", filename);
                        //Details left to reader....
                    }
*/
                        // Reset the key -- this step is critical if you want to
                        // receive further watch events.  If the key is no longer valid,
                        // the directory is inaccessible so exit the loop.
                        boolean valid = k.reset();
                        if (!valid) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch(Exception e)  {
            e.printStackTrace();
        }
    }
}
