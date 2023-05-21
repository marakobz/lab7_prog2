import exceptions.NotDeclaredLimitsException;
import exceptions.WrongAmountOfElementsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ClientRequest;
import util.User;
import utility.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;



public class Server {
    static Logger logger = LoggerFactory.getLogger(Server.class);

    public static int port = 1921;
    private static String databaseUsername = "s368310";
    private static String databaseHost;
    private static String databasePassword;
    private static String databaseAddress;

    public static void main(String[] args) throws IOException{
        logger.info("The server is running");
        if (!initialize(args)) return;

    var userIO = new UserIO(new Scanner(System.in), new PrintWriter(System.out));
    var organizationAsker = new OrganizationAsker(userIO);
    var databaseHandler = new DatabaseHandler(databaseAddress, databaseUsername, databasePassword);
    var databaseUserManager = new DatabaseUserManager(databaseHandler);
    var databaseCollectionHandler = new DatabaseCollectionHandler(databaseHandler, databaseUserManager);
    var collectionManager = new CollectionManager(databaseCollectionHandler);

    var commandManager = new CommandManager(collectionManager, userIO, organizationAsker, databaseCollectionHandler, databaseUserManager);

        HandleRequest handleRequest = new HandleRequest(commandManager);
        ServerManager serverManager = new ServerManager(port, handleRequest);
        serverManager.run();
    }
    private static boolean initialize(String[] args) {
        try {
            if (args.length != 3) throw new WrongAmountOfElementsException();
            port = Integer.parseInt(args[0]);
            if (port < 0) throw new NotDeclaredLimitsException();
            databaseHost = args[1];
            databasePassword = args[2];
            databaseAddress = "jdbc:postgresql://" + databaseHost + ":5432/studs";
            return true;
        } catch (WrongAmountOfElementsException exception) {
            String jarName = new java.io.File(Server.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath())
                    .getName();
            Console.println("Using: 'java -jar " + jarName + " <port> <db_host> <db_password>'");
        } catch (NumberFormatException exception) {
            Console.printerror("Port has to be a number");
            logger.error("Port has to be a number");
        } catch (NotDeclaredLimitsException exception) {
            Console.printerror("Port cannot be negative");
            logger.error("Port cannot be negative");
        }
        logger.error("Launch port initialization error");
        return false;
    }
}
