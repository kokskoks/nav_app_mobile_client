package pl.lodz.p.navapp.domain;

/**
 * Created by Calgon on 2016-12-06.
 */
public class Lecture {

    private int ID;
    private String firstName;
    private String lastName;
    private String title;
    private String description;
    private String mail;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        return "Lecture{" +
                "ID=" + ID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", mail='" + mail + '\'' +
                '}';
    }

    public Lecture() {
    }

    public Lecture(int ID, String firstName, String lastName, String title, String description, String mail) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.description = description;
        this.mail = mail;
    }
}
