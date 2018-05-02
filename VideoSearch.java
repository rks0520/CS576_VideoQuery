/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs576_videoquery;
import java.util.ArrayList;
/**
 *
 * @author Rachit
 */
public class VideoSearch {
    
    ArrayList<RankedVideo> RankedVideos = new ArrayList<RankedVideo>();
    
    class RankedVideo{
        VideoDataStructure video;
        long bestError;
        int bestErrorIndex;
        
        RankedVideo(VideoDataStructure v, long error, int errorIndex){
            video = v;
            bestError = error;
            bestErrorIndex = errorIndex;
        }
    }
    
    public static void compare(VideoDataStructure query, ArrayList<VideoDataStructure> databaseVids){
        
        for(VideoDataStructure v : databaseVids){
            
            int range = v.frames.size() - query.frames.size();
            
            long minError = 9223372036854775807L;
            int minErrorIndex = 0;
                
            for(int i=0; i<range; i++){     //iterate through starting Windows
                
                long runningError = 0;
                
                for(int j=i; (j-i) < query.frames.size(); j++){   //iterate through frames
                    
                    for(int k = 0; k < query.frames.get(j-i).r.size(); k++){    //iterate through pixels
                            
                        runningError += (v.frames.get(i).r.get(k) - query.frames.get(j-i).r.get(k)) * (v.frames.get(i).r.get(k) - query.frames.get(j-i).r.get(k));
                        runningError += (v.frames.get(i).g.get(k) - query.frames.get(j-i).g.get(k)) * (v.frames.get(i).g.get(k) - query.frames.get(j-i).g.get(k));
                        runningError += (v.frames.get(i).b.get(k) - query.frames.get(j-i).b.get(k)) * (v.frames.get(i).b.get(k) - query.frames.get(j-i).b.get(k));
                        
                    }
                    
                    if(runningError > minError) //This window will not give the best value
                        break;
                }
                
                if(runningError < minError){ //This window is the best value so far
                    minError = runningError;
                    minErrorIndex = i;
                }
            }
            
            System.out.println(v.title);
            System.out.println(minError);
            System.out.println(minErrorIndex);
            System.out.println();
            
            //RankedVideos.add(new RankedVideo(v, minError, minErrorIndex));
        }
    }
    
}
