package com.dave.image.feature.extractor;


import java.awt.image.BufferedImage;

import com.dave.image.feature.utils.ImageUtil;

/**
 * 颜色聚合向量
 * 参考链接：http://www.docin.com/p-396527256.html
 *
 * @author Dave
 *
 * Created by Dave on 2018/1/17
 */
public class ColorCoherenceVector extends ImageFeature{

    public ColorCoherenceVector() {
		super();
		WIDTH = 50;
		HEIGHT = 50;
		FEATURE_NAME = "ccv";
	}

	/**
     * 对矩阵进行分组
     * @param matrix
     * @param groupNums
     * @return 返回总共的组别数量
     */
    private int groupMatrix(int[][] matrix, int[][] groupNums) {
        if(matrix == null || matrix.length == 0 || matrix[0].length == 0) return 0;
        if(groupNums == null || groupNums.length == 0 || groupNums[0].length == 0) return 0;
        // 初始化，标为-1表示未进行分组的像素
        for(int i = 0; i < groupNums.length; i++){
            for(int j = 0; j < groupNums[0].length; j++){
                groupNums[i][j] = -1;
            }
        }

        int groupNum = 0;
        for(int i = 0; i < groupNums.length; i++){
            for(int j = 0; j < groupNums[0].length; j++){
                if(groupNums[i][j] < 0){
                    // 该像素点未进行分组，对其进行分组
                    groupNums[i][j] = groupNum;
                    recursive(matrix, i, j, groupNum, groupNums);
                    groupNum++;
                }
            }
        }
        return groupNum + 1;
    }

    /**
     * 递归查找与当前像素点拥有相同像素值的点，并对其进行分组
     * @param matrix
     * @param i
     * @param j
     * @param groupNum
     * @param groupNums
     */
    private void recursive(int[][] matrix, int i, int j, int groupNum, int[][] groupNums){
        if(matrix == null || matrix.length == 0 || matrix[0].length == 0) return;
        if(groupNums == null || groupNums.length == 0 || groupNums[0].length == 0) return;
        int num = matrix[i][j];
        int x = i - 1, y = j - 1;
        int maxX = matrix.length, maxY = matrix[0].length;
        if(x >= 0 && y >= 0 && x < maxX && y < maxY && groupNums[x][y] < 0 && matrix[x][y] == num){
            groupNums[x][y] = groupNum;
            recursive(matrix, x, y, groupNum, groupNums);
        }
        y = j;
        if(x >= 0 && y >= 0 && x < maxX && y < maxY && groupNums[x][y] < 0 && matrix[x][y] == num){
            groupNums[x][y] = groupNum;
            recursive(matrix, x, y, groupNum, groupNums);
        }
        y = j + 1;
        if(x >= 0 && y >= 0 && x < maxX && y < maxY && groupNums[x][y] < 0 && matrix[x][y] == num){
            groupNums[x][y] = groupNum;
            recursive(matrix, x, y, groupNum, groupNums);
        }
        x = i;y = j - 1;
        if(x >= 0 && y >= 0 && x < maxX && y < maxY && groupNums[x][y] < 0 && matrix[x][y] == num){
            groupNums[x][y] = groupNum;
            recursive(matrix, x, y, groupNum, groupNums);
        }
        y = j + 1;
        if(x >= 0 && y >= 0 && x < maxX && y < maxY && groupNums[x][y] < 0 && matrix[x][y] == num){
            groupNums[x][y] = groupNum;
            recursive(matrix, x, y, groupNum, groupNums);
        }
        x = i + 1;y = j - 1;
        if(x >= 0 && y >= 0 && x < maxX && y < maxY && groupNums[x][y] < 0 && matrix[x][y] == num){
            groupNums[x][y] = groupNum;
            recursive(matrix, x, y, groupNum, groupNums);
        }
        y = j;
        if(x >= 0 && y >= 0 && x < maxX && y < maxY && groupNums[x][y] < 0 && matrix[x][y] == num){
            groupNums[x][y] = groupNum;
            recursive(matrix, x, y, groupNum, groupNums);
        }
        y = j + 1;
        if(x >= 0 && y >= 0 && x < maxX && y < maxY && groupNums[x][y] < 0 && matrix[x][y] == num){
            groupNums[x][y] = groupNum;
            recursive(matrix, x, y, groupNum, groupNums);
        }
    }

    public void extract(BufferedImage srcImg) {
        // 均匀量化
        int[][] grayMatrix = ImageUtil.getGrayPixel(srcImg, WIDTH, HEIGHT);
        if(grayMatrix == null || grayMatrix.length == 0 || grayMatrix[0].length == 0) return;
        int width = grayMatrix[0].length;
        int height = grayMatrix.length;
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                grayMatrix[i][j] /= BIN_WIDTH;
            }
        }

        // 划分连通区域
        int[][] groupNums = new int[grayMatrix.length][grayMatrix[0].length];
        int groupNum = groupMatrix(grayMatrix, groupNums);

        // 判断聚合性
        // 统计每个分组下的像素数
        int[] groupCount = new int[groupNum];
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                groupCount[groupNums[i][j]]++;
            }
        }

        // 阈值
        int threshold = width * height / 100;
        for(int i = 0; i < groupNum; i++){
            if(groupCount[i] < threshold){
                // 0表示非聚合
                groupCount[i] = 0;
            }
            else{
                // 1表示聚合
                groupCount[i] = 1;
            }
        }

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                groupNums[i][j] = groupCount[groupNums[i][j]];
            }
        }

        // 计算图像特征
        int[][] feature = new int[256 / BIN_WIDTH][2];
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                if(groupNums[i][j] == 0){
                    feature[grayMatrix[i][j] / BIN_WIDTH][0]++;
                }
                else {
                    feature[grayMatrix[i][j] / BIN_WIDTH][1]++;
                }
            }
        }
        this.featureMatrix = feature;
    }

}