package ar.edu.itba.pod.servant;

import ar.edu.itba.pod.admin.AdminServiceGrpc.AdminServiceImplBase;
import ar.edu.itba.pod.admin.CapacityRequest;
import ar.edu.itba.pod.admin.PassRequest;
import ar.edu.itba.pod.admin.RideRequest;
import ar.edu.itba.pod.commons.Empty;
import io.grpc.stub.StreamObserver;

public class AdminServant extends AdminServiceImplBase {
    @Override
    public void addRide(RideRequest rideRequest, StreamObserver<Empty> empty) {
        //TODO
    }

    @Override
    public void addPass(PassRequest passRequest, StreamObserver<Empty> empty) {
        //TODO
    }

    @Override
    public void loadRideCapacity(CapacityRequest capacityRequest, StreamObserver<Empty> empty) {
        //TODO
    }
}
