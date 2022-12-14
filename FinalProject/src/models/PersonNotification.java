package models;

import domain.Application;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Objects;

public class PersonNotification {
    private int id;
    private int personId;
    private String title;
    private String message;
    private Date date;

    public PersonNotification(int id, int personId, String title, String message, Date date) {
        this.id = id;
        this.personId = personId;
        this.title = title;
        this.message = message;
        this.date = date;
    }

    public PersonNotification(int personId, String title, String message) {
        this.personId = personId;
        this.title = title;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonNotification that = (PersonNotification) o;
        return id == that.id && personId == that.personId && Objects.equals(title, that.title) && Objects.equals(message, that.message) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, personId, title, message, date);
    }

    public void create() throws SQLException {
        Application.Database.PersonNotifications.add(this);
    }
}
