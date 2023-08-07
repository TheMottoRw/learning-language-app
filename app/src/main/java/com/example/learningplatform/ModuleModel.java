package com.example.learningplatform;

public class ModuleModel {
    private String id;
    private String moduleName;
    private String image;
    private String level;

    private String isEnrolled;
    private String marks;
    private String totalMarks;
    private String isCompleted;

    public ModuleModel(String id,String level,String moduleName, String image, String isEnrolled,String marks,String totalMarks,String isCompleted) {
        this.id = id;
        this.level = level;
        this.moduleName = moduleName;
        this.image = image;
        this.isEnrolled = isEnrolled;
        this.marks = marks;
        this.totalMarks = totalMarks;
        this.isCompleted = isCompleted();
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }


    public String getIsEnrolled() {
        return isEnrolled;
    }

    public void setIsEnrolled(String isEnrolled) {
        this.isEnrolled = isEnrolled;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(String totalMarks) {
        this.totalMarks = totalMarks;
    }

    public String isCompleted() {
        return isCompleted;
    }

    public void setCompleted(String completed) {
        isCompleted = completed;
    }
}
