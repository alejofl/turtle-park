package ar.edu.itba.pod.client.admin;

import ar.edu.itba.pod.client.Action;

import java.util.List;

public enum AdminActions {
    RIDES("rides", new RidesAction(List.of("inPath"))),
    TICKETS("tickets", new TicketsAction(List.of("inPath"))),
    SLOTS("slots", new SlotsAction(List.of("ride", "capacity", "day")));

    private final String actionName;

    private final Action action;

    AdminActions(String actionName, Action action) {
        this.actionName = actionName;
        this.action = action;
    }

    public static AdminActions getAction(String actionName) {
        for (AdminActions action : values()) {
            if (actionName.equals(action.actionName)) {
                return action;
            }
        }
        throw new IllegalArgumentException();
    }

    public Action getActionClass() {
        return action;
    }
}
