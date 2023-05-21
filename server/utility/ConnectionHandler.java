package utility;

import org.slf4j.Logger;
import util.ClientRequest;
import util.ResponseCode;
import util.ServerResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.*;

public class ConnectionHandler implements Runnable{
    private Logger logger;
    private ServerManager server;
    private Socket clientSocket;
    private Thread thread = new Thread();
    private HandleRequest handleRequest;

    public ConnectionHandler(ServerManager server, Socket clientSocket, HandleRequest handleRequest){
        this.server = server;
        this.clientSocket = clientSocket;
        this.handleRequest = handleRequest;
    }
    /**
     * Main handling cycle.
     */
    @Override
    public void run() {
        ClientRequest userRequest = null;
        boolean stopFlag = false;
        try (ObjectInputStream clientReader = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream clientWriter = new ObjectOutputStream(clientSocket.getOutputStream())) {
            do {
                try {
                    userRequest = (ClientRequest) clientReader.readObject();
                    Requester requester = new Requester(userRequest, handleRequest);
                    Runnable task = () -> {
                        requester.handleRequest(clientWriter);
                        ServerResponse response = requester.getResponseToUser();

                    };
                    new Thread(task).start();

                } catch (IOException | ClassNotFoundException e) {
                    Console.println(e.getMessage());
                    throw new RuntimeException();
                }

                Console.println("Active threads:" + Thread.activeCount());
            } while (!stopFlag);
        } catch (CancellationException exception) {
            Console.println("A multithreading error occurred while processing the request");
            logger.warn("A multithreading error occurred while processing the request");
        } catch (IOException exception) {
            Console.printerror("Unexpected disconnection with client" + clientSocket.getInetAddress());
            logger.warn("Unexpected disconnection with client" + clientSocket.getInetAddress());
        }
    }

}
