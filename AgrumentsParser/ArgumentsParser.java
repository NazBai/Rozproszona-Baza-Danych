package AgrumentsParser;

import AgrumentsParser.models.Connection;

public class ArgumentsParser {
    
    public static ArgumentsObject parseArguments(String[] args) {
        ArgumentsObject argumentsObject = new ArgumentsObject();
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].charAt(0) == '-') {
                if (args[i].length() < 2) {
                    throw new IllegalArgumentException("Not a valid argument: " + args[i]);
                }
                if (args.length-1 == i) {
                    throw new IllegalArgumentException("Expected arg after: " + args[i]);
                }
                if (args[i].equals("-tcpport")) {
                    argumentsObject.setTcpPort(Integer.parseInt(args[i+1]));
                }
                if (args[i].equals("-connect")) {
                    String connectionString = args[i+1];
                    String[] connectionArgs = connectionString.split(":");
                    argumentsObject.addConnection(new Connection(connectionArgs[0], Integer.parseInt(connectionArgs[1])));
                }
                if (args[i].equals("-record")) {
                    String recordsString = args[i+1];
                    String[] recordsArgs = recordsString.split(":");
                    argumentsObject.setRecord(Integer.parseInt(recordsArgs[0]), Integer.parseInt(recordsArgs[1]));
                }
            }
        }
        return argumentsObject;
    }
}
