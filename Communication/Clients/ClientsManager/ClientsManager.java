package Communication.Clients.ClientsManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import AgrumentsParser.models.Connection;
import Communication.Clients.NodeClient.NodeClient;
import Communication.Common.Messages.InformMessage.InformMessage;
import Communication.Common.Serializers.MessageSerializer;

public class ClientsManager {
    private MessageSerializer<InformMessage> informMessageSerializer = new MessageSerializer<InformMessage>();
    private List<NodeClient> nodeClients = new ArrayList<NodeClient>();
    private List<String> clientsIds = new ArrayList<>();
    List<Connection> connections = null;
    Integer port = null;
    String nodeId = null;

    public List<String> getClientsIds() {
        return clientsIds;
    }

    public ClientsManager(List<Connection> connections, Integer port, String id) {
        this.connections = connections;
        this.port = port;
        this.nodeId = id;
    }

    public void init() {
        for (Connection connection : connections) {
            try {
                byte[] informMessage = informMessageSerializer.serialize(new InformMessage(nodeId, port));
                NodeClient newNodeClient = new NodeClient(InetAddress.getByName(connection.getHost()), null, connection.getPort());
                newNodeClient.sendMessage(informMessage);
            }
            catch (UnknownHostException e) {
                System.out.println("Caught exception is " + e);
            }
        }
    }

    public void registerClient(NodeClient newNodeClient) {
        this.nodeClients.add(newNodeClient);
        this.clientsIds.add(newNodeClient.getNodeId());
    }

    public void removeClient(String nodeClient) {
        this.nodeClients.removeIf((n)->(n.getNodeId().equals(nodeClient)));
        this.clientsIds.remove(nodeClient);
    }

    public Boolean notifyAllClients(byte[] msg, String except) {
        Boolean oneNotifiedAtLeast = false;
        for (NodeClient nodeClient : nodeClients) {
            if (!nodeClient.getNodeId().equals(except)) {
                nodeClient.sendMessage(msg);
                oneNotifiedAtLeast = true;
            }
        }
        return oneNotifiedAtLeast;
    }
    

    public void notifyOneClient(NodeClient nodeClient, byte[] msg) {
        nodeClient.sendMessage(msg);
    }

    public List<NodeClient> getNodeClients() {
        return nodeClients;
    }

    public NodeClient getNodeClientById(String nodeId) {
        for (NodeClient nodeClient : nodeClients) {
            if (nodeClient != null && nodeClient.getNodeId().equals(nodeId)) {
                return nodeClient;
            }
        }
        return null;
    }
}
