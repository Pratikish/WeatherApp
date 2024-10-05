import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.weatherApp.App.model.WeatherData;
import com.weatherApp.App.service.WeatherService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WeatherServiceIntegrationTest {

    @Autowired
    private WeatherService weatherService;

    @Test
    public void testApiConnection() {
        // Fetch weather data
        List<WeatherData> data = weatherService.fetchWeatherData();

        // Assert that data is not null
        assertNotNull(data, "Weather data should not be null");

        // Assert that the list is not empty before checking the first element
        assertTrue(data.size() > 0, "Weather data list should contain at least one entry");

        // Assert that the temperature is greater than 0 for the first entry
        assertTrue(data.get(0).getTemperature() > 0, "Temperature should be greater than 0");

        // Assert that the city name of the first entry is "Delhi"
        assertEquals("Delhi", data.get(0).getCity(), "The first city's name should be 'Delhi'");
    }
}
