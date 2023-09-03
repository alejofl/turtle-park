package ar.edu.itba.pod.client.notification;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Client;

import java.io.IOException;

public class NotificationClient extends Client {
    @Override
    public Action getActionClass() {
        return NotificationActions.getAction(System.getProperty("action")).getActionClass();
    }

    public static void main(String[] args) throws IOException {
        try (Client client = new NotificationClient()) {
            client.run();
        }
    }
}
