package Communication.Servers.NodeServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import MessageHandlers.InternalMessagesHandler.InternalMessagesHandler;

public class NodeServer extends Thread {
	private Integer port = null;
    private DatagramSocket sock = null;
	private InternalMessagesHandler internalMessagesHandler = null;

	public NodeServer(String nodeId, InternalMessagesHandler internalMessagesHandler, Integer port) {
		this.internalMessagesHandler = internalMessagesHandler;
		this.port = port;
		try {
            this.sock = new DatagramSocket(this.port);
        }
        catch (SocketException e) {
            System.out.println("Caught exception is " + e);
        }
	}

    public void run(){
		try
		{
			System.out.println("Node server socket created. Waiting for incomming data on port " + this.port);

			byte[] buffer = new byte[1024];
			DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);

			internalMessagesHandler.init();
			while(true) {
				sock.receive(incoming);
				byte[] msg = incoming.getData();
				InetAddress address = incoming.getAddress();
				internalMessagesHandler.handleMessageFromNode(address, msg);
			}
		}

		catch(IOException e)
		{
			System.err.println("IOException " + e);
		}
	}
}
