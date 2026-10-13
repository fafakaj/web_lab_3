package ru.itmo.students.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @Column(name = "isu_id", length = 6)
    private String isuId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "group_name", nullable = false, length = 5)
    private String group;

    @Column(nullable = false, length = 3)
    private String dormitory;

    @Column(nullable = false, length = 4)
    private String room;

    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check_out")
    private LocalDate checkOut;

    @Column(name = "is_foreign", nullable = false)
    private boolean foreign;

    @Column(nullable = false, length = 500)
    private String notes;

    public String getIsuId() {
        return isuId;
    }

    public void setIsuId(String isuId) {
        this.isuId = isuId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDormitory() {
        return dormitory;
    }

    public void setDormitory(String dormitory) {
        this.dormitory = dormitory;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public boolean isForeign() {
        return foreign;
    }

    public void setForeign(boolean foreign) {
        this.foreign = foreign;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
