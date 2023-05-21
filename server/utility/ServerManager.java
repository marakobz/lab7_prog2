package utility;

import exceptions.ClosingSocketException;
import exceptions.ConnectionErrorException;
import exceptions.OpeningServerSocketException;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/*
TODO разобраться с логированием
 */

/**
 * Runs the server.
 */
public class ServerManager {
    private int port;
    private ServerSocket serverSocket;
    private final HandleRequest handleRequest;
    private boolean isStopped;
    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);
    private final ReentrantLock reentrantLock;
    Logger logger;

    public ServerManager(int port, HandleRequest handleRequest) {
        this.port = port;
        this.reentrantLock = new ReentrantLock();
        this.handleRequest = handleRequest;
    }

    /**
     * Begins server operation.
     */
    public void run() {
        try {
            openServerSocket();
            while (!isStopped()) {
                try {
                    acquireConnection();
                    if (isStopped()) throw new ConnectionErrorException();
                    Socket clientSocket = connectToClient();
                    fixedThreadPool.submit(new ConnectionHandler(this, clientSocket, handleRequest));
                } catch (ConnectionErrorException exception) {
                    if (!isStopped()) {
                        Console.printerror("Mistake occurred while connecting to client");
                        logger.error("Mistake occurred while connecting to client");
                    } else break;
                }
            }
            fixedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            Console.println("Work of server is ended.");
        } catch (OpeningServerSocketException exception) {
            Console.printerror("Server cannot be started");
            logger.error("Server cannot be started");
        } catch (InterruptedException e) {
            Console.printerror("Mistake occurred while disconnecting with clients");
        }
    }

    /**
     * Acquire connection.
     */
    public void acquireConnection() {
        reentrantLock.tryLock();
        Console.println("Permission for a new connection has been received.");
    }

    /**
     * Release connection.
     */
    public void releaseConnection() {
        reentrantLock.lock();
        logger.info("A connection break has been registered.");
    }

    /**
     * Finishes server operation.
     */
    public synchronized void stop() {
        try {
            logger.info("Ending work of server...");
            if (serverSocket == null) throw new ClosingSocketException();
            isStopped = true;
            fixedThreadPool.shutdown();
            serverSocket.close();
            Console.println("Ending work with connected clients...");
            logger.info("Work of server is ended.");
        } catch (ClosingSocketException exception) {
            Console.printerror("Incapable to end the work of server which is not run");
            logger.error("Incapable to end the work of server which is not run");
        } catch (IOException exception) {
            Console.printerror("Mistake occurred while ending the work of server");
            Console.println("Ending work with connected clients...");
            logger.error("Mistake occurred while ending the server");
        }
    }

    /**
     * Checked stops of server.
     *
     * @return Status of server stop.
     */
    private synchronized boolean isStopped() {
        return isStopped;
    }

    /**
     * Open server socket.
     */
    private void openServerSocket() throws OpeningServerSocketException {
        try {
            Console.println("Running the server...");
            serverSocket = new ServerSocket(port);
            Console.println("Server is run.");
        } catch (IllegalArgumentException exception) {
            Console.printerror("Port '" + port + "' is beyond possible values");
            logger.error("Port '" + port + "' is beyond possible values");
            throw new OpeningServerSocketException();
        } catch (IOException exception) {
            Console.printerror("Mistake occurred while trying to use port '" + port + "'");
            logger.error("Mistake occurred while trying to use port '" + port + "'");
            throw new OpeningServerSocketException();
        }
    }

    /**
     * Connecting to client.
     */
    private Socket connectToClient() throws ConnectionErrorException {
        try {
            Console.println("Listening port '" + port + "'...");
            //logger.info("Прослушивание порта '" + port + "'...");
            Socket clientSocket = serverSocket.accept();
            Console.println("Connection with client is set.");
            //logger.info("Соединение с клиентом установлено.");
            return clientSocket;
        } catch (IOException exception) {
            throw new ConnectionErrorException();
        }
    }
}