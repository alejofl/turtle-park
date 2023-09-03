package ar.edu.itba.pod.client;

import java.util.List;

public abstract class Action {
    private final List<String> argumentsForAction;

    public Action(List<String> argumentsForAction) {
        this.argumentsForAction = argumentsForAction;
    }

    public abstract void run();

    public boolean hasValidArguments() {
        for (String arg : argumentsForAction) {
            if (System.getProperty(arg) == null) {
                return false;
            }
        }
        return true;
    }
}
