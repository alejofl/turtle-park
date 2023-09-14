package ar.edu.itba.pod.client.book;

import ar.edu.itba.pod.admin.AdminServiceGrpc;
import ar.edu.itba.pod.admin.CapacityRequest;
import ar.edu.itba.pod.admin.CapacityResponse;
import ar.edu.itba.pod.book.BookServiceGrpc;
import ar.edu.itba.pod.book.RideResponse;
import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;
import ar.edu.itba.pod.commons.Empty;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.awt.print.Book;
import java.util.List;

public class AttractionsAction extends Action {
    public AttractionsAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        BookServiceGrpc.BookServiceBlockingStub stub = BookServiceGrpc.newBlockingStub(channel);

        try {
            RideResponse response = stub.getRides(Empty.newBuilder().build());
            System.out.printf("%-40s | Opening Time | Closing Time\n", "Attraction");
            response.getRidesList().stream().map(ride -> String.format(
                                                            "%-40s | %12s | %12s",
                                                            ride.getRideName(),
                                                            ride.getOpeningTime(),
                                                            ride.getClosingTime()
                                                         )
                                                ).forEach(System.out::println);
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
    public String getUsageMessage() {
        return """
                Usage:
                    $> ./book-cli
                        -DserverAddress=xx.xx.xx.xx:yyyy
                        -Daction=attractions
                """;
    }
}
