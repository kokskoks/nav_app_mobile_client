package pl.lodz.p.navapp.domain;

/**
 * Created by Łukasz Świtoń on 04.12.2016.
 */

public class Sublocation {

    private int id;
    private String name;
    private String code;
    private int placeID;

    public Sublocation(int id, String name, String code, int placeID) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.placeID = placeID;
    }

    public Sublocation() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getPlaceID() {
        return placeID;
    }

    public void setPlaceID(int placeID) {
        this.placeID = placeID;
    }
}
