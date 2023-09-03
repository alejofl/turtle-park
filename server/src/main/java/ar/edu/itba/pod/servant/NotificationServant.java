package ar.edu.itba.pod.servant;

import ar.edu.itba.pod.commons.Empty;
import ar.edu.itba.pod.notification.NotificationRequest;
import ar.edu.itba.pod.notification.NotificationServiceGrpc.NotificationServiceImplBase;
import io.grpc.stub.StreamObserver;

public class NotificationServant extends NotificationServiceImplBase {
    @Override
    public void followBooking(NotificationRequest notificationRequest, StreamObserver<Empty> empty) {
        //TODO
    }

    @Override
    public void unfollowBooking(NotificationRequest notificationRequest, StreamObserver<Empty> empty) {
        //TODO
    }
}
