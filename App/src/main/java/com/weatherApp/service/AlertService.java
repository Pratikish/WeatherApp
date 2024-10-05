package com.weatherApp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.weatherApp.App.model.WeatherData;
import com.weatherApp.App.repository.WeatherDataRepository;

@Service
public class AlertService {

	private final WeatherDataRepository weatherDataRepository;

	public AlertService(WeatherDataRepository weatherDataRepository) {
		this.weatherDataRepository = weatherDataRepository;
	}

	public String checkForAlerts() {

		List<WeatherData> latestWeatherData = weatherDataRepository.findAll();

		for (WeatherData data : latestWeatherData) {

			if (data.getTemperature() > 30) {
				return sendAlert("High temperature alert in " + data.getCity() + ": " + data.getTemperature() + "°C");
			}
			if (data.getTemperature() < 5) {
				return sendAlert("Low temperature alert in " + data.getCity() + ": " + data.getTemperature() + "°C");

			}

		}
		return "No extreme temperatures";
	}

	private String sendAlert(String message) {

		return "ALERT: " + message;
	}

}
