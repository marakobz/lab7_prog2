package commands;

import exceptions.*;
import models.Ticket;
import util.ResponseCode;
import util.ServerResponse;
import util.User;
import utility.CollectionManager;
import utility.Console;
import utility.DatabaseCollectionHandler;

/*
TODO добавить недостающие исключения, всё перепроверить
 */
/**
 * Command 'clear'. Saves the collection to a file.
 */
public class ClearCommand extends AbstractCommand{
    private CollectionManager collectionManager;
    private DatabaseCollectionHandler databaseCollectionHandler;
    public ClearCommand(CollectionManager collectionManager, DatabaseCollectionHandler databaseCollectionHandler) {
     super("clear", "clear collection", "");
     this.collectionManager = collectionManager;
     this.databaseCollectionHandler = databaseCollectionHandler;
    }

    /**
     * Execute of 'clear' command.
     */

    @Override
    public ServerResponse execute(String argument, Object object, User user) throws NoSuchCommandException {
        try {
            if (!argument.isEmpty() || object!= null) throw new WrongAmountOfElementsException();
            for (Ticket ticket : collectionManager.getCollection()) {
                if (!ticket.getUser().equals(user)) throw new PermissionDeniedException();
                if (!databaseCollectionHandler.checkTicketUserId(ticket.getId(), user)) throw new ManualDatabaseEditException();
            }
            databaseCollectionHandler.clearCollection();
            collectionManager.clearCollection();
            Console.println("Collection is clear");
        } catch (WrongAmountOfElementsException exception) {
            Console.println("Used: '" + getName() + "'");
        } catch (PermissionDeniedException | DatabaseHandlingException | ManualDatabaseEditException e) {
            throw new RuntimeException(e);
        }
        return new ServerResponse("", ResponseCode.SUCCESS);
    }
}