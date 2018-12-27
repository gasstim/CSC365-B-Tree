/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc365lab2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author tim
 * i used the disk algorithm using bytebuffers and filechannels on a random access file
 * from https://www.javacodegeeks.com/2016/01/readingwriting-tofrom-files-using-filechannel-bytebuffer-java.html
 */
public class BTree implements Serializable {

    private Node root;
    private static int minDegree = 100;
    private int height = 1;
    private File nodeFile;
    RandomAccessFile clustersFile;
    private int numNodes = 0;
    ClusterWrapper cw;
    int helper =0;

    public BTree() throws FileNotFoundException, IOException, ClassNotFoundException {
        nodeFile = new File("Nodes2.ser");
        if (!nodeFile.exists()) {
            System.out.println("it doesnt exist");
            RandomAccessFile nodes = new RandomAccessFile("Nodes2.ser", "rw");
            nodes.seek(0);
            root = allocate();
            root.isLeaf = true;
            root.n = 0;
            diskWrite(root);
            cw = new ClusterWrapper();
        } else {
            RandomAccessFile roo = new RandomAccessFile("RootLocation2.ser", "rw");
            long ro = roo.readLong();
            root = diskRead(ro);
            cw = new ClusterWrapper();
        }
    }

    public Node getRoot() {
        return root;
    }

    private static double getSimilarity(Review pick, Review Center) {
        //simple string cosine similarity algorithm adopted from
        //https://blog.nishtahir.com/2015/09/19/fuzzy-string-matching-using-cosine-similarity/
        //get the words in it
        ArrayList pickWords = pick.revWords.returnAllObj();
        int dotProduct = 0;
        int magA = 0;
        int magB = 0;
        //compare each word in the chosen revLocation to each in the potential revLocation
        for (int q = 0; q < pickWords.size(); q++) {
            Word chosen = (Word) pickWords.get(q);
            Word potentialWord = (Word) Center.revWords.get(chosen.word);
            int word_used;
            if (potentialWord == null) {
                word_used = 0;
            } else {
                word_used = potentialWord.number_of_times_used;
            }
            dotProduct += chosen.number_of_times_used * word_used;
            magA += (chosen.number_of_times_used ^ 2);
            magB += (word_used ^ 2);
        }
        //similarity is dotProduct/magnitude
        double magnitude = Math.sqrt(magA) * Math.sqrt(magB);
        double result = dotProduct / magnitude;
        return result;
    }

    private Review getReview(long position) throws IOException, ClassNotFoundException {
        RandomAccessFile rev = new RandomAccessFile("Reviews.ser", "rw");
        rev.seek(position);
        // getting file channel
        FileChannel channel = rev.getChannel();
        // preparing buffer to read data from file
        ByteBuffer buffer = ByteBuffer.allocate(16384);
        // reading data from file channel into buffer
        int numOfBytesRead = channel.read(buffer);
        //System.out.println("number of bytes read : " + numOfBytesRead);
        // You need to filp the byte buffer before reading
        buffer.flip();
        // Recovering object
        Review temp=new Review();
        temp.recover(buffer);
        channel.close();
        rev.close();
        return temp;
    }

    public ClusterWrapper doClusters() throws IOException, ClassNotFoundException {
        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                Review temp = getReview(root.revLocation[0].position);
                cw.makeCluster(temp.revId, i, temp);
            } else if (i < minDegree) {
                Node temporary = diskRead(root.childPosition[0]);
                Review temp = getReview(diskRead(root.childPosition[0]).revLocation[i - 1].position);
                cw.makeCluster(temp.revId, i, temp);
            } else {
                Review temp = getReview(diskRead(root.childPosition[1]).revLocation[i - minDegree].position);
                cw.makeCluster(temp.revId, i, temp);
            }
        }
        cw = clusterTraverse(root);
        RandomAccessFile roo = new RandomAccessFile("clusters2.ser", "rw");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(cw);
        out.close();
        byte[] buf = bos.toByteArray();
        System.out.println("Clusters Byte array size: " + buf.length);
        roo.write(buf);
        roo.close();
        return cw;
    }

    public ClusterWrapper clusterTraverse(Node p) throws IOException, ClassNotFoundException {
        int i;
        for (i = 0; i < p.n; i++) {
            if (!p.isLeaf && helper < 500000) {
                clusterTraverse(diskRead(p.childPosition[i]));
            }
            if(helper < 500000){
            Review r = getReview(p.revLocation[i].position);
            ArrayList<Cluster> a = cw.getClusters();
            if (!(r.revId.equals(a.get(0).center.key) || r.revId.equals(a.get(1).center.key)
                    || r.revId.equals(a.get(2).center.key) || r.revId.equals(a.get(3).center.key)
                    || r.revId.equals(a.get(4).center.key))) {
                double similarity = 0;
                int clusterIndex = 0;
                for (int j = 0; j < cw.getNumClusters(); j++) {
                    Review center = cw.getClusters().get(j).cent;
                    Double simi = getSimilarity(r, center);
                    if (simi > similarity) {
                        similarity = simi;
                        clusterIndex = j;
                    }
                }
                Point point;
                point = new Point(r.revId, 0, 0, clusterIndex);
                if (similarity > cw.getClusters().get(clusterIndex).close) {
                    //more similar than current closest
                    cw.getClusters().get(clusterIndex).close = similarity;
                    cw.getClusters().get(clusterIndex).closest = point;
                }
                cw.getClusters().get(clusterIndex).addPoint(point);
                helper++;
            }}
        }

        if (!p.isLeaf && helper < 500000) {
            clusterTraverse(diskRead(p.childPosition[i]));
        }
        return cw;
    }

    /**
     *
     * @param d - Review to insert into the tree
     */
    public void insert(RevLocation d) throws IOException, ClassNotFoundException {
        Node r = root;
        if (r.n == 2 * minDegree - 1) {
            height++;
            Node s = allocate();
            root = s;
            root.isLeaf = false;
            root.n = 0;
            root.childPosition[0] = r.position;
            split(root, 0);
            insertNonFull(root, d);
        } else {
            insertNonFull(root, d);
        }

    }

    public int getHeight() {
        return height;
    }

    /**
     *
     * @param x - The Node that the revLocation will be inserted into
     * @param r - The Review that will be inserted
     */
    private void insertNonFull(Node x, RevLocation r) throws IOException, ClassNotFoundException {
        int i = x.n;
        if (x.isLeaf) {
            while (i >= 1 && r.key.compareTo(x.revLocation[i - 1].key) < 0) {
                if (x.revLocation[i - 1] == null) {
                    System.out.println("Null at1 " + (i - 1));
                }
                x.revLocation[i] = x.revLocation[i - 1];
                i--;
            }
            x.revLocation[i] = r;
            x.n++;
            diskWrite(x);
        } else {
            while (i >= 1 && r.key.compareTo(x.revLocation[i - 1].key) < 0) {
                i--;
            }
            i++;
            Node child = diskRead(x.childPosition[i - 1]);
            if (child.n == 2 * minDegree - 1) {
                split(x, i - 1);
                if (r.key.compareTo(x.revLocation[i - 1].key) > 0) {
                    i++;
                }
            }
            insertNonFull(diskRead(x.childPosition[i - 1]), r);
        }
    }

    /**
     *
     * @param parent - The Parent Node that the median key will be inserted into
     * @param i - The Integer index of the lesserChild that will be used to
     * populate the parent and greaterChild key
     */
    private void split(Node Parent, int i) throws IOException, ClassNotFoundException {
        Node greaterChild = allocate();
        Node lesserChild = diskRead(Parent.childPosition[i]);
        greaterChild.isLeaf = lesserChild.isLeaf;
        greaterChild.n = minDegree - 1;
        for (int j = 1; j <= (minDegree - 1); j++) {
            int temp = j + minDegree - 1;
            greaterChild.revLocation[j - 1] = lesserChild.revLocation[j + minDegree - 1];
        }
        if (!lesserChild.isLeaf) {
            for (int j = 1; j <= minDegree; j++) {
                greaterChild.childPosition[j - 1] = lesserChild.childPosition[j + minDegree - 1];
                greaterChild.numChildren++;
                lesserChild.numChildren--;
            }
        }
        lesserChild.n = minDegree - 1;
        for (int j = Parent.n + 1; j > i + 1; j--) {
            Parent.childPosition[j] = Parent.childPosition[j - 1];
        }
        Parent.childPosition[i + 1] = greaterChild.position;
        Parent.numChildren++;
        for (int j = Parent.n; j > i; j--) {
            Parent.revLocation[j] = Parent.revLocation[j - 1];
        }
        Parent.revLocation[i] = lesserChild.revLocation[minDegree - 1];
        Parent.n++;
        diskWrite(lesserChild);
        diskWrite(greaterChild);
        diskWrite(Parent);
    }

    /**
     *
     * @param x - The Node that will be traversed from to find the key asked for
     * @param k - The String key to search for
     * @return Review
     */
    public RevLocation search(Node x, String k) throws IOException, ClassNotFoundException {
        int i = 1;
        while (i <= x.n && k.compareTo(x.revLocation[i - 1].key) > 0) {
            i++;
        }
        if (i <= x.n && k.compareTo(x.revLocation[i - 1].key) == 0) {
            return x.revLocation[i - 1];
        } else if (x.isLeaf) {
            return null;
        } else {
            Node child = diskRead(x.childPosition[i - 1]);
            return search(child, k);
        }
    }

    //Shows the revLocation ids inside the current root Node
    public void showRoot() {
        for (RevLocation d : root.revLocation) {
            if (d != null) {
                System.out.println(d.key + " ;");
            }
        }
    }

    public static class Node implements Serializable {

        private long position;
        RevLocation[] revLocation;
        long[] childPosition;
        Integer n;
        Integer numChildren;
        private boolean isLeaf;

        public Node(boolean leaf) {
            revLocation = new RevLocation[2 * minDegree - 1];
            childPosition = new long[2 * minDegree];
            n = 0;
            numChildren = 0;
            isLeaf = leaf;
            position = -1;
        }

        public void persist(ByteBuffer buffer) {
            String filler = "v0i_UHJMo_hPBq9bxWvW4w";
            long fill = 1;
            //position is long
            buffer.putLong(this.position);
            //2 ints-n, numChildren
            buffer.putInt(this.n);
            buffer.putInt(this.numChildren);
            //array of revLocations-fixed length string and a long
            for (int i = 0; i < this.revLocation.length; i++) {
                if (i < this.n) {
                    //saves all revLoc's
                    byte[] strBytes = this.revLocation[i].key.getBytes();
                    buffer.putInt(strBytes.length);
                    buffer.put(strBytes, 0, strBytes.length);
                    buffer.putLong(this.revLocation[i].position);
                } else {
                    //fills empty revLoc space
                    byte[] strBytes = filler.getBytes();
                    buffer.putInt(strBytes.length);
                    buffer.put(strBytes, 0, strBytes.length);
                    buffer.putLong(fill);
                }
            }
            //array of long addresses
            for (int i = 0; i < this.childPosition.length; i++) {
                if (i < this.numChildren) {
                    //saves all children positions
                    buffer.putLong(this.childPosition[i]);
                } else {
                    //fills empty space
                    buffer.putLong(fill);
                }
            }
            //boolean
            if (this.isLeaf) {
                buffer.put((byte) 1);
            } else {
                buffer.put((byte) 0);
            }
        }

        public void recover(ByteBuffer buffer) {
            this.position = buffer.getLong();
            //2 ints-n, numChildren
            this.n = buffer.getInt();
            this.numChildren = buffer.getInt();
            //array of revLocations-fixed length string and a long
            RevLocation[] revLoc = new RevLocation[2 * minDegree - 1];
            for (int i = 0; i < (this.revLocation.length); i++) {
                if (i < n) {
                    //load all revLoc's
                    int size = buffer.getInt();
                    byte[] rawBytes = new byte[size];
                    buffer.get(rawBytes, 0, size);
                    this.revLocation[i] = new RevLocation(new String(rawBytes), buffer.getLong());
                } else {
                    //read and ignore empty space
                    int size = buffer.getInt();
                    byte[] rawBytes = new byte[size];
                    buffer.get(rawBytes, 0, size);
                    buffer.getLong();
                }
            }
            //array of long addresses
            for (int i = 0; i < (this.childPosition.length); i++) {
                if (i < numChildren) {
                    //read all children positions
                    this.childPosition[i] = buffer.getLong();
                } else {
                    //read and ignore empty space
                    buffer.getLong();
                }
            }
            //boolean
            byte b = buffer.get();
            if (b == 1) {
                this.isLeaf = true;
            } else {
                this.isLeaf = false;
            }
        }

    }

    private Node allocate() throws IOException {
        RandomAccessFile nodes = new RandomAccessFile("Nodes2.ser", "rw");
        Node n = new Node(false);
        nodes.seek(((numNodes++) * allocateNumBytes()));
        n.position = nodes.getFilePointer();
        diskWrite(n);
        return n;
    }

    private long allocateNumBytes() {
        //position is long = 8bytes
        long numBytes = 8;
        //array of revLocations- 22char string, 2bytes per char 
        //and an int saying how many bytes to read~48
        //and long positions, 8bytes each... 56 each revLoc
        numBytes += 56 * (2 * minDegree - 1);
        //array of long addresses =8bytes each
        numBytes += 8 * (2 * minDegree);
        //2 ints -2*4 bytes
        numBytes += 2 * 4;
        //boolean- 1 byte
        numBytes++;
        return numBytes;
    }

    private void diskWrite(Node x) throws IOException {
        RandomAccessFile nodes = new RandomAccessFile("Nodes2.ser", "rw");
        RandomAccessFile roo = new RandomAccessFile("RootLocation2.ser", "rw");
        if (x.position == -1) {
            x.position = nodes.getFilePointer();
        } else {
            nodes.seek(x.position);
        }
        // getting FileChannel from file
        FileChannel channel = nodes.getChannel();
        // creating and initializing ByteBuffer for reading/writing data
        ByteBuffer buffer = ByteBuffer.allocate((int) allocateNumBytes());
        // an instance of Persistable writing into ByteBuffer
        x.persist(buffer);
        // flip the buffer for writing into file
        buffer.flip();
        int numOfBytesWritten = channel.write(buffer); // writing into File
        //System.out.println("number of bytes written : " + numOfBytesWritten);
        channel.close(); // closing file channel
        //write root pointer to separate file
        if (root != null) {
            roo.writeLong(root.position);
        }
        //close files
        nodes.close();
        roo.close();
    }

    public Node diskRead(long position) throws IOException, ClassNotFoundException {
        RandomAccessFile nodes = new RandomAccessFile("Nodes2.ser", "rw");
        nodes.seek(position);
        // getting file channel
        FileChannel channel = nodes.getChannel();
        // preparing buffer to read data from file
        ByteBuffer buffer = ByteBuffer.allocate((int) allocateNumBytes());
        // reading data from file channel into buffer
        int numOfBytesRead = channel.read(buffer);
        //System.out.println("number of bytes read : " + numOfBytesRead);
        // You need to filp the byte buffer before reading
        buffer.flip();
        // Recovering object
        Node temp=new Node(false);
        temp.recover(buffer);
        channel.close();
        nodes.close();
        return temp;
    }
}
