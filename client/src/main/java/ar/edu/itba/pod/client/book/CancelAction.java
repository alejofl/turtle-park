package ar.edu.itba.pod.client.book;

import ar.edu.itba.pod.book.BookServiceGrpc;
import ar.edu.itba.pod.book.BookingRequest;
import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.List;

public class CancelAction extends Action {
    public CancelAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        BookServiceGrpc.BookServiceBlockingStub stub = BookServiceGrpc.newBlockingStub(channel);

        try {
            BookingRequest request = BookingRequest.newBuilder()
                    .setRideName(System.getProperty("ride"))
                    .setDayOfYear(Integer.parseInt(System.getProperty("day")))
                    .setSlot(System.getProperty("slot"))
                    .setUserId(System.getProperty("visitor"))
                    .build();
            stub.cancelBooking(request);
            System.out.printf(
                    "The reservation for %s at %s on the day %d is CANCELLED\n",
                    System.getProperty("ride"),
                    System.getProperty("slot"),
                    Integer.parseInt(System.getProperty("day"))
            );
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
    public boolean hasValidArguments() {
        try {
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
                    $> ./book-cli
                        -DserverAddress=xx.xx.xx.xx:yyyy
                        -Daction=cancel
                        -Dday=dayOfYear
                        -Dride=rideName
                        -Dvisitor=visitorId
                        -Dslot=bookingSlot
                """;
    }
}
