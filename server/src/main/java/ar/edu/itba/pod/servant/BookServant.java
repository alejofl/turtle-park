package ar.edu.itba.pod.servant;

import ar.edu.itba.pod.book.*;
import ar.edu.itba.pod.book.BookServiceGrpc.BookServiceImplBase;
import ar.edu.itba.pod.commons.Empty;
import ar.edu.itba.pod.data.Park;
import ar.edu.itba.pod.server.Util;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.UUID;

public class BookServant extends BookServiceImplBase {

    private Park park = Park.getInstance();

    @Override
    public void getRides(Empty empty, StreamObserver<RideResponse> rideResponse) {
        //TODO
    }

    @Override
    public void getAvailability(AvailabilityRequest availabilityRequest, StreamObserver<Empty> empty) {
        //TODO
    }

    @Override
    public void bookRide(BookingRequest bookingRequest, StreamObserver<BookingResponse> bookingResponse) {
        try {
            boolean didBook = park.bookRide(
                    bookingRequest.getRideName(),
                    bookingRequest.getDayOfYear(),
                    Util.checkTimeFormat(bookingRequest.getSlot()).orElseThrow(IllegalArgumentException::new),
                    UUID.fromString(bookingRequest.getUserId())
            );
            BookingResponse response = BookingResponse.newBuilder().setStatus(didBook ? BookingStatus.CONFIRMED : BookingStatus.PENDING).build();
            bookingResponse.onNext(response);
            bookingResponse.onCompleted();
        } catch (IllegalArgumentException e) {
            bookingResponse.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        } catch (IllegalStateException e) {
            bookingResponse.onError(Status.RESOURCE_EXHAUSTED.asRuntimeException());
        }
    }

    @Override
    public void confirmBooking(BookingRequest bookingRequest, StreamObserver<Empty> empty) {
        //TODO
    }

    @Override
    public void cancelBooking(BookingRequest bookingRequest, StreamObserver<Empty> empty) {
        //TODO
    }
}
