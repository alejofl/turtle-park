package ar.edu.itba.pod.client.notification;

import ar.edu.itba.pod.notification.NotificationResponse;

public enum NotificationStatus {
    BOOKING_PENDING {
        @Override
        public String consumeNotification(NotificationResponse response) {
            return String.format(
                    "The reservation for %s at %s on the day %d is PENDING.",
                    response.getRideName(),
                    response.getSlot(),
                    response.getDayOfYear()
            );
        }
    },
    BOOKING_CONFIRMED {
        @Override
        public String consumeNotification(NotificationResponse response) {
            return String.format(
                    "The reservation for %s at %s on the day %d is CONFIRMED.",
                    response.getRideName(),
                    response.getSlot(),
                    response.getDayOfYear()
            );
        }
    },
    BOOKING_CANCELLED {
        @Override
        public String consumeNotification(NotificationResponse response) {
            return String.format(
                    "The reservation for %s at %s on the day %d is CANCELLED.",
                    response.getRideName(),
                    response.getSlot(),
                    response.getDayOfYear()
            );
        }
    },
    BOOKING_MOVED {
        @Override
        public String consumeNotification(NotificationResponse response) {
            return String.format(
                    "The reservation for %s at %s on the day %d was moved to %s and is PENDING.",
                    response.getRideName(),
                    response.getSlot(),
                    response.getDayOfYear(),
                    response.getNewSlot()
            );
        }
    },
    CAPACITY_ANNOUNCED {
        @Override
        public String consumeNotification(NotificationResponse response) {
            return String.format(
                    "%s announced slot capacity for the day %d: %d places.",
                    response.getRideName(),
                    response.getDayOfYear(),
                    response.getCapacity()
            );
        }
    };

    public abstract String consumeNotification(NotificationResponse response);
}
