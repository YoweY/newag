package com.info.aegis.lawpush4android.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by 申富生 on 2016/12/29.
 * Description :
 */

public class FontsUtils {

    //从 Assets中获取 字体
    public static Typeface getTypeface(Context context,String fonts){
    return Typeface.createFromAsset(context.getAssets(), fonts);
    }

}
