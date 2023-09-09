package ar.edu.itba.pod.client.book;

import ar.edu.itba.pod.book.*;
import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Util;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.Comparator;
import java.util.List;

public class AvailabilityAction extends Action {
    public enum Type {
        SINGLE_SLOT {
            @Override
            public AvailabilityRequest getAvailabilityRequest(int dayOfYear, String rideName, String startingSlot, String endingSlot) {
                return AvailabilityRequest
                        .newBuilder()
                        .setDayOfYear(dayOfYear)
                        .setSingleSlotAvailability(SingleSlotAvailabilityRequest
                                                    .newBuilder()
                                                    .setRideName(rideName)
                                                    .setSlot(startingSlot)
                                                    .build()
                                                  )
                        .build();
            }
        },
        MULTIPLE_SLOT {
            @Override
            public AvailabilityRequest getAvailabilityRequest(int dayOfYear, String rideName, String startingSlot, String endingSlot) {
                return AvailabilityRequest
                        .newBuilder()
                        .setDayOfYear(dayOfYear)
                        .setMultipleSlotAvailability(MultipleSlotAvailabilityRequest
                                                        .newBuilder()
                                                        .setRideName(rideName)
                                                        .setStartingSlot(startingSlot)
                                                        .setEndingSlot(endingSlot)
                                                        .build()
                                                    )
                        .build();
            }
        },
        MULTIPLE_RIDES {
            @Override
            public AvailabilityRequest getAvailabilityRequest(int dayOfYear, String rideName, String startingSlot, String endingSlot) {
                return AvailabilityRequest
                        .newBuilder()
                        .setDayOfYear(dayOfYear)
                        .setMultipleRidesAvailability(MultipleRidesAvailabilityRequest
                                                        .newBuilder()
                                                        .setStartingSlot(startingSlot)
                                                        .setEndingSlot(endingSlot)
                                                        .build()
                                                     )
                        .build();
            }
        };

        abstract public AvailabilityRequest getAvailabilityRequest(
                int dayOfYear,
                String rideName,
                String startingSlot,
                String endingSlot
        );
    }

    private final Type type;

    public AvailabilityAction(List<String> argumentsForAction, Type type) {
        super(argumentsForAction);
        this.type = type;
    }

    @Override
    public void run(ManagedChannel channel) {
        BookServiceGrpc.BookServiceBlockingStub stub = BookServiceGrpc.newBlockingStub(channel);

        try {
            AvailabilityRequest request = type.getAvailabilityRequest(
                    Integer.parseInt(System.getProperty("day")),
                    System.getProperty("ride"),
                    System.getProperty("slot"),
                    System.getProperty("slotTo")
            );
            AvailabilityResponse response = stub.getAvailability(request);
            System.out.printf("\033[1m%-5s | %8s  | %7s   | %9s | %s\033[0m\n", "Slot", "Capacity", "Pending", "Confirmed", "Attraction");
            response.getDataList().stream().sorted((o1, o2) -> {
                int result = o1.getSlot().compareTo(o2.getSlot());
                if (result != 0) {
                    result = o1.getRideName().compareTo(o2.getRideName());
                }
                return result;
            }).map(avail -> String.format(
                                            "%-5s | %8s  | %8d  | %9d | %s",
                                            avail.getSlot(),
                                            avail.hasSlotCapacity() ? String.valueOf(avail.getSlotCapacity()) : "X",
                                            avail.getPendingBookings(),
                                            avail.getConfirmedBookings(),
                                            avail.getRideName()
                                         )
            ).forEach(System.out::println);
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.INVALID_ARGUMENT) {
                throw new IllegalArgumentException();
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
                        -Daction=availability
                        -Dday=dayOfYear
                        [ -Dride=rideName ]
                        -Dslot=bookingSlot
                        [ -DslotTo=bookingSlotTo ]
                """;
    }
}
