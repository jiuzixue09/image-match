package com.dave.image.feature.extractor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dave.image.feature.vo.Coordinate;
import com.dave.image.feature.utils.ImageUtil;

/**
 * 环形颜色分布直方图
 * 参考文献：
 *	http://wenku.baidu.com/link?url=bWfVkM-oyUn7cJuCnhICReeByt2XR-MUx07J-
 * 1pXvBz7UKzoe4iGmH4S-8j4MiuAXyzetBV7NEDwJC7BBjT8ecCpHvo7oSBNChO0gLJMhI7
 * @author Dave
 *
 * Created by dave on 2018/1/17
 */
public class AnnularColorLayoutHistogram extends ImageFeature{
	
    public AnnularColorLayoutHistogram() {
		super();
		FEATURE_NAME = "aclh";
	}

    @Override
    public void extract(BufferedImage srcImg) {
        // 获取灰度矩阵
        int[][] matrix = ImageUtil.getGrayPixel(srcImg, WIDTH, HEIGHT);

        // 根据灰度值对像素点进行分组
        if(matrix == null || matrix.length == 0 || matrix[0].length == 0) return;
        Map<Integer, List<Coordinate>> groupedPixels = new HashMap<Integer, List<Coordinate>>();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                Coordinate coordinate = new Coordinate();
                coordinate.x = i;
                coordinate.y = j;
                List<Coordinate> list = null;
                if (groupedPixels.containsKey(matrix[i][j])) {
                    list = groupedPixels.get(matrix[i][j]);
                    list.add(coordinate);
                } else {
                    list = new ArrayList<Coordinate>();
                    list.add(coordinate);
                    groupedPixels.put(matrix[i][j], list);
                }
            }
        }

        // 为不同的灰度值计算质心
        Coordinate[] centroid = new Coordinate[256];
        for (int i = 0; i <= 255; i++) {
            if (groupedPixels.containsKey(i)) {
                List<Coordinate> list = groupedPixels.get(i);
                double x = 0, y = 0;
                for (int j = 0; j < list.size(); j++) {
                    Coordinate coordinate = list.get(j);
                    x += coordinate.x;
                    y += coordinate.y;
                }
                x = x / list.size();
                y = y / list.size();
                Coordinate coordinate = new Coordinate();
                coordinate.x = x;
                coordinate.y = y;
                centroid[i] = coordinate;
            }
        }

        // 为每一个像素计算其到质心的距离
        double[][] distances = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                // 获取与(i,j)像素点拥有相同像素的质心
                Coordinate coordinate = centroid[matrix[i][j]];
                distances[i][j] = Math.sqrt(Math.pow(i - coordinate.x, 2) + Math.pow(j - coordinate.y, 2));
            }
        }

        // 比较出最大距离
        double[] maxDistances = new double[256];
        for (int i = 0; i <= 255; i++) {
            if (groupedPixels.containsKey(i)) {
                List<Coordinate> list = groupedPixels.get(i);
                double max = 0;
                for (int j = 0; j < list.size(); j++) {
                    Coordinate coordinate = list.get(j);
                    double distance = distances[(int) coordinate.x][(int) coordinate.y];
                    if (distance > max) {
                        max = distance;
                    }
                }
                maxDistances[i] = max;
            }
        }

        // 统计以不同距离为半径的同心圆内包含的像素数量
        int[][] nums = new int[256][N];
        for (int i = 0; i <= 255; i++) {
            for (int j = 1; j <= N; j++) {
                double minDis = maxDistances[i] * (j - 1) / N;
                double maxDis = maxDistances[i] * j / N;
                // 第一个同心圆的取值范围必须为[0, maxDis * j / n]
                // 必须包含0，因为有可能存在像素点和质心重叠的情况
                if (j == 1) {
                    minDis = -1;
                }
                if(groupedPixels.containsKey(i)){
                    List<Coordinate> list = groupedPixels.get(i);
                    int num = 0;
                    for (int k = 0; k < list.size(); k++) {
                        Coordinate coordinate = list.get(k);
                        double dis = distances[(int) coordinate.x][(int) coordinate.y];
                        if (dis > minDis && dis <= maxDis) {
                            num++;
                        }
                    }
                    nums[i][j - 1] = num;
                }
            }
        }

        this.featureMatrix = nums;
    }
    
 
}
