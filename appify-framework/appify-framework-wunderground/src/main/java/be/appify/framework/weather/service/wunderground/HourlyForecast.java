package be.appify.framework.weather.service.wunderground;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.api.client.util.Key;

@XmlType
public class HourlyForecast {
	@Key
	private ForecastTime FCTTIME;
	@Key
	private ForecastValue temp;
	@Key
	private String icon;
	@Key
	private String sky;
	@Key
	private ForecastValue wspd;
	@Key
	private ForecastValue qpf;
	@Key
	private ForecastValue snow;

	@XmlElement
	public ForecastTime getFCTTIME() {
		return FCTTIME;
	}

	@XmlElement
	public String getIcon() {
		return icon;
	}

	@XmlElement
	public ForecastValue getQpf() {
		return qpf;
	}

	@XmlElement
	public String getSky() {
		return sky;
	}

	@XmlElement
	public ForecastValue getSnow() {
		return snow;
	}

	@XmlElement
	public ForecastValue getTemp() {
		return temp;
	}

	@XmlElement
	public ForecastValue getWspd() {
		return wspd;
	}

	public void setFCTTIME(ForecastTime fcttime) {
		this.FCTTIME = fcttime;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setQpf(ForecastValue qpf) {
		this.qpf = qpf;
	}

	public void setSky(String sky) {
		this.sky = sky;
	}

	public void setSnow(ForecastValue snow) {
		this.snow = snow;
	}

	public void setTemp(ForecastValue temp) {
		this.temp = temp;
	}

	public void setWspd(ForecastValue wspd) {
		this.wspd = wspd;
	}

}
