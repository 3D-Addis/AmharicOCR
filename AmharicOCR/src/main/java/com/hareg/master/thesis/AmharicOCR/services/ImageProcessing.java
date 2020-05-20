package com.hareg.master.thesis.AmharicOCR.services;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

	

	public static List<Property> processImage(String fileLocation) {
		
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		OpenCV.loadShared();
		
		// Convert Image to Mat before processing
		//Mat srcMat = convertImageToMat(fileLocation);
		//String default_file = "C:\\Users\\Yonas\\Pictures\\ocrTestData1.jpg";
		Mat srcMat = Imgcodecs.imread(fileLocation);
		
		// START PROCESS TO CLEAN UP IMAGE FOR TEXT EXTRACTION
		// Process image to make text more visible
		Mat processedForTextMat = makeTextMoreVisible(srcMat);

		// START PROCESS TO EXTRACT GRID LINES AND EXTRACT CONTOURS OUT OF THE LINES
		// Convert source mat to gray mat
		Mat grayMat = convertMatToGray(srcMat);

		// Convert gray mat to black and white mat
		Mat bwMat = convertMatToBlackAndWhite(grayMat);
		
        // Morphological Closing to get Contours easily
        Mat morphologyExMat = morphologicalClosing(bwMat, 9, 9);
        
		// Extract horizontal and vertical lines
		 Mat verticalAndHorizontalLineMat = extractVerticalAndHorizontalLines(bwMat);
		
			// Extract contours
			ArrayList<MatOfPoint> contours1 =  extractContours(morphologyExMat);
			ArrayList<MatOfPoint> contours2 =  extractContours(verticalAndHorizontalLineMat);
			
			// Filter contour based on size
			ArrayList<RectContour> selectedRectContours1 = filterContoursBasedOnSize(contours1, 35000, 3400, bwMat, srcMat, processedForTextMat);
			ArrayList<RectContour> selectedRectContours2=  filterContoursBasedOnSize(contours2, 35000, 3400, bwMat, srcMat, processedForTextMat);
			
			
			
			// Group contours on row
			List <Property> properties = new ArrayList<Property>();
//			HashMap<Integer, List<RectContour>> groupedContours = null;
			if (selectedRectContours1.size() > selectedRectContours2.size()) {
				//groupedContours = groupeContoursUsingVerticalAxis(selectedContours1, srcMat, processedForTextMat, 35000);
				findRelatedContours(selectedRectContours1,properties, processedForTextMat, 1);
			} else {
				//groupedContours = groupeContoursUsingVerticalAxis(selectedContours2, srcMat, processedForTextMat, 35000);
				findRelatedContours(selectedRectContours2,properties, processedForTextMat, 1);
			}

	    System.out.println("done prorcessing image" + "\u007c" + "\u005b");
	    
	    Collections.reverse(properties);
	    
	    int id = 1;
	    for (Property property : properties) {
	    	property.setId(id++);
	    }
		return properties;
	}

	private static BufferedImage Mat2BufferedImage(Mat matrix)throws Exception {        
        MatOfByte mob=new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        byte ba[]=mob.toArray();
        BufferedImage bi=ImageIO.read(new ByteArrayInputStream(ba));
        return bi;
    }

	private static List<Property> findRelatedContours(ArrayList<RectContour> orignalRectContours, List<Property> properties, Mat processedForTextMat, int propertyId ) {
		
		ArrayList<RectContour> matchingRectContours = new ArrayList<RectContour>();
		ArrayList<RectContour> unmatchedRectContours = new ArrayList<RectContour>();
		String[] propertyFileds = {"fileNumber", "seller", "discription", "address", "auctionDate", "auctionTime", "estimate"};
		Property property = null;
		
        Rect cRect = null;
		int yDiffrence = 0;
		
        Rect pRect = Imgproc.boundingRect(orignalRectContours.get(0).getContour());

		for(int contourNumber=0; contourNumber<orignalRectContours.size(); contourNumber++) { 
			RectContour cContoure = orignalRectContours.get(contourNumber);
			cRect = Imgproc.boundingRect(cContoure.getContour());
	        
			if (pRect != null) {
            	if (pRect.y > cRect.y) {
            		yDiffrence = pRect.y - cRect.y;
            	} else {
            		yDiffrence = cRect.y - pRect.y;
            	}
            }
            
            if(yDiffrence < 30) {
        		Rect cropedRect = new Rect(cRect.x + 6, cRect.y + 3 , cRect.width - 12, cRect.height - 10);
            	cContoure.setMat(processedForTextMat.submat(cropedRect));
            	matchingRectContours.add(cContoure);
            } else {
            	unmatchedRectContours.add(cContoure);
            }   
		}
		
        Collections.sort(matchingRectContours);
        
        if(matchingRectContours.size() > 6) {
        	if (matchingRectContours.size()==8) {
        		matchingRectContours.remove(0);
            }
        	
        	if (matchingRectContours.size()==7) {
        		int index = 0;
        		property = new Property();
        		for (RectContour rectContour : matchingRectContours) {
        			rectContour.setColumnName(propertyFileds[index]);
        			preformOcrForRectContour(rectContour);
        			addPropertyField(propertyFileds[index++], property, rectContour);
        		}
        		property.setId(propertyId++);
        		properties.add(property);
        	}
        }
        
        if(unmatchedRectContours.size() > 6) {
        	findRelatedContours(unmatchedRectContours, properties, processedForTextMat, propertyId);
        } 
        
		return properties;
	}

	private static void addPropertyField(String field, Property property, RectContour rectContour) {
		switch(field) {
		  case "fileNumber" :
			  property.setFileNumber(rectContour.getOcrString());
		    break;
		  case "seller" :
			  property.setSeller(rectContour.getOcrString());
		    break;
		  case "discription" :
			  property.setDiscription(rectContour.getOcrString());
			    break;
		  case "address"  :
			  property.setAddress(rectContour.getOcrString());;
		  		break;
		  case "auctionDate" :
			  property.setAuctionDate(rectContour.getOcrString());;
			    break;
		  case "auctionTime" :
			  property.setAuctionTime(rectContour.getOcrString());;
			    break;
		  case "estimate" :
			  property.setEstimate(rectContour.getOcrString());
			    break;
		  default :
		    // code block
		}
	}

	private static void preformOcrForRectContour(RectContour rectContour) {
		ITesseract instance = new Tesseract();
		String ocrOutputStr = "";
		Mat subMat = rectContour.getMat();
    	instance.setLanguage("amh");
		
		try {
//			Imgproc.resize(subMat, subMat, new Size(), 1.5, 1.5, Imgproc.INTER_AREA);
//			Imgproc.blur(subMat, subMat, new Size(1,1));
			ocrOutputStr = instance.doOCR(Mat2BufferedImage(subMat));
			ocrOutputStr.replace("\u007c", "").replace("\u005b", "").replace("\u005d", "").replace("\u003b", "").replace("\u002e", "");
			ocrOutputStr.replace("\u005b", "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//			System.out.println("Area : " + rectContour.getRect().area());
//			System.out.println("Width : " + rectContour.getRect().width);
//			System.out.println("Data from the Image is for column " + rectContour.getRectColumn() + ":\n" + ocrOutputStr);
//			showWaitDestroy("subMat", subMat);
		
		rectContour.setOcrString(ocrOutputStr);
	}

	private static Mat makeTextMoreVisible(Mat srcMat) {
		
		// Important and interesting read : Yonas Kebede
		// https://www.freecodecamp.org/news/getting-started-with-tesseract-part-ii-f7f9a0899b3f/

		Mat bluredMat = new Mat();
    	Mat grayMat = new Mat();
    	Mat bwMat = new Mat();
    	Mat morphMat = new Mat();
        Mat kernel1 = Mat.ones(1, 1, CvType.CV_8S);
    	
    	Imgproc.cvtColor(srcMat, grayMat, Imgproc.COLOR_BGR2GRAY);
//    	showWaitDestroy("grayMat", grayMat );
    	Imgproc.GaussianBlur(grayMat, bluredMat, new Size(1, 1), 0);
//    	Imgproc.GaussianBlur(bluredMat, bluredMat, new Size(1, 1), 0);
//    	showWaitDestroy("bluredMat 1", bluredMat );
    	//Core.addWeighted(srcMat, 1.5, bluredMat, -0.4, 30, bluredMat);
    	//showWaitDestroy("bluredMat 2", bluredMat );
    	Imgproc.threshold(bluredMat, bwMat, 245, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
//    	showWaitDestroy("bwMat", bwMat );
//    	Imgproc.threshold(bluredMat, bluredMat, 190,255, Imgproc.THRESH_BINARY);
    	  	
//    	Imgproc.GaussianBlur(grayMat, bluredMat, new Size(1, 1), 0);
    	morphMat = morphologicalClosing(bwMat, 1, 1);
//    	morphMat = morphologicalClosing(morphMat, 1, 1);
//    	Imgproc.dilate(morphMat, morphMat, kernel1);
//    	showWaitDestroy("morphMat", morphMat );

		return morphMat;
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

	private static ArrayList<RectContour> filterContoursBasedOnSize(List<MatOfPoint> contours, int hSize, int vSize, Mat bwMat, Mat srcMat, Mat processedForTextMat) {

		ArrayList<RectContour> selectedRectContours = new ArrayList<RectContour>();
		Mat copyMat = new Mat();
		srcMat.copyTo(copyMat);
		
        for(int i=0; i< contours.size(); i++) {
            if (Imgproc.contourArea(contours.get(i)) < hSize  && Imgproc.contourArea(contours.get(i)) > 3000) {
            	Rect rect = Imgproc.boundingRect(contours.get(i));
            	if (rect.width > 70 && rect.width < 300 && rect.area() < 35000) {
            		
            		
//            		selectedContours.add(contours.get(i));
		RectContour selectedRectContour = new RectContour(rect, contours.get(i));
		selectedRectContour.setMat(processedForTextMat.submat(rect));
		selectedRectContours.add(selectedRectContour);
		
	            	Imgproc.rectangle(copyMat, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(0,0,255));
//	            	System.out.println(rect.height);
//	            	System.out.println(rect.area() + "\n\n");
//	    showWaitDestroy("Image Box", copyMat);
            	}
            }
        }
		return selectedRectContours;
	}

	private static ArrayList<MatOfPoint> extractContours(Mat mat) {
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.adaptiveThreshold(mat, mat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, -2);
		Imgproc.findContours(mat, contours, new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
		return contours;
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

//	private static void showWaitDestroy(String winname, Mat img) {
//        HighGui.imshow(winname, img);
//        HighGui.moveWindow(winname, 500, 0);
//        HighGui.waitKey(0);
//        HighGui.destroyWindow(winname);
//    }
	
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

 //       showWaitDestroy("horizontal", horizontal);
        
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

 //       showWaitDestroy("vertical", vertical );
        
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
}
