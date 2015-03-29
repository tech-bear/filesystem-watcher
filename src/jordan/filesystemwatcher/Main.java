package jordan.filesystemwatcher;

import java.io.File;
import java.nio.file.*;
import java.util.Collection;
import java.util.LinkedList;

import static java.nio.file.StandardWatchEventKinds.*;

public class Main {

    private static Collection<WatchService> watcherServices;

    public static void main(String[] args) {
        // write your code here
        System.out.println("Hello world");

        ConfigParser parser = new ConfigParser("src/jordan/filesystemwatcher/watcher-properties.xml");

        System.out.println("Config: " + parser.toString());

        watcherServices = new LinkedList<>();

        for (ConfigParser.WatcherOptions watcher : parser.getWatchers()) {
            try {
                // todo: implement recursive check here - if it's a recursive path, need to iterate down through

                WatchService watcherService = FileSystems.getDefault().newWatchService();
                Path p = Paths.get(new File(watcher.getDirectory()).getAbsolutePath());
                WatchKey key = p.register(
                        watcherService,
                        ENTRY_CREATE,
                        ENTRY_MODIFY,
                        ENTRY_DELETE);

                while (true) {
                    WatchKey k;
                    try {
                        k = watcherService.poll();
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


                            // todo: this is where we should filter by extention, then handle appropriately
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
                        watcherServices.add(watcherService);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
