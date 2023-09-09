package ar.edu.itba.pod.client.book;

import ar.edu.itba.pod.client.Action;

import java.util.Collections;
import java.util.List;

public enum BookActions {
    ATTRACTIONS("attractions", new AttractionsAction(Collections.emptyList())),
    AVAILABILITY_SINGLE_SLOT("availability", new AvailabilityAction(List.of("ride", "day", "slot"), AvailabilityAction.Type.SINGLE_SLOT)),
    AVAILABILITY_MULTIPLE_SLOT("availability", new AvailabilityAction(List.of("ride", "day", "slot", "slotTo"), AvailabilityAction.Type.MULTIPLE_SLOT)),
    AVAILABILITY_MULTIPLE_RIDES("availability", new AvailabilityAction(List.of("day", "slot", "slotTo"), AvailabilityAction.Type.MULTIPLE_RIDES)),
    BOOK("book", new BookAction(List.of("visitor", "ride", "day", "slot"))),
    CONFIRM("confirm", new ConfirmAction(List.of("visitor", "ride", "day", "slot"))),
    CANCEL("cancel", new CancelAction(List.of("visitor", "ride", "day", "slot")));

    private final String actionName;

    private final Action action;

    BookActions(String actionName, Action action) {
        this.actionName = actionName;
        this.action = action;
    }

    public static BookActions getAction(String actionName) {
        if (actionName.equals("availability")) {
            boolean hasRide = System.getProperty("ride") != null;
            boolean hasSlotTo = System.getProperty("slotTo") != null;

            if (hasSlotTo) {
                if (hasRide) {
                    return AVAILABILITY_MULTIPLE_SLOT;
                } else {
                    return AVAILABILITY_MULTIPLE_RIDES;
                }
            } else {
                return AVAILABILITY_SINGLE_SLOT;
            }
        }

        for (BookActions action : values()) {
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
