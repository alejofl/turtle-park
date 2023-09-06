package ar.edu.itba.pod.client.book;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Client;

import java.io.IOException;

public class BookClient extends Client {
    @Override
    public Action getActionClass() {
        return BookActions.getAction(System.getProperty("action")).getActionClass();
    }

    public static void main(String[] args) throws IOException {
        try (Client client = new BookClient()) {
            client.run();
        }
    }
}