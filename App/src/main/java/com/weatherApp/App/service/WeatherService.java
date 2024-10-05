package com.weatherApp.App.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherApp.App.model.Geolocation;
import com.weatherApp.App.model.WeatherData;
import com.weatherApp.App.repository.WeatherDataRepository;

import reactor.core.publisher.Mono;

@Service
public class WeatherService {

	@Value("${weather.api.key}")
	private String apiKey;

	@Value("${weather.api.url}")
	private String apiUrl;

	@Value("${weather.geo.url}")
	private String geoApiUrl;

	private final WebClient webClient;
	private final WeatherDataRepository weatherDataRepository;

	public WeatherService(WeatherDataRepository weatherDataRepository) {
		this.webClient = WebClient.builder().build();
		this.weatherDataRepository = weatherDataRepository;
	}

	private static final List<String> CITIES = Arrays.asList("Delhi", "Mumbai", "Chennai", "Bangalore", "Kolkata",
			"Hyderabad");

	@Scheduled(fixedRateString = "${weather.update.interval}")
	public List<WeatherData> fetchWeatherData() {
		List<WeatherData> weatherDataList = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper(); // Create an ObjectMapper instance

		for (String city : CITIES) {
			String geoUrl = String.format("%s?q=%s&appid=%s", geoApiUrl, city, apiKey);

			Mono<Geolocation[]> geoDataMono = webClient.get().uri(geoUrl).retrieve().bodyToMono(Geolocation[].class);
			Geolocation[] geoDataArray = geoDataMono.block();

			if (geoDataArray != null && geoDataArray.length > 0) {
				Geolocation geo = geoDataArray[0];

				String lat = String.valueOf(geo.getLat());
				String lon = String.valueOf(geo.getLon());

				// Construct the weather API URL with latitude and longitude
				String weatherUrl = String.format("%s?lat=%s&lon=%s&appid=%s", apiUrl, lat, lon, apiKey);

				// Fetch weather data as a raw JSON string
				Mono<String> weatherDataJsonMono = webClient.get().uri(weatherUrl).retrieve().bodyToMono(String.class);
				String weatherDataJson = weatherDataJsonMono.block();

				if (weatherDataJson != null) {
					try {
						// Parse the JSON response
						JsonNode root = objectMapper.readTree(weatherDataJson);

						String mainWeather = root.path("weather").get(0).path("main").asText();

						JsonNode mainNode = root.path("main");
						double temperature = mainNode.path("temp").asDouble() - 273.15;
						double feelsLike = mainNode.path("feels_like").asDouble() - 273.15;

						long unixTimestamp = root.path("dt").asLong();
						LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTimestamp),
								ZoneId.systemDefault());

						WeatherData weatherData = new WeatherData();
						weatherData.setMainCondition(mainWeather);
						weatherData.setCity(city);
						weatherData.setTemperature(temperature);
						weatherData.setFeelsLike(feelsLike);
						weatherData.setTimestamp(dateTime);

						weatherDataRepository.save(weatherData);

						weatherDataList.add(weatherData);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		return weatherDataList;
	}

	public List<Double> getDailySummary(String city) {
		return weatherDataRepository.findTemperaturesForToday(city);
	}

	public List<Object[]> getLast7DaysAverages(String city) {
		return weatherDataRepository.findAverageTemperaturesForLast7Days(city);
	}

}
