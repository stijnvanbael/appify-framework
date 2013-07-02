package be.appify.framework.weather.service.wunderground;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.api.client.util.Key;

@XmlType
public class Date {
	@Key
	private int day;
	@Key
	private int month;
	@Key
	private int year;

	@XmlElement
	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	@XmlElement
	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	@XmlElement
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

}
