package com.firstbrave.image_match;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import com.dave.image.feature.extractor.AnnularColorLayoutHistogram;
import com.dave.image.feature.extractor.ColorCoherenceVector;
import com.dave.image.feature.extractor.DHash;
import com.dave.image.feature.extractor.ImageFeature;
import com.dave.image.feature.extractor.HSVColorHistogram;
import com.dave.image.feature.extractor.PHash;
import com.dave.image.feature.extractor.RHash;
import com.dave.image.feature.utils.ImageUtil;

public class TestImageMatch {
	public static void main(String[] args) throws IOException {
		File image1 = new File("C:\\Users\\Administrator\\Desktop\\5.jpg");
		BufferedImage img1 = ImageIO.read(image1);
		File image2 = new File("C:\\Users\\Administrator\\Desktop\\2.jpg");
		BufferedImage img2 = ImageIO.read(image2);
		//1e4f270797cb6eb676524db4da5a5b2fbb59796b66da68692dade58dada5836db4a4b6f61687969295b692531b3d1f5e5a49d2dbc9ccccce4d6d672643efa783531b2db69b9b0976da4d2c2cbe4b0d8da492793c3cbe0b2797f6d24
		ImageFeature annularColorLayoutHistogram = new AnnularColorLayoutHistogram();
		ImageFeature colorCoherenceVector = new ColorCoherenceVector();
		ImageFeature hsvColorHistogram = new HSVColorHistogram();
		ImageFeature pHash = new PHash();
		ImageFeature rHash = new RHash();
		ImageFeature dHash = new DHash();
		
		System.out.println(image1.getName() + "\t" + image2.getName());
		annularColorLayoutHistogram.extract(img1);
		int[][] vector1 = annularColorLayoutHistogram.getMatrixFeature();
		
		annularColorLayoutHistogram.extract(img2);
		int[][] vector2 = annularColorLayoutHistogram.getMatrixFeature();
		
		double d = ImageUtil.calculateDistance(vector1, vector2);
		System.out.println("环形颜色分布直方图:" + d);
		
		colorCoherenceVector.extract(img1);
		vector1 = colorCoherenceVector.getMatrixFeature();
		
		colorCoherenceVector.extract(img2);
		vector2 = colorCoherenceVector.getMatrixFeature();
		
		d = ImageUtil.calculateDistance(vector1, vector2);
		System.out.println("颜色聚合向量:" + d);
		
		hsvColorHistogram.extract(img1);
		vector1 = hsvColorHistogram.getMatrixFeature();
		
		hsvColorHistogram.extract(img2);
		vector2 = hsvColorHistogram.getMatrixFeature();
		
		d = ImageUtil.calculateDistance(vector1, vector2);
		System.out.println("HSV颜色直方图-Chi-square:" + d);
		
		d = ImageUtil.calculateEuclideanSimilarity(vector1, vector2);
		System.out.println("HSV颜色直方图:-euclidean:" + d);
		
		d = ImageUtil.calculateDistance(vector1[0], vector2[0]);
		System.out.println("H颜色直方图:" + d);
		
		d = ImageUtil.calculateDistance(vector1[1], vector2[1]);
		System.out.println("S颜色直方图:" + d);
		
		d = ImageUtil.calculateDistance(vector1[2], vector2[2]);
		System.out.println("V颜色直方图:" + d);
		
		dHash.extract(img1);
		String str1 = dHash.getStringFeature();
		dHash.extract(img2);
		String str2 =  dHash.getStringFeature();
		
		d = ImageUtil.calculateSimilarity(str1, str2);
		System.out.println("dhash:" + d);
		String f1 = new BigInteger(str1, 2).toString(16);
		String f2 = new BigInteger(str2, 2).toString(16);
		System.out.println(f1);
		System.out.println(f2);
		System.out.println(str2);
		System.out.println(str1);
		System.out.println(new BigInteger(f1,16).toString(2));

		pHash.extract(img1);
		str1 = pHash.getStringFeature();
		pHash.extract(img2);
		str2 = pHash.getStringFeature();
		
		d = ImageUtil.calculateSimilarity(str1, str2);
		System.out.println("phash:" + d + "\t" +  str1 + "\t" + str2);
		
		rHash.extract(img1);
		str1 = rHash.getStringFeature();
		rHash.extract(img2);
		str2 = rHash.getStringFeature();
		
		d = ImageUtil.calculateSimilarity(str1, str2);
		System.out.println("rhash:" + d + "\t" +  str1 + "\t" + str2);
		
	
	}

}
