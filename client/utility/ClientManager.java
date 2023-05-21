package utility;

import exceptions.*;
import models.*;
import util.ClientRequest;
import util.ResponseCode;
import util.ServerResponse;
import util.User;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * manages all actions to run Client
 */
public class ClientManager {
    private String host;
    private int port;
    private int reconnectionTimeout;
    private int reconnectionAttempts;
    private int maxReconnectionAttempts;
    private Handler userHandler;
    private SocketChannel socketChannel;
    private ObjectOutputStream serverWriter;
    private ObjectInputStream serverReader;
    private AuthHandler authHandler;
    private User user;
    HandleRequest handleRequest;


    public ClientManager(String host, int port, int reconnectionTimeout, int maxReconnectionAttempts, Handler userHandler,
                  AuthHandler authHandler) {
        this.host = host;
        this.port = port;
        this.reconnectionTimeout = reconnectionTimeout;
        this.maxReconnectionAttempts = maxReconnectionAttempts;
        this.userHandler = userHandler;
        this.authHandler = authHandler;
    }

    /**
     * Begins client operation.
     */
    public void run() {
        try {
            while (true) {
                try {
                    connectToServer();
                    processAuthentication();
                    processRequestToServer();
                    break;
                } catch (ConnectionErrorException exception) {
                    if (reconnectionAttempts >= maxReconnectionAttempts) {
                        Console.printerror("Too much attempts of reconnection");
                        break;
                    }
                    try {
                        Thread.sleep(reconnectionTimeout);
                    } catch (IllegalArgumentException timeoutException) {
                        Console.printerror("Connection time out '" + reconnectionTimeout +
                                "' is beyond possible values");
                        Console.println("New reconnection will be started right now.");
                    } catch (Exception timeoutException) {
                        Console.printerror("Mistake occurred while trying to connect");
                        Console.println("New reconnection will be started right now.");
                    }
                }
                reconnectionAttempts++;
            }
            if (socketChannel != null) socketChannel.close();
            Console.println("Work of client is ended.");
        } catch (NotDeclaredLimitsException exception) {
            Console.printerror("Client cannot be started");
        } catch (IOException exception) {
            Console.printerror("Mistake occurred while trying to connect");
        }
    }

    /**
     * Connecting to server.
     */
    private void connectToServer() throws ConnectionErrorException, NotDeclaredLimitsException {
        try {
            if (reconnectionAttempts >= 1) Console.println("Reconnecting ...");
            socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
            Console.println("Connection with server is set successfully.");
            Console.println("Waiting for permission to exchange data ...");
            serverWriter = new ObjectOutputStream(socketChannel.socket().getOutputStream());
            serverReader = new ObjectInputStream(socketChannel.socket().getInputStream());
            Console.println("Permission to exchange data is received.");
        } catch (IllegalArgumentException exception) {
            Console.printerror("Incorrect address of server");
            throw new NotDeclaredLimitsException();
        } catch (IOException exception) {
            Console.printerror("Mistake occurred while trying to connect to server");
            throw new ConnectionErrorException();
        }
    }

    /**
     * Server request process.
     */
    private void processRequestToServer() {
        ClientRequest requestToServer = null;
        ServerResponse serverResponse = null;

        do {
            try {
                requestToServer = serverResponse != null ? userHandler.handle(serverResponse.getResponseCode(), user) :
                        userHandler.handle(null, user);
                if (requestToServer.isEmpty()) continue;
                serverWriter.writeObject(requestToServer);
                serverResponse = (ServerResponse) serverReader.readObject();

                //serverResponse = handleRequest.compute(requestToServer);
                Console.print(serverResponse.getMessage());
            } catch (InvalidClassException | NotSerializableException exception) {
                Console.printerror("Mistake occurred while sending data to the server");
            } catch (ClassNotFoundException exception) {
                Console.printerror("Mistake occurred while to read received data from server");
            } catch (IOException exception) {
                Console.printerror(exception.getMessage());
                Console.printerror("Connection with server is ended");
                try {
                    connectToServer();
                } catch (ConnectionErrorException | NotDeclaredLimitsException reconnectionException) {
                    if (requestToServer.getCommandName().equals("exit"))
                        Console.println("Command will not be registered on server.");
                    else Console.println("Try again later.");
                }
            }
        } while (!requestToServer.getCommandName().equals("exit"));
    }

    /**
     * Handle process authentication.
     */
    private void processAuthentication() {
        ClientRequest requestToServer = null;
        ServerResponse serverResponse = null;
        do {
            try {
                requestToServer = authHandler.handle();
                if (requestToServer.isEmpty()) continue;
                serverWriter.writeObject(requestToServer);
                serverResponse = (ServerResponse) serverReader.readObject();

                Console.print(serverResponse.getMessage());
            } catch (InvalidClassException | NotSerializableException exception) {
                Console.printerror("Mistake occurred while sending data to the server");
            } catch (ClassNotFoundException exception) {
                Console.printerror("Mistake occurred while to read received data from server");
            } catch (IOException exception) {
                Console.printerror("Connection with server is ended");
                try {
                    connectToServer();
                } catch (ConnectionErrorException | NotDeclaredLimitsException reconnectionException) {
                    Console.println("Tru to authorize again later.");
                }
            }
        } while (serverResponse == null || !serverResponse.getResponseCode().equals(ResponseCode.SUCCESS));
        user = requestToServer.getUser();
    }
}