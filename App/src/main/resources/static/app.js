let weatherChart; // Declare weatherChart globally to manage its state

async function fetchWeatherData(city) {
    try {
        const response = await fetch(`http://localhost:8080/weather/dailySummary?city=${city}`);
        if (!response.ok) {
            throw new Error(`Error fetching weather data: ${response.statusText}`);
        }
        const data = await response.json();
        console.log(data); // Inspect the structure of your data
        createWeatherChart(data);
    } catch (error) {
        console.error(error);
    }
}

document.getElementById('cityForm').addEventListener('submit', function (event) {
    event.preventDefault(); // Prevent the default form submission
    const city = document.getElementById('cityInput').value; // Get the city input value
    fetchWeatherData(city); // Call the function with the city name
});

function createWeatherChart(temperatures) {
    // Generate x-axis labels based on the number of temperature entries
    const labels = Array.from({ length: temperatures.length }, (_, i) => `Reading ${i + 1}`); // Labels for each reading

    // Generate a unique color for each temperature
    const colors = temperatures.map((_, index) => {
        return `hsl(${(index * 360) / temperatures.length}, 100%, 50%)`; // Generate colors using HSL
    });

    const ctx = document.getElementById('weatherChart').getContext('2d');

    // If the chart already exists, destroy it
    if (weatherChart) {
        weatherChart.destroy();
    }

    // Create a new chart
    weatherChart = new Chart(ctx, {
        type: 'bar', 
        data: {
            labels: labels, 
            datasets: [{
                label: 'Temperature (°C)', 
                data: temperatures, 
                backgroundColor: colors, 
                borderColor: 'rgba(0, 0, 0, 0.2)', 
                borderWidth: 1, 
            }],
        },
        options: {
            scales: {
                x: {
                    title: {
                        display: true,
                        text: 'Readings',
                    },
                    barPercentage: 0.5, 
                },
                y: {
                    title: {
                        display: true,
                        text: 'Temperature (°C)', 
                    },
                    beginAtZero: true, 
                },
            },
            elements: {
                bar: {
                    borderWidth: 2, 
                    borderSkipped: 'bottom', 
                },
            },
        },
    });
}
