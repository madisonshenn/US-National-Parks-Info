/**
 * This is the controller of the MVC.
 * It handles HTTP GET requests from users, calls the NationalParksModel
 * class to retrieve park data, and passes data to the appropriate JSP view
 * for rendering.
 */
package ds;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "NationalParksServlet", urlPatterns = {"/getNationalParksInfo"})
public class NationalParksServlet extends HttpServlet {
    private NationalParksModel model;
    private static final Map<String, Double[]> parkCoordinates = new HashMap<>();

    @Override
    public void init() {
        /**
         * Accepts a parkCode parameter from the request, and validates the park code and
         * retrieves the corresponding latitude and longitude.
         */
        model = new NationalParksModel();

        // Store lat/lon values for each park
        parkCoordinates.put("acad", new Double[]{44.3962, -68.2246}); // Acadia NP
        parkCoordinates.put("cuva", new Double[]{41.0969, -81.4611}); // Cuyahoga Valley NP
        parkCoordinates.put("grsm", new Double[]{35.726, -83.482}); // Great Smoky Mountains NP
        parkCoordinates.put("maca", new Double[]{37.1246, -86.0968}); // Mammoth Cave NP
        parkCoordinates.put("neri", new Double[]{37.9263, -81.1547}); // New River Gorge NP
        parkCoordinates.put("shen", new Double[]{38.6633, -78.4635}); // Shenandoah NP
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /**
         * It calls model functions to fetch park image, weather information,
         * and park activities. And then it sets the retrieved data as request
         * attributes, and forwards the request to result.jsp for display.
         * If an invalid parkCode is provided, redirects to prompt.jsp with an
         * error message.
         */
        String parkCode = request.getParameter("parkCode");
        String nextView;

        if (parkCode != null) {
            String parkName;
            Double[] coordinates = parkCoordinates.get(parkCode);

            if (coordinates == null) {
                request.setAttribute("error", "Invalid park code.");
                nextView = "/WEB-INF/prompt.jsp";
            } else {
                double lat = coordinates[0];
                double lon = coordinates[1];
                // Fetch image from NPS site
                String imageUrl = model.getParkImage(parkCode);

                // Fetch weather data
                String weatherData = model.getWeatherConditions(lat, lon);

                // Fetch activities from NPS API
                String activities = model.getParkActivities(parkCode);

                request.setAttribute("parkName", model.getParkName(parkCode));
                request.setAttribute("imageURL", imageUrl);
                request.setAttribute("weather", weatherData);
                request.setAttribute("activities", activities);
                nextView = "/WEB-INF/result.jsp";
            }
        } else {
            nextView = "/WEB-INF/prompt.jsp";
        }

        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);
    }
}