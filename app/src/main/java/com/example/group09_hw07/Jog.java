package com.example.group09_hw07;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;

public class Jog implements Serializable {
    String uid, username, title;
    ArrayList<GeoPoint> points;
    Timestamp createdAt;

    @Override
    public String toString() {
        return "Jog{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", title='" + title + '\'' +
                ", points=" + points +
                ", createdAt=" + createdAt +
                '}';
    }
}
