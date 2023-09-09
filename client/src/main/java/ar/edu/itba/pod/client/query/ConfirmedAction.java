package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.query.*;
import io.grpc.ManagedChannel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class ConfirmedAction extends Action {
    public ConfirmedAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        QueryServiceGrpc.QueryServiceBlockingStub stub = QueryServiceGrpc.newBlockingStub(channel);
        Integer dayOfYear = Integer.valueOf(System.getProperty("day"));
        QueryRequest request = QueryRequest.newBuilder().setDayOfYear(dayOfYear).build();
        ConfirmedBookingResponse response = stub.getConfirmedBookings(request);

        Path file = Paths.get("confirmedBooking.txt");
        try {
            Files.createFile(file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        String firstLine = String.format("%-5s | %-36s | %s\n", "Slot", "Visitor", "Attraction");
        try {
            Files.writeString(file, firstLine, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        for(ConfirmedBooking c : response.getBookingsList()) {
            String line = String.format("%s | %s | %s\n", c.getSlot(), c.getUserId(), c.getRideName());
            try {
                Files.writeString(file, line, StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
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
