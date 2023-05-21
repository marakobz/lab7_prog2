package commands;

import exceptions.NoSuchCommandException;
import util.ResponseCode;
import util.ServerResponse;
import util.User;
import utility.CollectionManager;
import utility.Console;

/**
 * Command 'group_counting'. Saves the collection to a file.
 */
public class GroupCountingCommand extends AbstractCommand{
    CollectionManager collectionManager;

    public GroupCountingCommand(CollectionManager collectionManager){
        super("group_counting","group the elements of the collection by the value of the CreationDate field, output the number of elements in each group", "");
        this.collectionManager = collectionManager;
    }

    /**
     * Execute of 'group_counting' command.
     */

    @Override
    public ServerResponse execute(String argument, Object object, User user) throws NoSuchCommandException {
        try{
            if (!argument.isEmpty() || object != null) throw new NoSuchCommandException();
            collectionManager.groupCountingByCrDate();

        } catch (NoSuchCommandException e){
            Console.println("Used: '" + getName() + "'");

        }
        return new ServerResponse("", ResponseCode.SUCCESS);
    }

}
