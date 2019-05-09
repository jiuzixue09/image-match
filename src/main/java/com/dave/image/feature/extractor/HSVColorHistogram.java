package com.dave.image.feature.extractor;



import java.awt.Color;
import java.awt.image.BufferedImage;

import com.dave.image.feature.vo.HSV;
import com.dave.image.feature.utils.ImageUtil;
import com.dave.image.feature.vo.Pixel;

/**
 * 该颜色直方图更接近于人类对图片的识别程度
 * @author Dave
 *
 * Created by Dave on 2018/0/17.
 */
public class HSVColorHistogram extends ImageFeature{
    public HSVColorHistogram() {
		super();
		FEATURE_NAME = "hsvch";
	}

	public void extract(BufferedImage srcImg) {
        // 获取RGB矩阵
        Pixel[][] matrix = ImageUtil.getImagePixel(srcImg, WIDTH, HEIGHT);

        // 转化为HSV矩阵
        if(matrix == null || matrix.length == 0 || matrix[0].length == 0) return;
        HSV[][] hsvMatrix = new HSV[matrix.length][];
        for(int i = 0; i < matrix.length; i++){
            hsvMatrix[i] = new HSV[matrix[i].length];
            for(int j = 0; j < matrix[i].length; j++){
                float[] fs = Color.RGBtoHSB(matrix[i][j].red, matrix[i][j].green, matrix[i][j].blue, null);
                HSV hsv = new HSV();
                hsv.h = (int)(fs[0] * 255);
                hsv.s = (int)(fs[1] * 255);
                hsv.v = (int)(fs[2] * 255);
                hsvMatrix[i][j] = hsv;
            }
        }

        // 统计
        int[][] histogram = new int[3][256];
        for(int i = 0; i < hsvMatrix.length; i++){
            for(int j = 0; j < hsvMatrix[0].length; j++){
                histogram[0][hsvMatrix[i][j].h]++;
                histogram[1][hsvMatrix[i][j].s]++;
                histogram[2][hsvMatrix[i][j].v]++;
            }
        }
        this.featureMatrix = histogram;
    }

}