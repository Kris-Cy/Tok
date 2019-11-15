package com.sleepy.tok.data_models;

public class User {

        private String bio;
        private String email;
        private String profile_photo;
        private String profile_photo_thumbnail;
        private String user_id;
        private String username;
        private String screen_name;
        private String online;

        //the main constructor for this class.
        public User(String bio, String email, String profile_photo, String profile_photo_thumbnail, String user_id, String username, String screen_name) {
            this.bio = bio;
            this.email = email;
            this.profile_photo = profile_photo;
            this.profile_photo_thumbnail = profile_photo_thumbnail;
            this.user_id = user_id;
            this.username = username;
            this.screen_name = screen_name;
            this.online=online;
        }

        //an empty constructor for now.
        public User(){

        }


        //Below are all my getter and setter methods for the children of the user object.

        public String getOnline() { return online; }

        public void setOnline(String online) { this.online = online; }

        public String getBio() {
        return bio;
    }

        public void setBio(String bio) {
        this.bio = bio;
    }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) { this.email = email; }

        public String getProfile_photo() { return profile_photo; }

        public void setProfile_photo(String profile_photo) { this.profile_photo = profile_photo; }

        public String getProfile_photo_thumbnail() { return profile_photo_thumbnail; }

        public void setProfile_photo_thumbnail(String profile_photo_thumbnail) { this.profile_photo_thumbnail = profile_photo_thumbnail; }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getScreen_name() {
        return screen_name;
    }

        public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

        //what this does is in the method's name.
        @Override
        public String toString() {
            return "User{" +
                    "bio='" + bio + '\''+
                    "email='" + email + '\'' +
                    ", profile_photo='" + profile_photo + '\'' +
                    ", profile_photo_thumbnail='" + profile_photo_thumbnail + '\'' +
                    ", user_id='" + user_id + '\'' +
                    ", username='" + username + '\'' +
                    ", screen_name='" + screen_name + '\'' +
                    '}';
        }
}
