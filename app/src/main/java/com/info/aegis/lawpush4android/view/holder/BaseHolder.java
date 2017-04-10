package com.info.aegis.lawpush4android.view.holder;

import android.view.View;

/**
 * 抽取的 BaseHolder
 * @param <Data>
 */
public abstract class BaseHolder<Data> {

    public View contentView;

    private Data data;

    public BaseHolder() {
        contentView = initView();
        contentView.setTag(this);
    }

    public void setData(Data data) {
        this.data = data;
        refreshView(data);
    }

    public View getContentView() {
        return contentView;
    }

    /**
     * 创建view 对象& view 的初始化 (findViewById)
     *
     * @return
     */
    protected abstract View initView();

    /**
     * 将数据显示到对应的控件上
     *
     * @param data
     */
    protected abstract void refreshView(Data data);

}

