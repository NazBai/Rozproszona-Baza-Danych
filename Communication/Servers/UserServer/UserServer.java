package Communication.Servers.UserServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import MessageHandlers.ClientsMessagesHandler.ClientsMessagesHandler;

public class UserServer {
    private ServerSocket serverSocket = null;
    private ClientsMessagesHandler clientsMessagesHandler = null;

	public UserServer(ClientsMessagesHandler clientsMessagesHandler, Integer port) {
        this.clientsMessagesHandler = clientsMessagesHandler;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("User server is listening on port: " + port);
        }
        catch (IOException e) {
            System.out.println("Caught exception" + e);
        }
	}

    public void start() {
        if (serverSocket != null) {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("New client connected");
                    new ServerThread(clientsMessagesHandler, socket).start(); 
                }
                catch (IOException e) {
                    System.out.println("Caught exception is " + e);
                }
            }
        } else {
            System.out.println("Cannot start server");
        }
	}
}