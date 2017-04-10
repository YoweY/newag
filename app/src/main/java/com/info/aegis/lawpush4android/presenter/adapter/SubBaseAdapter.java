package com.info.aegis.lawpush4android.presenter.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 子 ListView的Adapter
 * @param <Data>
 */
public abstract class SubBaseAdapter<Data> extends BaseAdapter {
        private List<Data> Datas;
    private Context mContent;
    public SubBaseAdapter(List<Data> datas, Context ctx) {
        this.Datas=datas;
        this.mContent=ctx;
    }

    @Override
    public int getCount() {
        return Datas==null?0:Datas.size();
    }

    @Override
    public Object getItem(int i) {
        return Datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        return  getContentView(i,convertView,viewGroup);
    }
    /**
     * 获取内容View
     * @return
     */
    protected abstract View getContentView(int position, View convertView, ViewGroup viewGroup);


}
