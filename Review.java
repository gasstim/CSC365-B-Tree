/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc365lab2;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author tim
 */
public class Review implements Serializable {

    String revId, userId, businessId, textRev;
    int useful, funny, cool, stars;
    Date date;
    CustomHashMap revWords;
    
    public Review(){
        userId=businessId=textRev=revId=new String();
        useful=funny=cool=stars=0;
        date= new Date();
        revWords=new CustomHashMap();
    }

    public Review(String rId, String uId, String bId, int use, int fun, int coo, int star, Date dat, CustomHashMap hm) {
        revId = rId;
        userId = uId;
        businessId = bId;
        useful = use;
        funny = fun;
        cool = coo;
        stars = star;
        date = dat;
        revWords = hm;
    }

    public void persist(ByteBuffer buffer) {
        //rId     
        byte[] rIdBytes = this.revId.getBytes();
        buffer.putInt(rIdBytes.length);
        buffer.put(rIdBytes, 0, rIdBytes.length);
        //uId     
        byte[] uIdBytes = this.userId.getBytes();
        buffer.putInt(uIdBytes.length);
        buffer.put(uIdBytes, 0, uIdBytes.length);
        //bId     
        byte[] bIdBytes = this.businessId.getBytes();
        buffer.putInt(bIdBytes.length);
        buffer.put(bIdBytes, 0, bIdBytes.length);
        //int useful
        buffer.putInt(this.useful);
        //int funny
        buffer.putInt(this.funny);
        //int cool
        buffer.putInt(this.cool);
        //int stars
        buffer.putInt(this.stars);
        //date as long
        buffer.putLong(this.date.getTime());
        //hashmap of words       
        ArrayList words = this.revWords.returnAllObj();
        buffer.putInt(words.size());
        for (int i = 0; i < words.size(); i++) {
            Word temp = (Word) words.get(i);
            byte[] stringBytes = temp.word.getBytes();
            buffer.putInt(stringBytes.length);
            buffer.put(stringBytes, 0, stringBytes.length);
            buffer.putInt(temp.number_of_times_used);
        }
    }

    public void recover(ByteBuffer buffer) {
        //rId
        int size = buffer.getInt();
        byte[] rawBytes = new byte[size];
        buffer.get(rawBytes, 0, size);
        this.revId = new String(rawBytes);
        //uId     
        size = buffer.getInt();
        rawBytes = new byte[size];
        buffer.get(rawBytes, 0, size);
        this.userId = new String(rawBytes);
        //bId     
        size = buffer.getInt();
        rawBytes = new byte[size];
        buffer.get(rawBytes, 0, size);
        this.businessId = new String(rawBytes);
        //int useful
        this.useful = buffer.getInt();
        //int funny
        this.funny = buffer.getInt();
        //int cool
        this.cool = buffer.getInt();
        //int stars
        this.stars = buffer.getInt();
        //date as long
        this.date = new Date(buffer.getLong());
        //hashmap of words
        int temp = buffer.getInt();
        for (int i = 0; i < temp; i++) {
            size = buffer.getInt();
            rawBytes = new byte[size];
            buffer.get(rawBytes, 0, size);
            Word t = new Word(new String(rawBytes));
            t.number_of_times_used=buffer.getInt();
            if(t==null){
                System.out.println("word t is null");
            }if(this.revWords==null){
                System.out.println("revwords is null");
            }
            this.revWords.put(t.word, t);
        }
    }
}
