package com.example.disc.models;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.util.List;

public class ApiResponse {
    @SerializedName("public_id")
    String public_id;
    @SerializedName("version")
    int version;
    @SerializedName("signature")
    String signature;
    @SerializedName("width")
    int width;
    @SerializedName("height")
    int height;
    @SerializedName("format")
    String format;
    @SerializedName("resource_type")
    String resource_type;
    @SerializedName("created_at")
    Date created_at;
    @SerializedName("tags")
    List<Object> tags;
    @SerializedName("bytes")
    int bytes;
    @SerializedName("type")
    String type;
    @SerializedName("url")
    String url;
    @SerializedName("secure_url")
    String secure_url;

    public String getPublic_id() {
        return public_id;
    }

    public int getVersion() {
        return version;
    }

    public String getSignature() {
        return signature;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getFormat() {
        return format;
    }

    public String getResource_type() {
        return resource_type;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public List<Object> getTags() {
        return tags;
    }

    public int getBytes() {
        return bytes;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getSecure_url() {
        return secure_url;
    }
}
