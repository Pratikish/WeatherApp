
package com.weatherApp.App.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.weatherApp.App.model.WeatherData;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {

	// List of temperatures for today for a particular city
	@Query("SELECT w.temperature FROM WeatherData w WHERE w.city = :city AND DATE(w.timestamp) = CURRENT_DATE")
	List<Double> findTemperaturesForToday(@Param("city") String city);

	// Average temperatures for the last 7 days for a particular city
	@Query(value = "SELECT AVG(w.temperature), DATE(w.timestamp) " + "FROM weather_data w " + "WHERE w.city = :city "
			+ "AND w.timestamp >= CURRENT_DATE - INTERVAL 7 DAY " + "GROUP BY DATE(w.timestamp) "
			+ "ORDER BY DATE(w.timestamp)", nativeQuery = true)
	List<Object[]> findAverageTemperaturesForLast7Days(@Param("city") String city);

}
