package com.info.aegis.lawpush4android.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.info.aegis.lawpush4android.R;


/**
 * Created by 申富生 on 2016/12/21.
 * Description :  菜单 的 holder对象
 */

public class MenuHolder {
    public   LinearLayout mMenuLl;
    private Context context;
    public View convertView;
    public TextView mItemName;
    public ImageView mImageView;


    public MenuHolder(Context context, View view) {
        this.context = context;
        this.convertView = view;
        this.mItemName = (TextView) convertView.findViewById(R.id.item_name);
        this. mMenuLl = (LinearLayout) convertView.findViewById(R.id.menu_ll);
        this.mImageView= (ImageView) convertView.findViewById(R.id.imageView);
  //      mItemName.setTypeface(FontsUtils.getTypeface(context,"fonts/STFANGSO.TTF"));
    }
}
