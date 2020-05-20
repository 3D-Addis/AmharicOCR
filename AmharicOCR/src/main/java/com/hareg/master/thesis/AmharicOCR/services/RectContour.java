package com.hareg.master.thesis.AmharicOCR.services;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;

public class RectContour implements Comparable<RectContour> {
	private Rect rect;
	private MatOfPoint contour;
	private Mat mat;
	
	private int rectX;
	private int rectY;
	private int rectRow;
	private int rectColumn;
	private String ocrString;
	private String columnName;

	public RectContour(Rect rect, MatOfPoint contour) {
		this.rect = rect;
		this.contour = contour;
		this.rectX = rect.x;
		this.rectY = rect.y;
		
//		rect.x = rect.x + 8;
//    	rect.width = rect.width - 16;
//    	rect.height = rect.height -10;
	}

	public String getColumnName() {
		return columnName;
	}
	
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getOcrString() {
		return ocrString;
	}

	public void setOcrString(String ocrString) {
		this.ocrString = ocrString;
	}

	public Rect getRect() {
		return rect;
	}
	
	
	public MatOfPoint getContour() {
		return contour;
	}

	public Mat getMat() {
		return mat;
	}

	public void setMat(Mat mat) {
		this.mat = mat;
	}

	public int getRectX() {
		return rectX;
	}

	public int getRectY() {
		return rectY;
	}

	public int getRectRow() {
		return rectRow;
	}

	public void setRectRow(int rectRow) {
		this.rectRow = rectRow;
	}

	public int getRectColumn() {
		return rectColumn;
	}

	public void setRectColumn(int rectColumn) {
		this.rectColumn = rectColumn;
	}

	@Override
	public int compareTo(RectContour rectContour) {
		int compareage=rectContour.getRectX();
		
        /* For Ascending order*/
        return this.rectX-compareage;

        /* For Descending order do like this */
        //return compareage-this.studentage;
	}

}
