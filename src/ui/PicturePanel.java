package ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

class PicturePanel extends JPanel {

    File folder = new File("F:/Eclipse/workspace/cbir/101_ObjectCategories/bonsai");
    File[] listOfFiles = folder.listFiles();
    ImageIcon[] img ;
    JComponent lblimg;
    ArrayList<JLabel> labels = new ArrayList<JLabel>();
    ArrayList<String> imgList = new ArrayList<String>();

    public PicturePanel() throws IOException {
    	super.setLayout(new FlowLayout());
    	super.setMinimumSize(new Dimension(1000,1000));
    	super.setPreferredSize(new Dimension(1000,1000));
//        for (int i = 0; i < listOfFiles.length; i++) {
//            
//            BufferedImage  img = ImageIO.read(new File("F:/Eclipse/workspace/cbir/101_ObjectCategories/bonsai/" + listOfFiles[i].getName().toString()));
//            Image resizedImage = 
//            	    img.getScaledInstance(100, 100, i);
//            ImageIcon im = new ImageIcon(resizedImage);
//            
//            JLabel l = new JLabel(im);
//            l.setSize(im.getIconWidth(), im.getIconHeight());
//            super.add(l);
//        }
//        for (JLabel label : labels) {
//			super.add(label);
//		}
    }
    public void showThumbnail(){
    	labels = new ArrayList<JLabel>();
    	for (int i = 0; i < imgList.size(); i++) {    
            BufferedImage img = null;
			try {
				img = ImageIO.read(new File(imgList.get(i)));
				Image resizedImage = 
	            	    img.getScaledInstance(100, 100, 1);
	            ImageIcon im = new ImageIcon(resizedImage);
	            
	            JLabel l = new JLabel(im);
	            l.setSize(im.getIconWidth(), im.getIconHeight());
	            labels.add(l);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	super.removeAll();
    	 for (int i=0;i<labels.size();i++){
             super.add(labels.get(i));
         }
          
    }
    public ArrayList<String> getImgList() {
		return imgList;
	}
	public void setImgList(ArrayList<String> imgList) {
		this.imgList = imgList;
	}
	public void addImage(String path){
    	imgList.add(path);
    }
}
