package com.inhatc.mypet;

import android.graphics.drawable.Drawable;

import com.google.firebase.storage.StorageReference;

public class listview_item {

    private StorageReference uri;
    private String name;
    private String birth;
    private String gender;
    private String type;
    private String id;
    private Drawable icon;
    private String title;
    private String time;

    public StorageReference getUri() {
        return uri;
    }

    public void setUri(StorageReference uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getGender(){return gender;}

    public void setGender(String gender){this.gender=gender;}

    public String getType(){return type;}

    public void setType(String type){this.type=type;}

    public String getid(){return id;}

    public void setid(String id){this.id=id;}

    public Drawable getIcon(){return icon;}

    public void setIcon(Drawable icon){this.icon=icon;}

    public String getTitle(){return title;}

    public void setTitle(String title){this.title=title;}

    public String getTime(){return time;}

    public void setTime(String time){this.time=time;}
}
