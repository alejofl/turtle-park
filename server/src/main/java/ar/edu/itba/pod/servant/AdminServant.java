package ar.edu.itba.pod.servant;

import ar.edu.itba.pod.admin.AdminServiceGrpc.AdminServiceImplBase;
import ar.edu.itba.pod.admin.CapacityRequest;
import ar.edu.itba.pod.admin.PassRequest;
import ar.edu.itba.pod.admin.RideRequest;
import ar.edu.itba.pod.commons.Empty;
import ar.edu.itba.pod.data.Park;
import ar.edu.itba.pod.server.Util;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.time.LocalTime;
import java.util.UUID;

public class AdminServant extends AdminServiceImplBase {

    private Park park = Park.getInstance();

    @Override
    public void addRide(RideRequest rideRequest, StreamObserver<Empty> empty) {
        try {
            park.addRide(
                    rideRequest.getRideName(),
                    Util.checkTimeFormat(rideRequest.getOpeningTime()).orElseThrow(IllegalArgumentException::new),
                    Util.checkTimeFormat(rideRequest.getClosingTime()).orElseThrow(IllegalArgumentException::new),
                    rideRequest.getSlotSize()
            );
            empty.onNext(Empty.newBuilder().build());
            empty.onCompleted();
        } catch (IllegalArgumentException e) {
            empty.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
    }

    @Override
    public void addPass(PassRequest passRequest, StreamObserver<Empty> empty) {
        try {
            park.addPass(
                    UUID.fromString(passRequest.getUserId()),
                    passRequest.getType(),
                    passRequest.getDayOfYear()
            );
            empty.onNext(Empty.newBuilder().build());
            empty.onCompleted();
        } catch (IllegalArgumentException e) {
            empty.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
    }

    @Override
    public void loadRideCapacity(CapacityRequest capacityRequest, StreamObserver<Empty> empty) {
        try {
            park.loadRideCapacity(
                    capacityRequest.getRideName(),
                    capacityRequest.getDayOfYear(),
                    capacityRequest.getCapacity()
            );
            empty.onNext(Empty.newBuilder().build());
            empty.onCompleted();
        } catch (IllegalArgumentException e) {
            empty.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
    }
}
