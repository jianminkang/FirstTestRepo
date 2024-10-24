package com.microsoft.hackathon.quarkus;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/*
* The Demo resource should be mapped to the root path.
* Create a GET operation to return the value of a key passed as query parameter in the request.
* If the key is not passed, return "key not passed".
* If the key is passed, return "hello <key>".
*/

@Path("/")
public class DemoResource {
    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@QueryParam("key") String key) {
        if (key == null) {
            return "key not passed";
        } else {
            return "hello " + key;
        }
    }

    // New operation under /diffdates that calculates the difference between two
    // dates. The operation should receive two dates as parameter in format
    // dd-MM-yyyy and return the difference in days.

    @GET
    @Path("/diffdates")
    @Produces(MediaType.TEXT_PLAIN)
    public String diffdates(@QueryParam("date1") String date1, @QueryParam("date2") String date2) {
        Objects.requireNonNull(date1, "date1 must not be null");
        Objects.requireNonNull(date2, "date2 must not be null");
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date1Obj = dateFormat.parse(date1);
            Date date2Obj = dateFormat.parse(date2);
            long diffMillis = Math.abs(date1Obj.getTime() - date2Obj.getTime());
            long diffDays = diffMillis / (24 * 60 * 60 * 1000);
            return String.valueOf(diffDays);
        } catch (Exception e) {
            return "invalid date format";
        }
    }

//Validate the format of a spanish phone number (+34 prefix, then 9 digits, starting with 6, 7 or 9). 
//The operation should receive a phone number as parameter and return true if the format is correct, false otherwise
        @GET 
        @Path("/validatephone") 
        @Produces(MediaType.TEXT_PLAIN) 
        public boolean validatephone(@QueryParam("phone") String phone) { 
            Objects.requireNonNull(phone, "phone must not be null"); 
            return phone.matches("^\\+34[6,7,9]\\d{8}$"); 
        }

// Validate the format of a spanish DNI (8 digits and 1 letter). The operation should receive a DNI as parameter 
//and return true if the format is correct, false otherwise.
        @GET 
        @Path("/validatedni") 
        @Produces(MediaType.TEXT_PLAIN) 
        public boolean validatedni(@QueryParam("dni") String dni) { 
            Objects.requireNonNull(dni, "dni must not be null"); 
            return dni.matches("^\\d{8}[A-Z]$"); 
        }

// Based on the existing colors.json file under resources, given the name of the color as path parameter, 
// return the hexadecimal code. If the color is not found, return 404. 
// The operation should be mapped to /color/{name} and return the hexadecimal code as plain text.
        @GET 
        @Path("/color") 
        @Produces(MediaType.TEXT_PLAIN) 
        public String color(@QueryParam("color") String name) {
        Objects.requireNonNull(name, "name must not be null");
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream colorsStream = getClass().getClassLoader().getResourceAsStream("colors.json")) {
            JsonNode colors = objectMapper.readTree(colorsStream);
            for (JsonNode color : colors) {
                if (name.equalsIgnoreCase(color.get("color").asText())) {
                    return color.get("code").get("hex").asText();
                }
            }
        } catch (Exception e) {
            return "error";
        }
        return "not found";
    }

    // Create a new operation that call the API https://api.chucknorris.io/jokes/random and return the joke.
    @GET
    @Path("/joke")
    @Produces(MediaType.TEXT_PLAIN)
    public String joke() {
        String apiUrl = "https://api.chucknorris.io/jokes/random";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(jsonResponse);
                return jsonNode.get("value").asText();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    //Given a url as query parameter, parse it and return the protocol, host, port, path and query parameters. 
    //The response should be in Json format. 
    @GET
    @Path("/parseurl")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject parseurl(@QueryParam("url") String url) {
        Objects.requireNonNull(url, "url must not be null");
        JsonObject result = new JsonObject();
        result.put("protocol", url.substring(0, url.indexOf(":")));
        String hostAndPort = url.substring(url.indexOf(":") + 3);
        result.put("host", hostAndPort.substring(0, hostAndPort.indexOf("/")));
        result.put("port", hostAndPort.contains(":") ? hostAndPort.substring(hostAndPort.indexOf(":") + 1, hostAndPort.indexOf("/")) : "");
        String pathAndQuery = hostAndPort.substring(hostAndPort.indexOf("/"));
        result.put("path", pathAndQuery.contains("?") ? pathAndQuery.substring(0, pathAndQuery.indexOf("?")) : pathAndQuery);
        if (pathAndQuery.contains("?")) {
            String query = pathAndQuery.substring(pathAndQuery.indexOf("?") + 1);
            JsonArray queryParameters = new JsonArray();
            for (String queryParameter : query.split("&")) {
                JsonObject queryParameterObject = new JsonObject();
                queryParameterObject.put(queryParameter.substring(0, queryParameter.indexOf("=")), queryParameter.substring(queryParameter.indexOf("=") + 1));
                queryParameters.add(queryParameterObject);
            }
            result.put("query", queryParameters);
        }
        return result;
    }

    //Given the path of a file and count the number of occurrence of a provided word. 
    //The path and the word should be query parameters. The response should be in Json format. 
    @GET
    @Path("/countword")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject countword(@QueryParam("path") String path, @QueryParam("word") String word) {
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(word, "word must not be null");
        JsonObject result = new JsonObject();
        try (InputStream fileStream = getClass().getClassLoader().getResourceAsStream(path)) {
            if (fileStream == null) {
                result.put("error", "File not found");
                return result;
            }
            byte[] fileBytes = fileStream.readAllBytes();
            String fileContent = new String(fileBytes);
            int count = 0;
            int index = 0;
            while (index != -1) {
                index = fileContent.indexOf(word, index);
                if (index != -1) {
                    count++;
                    index += word.length();
                }
            }
            result.put("count", count);
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        return result;
    }
}