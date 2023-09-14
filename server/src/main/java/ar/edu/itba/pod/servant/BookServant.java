package ar.edu.itba.pod.servant;

import ar.edu.itba.pod.book.*;
import ar.edu.itba.pod.book.BookServiceGrpc.BookServiceImplBase;
import ar.edu.itba.pod.commons.Empty;
import ar.edu.itba.pod.data.Park;
import ar.edu.itba.pod.server.Util;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.UUID;
import java.util.stream.Collectors;

public class BookServant extends BookServiceImplBase {

    private final Park park = Park.getInstance();

    @Override
    public void getRides(Empty empty, StreamObserver<RideResponse> rideResponse) {
        RideResponse response = RideResponse.newBuilder().addAllRides(
                park.getRides().stream().map(ride -> RideInformation
                        .newBuilder()
                        .setRideName(ride.getName())
                        .setOpeningTime(ride.getOpeningTime().format(Util.TIME_FORMATTER))
                        .setClosingTime(ride.getClosingTime().format(Util.TIME_FORMATTER))
                        .build()).collect(Collectors.toList())
        ).build();
        rideResponse.onNext(response);
        rideResponse.onCompleted();
    }

    private AvailabilityInformation getAvailabilityInformationFromData(ar.edu.itba.pod.data.AvailabilityInformation data) {
        AvailabilityInformation.Builder builder = AvailabilityInformation
                .newBuilder()
                .setRideName(data.rideName())
                .setConfirmedBookings(data.confirmedBookings())
                .setPendingBookings(data.pendingBookings())
                .setSlot(data.slot().format(Util.TIME_FORMATTER));
        data.capacity().ifPresent(builder::setSlotCapacity);
        return builder.build();
    }

    @Override
    public void getAvailability(AvailabilityRequest availabilityRequest, StreamObserver<AvailabilityResponse> availabilityResponse) {
        try {
            AvailabilityResponse.Builder response = AvailabilityResponse.newBuilder();
            if (availabilityRequest.hasSingleSlotAvailability()) {
                park.getAvailabilityForSlot(
                        availabilityRequest.getSingleSlotAvailability().getRideName(),
                        availabilityRequest.getDayOfYear(),
                        Util.checkTimeFormat(availabilityRequest.getSingleSlotAvailability().getSlot()).orElseThrow(IllegalArgumentException::new)
                ).ifPresent(data -> response.addData(getAvailabilityInformationFromData(data)));
            } else if (availabilityRequest.hasMultipleSlotAvailability()) {
                response.addAllData(
                        park.getAvailabilityForSlot(
                                availabilityRequest.getMultipleSlotAvailability().getRideName(),
                                availabilityRequest.getDayOfYear(),
                                Util.checkTimeFormat(availabilityRequest.getMultipleSlotAvailability().getStartingSlot()).orElseThrow(IllegalArgumentException::new),
                                Util.checkTimeFormat(availabilityRequest.getMultipleSlotAvailability().getEndingSlot()).orElseThrow(IllegalArgumentException::new)
                        ).stream().map(this::getAvailabilityInformationFromData).collect(Collectors.toList())
                );
            } else if (availabilityRequest.hasMultipleRidesAvailability()) {
                response.addAllData(
                        park.getAvailabilityForSlot(
                                availabilityRequest.getDayOfYear(),
                                Util.checkTimeFormat(availabilityRequest.getMultipleRidesAvailability().getStartingSlot()).orElseThrow(IllegalArgumentException::new),
                                Util.checkTimeFormat(availabilityRequest.getMultipleRidesAvailability().getEndingSlot()).orElseThrow(IllegalArgumentException::new)
                        ).stream().map(this::getAvailabilityInformationFromData).collect(Collectors.toList())
                );
            } else {
                throw new IllegalArgumentException();
            }
            availabilityResponse.onNext(response.build());
            availabilityResponse.onCompleted();
        } catch (IllegalArgumentException e) {
            availabilityResponse.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
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
        try {
            park.confirmBooking(
                    bookingRequest.getRideName(),
                    bookingRequest.getDayOfYear(),
                    Util.checkTimeFormat(bookingRequest.getSlot()).orElseThrow(IllegalArgumentException::new),
                    UUID.fromString(bookingRequest.getUserId())
            );
            empty.onNext(Empty.newBuilder().build());
            empty.onCompleted();
        } catch (IllegalArgumentException e) {
            empty.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
    }

    @Override
    public void cancelBooking(BookingRequest bookingRequest, StreamObserver<Empty> empty) {
        try {
            park.cancelBooking(
                    bookingRequest.getRideName(),
                    bookingRequest.getDayOfYear(),
                    Util.checkTimeFormat(bookingRequest.getSlot()).orElseThrow(IllegalArgumentException::new),
                    UUID.fromString(bookingRequest.getUserId())
            );
            empty.onNext(Empty.newBuilder().build());
            empty.onCompleted();
        } catch (IllegalArgumentException e) {
            empty.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
    }
}
