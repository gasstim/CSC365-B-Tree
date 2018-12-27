/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc365lab2;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JComboBox;

/**
 *
 * @author tim
 */
public class CustomHashMap implements Serializable{
    static int currentSize=16; 
    Node [] table;
    int count;
    static final int InitialCap = 16;
    
    private void resize() {
        //System.out.println("Map is doubling with count: " + count);
        int in=0;
        //getting a temporary array of all nodes
        Node [] temp = new Node[count];
        for (Node t : table) {
            if(t!=null){
                for (Node e = t; e!=null; e=e.next) {
                    if(e==null){}else{
                    temp[in++] =e;
                    }
                }
            }
        }
        //resizing the table
        currentSize = currentSize*2;
        table = new Node[currentSize];
        //populating the table from the temporary array of nodes
        for(int i =0; i<temp.length; i++){
            if(temp[i]!=null){
                count=count-1;
                put(temp[i].key,temp[i].dataObj);
            }
        }
        //System.out.println("Map doubled in size to : "+currentSize);
    }
    
    static final class Node implements Serializable{
        final String key;
        int hash;
        Node next;
        Object dataObj;
        Node(String ke, Object obj, Node ne){
            this.key =ke; this.next=ne; this.dataObj=obj;
        }
        
    }
    
    CustomHashMap(){table=new Node[InitialCap]; currentSize=InitialCap;}
    
    Object get(String k){
        int h = k.hashCode();
        int i = h &(table.length-1);
        for(Node e =table[i]; e!=null; e=e.next){
            if(k.equals(e.key)){
                return e.dataObj;
            }
        } return (String) null;
    }
    
    void put(String k, Object v){
        int h = k.hashCode();
        int i = h&(table.length-1);
        for(Node e =table[i]; e!=null; e=e.next){
            if(k.equals(e.key)){
                e.dataObj = v;
                return;
            }
        }
        Node p=new Node(k,v,table[i]);
        table[i]=p;
        if(++count>3*table.length/4){
            resize();
        }
    }
    
    void printAll(){
        int t=0;
        for(int i=0; i<table.length; i++){
            for(Node e = table[i]; e!=null; e=e.next){
                System.out.println(e.key + " " + t++);
            }
        }
    }    
    
    ArrayList returnAllKeys(){
        ArrayList temp = new ArrayList();
        int t=0;
        for(int i=0; i<table.length; i++){
            for(Node e = table[i]; e!=null; e=e.next){
                temp.add(e.key);
            }
        }
        return temp;
    }
    
    ArrayList returnAllObj(){
        ArrayList temp = new ArrayList();
        for(int i=0; i<table.length; i++){
            for(Node e = table[i]; e!=null; e=e.next){
                temp.add(e.dataObj);
            }
        }
        return temp;
    }
}
