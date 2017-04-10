package com.info.aegis.lawpush4android.view.holder;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.model.bean.entity.law.CaseCause;
import com.info.aegis.lawpush4android.model.bean.entity.law.GuidanceCase;
import com.info.aegis.lawpush4android.view.iml.LawDetailActivity;

import java.util.List;


/**
 * Created by 申富生 on 2016/12/19.
 * Description : 相关案例的holder
 */

public class CaseHolder {

    private Context context;
    private View convertView;
    private List<GuidanceCase> guidances;
    private List<CaseCause> cases;
    private TextView mPointName;
    private TextView mPointContent;
    private TextView mFailName;
    private TextView mFailContent;
    private TextView mSuccessName;
    private TextView mSuccessContent;
    private GuidanceCase guidanceCase;
    private LinearLayout mFailLinear;
    private LinearLayout mSuccessLinear;
    private LinearLayout mPointLinear;
    private CaseCause cause;

    public CaseHolder(Context context, View convertView, List<GuidanceCase> guidances, List<CaseCause> cases) {
        this.context = context;
        this.convertView = convertView;
        this.guidances = guidances;
        this.cases = cases;
//        ListView mListView = (ListView) convertView.findViewById(R.id.listView_case);
//        MyCaseAdapter adapter = new MyCaseAdapter(guidances, context);
//        mListView.setAdapter(adapter);
        initView();

    }

    /**
     * 初始化View
     */
    private void initView() {
        mPointName = (TextView) convertView.findViewById(R.id.item_case_point_name);
        mPointContent = (TextView) convertView.findViewById(R.id.item_case_point_content);
        mFailName = (TextView) convertView.findViewById(R.id.item_case_fill_name);
        mFailContent = (TextView) convertView.findViewById(R.id.item_case_fill_content);
        mSuccessName = (TextView) convertView.findViewById(R.id.item_case_success_name);
        mSuccessContent = (TextView) convertView.findViewById(R.id.item_case_success_content);
        mPointLinear = (LinearLayout) convertView.findViewById(R.id.item_case_point_linear);
        mFailLinear = (LinearLayout) convertView.findViewById(R.id.item_case_fail_linear);
        mSuccessLinear = (LinearLayout) convertView.findViewById(R.id.item_case_success_linear);
        TextView   mPointText = (TextView) convertView.findViewById(R.id.item_point_text);
        if (null==guidances||null==cases||0==guidances.size()||0==cases.size()){
            mPointText.setVisibility(View.VISIBLE);
            mPointLinear.setVisibility(View.GONE);
            mSuccessLinear.setVisibility(View.GONE);
        }else{
            mPointText.setVisibility(View.GONE);
            mPointLinear.setVisibility(View.VISIBLE);
            mSuccessLinear.setVisibility(View.VISIBLE);
            initData();
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {

        if (guidances != null && guidances.size() > 0) {
            guidanceCase = guidances.get(0);
            mPointName.setText(guidances.get(0).getTitle());
            mPointContent.setText(Html.fromHtml("\u3000\u3000" + guidanceCase.getGist() + "<font color='#0078DA'>[查看详情]</font> "));
            mPointContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, LawDetailActivity.class);
                    intent.putExtra("name", guidanceCase.getTitle());
                    intent.putExtra("id", guidanceCase.getId());
                    intent.putExtra("type", 4);
                    context.startActivity(intent);
                }
            });
        } else {
            mPointLinear.setVisibility(View.GONE);

        }


        if (cases!=null&&cases.size()>0){
            cause = cases.get(0);
            mSuccessName.setText(cause.getName());
            mSuccessContent.setText(Html.fromHtml("\u3000\u3000" + cause.getDescription() + "<font color='#0078DA'>[查看详情]</font> "));
            mSuccessContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, LawDetailActivity.class);
                    intent.putExtra("name", cause.getName());
                    intent.putExtra("id", cause.getCId());
                    intent.putExtra("type", 1);
                    context.startActivity(intent);
                }
            });
        }else{
            mSuccessLinear.setVisibility(View.GONE);
        }
    }

}



