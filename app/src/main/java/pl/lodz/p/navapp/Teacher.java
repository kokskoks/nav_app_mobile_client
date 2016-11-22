package pl.lodz.p.navapp;

/**
 * Created by Calgon on 2016-11-22.
 */

public class Teacher {
    private int ID;
    private String name;
    private String surname;

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFullName() {
        return name + " " + surname;
    }

    public void setFullName(String fullname) {
        String[] name = fullname.split(" ");
        this.name = name[0];
        this.surname = name[1];
    }
}
