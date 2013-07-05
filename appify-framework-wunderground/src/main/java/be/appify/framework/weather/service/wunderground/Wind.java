package be.appify.framework.weather.service.wunderground;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.api.client.util.Key;

@XmlType
public class Wind {
	@Key
	private int kph;
	@Key
	private String dir;

	@XmlElement
	public int getKph() {
		return kph;
	}

	public void setKph(int speed) {
		this.kph = speed;
	}

	@XmlElement(name = "dir")
	public String getDir() {
		return dir;
	}

	public void setDir(String direction) {
		this.dir = direction;
	}
}
