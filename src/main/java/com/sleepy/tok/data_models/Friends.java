package com.sleepy.tok.data_models;

public class Friends {
    public String friends_since;
    private String bio;
    private String profile_photo_thumbnail;
    private String user_id;
    private String screen_name;



    public Friends (String bio, String profile_photo_thumbnail, String user_id, String screen_name){
        this.bio = bio;
        this.profile_photo_thumbnail = profile_photo_thumbnail;
        this.user_id=user_id;
        this.screen_name = screen_name;
    }

    public Friends(){
    }


    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfile_photo_thumbnail() {
        return profile_photo_thumbnail;
    }

    public void setProfile_photo_thumbnail(String profile_photo_thumbnail) {
        this.profile_photo_thumbnail = profile_photo_thumbnail;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }
}
