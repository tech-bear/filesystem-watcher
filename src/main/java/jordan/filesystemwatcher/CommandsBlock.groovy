package jordan.filesystemwatcher

import jordan.filesystemwatcher.event.FilesystemEvent

/**
 * Created by Jordan-admin on 9/19/2015.
 */
class CommandsBlock {
    final Long timeToExecute
    final List<String> commands
    final FilesystemEvent event

    public CommandsBlock(FilesystemEvent event) {
        this.event = event
        commands = event.getWatchInstance().getFilter().getCommand()
        timeToExecute = event.getTimestamp().getMillis() + event.getWatchInstance().getFilter().getTimerVal().longValue()
    }

    boolean equals(CommandsBlock block) {
        return event.getWatchInstance().equals(block.event.getWatchInstance())
    }

    public void executeCommands() {
        for(command in commands) {
            def cmd = new CommandFilter(this).filter(command)
            println("executing command: $cmd")
            try {
                Runtime.getRuntime().exec(cmd)
            }
            catch(Exception e) {
                e.printStackTrace()
            }
        }
    }
}
