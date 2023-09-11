package ar.edu.itba.pod.client.book;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Client;
import ar.edu.itba.pod.client.Util;
import ar.edu.itba.pod.client.notification.NotificationClient;

import java.io.IOException;

public class BookClient extends Client {
    public static final String USAGE_MESSAGE = """
                                                Usage:
                                                    $> ./book-cli
                                                        -DserverAddress=xx.xx.xx.xx:yyyy
                                                        -Daction=[ attractions | availability | book | cancel | confirm ]
                                                """;

    @Override
    public Action getActionClass() {
        return BookActions.getAction(System.getProperty("action")).getActionClass();
    }

    public static void main(String[] args) throws IOException {
        String usageMessage = BookClient.USAGE_MESSAGE;
        try (Client client = new BookClient()) {
            usageMessage = client.getUsageMessage();
            client.run();
        } catch (IllegalArgumentException e) {
            System.err.println(Util.INVALID_ARGUMENT_MESSAGE);
            System.err.println(usageMessage);
            System.exit(2);
        }
    }
}
