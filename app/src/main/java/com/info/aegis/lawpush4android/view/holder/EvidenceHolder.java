package com.info.aegis.lawpush4android.view.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.model.bean.entity.law.Evidence;
import com.info.aegis.lawpush4android.presenter.adapter.SubBaseAdapter;

import java.util.List;

/**
 * Created by 申富生 on 2016/12/19.
 * Description : 证据分析的holder
 */

public class EvidenceHolder {
    public View convertView;
    private Context context;
    private List<Evidence> evidences;


    public EvidenceHolder(Context context, View view, List<Evidence> evidences) {
        this.convertView = view;
        this.context = context;
        this.evidences = evidences;
        ListView mListView = (ListView) convertView.findViewById(R.id.listView_evidence);
        TextView mTextNormal = (TextView) convertView.findViewById(R.id.item_evidence_text);
        LinearLayout mTop = (LinearLayout) convertView.findViewById(R.id.item_evidence_linear_top);
        LinearLayout mBot = (LinearLayout) convertView.findViewById(R.id.item_evidence_linear_bot);
        // TODO: 2017/3/10 cuowu 空指针
        if (null==this.evidences|| 0==evidences.size()) {
            mTop.setVisibility(View.GONE);
            mBot.setVisibility(View.GONE);
            mTextNormal.setVisibility(View.VISIBLE);
        } else {
            mTop.setVisibility(View.VISIBLE);
            mBot.setVisibility(View.VISIBLE);
            mTextNormal.setVisibility(View.GONE);
            MyEvidenceAdapter adapter = new MyEvidenceAdapter(evidences, context);
            mListView.setAdapter(adapter);
            fixListViewHeight(mListView);
        }
    }


    /**
     * 测量 listView 子控件的高度
     *
     * @param listView
     */
    public void fixListViewHeight(ListView listView) {

        // 如果没有设置数据适配器，则ListView没有子项，返回。
        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {

            return;

        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {

            View listItem = listAdapter.getView(i, null, listView);

            listItem.measure(0, 0);

            totalHeight += listItem.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // TODO: 2016/12/21   数据源是paeams的 1.5 倍  就是 数据源为3 实际上现实的是2个
        params.height = (int) ((totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))));
        listView.setLayoutParams(params);

    }


    class MyEvidenceAdapter extends SubBaseAdapter<Evidence> {


        public MyEvidenceAdapter(List<Evidence> evidences, Context ctx) {
            super(evidences, ctx);
        }

        @Override
        public int getCount() {
            if (null == evidences) {
                return 0;
            } else {
                return (evidences.size() / 2) + (evidences.size() % 2);
            }
        }


        @Override
        protected View getContentView(int position, View convertView, ViewGroup viewGroup) {

            BaseHolder holder = null;
            if (convertView == null) {
                holder = new EvidenceSubHolder(position);
            } else {
                holder = (BaseHolder) convertView.getTag();
            }
            holder.setData(evidences.get(position));

            return holder.getContentView();
        }


        public class EvidenceSubHolder extends BaseHolder<Evidence> {
            private View convertView;
            private TextView mItemEvidenceText1;
            private TextView mItemEvidenceNum1;
            private LinearLayout mItemEvidenceLl1;
            private TextView mItemEvidenceText2;
            private TextView mItemEvidenceNum2;
            private LinearLayout mItemEvidenceLl2;
            private int position;

            public EvidenceSubHolder(int position) {

                this.position = position;
            }

            @Override
            protected View initView() {
                convertView = View.inflate(context, R.layout.item_evidence_sub, null);
                mItemEvidenceText1 = (TextView) convertView.findViewById(R.id.item_evidence_text1);
                mItemEvidenceNum1 = (TextView) convertView.findViewById(R.id.item_evidence_num1);
                mItemEvidenceLl1 = (LinearLayout) convertView.findViewById(R.id.item_evidence_ll1);
                mItemEvidenceText2 = (TextView) convertView.findViewById(R.id.item_evidence_text2);
                mItemEvidenceNum2 = (TextView) convertView.findViewById(R.id.item_evidence_num2);
                mItemEvidenceLl2 = (LinearLayout) convertView.findViewById(R.id.item_evidence_ll2);
                return convertView;
            }

            @Override
            protected void refreshView(Evidence evidence) {
                Evidence evidence1 = evidences.get(position * 2);
                Evidence evidence2;
                if (position * 2 + 1 < evidences.size()) {
                    evidence2 = evidences.get(position * 2 + 1);
                } else {
                    evidence2 = null;
                }


                // item 1 的属性设置
                mItemEvidenceText1.setText(evidence1.getTag());
                mItemEvidenceNum1.setText(evidence1.getCount() + "%");

                //  item 2 的属性设置
                if (evidence2 == null) {
                    if (mItemEvidenceLl2.getVisibility() == View.VISIBLE) {
                        mItemEvidenceLl2.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (mItemEvidenceLl2.getVisibility() == View.INVISIBLE) {
                        mItemEvidenceLl2.setVisibility(View.VISIBLE);
                    }
                    mItemEvidenceText2.setText(evidence2.getTag());
                    mItemEvidenceNum2.setText(evidence2.getCount() + "%");
                }

            }
        }


    }
}
