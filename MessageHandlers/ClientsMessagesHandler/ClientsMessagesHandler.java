package MessageHandlers.ClientsMessagesHandler;

import Communication.Common.Messages.FindKeyMessage.FindKeyMessage;
import Communication.Common.Messages.GetExtremumMessage.ExtremumType;
import Communication.Common.Messages.GetExtremumMessage.GetExtremumMessage;
import Communication.Common.Messages.GetMessage.GetMessage;
import Communication.Common.Messages.SetMessage.SetMessage;
import Communication.Common.Messages.TerminateMessage.TerminateMessage;
import MessageHandlers.IClientsMessagesHandler;
import MessageHandlers.InternalMessagesHandler.InternalMessagesHandler;
import MessageHandlers.InternalMessagesHandler.OnRequestFinishListener;

public class ClientsMessagesHandler implements IClientsMessagesHandler {
    private InternalMessagesHandler internalMessagesHandler = null;
    private Integer port = null;
    private String nodeId = null;

    public ClientsMessagesHandler(InternalMessagesHandler internalMessagesHandler, Integer port, String nodeId) {
        this.internalMessagesHandler = internalMessagesHandler;
        this.port = port;
        this.nodeId = nodeId;
    }

    @Override
    public void handleMessage(String message, OnRequestFinishListener requestFinishListener) {
        // Must be synchornized handling only one thing
        System.out.println("Handling message " + message);

        // Hanling commands without argument
        if (message.equals("terminate")) {
            TerminateMessage msg = new TerminateMessage(nodeId, port);
            internalMessagesHandler.handleMessageFromClient(msg, requestFinishListener);
            return;
        }
        if (message.equals("get-min")) {
            GetExtremumMessage msg = new GetExtremumMessage(nodeId, port, ExtremumType.EXT_MIN);
            internalMessagesHandler.handleMessageFromClient(msg, requestFinishListener);
            return;
        }
        if (message.equals("get-max")) {
            GetExtremumMessage msg = new GetExtremumMessage(nodeId, port, ExtremumType.EXT_MAX);
            internalMessagesHandler.handleMessageFromClient(msg, requestFinishListener);
            return;
        }

        // Hanling commands with argument
        String operation = message;
        operation = operation.substring(0, operation.indexOf(" "));
        String arg = message.substring(message.indexOf(" ") + 1);

        if (operation.equals("get-value")) {
            GetMessage msg = new GetMessage(nodeId, port, operation, arg);
            internalMessagesHandler.handleMessageFromClient(msg, requestFinishListener);
            return;
        }
        if (operation.equals("set-value")) {
            SetMessage msg = new SetMessage(nodeId, port, operation, arg);
            internalMessagesHandler.handleMessageFromClient(msg, requestFinishListener);
            return;
        }
        if (operation.equals("find-key")) {
            FindKeyMessage msg = new FindKeyMessage(nodeId, port, operation, arg);
            internalMessagesHandler.handleMessageFromClient(msg, requestFinishListener);
            return;
        }
        if (operation.equals("new-record")) {
            internalMessagesHandler.updateDatabaseLocally(arg, requestFinishListener);
            return;
        }
    }
}