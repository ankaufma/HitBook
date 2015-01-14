package htwg.de.hitbook.model;

import android.graphics.Bitmap;

/**
 * Created by ankaufma on 14.01.2015.
 */
public class JSONFelledTree {
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

        public static JSONFelledTree getJSONFelledTree(FelledTree ft, String image) {
            return new JSONFelledTree(
                    ft.getLumberjack(), ft.getTeam(), ft.getAreaDescription(), ft.getLatitude(), ft.getLongitude(), ft.getHeight(),ft.getDiameter(),ft.getDate(),image
            );
        }

}
