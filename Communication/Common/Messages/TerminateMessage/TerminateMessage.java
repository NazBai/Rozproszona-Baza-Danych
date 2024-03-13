package Communication.Common.Messages.TerminateMessage;

import Communication.Common.Messages.MessageType;
import Communication.Common.Messages.Message.Message;

public class TerminateMessage extends Message {

    private Boolean active = true;

    public TerminateMessage(String senderId, Integer senderPort) {
        super(senderId, senderPort);
        this.type = MessageType.TERMINATE_MESSAGE;
    }

    public void deactivate() {
        active = false;
    }

    public Boolean getActive() {
        return active;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("[Active: %s]", active);
    }

}
