/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc365lab2;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author tim
 */
public class ClusterWrapper implements Serializable{

    private ArrayList<Point> points;
    private ArrayList<Cluster> clusters;

    public ClusterWrapper() {
        points = new ArrayList<>();
        clusters = new ArrayList<>();
    }

    public void addPoints(Point p) {
        points.add(p);
    }

    public int getNumClusters() {
        return clusters.size();
    }

    public ArrayList<Cluster> getClusters() {
        return clusters;
    }

    public void makeCluster(String centerKey, int in, Review cen) {
            Cluster cluster = new Cluster();
            Point centroid = new Point(centerKey, 350, 350, in);
            cluster.center = centroid;
            cluster.cent=cen;
            clusters.add(cluster);
    }
    
    private ArrayList<Point> getCenters() {
        ArrayList<Point> centers = new ArrayList<>();
        for (Cluster cluster : clusters) {
            Point point = cluster.center;
            centers.add(point);
        }
        return centers;
    }

}
