package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;
import ar.edu.itba.pod.query.*;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class ConfirmedAction extends Action {
    public ConfirmedAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        QueryServiceGrpc.QueryServiceBlockingStub stub = QueryServiceGrpc.newBlockingStub(channel);
        int dayOfYear = Integer.parseInt(System.getProperty("day"));
        QueryRequest request = QueryRequest.newBuilder().setDayOfYear(dayOfYear).build();

        try {
            ConfirmedBookingResponse response = stub.getConfirmedBookings(request);
            List<ConfirmedBooking> bookings = response.getBookingsList();
            try {
                if (bookings.isEmpty()) {
                    throw new IllegalStateException();
                }

                Path path = Paths.get(System.getProperty("outPath"));
                Files.write(
                        path,
                        String.format("%-5s | %-36s | %s\n", "Slot", "Visitor", "Attraction").getBytes(),
                        StandardOpenOption.WRITE,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                );
                for (ConfirmedBooking c : bookings) {
                    Files.write(
                            path,
                            String.format("%s | %s | %s\n", c.getSlot(), c.getUserId(), c.getRideName()).getBytes(),
                            StandardOpenOption.WRITE,
                            StandardOpenOption.APPEND
                    );
                }
            } catch (IOException | InvalidPathException e) {
                System.err.println(Util.IO_ERROR_MESSAGE);
                System.exit(1);
            } catch (IllegalStateException e) {
                System.err.println("The query gave no results.");
                System.exit(0);
            }
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.INVALID_ARGUMENT) {
                throw new IllegalArgumentException();
            } else if (e.getStatus().getCode() == Status.UNAVAILABLE.getCode()) {
                throw new ServerUnavailableException();
            }
            System.err.println(Util.GENERIC_ERROR_MESSAGE);
            System.exit(1);
        }
    }

    @Override
    public String getUsageMessage() {
        return """
                Usage:
                    $> ./query-cli
                        -DserverAddress=xx.xx.xx.xx:yyyy
                        -Daction=confirmed
                        -Dday=dayOfYear
                        -DoutPath=filePath
                """;
    }

    @Override
    public boolean hasValidArguments() {
        try {
            Integer.parseInt(System.getProperty("day"));
            return super.hasValidArguments();
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
