package Communication.Clients.NodeClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import Communication.Clients.IClient.IClient;

public class NodeClient implements IClient {

    public static final String TAG = NodeClient.class.getSimpleName();
    private DatagramSocket sock = null;
    private InetAddress host = null;
    private String nodeId = null;
    private Integer port = null;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public NodeClient(InetAddress host, String nodeId, Integer port) {
        try {
            this.sock = new DatagramSocket();
        }
        catch (SocketException e) {
            System.out.println("Caught exception is " + e);
        }
        this.host = host;
        this.nodeId = nodeId;
        this.port = port;
    }

    @Override
    public void sendMessage(byte[] message) {
        try {
            DatagramPacket datagramPacket = new DatagramPacket(message , message.length , this.host , this.port);
            sock.send(datagramPacket);
        }
        catch(IOException e)
		{
			System.err.println("IOException " + e);
		}
    }
}
