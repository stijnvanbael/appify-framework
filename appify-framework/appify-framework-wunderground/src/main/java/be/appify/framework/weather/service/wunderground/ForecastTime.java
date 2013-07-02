package be.appify.framework.weather.service.wunderground;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.api.client.util.Key;

@XmlType
public class ForecastTime {
	@Key
	private String hour;
	@Key
	private String year;
	@Key
	private String mon;
	@Key
	private String mday;

	@XmlElement
	public String getHour() {
		return hour;
	}

	@XmlElement
	public String getMday() {
		return mday;
	}

	@XmlElement
	public String getMon() {
		return mon;
	}

	@XmlElement
	public String getYear() {
		return year;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public void setMday(String mday) {
		this.mday = mday;
	}

	public void setMon(String mon) {
		this.mon = mon;
	}

	public void setYear(String year) {
		this.year = year;
	}
}
