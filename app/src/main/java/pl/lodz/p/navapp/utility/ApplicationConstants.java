package pl.lodz.p.navapp.utility;

import pl.lodz.p.navapp.R;
import pl.lodz.p.navapp.domain.PlaceInfo;

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

    public static int getImageFromRes(PlaceInfo info) {
        int resourceId;
        String place = info.getPlaceNumber();

        switch (place) {
            case "A1":
            case "A6":
            case "B5":
                resourceId = R.drawable.ipos2;
                break;
            case "A2":
            case "A8":
            case "A9":
            case "A24":
            case "A26":
            case "C1":
            case "C2":
            case "C18":
                resourceId = R.drawable.chemiczny2;
                break;
            case "A4":
                resourceId = R.drawable.binoz;
                break;
            case "A5":
                resourceId = R.drawable.ck;
                break;
            case "A10":
                resourceId = R.drawable.weeia;
                break;
            case "A11":
            case "A12":
            case "C6":
            case "C7":
                resourceId = R.drawable.weeia2;
                break;
            case "A13":
                resourceId = R.drawable.rekrutacja;
                break;
            case "A16":
                resourceId = R.drawable.ife;
                break;
            case "A18":
                resourceId = R.drawable.fabryka;
                break;
            case "A20":
            case "A21":
            case "A31":
                resourceId = R.drawable.mechaniczny2;
                break;
            case "A22":
                resourceId = R.drawable.mechaniczny;
                break;
            case "A27":
                resourceId = R.drawable.chemiczny;
                break;
            case "A33":
                resourceId = R.drawable.tmiwt;
                break;
            case "B1":
                resourceId = R.drawable.rektorat;
                break;
            case "B4":
                resourceId = R.drawable.ipos;
                break;
            case "B6":
                resourceId = R.drawable.bais2;
                break;
            case "B7":
                resourceId = R.drawable.bais;
                break;
            case "B9":
                resourceId = R.drawable.lodex;
                break;
            case "C4":
                resourceId = R.drawable.cs;
                break;
            case "C5":
                resourceId = R.drawable.d6;
                break;
            case "C9":
                resourceId = R.drawable.d7;
                break;
            case "C11":
                resourceId = R.drawable.d4;
                break;
            case "C12":
                resourceId = R.drawable.d3;
                break;
            case "C13":
                resourceId = R.drawable.d2;
                break;
            case "C14":
                resourceId = R.drawable.d1;
                break;
            case "C15":
                resourceId = R.drawable.stolowka;
                break;
            case "C3":
                resourceId = R.drawable.akwarium;
                break;
            case "C16":
                resourceId = R.drawable.d8;
                break;
            case "D1":
            case "D3":
            case "D4":
                resourceId = R.drawable.oiz;
                break;
            case "E1":
                resourceId = R.drawable.d9;
                break;
            case "W1":
                resourceId = R.drawable.artefakt;
                break;
            case "W2":
                resourceId = R.drawable.cotton;
                break;
            case "W3":
                resourceId = R.drawable.futurysta;
                break;
            case "W4":
            case "W11":
                resourceId = R.drawable.pko;
                break;
            case "W5":
                resourceId = R.drawable.azs;
                break;
            case "W6":
                resourceId = R.drawable.sukcesja;
                break;
            case "W7":
                resourceId = R.drawable.expo;
                break;
            case "W8":
                resourceId = R.drawable.finestra;
                break;
            case "W10":
                resourceId = R.drawable.brodway;
                break;
            case "W12":
                resourceId = R.drawable.indeks;
                break;
            case "W13":
                resourceId = R.drawable.zabka;
                break;
            case "W14":
                resourceId = R.drawable.drukarniastudencka;
                break;
            case "W15":
                resourceId = R.drawable.dino;
                break;
            case "W18":
                resourceId = R.drawable.d5;
                break;
            default:
                resourceId = R.drawable.pl;
                break;
        }

        return resourceId;
    }
}
