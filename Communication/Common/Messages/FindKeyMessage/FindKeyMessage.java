package Communication.Common.Messages.FindKeyMessage;

import Communication.Common.Messages.MessageType;
import Communication.Common.Messages.Message.Message;

public class FindKeyMessage extends Message {
    private String operation = null;
    private String arg = null;

    public String getOperation() {
        return operation;
    }

    public String getArg() {
        return arg;
    }

    public FindKeyMessage(String senderId, Integer senderPort, String operation, String arg) {
        super(senderId, senderPort);
        this.type = MessageType.FIND_KEY_MESSAGE;
        this.operation = operation;
        this.arg = arg;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("[Operation: %s][Arg: %s]", this.operation, this.arg);
    }

}
