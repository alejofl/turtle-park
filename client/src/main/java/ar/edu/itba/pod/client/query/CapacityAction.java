package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.client.Action;
import io.grpc.ManagedChannel;

import java.util.List;

public class CapacityAction extends Action {
    public CapacityAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        //TODO
    }
}
