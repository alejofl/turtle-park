package ar.edu.itba.pod.client.admin;

import ar.edu.itba.pod.admin.AdminServiceGrpc;
import ar.edu.itba.pod.admin.PassRequest;
import ar.edu.itba.pod.admin.PassType;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class  TicketsAction extends Action {
    private final AtomicInteger successfulCalls = new AtomicInteger();
    private final AtomicInteger failedCalls = new AtomicInteger();

    public TicketsAction(List<String> argumentsForAction) {
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
                    String[] fields = line.split(",");
                    if (fields.length != 3) {
                        failedCalls.getAndIncrement();
                    } else {
                        service.submit(new AddTicketRunnable(
                                channel,
                                successfulCalls,
                                failedCalls,
                                fields[0],
                                fields[1],
                                Integer.parseUnsignedInt(fields[2])
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
        System.out.printf("Cannot add %d passes\n%d passes added\n", failedCalls.get(), successfulCalls.get());
    }

    @Override
    public String getUsageMessage() {
        return """
                Usage:
                    $> ./admin-cli
                        -DserverAddress=xx.xx.xx.xx:yyyy
                        -Daction=tickets
                        -DinPath=filename
                """;
    }

    private static class AddTicketRunnable implements Runnable {
        private final AdminServiceGrpc.AdminServiceBlockingStub stub;
        private final AtomicInteger successfulCalls;
        private final AtomicInteger failedCalls;
        private final String visitorId;
        private final String passType;
        private final int dayOfYear;

        public AddTicketRunnable(
                ManagedChannel channel,
                AtomicInteger successfulCalls,
                AtomicInteger failedCalls,
                String visitorId,
                String passType,
                int dayOfYear
        ) {
            stub = AdminServiceGrpc.newBlockingStub(channel);
            this.successfulCalls = successfulCalls;
            this.failedCalls = failedCalls;
            this.visitorId = visitorId;
            this.passType = passType;
            this.dayOfYear = dayOfYear;
        }
        @Override
        public void run() {
            PassRequest request = PassRequest
                    .newBuilder()
                    .setUserId(this.visitorId)
                    .setType(PassType.valueOf(this.passType))
                    .setDayOfYear(this.dayOfYear)
                    .build();
            try {
                stub.addPass(request);
            } catch (IllegalArgumentException e) {
                failedCalls.getAndIncrement();
                return;
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
