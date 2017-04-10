package com.info.aegis.lawpush4android.model.bean.entity.law;

/**
 * 作者：jksfood on 2017/2/23 15:46
 */

public class MsgCauseBean {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
