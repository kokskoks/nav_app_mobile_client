package pl.lodz.p.navapp.domain;

/**
 * Created by Calgon on 2016-11-22.
 */

public class ClassInfo {
    public enum Day {
        PONIEDZIALEK(0), WTOREK(1), SRODA(2), CZWARTEK(3), PIATEK(4);
        int value;

        Day(int number) {
            this.value = number;
        }

        public int getValue() {
            return value;
        }
    }

    public enum Type {
        SEMINARIUM(0), WYKLAD(1), LABORATORIUM(2);
        int type;

        Type(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    private String groupName;
    private String name;
    private Type type;
    private String building;
    private String classroom;
    private Day day;
    private String from;
    private String to;
    private Teacher teacher;


    public ClassInfo(String groupName, String name, Type type, String building, String classroom, Day day, String from, String to, Teacher teacher) {
        this.groupName = groupName;
        this.name = name;
        this.type = type;
        this.building = building;
        this.classroom = classroom;
        this.day = day;
        this.from = from;
        this.to = to;
        this.teacher = teacher;
    }

    public ClassInfo(String name, String building, String from, String to) {
        this.name = name;
        this.building = building;
        this.from = from;
        this.to = to;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
