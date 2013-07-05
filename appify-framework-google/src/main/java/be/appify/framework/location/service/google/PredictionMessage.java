package be.appify.framework.location.service.google;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.api.client.util.Key;

@XmlRootElement
public class PredictionMessage {
	@Key
	private List<Prediction> predictions;

	@XmlElement
	public List<Prediction> getPredictions() {
		return predictions;
	}

	public void setPredictions(List<Prediction> predictions) {
		this.predictions = predictions;
	}

}
