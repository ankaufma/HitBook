package htwg.de.hitbook.model;

/**
 * Created by ankaufma on 27.11.2014.
 */
public class FelledTree {
    private int id;
    private String lumberjack;
    private String team;
    private String areaDescription;
    private String latitude;
    private String longitude;
    private double height;
    private double diameter;

    public FelledTree(int id, String lumberjack, String team, String areaDescription, String latitude, String longitude, double height, double diameter) {
        this.id = id;
        this.lumberjack = lumberjack;
        this.team = team;
        this.areaDescription = areaDescription;
        this.latitude = latitude;
        this.longitude = longitude;
        this.height = height;
        this.diameter = diameter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLumberjack() {
        return lumberjack;
    }

    public String getTeam(){
        return team;
    }

    public void setLumberjack(String lumberjack) {
        this.lumberjack = lumberjack;
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

    public double getVolume(){
        return (diameter*diameter)/4*Math.PI*height;
    }
}
