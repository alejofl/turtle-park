package ar.edu.itba.pod.client.admin;

import ar.edu.itba.pod.client.Action;
import io.grpc.ManagedChannel;

import java.util.List;

public class  TicketsAction extends Action {
    public TicketsAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        //TODO
    }
}
