package be.appify.framework.weather.service.wunderground;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.api.client.util.Key;

@XmlType
public class ForecastDay {
	@Key
	private Date date;
	@Key
	private Temperature high;
	@Key
	private Temperature low;
	@Key
	private String icon;
	@Key
	private String skyicon;
	@Key
	private Precipitation qpf_day;
	@Key
	private Wind avewind;

	@XmlElement
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@XmlElement
	public Temperature getHigh() {
		return high;
	}

	public void setHigh(Temperature high) {
		this.high = high;
	}

	@XmlElement
	public Temperature getLow() {
		return low;
	}

	public void setLow(Temperature low) {
		this.low = low;
	}

	@XmlElement
	public String getIcon() {
		return icon;
	}

	public void setIcon(String condition) {
		this.icon = condition;
	}

	@XmlElement
	public String getSkyicon() {
		return skyicon;
	}

	public void setSkyicon(String sky) {
		this.skyicon = sky;
	}

	@XmlElement
	public Precipitation getQpf_day() {
		return qpf_day;
	}

	public void setQpf_day(Precipitation precipitation) {
		this.qpf_day = precipitation;
	}

	@XmlElement
	public Wind getAvewind() {
		return avewind;
	}

	public void setAvewind(Wind averageWind) {
		this.avewind = averageWind;
	}
}
