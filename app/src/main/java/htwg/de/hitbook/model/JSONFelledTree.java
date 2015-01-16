package htwg.de.hitbook.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by ankaufma on 14.01.2015.
 */
public class JSONFelledTree implements Serializable {
    private String lumberjack;
    private String team;
    private String areaDescription;
    private String latitude;
    private String longitude;
    private double height; //in meters
    private double diameter; //in centimeters
    private String date; // yyyy.mm.dd
    private String image;

    public JSONFelledTree(String lumberjack, String team, String areaDescription, String latitude, String longitude, double height, double diameter, String date, String image) {
        this.lumberjack = lumberjack;
        this.team = team;
        this.areaDescription = areaDescription;
        this.latitude = latitude;
        this.longitude = longitude;
        this.height = height;
        this.diameter = diameter;
        this.date = date;
        this.image = image;
    }

    public static JSONFelledTree getJSONFelledTree(FelledTree ft) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ft.getThumbnail().compress(Bitmap.CompressFormat.JPEG, 70, stream);
            byte[] byteFormat = stream.toByteArray();
            String imgString = Base64.encodeToString(byteFormat, Base64.NO_CLOSE);
        return new JSONFelledTree(
                ft.getLumberjack(), ft.getTeam(), ft.getAreaDescription(), ft.getLatitude(), ft.getLongitude(), ft.getHeight(),ft.getDiameter(),ft.getDate(),imgString
        );
    }

    public static JSONFelledTree getJSONFelledTreeWithoutImage(FelledTree ft) {
        return new JSONFelledTree(
                ft.getLumberjack(), ft.getTeam(), ft.getAreaDescription(), ft.getLatitude(), ft.getLongitude(), ft.getHeight(),ft.getDiameter(),ft.getDate(),""
        );
    }

    public static FelledTree getFelledTree(JSONFelledTree ft) {
        byte[] decodedByte = Base64.decode(ft.getImage(), 0);
        Bitmap image = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        return new FelledTree(
                0,ft.getLumberjack(), ft.getTeam(), ft.getAreaDescription(), ft.getLatitude(), ft.getLongitude(), ft.getHeight(),ft.getDiameter(),ft.getDate(),image
        );
    }

    public String getLumberjack() {
        return lumberjack;
    }

    public void setLumberjack(String lumberjack) {
        this.lumberjack = lumberjack;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getAreaDescription() {
        return areaDescription;
    }

    public void setAreaDescription(String areaDescription) {
        this.areaDescription = areaDescription;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getDiameter() {
        return diameter;
    }

    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
