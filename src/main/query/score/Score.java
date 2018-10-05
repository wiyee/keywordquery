package main.query.score;

import javafx.beans.binding.DoubleExpression;
import main.pojo.POI;
import main.pojo.Position;
import main.pojo.Query;
import main.pojo.User;
import main.query.irtree.Tree;
import main.tools.StaticMethod;
import main.tools.StaticValue;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.*;

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
        return (0.5 * dist1 + 0.5 * dist2)/1000d;
    }

//==========================================preference cost============================================================

    /**
     * 偏好损失函数
     * @param candidateList
     * @param usrInt
     * @return
     */
    private static double Cost2(List<POI> candidateList, Map<String,Double> usrInt, int t){
        double result = 0d;
        for (POI poi: candidateList){
//            System.out.println("access");
//            System.out.println(getAccess(poi,t));
//            System.out.println(getPreference(poi,usrInt));
            result = (3.0) / (getAccess(poi,t) * StaticValue.alpha + getPreference(poi,usrInt) * (1 - StaticValue.alpha));
        }
        return result;
    }

    private static double getPopular(POI poi){
        return poi.getPopularity();
    }
    private static double getCrowd(POI poi, int t){
        return poi.getCongestion()[t];
    }
    private static double getAccess(POI poi, int t){
        if (poi.getAccess() != null)
            if (poi.getAccess()[t-1] != 0.5d){
                return poi.getAccess()[t-1];
            }
        return 0.5d;
    }

    /**
     *
     * @param poi 兴趣点对象
     * @param userPre 用户潜在兴趣点
     * @return
     */
    private static double getPreference(POI poi, Map<String,Double> userPre){
        double result = 0d;
        String[] cateLabel = poi.getCategoryLabel();
        String[] topicLabel = poi.getTopicLabel();
//        JSONObject object = new JSONObject(userPre);
        if (cateLabel != null){
            for (String s:cateLabel){
                if (userPre.get(s)!=null){
                    result += Double.parseDouble(String.valueOf(userPre.get(s)));
                }
            }
        }

        if (topicLabel != null){
            for (String s:topicLabel){
                if (userPre.get(s)!=null){
                    result += Double.parseDouble(String.valueOf(userPre.get(s)));
                }
            }
        }
        return result * 100;
    }

    //==========================================cost============================================================
    public static double getScore(List<POI> list,Query query){
//        Map<String,Double> intMap = StaticMethod.json2Map(query.getUser().getInterestTopic());
//        System.out.println("cost1: " + Cost1(list,query.getPosition()) * StaticValue.beta);
//        System.out.println("cost2: " + Cost2(list,query.getUser().getTopicMap(),query.getHour()) / list.size() * (1 - StaticValue.beta));
        return Cost1(list,query.getPosition()) * StaticValue.beta + Cost2(list,query.getUser().getTopicMap(),query.getHour()) / list.size() * (1 - StaticValue.beta);
    }

    public static double getCSKQCost(List<POI> list, Query query){
        return Cost1(list,query.getPosition());
    }

    public static void main(String[] args) {

//        double distance = getDistBetweenCoordinate(120.6847393,27.8426045297,120.686075789,27.8426207568);
//        double distance2 = getDistBetweenCoordinate(120.686075789,27.8426207568,120.6874145,27.8426598299);
//        String result = String.format("%.3f", (distance + distance2)/1000);
//        System.out.println("Distance is:" + result);
        Tree tree = new Tree();
        String userId = "-0AyZxS5C--WySnbW_Q8yQ";
        Set<String> keySet = new HashSet<String>();
        keySet.add("Food".toLowerCase());
        keySet.add("eyewear".toLowerCase());
        keySet.add("shopping".toLowerCase());
        int hour = 12;
        Map<String,Double> intMap = StaticMethod.json2Map(StaticValue.userInt);
        Position position = new Position(-79.437,43.6596);
        User user = new User(userId,"Tim",25,3.56,7,intMap);


        Query queryTest = new Query(keySet,user,hour,position);
        String[] poi = new String[]{"KjjQGzu0241FbeERM32dpQ", "SIJQw21uevKbwabMkX7mYQ", "lI0kHT7wPJJuT8yVFIrRdg"};
        List<POI> list = new LinkedList<POI>();
        for (String str: poi){
            list.add(tree.poiMap.get(str));
        }

        double cost = Cost1(list,queryTest.getPosition()) * StaticValue.beta + Cost2(list,intMap,hour)*10/list.size()*(1 - StaticValue.beta);
        System.out.println(cost);
        System.out.println(Cost1(list,queryTest.getPosition()) * StaticValue.beta);
        System.out.println(Cost2(list,intMap,hour)*10/list.size()*(1 - StaticValue.beta));
    }

}
