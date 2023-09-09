package ar.edu.itba.pod.client.admin;

import ar.edu.itba.pod.admin.*;
import ar.edu.itba.pod.client.Action;
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
        } catch (IllegalArgumentException e) {
            throw new StatusRuntimeException(Status.INVALID_ARGUMENT);
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.INVALID_ARGUMENT) {
                System.err.println(Util.INVALID_ARGUMENT_MESSAGE);
                System.err.println("""
                            Usage:
                                $> ./admin-cli
                                        -DserverAddress=xx.xx.xx.xx:yyyy
                                        -Daction=[ rides | tickets | slots ]
                                        [ -DinPath=filename | -Dride=rideName | -Dday=dayOfYear | -Dcapacity=amount ]
                            """);
                System.exit(2);
            }
            System.err.println(Util.GENERIC_ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
