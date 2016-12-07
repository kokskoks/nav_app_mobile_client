package pl.lodz.p.navapp;

/**
 * Created by Łukasz Świtoń on 07.12.2016.
 */

public class ApplicationConstants {
    public static final String URL = "https://nav-app.herokuapp.com/api";
    public static final String VERSION_FILE_NAME = "version.txt";
    public static final int MAX_ZOOM_LEVEL = 20;
    public static final int ZOOMLEVEL = 17;
    public static final int FILE_READ_ERROR = -2;
    public static final int NO_INTERNET_ACCESS = -1;
    public enum TravelType {
        PEDESTRIAN("pedestrian"), BIKE("bicycle"), CAR("fastest");
        private String type;

        TravelType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }
    }
}
