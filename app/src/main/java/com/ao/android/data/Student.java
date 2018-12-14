package com.ao.android.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Student implements Serializable {

    private String studentId;
    private String username;
    private List<Classroom> classrooms;

    public Student(String studentId) {
        this.studentId = studentId;
        classrooms = new ArrayList<>();
    }

    public String getStudentId() {
        return studentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void addClassroom(Classroom classroom) {
        this.classrooms.add(classroom);
    }

    public List<Classroom> getClassrooms() {
        return classrooms;
    }

}
