/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc365lab2;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 *
 * @author tim
 */
public class Cluster implements Serializable{

    ArrayList<Point> points;
    Point center;
    Review cent;
    Point closest;
    double close=0;
    DecimalFormat df;

    public Cluster() {
        points = new ArrayList<>();
        center = null;
        closest = null;
        cent=null;
        df = new DecimalFormat("#.00");
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public String centerCoord() {
        return df.format(center.x) + ", " + df.format(center.y);
    }

    public void clear() {
        points.clear();
    }
}
