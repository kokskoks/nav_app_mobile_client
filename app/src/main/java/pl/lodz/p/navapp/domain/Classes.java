package pl.lodz.p.navapp.domain;

/**
 * Created by Calgon on 2016-12-06.
 */
public class Classes {

    private int ID;
    private String name;
    private String moduleCode;
    private String description;
    private String type;
    private int startHour;
    private int endHour;
    private String weekday;
    private int lecturerID;


    public Classes(int ID, String name, String moduleCode, String description, String type, int startHour, int endHour, String weekday, int lecturerID) {
        this.ID = ID;
        this.name = name;
        this.moduleCode = moduleCode;
        this.description = description;
        this.type = type;
        this.startHour = startHour;
        this.endHour = endHour;
        this.weekday = weekday;
        this.lecturerID = lecturerID;
    }

    public Classes() {

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

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public int getLecturerID() {
        return lecturerID;
    }

    public void setLecturerID(int lecturerID) {
        this.lecturerID = lecturerID;
    }

    @Override
    public String toString() {
        return "Classes{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", moduleCode='" + moduleCode + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", startHour=" + startHour +
                ", endHour=" + endHour +
                ", weekday='" + weekday + '\'' +
                ", lecturerID=" + lecturerID +
                '}';
    }
}
