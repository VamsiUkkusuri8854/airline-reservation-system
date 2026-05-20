package com.airline.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AviationStackResponse {

    private List<FlightData> data;

    public List<FlightData> getData() {
        return data;
    }

    public void setData(List<FlightData> data) {
        this.data = data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FlightData {
        @JsonProperty("flight_date")
        private String flightDate;

        @JsonProperty("flight_status")
        private String flightStatus;

        private DepartureData departure;
        private ArrivalData arrival;
        private AirlineData airline;
        private FlightInfo flight;

        // Getters and Setters
        public String getFlightDate() { return flightDate; }
        public void setFlightDate(String flightDate) { this.flightDate = flightDate; }
        public String getFlightStatus() { return flightStatus; }
        public void setFlightStatus(String flightStatus) { this.flightStatus = flightStatus; }
        public DepartureData getDeparture() { return departure; }
        public void setDeparture(DepartureData departure) { this.departure = departure; }
        public ArrivalData getArrival() { return arrival; }
        public void setArrival(ArrivalData arrival) { this.arrival = arrival; }
        public AirlineData getAirline() { return airline; }
        public void setAirline(AirlineData airline) { this.airline = airline; }
        public FlightInfo getFlight() { return flight; }
        public void setFlight(FlightInfo flight) { this.flight = flight; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DepartureData {
        private String airport;
        private String iata;
        private String scheduled;

        public String getAirport() { return airport; }
        public void setAirport(String airport) { this.airport = airport; }
        public String getIata() { return iata; }
        public void setIata(String iata) { this.iata = iata; }
        public String getScheduled() { return scheduled; }
        public void setScheduled(String scheduled) { this.scheduled = scheduled; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ArrivalData {
        private String airport;
        private String iata;
        private String scheduled;

        public String getAirport() { return airport; }
        public void setAirport(String airport) { this.airport = airport; }
        public String getIata() { return iata; }
        public void setIata(String iata) { this.iata = iata; }
        public String getScheduled() { return scheduled; }
        public void setScheduled(String scheduled) { this.scheduled = scheduled; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AirlineData {
        private String name;
        private String iata;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getIata() { return iata; }
        public void setIata(String iata) { this.iata = iata; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FlightInfo {
        private String number;
        private String iata;

        public String getNumber() { return number; }
        public void setNumber(String number) { this.number = number; }
        public String getIata() { return iata; }
        public void setIata(String iata) { this.iata = iata; }
    }
}
