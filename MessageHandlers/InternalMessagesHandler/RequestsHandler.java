package MessageHandlers.InternalMessagesHandler;

import java.util.ArrayList;
import java.util.List;

import Communication.Common.Messages.GetExtremumMessage.ExtremumType;
import Communication.Common.Messages.ResultResponse.ResultResponse;

public class RequestsHandler {
    public static class Request {
        private String senderId = null;
        private String requestId = null;
        private List<String> receivers = null;
        private List<ResultResponse> handled = null;
        private OnRequestFinishListener onRequestFinishListener = null;

        public OnRequestFinishListener getOnRequestFinishListener() {
            return onRequestFinishListener;
        }
        public String getSenderId() {
            return senderId;
        }
        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }
        public String getRequestId() {
            return requestId;
        }
        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }
        public void addHandled(ResultResponse newHandled) {
            handled.add(newHandled);
        }

        public Request(String senderId, String requestId, List<String> receivers, OnRequestFinishListener onRequestFinishListener) {
            this.senderId = senderId;
            this.requestId = requestId;
            this.receivers = receivers;
            this.handled = new ArrayList<>();
            this.onRequestFinishListener = onRequestFinishListener;
        }

        public Boolean isHandled(Boolean requester) {
            if (requester) {
                return receivers.size() == handled.size();
            }
            return receivers.size() - 1 == handled.size();
        }

        public List<ResultResponse> getResponses() {
            return handled;
        }

        public String toString() {
            String handledString = "";
            for (ResultResponse handledEntity : handled) {
                handledString += "\t" + handledEntity.toString() + "\n";
            }
            return String.format("[%s][SenderId: %s][RequestId: %s][Receivers: %s][Handled: \n %s \n ]",
                Request.class.getSimpleName(),
                this.senderId,
                this.requestId,
                this.receivers,
                handledString
            );
        }
    }

    public static class GetExtremumRequest extends Request {
        private ExtremumType type;

        public GetExtremumRequest(String senderId, String requestId, List<String> receivers,
                OnRequestFinishListener onRequestFinishListener, ExtremumType type) {
            super(senderId, requestId, receivers, onRequestFinishListener);
            this.type = type;
        }

        public ExtremumType getType() {
            return type;
        }
    }

    private List<Request> requests = null;

    public RequestsHandler() {
        this.requests = new ArrayList<>();
    }

    public void addRequest(Request newRequest) {
        this.requests.add(newRequest);
    }

    public void removeRequest(String requestId) {
        this.requests.removeIf((n) -> (n.getRequestId().equals(requestId)));
    }

    public Request getRequestById(String requestId) {
        for (Request request : this.requests) {
            if (request.getRequestId().equals(requestId)) {
                return request;
            }
        }
        return null;
    }
    
    public Integer getRequestsAMount() {
        return this.requests.size();
    }

    public OnRequestFinishListener getFinishListenerById(String requestId) {
        for (Request request : this.requests) {
            if (request.getRequestId().equals(requestId)) {
                return request.getOnRequestFinishListener();
            }
        }
        return null;
    }

    public void handleRequest(ResultResponse response) {
        for (Request request : this.requests) {
            if (request.getRequestId().equals(response.getRequestId())) {
                request.addHandled(response);
                break;
            }
        }
    }

    public List<Request> getAllRequests() {
        return requests;
    }
}
