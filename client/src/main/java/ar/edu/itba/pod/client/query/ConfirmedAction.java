package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.client.Action;
import io.grpc.ManagedChannel;

import java.util.List;

public class ConfirmedAction extends Action {
    public ConfirmedAction(List<String> argumentsForAction) {
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
                    $> ./query-cli
                        -DserverAddress=xx.xx.xx.xx:yyyy
                        -Daction=confirmed
                        -Dday=dayOfYear
                        -DoutPath=filePath
                """;
    }
}
