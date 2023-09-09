package ar.edu.itba.pod.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract class Client implements Closeable {
    private final ManagedChannel channel;

    private final Action action;

    public Client() {
        if (!hasValidArguments()) {
            throw new IllegalArgumentException();
        }
        this.action = getActionClass();
        if (!this.action.hasValidArguments()) {
            throw new IllegalArgumentException();
        }
        channel = ManagedChannelBuilder.forTarget(System.getProperty("serverAddress"))
                .usePlaintext()
                .build();
    }

    private boolean hasValidArguments() {
        return System.getProperty("serverAddress") != null && System.getProperty("action") != null;
    }

    public void run() {
        action.run(channel);
    }

    public String getUsageMessage() {
        return action.getUsageMessage();
    }

    public abstract Action getActionClass();

    @Override
    public void close() throws IOException {
        try {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }
}
