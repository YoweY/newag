package com.info.aegis.lawpush4android.model.bean;

/**
 * 作者：jksfood on 2017/2/23 10:09
 */

public class MsgTagBean {

    private String enName;
    private String zhName;

    /**
     * 叶子标签为1
     * 父标签为0
     */
    private int type;

    public String getZhName() {
        return zhName;
    }

    public void setZhName(String zhName) {
        this.zhName = zhName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "{" +
                "enName='" + enName + '\'' +
                ", zhName='" + zhName + '\'' +
                ", type=" + type +
                '}';
    }
}
