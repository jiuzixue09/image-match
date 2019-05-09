package com.dave.image.feature.utils;

import com.dave.image.feature.vo.Pixel;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Created by Dave on 2018/1/17.
 */
public class ImageUtil {
	public static double EPS = 1e-4;
	
    public static Pixel[][] getImagePixel(BufferedImage srcImg, int width, int height) {
        BufferedImage bi = null;
        try {
            bi = resizeImage(srcImg, width, height, BufferedImage.TYPE_INT_RGB);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        int minx = bi.getMinX();
        int miny = bi.getMinY();
        Pixel[][] rgbMatrix = new Pixel[width - minx][height - miny];
        for (int i = minx; i < width; i++) {
            for (int j = miny; j < height; j++) {
                int pixel = bi.getRGB(i, j);
                int red = (pixel & 0xff0000) >> 16;
                int green = (pixel & 0xff00) >> 8;
                int blue = (pixel & 0xff);
                Pixel p = new Pixel();
                p.red = red;
                p.green = green;
                p.blue = blue;
                rgbMatrix[i - minx][j - miny] = p;
            }
        }
        return rgbMatrix;
    }

    public static int[][] getGrayPixel(BufferedImage srcImg, int width, int height) {
        BufferedImage bi = null;
        try {
            bi = resizeImage(srcImg, width, height, BufferedImage.TYPE_INT_RGB);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        int minx = bi.getMinX();
        int miny = bi.getMinY();
        int[][] matrix = new int[width - minx][height - miny];
        for (int i = minx; i < width; i++) {
            for (int j = miny; j < height; j++) {
                int pixel = bi.getRGB(i, j);
                int red = (pixel & 0xff0000) >> 16;
                int green = (pixel & 0xff00) >> 8;
                int blue = (pixel & 0xff);
                int gray = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                matrix[i][j] = gray;
            }
        }
        return matrix;
    }

    public static BufferedImage resizeImage(BufferedImage srcImg, int width, int height, int imageType)
            throws IOException {
        if(srcImg == null) return null;
        BufferedImage buffImg = new BufferedImage(width, height, imageType);
        buffImg.getGraphics().drawImage(srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        return buffImg;
    }

    public static double calculateSimilarity(int[][] matrix1, int[][] matrix2) {
        return calculateSimilarity(FeatureUtil.matrix2vector(matrix1), FeatureUtil.matrix2vector(matrix2));
    }
    
    public static double calculateDistance(int[][] matrix1, int[][] matrix2) {
        return calculateDistance(FeatureUtil.matrix2vector(matrix1), FeatureUtil.matrix2vector(matrix2));
    }

    public static double calculateSimilarity(int[] vector1, int[] vector2) {
        if(vector1 == null || vector2 == null){
            throw new NullPointerException();
        }
        if(vector1.length != vector2.length){
            throw new RuntimeException("两向量长度不相等");
        }
        double len1 = 0, len2 = 0, numerator = 0;
        for (int i = 0; i < vector1.length; i++) {
            len1 += Math.pow(vector1[i], 2);
            len2 += Math.pow(vector2[i], 2);
            numerator += vector1[i] * vector2[i];
        }
        len1 = Math.sqrt(len1);
        len2 = Math.sqrt(len2);

        return numerator / (len1 * len2);
    }
    

	/**
	 * 向量范数
	 */
	public static double norm(int[] p) {
	    return norm(Arrays.stream(p).asDoubleStream().toArray());
	}

    public static double norm(double[] p) {
        double sum = IntStream.range(0, p.length).mapToDouble(it -> Math.pow(p[it], 2)).sum();
        return Math.sqrt(sum);
    }


    public static double calculateEuclideanSimilarity(int[][] matrix1, int[][] matrix2) {
        return calculateEuclideanSimilarity(FeatureUtil.matrix2vector(matrix1), FeatureUtil.matrix2vector(matrix2));
    }
	public static double calculateEuclideanSimilarity(int[] vector1, int[] vector2){
        double[] dVector1 = Arrays.stream(vector1).asDoubleStream().toArray();
        double[] dVector2 = Arrays.stream(vector2).asDoubleStream().toArray();

        return calculateEuclideanSimilarity(dVector1,dVector2);
	}

    public static double calculateEuclideanSimilarity(double[] vector1, double[] vector2) {
        double[] t = new double[vector1.length];
        for (int i = 0; i < t.length; i++) {
            t[i] = vector1[i] - vector2[i];
        }
        double norm_diff = norm(t);//欧式距离

        double norm1 = norm(vector1);
        double norm2 = norm(vector2);

        double d = norm_diff / (norm1 + norm2);
        return 1 - d;
    }

    
    public static double calculateDistance(int[] vector1, int[] vector2) {
        if(vector1 == null || vector2 == null){
            throw new NullPointerException();
        }
        if(vector1.length != vector2.length){
            throw new RuntimeException("两向量长度不相等");
        }
        
        double sum = IntStream.range(0, vector1.length).filter( i -> vector1[i] + vector2[i] != 0).mapToDouble(i -> Math.pow(vector1[i] - vector2[i], 2) / (vector1[i] + vector2[i])).sum();

        return 0.5 * sum * EPS;
    }

   	public static int getHammingDistance(String fromSignature, String toSignature) {
   		return getHammingDistance(new BigInteger(fromSignature), new BigInteger(toSignature));
   	}

    /**
     * 获得两个签名的汉明距离
     * @param fromSignature
     * @param toSignature
     * @return
     */
	public static int getHammingDistance(BigInteger fromSignature, BigInteger toSignature) {
		BigInteger x = fromSignature.xor(toSignature);
		int tot = 0;

		// 统计x中二进制位数为1的个数
		// 我们想想，一个二进制数减去1，那么，从最后那个1（包括那个1）后面的数字全都反了，
		// 对吧，然后，n&(n-1)就相当于把后面的数字清0，
		// 我们看n能做多少次这样的操作就OK了。

		while (x.signum() != 0) {
			tot += 1;
			x = x.and(x.subtract(new BigInteger("1")));
		}

		return tot;
	}

    /**
     * 用于计算pHash的相似度
     * 相似度为1时，图片最相似
     * @param str1
     * @param str2
     * @return
     */
    public static double calculateSimilarity(String str1, String str2) {
        if(FeatureUtil.isStringEmpty(str1) || FeatureUtil.isStringEmpty(str2)){
            throw new NullPointerException();
        }
        if(str1.length() != str2.length()) throw new IllegalArgumentException();
        int num = 0;
        for(int i = 0; i < str1.length(); i++){
            if(str1.charAt(i) == str2.charAt(i)){
                num++;
            }
        }
        return ((double)num) / str1.length();
    }

    /**
     * 离散余弦变换
     *
     * @param pix
     *            原图像的数据矩阵
     * @param n
     *            原图像(n*n)的高或宽
     * @return 变换后的矩阵数组
     */
    public static int[][] DCT(int[][] pix, int n) {
        double[][] iMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                iMatrix[i][j] = (double) (pix[i][j]);
            }
        }
        double[][] quotient = coefficient(n); // 求系数矩阵
        double[][] quotientT = transposingMatrix(quotient, n); // 转置系数矩阵

        double[][] temp = new double[n][n];
        temp = matrixMultiply(quotient, iMatrix, n);
        iMatrix = matrixMultiply(temp, quotientT, n);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                pix[i][j] = (int) (iMatrix[i][j]);
            }
        }
        return pix;
    }

    /**
     * 求离散余弦变换的系数矩阵
     *
     * @param n
     *            n*n矩阵的大小
     * @return 系数矩阵
     */
    private static double[][] coefficient(int n) {
        double[][] coeff = new double[n][n];
        double sqrt = 1.0 / Math.sqrt(n);
        for (int i = 0; i < n; i++) {
            coeff[0][i] = sqrt;
        }
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < n; j++) {
                coeff[i][j] = Math.sqrt(2.0 / n) * Math.cos(i * Math.PI * (j + 0.5) / (double) n);
            }
        }
        return coeff;
    }

    /**
     * 矩阵转置
     *
     * @param matrix
     *            原矩阵
     * @param n
     *            矩阵(n*n)的高或宽
     * @return 转置后的矩阵
     */
    private static double[][] transposingMatrix(double[][] matrix, int n) {
        double nMatrix[][] = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                nMatrix[i][j] = matrix[j][i];
            }
        }
        return nMatrix;
    }

    /**
     * 矩阵相乘
     *
     * @param A
     *            矩阵A
     * @param B
     *            矩阵B
     * @param n
     *            矩阵的大小n*n
     * @return 结果矩阵
     */
    private static double[][] matrixMultiply(double[][] A, double[][] B, int n) {
        double nMatrix[][] = new double[n][n];
        int t = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                t = 0;
                for (int k = 0; k < n; k++) {
                    t += A[i][k] * B[k][j];
                }
                nMatrix[i][j] = t;
            }
        }
        return nMatrix;
    }
}