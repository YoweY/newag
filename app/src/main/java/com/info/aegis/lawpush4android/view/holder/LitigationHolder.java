package com.info.aegis.lawpush4android.view.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.model.bean.entity.law.Claim;
import java.util.List;


/**
 * Created by 申富生 on 2016/12/19.
 * Description : 诉讼请求的holder
 */

public class LitigationHolder {

    private Context context;
    public View convertView;
    private List<Claim> claims;


    public LitigationHolder(Context context, View view, List<Claim> claims) {
        this.context = context;
        this.convertView = view;
        this.claims = claims;
        ListView mListView = (ListView) convertView.findViewById(R.id.listView_litigation);

        FrameLayout mFramLayout = (FrameLayout) convertView.findViewById(R.id.item_litigation_framelayout);

        View BarView = View.inflate(context, R.layout.fail_cause, null);
        mFramLayout.addView(BarView);

        MyLitigationAdapter adapter = new MyLitigationAdapter(context, claims);
        mListView.setAdapter(adapter);
        fixListViewHeight(mListView);

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
            listItem.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
// TODO: 2016/12/21   数据源是paeams的 1.5 倍  就是 数据源为3 实际上现实的是2个
        //   params.height = (int) (((totalHeight + (listView.getDividerHeight() * (listAdapter.getCount()-1 )))) * 1.5);

        //这是正常的适配
        params.height = ((totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))));

        //   params.height = (int) ((totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)))*1.5);
        listView.setLayoutParams(params);

    }

    class MyLitigationAdapter extends BaseAdapter {

        private Context context;
        private List<Claim> datas;

        public MyLitigationAdapter(Context context, List<Claim> claims) {

            this.context = context;
            this.datas = claims;
        }

        @Override
        public int getCount() {
            return null == datas ? 0 : datas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_litigation_sub, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position == 0) {
                holder.mItemLitigationCurrent.setVisibility(View.VISIBLE);
                holder.mItemLitigationOther.setVisibility(View.VISIBLE);
                holder.mViewLine.setVisibility(View.VISIBLE);
            } else {
                holder.mItemLitigationCurrent.setVisibility(View.GONE);
                holder.mItemLitigationOther.setVisibility(View.GONE);
                holder.mViewLine.setVisibility(View.GONE);
            }
            if (datas.size() == 1) {
                holder.mItemLitigationOther.setVisibility(View.GONE);
            }

            Claim claim = datas.get(position);
            holder.mItemLitigationTitle.setText(claim.getName());
            holder.mItemLitigationTextReject.setText("驳回" + claim.getReject() + "%");
            holder.mItemLitigationTextSupport.setText("支持" + claim.getSupport() + "%");
            holder.mProgressBar.setProgress(claim.getSupport());

            holder.mSeekBar.setEnabled(false);
            if (claim.getSupport()<25){
                holder.mSeekBar.setProgress(claim.getSupport()-2);
            }
            else if (claim.getSupport()<50) {
                holder.mSeekBar.setProgress(claim.getSupport());
            }else if (claim.getSupport()<75){
                holder.mSeekBar.setProgress(claim.getSupport()+1);
            }else{
                holder.mSeekBar.setProgress(claim.getSupport()+2);
            }
/////////////////////////////////////////////////////


            return convertView;
        }


        public class ViewHolder {
            public View rootView;
            public View mViewLine;
            public TextView mItemLitigationTitle;
            public TextView mItemLitigationTextSupport;
            public ProgressBar mProgressBar;
            public TextView mItemLitigationTextReject;
            public LinearLayout mItemLitigationOther;
            public LinearLayout mItemLitigationCurrent;
            public SeekBar mSeekBar;

            public ViewHolder(View rootView) {
                this.rootView = rootView;
                this.mItemLitigationCurrent = (LinearLayout) rootView.findViewById(R.id.item_litigation_current);
                this.mItemLitigationOther = (LinearLayout) rootView.findViewById(R.id.item_litigation_other);
                this.mItemLitigationTitle = (TextView) rootView.findViewById(R.id.item_litigation_title);
                this.mItemLitigationTextSupport = (TextView) rootView.findViewById(R.id.item_litigation_text_support);
                this.mProgressBar = (ProgressBar) rootView.findViewById(R.id.item_litigation_progressBar);
                this.mItemLitigationTextReject = (TextView) rootView.findViewById(R.id.item_litigation_text_reject);
                this.mSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
                this.mViewLine = rootView.findViewById(R.id.view_line);

            }
        }

    }
}