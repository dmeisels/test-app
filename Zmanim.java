package com.dmm.calendar.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import net.sourceforge.zmanim.ComplexZmanimCalendar;
import net.sourceforge.zmanim.ZmanimCalendar;
import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;
import net.sourceforge.zmanim.util.GeoLocation;

public class Zmanim {

	protected HebrewDateFormatter hdf = new HebrewDateFormatter();
	private ZmanimCalendar zc;
	private JewishCalendar jcal;
	public JewishCalendar getJcal() {return jcal;}
	public void setJcal(JewishCalendar jcal) {this.jcal = jcal;}

	private boolean summer=true;
	
	public void setSummer(boolean summer) {this.summer = summer;}
	private boolean isSummer() {return this.summer;}

	public static final String parshaTitle = "";
	public static final String shachrisTitle = "שחרות‎";
	public static final String minchaTitle = "מנחה";
	public static final String mincha1Title = "מנחה בחול";
	public static final String mincha2Title = "מנחה עש״ק";
	public static final String mincha3Title = "מנחה עש״ק ב";
	public static final String maariv1Title = "מעריב";
	public static final String maariv2Title = "מעריב ב";
	public static final String candlesTitle = "נרות";
	public static final String tzaisTitle72 = "צאת / מוצש״ק";
	public static final String tzaisTitle = "צאת";
	public static final String dawnTitle = "עלות השחר‎";
	public static final String talisTitle = "הנחת טו״ת";
	public static final String sunriseTitle = "נץ החמה";
	public static final String latestShemaTitle = "סוף זמן קר״ש";
	public static final String latestShachrisTitle = "סוף זמן תפילה";
	public static final String midDayTitle = "חצות";
	public static final String minchaKetanaTitle = "מנחה קטנה";
	public static final String plagMinchaTitle = "מנחה פלג";
	public static final String sunsetTitle = " שקיעה‎ ";
	
	private static List<String> colList= new ArrayList<String>();
	
	{
		colList.add(parshaTitle); 
		colList.add(mincha1Title); 
		colList.add(mincha2Title);
		colList.add(maariv1Title); 
		colList.add(maariv2Title); 
		colList.add(candlesTitle); 
		colList.add(tzaisTitle);
		colList.add(dawnTitle); 
		colList.add(talisTitle);
		colList.add(sunriseTitle);
		colList.add(latestShemaTitle); 
		colList.add(latestShachrisTitle);
		colList.add(midDayTitle);
		colList.add(minchaKetanaTitle);
		colList.add(plagMinchaTitle);
		colList.add(sunsetTitle);	
	}
	
//	public Zmanim(ZmanimCalendar zc) {
//		this.zc = zc;
//		this.jcal=new JewishCalendar(zc.getCalendar());
//		this.hdf.setHebrewFormat(true);
//		this.hdf.setLongWeekFormat(false);
//		
//	}
	
	public Zmanim() {
		this(Calendar.getInstance(), getDefaultLocation());
	}
	

	public Zmanim(Calendar cal) {
		this(cal, getDefaultLocation());
	}

	public Zmanim(Calendar cal, GeoLocation location) {
        this.zc = new ZmanimCalendar(location);
        this.zc.setCalendar(cal);
        this.zc.setCandleLightingOffset(18);
        
		this.jcal=new JewishCalendar(zc.getCalendar());
		this.hdf.setHebrewFormat(true);
		this.hdf.setLongWeekFormat(false);
	}

	public Calendar getCalendar() {return this.zc.getCalendar();}
	public void setCalendar(Calendar cal) {zc.setCalendar(cal);}
	
	private static GeoLocation getDefaultLocation() {
        String locationName = "Monsey, NY";
        double latitude = 41.11121; 
        double longitude = -74.06848;
        double elevation = 166.71;
        TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
        return new GeoLocation(locationName, latitude, longitude, elevation, timeZone);
	}
	
	public String getParsha() {
		return hdf.formatParsha(jcal).trim();
	}		
	
	public String getYomTov() {
		return hdf.formatYomTov(jcal).trim();
	}
	
	public String getParshaExt() {
		StringBuffer sb = new StringBuffer();
		zc.getCalendar();
		sb.append(hdf.formatHebrewNumber(jcal.getJewishDayOfMonth()));
		sb.append(" ");
		sb.append(hdf.formatMonth(jcal).trim());
		sb.append("\n");
		if (jcal.getDayOfWeek()==7) {
			sb.append("  ");
			sb.append(hdf.formatParsha(jcal).trim());
		}
		if (jcal.isYomTov() || jcal.isTaanis()) {
			sb.append("  ");
			sb.append(hdf.formatYomTov(jcal).trim());
		}
//		if (jcal.isRoshChodesh()) {
//			sb.append(" ");
//			sb.append(hdf.formatRoshChodesh(jcal).trim());
//			JewishDate molad = jcal.getMolad();
//			sb.append(" מולד ");
//			sb.append(hdf.formatDayOfWeek(molad));
//			sb.append("  ");
//			sb.append(molad.getMoladHours());
//			sb.append(":");
//			sb.append(molad.getMoladMinutes());
//			sb.append(":");
//			sb.append(molad.getMoladChalakim());
//		}
		
		return sb.toString();
	}
	
	public String getMincha() {
		return String.format("%tl:%<tM", addMinuteRounded(zc.getSunset(), 30));
	}
	
	public Date getMincha1() {
		return addMinuteRounded(zc.getSunset(), 30);
	}
	
	public Date getMincha2() {
		return addMinuteRounded(zc.getCandleLighting(), -30);
	}
	
	public Date getMincha3() {
		return addMinuteRounded(zc.getCandleLighting(), 25);
	}
	
	public Date getMaariv2() {
		Calendar c = Calendar.getInstance();
		c.setTime(jcal.getTime());
		c.set(Calendar.HOUR_OF_DAY, 9);
		c.set(Calendar.MINUTE, 45);
		return c.getTime();
	}

	public String getMaariv() {
		return String.format("%tl:%<tM", addMinuteRounded(zc.getSunset(), 50));
	}
	
	public Date getMaariv1() { 
		return addMinuteRounded(zc.getSunset(), 50);
	}
	
	public Date getCandles() {
		return zc.getCandleLighting();
	}
	
	public String getCandlesAsString() {
		return String.format("%tl:%<tM", zc.getCandleLighting());
	}
	
	public Date getTzais() {
		return zc.getTzais();
	}
	
	public Date getTzais72() {
		return zc.getTzais72();
	}
	
	public Date getDawn() {
		return zc.getAlos72();	
	}
	
	public Date getTalis() {
//		ComplexZmanimCalendar czc = new ComplexZmanimCalendar(zc.getGeoLocation());
//		czc.getCalendar().setTime(zc.getCalendar().getTime());
//		return czc.getMisheyakir10Point2Degrees();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(zc.getAlos72().getTime());
		c.add(Calendar.MINUTE, 25);
		return c.getTime();	
	}
	
	public Date getSunrise() {
		return zc.getSunrise();	
	}
	
	public Date getLatestShema() {
		return zc.getSofZmanShmaMGA();
	}
	
	public Date getLatestShachris() {
		return zc.getSofZmanTfilaMGA();	
	}
	
	public Date getMidDay() {		
		return zc.getChatzos();	
	}
	
	public Date getMinchaKetana() {
		ComplexZmanimCalendar czc = new ComplexZmanimCalendar(zc.getGeoLocation());
		czc.getCalendar().setTime(zc.getCalendar().getTime());
//		System.out.println(String.format("Mincha Kitana for %tF is %tR / %tR / %tR", 
//				zc.getCalendar().getTime(), ,czc.getMinchaKetana16Point1Degrees(),zc.getMinchaKetana()));
		
		return czc.getMinchaKetana72Minutes();	
	}
	
	public Date getPlagMincha() {
		ComplexZmanimCalendar czc = new ComplexZmanimCalendar(zc.getGeoLocation());
		czc.getCalendar().setTime(zc.getCalendar().getTime());
		return czc.getPlagHamincha72Minutes();	
	}
	
	public Date getSunset() {
		return zc.getSunset();
	}
	
	public String getJewishYear() {
		return hdf.formatHebrewNumber(jcal.getJewishYear());		
	}
	
	public String getJewishDate() {
		return hdf.format(jcal);		
	}
	
	public String getJewishMonth() {
		return hdf.formatMonth(jcal);		
	}
	
	public String getJewishDayOfWeek() {
		return hdf.formatDayOfWeek(jcal);
	}
	
	public String getJewishDayOfMonth() {
		return hdf.formatHebrewNumber(jcal.getJewishDayOfMonth());
	}
	
	public static int getColumnCount() {
		return colList.size();
	}
	
	public static String getColumnHeader(int i) {
		return colList.get(i);
	}
	
	private Date addMinuteRounded(Date d, int m ) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(d.getTime());
		c.add(Calendar.MINUTE, m);
		int min = c.get(Calendar.MINUTE);
		if ((min % 5) <3)
			c.add(Calendar.MINUTE, (min % 5) * -1);
		else
			c.add(Calendar.MINUTE, 5 - (min % 5));
		return c.getTime();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("Date %tF",zc.getCalendar().getTime()));
		sb.append(String.format(" Mincha1 %tR",this.getMincha1()));
		sb.append(String.format(" Mincha2 %tR",this.getMincha2()));
		sb.append(String.format(" Maariv1 %tR",this.getMaariv1()));
		sb.append(String.format(" Maariv2 %tR",this.getMaariv2()));
		sb.append(String.format(" Candles %tR",this.getCandles()));
		sb.append(String.format(" Tzais %tR",this.getTzais()));
		sb.append(String.format(" Dawn %tR",this.getDawn()));
		sb.append(String.format(" Talis %tR",this.getTalis()));
		sb.append(String.format(" Sunrise %tR",this.getSunrise()));
		sb.append(String.format(" Latest Shema %tR",this.getLatestShema()));
		sb.append(String.format(" Latest Shachris %tR",this.getLatestShachris()));
		sb.append(String.format(" Midday %tR",this.getMidDay()));
		sb.append(String.format(" Mincha Kitana %tR",this.getMinchaKetana()));
		sb.append(String.format(" Plag Mincha %tR",this.getPlagMincha()));
		sb.append(String.format(" Sunset %tR",this.getSunset()));

		return sb.toString();
	}

}
