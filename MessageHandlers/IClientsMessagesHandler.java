package MessageHandlers;

import MessageHandlers.InternalMessagesHandler.OnRequestFinishListener;

public interface IClientsMessagesHandler {
    public void handleMessage(String message, OnRequestFinishListener requestFinishListener);
}
