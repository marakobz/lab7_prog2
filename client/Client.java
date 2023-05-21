import exceptions.NotDeclaredLimitsException;
import exceptions.WrongAmountOfElementsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.Console;
import utility.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Client application class. Creates all instances and runs the program.
 *
 * @author Кобзарь Мария P3115
 */

public class Client {

    private static final int RECONNECTION_TIMEOUT = 5 * 1000;
    private static final int MAX_RECONNECTION_ATTEMPTS = 5;

    private static String host;
    private static int port;

    private static Logger logger = LoggerFactory.getLogger(Client.class);
    private static boolean initialize(String[] hostAndPortArgs) {
        try {
            if (hostAndPortArgs.length != 2) throw new WrongAmountOfElementsException();
            host = hostAndPortArgs[0];
            port = Integer.parseInt(hostAndPortArgs[1]);
            if (port < 0) throw new NotDeclaredLimitsException();
            return true;
        } catch (WrongAmountOfElementsException exception) {
            String jarName = new java.io.File(Client.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath())
                    .getName();
            Console.println("Used: 'java -jar " + jarName + " <host> <port>'");
        } catch (NumberFormatException exception) {
            Console.printerror("Port has to be a number");
        } catch (NotDeclaredLimitsException exception) {
            Console.printerror("Port cannot be negative number");
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        if (!initialize(args)) return;

        var userIO = new UserIO(new Scanner(System.in), new PrintWriter(System.out));


        Scanner userScanner = new Scanner(System.in);

        Handler handler = new Handler(userScanner, userIO);
        AuthHandler authHandler = new AuthHandler(userScanner);
        ClientManager clientManager = new ClientManager(host, port, RECONNECTION_TIMEOUT, MAX_RECONNECTION_ATTEMPTS, handler, authHandler);
        clientManager.run();
        userScanner.close();
    }
}
