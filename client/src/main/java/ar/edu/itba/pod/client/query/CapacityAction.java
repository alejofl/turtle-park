package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Util;
import ar.edu.itba.pod.query.QueryRequest;
import ar.edu.itba.pod.query.QueryServiceGrpc;
import ar.edu.itba.pod.query.SuggestedCapacity;
import ar.edu.itba.pod.query.SuggestedCapacityResponse;
import io.grpc.ManagedChannel;
import jdk.dynalink.StandardOperation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class CapacityAction extends Action {
    public CapacityAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        QueryServiceGrpc.QueryServiceBlockingStub stub = QueryServiceGrpc.newBlockingStub(channel);
        Integer dayOfYear = Integer.valueOf(System.getProperty("day"));
        QueryRequest request = QueryRequest.newBuilder().setDayOfYear(dayOfYear.intValue()).build();
        SuggestedCapacityResponse response = stub.getSuggestedCapacities(request);

        Path file = Paths.get("suggestedCapacities.txt");
        try {
            Files.createFile(file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        String firstLine = String.format("%-5s | %s | %s\n", "Slot", "Capacity", "Attraction");
        try {
            Files.writeString(file, firstLine, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        for(SuggestedCapacity s : response.getCapacitiesList()) {
            String line = String.format("%-5s | %8d | %s\n", s.getSlot(), s.getSuggestedCapacity(), s.getRideName());
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
                        -Daction=capacity
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
