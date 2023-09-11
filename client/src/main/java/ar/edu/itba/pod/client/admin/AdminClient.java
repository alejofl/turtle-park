package ar.edu.itba.pod.client.admin;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Client;
import ar.edu.itba.pod.client.Util;

import java.io.IOException;

public class AdminClient extends Client {
    public static final String USAGE_MESSAGE = """
                                                Usage:
                                                    $> ./admin-cli
                                                        -DserverAddress=xx.xx.xx.xx:yyyy
                                                        -Daction=[ rides | slots | tickets ]
                                                """;

    @Override
    public Action getActionClass() {
        return AdminActions.getAction(System.getProperty("action")).getActionClass();
    }

    public static void main(String[] args) throws IOException {
        String usageMessage = AdminClient.USAGE_MESSAGE;
        try (Client client = new AdminClient()) {
            usageMessage = client.getUsageMessage();
            client.run();
        } catch (IllegalArgumentException e) {
            System.err.println(Util.INVALID_ARGUMENT_MESSAGE);
            System.err.println(usageMessage);
            System.exit(2);
        }
    }
}
