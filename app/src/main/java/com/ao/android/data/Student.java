package com.ao.android.data;

import java.io.Serializable;

public class Student implements Serializable {

    private String studentId;
    private String username;

    public Student(String studentId) {
        this.studentId = studentId;
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

}
