package be.appify.framework.location.service.google;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.TimeoutException;

import org.junit.*;

import be.appify.framework.common.service.RemoteException;
import be.appify.framework.location.domain.Location;
import be.appify.framework.test.service.WebServiceClientTest;

import com.google.api.client.http.apache.ApacheHttpTransport;

public class GoogleLocationServiceTest extends WebServiceClientTest {
	private GoogleLocationService locationService;

	@Before
	public void before() {
		locationService = new GoogleLocationService(new ApacheHttpTransport(), "API-KEY");
		locationService.setDetailsUrl("http://localhost:" + PORT + "/maps/api/place/textsearch/json");
		addStubResponse("/maps/api/place/textsearch/json\\?.*query=Ruddervoorde.*",
				"/be/appify/framework/location/service/google/Ruddervoorde.json");
	}

	@Test
	public void testGetLocation() throws TimeoutException, RemoteException {
		Location location = locationService.getLocation("Ruddervoorde");
		assertNotNull(location);

		assertEquals("Ruddervoorde, Oostkamp, Belgium", location.getName());
		assertEquals(51.0959410, location.getLatitude(), 0.0000001);
		assertEquals(3.20651930, location.getLongitude(), 0.0000001);
	}
}