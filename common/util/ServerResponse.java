package util;

import java.io.Serializable;

/**
 * Class for get response value.
 */
public class ServerResponse implements Serializable {
    private final String message;
    private final ResponseCode responseCode;

    public ServerResponse(String message, ResponseCode responseCode) {
        this.message = message;
        this.responseCode = responseCode;
    }


    public String getMessage() {
        return message;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    @Override
    public String toString() {
        return "ServerResponse{"
                + " message='" + message + '\''
                + ", executeCode=" + responseCode
                + '}';
    }
}
