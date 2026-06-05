package com.example.flixgo;

import com.google.gson.annotations.SerializedName;

public class Video {
    @SerializedName("key")
    private String key;

    @SerializedName("site")
    private String site;

    @SerializedName("type")
    private String type;

    @SerializedName("official")
    private boolean official;

    public String getKey() { return key; }
    public String getSite() { return site; }
    public String getType() { return type; }
    public boolean isOfficial() { return official; }
}