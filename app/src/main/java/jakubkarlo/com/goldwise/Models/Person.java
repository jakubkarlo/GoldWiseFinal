package jakubkarlo.com.goldwise.Models;

/**
 * Created by Jakub on 20.11.2017.
 */

public class Person {

    private String name;
    private String phoneNumber;
    private double share;
    private int color;


    public Person(String name, String phoneNumber, double share, int color) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.share = share;
        this.color = color;
    }

    public double getShare() {
        return share;
    }

    public void setShare(double share) {
        this.share = share;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
