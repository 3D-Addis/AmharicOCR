package com.hareg.master.thesis.AmharicOCR.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.hareg.master.thesis.AmharicOCR.entity.Property;
import com.hareg.master.thesis.AmharicOCR.repository.ImageRepository;
import com.hareg.master.thesis.AmharicOCR.services.ImageProcessing;

import nu.pattern.OpenCV;

@BasePathAwareController
public class ImageUploadController {



    private final ImageRepository imageRepository;
    
    public ImageUploadController(final ImageRepository imageRepository) {
		this.imageRepository = imageRepository;
		
    }

	@RequestMapping(path="/image/upload", method=RequestMethod.POST)
	@ResponseBody
	public List<Property> uplaodImage(@RequestParam("file") MultipartFile file) throws IOException {

		System.out.println("Original Image Byte Size - " + file.getBytes().length);
		String folder = "C:\\Dev 2020\\angular-java\\OCR Tool for Hareg\\AmharicOCR\\AmharicOCR\\src\\main\\resources\\photos\\new.png";
		Files.write(Paths.get(folder), file.getBytes());

		
		OpenCV.loadShared();
		
		byte [] data = file.getBytes();
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		BufferedImage bImage2 = ImageIO.read(bis);
		
		return ImageProcessing.processImage(); //bImage2);
		
//		String default_file = "C:\\Users\\Yonas\\Pictures\\ocrTestData1.jpg";
//		Mat srcMat = Imgcodecs.imread(default_file);
//		
//		Property property = new Property();
//		List <Property> properties = new ArrayList <Property>();
//		properties.add(property);
//		
//		return properties;
	}
}