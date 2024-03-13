package MessageHandlers.InternalMessagesHandler;

import java.io.PrintWriter;
import java.net.Socket;

import Communication.Common.Messages.ResultResponse.ResultResponse;

public interface OnRequestFinishListener {
    public void finishRequest(ResultResponse response);
    public void setSocketWriter(Socket socket, PrintWriter writer);
}
