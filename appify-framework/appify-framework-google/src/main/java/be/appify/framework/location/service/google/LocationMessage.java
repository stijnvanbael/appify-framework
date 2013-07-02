package be.appify.framework.location.service.google;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.api.client.util.Key;

@XmlRootElement
public class LocationMessage {
	@Key
	private List<LocationResult> results;

	@XmlElement
	public List<LocationResult> getResults() {
		return results;
	}

	public void setResults(List<LocationResult> results) {
		this.results = results;
	}

}
