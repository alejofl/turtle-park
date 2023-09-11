package ar.edu.itba.pod.client.notification;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Client;
import ar.edu.itba.pod.client.Util;
import ar.edu.itba.pod.client.query.QueryClient;

import java.io.IOException;

public class NotificationClient extends Client {
    public static final String USAGE_MESSAGE = """
                                                Usage:
                                                    $> ./notif-cli
                                                        -DserverAddress=xx.xx.xx.xx:yyyy
                                                        -Daction=[ follow | unfollow ]
                                                """;

    @Override
    public Action getActionClass() {
        return NotificationActions.getAction(System.getProperty("action")).getActionClass();
    }

    public static void main(String[] args) throws IOException {
        String usageMessage = NotificationClient.USAGE_MESSAGE;
        try (Client client = new NotificationClient()) {
            usageMessage = client.getUsageMessage();
            client.run();
        } catch (IllegalArgumentException e) {
            System.err.println(Util.INVALID_ARGUMENT_MESSAGE);
            System.err.println(usageMessage);
            System.exit(2);
        }
    }
}
