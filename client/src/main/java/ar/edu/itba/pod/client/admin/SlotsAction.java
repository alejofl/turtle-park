package ar.edu.itba.pod.client.admin;

import ar.edu.itba.pod.admin.*;
import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.List;

public class SlotsAction extends Action {
    public SlotsAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        AdminServiceGrpc.AdminServiceBlockingStub stub = AdminServiceGrpc.newBlockingStub(channel);

        try {
            CapacityRequest request = CapacityRequest
                    .newBuilder()
                    .setRideName(System.getProperty("ride"))
                    .setCapacity(Integer.parseInt(System.getProperty("capacity")))
                    .setDayOfYear(Integer.parseInt(System.getProperty("day")))
                    .build();
            CapacityResponse response = stub.loadRideCapacity(request);
            System.out.printf("""
                            Loaded capacity of %d for %s on day %d
                            %d bookings confirmed without changes
                            %d bookings relocated
                            %d bookings cancelled
                            """,
                    Integer.parseInt(System.getProperty("capacity")),
                    System.getProperty("ride"),
                    Integer.parseInt(System.getProperty("day")),
                    response.getConfirmedBookings(),
                    response.getPendingBookings(),
                    response.getCancelledBookings()
            );
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.INVALID_ARGUMENT) {
                throw new IllegalArgumentException();
            } else if (e.getStatus() == Status.UNAVAILABLE) {
                throw new ServerUnavailableException();
            }
            System.err.println(Util.GENERIC_ERROR_MESSAGE);
            System.exit(1);
        }
    }

    @Override
    public boolean hasValidArguments() {
        try {
            Integer.parseInt(System.getProperty("capacity"));
            Integer.parseInt(System.getProperty("day"));
            return super.hasValidArguments();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getUsageMessage() {
        return """
                Usage:
                    $> ./admin-cli
                        -DserverAddress=xx.xx.xx.xx:yyyy
                        -Daction=slots
                        -Dride=rideName
                        -Dday=dayOfYear
                        -Dcapacity=amount
                """;
    }
}
