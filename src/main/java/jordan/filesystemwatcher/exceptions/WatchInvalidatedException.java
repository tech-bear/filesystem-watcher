package jordan.filesystemwatcher.exceptions;

import jordan.filesystemwatcher.WatchInstance;

/**
 * Created by Jordan-admin on 9/5/2015.
 */
public class WatchInvalidatedException extends Exception {
    private WatchInstance watchInstance;

    public WatchInvalidatedException(WatchInstance watchInstance) {
        super();
        this.watchInstance = watchInstance;
    }

    public WatchInvalidatedException(WatchInstance watchInstance, String message) {
        super(message);
        this.watchInstance = watchInstance;
    }

    public WatchInstance getWatchInstance() {
        return watchInstance;
    }
}
