package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.admin.PassRequest;
import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Client;
import ar.edu.itba.pod.client.Util;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.io.IOException;

public class QueryClient extends Client {
    public static final String USAGE_MESSAGE = """
                                                Usage:
                                                    $> ./query-cli
                                                        -DserverAddress=xx.xx.xx.xx:yyyy
                                                        -Daction=[ capacity | confirmed ]
                                                """;

    @Override
    public Action getActionClass() {
        return QueryActions.getAction(System.getProperty("action")).getActionClass();
    }

    public static void main(String[] args) throws IOException {
        String usageMessage = QueryClient.USAGE_MESSAGE;
        try (Client client = new QueryClient()) {
            usageMessage = client.getUsageMessage();
            client.run();
        } catch (IllegalArgumentException e) {
            System.err.println(Util.INVALID_ARGUMENT_MESSAGE);
            System.err.println(usageMessage);
            System.exit(2);
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.UNAVAILABLE) {
                System.err.println(Util.SERVER_UNAVAILABLE_MESSAGE);
                System.exit(2);
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
