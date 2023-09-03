package ar.edu.itba.pod.client.admin;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Client;

import java.io.IOException;

public class AdminClient extends Client {
    @Override
    public Action getActionClass() {
        return AdminActions.getAction(System.getProperty("action")).getActionClass();
    }

    public static void main(String[] args) throws IOException {
        try (Client client = new AdminClient()) {
            client.run();
        }
    }
}
