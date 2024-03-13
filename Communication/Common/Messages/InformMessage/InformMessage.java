package Communication.Common.Messages.InformMessage;

import Communication.Common.Messages.MessageType;
import Communication.Common.Messages.Message.Message;

public class InformMessage extends Message {

    public InformMessage(String senderId, Integer senderPort) {
        super(senderId, senderPort);
        this.type = MessageType.INFORM_MESSAGE;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
