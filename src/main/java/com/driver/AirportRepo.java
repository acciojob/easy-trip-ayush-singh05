package com.driver;

import com.driver.controllers.AirportController;
import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Repository
public class AirportRepo {
    HashMap<City, Airport> airportDB = new HashMap<>();
    HashMap<Integer,Flight> flightDB = new HashMap<>();
    HashMap<Integer,Passenger> passangerdDB = new HashMap<>();
    HashMap<Integer, Set<Integer>>ticketDB = new HashMap<>();


    public void addAirport(Airport airport)  {
        City name = airport.getCity();
        if(airportDB.containsKey(name)){
           return;
        }
        airportDB.put(name,airport);
    }
    public  String getLargestAirportName() {
        String airport_name = "";
        int largest = 0;
        for(City ct : airportDB.keySet()) {
            Airport ap = airportDB.get(ct);
            if(ap.getNoOfTerminals() > largest){
                airport_name = ap.getAirportName();
                largest = ap.getNoOfTerminals();
            }else if(ap.getNoOfTerminals() == largest) {
                int res = ap.getAirportName().compareTo(airport_name);
                if(res < 0){
                    airport_name = ap.getAirportName();
                }
            }
        }
        return airport_name;

    }

    public void addFlight(Flight flight)  {
        int id = flight.getFlightId();
        if(flightDB.containsKey(id)) {
            return;
        }
        flightDB.put(id,flight);
    }

    public void addPassenger(Passenger passenger) {
        int passengerId = passenger.getPassengerId();
        if(passangerdDB.containsKey(passengerId)){
            return;
        }
        passangerdDB.put(passengerId,passenger);
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity) {
        double sortestDur = 25.5;
        boolean flag = true;
        for(int key : flightDB.keySet()) {
            Flight fl = flightDB.get(key);
            if(fl.getFromCity().equals(fromCity) && fl.getToCity().equals(toCity)) {
                sortestDur = Math.min(sortestDur,fl.getDuration());
                flag = false;
            }
        }
        if(flag)return -1;
        return sortestDur;
    }

    public String bookATicket(Integer flightId, Integer passengerId) {
        if(ticketDB.containsKey(flightId)) {
            Set<Integer> pl = ticketDB.get(flightId);
            if(pl.size() > flightDB.get(flightId).getMaxCapacity()){
                return "FAILURE";
            }else if(pl.contains(passengerId)) {
                return "FAILURE";
            }else {
                pl.add(passengerId);
                ticketDB.put(flightId,pl);
            }
        }else {
            Set<Integer> pl = new HashSet<>();
            pl.add(passengerId);
            ticketDB.put(flightId,pl);
        }
        return "SUCCESS";
    }

    public String cancelATicket(Integer flightId, Integer passengerId) {
        if(ticketDB.containsKey(flightId)) {
            if(ticketDB.get(flightId).contains(passengerId))
               ticketDB.get(flightId). remove(passengerId);
               return "SUCCESS";
        }else {
            return "FAILURE";
        }
    }

    public String getAirportNameFromFlightId(Integer flightId) {
        Flight fl = flightDB.get(flightId);
        if(fl == null){
            return null;
        }
        City ct = fl.getFromCity();
        if(airportDB.containsKey(ct)){
            return airportDB.get(ct).getAirportName();
        }
        return null;
    }

    public int calculateFlightFare(Integer flightId) {
        int noOfPeople = ticketDB.get(flightId).size();
        return 3000+(noOfPeople*50);
    }

    public int getNumberOfPeopleOn(Date date, String airportName) {
//        for(City city : airportDB.keySet()) {
//            Airport ap = airportDB.get(city);
//            if(ap.getAirportName().equals(airportName)) {
//
//            }
//        }
        return 0;
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {
        int cnt = 0;
        for(int id : flightDB.keySet()) {
           Set<Integer>st = ticketDB.get(id);
           if(st.contains(passengerId)) {
               cnt++;
           }
        }
        return cnt;
    }

    public int calculateRevenueOfAFlight(Integer flightId) {
        int sz = ticketDB.get(flightId).size();
        int rate = 3000 * sz;
        int total_revenue = 0;
        for(int i = 0; i < sz-1 ; i++) {
            total_revenue += 50;
        }
        return total_revenue+rate;
    }
}
