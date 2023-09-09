package ar.edu.itba.pod.client.book;

import ar.edu.itba.pod.client.Action;
import io.grpc.ManagedChannel;

import java.util.List;

public class AvailabilityAction extends Action {
    public enum Type {
        SINGLE_SLOT,
        MULTIPLE_SLOT,
        MULTIPLE_RIDES;
    }

    private final Type type;

    public AvailabilityAction(List<String> argumentsForAction, Type type) {
        super(argumentsForAction);
        this.type = type;
    }

    @Override
    public void run(ManagedChannel channel) {
        //TODO
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
