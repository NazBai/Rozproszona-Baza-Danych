package Communication.Common.Messages.SetMessage;

import Communication.Common.Messages.MessageType;
import Communication.Common.Messages.Message.Message;

public class SetMessage extends Message {
    private String operation = null;
    private String arg = null;

    public String getOperation() {
        return operation;
    }

    public String getArg() {
        return arg;
    }

    public SetMessage(String senderId, Integer senderPort, String operation, String arg) {
        super(senderId, senderPort);
        this.type = MessageType.SET_MESSAGE;
        this.operation = operation;
        this.arg = arg;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("[Operation: %s][Arg: %s]", this.operation, this.arg);
    }

    public Integer getKeyToSet() {
        return Integer.parseInt(arg.substring(0, arg.indexOf(":")));
    }

    public Integer getValueToSet() {
        return Integer.parseInt(arg.substring(arg.indexOf(":") + 1));
    }
}
