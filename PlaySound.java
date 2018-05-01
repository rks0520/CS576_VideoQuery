package cs576_videoquery;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * 
 * <Replace this with a short description of the class.>
 * 
 * @author Giulio
 */
public class PlaySound {

    private InputStream waveStream;
    //private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb
    private String soundfilename;
    private Clip soundclip = null;
    private int start_point = 0;

    /**
     * CONSTRUCTOR 
     */
    public PlaySound(String filename) 
    {
	this.soundfilename = filename;
    }
    public void pause()   {start_point = soundclip.getFramePosition();soundclip.stop();}
    public void stop()    {start_point = 0; soundclip.stop();}
    public void repeat()  {soundclip.loop(Clip.LOOP_CONTINUOUSLY);}
    
    public void play() throws PlayWaveException 
    {
	AudioInputStream audioInputStream = null;
        try { this.waveStream = new FileInputStream(this.soundfilename);}
        catch (FileNotFoundException fnfe) {fnfe.printStackTrace();}
        
	try 
        {
            //add buffer for mark/reset support, modified by Jian
            InputStream bufferedIn = new BufferedInputStream(this.waveStream);
            audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);	
	} 
        catch (UnsupportedAudioFileException e1) {throw new PlayWaveException(e1);}
	catch (IOException e1) { throw new PlayWaveException(e1);}
	
	// Obtain the information about the AudioInputStream
	//AudioFormat audioFormat = audioInputStream.getFormat();
	//Info info = new Info(SourceDataLine.class, audioFormat);

	// opens the audio channel
	//SourceDataLine dataLine = null;
	try 
        {
	    //dataLine = (SourceDataLine) AudioSystem.getLine(info);
	    //dataLine.open(audioFormat, this.EXTERNAL_BUFFER_SIZE);
            soundclip = AudioSystem.getClip();
	} 
        catch (LineUnavailableException e1) {throw new PlayWaveException(e1);}
	try {
		soundclip.open(audioInputStream);
		soundclip.setFramePosition(start_point);
		soundclip.loop(start_point);
		soundclip.start();
	} 
        catch (LineUnavailableException e1 ) { e1.printStackTrace();}
        catch (IOException e1){ e1.printStackTrace();}
        // Starts the music :P
        //	dataLine.start();
        //
        //	int readBytes = 0;
        //	byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];
        //
        //	try {
        //	    while (readBytes != -1) {
        //		readBytes = audioInputStream.read(audioBuffer, 0,
        //			audioBuffer.length);
        //		if (readBytes >= 0){
        //		    dataLine.write(audioBuffer, 0, readBytes);
        //		}
        //	    }
        //	} catch (IOException e1) {
        //	    throw new PlayWaveException(e1);
        //	} finally {
        //	    // plays what's left and and closes the audioChannel
        //	    dataLine.drain();
        //	    dataLine.close();
        //	}
    }   
}
