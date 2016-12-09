package pl.lodz.p.navapp.domain;

import java.util.List;

/**
 * Created by Calgon on 2016-12-07.
 */
public class Group {
    private int ID;
    private String subject;
    private String code;
    private String description;
    private String semester;
    private String specialisation;
    private List<Classes> classesList;
    private Classroom classroom;

    public Group() {
    }

    public Group(int ID, String subject, String code, String description, String semester, String specialisation) {
        this.ID = ID;
        this.subject = subject;
        this.code = code;
        this.description = description;
        this.semester = semester;
        this.specialisation = specialisation;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getSpecialisation() {
        return specialisation;
    }

    public void setSpecialisation(String specialisation) {
        this.specialisation = specialisation;
    }

    @Override
    public String toString() {
        return "Group{" +
                "ID=" + ID +
                ", subject='" + subject + '\'' +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", semester='" + semester + '\'' +
                ", specialisation='" + specialisation + '\'' +
                '}';
    }

    public List<Classes> getClassesList() {
        return classesList;
    }

    public void setClassesList(List<Classes> classesList) {
        this.classesList = classesList;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }
}
