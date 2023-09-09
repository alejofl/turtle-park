package ar.edu.itba.pod.servant;

import ar.edu.itba.pod.data.Booking;
import ar.edu.itba.pod.data.Park;
import ar.edu.itba.pod.query.*;
import ar.edu.itba.pod.server.Util;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.stream.Collectors;

public class QueryServant extends QueryServiceGrpc.QueryServiceImplBase {
    Park park = Park.getInstance();

    private SuggestedCapacity getSuggestedCapacityInformation(ar.edu.itba.pod.data.SuggestedCapacityInformation data) {
        SuggestedCapacity.Builder builder = SuggestedCapacity.newBuilder()
                .setRideName(data.rideName())
                .setSuggestedCapacity(data.suggestedCapacity())
                .setSlot(data.slot().format(Util.TIME_FORMATTER));
        return builder.build();
    }

    private ConfirmedBooking getConfirmedBookingsInformation(Booking booking) {
        ConfirmedBooking.Builder builder = ConfirmedBooking.newBuilder()
                .setRideName(booking.getRideName())
                .setUserId(booking.getVisitor().getId().toString())
                .setSlot(booking.getSlot().format(Util.TIME_FORMATTER));

        return builder.build();
    }
    @Override
    public void getSuggestedCapacities(QueryRequest queryRequest, StreamObserver<SuggestedCapacityResponse> suggestedCapacityResponse) {
        try {
            SuggestedCapacityResponse.Builder response = SuggestedCapacityResponse.newBuilder();
            response.addAllCapacities(
                    park.getSuggestedCapacities(queryRequest.getDayOfYear())
                            .stream()
                            .map(this::getSuggestedCapacityInformation).collect(Collectors.toList())
            );
            suggestedCapacityResponse.onNext(response.build());
            suggestedCapacityResponse.onCompleted();
        } catch (IllegalArgumentException e) {
            suggestedCapacityResponse.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
    }

    @Override
    public void getConfirmedBookings(QueryRequest queryRequest, StreamObserver<ConfirmedBookingResponse> confirmedBookingResponse) {
        try {
            ConfirmedBookingResponse.Builder response = ConfirmedBookingResponse.newBuilder();
            response.addAllBookings(
                    park.getConfirmedBookings(queryRequest.getDayOfYear())
                            .stream()
                            .map(this::getConfirmedBookingsInformation).collect(Collectors.toList())
            );
            confirmedBookingResponse.onNext(response.build());
            confirmedBookingResponse.onCompleted();
        } catch (IllegalArgumentException e) {
            confirmedBookingResponse.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
    }
}
