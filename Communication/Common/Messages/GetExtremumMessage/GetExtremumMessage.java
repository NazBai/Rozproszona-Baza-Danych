package Communication.Common.Messages.GetExtremumMessage;

import Communication.Common.Messages.MessageType;
import Communication.Common.Messages.Message.Message;

public class GetExtremumMessage extends Message {
    private ExtremumType extremumType = null;

    public GetExtremumMessage(String senderId, Integer senderPort, ExtremumType extremumType) {
        super(senderId, senderPort);
        this.type = MessageType.GET_EXTREMUM_MESSAGE;
        this.extremumType = extremumType;
    }

    public ExtremumType getExtremumType() {
        return extremumType;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("[Extremum type: %s]", this.extremumType);
    }

}
