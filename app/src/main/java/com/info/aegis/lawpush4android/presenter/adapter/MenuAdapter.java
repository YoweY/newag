package com.info.aegis.lawpush4android.presenter.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.view.holder.MenuHolder;
/**
 * 右侧菜单ListView的适配器
 *
 * @author Administrator
 */
public class MenuAdapter extends BaseAdapter {

    private Context context;
    private int selectItem = 0;
    private String [] data ;

    private AbsListView.LayoutParams layoutParams;
    private int width;
    private int height;
    private int [] imgsGray={R.mipmap.icon1_grey,R.mipmap.icon2_grey,R.mipmap.icon3_grey,
    R.mipmap.icon4_grey,R.mipmap.icon5_grey,R.mipmap.icon6_grey,R.mipmap.icon7_grey,R.mipmap.icon8_grey};
    private int [] imgsBlue={R.mipmap.icon1_blue,R.mipmap.icon2_blue,R.mipmap.icon3_blue,
    R.mipmap.icon4_blue,R.mipmap.icon5_blue,R.mipmap.icon6_blue,R.mipmap.icon7_blue,R.mipmap.icon8_blue};

    public MenuAdapter(Context context, String [] data) {
        this.context = context;
        this.data=data;

    }

    public int getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    public void setHeight(int height){
        this.height = height;
    };
    public void setWidth(int width){
        this.width = width;
    };



    @Override
    public int getCount() {

        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MenuHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_menu_sub, null);
           layoutParams= new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height/(data.length+1));
            convertView.setLayoutParams(layoutParams);
            holder=new MenuHolder(context,convertView);
            convertView.setTag(holder);
        } else {
            holder = (MenuHolder) convertView.getTag();
        }
        holder.mImageView.setImageResource(imgsGray[position]);
        // 当前条目为第一条
        if (position == selectItem) {
            holder.mImageView.setImageResource(imgsBlue[position]);
         holder.mMenuLl.setBackgroundResource(R.mipmap.qianjing);
            holder.mItemName.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.mMenuLl.setBackground(context.getResources().getDrawable(R.mipmap.beijing_huise));
            holder.mItemName.setTextColor(context.getResources().getColor(R.color.text_deep));

        }
        holder.mItemName.setText(data[position]);
        return convertView;
    }




}
