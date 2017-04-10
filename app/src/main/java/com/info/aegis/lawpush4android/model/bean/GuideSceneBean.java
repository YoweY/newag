package com.info.aegis.lawpush4android.model.bean;

import java.util.List;

/**
 * 作者：jksfood on 2017/2/27 21:34
 */

public class GuideSceneBean {
    private String name;
    private List<ScenesBean> scenesBeanList ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ScenesBean> getScenesBeanList() {
        return scenesBeanList;
    }

    public void setScenesBeanList(List<ScenesBean> scenesBeanList) {
        this.scenesBeanList = scenesBeanList;
    }

    @Override
    public String toString() {
        return "GuideSceneBean{" +
                "name='" + name + '\'' +
                ", scenesBeanList=" + scenesBeanList +
                '}';
    }
}
