package ar.edu.itba.pod.data;

import ar.edu.itba.pod.notification.NotificationResponse;
import ar.edu.itba.pod.server.Util;

public enum NotificationStatus {
    BOOKING_PENDING {
        @Override
        public NotificationResponse consumeNotification(NotificationInformation notificationInformation) {
            return NotificationResponse
                    .newBuilder()
                    .setRideName(notificationInformation.rideName())
                    .setUserId(notificationInformation.visitorId().toString())
                    .setDayOfYear(notificationInformation.dayOfYear())
                    .setSlot(notificationInformation.slot().format(Util.TIME_FORMATTER))
                    .setStatus(ar.edu.itba.pod.notification.NotificationStatus.BOOKING_PENDING)
                    .build();
        }
    },
    BOOKING_CONFIRMED {
        @Override
        public NotificationResponse consumeNotification(NotificationInformation notificationInformation) {
            return NotificationResponse
                    .newBuilder()
                    .setRideName(notificationInformation.rideName())
                    .setUserId(notificationInformation.visitorId().toString())
                    .setDayOfYear(notificationInformation.dayOfYear())
                    .setSlot(notificationInformation.slot().format(Util.TIME_FORMATTER))
                    .setStatus(ar.edu.itba.pod.notification.NotificationStatus.BOOKING_CONFIRMED)
                    .build();
        }
    },
    BOOKING_CANCELLED {
        @Override
        public NotificationResponse consumeNotification(NotificationInformation notificationInformation) {
            return NotificationResponse
                    .newBuilder()
                    .setRideName(notificationInformation.rideName())
                    .setUserId(notificationInformation.visitorId().toString())
                    .setDayOfYear(notificationInformation.dayOfYear())
                    .setSlot(notificationInformation.slot().format(Util.TIME_FORMATTER))
                    .setStatus(ar.edu.itba.pod.notification.NotificationStatus.BOOKING_CANCELLED)
                    .build();
        }
    },
    BOOKING_MOVED {
        @Override
        public NotificationResponse consumeNotification(NotificationInformation notificationInformation) {
            return NotificationResponse
                    .newBuilder()
                    .setRideName(notificationInformation.rideName())
                    .setUserId(notificationInformation.visitorId().toString())
                    .setDayOfYear(notificationInformation.dayOfYear())
                    .setSlot(notificationInformation.slot().format(Util.TIME_FORMATTER))
                    .setNewSlot(notificationInformation.newSlot().format(Util.TIME_FORMATTER))
                    .setStatus(ar.edu.itba.pod.notification.NotificationStatus.BOOKING_MOVED)
                    .build();
        }
    },
    CAPACITY_ANNOUNCED {
        @Override
        public NotificationResponse consumeNotification(NotificationInformation notificationInformation) {
            return NotificationResponse
                    .newBuilder()
                    .setRideName(notificationInformation.rideName())
                    .setUserId(notificationInformation.visitorId().toString())
                    .setDayOfYear(notificationInformation.dayOfYear())
                    .setCapacity(notificationInformation.capacity())
                    .setStatus(ar.edu.itba.pod.notification.NotificationStatus.CAPACITY_ANNOUNCED)
                    .build();
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
