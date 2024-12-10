/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.batfeet;


import javax.swing.*;
public class Batfeet {
   

    public static void main(String[] args) {
        int screenheight =622;   //height of our screen
        int screenwidth=350;     //width of our screen
        JFrame Frame=new JFrame("Bat wings"); //creat the main Frame object
        Frame.setSize(screenwidth,screenheight);
        Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Frame.setResizable(false);          //so the user will not be able to resize the frame , that' will help me to keep the main size that i will set 
        Frame.setLocationRelativeTo(null);  //make the frame at centre of our computer screen
        
        
       
        
        FlappyBat Flappybird= new FlappyBat();
        Frame.add(Flappybird);
        Frame.pack();
        Flappybird.requestFocus();
        Frame.setVisible(true);             //set visbility of the frame so it will be visible to the user
           
    }
}
