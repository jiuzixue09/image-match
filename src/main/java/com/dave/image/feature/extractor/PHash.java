package com.dave.image.feature.extractor;


import java.awt.image.BufferedImage;

import com.dave.image.feature.utils.ImageUtil;

/**
 * pHash
 * 参考链接：http://blog.csdn.net/zouxy09/article/details/17471401
 * http://blog.csdn.net/luoweifu/article/details/8220992
 *
 * @author Dave
 *
 * Created by Dave on 2018/1/17.
 */
public class PHash extends ImageFeature{
    public PHash() {
		super();
		FEATURE_NAME = "phash";
	}

    public void extract(BufferedImage srcImg) {
        // 缩小尺寸，简化色彩
        int[][] grayMatrix = ImageUtil.getGrayPixel(srcImg, 32, 32);
        // 计算DCT
        grayMatrix = ImageUtil.DCT(grayMatrix, 32);
        // 缩小DCT，计算平均值
        int[][] newMatrix = new int[8][8];
        double average = 0;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                newMatrix[i][j] = grayMatrix[i][j];
                average += grayMatrix[i][j];
            }
        }
        average /= 64.0;
        // 计算hash值
        String hash = "";
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(newMatrix[i][j] < average){
                    hash += '0';
                }
                else{
                    hash += '1';
                }
            }
        }
        this.featureValue = hash;
    }

}