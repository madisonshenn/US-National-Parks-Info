/**
 *  This is the model layer of MVC.
 *  It fetches park-related information using the National Park Service
 *  API, retrieves weather conditions from the National Weather Service,
 *  and scrapes images from the National Park Service website.
 */


package ds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class NationalParksModel {
    private static final Logger logger = Logger.getLogger(NationalParksModel.class.getName());

    // Base URLs for scraping data
    private static final String NPS_BASE_URL = "https://www.nps.gov/";
    private static final String WEATHER_BASE_URL = "https://forecast.weather.gov/MapClick.php?";
    private static final String NPS_API_URL = "https://developer.nps.gov/api/v1/parks?parkCode=";
    private static final String API_KEY = "7gLyxri27TsNH9Eqhe8mAWXNZ0B0D4RIfB9lhW52"; // Replace with your API key

    public String getParkName(String parkCode) {
        /**
         * This method returns the full name of the selected national park
         * based on the given park code.
         */
        switch (parkCode) {
            case "acad": return "Acadia National Park";
            case "cuva": return "Cuyahoga Valley National Park";
            case "grsm": return "Great Smoky Mountains National Park";
            case "maca": return "Mammoth Cave National Park";
            case "neri": return "New River Gorge National Park";
            case "shen": return "Shenandoah National Park";
            default: return "Unknown Park";
        }
    }

    public String getParkImage(String parkCode) throws IOException {
        /**
         *  This method scrapes the selected park's image from the NPS website.
         *  I referred to the codes from InterestingPicture on fetching images from
         *  NPS instead of Flicker.
         */

        String parkURL = NPS_BASE_URL + parkCode + "/index.htm";
        String htmlContent = fetch(parkURL);

        int cutLeft = htmlContent.indexOf("<meta property=\"og:image\" content=") + 35;
        int cutRight = htmlContent.indexOf("\"", cutLeft);
        return htmlContent.substring(cutLeft, cutRight);
    }

    // Fetches weather conditions from National Weather Service API
    public String getWeatherConditions(double lat, double lon) throws IOException {
        /**
         * This method retrieves real-time weather conditions from the National Weather Service API
         * using the specific lat and lon of the selected national park.
         * It looks for Temp as Temperature, Relh as Humidity, and Winds as Wind Speed from
         * for instance "https://forecast.weather.gov/MapClick.php?lat=38.6633&lon=-78.4635&FcstType=json"
         * for Shenandoah NP.
         */
        String weatherURL = "https://forecast.weather.gov/MapClick.php?lat=" + lat + "&lon=" + lon + "&FcstType=json";
        String jsonResponse = fetch(weatherURL);

        System.out.println("Weather API Response: " + jsonResponse);

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonObject currentObservation = jsonObject.getAsJsonObject("currentobservation");

            String temperature = currentObservation.get("Temp").getAsString();
            String humidity = currentObservation.get("Relh").getAsString();
            String rawWindSpeed = currentObservation.get("Winds").getAsString();

            String windCategory = getWindCategory(rawWindSpeed);

            return "<strong>Temperature:</strong> " + temperature + "°F, " +
                    "<strong>Humidity:</strong> " + humidity + "%, " +
                    "<strong>Wind Speed:</strong> " + windCategory;
        } catch (Exception e) {
            System.out.println("Weather API Parsing Error: " + e.getMessage());
            return "Error retrieving weather data";
        }
    }

    // LLM Self-reporting: I used ChatGPT to help convert numeric wind speeds
    // into descriptive categories (e.g., "Light Breeze", "Gale", "Hurricane Force")
    // based on a provided scale.
    public String getWindCategory(String windSpeedStr) {
        /**
         * This is a helpler.
         * This method has raw wind speed as a number from the weather forecast
         * website as input, and then group and return the wind category based on
         * the scale provided by "https://www.weather.gov/pqr/wind"
         */
        try {
            int windSpeed = Integer.parseInt(windSpeedStr.replaceAll("[^0-9]", "")); // Extract number

            if (windSpeed >= 1 && windSpeed <= 3) return "Light Air";
            else if (windSpeed >= 4 && windSpeed <= 7) return "Light Breeze";
            else if (windSpeed >= 8 && windSpeed <= 12) return "Gentle Breeze";
            else if (windSpeed >= 13 && windSpeed <= 18) return "Moderate Breeze";
            else if (windSpeed >= 19 && windSpeed <= 24) return "Fresh Breeze";
            else if (windSpeed >= 25 && windSpeed <= 31) return "Strong Breeze";
            else if (windSpeed >= 32 && windSpeed <= 38) return "Near Gale";
            else if (windSpeed >= 39 && windSpeed <= 46) return "Gale";
            else if (windSpeed >= 47 && windSpeed <= 54) return "Strong Gale";
            else if (windSpeed >= 55 && windSpeed <= 63) return "Whole Gale";
            else if (windSpeed >= 64 && windSpeed <= 75) return "Storm Force";
            else if (windSpeed > 75) return "Hurricane Force";

        } catch (NumberFormatException e) {
            return "Unknown";
        }

        return "Unknown";
    }



    // LLM Self-reporting: I used ChatGPT to remove unwanted bullet points under the activities section
    // on the output page, as well as making them in alphabetical order.
    public String getParkActivities(String parkCode) throws IOException {
        /**
         * This method fetches available activities from the NPS API based on the parkcode
         * of the selected national park. It fetched available activities from the website
         * as a list of string values.
         */
        String apiUrl = "https://developer.nps.gov/api/v1/parks?parkCode=" + parkCode + "&api_key=" + API_KEY;
        String jsonResponse = fetch(apiUrl);

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonObject parkData = jsonObject.getAsJsonArray("data").get(0).getAsJsonObject();

            StringBuilder activitiesList = new StringBuilder();
            for (var activity : parkData.getAsJsonArray("activities")) {
                String activityName = activity.getAsJsonObject().get("name").getAsString();
                activitiesList.append(activityName).append("\n");
            }

            return activitiesList.toString();
        } catch (Exception e) {
            System.out.println("Error parsing activities: " + e.getMessage());
            return "Error retrieving park activities";
        }
    }


    private String fetch(String urlString) throws IOException {
        /**
         * This method handles HTTP GET requests to external APIs.
         */
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();
        if (responseCode == 403) {
            return "Error: API access forbidden. Check API key or headers.";
        } else if (responseCode != 200) {
            return "Error: Received HTTP " + responseCode;
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();
        return response.toString();
    }

}
