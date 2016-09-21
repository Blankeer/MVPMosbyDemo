package com.blanke.testmosby.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by asus on 2016/9/20.
 */
public class News implements Parcelable {
    private String title;
    private String content;

    public News(String content, String title) {
        this.content = content;
        this.title = title;
    }

    @Override
    public String toString() {
        return "News{" +
                "content='" + content + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.content);
    }

    protected News(Parcel in) {
        this.title = in.readString();
        this.content = in.readString();
    }

    public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
        @Override
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };
}
