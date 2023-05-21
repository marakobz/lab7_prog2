package commands;

import exceptions.NoSuchCommandException;

import util.ResponseCode;
import util.ServerResponse;
import util.User;
import utility.Console;
import utility.UserIO;


/**
 * Command 'execute_script'. Saves the collection to a file.
 */
public class ExecuteScriptCommand extends AbstractCommand{
    private UserIO userIO;
    public ExecuteScriptCommand(UserIO userIO){
        super("execute_script", "read and execute the script from the specified file", "");
        this.userIO = userIO;
    }

    /**
     * Execute of 'execute_script' command.
     */

    @Override
    public ServerResponse execute(String argument, Object object, User user) throws NoSuchCommandException {
        if (argument.isEmpty() || object != null) {
            Console.println("Used: '" + getName() + "'");
            throw new NoSuchCommandException();
        }
        Console.println("Reading the given script ...");
        userIO.startReadScript(argument);

        return new ServerResponse("", ResponseCode.SUCCESS);
    }
}

