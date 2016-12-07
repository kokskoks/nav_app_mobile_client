package pl.lodz.p.navapp.domain;

/**
 * Created by Calgon on 2016-12-07.
 */
public class Classroom {
    private int ID;
    private String name;
    private String description;
    private int floor;
    private PlaceInfo building;

    public Classroom() {
    }

    public Classroom(int ID, String name, String description, int floor) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.floor = floor;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", floor=" + floor +
                '}';
    }

    public PlaceInfo getBuilding() {
        return building;
    }

    public void setBuilding(PlaceInfo building) {
        this.building = building;
    }
}
