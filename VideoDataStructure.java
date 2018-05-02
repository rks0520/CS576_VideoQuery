/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs576_videoquery;
import java.util.Arrays;
import java.util.ArrayList;
/**
 *
 * @author johnpaulfrancis
 */
public class VideoDataStructure {
    
    String title;
    ArrayList<Frame> frames;
    
    public VideoDataStructure(){
        frames = new ArrayList<Frame>();
    }
    
    public void addFrame(){
        frames.add(new Frame());
    }
    
    public void readInSound(){
        
    }
    
    class Frame{
        ArrayList<Integer> r;
        ArrayList<Integer> g;
        ArrayList<Integer> b;
        
        int sound;
        
        Frame(){
            r = new ArrayList<Integer>();
            g = new ArrayList<Integer>();
            b = new ArrayList<Integer>();
            
            sound = 0;
        }
        
        public void addPixel(int red, int green, int blue){
           r.add(red);
           g.add(green);
           b.add(blue);
        }
    }
}    
