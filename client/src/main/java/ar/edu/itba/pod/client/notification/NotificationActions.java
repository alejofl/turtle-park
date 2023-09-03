package ar.edu.itba.pod.client.notification;

import ar.edu.itba.pod.client.Action;

import java.util.List;

public enum NotificationActions {
    FOLLOW("follow", new FollowAction(List.of("day", "ride", "visitor"))),
    UNFOLLOW("unfollow", new UnfollowAction(List.of("day", "ride", "visitor")));

    private final String actionName;

    private final Action action;

    NotificationActions(String actionName, Action action) {
        this.actionName = actionName;
        this.action = action;
    }

    public static NotificationActions getAction(String actionName) {
        for (NotificationActions action : values()) {
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
