package com.weatherApp.App.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weatherApp.App.model.WeatherData;
import com.weatherApp.App.service.WeatherService;
import com.weatherApp.service.AlertService;

@RestController
@RequestMapping("/weather")
public class AppController {

	@Autowired
	private final WeatherService weatherService;

	@Autowired
	private final AlertService alertService;

	public AppController(WeatherService weatherService, AlertService alertService) {
		this.weatherService = weatherService;
		this.alertService = alertService;
	}

	@GetMapping("/alerts")
	public String checkAlerts() {
		return alertService.checkForAlerts();

	}

	@GetMapping("/current")
	public List<WeatherData> getCurrentWeatherData() {
		return weatherService.fetchWeatherData();
	}

	@GetMapping("/dailySummary")
	public List<Double> getDailySummary(@RequestParam String city) {
		return weatherService.getDailySummary(city);
	}

	@GetMapping("/last7days")
	public List<Object[]> getLast7DaysAverage(@RequestParam String city) {
		return weatherService.getLast7DaysAverages(city);
	}

}
