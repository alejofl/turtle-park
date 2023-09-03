package ar.edu.itba.pod.servant;

import ar.edu.itba.pod.book.AvailabilityRequest;
import ar.edu.itba.pod.book.BookServiceGrpc.BookServiceImplBase;
import ar.edu.itba.pod.book.BookingRequest;
import ar.edu.itba.pod.book.BookingResponse;
import ar.edu.itba.pod.book.RideResponse;
import ar.edu.itba.pod.commons.Empty;
import io.grpc.stub.StreamObserver;

public class BookServant extends BookServiceImplBase {
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
        //TODO
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
