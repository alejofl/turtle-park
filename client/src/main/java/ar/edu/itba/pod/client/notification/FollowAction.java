package ar.edu.itba.pod.client.notification;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;
import ar.edu.itba.pod.notification.NotificationRequest;
import ar.edu.itba.pod.notification.NotificationResponse;
import ar.edu.itba.pod.notification.NotificationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.Iterator;
import java.util.List;

public class FollowAction extends Action {
    public FollowAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        NotificationServiceGrpc.NotificationServiceBlockingStub stub = NotificationServiceGrpc.newBlockingStub(channel);
        NotificationRequest request = NotificationRequest.newBuilder()
                .setDayOfYear(Integer.parseInt(System.getProperty("day")))
                .setRideName(System.getProperty("ride"))
                .setUserId(System.getProperty("visitor"))
                .build();
        try {
            Iterator<NotificationResponse> responseIterator = stub.followBooking(request);
            while (responseIterator.hasNext()) {
                NotificationResponse response = responseIterator.next();
                System.out.println(NotificationStatus.valueOf(response.getStatus().name()).consumeNotification(response));
            }
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.INVALID_ARGUMENT) {
                throw new IllegalArgumentException();
            } else if (e.getStatus().getCode() == Status.UNAVAILABLE.getCode()) {
                throw new ServerUnavailableException();
            }
            System.err.println(Util.GENERIC_ERROR_MESSAGE);
            System.exit(1);
        }
    }

    @Override
    public boolean hasValidArguments() {
        try {
            Integer.parseInt(System.getProperty("day"));
            return super.hasValidArguments();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getUsageMessage() {
        return """
                Usage:
                    $> ./notif-cli
                        -DserverAddress=xx.xx.xx.xx:yyyy
                        -Daction=follow
                        -Dday=dayOfYear
                        -Dride=rideName
                        -Dvisitor=visitorId
                """;
    }
}
