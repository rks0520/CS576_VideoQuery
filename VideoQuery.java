package cs576_videoquery;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * plays a wave file using PlaySound class
 * 
 * @author Giulio
 */
public class VideoQuery {

    /**
     * <Replace this with one clearly defined responsibility this method does.>
     * 
     * @param args
     *            the name of the wave file to play
     */
    
    static final int width = 352;
    static final int height = 288;
    static String databasePath = System.getProperty("user.dir") + "/src/cs576_videoquery/database_videos/";
    static String queryPath = System.getProperty("user.dir") + "/src/cs576_videoquery/query_videos/";
    
    static ArrayList<VideoDataStructure> VideoStructures = new ArrayList<VideoDataStructure>();
    static VideoDataStructure queryVDS = new VideoDataStructure();
    
    public static void main(String[] args) 
    {
	if (args.length < 1) {
	    System.err.println("usage: java -jar PlayWaveFile.jar [filename]");
	    return;
	}
                
	String queryVideoPath = queryPath + args[0];
        System.out.println("Path for the QueryVideo Folder: "+ queryVideoPath);
        
        File queryVideoFile = new File(queryVideoPath);
        String queryFolder = queryVideoFile.getName();

        ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
        
        try
        {
            String firstFile = getFirstFile(queryVideoPath);
            System.out.println("Identified First Frame: "+firstFile);
            
            String firstFrameofQuery = queryVideoPath + "//" + firstFile;
            System.out.println("Identified First Frame Path: "+firstFrameofQuery);
            
            File firstFrameofQueryFile = new File(firstFrameofQuery);
            InputStream is = new FileInputStream(firstFrameofQueryFile);

            long len = firstFrameofQueryFile.length();
            byte[] bytes = new byte[(int)len];
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) 
            {
                offset += numRead;
            }

            System.out.println("Frame Load Start:" + firstFrameofQueryFile);
            int ind = 0;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < height; y++) 
            {
                for (int x = 0; x < width; x++) 
                {
                    byte r = bytes[ind];
                    byte g = bytes[ind+height*width];
                    byte b = bytes[ind+height*width*2]; 
                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    image.setRGB(x,y,pix);
                    ind++;
                }
            }
            frames.add(image);
            is.close();
            System.out.println("Frame Load End: " + firstFrameofQueryFile);
        } 
        catch (FileNotFoundException e) { e.printStackTrace();}
        catch (IOException e) { e.printStackTrace();}
        
        loadDatabaseFiles();
        loadQueryVideoAsVDS(args[0]);
        
        VideoSearch.compare(queryVDS, VideoStructures);
        
        UserInterface userInterface = new UserInterface(frames, args[0]);
	userInterface.showUI();
    }

    //Method to get the first file of the directory for the query Video
    public static String getFirstFile(String directory)
    {
        File dirName = new File(directory);
        String[] children = dirName.list();
        if (children == null) {return "";}
        else 
        {
            int i=0;
            String filename = children[i];
            while (i<children.length && !filename.contains(".rgb"))
            {
                i++;
                filename = children[i];
            }
            return filename;
        }
    } 
    
    public static void loadDatabaseFiles(){
        
        File databaseVideosFile = new File(databasePath);
        String[] videos = databaseVideosFile.list();
        
        
        
        System.out.println(databasePath);
        System.out.println("Reading in Database Files:");
        
        String fullName = "";
        
        for(int i=0; i<videos.length; i++){
            System.out.println(videos[i]);
            if(!videos[i].equals(".DS_Store")){
                String videoPath = databasePath + videos[i] + "/";

                File databaseVideoFile = new File(videoPath);
                String[] frames = databaseVideoFile.list();

                VideoStructures.add(new VideoDataStructure());
                VideoStructures.get(VideoStructures.size()-1).title = videos[i];

                try {

                    String filename = getFirstFile(videoPath);
                    //System.out.println("Identified First Frame: "+ filename);
                    
                    for(int j=1; j<frames.length-1; j++) {

                        String firstFrameName = videoPath + "/" + filename;

                        File firstFrameFile = new File(firstFrameName);

                        //System.out.println("Database Loading Frame:" + fullName);

                        String fileNum = "00";
                        if(j < 100 && j > 9) {
                            fileNum = "0";
                        } else if(j > 99) {
                            fileNum = "";
                        }

                        fullName = databasePath + videos[i] + "/" + videos[i] +fileNum + new Integer(j).toString() + ".rgb";
                        File file = new File(fullName);

                        InputStream is = new FileInputStream(file);

                        long len = firstFrameFile.length();
                        byte[] bytes = new byte[(int)len];
                        int offset = 0;
                        int numRead = 0;
                        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) 
                        {
                            offset += numRead;
                        }

                        int ind = 0;

                        VideoStructures.get(VideoStructures.size()-1).addFrame();

                        for (int y = 0; y < height; y++) 
                        {
                            for (int x = 0; x < width/5; x++) 
                            {
                                VideoStructures.get(VideoStructures.size()-1).frames.get(j-1).addPixel(bytes[ind], bytes[ind+height*width], bytes[ind+height*width*2]);
                                ind+=5;
                            }
                        }

                        is.close();
                    }
                }
                catch (FileNotFoundException e) { e.printStackTrace();}
                catch (IOException e) { e.printStackTrace();}
            }
	    System.out.println("End loading database video contents.");
	}
            
    }
    
    public static void loadQueryVideoAsVDS(String video){
            
                String videoPath = queryPath + video + "/";

                queryVDS.title = video;

                try {

                    String filename = getFirstFile(videoPath);
                    
                    for(int i=1; i <= 150; i++) {
                        
                        String firstFrameName = videoPath + "/" + filename;

                        File firstFrameFile = new File(firstFrameName);

                        //System.out.println("Database Loading Frame:" + fullName);

                        String fileNum = "00";
                        if(i < 100 && i > 9) {
                            fileNum = "0";
                        } else if(i > 99) {
                            fileNum = "";
                        }

                        String fullName = queryPath + video + "/" + video +fileNum + new Integer(i).toString() + ".rgb";
                        File file = new File(fullName);

                        InputStream is = new FileInputStream(file);

                        long len = firstFrameFile.length();
                        byte[] bytes = new byte[(int)len];
                        int offset = 0;
                        int numRead = 0;
                        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) 
                        {
                            offset += numRead;
                        }

                        int ind = 0;

                        queryVDS.addFrame();

                        for (int y = 0; y < height; y++) 
                        {
                            for (int x = 0; x < width/5; x++) 
                            {
                                queryVDS.frames.get(i-1).addPixel(bytes[ind], bytes[ind+height*width], bytes[ind+height*width*2]);
                                ind+=5;
                            }
                        }

                        is.close();
               
                    }
                }
                
                catch (FileNotFoundException e) { e.printStackTrace();}
                catch (IOException e) { e.printStackTrace();}
                
                System.out.println("End loading query video VDS.");
    }
}
