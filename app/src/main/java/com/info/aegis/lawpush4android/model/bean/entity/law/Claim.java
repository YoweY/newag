
package com.info.aegis.lawpush4android.model.bean.entity.law;

/**
 * 诉讼请求
 */
public class Claim {

    private String name;
    private int   reject;
    private int   support;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReject() {
        return reject;
    }

    public void setReject(int reject) {
        this.reject = reject;
    }

    public int getSupport() {
        return support;
    }

    public void setSupport(int support) {
        this.support = support;
    }

    @Override
    public String toString() {
        return "Claim{" +
                "name='" + name + '\'' +
                ", reject=" + reject +
                ", support=" + support +
                '}';
    }
}
