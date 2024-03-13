package Communication.Common.Messages.Message;

import java.io.Serializable;
import java.util.UUID;

import Communication.Common.Messages.MessageType;

public class Message implements Serializable {
    protected MessageType type = null;
    protected String senderId = null;
    protected Integer senderPort = null;
    protected String requestId = null;

    public Message(String senderId, Integer senderPort) {
        this.senderId = senderId;
        this.senderPort = senderPort;
        this.requestId = UUID.randomUUID().toString();
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Integer getSenderPort() {
        return senderPort;
    }

    public void setSenderPort(Integer senderPort) {
        this.senderPort = senderPort;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("[Message][Type: %s][SenderId: %s][SenderPort: %d][Request id: %s]", 
            this.type, 
            this.senderId,  
            this.senderPort,
            this.requestId
        );
    }
}
