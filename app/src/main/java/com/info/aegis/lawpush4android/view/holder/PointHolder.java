package com.info.aegis.lawpush4android.view.holder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.model.bean.entity.law.JudicialPoint;
import com.info.aegis.lawpush4android.presenter.adapter.SubBaseAdapter;
import com.info.aegis.lawpush4android.view.iml.LawDetailActivity;

import java.util.List;

/**
 * Created by 申富生 on 2016/12/27.
 * Description :   司法观点的holder
 */

public class PointHolder {


    private Context context;
    private View convertView;
    private List<JudicialPoint> points;
    private MyPointAdapter mAdapter;


    public PointHolder(Context context, View convertView, List<JudicialPoint> points) {

        this.context = context;
        this.convertView = convertView;
        this.points = points;

        ListView mListView = (ListView) convertView.findViewById(R.id.listView_point);

        TextView   mPointText = (TextView) convertView.findViewById(R.id.item_point_text);

        if (null==points||0==points.size()){
            mPointText.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }else{
            mPointText.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mAdapter = new MyPointAdapter(points,context);
            mListView.setAdapter(mAdapter);
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
            listItem.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // TODO: 2016/12/21   数据源是paeams的 1.5 倍  就是 数据源为3 实际上现实的是2个
        //   params.height = (int) (((totalHeight + (listView.getDividerHeight() * (listAdapter.getCount()-1 )))) * 1.5);

        //这是正常的适配
        //     params.height = ((totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))));

        params.height = (int) ((totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))) * 1.1);
        listView.setLayoutParams(params);

    }


    class MyPointAdapter extends SubBaseAdapter<JudicialPoint> {


        public MyPointAdapter(List<JudicialPoint> judicialPoints, Context ctx) {
            super(judicialPoints, ctx);
        }

        @Override
        protected View getContentView(int position, View convertView, ViewGroup viewGroup) {
            BaseHolder holder = null;
            if (convertView == null) {
                holder = new PointSubHolder(position);

            } else {
                holder = (BaseHolder) convertView.getTag();
            }
            holder.setData(points.get(position));

            return holder.getContentView();
        }


    }
    public class PointSubHolder extends BaseHolder<JudicialPoint> {
        public View convertView;
        private TextView mItemPointName;
        private TextView mItemPointContent;
        private TextView mItemPointOther;
        private LinearLayout mItemPointLl;
        private int position;

        public PointSubHolder(int position) {
            this.position = position;
        }

        @Override
        protected View initView() {
            convertView = View.inflate(context, R.layout.item_point_sub, null);
            mItemPointName = (TextView) convertView.findViewById(R.id.item_point_name);
            mItemPointLl = (LinearLayout) convertView.findViewById(R.id.item_point_ll);

            return convertView;
        }

        @Override
        protected void refreshView(final JudicialPoint point) {
            mItemPointName.setText(point.getTitle());


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, LawDetailActivity.class);
                    intent.putExtra("name", point.getTitle());
                    intent.putExtra("id", point.getId());
                    intent.putExtra("type", 3);
                    context.startActivity(intent);
                }
            });
        }
    }


}
