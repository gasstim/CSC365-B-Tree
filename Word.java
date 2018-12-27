/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc365lab2;

import java.io.Serializable;

/**
 *
 * @author tim
 */
public class Word implements Serializable{
    String word;
    int number_of_times_used;
    public Word(String w){
        word = w;
        number_of_times_used = 0;
    }
}
