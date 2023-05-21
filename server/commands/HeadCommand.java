package commands;

import exceptions.NoSuchCommandException;
import util.ResponseCode;
import util.ServerResponse;
import util.User;
import utility.CollectionManager;
import utility.Console;

/**
 * Command 'head'. Saves the collection to a file.
 */
public class HeadCommand extends AbstractCommand {
    CollectionManager collectionManager;

    public HeadCommand(CollectionManager collectionManager) {
        super("head", " show first element of collection", "");
        this.collectionManager = collectionManager;
    }

    /**
     * Execute of 'head' command.
     */

    @Override
    public ServerResponse execute(String argument, Object object, User user) throws NoSuchCommandException {
        if (!argument.isEmpty() || object != null) {
            throw new NoSuchCommandException();
        }
        if (collectionManager.getCollection().isEmpty()) {
            Console.println("Collection is empty");
        }else {
            Console.println(collectionManager.getFirst());
        }

        return new ServerResponse("", ResponseCode.SUCCESS);
    }
}
