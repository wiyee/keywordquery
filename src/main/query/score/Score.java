package main.query.score;

import main.pojo.POI;
import main.pojo.Position;
import main.pojo.User;
import main.tools.StaticValue;

import java.util.List;

/**
 * Created by wiyee on 2018/4/25.
 * 评分函数
 */
public class Score {
    private static double EARTH_RADIUS = 6371.393;

//==========================================dist cost============================================================
    /**
     * 计算候选poi集合与用户之间的最远距离  外距离
     *
     * @param candidateList [poi1, poi2, ... , poin]
     * @param position
     * @return
     */
    public static double getMaxDistBetweenUserAndPoi(List<POI> candidateList, Position position) {
        double maxDist = 0d;
        for (POI poi: candidateList){
            double dist = getDistBetweenCoordinate(poi.getLon(),poi.getLat(),position.getLon(),position.getLat());
            if (dist > maxDist){
                maxDist = dist;
            }
        }
        return maxDist;
    }

    /**
     * 计算候选poi之间的最远距离  内距离
     * @param candidateList
     * @return
     */
    private static double getMaxDistBetweenPOI(List<POI> candidateList){
        double maxDist = 0d;
        for (int i = 0; i < candidateList.size() - 1; i++){
            for (int j = i + 1; j < candidateList.size(); j++){
                double dist = getDistBetweenCoordinate (candidateList.get(i).getLat(),candidateList.get(i).getLon(),
                        candidateList.get(j).getLat(), candidateList.get(j).getLon());
                if (dist > maxDist){
                    maxDist = dist;
                }
            }
        }
        return maxDist;
    }

    /**
     * 计算两个坐标点之间的距离
     * @param lon1
     * @param lat1
     * @param lon2
     * @param lat2
     * @return
     */
    private static double getDistBetweenCoordinate(double lon1, double lat1, double lon2, double lat2) {

        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        return s * 1000;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 距离损失函数
     * @param candidateList
     * @param position
     * @return 结果尚未进行标准化
     */
    private static double Cost1(List<POI> candidateList, Position position){
        double dist1 = getMaxDistBetweenUserAndPoi(candidateList,position);
        double dist2 = getMaxDistBetweenPOI(candidateList);
        return StaticValue.beta * dist1 + (1 - StaticValue.beta) * dist2;
    }

//==========================================preference cost============================================================

    /**
     * 偏好损失函数
     * @param candidateList
     * @param user
     * @return
     */
    private static double Cost2(List<POI> candidateList, User user){
        return 0d;
    }

    private static double getPopular(POI poi){
        return 0d;
    }

    private static double getCrowd(POI poi){
        return 0d;
    }

    private static double getPreference(POI poi, User user){
        return 0d;
    }


    public static void main(String[] args) {

        double distance = getDistBetweenCoordinate(-79.5141,43.6599,-79.3981,43.7076);
        System.out.println("Distance is:" + distance);
    }
}
