package com.ao.android.data;

import java.util.Calendar;

public class Classroom {

    private String title;
    private String section;
    private ClassType classtype;
    private Calendar startTime;
    private Calendar endTime;
    private String location;

    public Classroom(String title, String section) {
        this.title = title;
        this.section = section;
    }

    public String getTitle() {
        return title;
    }

    private String getSection() {
        return section;
    }

    public void setClassType(ClassType classtype) {
        this.classtype = classtype;
    }

    public ClassType getClassType() {
        return classtype;
    }

    private Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    private Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFullTitle() {
        return getTitle() + " - " + getSection();
    }

    public String getTimeDuration() {
        String begin = getStartTime().get(Calendar.HOUR_OF_DAY) + ":" + getStartTime().get(Calendar.MINUTE);
        int am_pms = getStartTime().get(Calendar.AM_PM);
        switch (am_pms) {
            case Calendar.AM :
                begin += "AM";
                break;
            case Calendar.PM :
                begin += "PM";
                break;
        }
        String end = getEndTime().get(Calendar.HOUR_OF_DAY) + ":" + getEndTime().get(Calendar.MINUTE);
        int am_pme = getEndTime().get(Calendar.AM_PM);
        switch (am_pme) {
            case Calendar.AM :
                end += "AM";
                break;
            case Calendar.PM :
                end += "PM";
                break;
        }
        return begin + " - " + end;
    }

    public enum ClassType {
        LECTURE, LAB
    }

}
