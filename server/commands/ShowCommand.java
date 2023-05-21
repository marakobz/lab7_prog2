package commands;

import exceptions.NoSuchCommandException;
import util.ResponseCode;
import util.ServerResponse;
import util.User;
import utility.CollectionManager;
import utility.Console;

/**
 * This is command 'show'. Prints all elements of collection.
 */
/*
TODO сделать нормальное отображение
 */
public class ShowCommand extends AbstractCommand{
    private CollectionManager collectionManager;

    public ShowCommand(CollectionManager collectionManager) {
        super("show", "show all collection's elements", "");
        this.collectionManager = collectionManager;
    }

    /**
     * Execute of 'show' command.
     */
    @Override
    public ServerResponse execute(String argument, Object object, User user) {
        try {
            if (!argument.isEmpty() || object != null) throw new NoSuchCommandException();
            Console.println("\nAll elements of collection: \n");
            Console.println(collectionManager.toString());

        }catch (NoSuchCommandException ex){
            Console.println("Collection is empty\n");
        }
        return new ServerResponse("", ResponseCode.SUCCESS);
    }
}
