package commands;

import exceptions.NoSuchCommandException;
import exceptions.WrongAmountOfElementsException;
import util.ResponseCode;
import util.ServerResponse;
import util.User;
import utility.CollectionManager;
import utility.Console;

import java.io.IOException;


/**
 * Command 'exit'. Saves the collection to a file.
 */
public class ExitCommand extends AbstractCommand{
    private CollectionManager collectionManager;


    public ExitCommand(CollectionManager collectionManager){
        super("exit", "end the program (without saving to a file)", "");
        this.collectionManager = collectionManager;

    }

    /**
     * Execute of 'exit' command.
     */
    @Override
    public ServerResponse execute(String argument, Object object, User user) throws NoSuchCommandException {
        try {
            if (!argument.isEmpty() || object != null) throw new WrongAmountOfElementsException();
            collectionManager.exit();
        } catch (WrongAmountOfElementsException exception) {
            Console.println("Used: '" + getName() + "'");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ServerResponse("", ResponseCode.SERVER_EXIT);
    }


}
