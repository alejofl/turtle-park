package ar.edu.itba.pod.servant;

import ar.edu.itba.pod.book.AvailabilityInformation;
import ar.edu.itba.pod.data.Park;
import ar.edu.itba.pod.data.SuggestedCapacityInformation;
import ar.edu.itba.pod.query.*;
import ar.edu.itba.pod.server.Util;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.UUID;
import java.util.stream.Collectors;

import static ar.edu.itba.pod.data.Park.park;

public class QueryServant extends QueryServiceGrpc.QueryServiceImplBase {
    Park park = Park.getInstance();

    private SuggestedCapacity getSuggestedCapacityInformation(ar.edu.itba.pod.data.SuggestedCapacityInformation data) {
        SuggestedCapacity.Builder builder = SuggestedCapacity.newBuilder()
                .setRideName(data.rideName())
                .setSuggestedCapacity(data.suggestedCapacity())
                .setSlot(data.slot().format(Util.TIME_FORMATTER));
        return builder.build();
    }
    @Override
    public void getSuggestedCapacities(QueryRequest queryRequest, StreamObserver<SuggestedCapacityResponse> suggestedCapacityResponse) {
        try {
            SuggestedCapacityResponse.Builder response = SuggestedCapacityResponse.newBuilder();
            response.addAllCapacities(
                    park.getSuggestedCapacities(queryRequest.getDayOfYear())
                            .stream()
                            .map(this::getSuggestedCapacityInformation).collect(Collectors.toList()));
            suggestedCapacityResponse.onNext(response.build());
            suggestedCapacityResponse.onCompleted();
        } catch (IllegalArgumentException e) {
            suggestedCapacityResponse.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
    }

    @Override
    public void getConfirmedBookings(QueryRequest queryRequest, StreamObserver<ConfirmedBookingResponse> confirmedBookingResponse) {
        //TODO
    }
}
