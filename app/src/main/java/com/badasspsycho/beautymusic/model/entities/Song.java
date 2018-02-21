package com.badasspsycho.beautymusic.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {

    private long songId;

    private String title;
    private String album;
    private String artist;

    private long duration;
    private String uri;

    public Song(long songId, String title, String album, String artist, long duration, String uri) {
        this.songId = songId;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.duration = duration;
        this.uri = uri;
    }

    public long getSongId() {
        return songId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUri() {
        return uri;
    }

    protected Song(Parcel in) {
        songId = in.readLong();
        title = in.readString();
        album = in.readString();
        artist = in.readString();
        duration = in.readLong();
        uri = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(songId);
        parcel.writeString(title);
        parcel.writeString(album);
        parcel.writeString(artist);
        parcel.writeLong(duration);
        parcel.writeString(uri);
    }
}
