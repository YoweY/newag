
package com.info.aegis.lawpush4android.model.bean.entity.law;


public class RejectReason {

    private Long   count;
    private String name;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RejectReason{" +
                "count=" + count +
                ", name='" + name + '\'' +
                '}';
    }
}
