package com.info.aegis.lawpush4android.model.bean.entity.law;


/**
 * Created by 吴昊 on 2016/10/26.
 */
public class LawTag {


    private String id;
    private String zhName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZhName() {
        return zhName;
    }

    public void setZhName(String zhName) {
        this.zhName = zhName;
    }

    @Override
    public String toString() {
        return "LawTag{" +
                "id='" + id + '\'' +
                ", zhName='" + zhName + '\'' +
                '}';
    }
}
