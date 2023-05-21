package utility;

import exceptions.CommandUsageException;
import exceptions.IncorrectScriptException;
import exceptions.ScriptRecursionException;
import models.Coordinates;
import models.Person;
import models.TicketType;
import util.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

/**
 * Handle user's actions
 */
public class Handler {
    public static final String click = ">>";

    private final UserIO userIO;
    private Scanner userScanner;
    private final Stack<File> scriptStack = new Stack<>();
    private final Stack<Scanner> scannerStack = new Stack<>();

    public Handler(Scanner userScanner, UserIO userIO) {
        this.userScanner = userScanner;
        this.userIO = userIO;
    }
    /**
     * Generates ticket to add.
     *
     * @return Ticket to add.
     * @throws IncorrectScriptException When something went wrong in script.
     */
    private TicketRaw generateTicketAdd() throws IncorrectScriptException {
        OrganizationAsker organizationAsker = new OrganizationAsker(userIO);
        return new TicketRaw(
                organizationAsker.askName(),
                organizationAsker.askCoordinates(),
                organizationAsker.askPrice(),
                organizationAsker.askDiscount(),
                organizationAsker.askRefund(""),
                organizationAsker.askTicketType(),
                organizationAsker.askPerson()

        );
    }

    /**
     * Generates ticket to update.
     *
     * @return Ticket to update.
     * @throws IncorrectScriptException When something went wrong in script.
     */
    private TicketRaw generateTicketUpdate() throws IncorrectScriptException {
        OrganizationAsker organizationAsker = new OrganizationAsker(userIO);
        if (fileMode()) userIO.setFileMode();
        String name = organizationAsker.askQuestion("Do you want to change ticket's name?") ?
                organizationAsker.askName() : null;
        Coordinates coordinates = organizationAsker.askQuestion("Do you want to change coordinates?") ?
                organizationAsker.askCoordinates() : null;
        int price = organizationAsker.askQuestion("Do you want to change price of the ticket?") ?
                organizationAsker.askPrice() : -100;
        long discount = organizationAsker.askQuestion("Do you want to change discount of ticket?") ?
                organizationAsker.askDiscount() : -100;
        Boolean refund = organizationAsker.askQuestion("Do you want to change refund?") ?
                organizationAsker.askRefund("") : null;
        TicketType ticketType = organizationAsker.askQuestion("Do you want to change the type of a ticket?") ?
                organizationAsker.askTicketType() : null;
        Person person = organizationAsker.askQuestion("Do you want to change anything in person?")?
                organizationAsker.askPerson() : null;
        return new TicketRaw(
                name,
                coordinates,
                price,
                discount,
                refund,
                ticketType,
                person

        );
    }

    /**
     * Checks if Handler is in file mode now.
     *
     * @return Is Handler in file mode now boolean.
     */
    private boolean fileMode() {
        return !scannerStack.isEmpty();
    }

    /**
     * Handles client's request
     */
    public ClientRequest handle(ResponseCode serverResponseCode, User user) {
        String userInput;
        String[] userCommand;
        ProcessingCode processingCode;
        int rewriteAttempts = 0;
        try {
            do {
                try {
                    if (fileMode() && (serverResponseCode == ResponseCode.ERROR ||
                            serverResponseCode == ResponseCode.CLIENT_EXIT))
                        throw new IncorrectScriptException();
                    while (fileMode() && !userScanner.hasNextLine()) {
                        userScanner.close();
                        userScanner = scannerStack.pop();
                        Console.println("Going back to the script " + scriptStack.pop().getName());
                    }
                    if (fileMode()) {
                        userInput = userScanner.nextLine();
                        if (!userInput.isEmpty()) {
                            Console.print(click);
                            Console.println(userInput);
                        }
                    } else {
                        Console.print(click);
                        userInput = userScanner.nextLine();
                    }
                    userCommand = (userInput.trim() + " ").split(" ", 2);
                    userCommand[1] = userCommand[1].trim();
                } catch (NoSuchElementException | IllegalStateException exception) {
                    Console.println(click);
                    Console.printerror("Mistake occurred while entering the command");
                    userCommand = new String[]{"", ""};
                    rewriteAttempts++;
                    int maxRewriteAttempts = 1;
                    if (rewriteAttempts >= maxRewriteAttempts) {
                        Console.printerror("Too much attends happened");
                        System.exit(0);
                    }
                } catch (IncorrectScriptException e) {
                    throw new RuntimeException(e);
                }
                processingCode = processCommand(userCommand[0], userCommand[1]);
            } while (processingCode == ProcessingCode.ERROR && !fileMode() || userCommand[0].isEmpty());
            try {
                if (fileMode() && (serverResponseCode == ResponseCode.ERROR || processingCode == ProcessingCode.ERROR))
                    throw new IncorrectScriptException();
                switch (processingCode) {
                    case OBJECT:
                        TicketRaw ticketRaw = generateTicketAdd();
                        return new ClientRequest(userCommand[0], userCommand[1], ticketRaw, user);
                    case UPDATE_OBJECT:
                        TicketRaw ticketUpdateRaw = generateTicketUpdate();
                        return new ClientRequest(userCommand[0], userCommand[1], ticketUpdateRaw, user);
                    case SCRIPT:
                        File scriptFile = new File(userCommand[1]);
                        if (!scriptFile.exists()) throw new FileNotFoundException();
                        if (!scriptStack.isEmpty() && scriptStack.search(scriptFile) != -1)
                            throw new ScriptRecursionException();
                        scannerStack.push(userScanner);
                        scriptStack.push(scriptFile);
                        userScanner = new Scanner(scriptFile);
                        Console.println("Doing the script " + scriptFile.getName());
                        break;
                }
            } catch (FileNotFoundException exception) {
                Console.printerror("File with script is not found");
            } catch (ScriptRecursionException exception) {
                Console.printerror("Scripts cannot be in recursion mode");
                throw new IncorrectScriptException();
            }
        } catch (IncorrectScriptException exception) {
            Console.printerror("Reading the script was stopped");
            while (!scannerStack.isEmpty()) {
                userScanner.close();
                userScanner = scannerStack.pop();
            }
            scriptStack.clear();
            return new ClientRequest(user);
        }
        return new ClientRequest(userCommand[0], userCommand[1], user);

    }
    /**
     * Processes the entered command.
     *
     * @return Status of code.
     */
    private ProcessingCode processCommand(String command, String commandArgument) {
        try {
            switch (command) {
                case "":
                    return ProcessingCode.ERROR;
                case "help":
                    if (!commandArgument.isEmpty()) throw new CommandUsageException();
                    break;
                case "info":
                    if (!commandArgument.isEmpty()) throw new CommandUsageException();
                    break;
                case "show":
                    if (!commandArgument.isEmpty()) throw new CommandUsageException();
                    break;
                case "add":
                    if (!commandArgument.isEmpty()) throw new CommandUsageException("{element}");
                    return ProcessingCode.OBJECT;
                case "update":
                    if (commandArgument.isEmpty()) throw new CommandUsageException("<ID> {element}");
                    return ProcessingCode.UPDATE_OBJECT;
                case "remove_by_id":
                    if (commandArgument.isEmpty()) throw new CommandUsageException("<ID>");
                    break;
                case "clear":
                    if (!commandArgument.isEmpty()) throw new CommandUsageException();
                    break;
                case "execute_script":
                    if (commandArgument.isEmpty()) throw new CommandUsageException("<file_name>");
                    return ProcessingCode.SCRIPT;
                case "exit":
                    if (!commandArgument.isEmpty()) throw new CommandUsageException();
                    break;
                case "add_if_min":
                    if (!commandArgument.isEmpty()) throw new CommandUsageException("{element}");
                    return ProcessingCode.OBJECT;
                case "remove_greater":
                    if (!commandArgument.isEmpty()) throw new CommandUsageException("{element}");
                    return ProcessingCode.OBJECT;
                case "head":
                    if (!commandArgument.isEmpty()) throw new CommandUsageException() ;
                    break;
                case "average_of_discount":
                    if (!commandArgument.isEmpty()) throw new CommandUsageException();
                    break;
                case "group_counting":
                    if (!commandArgument.isEmpty()) throw new CommandUsageException();
                    break;
                case "print_unique_person":
                    if (!commandArgument.isEmpty()) throw new CommandUsageException();
                    break;
                default:
                    System.out.println("Command " + command + " is not found. Enter command 'help' for list of available commands.");
                    return ProcessingCode.ERROR;
            }
        } catch (CommandUsageException exception) {
            if (exception.getMessage() != null) command += " " + exception.getMessage();
            System.out.println("Using: '" + command + "'");
            return ProcessingCode.ERROR;
        }
        return ProcessingCode.OK;
    }
}
