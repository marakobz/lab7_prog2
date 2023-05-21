package commands;

import exceptions.WrongAmountOfElementsException;
import util.ResponseCode;
import util.ServerResponse;
import util.User;
import utility.CollectionManager;
import utility.Console;

import java.time.LocalDate;

/**
 * Command 'info'. Saves the collection to a file.
 */
public class InfoCommand extends AbstractCommand {
    private LocalDate creationDate;
    private CollectionManager collectionManager;

    public InfoCommand(CollectionManager collectionManager) {
        super("info", "shows information about the commands", "");
        this.collectionManager = collectionManager;
        creationDate = LocalDate.now();
    }

    /**
     * Execute of 'info' command.
     */

    @Override
    public ServerResponse execute(String argument, Object object, User user) {
        try {
            if (!argument.isEmpty() || object != null) throw new WrongAmountOfElementsException();

            System.out.println(
                    "Info about collection:"
                            + "  Type: Priority Queue <Ticket>\n"
                            + "  Creation Date:" + creationDate + " \n"
                            + "  Number of elements:" + collectionManager.collectionSize()
            );

        } catch (WrongAmountOfElementsException exception) {
            Console.println("Used: '" + getName() + "'");
        }
        return new ServerResponse("", ResponseCode.SUCCESS);
    }

}

