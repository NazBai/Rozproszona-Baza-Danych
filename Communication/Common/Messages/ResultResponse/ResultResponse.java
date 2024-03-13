package Communication.Common.Messages.ResultResponse;

import Communication.Common.Messages.MessageType;
import Communication.Common.Messages.Message.Message;

public class ResultResponse extends Message {
    private Boolean result = false;
    private String response = null;

    public ResultResponse(String senderId, Integer senderPort, MessageType type, Boolean result, String response) {
        super(senderId, senderPort);
        this.type = type;
        this.result = result;
        this.response = response;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return String.format("%s[Result response][Result: %s][Response: %s]",
            super.toString(),
            this.result,
            this.response
        );
    }
}