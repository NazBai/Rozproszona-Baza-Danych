package MessageHandlers.InternalMessagesHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import Communication.Clients.ClientsManager.ClientsManager;
import Communication.Clients.NodeClient.NodeClient;
import Communication.Common.Messages.MessageType;
import Communication.Common.Messages.FindKeyMessage.FindKeyMessage;
import Communication.Common.Messages.GetExtremumMessage.ExtremumType;
import Communication.Common.Messages.GetExtremumMessage.GetExtremumMessage;
import Communication.Common.Messages.GetMessage.GetMessage;
import Communication.Common.Messages.InformMessage.InformMessage;
import Communication.Common.Messages.Message.Message;
import Communication.Common.Messages.ResultResponse.ResultResponse;
import Communication.Common.Messages.SetMessage.SetMessage;
import Communication.Common.Messages.TerminateMessage.TerminateMessage;
import Communication.Common.Serializers.MessageSerializer;
import Database.Database;
import MessageHandlers.IInternalMessagesHandler;
import MessageHandlers.InternalMessagesHandler.RequestsHandler.GetExtremumRequest;
import MessageHandlers.InternalMessagesHandler.RequestsHandler.Request;


public class InternalMessagesHandler implements IInternalMessagesHandler {
    private Database database = null;
    private ClientsManager clientsManager = null;
    private String nodeId = null;
    private Integer port = null;
    private MessageSerializer<Message> serializer = null;
    private MessageSerializer<ResultResponse> resultResponseMessageSerializer = null;
    private MessageSerializer<InformMessage> informMessageSerializer = null;
    private MessageSerializer<GetMessage> getMessageSerializer = null;
    private MessageSerializer<SetMessage> setMessageSerializer = null;
    private MessageSerializer<FindKeyMessage> findKeyMessageSerializer = null;
    private MessageSerializer<GetExtremumMessage> getExtremumMessageSerializer = null;
    private MessageSerializer<TerminateMessage> terminateMessageSerializer = null;
    private RequestsHandler requestsHandler = null;

    public InternalMessagesHandler(Database database, ClientsManager clientsManager, String nodeId, Integer port) {
        this.database = database;
        this.clientsManager = clientsManager;
        this.nodeId = nodeId;
        this.port = port;
        this.serializer = new MessageSerializer<Message>();
        this.resultResponseMessageSerializer = new MessageSerializer<ResultResponse>();
        this.informMessageSerializer = new MessageSerializer<InformMessage>();
        this.getMessageSerializer = new MessageSerializer<GetMessage>();
        this.setMessageSerializer = new MessageSerializer<SetMessage>();
        this.findKeyMessageSerializer = new MessageSerializer<FindKeyMessage>();
        this.getExtremumMessageSerializer = new MessageSerializer<GetExtremumMessage>();
        this.terminateMessageSerializer = new MessageSerializer<TerminateMessage>();

        this.requestsHandler = new RequestsHandler();
    }

    @Override
    public synchronized void handleMessageFromNode(InetAddress address, byte[] msg) {
        Message receivedMsg = serializer.deserialize(msg);

        switch (receivedMsg.getType()) {
            case INFORM_MESSAGE:
                InformMessage receivedInformMsg = informMessageSerializer.deserialize(msg);
                System.out.println("Inform message: " + receivedInformMsg.toString());
                NodeClient newNode = new NodeClient(address, receivedInformMsg.getSenderId(), receivedInformMsg.getSenderPort());
                Boolean registered = false;
                if (!clientsManager.getClientsIds().contains(receivedInformMsg.getSenderId())) {
                    System.out.println("Handling inform message: " + receivedInformMsg.toString());
                    clientsManager.registerClient(newNode);
                    registered = true;
                }
                if (registered) {
                    InformMessage response = new InformMessage(nodeId, port);
                    response.setRequestId(receivedInformMsg.getRequestId());
                    newNode.sendMessage(informMessageSerializer.serialize(response));
                }
                break;
            case GET_MESSAGE:
            case SET_MESSAGE:
            case FIND_KEY_MESSAGE:
            case GET_EXTREMUM_MESSAGE:
            case TERMINATE_MESSAGE:
                handleOperation(receivedMsg, null);
                break;
            case GET_MESSAGE_RESPONSE:
            case SET_MESSAGE_RESPONSE:
            case FIND_KEY_MESSAGE_RESPONSE:
            case GET_EXTREMUM_MESSAGE_RESPONSE:
            case TERMINATE_MESSAGE_RESPONSE:
                handleResponse(receivedMsg);
                break;
            default:
                System.out.println("Message not handled");
                break;
        }
    }

    @Override
    public synchronized void handleMessageFromClient(Message message, OnRequestFinishListener requestFinishListener) {
        handleOperation(message, requestFinishListener);
    }

    private void handleOperation(Message message, OnRequestFinishListener requestFinishListener) {
        if (this.requestsHandler.getRequestById(message.getRequestId()) != null) {
            ResultResponse response = null;
            if (message.getType().equals(MessageType.GET_MESSAGE))  {
                response = new ResultResponse(nodeId, port, MessageType.GET_MESSAGE_RESPONSE, false, null);
            }
            if (message.getType().equals(MessageType.SET_MESSAGE))  {
                response = new ResultResponse(nodeId, port, MessageType.SET_MESSAGE_RESPONSE, false, null);
            }
            if (message.getType().equals(MessageType.FIND_KEY_MESSAGE))  {
                response = new ResultResponse(nodeId, port, MessageType.FIND_KEY_MESSAGE_RESPONSE, false, null);
            }
            if (message.getType().equals(MessageType.GET_EXTREMUM_MESSAGE))  {
                response = new ResultResponse(nodeId, port, MessageType.GET_EXTREMUM_MESSAGE_RESPONSE, false, null);
            }
            response.setRequestId(message.getRequestId());
            clientsManager.notifyOneClient(clientsManager.getNodeClientById(message.getSenderId()), 
                resultResponseMessageSerializer.serialize(response)
            );
            return;
        }
        if (message.getType().equals(MessageType.GET_EXTREMUM_MESSAGE))  {
            GetExtremumMessage getExtremumMessage = (GetExtremumMessage)message;
            this.requestsHandler.addRequest(new GetExtremumRequest(message.getSenderId(), message.getRequestId(), clientsManager.getClientsIds(), requestFinishListener, getExtremumMessage.getExtremumType()));
        } else {
            this.requestsHandler.addRequest(new Request(message.getSenderId(), message.getRequestId(), clientsManager.getClientsIds(), requestFinishListener));
        }
        
        System.out.println(String.format("Handling %s in node: %s", message.getType(), message.toString()));

        switch (message.getType()) {
            case GET_MESSAGE: {
                String senderId = message.getSenderId();
                Integer key = Integer.parseInt(((GetMessage)message).getArg());
                if (database != null && database.isKeyInDatabase(key)) {
                    Integer value = database.getValue();
                    OnRequestFinishListener onRequestFinishListener = this.requestsHandler.getFinishListenerById(message.getRequestId());
                    this.requestsHandler.removeRequest(message.getRequestId());
                    ResultResponse responseForClient = new ResultResponse(nodeId, port, MessageType.GET_MESSAGE_RESPONSE, true, String.format("%d:%d", key, value));
                    responseForClient.setRequestId(message.getRequestId());
                    if (onRequestFinishListener == null) {
                        // Send response
                        clientsManager.notifyOneClient(clientsManager.getNodeClientById(senderId), 
                            resultResponseMessageSerializer.serialize(responseForClient)
                        );
                    } else {
                        onRequestFinishListener.finishRequest(responseForClient);
                    }
                    break;
                }
                message.setSenderId(nodeId);
                message.setSenderPort(port);
                if(!clientsManager.notifyAllClients(this.getMessageSerializer.serialize((GetMessage)message), senderId)) {
                    OnRequestFinishListener onRequestFinishListener = this.requestsHandler.getFinishListenerById(message.getRequestId());
                    this.requestsHandler.removeRequest(message.getRequestId());
                    ResultResponse responseForClient = new ResultResponse(nodeId, port, MessageType.GET_MESSAGE_RESPONSE, false, null);
                    responseForClient.setRequestId(message.getRequestId());
                    if (onRequestFinishListener == null) {
                        // Send response
                        clientsManager.notifyOneClient(clientsManager.getNodeClientById(senderId), 
                            resultResponseMessageSerializer.serialize(responseForClient)
                        );
                    } else {
                        onRequestFinishListener.finishRequest(responseForClient);
                    }
                }
            }
            break;
            case SET_MESSAGE: {
                SetMessage setMessage = (SetMessage)message;
                String senderId = setMessage.getSenderId();
                Integer key = setMessage.getKeyToSet();
                if (database.isKeyInDatabase(key)) {
                    Integer value = setMessage.getValueToSet();
                    database.setValue(value);
                    OnRequestFinishListener onRequestFinishListener = this.requestsHandler.getFinishListenerById(message.getRequestId());
                    this.requestsHandler.removeRequest(message.getRequestId());
                    ResultResponse responseForClient = new ResultResponse(nodeId, port, MessageType.SET_MESSAGE_RESPONSE, true, "OK");
                    responseForClient.setRequestId(setMessage.getRequestId());
                    if (onRequestFinishListener == null) {
                        // Send response
                        clientsManager.notifyOneClient(clientsManager.getNodeClientById(senderId), 
                            resultResponseMessageSerializer.serialize(responseForClient)
                        );
                    } else {
                        onRequestFinishListener.finishRequest(responseForClient);
                    }
                    break;
                }
                setMessage.setSenderId(nodeId);
                setMessage.setSenderPort(port);
                if(!clientsManager.notifyAllClients(this.setMessageSerializer.serialize(setMessage), senderId)) {
                    OnRequestFinishListener onRequestFinishListener = this.requestsHandler.getFinishListenerById(message.getRequestId());
                    this.requestsHandler.removeRequest(message.getRequestId());
                    ResultResponse responseForClient = new ResultResponse(nodeId, port, MessageType.SET_MESSAGE_RESPONSE, false, null);
                    responseForClient.setRequestId(setMessage.getRequestId());
                    if (onRequestFinishListener == null) {
                        // Send response
                        clientsManager.notifyOneClient(clientsManager.getNodeClientById(senderId), 
                            resultResponseMessageSerializer.serialize(responseForClient)
                        );
                    } else {
                        onRequestFinishListener.finishRequest(responseForClient);
                    }
                }
            }
            break;
            case FIND_KEY_MESSAGE: {
                FindKeyMessage findKeyMessage = (FindKeyMessage)message;
                String senderId = findKeyMessage.getSenderId();
                if (database.isKeyInDatabase(Integer.parseInt(findKeyMessage.getArg()))) {
                    OnRequestFinishListener onRequestFinishListener = this.requestsHandler.getFinishListenerById(message.getRequestId());
                    this.requestsHandler.removeRequest(message.getRequestId());
                    ResultResponse responseForClient = null;
                    try {
                        responseForClient = new ResultResponse(nodeId, port, MessageType.FIND_KEY_MESSAGE_RESPONSE, true, String.format("%s,%s", InetAddress.getLocalHost().getHostAddress(), this.port));
                    }
                    catch(IOException e) {
                        responseForClient = new ResultResponse(nodeId, port, MessageType.FIND_KEY_MESSAGE_RESPONSE, true, String.format("%s,%s", "unknown", this.port));
                    }
                    responseForClient.setRequestId(findKeyMessage.getRequestId());
                    if (onRequestFinishListener == null) {
                        // Send response
                        clientsManager.notifyOneClient(clientsManager.getNodeClientById(senderId), 
                            resultResponseMessageSerializer.serialize(responseForClient)
                        );
                    } else {
                        onRequestFinishListener.finishRequest(responseForClient);
                    }
                    break;
                }
                findKeyMessage.setSenderId(nodeId);
                findKeyMessage.setSenderPort(port);
                if(!clientsManager.notifyAllClients(this.findKeyMessageSerializer.serialize(findKeyMessage), senderId)) {
                    OnRequestFinishListener onRequestFinishListener = this.requestsHandler.getFinishListenerById(message.getRequestId());
                    this.requestsHandler.removeRequest(message.getRequestId());
                    ResultResponse responseForClient = new ResultResponse(nodeId, port, MessageType.FIND_KEY_MESSAGE_RESPONSE, false, null);
                    responseForClient.setRequestId(findKeyMessage.getRequestId());
                    if (onRequestFinishListener == null) {
                        // Send response
                        clientsManager.notifyOneClient(clientsManager.getNodeClientById(senderId), 
                            resultResponseMessageSerializer.serialize(responseForClient)
                        );
                    } else {
                        onRequestFinishListener.finishRequest(responseForClient);
                    }
                }
                }
                break;

            case GET_EXTREMUM_MESSAGE: {
                GetExtremumMessage getExtremumMessage = (GetExtremumMessage)message;
                String senderId = getExtremumMessage.getSenderId();
                GetExtremumRequest request = (GetExtremumRequest)this.requestsHandler.getRequestById(message.getRequestId());
                NodeClient senderToInform = clientsManager.getNodeClientById(request.getSenderId());
                getExtremumMessage.setSenderId(nodeId);
                getExtremumMessage.setSenderPort(port);
                if(!clientsManager.notifyAllClients(this.getExtremumMessageSerializer.serialize(getExtremumMessage), senderId)) {
                    ResultResponse responseForClient = new ResultResponse(nodeId, port, MessageType.GET_EXTREMUM_MESSAGE_RESPONSE, true, String.format("%d:%d", database.getKey(), database.getValue()));
                    responseForClient.setRequestId(message.getRequestId());
                    if (senderToInform != null) {
                        senderToInform.sendMessage(resultResponseMessageSerializer.serialize(responseForClient));
                    } else {
                        OnRequestFinishListener onRequestFinishListener = this.requestsHandler.getFinishListenerById(message.getRequestId());
                        if (onRequestFinishListener != null) {
                            onRequestFinishListener.finishRequest(responseForClient);
                        }
                    }
                    this.requestsHandler.removeRequest(request.getRequestId());
                }
                }
                break;

            case TERMINATE_MESSAGE:
                TerminateMessage terminateMessage = (TerminateMessage)message;
                Request request = this.requestsHandler.getRequestById(message.getRequestId());
                if (terminateMessage.getActive()) {
                    terminateMessage.deactivate();
                    if(!this.clientsManager.notifyAllClients(terminateMessageSerializer.serialize(terminateMessage), message.getSenderId())) {
                        if (request != null && request.isHandled(nodeId.equals(request.getSenderId()))) {
                            OnRequestFinishListener onRequestFinishListener = this.requestsHandler.getFinishListenerById(message.getRequestId());
                            if (onRequestFinishListener != null) {
                                onRequestFinishListener.finishRequest(new ResultResponse(nodeId, port, MessageType.TERMINATE_MESSAGE_RESPONSE, true, "OK"));
                            }

                            this.requestsHandler.removeRequest(request.getRequestId());
                            System.exit(0);
                        }
                    }
                } else {
                    ResultResponse responseForClient = new ResultResponse(nodeId, port, MessageType.TERMINATE_MESSAGE_RESPONSE, true, "OK");
                    responseForClient.setRequestId(message.getRequestId());
                    System.out.println(message.getRequestId());
                    NodeClient senderToInform = clientsManager.getNodeClientById(request.getSenderId());
                    senderToInform.sendMessage(resultResponseMessageSerializer.serialize(responseForClient));
                    this.clientsManager.removeClient(message.getSenderId());
                    this.requestsHandler.removeRequest(request.getRequestId());
                }
                break;
    
            default:
                System.out.println("Message not handled");
                break;
        }
    }

    private void handleResponse(Message message) {
        if (requestsHandler.getRequestById(message.getRequestId()) == null) {
            return;
        }
        System.out.println(String.format("Handling %s in node: %s", message.getType(), message.toString()));
        ResultResponse response = (ResultResponse)message;
        switch (response.getType()) {
            case GET_MESSAGE_RESPONSE: 
            case SET_MESSAGE_RESPONSE: 
            case FIND_KEY_MESSAGE_RESPONSE: {
                if (response.getResult()) {
                    Request request = this.requestsHandler.getRequestById(response.getRequestId());
                    if (request != null) {
                        this.requestsHandler.removeRequest(response.getRequestId());
                        // CHecking if request handler has finish listener
                        if (nodeId.equals(request.getSenderId())) {
                            // Find finisher
                            OnRequestFinishListener onRequestFinishListener = request.getOnRequestFinishListener();
                            if (onRequestFinishListener != null) {
                                onRequestFinishListener.finishRequest(response);
                            }
                        } else {
                            // Send response
                            response.setSenderId(nodeId);
                            response.setSenderPort(port);
                            clientsManager.notifyOneClient(clientsManager.getNodeClientById(request.getSenderId()), 
                                resultResponseMessageSerializer.serialize(response)
                            );
                        }
                    }
                } else {
                    this.requestsHandler.handleRequest(response);
                    Request request = this.requestsHandler.getRequestById(response.getRequestId());
                    if (request != null && request.isHandled(nodeId.equals(request.getSenderId()))) {
                        // Request handled but none of the responses was successfull
                        NodeClient senderToInform = clientsManager.getNodeClientById(request.getSenderId());
                        ResultResponse responseForClient = new ResultResponse(nodeId, port, response.getType(), false, null);
                        responseForClient.setRequestId(message.getRequestId());
                        if (senderToInform != null) {
                            senderToInform.sendMessage(resultResponseMessageSerializer.serialize(responseForClient));
                            
                        } else {
                            OnRequestFinishListener onRequestFinishListener = this.requestsHandler.getFinishListenerById(message.getRequestId());
                            if (onRequestFinishListener != null) {
                                onRequestFinishListener.finishRequest(responseForClient);
                            }
                        }
                        this.requestsHandler.removeRequest(request.getRequestId());
                    }
                }
            }
            break;
            case GET_EXTREMUM_MESSAGE_RESPONSE: {
                this.requestsHandler.handleRequest(response);
                GetExtremumRequest request = (GetExtremumRequest)this.requestsHandler.getRequestById(response.getRequestId());
                if (request != null && request.isHandled(nodeId.equals(request.getSenderId()))) {
                    // Request handled but none of the responses was successfull
                    NodeClient senderToInform = clientsManager.getNodeClientById(request.getSenderId());

                    // Calcaulte extremum
                    Integer extremumValue = database.getValue();
                    Integer key = database.getKey();

                    for (ResultResponse result : request.getResponses()) {
                        if (result.getResult()) {
                            String[] args = result.getResponse().split(":");
                            Integer potentialNewExtremum = Integer.parseInt(args[1]);
                            
                            if (request.getType().equals(ExtremumType.EXT_MAX)) {
                                if (extremumValue == null || potentialNewExtremum > extremumValue) {
                                    extremumValue = potentialNewExtremum;
                                    key = Integer.parseInt(args[0]);
                                } 
                            } else {
                                if (extremumValue == null || potentialNewExtremum < extremumValue) {
                                    extremumValue = potentialNewExtremum;
                                    key = Integer.parseInt(args[0]);
                                }
                            }
                        }
                    }
                    ResultResponse responseForClient = new ResultResponse(nodeId, port, MessageType.GET_EXTREMUM_MESSAGE_RESPONSE, true, String.format("%d:%d", key, extremumValue));
                    responseForClient.setRequestId(message.getRequestId());
                    if (senderToInform != null) {
                        senderToInform.sendMessage(resultResponseMessageSerializer.serialize(responseForClient));
                    } else {
                        OnRequestFinishListener onRequestFinishListener = this.requestsHandler.getFinishListenerById(message.getRequestId());
                        if (onRequestFinishListener != null) {
                            onRequestFinishListener.finishRequest(responseForClient);
                        }
                    }
                    this.requestsHandler.removeRequest(request.getRequestId());
                }
            }
            break;
            case TERMINATE_MESSAGE_RESPONSE:
                this.requestsHandler.handleRequest(response);
                Request request = this.requestsHandler.getRequestById(response.getRequestId());
                if (request != null && request.isHandled(nodeId.equals(request.getSenderId()))) {
                    List<ResultResponse> responses = request.getResponses();
                    OnRequestFinishListener onRequestFinishListener = this.requestsHandler.getFinishListenerById(message.getRequestId());
                    for (ResultResponse resultResponse : responses) {
                        if (!resultResponse.getResult()) {
                            if (onRequestFinishListener != null) {
                                onRequestFinishListener.finishRequest(response);
                            }
                        }
                    }
                    if (onRequestFinishListener != null) {
                        onRequestFinishListener.finishRequest(response);
                    }

                    this.requestsHandler.removeRequest(request.getRequestId());
                    System.exit(0);
                }
            break;
            default:
                System.out.println("Message not handled");
                break;
        }
    }

    @Override
    public void init() {
        clientsManager.init();
    }

    public void updateDatabaseLocally(String arg, OnRequestFinishListener onRequestFinishListener) {
        String[] args = arg.split(":");
        database.setKey(Integer.parseInt(args[0]));
        database.setValue(Integer.parseInt(args[1]));
        ResultResponse response = new ResultResponse(nodeId, port, MessageType.DATABASE_UPDATED, true, "OK");
        onRequestFinishListener.finishRequest(response);
    }
}
