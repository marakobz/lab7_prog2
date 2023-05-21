package utility;

import util.ClientRequest;
import util.ServerResponse;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class Requester {
    private ClientRequest userRequest;
    private ServerResponse responseToUser;
    private HandleRequest handleRequest;

    public Requester(ClientRequest userRequest, HandleRequest handleRequest) {
        this.userRequest = userRequest;
        this.handleRequest = handleRequest;
    }

    public void handleRequest(ObjectOutputStream clientWriter) {
        responseToUser = handleRequest.compute(userRequest);
        Console.println("Request '" + userRequest.getCommandName() + "' is completed.");
        try {
            clientWriter.writeObject(responseToUser);
            clientWriter.flush();
            Console.println("Response on request '" + userRequest.getCommandName() + "' is send.");
        } catch (IOException e) {
            Console.println("Mistake occurred while sending response on '" + userRequest.getCommandName() + "' request");
        }
    }

    public ServerResponse getResponseToUser() {
        return responseToUser;
    }
}
