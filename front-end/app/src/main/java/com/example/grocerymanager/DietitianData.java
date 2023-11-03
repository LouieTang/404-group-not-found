package com.example.grocerymanager;

import android.net.Uri;

public class DietitianData {
    private String firstName;
    private String lastName;
    private String dietitianEmail;
    private Uri dietitianProfilePictureUrl;
    private int DID;

    public DietitianData(String firstName, String lastName, String userEmail, Uri dietitianProfilePictureUrl, int DID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dietitianEmail = dietitianEmail;
        this.dietitianProfilePictureUrl = this.dietitianProfilePictureUrl;
        this.DID = DID;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setLastName(String lastName){
        this.lastName = lastName;
    }
    public String getLastName() {
        return lastName;
    }

    public String getDietitianEmail(){return dietitianEmail;}

    public int getDID(){
        return DID;
    }

    public void setUserProfilePictureUrl(Uri userProfilePictureUrl){
        this.dietitianProfilePictureUrl = userProfilePictureUrl;
    }
    public Uri getDietitianProfilePictureUrl() {
        return dietitianProfilePictureUrl;
    }

}
