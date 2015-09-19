package jordan.filesystemwatcher;

import jordan.filesystemwatcher.event.FilesystemEvent;
import jordan.filesystemwatcher.event.FilesystemEventListener;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jordan-admin on 9/18/2015.
 */
public class CommandQueue <FSEventType extends FilesystemEvent> extends Thread implements FilesystemEventListener<FSEventType> {
    private Set<CommandsBlock> commandsToExecute;
    private ExecutorService threadGroup;

    public CommandQueue() {
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder()
                .namingPattern("CommandQueue-%d")
                .daemon(false)
                .build();
        threadGroup = Executors.newFixedThreadPool(4, threadFactory);

        commandsToExecute = new LinkedHashSet<>();
    }

    @Override
    public void run() {
        while(true) {
            commandsToExecute.forEach(commandsBlock -> {
                if (commandsBlock.getTimeToExecute() <= System.currentTimeMillis()) {
                    threadGroup.submit(commandsBlock::executeCommands);
                    commandsToExecute.remove(commandsBlock);
                }
            });
            try {
                Thread.sleep(50);
            }
            catch(Exception ignore) { }
        }
    }

    @Override
    public void handleEvent(FSEventType event) {
        commandsToExecute.add(new CommandsBlock(event));
    }
}
