package com.tourism.backend.convert;

import com.tourism.backend.dto.response.BookingFlightDTO;
import com.tourism.backend.entity.DepartureTransport;
import com.tourism.backend.entity.TourDeparture;

public class BookingFlightConverter {
    private BookingFlightDTO mapToFlightDTO(DepartureTransport transport) {
        BookingFlightDTO flight = new BookingFlightDTO();
        flight.setTransportCode(transport.getTransportCode());
        flight.setDepartTime(transport.getDepartTime());
        flight.setArrivalTime(transport.getArrivalTime());
        flight.setStartPoint(transport.getStartPoint());
        flight.setEndPoint(transport.getEndPoint());
        return flight;
    }
}
