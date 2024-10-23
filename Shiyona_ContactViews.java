package com.example.phonebook;

public class ContactViews {

    private String name;
    private String bio;
    private int contactImageID;
    private String number;

    public ContactViews(int contactImageResourceID, String contactName, String contactNumber, String contactBio)
    {
        contactImageID = contactImageResourceID;
        name = contactName;
        number = contactNumber;
        bio = contactBio;
    }

    public int getContactImageID(){
        return contactImageID;
    }

    public void setContactImageID(int contactImageResourceID){
        contactImageID = contactImageResourceID;
    }

    public String getContactName(){
        return name;
    }

    public void setContactName(String contactName){
        name = contactName;
    }

    public String getContactBio(){
        return bio;
    }

    public void setContactBio(String contactBio){
        bio = contactBio;
    }

    public String getContactNumber(){
        return number;
    }

    public void setContactNumber(String contactNumber){
        number = contactNumber;
    }
}
