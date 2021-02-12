package ds.communication.response;

import ds.communication.Message;

public class ErrorResponse extends Message {

    @Override
    public String getMessageAsString(String message) {
        return super.getMessageAsString(message);
    }

    @Override
    public String toString() {
        String response = "An error occurred";
        return response;
    }
}
