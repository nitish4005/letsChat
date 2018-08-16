package com.example.android.letschat;

/**
 * Created by Prasad on 09-Jan-18.
 */

public class Users {
    public String name,image,status,thumb_image,phone_no;
  public Users(){

  }

    public Users(String name, String image, String status, String thumb_image, String phone_no) {

        this.name = name;
        this.image = image;
        this.status = status;
        this.thumb_image = thumb_image;
        this.phone_no = phone_no;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

}
