package main.pojo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by wiyee on 2018/5/18.
 * 兴趣点主题向量与标签向量
 */
public class Label {
    private String business_id;
    private ArrayList<String> categoryLabel;
    private Map<String,String> topicLabel;

    public Label(String business_id, ArrayList<String> categoryLabel, Map<String, String> topicLabel) {
        this.business_id = business_id;
        this.categoryLabel = categoryLabel;
        this.topicLabel = topicLabel;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public ArrayList<String> getCategoryLabel() {
        return categoryLabel;
    }

    public void setCategoryLabel(ArrayList<String> categoryLabel) {
        this.categoryLabel = categoryLabel;
    }

    public Map<String, String> getTopicLabel() {
        return topicLabel;
    }

    public void setTopicLabel(Map<String, String> topicLabel) {
        this.topicLabel = topicLabel;
    }
}
