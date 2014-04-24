package com.dmm.calendar.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "EventList")
public class EventList {

    private List<CalendarEvent> eventList = new ArrayList<CalendarEvent>();
    
    public List<CalendarEvent> getEventList() {return eventList;}
    @XmlElement(name = "Event")
    public void setEventList(List<CalendarEvent> events) {this.eventList = events;}
 
    public EventList() {}
 
    public EventList(List<CalendarEvent> events) {this.eventList = events;}
}
