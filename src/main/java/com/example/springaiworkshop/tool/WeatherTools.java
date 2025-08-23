package com.example.springaiworkshop.tool;

import com.example.springaiworkshop.model.Weather;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class WeatherTools {

    // "name" if provided should be of this format:
    @Tool(
        name = "getWeatherForZipCode",
        description = "Returns the weather for a given zipcode."
    )
    public Weather getWeatherForZipCode(
        @ToolParam(description = "Zipcode to get weather for")
        String zipCode
    ) {
        System.out.println("TOOL: Getting weather for " + zipCode);
        return new Weather("Raining cats and dogs", "15 degrees Celsius");
    }
}
