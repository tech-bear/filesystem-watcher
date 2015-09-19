package jordan.filesystemwatcher

import org.joda.time.DateTime

/**
 * Created by Jordan-admin on 9/19/2015.
 */
class CommandFilter {
    // absolute file URI
    final String file

    // String representation of time command is executed
    final String time

    // Millisecond Unix Timestamp of when command is executed
    final Long utime

    // File extension
    final String ext

    CommandFilter(CommandsBlock block) {
        DateTime timestamp = new DateTime()
        utime = timestamp.getMillis();
        time = timestamp.toString();

        file = block.getEvent().path.toString();
        ext  = block.getEvent().getWatchInstance().getFilter().getExtension();
    }

    String filter(String value) {
        return value
                .replace('$file', "$file")
                .replace('$time', "$time")
                .replace('$utime', "$utime")
                .replace('$ext', "$ext")
    }
}
