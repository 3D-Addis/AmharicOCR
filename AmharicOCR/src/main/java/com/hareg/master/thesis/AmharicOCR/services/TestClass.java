package com.hareg.master.thesis.AmharicOCR.services;

import nu.pattern.OpenCV;


public class TestClass {
    public static void main(String[] args) {
        // Load the native library.
		OpenCV.loadShared();
        ImageProcessing.processImage("C:\\\\Users\\\\Yonas\\\\Pictures\\\\ocrTestData3.jpg");
    }
}
