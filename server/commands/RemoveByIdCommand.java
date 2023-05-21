package commands;

import exceptions.DatabaseHandlingException;
import exceptions.ManualDatabaseEditException;
import exceptions.NoSuchCommandException;
import exceptions.PermissionDeniedException;
import util.ResponseCode;
import util.ServerResponse;
import util.User;
import utility.CollectionManager;
import models.Ticket;
import utility.Console;
import utility.DatabaseCollectionHandler;

/**
 * Command 'remove_by_id'. Saves the collection to a file.
 */
public class RemoveByIdCommand extends AbstractCommand{
    CollectionManager collectionManager;
    DatabaseCollectionHandler databaseCollectionHandler;

    public RemoveByIdCommand(CollectionManager collectionManager, DatabaseCollectionHandler databaseCollectionHandler){
        super("remove_by_id","delete an item from the collection by its id", "");
        this.collectionManager = collectionManager;
        this.databaseCollectionHandler = databaseCollectionHandler;
    }

    /**
     * Execute of 'remove_by_id' command.
     */


    @Override
    public ServerResponse execute(String argument, Object object, User user) {
        if (argument.isEmpty() || object != null) {
            throw new NoSuchCommandException();
        }
        try {
            if (collectionManager.collectionSize() == 0) {
                Console.println("Cannot remove object");
            }
            int id = Integer.parseInt(argument);
            Ticket ticketToRemove = collectionManager.getById(id);

            if (ticketToRemove == null) {
                Console.println("Cannot remove object");
            }
            if (!ticketToRemove.getUser().equals(user)) throw new PermissionDeniedException();
            if (!databaseCollectionHandler.checkTicketUserId(ticketToRemove.getId(), user)) throw new ManualDatabaseEditException();

            databaseCollectionHandler.deleteTicketById(id);
            collectionManager.removeFromCollection(ticketToRemove);
            Console.println("Ticket is deleted");
        } catch (NumberFormatException e) {
            Console.println("the argument must be a long number");
        } catch (DatabaseHandlingException | ManualDatabaseEditException | PermissionDeniedException e) {
            throw new RuntimeException(e);
        }
        return new ServerResponse("", ResponseCode.SUCCESS);
    }

}
