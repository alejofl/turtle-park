package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Client;

import java.io.IOException;

public class QueryClient extends Client {
    @Override
    public Action getActionClass() {
        return QueryActions.getAction(System.getProperty("action")).getActionClass();
    }

    public static void main(String[] args) throws IOException {
        try (Client client = new QueryClient()) {
            client.run();
        }
    }
}
