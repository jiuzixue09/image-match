package com.dave.image.feature.extractor;

import java.awt.image.BufferedImage;

import com.dave.image.feature.utils.FeatureUtil;

/**
 * Created by Dave on 2018/1/17
 */
public abstract class ImageFeature {
	protected int[][] featureMatrix = null;
	protected String featureValue = null;
	protected String FEATURE_NAME = null;
	public static final int PURECOLOUR = 20000;
	
	 /**
     * 该特征提取算法处理的图像的固定宽高
     * 即在特征提取前，需要先把图像重置为固定尺寸
     **/
    protected int WIDTH = 200, HEIGHT = 200;
    
    /** 同心圆个数 */
    protected int N = 10;
    
    protected int BIN_WIDTH = 4;
    
	public abstract void extract(BufferedImage srcImg);


    public int[] getVectorfeature() {
		if (this instanceof PHash || this instanceof RHash) {
			throw new RuntimeException("请调用getStringFeature");
		}
		if(this.featureMatrix == null){
            throw new RuntimeException("该对象还未提取图像的特征值，请先调用extract方法提取图像的特征值");
        }
    	return FeatureUtil.matrix2vector(featureMatrix);
    }

    public String getStringFeature() {
		if (this instanceof PHash || this instanceof RHash || this instanceof DHash) {
			 if(this.featureValue == null){
		            throw new RuntimeException("该对象还未提取图像的特征值，请先调用extract方法提取图像的特征值");
		        }
			return featureValue;
		}
        if(this.featureMatrix == null){
            throw new RuntimeException("该对象还未提取图像的特征值，请先调用extract方法提取图像的特征值");
        }
        return FeatureUtil.matrix2string(this.featureMatrix);
    }

    public int[][] getMatrixFeature() {
		if (this instanceof PHash || this instanceof RHash || this instanceof DHash) {
			throw new RuntimeException("请调用getStringFeature");
		}
    	if(this.featureMatrix == null){
            throw new RuntimeException("该对象还未提取图像的特征值，请先调用extract方法提取图像的特征值");
        }
    	return featureMatrix;
    }

    public String getFeatureName() {
        return FEATURE_NAME;
    }

	public boolean isPureColour() {
		for (int i = 0; i < featureMatrix.length; i++) {
			if(featureMatrix[i][0] > PURECOLOUR) return true;
		}
		
		int tmp = 0;
		for (int i = 0; i < featureMatrix[0].length; i++) {
			if(featureMatrix[0][i] > 10000) tmp += featureMatrix[0][i];
		}
		if(tmp > PURECOLOUR) return true;
		return false;
	}
    
}