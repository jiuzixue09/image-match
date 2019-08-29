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
    	for (int i = 1; i < matrix.length - 1; i++) {
			for (int j = 1; j < matrix[0].length - 1; j++) {

				for (int k = -1; k < 2; k++) {
					append(matrix[ i + 1][j + 1], average[i + k][j -1], sb);
					if(k != 0) append(matrix[ i + 1][j + 1], average[i + k][j], sb);
					append(matrix[ i + 1][j + 1], average[i + k][j +1], sb);

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
        int[][] grayMatrix = ImageUtil.getGrayPixel(srcImg, WIDTH, HEIGHT);
        // 计算均值
        if(grayMatrix == null || grayMatrix.length == 0 || grayMatrix[0].length == 0) return;
        double[][] average = new double[WIDTH][HEIGHT];

        for(int i = 0; i < HEIGHT - 1; i++){
            for(int j = 0; j < WIDTH - 1; j++){
            	for (int k = -1; k < 2; k++) {
            		if(i + k < 0) continue;
            		if(j - 1 > -1) average[i][j] += grayMatrix[i + k][j -1];

            		average[i][j] += grayMatrix[i + k][j];

            		average[i][j] += grayMatrix[i + k][j + 1];

				}
            	average[i][j] /= 9;
            }
        }
        
        
        this.featureValue = getFeature(grayMatrix, average);
    }


}