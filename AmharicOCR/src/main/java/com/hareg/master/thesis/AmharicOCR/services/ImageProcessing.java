package com.hareg.master.thesis.AmharicOCR.services;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.hareg.master.thesis.AmharicOCR.entity.Property;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import nu.pattern.OpenCV;

public class ImageProcessing {

	public static List<Property> processImage() {
		
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		OpenCV.loadShared();
		
		// Convert Image to Mat before processing
//		Mat srcMat = convertImageToMat(image);
		String default_file = "C:\\Users\\Yonas\\Pictures\\ocrTestData1.jpg";
		Mat srcMat = Imgcodecs.imread(default_file);
		
		// START PROCESS TO CLEAN UP IMAGE FOR TEXT EXTRACTION
		// Process image to make text more visible
		Mat processedForTextMat = makeTextMoreVisible(srcMat);
        //showWaitDestroy("srcMat", srcMat);

		// START PROCESS TO EXTRACT GRID LINES AND EXTRACT CONTOURS OUT OF THE LINES
		// Convert source mat to gray mat
		Mat grayMat = convertMatToGray(srcMat);
        //showWaitDestroy("grayMat", grayMat);

		// Convert gray mat to black and white mat
		Mat bwMat = convertMatToBlackAndWhite(grayMat);
        //showWaitDestroy("bwMat", bwMat);
		
        // Morphological Closing to get Contours easily
        Mat morphologyExMat = morphologicalClosing(bwMat, 9, 9);
//        showWaitDestroy("morphologyEx", morphologyExMat);
        
		// Extract horizontal and vertical lines
		// Mat verticalAndHorizontalLineMat = extractVerticalAndHorizontalLines(bwMat);
        // showWaitDestroy("verticalAndHorizontalLineMat", verticalAndHorizontalLineMat);
		
		// Extract contours
		List<MatOfPoint> contours =  extractContours(morphologyExMat);
		
		// Filter contour based on size
		List<MatOfPoint> selectedContours = filterContoursBasedOnSize(contours, 35000, 7000, bwMat, srcMat);
		
		// Group contours on row
		HashMap<Integer, List<RectContour>> groupedContours = groupeContoursUsingVerticalAxis(selectedContours, srcMat, processedForTextMat);
		
		// START EXTRACTING TEXT
		List <Property> properties = extractTextFromImage(processedForTextMat, srcMat, groupedContours);
		
		return properties;
	}
    
    private static Mat morphologicalClosing(Mat src, int rows, int cols) {
        //Creating destination matrix
        Mat dst = new Mat(src.rows(), src.cols(), src.type());
        //Preparing the kernel matrix object
        Mat kernel = Mat.ones(rows, cols, CvType.CV_8U);
        //Applying dilate on the Image
        Imgproc.morphologyEx(src, dst, Imgproc.MORPH_CLOSE, kernel);
        
		return dst;
	}

	private static void showWaitDestroy(String winname, Mat img) {
        HighGui.imshow(winname, img);
        HighGui.moveWindow(winname, 500, 0);
        HighGui.waitKey(0);
        HighGui.destroyWindow(winname);
    }

	private static List<Property> extractTextFromImage(Mat processedForTextMat, Mat srcMat, HashMap<Integer, List<RectContour>> groupedContours) {
        

        List <Property> properties = new ArrayList<Property>();
		for (int index=1; index<=groupedContours.size(); index++) {
			Property property = processData(groupedContours.get(index), processedForTextMat);
			property.setId(index);
			properties.add(property);
		}
		return properties;
	}
	
	private static Property processData(List<RectContour> list, Mat processedForTextMat) {	    	
    	// Create JNA Interface Mapping
		ITesseract instance = new Tesseract();
		String str;
		Property property = new Property();
		for(int i=1; i<=list.size(); i++) {
//			Rect rect = Imgproc.boundingRect(list.get(i-1));
//			rect.x = rect.x + 3;
//        	rect.width = rect.width - 6;
//        	rect.height = rect.height -20;
//        	Mat subMat = processedForTextMat.submat(rect);
        	//Imgproc.rectangle(srcMat, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(0,0,255));
			
			Mat subMat = list.get(i-1).getMat();
			
        	instance.setLanguage("amh");
			
			try {
				str = instance.doOCR(Mat2BufferedImage(subMat));
				//System.out.println("Data from the Image is for column " + i  + ":\n" + str);
				//showWaitDestroy("subMat", subMat);

				str = str.replace("|", "").replace("[", "").replace("]", "");

				if (i == 1) {
					property.setFileNumber(str);;
				} else if (i == 2) {
					property.setSeller(str);
				}  else if (i == 3) {
					property.setDiscription(str);
				}  else if (i == 4) {
					property.setAddress(str);;
				}  else if (i == 5) {
					property.setAuctionDate(str);;
				}  else if (i == 6) {
					property.setAuctionTime(str);;
				}  else if (i == 7) {
					property.setEstimate(str);;
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return property;
	}

	private static BufferedImage Mat2BufferedImage(Mat matrix)throws Exception {        
        MatOfByte mob=new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        byte ba[]=mob.toArray();

        BufferedImage bi=ImageIO.read(new ByteArrayInputStream(ba));
        return bi;
    }
	
	private static HashMap<Integer, List<RectContour>> groupeContoursUsingVerticalAxis(List<MatOfPoint> selectedContours, Mat srcMat, Mat processedForTextMat) {
        
		OpenCV.loadShared();
		
		int yDiffrence = 0;
        Rect pRect = null;
        Rect cRect = null;
        int rowNumber = 6;
        int columnNumber = 1;
        //List<MatOfPoint> rowOfContours = new ArrayList<MatOfPoint>();
		ArrayList<RectContour> rectContours = new ArrayList<RectContour>();
        HashMap<Integer, List<RectContour>> groupedContours = new HashMap<Integer, List<RectContour>>();
        RectContour rectContour;
        
        Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(srcMat, srcMat, 190,255, Imgproc.THRESH_BINARY);
        //srcMat = morphologicalClosing(srcMat, 1, 1);
        //showWaitDestroy("+++++++", srcMat);
        
        

		//Mat processedMat = morphologicalClosing(processedForTextMat, 3, 3);
		//showWaitDestroy("last", processedMat);
        for(int contourNumber=0; contourNumber< selectedContours.size(); contourNumber++) {
            cRect = Imgproc.boundingRect(selectedContours.get(contourNumber));
            
            if (pRect != null) {
            	if (pRect.y > cRect.y) {
            		yDiffrence = pRect.y - cRect.y;
            	} else {
            		yDiffrence = cRect.y - pRect.y;
            	}
            }
            
	        if (columnNumber == 1) {
	            pRect = cRect;
        		rectContour = new RectContour(cRect, selectedContours.get(contourNumber));
        		//Rect rect = Imgproc.boundingRect(list.get(i-1));
        		rectContour.setMat(processedForTextMat.submat(cRect));
        		rectContours.add(rectContour);
	            //rowOfContours.add(selectedContours.get(contourNumber));
	            columnNumber++;
	        } else if(pRect!=null && rowNumber>0 && yDiffrence < 15) {
        		rectContour = new RectContour(cRect, selectedContours.get(contourNumber));
        		rectContour.setMat(processedForTextMat.submat(cRect));
        		rectContours.add(rectContour);
	            //rowOfContours.add(selectedContours.get(contourNumber));
	            columnNumber++;
	            if (rectContours.size() == 7) {
	            	Collections.sort(rectContours);
	            	groupedContours.put(rowNumber--, rectContours);
	            	rectContours = new ArrayList<RectContour>();;
		            //rowOfContours.clear();
		            columnNumber = 1;
	            }
	        } else {
	            pRect = cRect;
	            rectContours = new ArrayList<RectContour>();;
        		rectContour = new RectContour(cRect, selectedContours.get(contourNumber));
        		rectContour.setMat(processedForTextMat.submat(cRect));
        		rectContours.add(rectContour);
//	            rowOfContours.clear();
//	            rowOfContours.add(selectedContours.get(contourNumber));
	            columnNumber = 2;
            }
        }
        groupedContours.get(1);
		return groupedContours;
	}

	private static Mat makeTextMoreVisible(Mat srcMat) {

		Mat bluredMat = new Mat();
    	Mat grayMat = new Mat();
    	Mat bwMat = new Mat();
        Mat kernel1 = Mat.ones(1, 1, CvType.CV_8S);
    	
    	Imgproc.cvtColor(srcMat, grayMat, Imgproc.COLOR_BGR2GRAY);
    	Imgproc.GaussianBlur(grayMat, bluredMat, new Size(1, 1), 0);
 //   	Core.addWeighted(srcMat, 1.5, bluredMat, -0.4, 30, bluredMat);
    	//Imgproc.threshold(bwMat, bwMat, 255, 245, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
    	Imgproc.threshold(bluredMat, bluredMat, 190,255, Imgproc.THRESH_BINARY);
    	
    	
    	Imgproc.GaussianBlur(grayMat, bluredMat, new Size(1, 1), 0);
    	bluredMat = morphologicalClosing(bluredMat, 1, 1);
    	Imgproc.dilate(bluredMat, bluredMat, kernel1);
    	Imgproc.dilate(bluredMat, bluredMat, kernel1);
    	Imgproc.dilate(bluredMat, bluredMat, kernel1);
    	
    	//showWaitDestroy("makeTextMoreVisible", bluredMat );
		return bluredMat;
	}

	private static List<MatOfPoint> filterContoursBasedOnSize(List<MatOfPoint> contours, int hSize, int vSize, Mat bwMat, Mat srcMat) {

//        int columnNumber = 1;
		List<MatOfPoint> selectedContours = new ArrayList<MatOfPoint>();
//		ArrayList<RectContour> rectContours = new ArrayList<RectContour>();
//		Mat processedMat = morphologicalClosing(bwMat, 3, 3);
		
        for(int i=0; i< contours.size(); i++) {
            if (Imgproc.contourArea(contours.get(i)) < hSize  && Imgproc.contourArea(contours.get(i)) > vSize) {
            	Rect rect = Imgproc.boundingRect(contours.get(i));
            	if (rect.width > 70) {
//            		
//            		RectContour rectContour = new RectContour(rect, contours.get(i));
//            		rectContour.setMat(processedMat.submat(rect));
            		
            		
            		selectedContours.add(contours.get(i));
	            	Imgproc.rectangle(srcMat, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(0,0,255));
//	            	System.out.println(rect.width);
	            	
            	}
            }
        }
	    //showWaitDestroy("Image Box", srcMat);
		return selectedContours;
	}

	private static List<MatOfPoint> extractContours(Mat mat) {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.adaptiveThreshold(mat, mat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, -2);
		Imgproc.findContours(mat, contours, new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
		return contours;
	}

	private static Mat extractVerticalAndHorizontalLines(Mat mat) {
        /*
         *  - Create the images that will use to extract the horizontal and vertical lines
         */
        Mat horizontal = mat.clone();
        Mat vertical = mat.clone();
        
        
        /*
         *  - Specify size on horizontal axis
         *  - Create structure element for extracting horizontal lines through morphology operations
         *  - Apply morphology operations
         *  - Show extracted horizontal lines
         */
        int horizontal_size = horizontal.cols() / 150;
        Mat horizontalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(horizontal_size,1));
        Imgproc.dilate(horizontal, horizontal, horizontalStructure);
        Imgproc.erode(horizontal, horizontal, horizontalStructure);

        showWaitDestroy("horizontal", horizontal);
        
        /*
         *  - Specify size on vertical axis
         *  - Create structure element for extracting vertical lines through morphology operations
         *  - Apply morphology operations
         *  - Show extracted vertical lines
         *  - Inverse vertical image and show
         */
        int vertical_size = vertical.rows() / 150;
        Mat verticalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size( 1,vertical_size));
        Imgproc.dilate(vertical, vertical, verticalStructure);
        Imgproc.erode(vertical, vertical, verticalStructure);

        showWaitDestroy("vertical", vertical );
        
        /*
         * Get the summation for vertical and horizontal
         * 
         */
        double alpha = 0.5;
        double beta =  1.0 - alpha ;
        Mat dst = new Mat();
        Core.addWeighted( horizontal, alpha, vertical, beta, 10.0, dst);
        
        
        /*
         *  Extract edges and smooth image according to the logic
         *  1. extract edges
         *  2. dilate(edges)
         *  3. src.copyTo(smooth)
         *  4. blur smooth img
         *  5. smooth.copyTo(src, edges)
         */
        Mat kernel1 = Mat.ones(1, 1, CvType.CV_8UC1);
        Imgproc.dilate(dst, dst, kernel1);
        
        // Step 1
        Mat edges = new Mat();
        Imgproc.adaptiveThreshold(dst, edges, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 3, -2);

        //showWaitDestroy("edges1", edges );
        
        // Step 2
        Mat kernel2 = Mat.ones(1, 1, CvType.CV_8UC1);
        Imgproc.dilate(edges, edges, kernel2);

        //showWaitDestroy("edges2", edges );
        
        // Step 3
        Mat smooth = new Mat();
        dst.copyTo(smooth);
        
        // Step 4
        Imgproc.blur(smooth, smooth, new Size(1, 1));
        
        // Step 5
        smooth.copyTo(dst, edges);
        
		return smooth;
	}

	private static Mat convertMatToBlackAndWhite(Mat mat) {
        Mat bw = new Mat();
        Core.bitwise_not(mat, mat);
        Imgproc.adaptiveThreshold(mat, bw, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, -2);
		return mat;
	}

	private static Mat convertMatToGray(Mat mat) {
        Mat gray = new Mat();
        if (mat.channels() == 3) {
            Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
        } else {
            gray = mat;
        }
		return gray;
	}

	private static Mat convertImageToMat(BufferedImage inBuffImg) {

		/*
		 * Because the data type of the different images differ.
		 * For one you have DataBufferByte, for other you may have DataBufferInt.
		 * You can create an new BufferedImage of same size with type 3BYTE_BGR, 
		 * and then draw the original image into it, then you can construct 
		 * a Mat from this new one. You can also use different supported Mat image
		 *  type instead of CvType.CV_8UC3, but that depends if there are 
		 *  equivalent types for java ones.
		 */
		
		
	    BufferedImage image = new BufferedImage(inBuffImg.getWidth(), inBuffImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
	    Graphics2D g2d= image.createGraphics();
	    g2d.drawImage(inBuffImg, 0, 0, null);
	    g2d.dispose();

	    Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
	    byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	    mat.put(0, 0, data);

	    return mat;
//		// Here we convert into *supported* format
//		BufferedImage imageCopy =
//		    new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
//		imageCopy.getGraphics().drawImage(image, 0, 0, null);
//
//		byte[] data = ((DataBufferByte) imageCopy.getRaster().getDataBuffer()).getData();  
//		Mat src = new Mat(image.getHeight(),image.getWidth(), CvType.CV_8UC3);
//		src.put(0, 0, data);
	}
}
