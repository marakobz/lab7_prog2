package utility;

import commands.AbstractCommand;
import exceptions.NoSuchCommandException;
import util.ClientRequest;
import util.ResponseCode;
import util.ServerResponse;
import util.User;

import java.util.concurrent.locks.ReentrantLock;

public class HandleRequest extends ReentrantLock {
    private ServerResponse response;
    private CommandManager commandManager;

    public HandleRequest(CommandManager commandManager) {
        this.commandManager = commandManager;
    }


    protected ServerResponse compute(ClientRequest request) {
        User hashedUser = new User(
                request.getUser().getUsername(),
                PasswordHasher.hashPassword(request.getUser().getPassword())
        );
        executeCommand(request.getCommandName(), request.getCommandArguments(),
                request.getObjectArgument(), hashedUser);
        return new ServerResponse(" ", ResponseCode.SUCCESS);
    }

    /**
     * Executes a command from a request.
     *
     * @param command               Name of command.
     * @param commandStringArgument String argument for command.
     * @param commandObjectArgument Object argument for command.
     * @return Command execute status.
     */
    private synchronized ServerResponse executeCommand(String command, String commandStringArgument,
                                                       Object commandObjectArgument, User user) {
        if (commandManager.getCommands().containsKey(command)) {
            AbstractCommand abstractCommand = commandManager.getCommands().get(command);
            try {
                response = abstractCommand.execute(commandStringArgument, commandObjectArgument, user);
            } catch (NoSuchCommandException e) {

                response = new ServerResponse("Unknown command detected: " + command, ResponseCode.ERROR);

            }
        } else {
            response = new ServerResponse("Unknown command detected: " + command, ResponseCode.ERROR);

        }
        return response;
    }
}
