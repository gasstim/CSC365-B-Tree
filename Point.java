/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc365lab2;

import java.io.Serializable;
import java.util.Random;

/**
 *
 * @author tim
 */
public class Point implements Serializable{

    int x = 0;
    int y = 0;
    String key;
    int cluster = 0;

    public Point(String k, int xcord, int ycord,int cl) {
        key = k;
        x = xcord;
        y = ycord;
        cluster = cl;
    }

    public static double distance(Point p, Point center) {
        return Math.sqrt(Math.pow((center.y - p.y), 2) + Math.pow((center.x - p.x), 2));
    }

    public String toString() {
        return key;
    }
}
