package com.info.aegis.lawpush4android.model.bean.entity.law;

/**
 * Created by SFS on 2017/2/22.
 * Description :
 */

public class DialogDatas {
    public String content;
    public boolean select;
    public int position;

    public DialogDatas(String content, boolean select, int position) {
        this.content = content;
        this.select = select;
        this.position = position;
    }

    @Override
    public String toString() {
        return "DialogDatas{" +
                "content='" + content + '\'' +
                ", select=" + select +
                ", position=" + position +
                '}';
    }
}
