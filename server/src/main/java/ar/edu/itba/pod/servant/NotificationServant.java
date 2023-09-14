package ar.edu.itba.pod.servant;

import ar.edu.itba.pod.commons.Empty;
import ar.edu.itba.pod.data.NotificationInformation;
import ar.edu.itba.pod.data.Park;
import ar.edu.itba.pod.notification.NotificationRequest;
import ar.edu.itba.pod.notification.NotificationResponse;
import ar.edu.itba.pod.notification.NotificationServiceGrpc.NotificationServiceImplBase;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class NotificationServant extends NotificationServiceImplBase {
    private final Park park = Park.getInstance();
    @Override
    public void followBooking(NotificationRequest notificationRequest, StreamObserver<NotificationResponse> notificationResponse) {
        System.out.println("FOLLOW BOOKING " + notificationRequest.getAllFields().toString());
        try {
            BlockingQueue<NotificationInformation> blockingQueue = park.followBooking(notificationRequest.getRideName(), UUID.fromString(notificationRequest.getUserId()), notificationRequest.getDayOfYear());
            NotificationInformation info = blockingQueue.take();
            for (NotificationResponse notif = info.status().consumeNotification(info); notif != null; info = blockingQueue.take(), notif = info.status().consumeNotification(info)) {
                notificationResponse.onNext(notif);
            }
            notificationResponse.onCompleted();
        } catch (IllegalArgumentException e) {
            notificationResponse.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        } catch (InterruptedException e) {
            notificationResponse.onError(Status.INTERNAL.asRuntimeException());
        }
    }

    @Override
    public void unfollowBooking(NotificationRequest notificationRequest, StreamObserver<Empty> empty) {
        System.out.println("UNFOLLOW BOOKING " + notificationRequest.getAllFields().toString());
        try {
            park.unfollowBooking(notificationRequest.getRideName(), notificationRequest.getDayOfYear(), UUID.fromString(notificationRequest.getUserId()));
            empty.onNext(Empty.newBuilder().build());
            empty.onCompleted();
        } catch (IllegalArgumentException e) {
            empty.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
    }
}
