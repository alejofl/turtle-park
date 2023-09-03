package ar.edu.itba.pod.client.book;

import ar.edu.itba.pod.client.Action;

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
    public void run() {
        //TODO
    }
}
