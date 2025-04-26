package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApp extends Application {
    private TextField citySearchField; 
    private Label weatherLabel, descriptionLabel, tempLabel, pressureLabel;


    private static final String API_KEY = "YOUR API KEY";// Add your own key here 

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Weather App Port from Python");
        primaryStage.setWidth(550);
        primaryStage.setHeight(370);


        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #ADD8E6;");


        Label titleLabel = new Label("Weather App");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        VBox.setMargin(titleLabel, new Insets(0, 0, 20, 0));


        citySearchField = new TextField();
        citySearchField.setPromptText("Enter city name");
        citySearchField.setMaxWidth(450);


        Button fetchButton = new Button("Fetch Weather");
        fetchButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");
        fetchButton.setOnAction(e -> fetchWeatherData());


        weatherLabel = createLabel("Weather Climate");
        descriptionLabel = createLabel("Weather Description");
        tempLabel = createLabel("Temperature (°C)");
        pressureLabel = createLabel("Pressure (hPa)");


        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(weatherLabel, 0, 0);
        gridPane.add(descriptionLabel, 1, 0);
        gridPane.add(tempLabel, 0, 1);
        gridPane.add(pressureLabel, 1, 1);


        root.getChildren().addAll(titleLabel, citySearchField, fetchButton, gridPane);


        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        return label;
    }

    private void fetchWeatherData() {
        String city = citySearchField.getText().trim(); 
        if (city.isEmpty()) {
            showAlert("Error", "Please enter a city name.");
            return;
        }

        try {
            // Construct the API URL
            String urlStr = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY;
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse the JSON response
            JSONObject json = new JSONObject(response.toString());
            JSONObject main = json.getJSONObject("main");
            JSONObject weather = json.getJSONArray("weather").getJSONObject(0);

            String weatherMain = weather.getString("main");
            String description = weather.getString("description");
            double temperature = main.getDouble("temp") - 273.15; // Convert Kelvin to Celsius
            int pressure = main.getInt("pressure");

            // Update the labels
            weatherLabel.setText("Weather Climate: " + weatherMain);
            descriptionLabel.setText("Description: " + description);
            tempLabel.setText("Temperature: " + String.format("%.1f °C", temperature));
            pressureLabel.setText("Pressure: " + pressure + " hPa");

        } catch (Exception e) {
            showAlert("Error", "Failed to fetch weather data. Please check the city name and try again.");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}