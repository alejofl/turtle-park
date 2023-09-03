package ar.edu.itba.pod.servant;

import ar.edu.itba.pod.query.ConfirmedBookingResponse;
import ar.edu.itba.pod.query.QueryRequest;
import ar.edu.itba.pod.query.QueryServiceGrpc;
import ar.edu.itba.pod.query.SuggestedCapacityResponse;
import io.grpc.stub.StreamObserver;

public class QueryServant extends QueryServiceGrpc.QueryServiceImplBase {
    @Override
    public void getSuggestedCapacities(QueryRequest queryRequest, StreamObserver<SuggestedCapacityResponse> suggestedCapacityResponse) {
        //TODO
    }

    @Override
    public void getConfirmedBookings(QueryRequest queryRequest, StreamObserver<ConfirmedBookingResponse> confirmedBookingResponse) {
        //TODO
    }
}
