package Communication.Servers.UserServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Communication.Common.Messages.ResultResponse.ResultResponse;
import MessageHandlers.ClientsMessagesHandler.ClientsMessagesHandler;
import MessageHandlers.InternalMessagesHandler.OnRequestFinishListener;

public class ServerThread extends Thread {
    private Socket socket = null;
    private BufferedReader reader = null;
    private PrintWriter writer = null;
    private ClientsMessagesHandler clientsMessagesHandler;

    public ServerThread(ClientsMessagesHandler clientsMessagesHandler, Socket socket) {
        this.clientsMessagesHandler = clientsMessagesHandler;
        this.socket = socket;
        try {
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream(), true);
        }
        catch (IOException e) {

        }
    }

    public void run() {
        try {
            OnRequestFinishListener onRequestFinishListener = new OnRequestFinishListener() {
                private Socket socket = null;
                private PrintWriter socketWriter = null;
                
                @Override
                public void setSocketWriter(Socket socket, PrintWriter writer) {
                    this.socket = socket;
                    this.socketWriter = writer;
                }
                @Override
                public void finishRequest(ResultResponse response) {
                    // Waiting for response
                    if(response.getResult()) {
                        socketWriter.println(response.getResponse());
                    } else {
                        socketWriter.println("ERROR");
                    }
                    try {
                        this.socket.close();
                    }
                    catch (IOException e) {}
                }
            };
            onRequestFinishListener.setSocketWriter(this.socket, this.writer);
            clientsMessagesHandler.handleMessage(reader.readLine(), onRequestFinishListener);

        } catch (IOException e) {
            System.out.println("Caught exception is " + e);
        }
    }
}
