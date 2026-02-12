package com.komsiluk.taxi.ui.ride.map;
import java.util.List;

public class PhotonFeature {
    public Geometry geometry;
    public Properties properties;

    public static class Geometry {
        public List<Double> coordinates;
    }

    public static class Properties {
        public String name;
        public String city;
        public String street;
        public String housenumber;

        public String getDisplayName() {
            StringBuilder sb = new StringBuilder();
            if (name != null) sb.append(name);

            if (street != null) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(street);
                if (housenumber != null) sb.append(" ").append(housenumber);
            }

            if (city != null) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(city);
            }
            return sb.toString();
        }
    }
}