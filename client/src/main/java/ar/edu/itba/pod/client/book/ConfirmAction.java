package ar.edu.itba.pod.client.book;

import ar.edu.itba.pod.client.Action;
import io.grpc.ManagedChannel;

import java.util.List;

public class ConfirmAction extends Action {
    public ConfirmAction(List<String> argumentsForAction) {
        super(argumentsForAction);
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
                        -Daction=confirm
                        -Dday=dayOfYear
                        -Dride=rideName
                        -Dvisitor=visitorId
                        -Dslot=bookingSlot
                """;
    }
}
