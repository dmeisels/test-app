package com.dmm.calendar.model;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javafx.beans.property.SimpleStringProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;
import net.sourceforge.zmanim.hebrewcalendar.JewishDate;

/**
 * Model class for an Event.
 *
 */
@XmlRootElement(name="Event")
@XmlType(propOrder = {"date", "jewishDate",  "description", "eventType", "recurrence" })
public class CalendarEvent {
	public static final int ENGLISH_EVENT = 0; 
	public static final int JEWISH_EVENT = 1; 
	public static final int YEARLY = 0; 
	public static final int MONTHLY = 1; 
	public static final int WEEKLY = 2; 
	public static final int DAILY = 3; 

	private SimpleStringProperty desc;
	private Calendar date;
	private JewishDate jewishDate;
	private int eventType;
	private int recurrence;
	
//	@XmlJavaTypeAdapter(SimpleStringPropertyFormatterAdapter.class)
	public String getDescription() {return desc.get();}
	@XmlElement(name="Description")
	public void setDescription(String desc) {this.desc.set(desc);}
//	public void setDesc(SimpleStringProperty desc) {this.desc=desc;}
	public SimpleStringProperty descriptionProperty() {return desc;}
	
	public Calendar getDate() {return date;}
	public String getDisplayDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date.getTime());
	}
	@XmlElement(name="Date")
	public void setDate(Calendar date) {this.date = date;}
	public void setDate(Date date) {setDate(date, eventType);}
	public void setDate(Date date, int eventType) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		this.date = cal;
		if (eventType==JEWISH_EVENT)
			setJewishDate(date);
	}

	@XmlJavaTypeAdapter(JewishDateFormatterAdapter.class)
	public JewishDate getJewishDate() {return jewishDate;}
	public String getJewishDisplayDate() {
		HebrewDateFormatter hdf = new HebrewDateFormatter();
		hdf.setHebrewFormat(true);
		return reverseWords(hdf.format(jewishDate));
	}
	@XmlElement(name="JewishDate", nillable=true)
	public void setJewishDate(Date date) {this.jewishDate.setDate(date);	}
	public void setJewishDate(JewishDate jDate) {setJewishDate(jDate, eventType);}
	public void setJewishDate(JewishDate jDate, int type) {
		this.jewishDate = jDate;
		if (eventType==JEWISH_EVENT)
			setDate(jDate.getTime());
	}
	
	public int getEventType() {return eventType;}
	@XmlElement(name="Type")
	public void setEventType(int eventType) {this.eventType = eventType;}
	public boolean isJewishEvent() {return this.eventType == CalendarEvent.JEWISH_EVENT;}
	
	public int getRecurrence() {return recurrence;}
	@XmlElement(name="Recurrence")
	public void setRecurrence(int recurrence) {this.recurrence = recurrence;}
	
	public boolean isYearlyEvent() {return this.recurrence == CalendarEvent.YEARLY;}
	
	private String reverseWords(String sentence) {
		StringBuilder sb = new StringBuilder(sentence);
		return sb.reverse().toString();
	}
	
	public CalendarEvent() {
		this.date = Calendar.getInstance();
		this.jewishDate = new JewishDate();
		this.desc = new SimpleStringProperty();
	}
	
	/**
	 * Constructor with some initial data.
	 * 
	 * @param firstName
	 * @param lastName
	 */
	public CalendarEvent(Calendar date, String desc) {
		this.date = date;
		this.jewishDate = new JewishDate(date);
		this.desc = new SimpleStringProperty(desc);
		this.eventType = ENGLISH_EVENT;
		this.recurrence = YEARLY;
	}
	
	public CalendarEvent(Calendar date, String desc, int eventType) {
		this.date = date;
		this.jewishDate = new JewishDate(date);
		this.desc = new SimpleStringProperty(desc);
		this.eventType = eventType;
		this.recurrence = YEARLY;
	}
	
	private static class JewishDateFormatterAdapter extends XmlAdapter<String, JewishDate> {

		//* XML => Java
        @Override
        public JewishDate unmarshal(final String v) throws Exception {
        	String[] tokens = v.split("-");
        	JewishDate jd=null;
        	if (tokens!=null && tokens.length==3) {
	        	int month = Integer.parseInt(tokens[0]);
	        	int date = Integer.parseInt(tokens[1]);
	        	int year = Integer.parseInt(tokens[2]);
	        	jd = new JewishDate(year, month, date);
        	}
            return jd;
        }
        
        //* Java => XML
        @Override
        public String marshal(final JewishDate jd) throws Exception {
        	return String.format("%02d-%02d-%04d",
        			jd.getJewishMonth(), jd.getJewishDayOfMonth(), jd.getJewishYear());
        }
    }

/*
	private static class SimpleStringPropertyFormatterAdapter extends XmlAdapter<String, SimpleStringProperty> {
		
		//* XML => Java
        @Override
        public SimpleStringProperty unmarshal(final String v) throws Exception {
            return new SimpleStringProperty(v);
        }
        
        //* Java => XML
        @Override
        public String marshal(final SimpleStringProperty prop) throws Exception {
        	return prop.get();
        }
	}
*/
}

