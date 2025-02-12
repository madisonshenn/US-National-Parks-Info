<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
    <head>
        <title><%= request.getAttribute("parkName") %></title>
    </head>

    <style>
        body {
            font-family: Arial, sans-serif;
            text-align: left;
            margin-top: 20px;
        }
        h1, h2 {
            font-family: Times New Roman, serif;
            margin-bottom: 10px;
        }
        img {
            width: 100%;
            height: auto;
            max-width: 100vw;
        }
        .weather-conditions {
            margin-left: 40px;
            font-size: 15px;
            line-height: 1.5;
        }
        .activities-list {
            margin-left: 40px;
            font-size: 15px;
            line-height: 1.5;
        }
        .credit {
            font-family: Times New Roman, serif;
            margin-top: 10px;
        }
    </style>



    <body>
        <% String parkName = (String) request.getAttribute("parkName"); %>
        <% if (parkName != null) { %>
            <h1><%= parkName %></h1>
        <% } %>
        <% if (request.getAttribute("imageURL") != null) { %>
            <img src="<%= request.getAttribute("imageURL") %>" alt="Park Image">
            <p class="credit">Credit: <a href="https://www.nps.gov/<%= request.getAttribute("parkCode") %>/index.htm" target="_blank">www.nps.gov</a></p>
            <p><em><%= request.getAttribute("parkName") %> (<a href="https://www.nps.gov/media/multimedia-search.htm" target="_blank">https://www.nps.gov/media/multimedia-search.htm</a>).</em></p>
        <% } else { %>
            <h2>A picture of <%= request.getAttribute("parkName") %> could not be found</h2><br>
        <% } %>

        <h2>Current conditions:</h2>
        <% String weather = (String) request.getAttribute("weather"); %>
        <% if (weather != null) { %>
        <div class="weather-conditions">
            Temperature: <%= weather.split(",")[0].replace("<strong>Temperature:</strong>", "").replace("°F", "F").trim() %><br>
            Humidity : <%= weather.split(",")[1].replace("<strong>Humidity:</strong>", "").trim() %><br>
            Wind speed : <%= weather.split(",")[2].replace("<strong>Wind Speed:</strong>", "").replace(" mph", "").trim() %>
        </div>
        <% } else { %>
        <p>Error retrieving weather data</p>
        <% } %>
        <p class="credit">Credit: forecast.weather.gov (National Weather Service)</p>

        <h2><%= request.getAttribute("parkName") %> Activities:</h2>
        <%
            String activities = (String) request.getAttribute("activities");
            if (activities != null) {
                // Remove HTML tags and extract activity names
                activities = activities.replaceAll("</?ul>|</?li>", "");
                String[] activitiesArray = activities.split("\n");
                java.util.Arrays.sort(activitiesArray);
        %>
        <div class="activities-list">
            <% for (String activity : activitiesArray) {
                if (!activity.trim().isEmpty()) {
            %>
            <%= activity.trim() %><br>
            <%
                    }
                } %>
        </div>
        <% } %>
        <p class="credit">Credit: https://developer.nps.gov</p>
    </body>
</html>
