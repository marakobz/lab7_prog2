package util;

import java.io.Serializable;

/**
 * Class for get request value.
 */
public class ClientRequest implements Serializable {
    private String commandName;
    private String commandArguments;
    private Serializable objectArgument;
    private User user;

    public ClientRequest(String commandName, String commandArguments, Serializable objectArgument, User user) {
        this.commandName = commandName;
        this.commandArguments = commandArguments;
        this.objectArgument = objectArgument;
        this.user = user;
    }
    public ClientRequest(String commandName, String commandStringArgument, User user) {
        this(commandName, commandStringArgument, null, user);
    }

    public ClientRequest(User user) {
        this("", "", user);
    }
    public String getCommandName() {
        return commandName;
    }

    public String getCommandArguments() {
        return commandArguments;
    }

    public Object getObjectArgument() {
        return objectArgument;
    }

    public User getUser(){
        return user;
    }
    public boolean isEmpty(){
        return commandName.isEmpty() && commandArguments.isEmpty() && objectArgument==null;
    }

    @Override
    public String toString() {
        return "ClientRequest{"
                + " commandName='" + commandName + '\''
                + ", commandArguments='" + commandArguments + '\''
                + ", objectArgument=" + objectArgument + user
                + '}';
    }
}
