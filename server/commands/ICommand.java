package commands;

import util.ServerResponse;
import util.User;

/**
 * Interface for all commands.
 */
public interface ICommand {

    String getName();
    ServerResponse execute(String commandArguments, Object objectArgument, User user);

    String getUsage();
}