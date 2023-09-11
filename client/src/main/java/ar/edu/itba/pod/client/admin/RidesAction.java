package ar.edu.itba.pod.client.admin;

import ar.edu.itba.pod.admin.AdminServiceGrpc;
import ar.edu.itba.pod.admin.AdminServiceGrpc.AdminServiceBlockingStub;
import ar.edu.itba.pod.admin.RideRequest;
import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Util;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class RidesAction extends Action {
    private final AtomicInteger successfulCalls = new AtomicInteger();
    private final AtomicInteger failedCalls = new AtomicInteger();

    public RidesAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        try (
                ExecutorService service = Executors.newCachedThreadPool();
                Stream<String> lines = Files.lines(Paths.get(System.getProperty("inPath"))).skip(1)
        ) {
            lines.forEach(line -> {
                try {
                    String[] fields = line.split(";");
                    if (fields.length != 4) {
                        failedCalls.getAndIncrement();
                    } else {
                        service.submit(new AddRideRunnable(
                                channel,
                                successfulCalls,
                                failedCalls,
                                fields[0],
                                fields[1],
                                fields[2],
                                Integer.parseInt(fields[3])
                        ));
                    }
                } catch (NumberFormatException e) {
                    failedCalls.getAndIncrement();
                }
            });
            service.shutdown();
            service.awaitTermination(Util.SYSTEM_TIMEOUT, Util.SYSTEM_TIMEOUT_UNIT);
        } catch (IOException | InterruptedException e) {
            System.err.println(Util.GENERIC_ERROR_MESSAGE);
            System.exit(2);
        }
        int failed = failedCalls.get();
        int successful = successfulCalls.get();
        if (failed > 0) {
            System.out.printf("Cannot add %d attractions\n", failed);
        }
        if (successful > 0) {
            System.out.printf("%d attractions added\n", successful);
        }
    }

    @Override
    public boolean hasValidArguments() {
        return super.hasValidArguments() && Files.exists(Paths.get(System.getProperty("inPath")));
    }

    @Override
    public String getUsageMessage() {
        return """
                Usage:
                    $> ./admin-cli
                        -DserverAddress=xx.xx.xx.xx:yyyy
                        -Daction=rides
                        -DinPath=filename
                """;
    }

    private static class AddRideRunnable implements Runnable {
        private final AdminServiceBlockingStub stub;
        private final AtomicInteger successfulCalls;
        private final AtomicInteger failedCalls;
        private final String rideName;
        private final String openingTime;
        private final String closingTime;
        private final int slotSize;

        public AddRideRunnable(
                ManagedChannel channel,
                AtomicInteger successfulCalls,
                AtomicInteger failedCalls,
                String rideName,
                String openingTime,
                String closingTime,
                int slotSize
        ) {
            stub = AdminServiceGrpc.newBlockingStub(channel);
            this.successfulCalls = successfulCalls;
            this.failedCalls = failedCalls;
            this.rideName = rideName;
            this.openingTime = openingTime;
            this.closingTime = closingTime;
            this.slotSize = slotSize;
        }
        @Override
        public void run() {
            RideRequest request = RideRequest
                    .newBuilder()
                    .setRideName(this.rideName)
                    .setOpeningTime(this.openingTime)
                    .setClosingTime(this.closingTime)
                    .setSlotSize(this.slotSize)
                    .build();
            try {
                stub.addRide(request);
            } catch (StatusRuntimeException e) {
                if (e.getStatus() == Status.INVALID_ARGUMENT) {
                    failedCalls.getAndIncrement();
                    return;
                }
                System.err.println(Util.GENERIC_ERROR_MESSAGE);
                System.exit(1);
            }
            successfulCalls.getAndIncrement();
        }
    }
}
