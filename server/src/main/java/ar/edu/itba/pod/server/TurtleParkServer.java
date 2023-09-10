package ar.edu.itba.pod.server;

import ar.edu.itba.pod.data.Park;
import ar.edu.itba.pod.servant.AdminServant;
import ar.edu.itba.pod.servant.BookServant;
import ar.edu.itba.pod.servant.NotificationServant;
import ar.edu.itba.pod.servant.QueryServant;
import io.grpc.ServerBuilder;
import io.grpc.Server;

import java.io.IOException;
import java.util.Optional;

public class TurtleParkServer {
    public static void main(String[] args) throws InterruptedException, IOException {
        int port = Integer.parseInt(Optional.ofNullable(System.getProperty("port")).orElse("7321"));

        Server server = ServerBuilder.forPort(port)
                .addService(new AdminServant())
                .addService(new BookServant())
                .addService(new NotificationServant())
                .addService(new QueryServant())
                .build();
        server.start();

        server.awaitTermination();
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
    }
}
