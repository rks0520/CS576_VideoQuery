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
    public static void main(String[] args) 
    {
        System.out.println("test");
	// get the command line parameters
	if (args.length < 1) {
	    System.err.println("usage: java -jar PlayWaveFile.jar [filename]");
	    return;
	}
	String queryVideoPath = System.getProperty("user.dir") + "/src/cs576_videoquery/query_videos/" + args[0];
        System.out.println("Path for the QueryVideo Folder: "+ queryVideoPath);
        File myFile = new File(queryVideoPath);
        String queryFolder = myFile.getName();
        System.out.println("Identified Query Folder: "+queryFolder);
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
}
