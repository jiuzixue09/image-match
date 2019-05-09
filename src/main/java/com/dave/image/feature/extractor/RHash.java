package com.dave.image.feature.extractor;

import java.awt.image.BufferedImage;

import com.dave.image.feature.utils.ImageUtil;

/**
 * 旋转不变的感知哈希
 *
 * @author Dave
 *
 * Created by Dave on 2018/1/17.
 */
public class RHash extends ImageFeature{
	
    public RHash() {
		super();
		FEATURE_NAME = "rhash";
	}

	/**
     * 旋转不变性
     * @return
     */
    private String getFeature(int[][] matrix, double average) {
        // 半径
        String featureValue = "";
        int[] r = {2, 4, 6, 8};
        for(int i = 0; i < 4; i++){
            // 正方形左上角的点的下标
            int start = (8 - r[i]) / 2;
            int feature = 0;
            for(int j = start; j < start + r[i]; j++){
                feature = matrix[start][j] < average ? feature<<1 : (feature<<1)+1;
            }
            for(int j = start + 1; j < start + r[i]; j++){
                feature = matrix[j][start + r[i] - 1] < average ? feature<<1 : (feature<<1)+1;
            }
            for(int j = start + r[i] - 2; j >= start; j--){
                feature = matrix[start + r[i] - 1][j] < average ? feature<<1 : (feature<<1)+1;
            }
            for(int j = start + r[i] - 2; j > start; j--){
                feature = matrix[j][start] < average ? feature<<1 : (feature<<1)+1;
            }
            featureValue += getMinFeature(feature, 4 * (r[i] - 1));
        }
        return featureValue;
    }

    private String getMinFeature(int feature, int bitNum) {
        // 位数为bitNum的情况下的最大值
        int max = 1;
        for(int i = 1; i < bitNum; i++){
            max = (max << 1) + 1;
        }

        int min = feature;
        for(int i = 0; i < bitNum - 1; i++){
            // 循环右移一位
            feature = (feature>>1 | feature<<(bitNum - 1)) & max;
            if(feature < min) min = feature;
        }

        String result = "";
        for(int i = 0; i < bitNum; i++){
            if(min % 2 == 0){
                result = "0" + result;
            }
            else {
                result = "1" + result;
            }
            min >>= 1;
        }
        return result;
    }

    public void extract(BufferedImage srcImg) {
        // 缩小尺寸，简化色彩
        int[][] grayMatrix = ImageUtil.getGrayPixel(srcImg, 8, 8);
        // 缩小DCT，计算平均值
        if(grayMatrix == null || grayMatrix.length == 0 || grayMatrix[0].length == 0) return;
        int[][] newMatrix = new int[8][8];
        double average = 0;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                newMatrix[i][j] = grayMatrix[i][j];
                average += grayMatrix[i][j];
            }
        }
        average /= 64.0;
        this.featureValue = getFeature(newMatrix, average);
    }


}