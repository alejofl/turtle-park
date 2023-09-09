package ar.edu.itba.pod.data;

import ar.edu.itba.pod.notification.NotificationResponse;

public enum NotificationStatus {
    BOOKING_PENDING {
        @Override
        public NotificationResponse consumeNotification(NotificationInformation notificationInformation) {
            return null;
        }
    },
    BOOKING_CONFIRMED {
        @Override
        public NotificationResponse consumeNotification(NotificationInformation notificationInformation) {
            return null;
        }
    },
    BOOKING_CANCELLED {
        @Override
        public NotificationResponse consumeNotification(NotificationInformation notificationInformation) {
            return null;
        }
    },
    BOOKING_MOVED {
        @Override
        public NotificationResponse consumeNotification(NotificationInformation notificationInformation) {
            return null;
        }
    },
    CAPACITY_ANNOUNCED {
        @Override
        public NotificationResponse consumeNotification(NotificationInformation notificationInformation) {
            return null;
        }
    },
    POISON_PILL {
        @Override
        public NotificationResponse consumeNotification(NotificationInformation notificationInformation) {
            return null;
        }
    };

    public abstract NotificationResponse consumeNotification(NotificationInformation notificationInformation);
}
