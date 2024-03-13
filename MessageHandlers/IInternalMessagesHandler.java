package MessageHandlers;

import java.net.InetAddress;

import Communication.Common.Messages.Message.Message;
import MessageHandlers.InternalMessagesHandler.OnRequestFinishListener;

public interface IInternalMessagesHandler {
    public void init();
    public void handleMessageFromNode(InetAddress address, byte[] msg);
    public void handleMessageFromClient(Message message, OnRequestFinishListener requestFinishListener);
}
