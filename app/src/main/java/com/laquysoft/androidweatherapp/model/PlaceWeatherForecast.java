package com.laquysoft.androidweatherapp.model;

import java.util.List;

/**
 * Created by joaobiriba on 29/08/14.
 */
public class PlaceWeatherForecast {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class CurrentConditionObj {
        private String cloudcover;

        public String getCloudcover() {
            return cloudcover;
        }

        public void setCloudcover(String cloudcover) {
            this.cloudcover = cloudcover;
        }
    }

    public class Data {
        private List<CurrentConditionObj> currentCondition;
        private List<Weather> weather;
        private List<Request> request;

        public List<CurrentConditionObj> getCurrentCondition() {
            return currentCondition;
        }

        public void setCurrentCondition(List<CurrentConditionObj> currentCondition) {
            this.currentCondition = currentCondition;
        }

        public List<Weather> getWeather() {
            return weather;
        }

        public void setWeather(List<Weather> weather) {
            this.weather = weather;
        }

        public List<Request> getRequest() {
            return request;
        }

        public void setRequest(List<Request> request) {
            this.request = request;
        }
    }

    public class Weather {
        private String date;
        private Float precipMM;
        private Integer weatherCode;
        private Integer windspeedKmph;
        private String winddir16Point;

        private List<WeatherDesc> weatherDesc;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public List<WeatherDesc> getWeatherDesc() {
            return weatherDesc;
        }

        public void setWeatherDesc(List<WeatherDesc> weatherDesc) {
            this.weatherDesc = weatherDesc;
        }

        public Float getPrecipMM() {
            return precipMM;
        }

        public void setPrecipMM(Float precipMM) {
            this.precipMM = precipMM;
        }

        public Integer getWeatherCode() {
            return weatherCode;
        }

        public void setWeatherCode(Integer weatherCode) {
            this.weatherCode = weatherCode;
        }

        public Integer getWindspeedKmph() {
            return windspeedKmph;
        }

        public void setWindspeedKmph(Integer windspeedKmph) {
            this.windspeedKmph = windspeedKmph;
        }

        public String getWinddir16Point() {
            return winddir16Point;
        }

        public void setWinddir16Point(String winddir16Point) {
            this.winddir16Point = winddir16Point;
        }
    }

    public class WeatherDesc {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    public class Request {
        private String query;
        private String type;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
