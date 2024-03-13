import java.util.UUID;

import Communication.Clients.ClientsManager.ClientsManager;
import Communication.Servers.NodeServer.NodeServer;
import Communication.Servers.UserServer.UserServer;
import Database.Database;
import MessageHandlers.ClientsMessagesHandler.ClientsMessagesHandler;
import MessageHandlers.InternalMessagesHandler.InternalMessagesHandler;
import AgrumentsParser.ArgumentsObject;
import AgrumentsParser.ArgumentsParser;

class DatabaseNode {
    public static void main(final String[] args) {
        final ArgumentsObject obj = ArgumentsParser.parseArguments(args);

        String nodeId = UUID.randomUUID().toString();
        System.out.println("My id: " + nodeId);

        Database database = new Database(obj.getKey(), obj.getValue());
        ClientsManager clientsManager = new ClientsManager(obj.getConnections(), obj.getTcpPort(), nodeId);
        InternalMessagesHandler internalMessagesHandler = new InternalMessagesHandler(database, clientsManager, nodeId, obj.getTcpPort());
        ClientsMessagesHandler clientsMessagesHandler = new ClientsMessagesHandler(internalMessagesHandler, obj.getTcpPort(), nodeId);


        new NodeServer(nodeId, internalMessagesHandler, obj.getTcpPort()).start();
        new UserServer(clientsMessagesHandler, obj.getTcpPort()).start();
    }
}