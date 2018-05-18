package main.pojo;

/**
 * Created by wiyee on 2018/4/23.
 * category
 */
public class Category {
    private int category_id;
    private String business_id;
    private String category;

    public Category(int category_id, String business_id, String category) {
        this.category_id = category_id;
        this.business_id = business_id;
        this.category = category;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
