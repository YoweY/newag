package com.info.aegis.lawpush4android.model.bean.entity.law;


/*
 * Created by wuhao on 2016/9/27.
 */
public class CaseCause {

    private String  name;

    private Integer grade;//案由级别
    private Integer id;//案由id
    private Integer number;
    private Integer parentId;//父案由id




    private String CId;
    private String description;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return "CaseCause{" +
                "id=" + CId +
                ", grade=" + grade +
                ", name='" + name + '\'' +
                '}';
    }
    public String getCId() {
        return CId;
    }

    public void setCId(String CId) {
        this.CId = CId;
    }
}
