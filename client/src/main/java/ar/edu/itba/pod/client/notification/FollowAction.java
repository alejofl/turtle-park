package ar.edu.itba.pod.client.notification;

import ar.edu.itba.pod.client.Action;
import io.grpc.ManagedChannel;

import java.util.List;

public class FollowAction extends Action {
    public FollowAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        //TODO
    }
}
