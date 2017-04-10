package com.info.aegis.lawpush4android.model.bean.entity.law;

/**
 * Created by 申富生 on 2016/12/22.
 * Description :证据
 */
public class Evidence {


    private String tag;
    private int count;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Evidence{" +
                "tag='" + tag + '\'' +
                ", count=" + count +
                '}';
    }
}
