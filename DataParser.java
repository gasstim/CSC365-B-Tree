/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc365lab2;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author tim
 */
public class DataParser {

    private RandomAccessFile revFile;
    BTree tree;

    public DataParser(BTree t) throws FileNotFoundException {
        this.tree = t;
    }

    //Gets the revFile, parses it, puts it into seperate files based on Keys and Values
    public void getData() throws FileNotFoundException, IOException, ParseException, org.json.simple.parser.ParseException, ClassNotFoundException {
        CustomHashMap wordMap;
        revFile = new RandomAccessFile("Reviews.ser", "rw");
        FileChannel channel = revFile.getChannel();
        String vFile = "review.json";
        String line = "";
        JSONParser parser = new JSONParser();
        try (BufferedReader br = new BufferedReader(new FileReader(vFile))) {
            int t = 0;
            while (((line = br.readLine()) != null && t < 1000000)) {
                t++;
                JSONObject obj1 = (JSONObject) parser.parse(line);
                wordMap = new CustomHashMap();
                //get each attribute and cast to the proper type
                String rId = (String) obj1.get("review_id");
                String uId = (String) obj1.get("user_id");
                String bId = (String) obj1.get("business_id");
                String rev = (String) obj1.get("text");
                Long usefu = (Long) obj1.get("useful");
                int useful = usefu.intValue();
                Long funn = (Long) obj1.get("funny");
                int funny = funn.intValue();
                Long coo = (Long) obj1.get("cool");
                int cool = coo.intValue();
                Long star = (Long) obj1.get("stars");
                int stars = star.intValue();
                String dates = (String) obj1.get("date");
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dates);
                String[] revArr = rev.split(" ");
                for (String word : revArr) {
                    //need to trim whitespace, remove as many characters as possible
                    // and convert to lower case to reduce duplicate words
                    word = word.toLowerCase();
                    word = word.replace(".", "");
                    word = word.replace(".", "");
                    word = word.replace(",", "");
                    word = word.replace("\"", "");
                    word = word.replace(":", "");
                    word = word.replace("!", "");
                    word = word.replace("?", "");
                    word = word.replace("(", "");
                    word = word.replace(")", "");
                    word = word.trim();
                    //check if word is in map, null if not.
                    Word f = (Word) wordMap.get(word);
                    if (f == null) {
                        Word w = new Word(word);
                        w.number_of_times_used++;
                        wordMap.put(word, w);
                    } else {
                        f.number_of_times_used++;
                        wordMap.put(word, f);
                    }
                }
                //now create the review and put into the tree
                Review r = new Review(rId, uId, bId, useful, funny, cool, stars, date, wordMap);
                long revPos = revFile.getFilePointer();
                // getting FileChannel from file
                //channel = revFile.getChannel();
                // creating and initializing ByteBuffer for reading/writing data
                ByteBuffer buffer = ByteBuffer.allocate(16384);
                // an instance of Persistable writing into ByteBuffer
                r.persist(buffer);
                // flip the buffer for writing into file
                buffer.flip();
                int numOfBytesWritten = channel.write(buffer); // writing into File
                //System.out.println("number of bytes written : " + numOfBytesWritten);
                //channel.close(); // closing file channel
                RevLocation rl = new RevLocation(rId, revPos);
                tree.insert(rl);
            }
            channel.close();
            revFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
