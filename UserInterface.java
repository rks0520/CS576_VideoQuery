/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs576_videoquery;



import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class UserInterface extends Frame implements ActionListener {

    public enum PlayStatus{PLAY, PAUSE, STOP};
    
    class VideoObject{
        
        ArrayList<BufferedImage> images;
        Thread videoThread;
        Thread audioThread;
        PlaySound playSound;
        
        int currentFrameNum;
        PlayStatus playStatus = PlayStatus.STOP;
        
        String filename;
        
        JLabel imageLabel;
        Button PlayButton;
        Button PauseButton;
        Button StopButton;
        Button loadButton;
        
        Panel panel;
        
        /*CONSTRUCTOR*/
        
        VideoObject(UserInterface ui, String CategoryLabel){
            panel = new Panel();
            
            JLabel imageLabel = new JLabel(CategoryLabel);
            panel.add(imageLabel);
            
            loadButton = new Button("Load Video");
            loadButton.addActionListener(ui); 
            
            PlayButton = new Button("PLAY");
	    PlayButton.addActionListener(ui);
            
            PauseButton = new Button("PAUSE");
	    PauseButton.addActionListener(ui);
            
            StopButton = new Button("STOP");
	    StopButton.addActionListener(ui);
        }
        
        public void setImages(ArrayList<BufferedImage> imgs){
            this.images = imgs;
            currentFrameNum = 0;
            imageLabel = new JLabel(new ImageIcon(images.get(currentFrameNum)));
        }
    }
    
//PLAYBACK DATA
    
    VideoObject queryVideo;
    VideoObject resultVideo;
    
    static final int frameRate = 30;
    
    private String queryFileFolder = System.getProperty("user.dir") + "/src/cs576_videoquery/query_videos";
    private String dbFileFolder = System.getProperty("user.dir") + "/src/cs576_videoquery/database_videos";
    
    static final int WIDTH = 352;
    static final int HEIGHT = 288;
    
    
//SEARCH RESULT DATA
    
    private Map<String, Double> resultMap;
    private Map<String, Double> sortedResultMap;
    private ArrayList<Double> resultList;
    private ArrayList<String> resultListRankedNames;
    
    //private videosearch searchClass;
    private Map<String, Integer> similarFrameMap;

//TEXT LABELS

    private JLabel errorLabel;
    private String errorsg;   //What does this mean?
    
    private TextField queryField;
    
    private List resultListDisplay;

    private Button searchButton;

//FUNCTIONS
    
    public UserInterface(ArrayList<BufferedImage> imgs, String path) {
	
        queryVideo = new VideoObject(this, "Query:");
        queryVideo.setImages(imgs);
        
	queryField = new TextField(13);
	queryVideo.panel.add(queryField);
        queryVideo.panel.add(queryVideo.loadButton);
        
	errorLabel = new JLabel("");
	errorLabel.setForeground(Color.RED);
	    
        searchButton = new Button("Search");
	searchButton.setFont(new Font("monspaced", Font.BOLD, 60));
	searchButton.addActionListener(this);
	queryVideo.panel.add(errorLabel);
        
	Panel searchPanel = new Panel();
	searchPanel.add(searchButton);
	    
        Panel controlQueryPanel = new Panel();
	controlQueryPanel.setLayout(new GridLayout(2, 0));
	controlQueryPanel.add(queryVideo.panel);
	
        controlQueryPanel.add(searchPanel);
	add(controlQueryPanel, BorderLayout.WEST);
	  
        resultVideo = new VideoObject(this, "Result:");

	resultListDisplay = new List(7);
	resultListDisplay.add("Matched Videos:    ");
	resultList = new ArrayList<Double>(7);
	resultListRankedNames = new ArrayList<String>(7);

	resultVideo.panel.add(resultListDisplay, BorderLayout.SOUTH);

	resultVideo.panel.add(resultVideo.loadButton);
	add(resultVideo.panel, BorderLayout.EAST);
	    
	//Video List Panel
	Panel listPanel = new Panel();
	listPanel.setLayout(new GridLayout(2, 2));
        loadVideo(queryVideo, path);
	
        Panel queryImagePanel = new Panel();
	queryImagePanel.add(queryVideo.imageLabel);
	
        Panel resultImagePanel = new Panel();
	resultImagePanel.add(queryVideo.imageLabel);
	
        listPanel.add(queryImagePanel);
        listPanel.add(resultImagePanel);
	    
	    //Control Panel
	    Panel controlPanel = new Panel();
	    Panel resultControlPanel = new Panel();
	  
	    controlPanel.add(queryVideo.PlayButton);
	    resultControlPanel.add(resultVideo.PlayButton);
	   
	    controlPanel.add(queryVideo.PauseButton);
	    resultControlPanel.add(resultVideo.PauseButton);
	   
	    controlPanel.add(queryVideo.StopButton);
	    resultControlPanel.add(resultVideo.StopButton);
	    resultControlPanel.add(errorLabel);
	    
	    listPanel.add(controlPanel);
	    listPanel.add(resultControlPanel);
            
	    add(listPanel, BorderLayout.SOUTH);
	    
	    //searchClass = new compareAndsearch();
//	    try {
//			searchClass.init();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
            
	}
	
	public void setImages(ArrayList<BufferedImage> images){
		this.queryVideo.setImages(images);
	}
	
	public void showUI() {
	    pack();
	    setVisible(true);
	}
	
        private void playVideo(VideoObject vo){
                vo.videoThread = new Thread() {
                    public void run() {
                        System.out.println("Start playing video: " + vo.filename);

                        for (int i = vo.currentFrameNum; i < vo.images.size(); i++) {
                            vo.imageLabel.setIcon(new ImageIcon(vo.images.get(i)));
                            try {
                                sleep(1000/frameRate);
                            } 
                            catch (InterruptedException e) {
                                if(vo.playStatus == PlayStatus.STOP) {
                                    vo.currentFrameNum = 0;
                                    vo.playSound.stop();
                                } else {
                                    vo.currentFrameNum = i;
                                }

                                vo.imageLabel.setIcon(new ImageIcon(vo.images.get(vo.currentFrameNum)));
                                currentThread().interrupt();

                                break;
                            }
                        }
                        if(vo.playStatus != PlayStatus.STOP && vo.playStatus ==PlayStatus.PLAY) {
                            vo.playStatus = PlayStatus.STOP;
                            vo.currentFrameNum = 0;
                            System.out.println("End playing video: " + vo.filename);
                            currentThread().interrupt();
                            vo.playSound.stop();
                        }
                    }
            };
                
            vo.audioThread = new Thread() {
                public void run() {
                    try {
                        vo.playSound.play();
                    } 
                    catch (PlayWaveException e) {
                        e.printStackTrace();
                        errorLabel.setText(e.getMessage());
                        return;
                    }
                }
            };
	    vo.audioThread.start();
	    vo.videoThread.start();
        }

	
	private void pauseVideo(VideoObject vo) throws InterruptedException {
		if(vo.videoThread != null) {
			vo.videoThread.interrupt();
			vo.audioThread.interrupt();
			vo.playSound.pause();
		}
	}
	
	
        public void stopVideo(VideoObject vo){
            if(vo.videoThread != null){
                vo.videoThread.interrupt();
                vo.audioThread.interrupt();
                vo.playSound.stop();
                vo.currentFrameNum = 0;
                vo.videoThread = null;
                vo.audioThread = null;
            }
            else{
                vo.currentFrameNum = 0;
            }
        }
	
	
//	private void updateSimilarFrame() {
//		int userSelect = resultListDisplay.getSelectedIndex() - 1;
//		String userSelectStr = resultListRankedNames.get(userSelect);
//		Integer frm = similarFrameMap.get(userSelectStr);
//		errorsg = "The most similar clip is from frame " + (frm+1) + " to frame " + (frm+151) + ".";
//	    Thread initThread = new Thread() {
//            public void run() {
//            	errorLabel.setText(errorsg);  	   
//	        }
//	    };
//	    initThread.start();
//	}
	
	private void loadVideo(VideoObject vo, String userInput) {
            System.out.println("Start loading query video contents.");
            vo.filename = userInput;
                
	    try {
	      if(userInput == null || userInput.isEmpty()){
	    	  return;
	      }
	      //every query video in has 150 frames
	      vo.images = new ArrayList<BufferedImage>();
	      for(int i=1; i<=150; i++) {
	    	  String fileNum = "00";
	    	  if(i < 100 && i > 9) {
	    		  fileNum = "0";
	    	  } else if(i > 99) {
	    		  fileNum = "";
	    	  }
	    	  String fullName = queryFileFolder + "/" + userInput + "/" + userInput +fileNum + new Integer(i).toString() + ".rgb";
	    	  String audioFilename = queryFileFolder + "/" + userInput + "/" + userInput + ".wav";
	    	  
	    	  File file = new File(fullName);
	    	  InputStream is = new FileInputStream(file);

	   	      long len = file.length();
		      byte[] bytes = new byte[(int)len];
		      int offset = 0;
	          int numRead = 0;
	          while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	              offset += numRead;
	          }
	    	  int index = 0;
	          BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	          for (int y = 0; y < HEIGHT; y++) {
	            for (int x = 0; x < WIDTH; x++) {
	   				byte r = bytes[index];
	   				byte g = bytes[index+HEIGHT*WIDTH];
	   				byte b = bytes[index+HEIGHT*WIDTH*2]; 
	   				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
	    			image.setRGB(x,y,pix);
	    			index++;
	    		}
	    	  }
	          vo.images.add(image);
	          is.close();
	          vo.playSound = new PlaySound(audioFilename);
	      }//end for
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	      errorLabel.setText(e.getMessage());
	    } catch (IOException e) {
	      e.printStackTrace();
	      errorLabel.setText(e.getMessage());
	    }
	    vo.playStatus = PlayStatus.STOP;
	    vo.currentFrameNum = 0;
            
            
	    System.out.println("End loading query video contents.");
	}
	
	
//	private void loadDBVideo(String dbVideoName) {
//		System.out.println("Start loading db video contents.");
//	    try {
//	      if(dbVideoName == null || dbVideoName.isEmpty()){
//	    	  return;
//	      }
//	      //every query video in has 600 frames
//	      dbImages = new ArrayList<BufferedImage>();
//	      for(int i=1; i<=600; i++) {
//	    	  String fileNum = "00";
//	    	  if(i < 100 && i > 9) {
//	    		  fileNum = "0";
//	    	  } else if(i > 99) {
//	    		  fileNum = "";
//	    	  }
//	    	  String fullName = dbFileFolder + "/" + dbVideoName + "/" + dbVideoName + fileNum + new Integer(i).toString() + ".rgb";
//	    	  String audioFilename = dbFileFolder + "/" + dbVideoName + "/" + dbVideoName + ".wav";
//	    	  
//	    	  File file = new File(fullName);
//	    	  InputStream is = new FileInputStream(file);
//
//	   	      long len = file.length();
//		      byte[] bytes = new byte[(int)len];
//		      int offset = 0;
//	          int numRead = 0;
//	          while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
//	              offset += numRead;
//	          }
//	          System.out.println("Start loading frame: " + fullName);
//	    	  int index = 0;
//	          BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
//	          for (int y = 0; y < HEIGHT; y++) {
//	            for (int x = 0; x < WIDTH; x++) {
//	   				byte r = bytes[index];
//	   				byte g = bytes[index+HEIGHT*WIDTH];
//	   				byte b = bytes[index+HEIGHT*WIDTH*2]; 
//	   				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
//	    			image.setRGB(x,y,pix);
//	    			index++;
//	    		}
//	    	  }
//	          dbImages.add(image);
//	          is.close();
//	          playDBSound = new PlaySound(audioFilename);
//	          System.out.println("End loading db frame: " + fullName);
//	      }//end for
//	    } catch (FileNotFoundException e) {
//	      e.printStackTrace();
//	      errorLabel.setText(e.getMessage());
//	    } catch (IOException e) {
//	      e.printStackTrace();
//	      errorLabel.setText(e.getMessage());
//	    }
//	    this.resultPlayStatus = PlayStatus.STOP;
//	    currentDBFrameNum = 0;
//	    displayDBScreenShot();
//	    System.out.println("End loading db video contents.");
//	}
	
	@Override
	public void actionPerformed(ActionEvent e) { 
		if(e.getSource() == queryVideo.PlayButton /*|| e.getSource() == this.resultVideo.PlayButton*/) {
                        VideoObject vo;
                        if(e.getSource() == queryVideo.PlayButton)
                            vo = queryVideo;
                        else
                            vo = resultVideo;
                        
			System.out.println("play button clicked");
			if(vo.playStatus != PlayStatus.PLAY) {
				vo.playStatus = PlayStatus.PLAY;
				playVideo(vo);
			}
		}  
                
                else if(e.getSource() == queryVideo.PauseButton /*|| e.getSource() == resultVideo.PauseButton*/) {
                        VideoObject vo;
                        if(e.getSource() == queryVideo.PauseButton)
                            vo = queryVideo;
                        else
                            vo = resultVideo;
                    
			System.out.println("pause button clicked");
			if(vo.playStatus == PlayStatus.PLAY) {
				vo.playStatus = PlayStatus.PAUSE;
				try {
					pauseVideo(vo);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					errorLabel.setText(e1.getMessage());
				}
			}
		} 
                
                else if(e.getSource() == queryVideo.StopButton /*|| e.getSource() == resultVideo.StopButton*/) {
			VideoObject vo;
                        if(e.getSource() == queryVideo.StopButton)
                            vo = queryVideo;
                        else
                            vo = resultVideo;
                        
                        System.out.println("stop button clicked");
			if(vo.playStatus != PlayStatus.STOP) {
				vo.playStatus = PlayStatus.STOP;
				stopVideo(vo);
			}
		} 
		
                else if(e.getSource() == queryVideo.loadButton /*|| e.getSource() == this.resultVideo.loadButton*/) {
                        VideoObject vo;
                        if(e.getSource() == queryVideo.loadButton){
                            vo = queryVideo;
                            String userInput = queryField.getText();
                            if(userInput != null && !userInput.isEmpty()) {
				vo.videoThread = null;
				vo.audioThread = null;
				loadVideo(vo, userInput.trim()); 
                            }
                        }
                        else
                            vo = resultVideo;
		} 
                        //else if(e.getSource() == this.searchButton){
//			String userInput = queryField.getText();
//			if(userInput.trim().isEmpty()) {
//				return;
//			}
//			resultMap = searchClass.search(userInput.trim());
//			resultListDisplay.removeAll();
//		    resultListDisplay.add("Matched Videos:    ");
//		    resultList = new ArrayList<Double>(7);
//		    resultListRankedNames = new ArrayList<String>(7);
//			sortedResultMap = new HashMap<String, Double>();
//		    
//		    Iterator it = resultMap.entrySet().iterator();
//		    while (it.hasNext()) {
//		        Entry pair = (Entry)it.next();
//		        String videoName = (String)pair.getKey();
//		        Double videoRank = new BigDecimal((Double)pair.getValue()).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
//		        resultList.add(videoRank);
//		        sortedResultMap.put(videoName, videoRank);
//		    }
//		    Collections.sort(resultList);
//		    Collections.reverse(resultList);
//		    for(int i=0; i<resultList.size(); i++) {
//		    	Double tmpRank = resultList.get(i);
//		    	it = sortedResultMap.entrySet().iterator();
//			    while (it.hasNext()) {
//			    	Entry pair = (Entry)it.next();
//			    	Double videoRank = (Double)pair.getValue();
//			    	if(videoRank == tmpRank) {
//			    		resultListDisplay.add(pair.getKey() + "   " + (videoRank * 100) + "%");
//			    		resultListRankedNames.add((String)pair.getKey());
//			    		break;
//			    	}
//			    }
//		    }
//		    similarFrameMap = searchClass.framemap;
		//} 
//                else if(e.getSource() == this.loadResultButton) {
//			int userSelect = resultListDisplay.getSelectedIndex() - 1;
//			if(userSelect > -1) {
//				this.playingDBThread = null;
//				this.audioDBThread = null;
//				this.loadDBVideo(resultListRankedNames.get(userSelect));
//				this.updateSimilarFrame();
//			}
		}
	
}


