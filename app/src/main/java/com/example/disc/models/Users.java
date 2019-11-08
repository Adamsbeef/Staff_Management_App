package com.example.disc.models;

import android.net.Uri;

import java.io.Serializable;

public class Users implements Serializable {
    private String mId;
    private String mFullName;
    private String mDOB;
    private String mPhoneNumber;
    private String mEmail;
    private String mStateOfOrigin;
    private String mPictureUri;

    public Users() {
    }

    public Users(String id, String firstName, String lastName, String phoneNumber, String email,String stateOfOrigin,String pictureUri) {
        this.mId = id;
        this.mFullName = firstName;
        this.mDOB = lastName;
        this.mPhoneNumber = phoneNumber;
        this.mEmail = email;
        this.mStateOfOrigin = stateOfOrigin;
        this.mPictureUri = pictureUri;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmFullName() {
        return mFullName;
    }

    public void setmFullName(String mFullName) {
        this.mFullName = mFullName;
    }

    public String getmDOB() {
        return mDOB;
    }

    public void setmDOB(String mDOB) {
        this.mDOB = mDOB;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmStateOfOrigin() {
        return mStateOfOrigin;
    }

    public void setmStateOfOrigin(String mStateOfOrigin) {
        this.mStateOfOrigin = mStateOfOrigin;
    }

    public String getmPictureUri() {
        return mPictureUri;
    }

    public void setmPictureUri(String mPictureUri) {
        this.mPictureUri = mPictureUri;
    }
}
