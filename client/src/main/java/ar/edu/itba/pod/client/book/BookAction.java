package ar.edu.itba.pod.client.book;

import ar.edu.itba.pod.client.Action;
import io.grpc.ManagedChannel;

import java.util.List;

public class BookAction extends Action {
    public BookAction(List<String> argumentsForAction) {
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
                        -Daction=book
                        -Dday=dayOfYear
                        -Dride=rideName
                        -Dvisitor=visitorId
                        -Dslot=bookingSlot
                """;
    }
}
