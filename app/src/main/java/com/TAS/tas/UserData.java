package com.TAS.tas;

public class UserData {
    public UserData(){

    }
    String name;
    String password;
    String email;
    String isstu;
    String  image;
    String uid;
    String onlineStatus;
    String typingTo;
    String school;
    String phno;
    Boolean isBlocked = false;
    String agree;

    public UserData(String name, String password, String email, String isstu, String image, String uid, String onlineStatus, String typingTo, String school, String phno, Boolean isBlocked) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.isstu = isstu;
        this.image = image;
        this.uid = uid;
        this.onlineStatus = onlineStatus;
        this.typingTo = typingTo;
        this.school = school;
        this.phno = phno;
        this.isBlocked = isBlocked;
    }

    public Boolean getBlocked() {
        return isBlocked;
    }
    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTypingTo() {
        return typingTo;
    }
    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }
    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getSchool() {
        return school;
    }
    public void setSchool(String school) {
        this.school = school;
    }

    public String getPhno() {
        return phno;
    }
    public void setPhno(String phno) {
        this.phno = phno;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }


    public String getName() {
        return name;
    }
    public void setName() {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword() {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail() {
        this.email = email;
    }

    public String getIsstu() {
        return isstu;
    }
    public void setIsstu(String isstu) {
        this.isstu = isstu;
    }

    public String getAgree() {
        return agree;
    }
    public void setAgree(String agree) {
        this.agree = agree;
    }


    public UserData(String name, String password, String email, String isstu, String phno, String onlineStatus, String typingTo, String uid,String school) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.isstu = isstu;
        this.typingTo = typingTo;
        this.onlineStatus = onlineStatus;
        this.school = school;
        this.phno = phno;
    }

    public UserData(String name, String password, String email, String isstu, String phno, String onlineStatus,String typingTo,String uid){
        this.name=name;
        this.password=password;
        this.email = email;
        this.isstu = isstu;
        this.phno = phno;
        this.onlineStatus =onlineStatus;
        this.typingTo = typingTo;
        this.uid = uid;
    }

}
