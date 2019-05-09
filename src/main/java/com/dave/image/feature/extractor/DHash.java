package com.dave.image.feature.extractor;

import java.awt.image.BufferedImage;

import com.dave.image.feature.utils.ImageUtil;

/**
 *
 * @author Dave
 *
 * Created by Dave on 2018/1/17.
 */
public class DHash extends ImageFeature{
	
    public DHash() {
		super();
		FEATURE_NAME = "dhash";
		WIDTH = 9;
		HEIGHT = 9;
	}

    private String getFeature(int[][] matrix, double[][] average) {
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < average.length; i++) {
			for (int j = 0; j < average[0].length; j++) {
				
				for (int k = 0; k < 3; k++) {
					append(matrix[i + k][j], average[i][j], sb);
					append(matrix[i + k][j + 1], average[i][j], sb);
					append(matrix[i + k][j + 2], average[i][j], sb);
				}
			}
		}
        return sb.toString();
    }
    
    private void append(int i, double j, StringBuffer sb) {
    	if(i >=j) sb.append("1");
    	else sb.append("0");
    }



    public void extract(BufferedImage srcImg) {
        // 缩小尺寸，简化色彩
        int[][] grayMatrix = ImageUtil.getGrayPixel(srcImg, WIDTH + 2, HEIGHT + 2);
        // 缩小DCT，计算平均值
        if(grayMatrix == null || grayMatrix.length == 0 || grayMatrix[0].length == 0) return;
        double[][] average = new double[WIDTH][HEIGHT];
        for(int i = 0; i < WIDTH; i++){
            for(int j = 0; j < HEIGHT; j++){
            	for (int k = 0; k < 3; k++) {
            		average[i][j] += grayMatrix[i + k][j];
            		average[i][j] += grayMatrix[i + k][j + 1];
            		average[i][j] += grayMatrix[i + k][j + 2];
				}
            	average[i][j] /= 9;
            }
        }
        
        
        this.featureValue = getFeature(grayMatrix, average);
    }


}