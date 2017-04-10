package com.info.aegis.lawpush4android.view.holder;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.model.bean.entity.law.Law;
import com.info.aegis.lawpush4android.presenter.adapter.SubBaseAdapter;
import com.info.aegis.lawpush4android.view.iml.DataDetailActivity;

import java.util.List;

/**
 * Created by 申富生 on 2016/12/19.
 * Description :法律知识holder
 */
public class LawInfoHolder {
    private final TextView mLawText;
    private List<Law> laws;
    private Context context;
    public View convertView;
    private MyLawInfoAdapter adapter;
    private ListView mListView;
    private PopupWindow popuWindow1;
    private View windowView;
    private TextView mWindowName;
    private TextView mWindowTag;

    public LawInfoHolder(Context ctx, View view, List<Law> laws) {
        this.context = ctx;
        this.convertView = view;
        this.laws = laws;
        mListView = (ListView) convertView.findViewById(R.id.listView_low);

        mLawText = (TextView) view.findViewById(R.id.item_law_text);

        if (null == laws ||0== laws.size()) {
            mListView.setVisibility(View.GONE);
            mLawText.setVisibility(View.VISIBLE);
        } else {
            mListView.setVisibility(View.VISIBLE);
            mLawText.setVisibility(View.GONE);
            adapter = new MyLawInfoAdapter(laws, ctx);
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
            listItem.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // TODO: 2016/12/21   数据源是paeams的 1.5 倍  就是 数据源为3 实际上现实的是2个
        //    params.height = (int) (((totalHeight + (listView.getDividerHeight() * (listAdapter.getCount()-1 )))) * 1.5);

        //这是正常的适配
        params.height = ((totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))));

        //   params.height = (int) ((totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)))*1.5);
        listView.setLayoutParams(params);

    }


    class MyLawInfoAdapter extends SubBaseAdapter<Law> {

        public MyLawInfoAdapter(List<Law> laws, Context ctx) {
            super(laws, ctx);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        protected View getContentView(int position, View convertView, ViewGroup viewGroup) {
            BaseHolder holder = null;
            if (convertView == null) {
                holder = new LawInfoSubHolder(position);
            } else {
                holder = (BaseHolder) convertView.getTag();
            }
            holder.setData(laws.get(position));
            return holder.getContentView();
        }
    }

    public class LawInfoSubHolder extends BaseHolder<Law> {

        private View convertView;
        private ImageView mItemImg;
        private TextView mItemLowInfoName;
        private ImageView mBestImg;
        private TextView mItemLowInfoContent;
        private TextView mItemLowInfoTag;
        private int position;
        private TextView mWindowContent;

        public LawInfoSubHolder(int position) {
            this.position = position;
        }

        @Override
        protected View initView() {
            convertView = View.inflate(context, R.layout.item_law_sub, null);
            mItemLowInfoName = (TextView) convertView.findViewById(R.id.item_lowInfo_name);
            mItemLowInfoContent = (TextView) convertView.findViewById(R.id.item_lowInfo_content);
            mItemLowInfoTag = (TextView) convertView.findViewById(R.id.item_lowInfo_tag);

            return convertView;
        }

        @Override
        protected void refreshView(final Law law) {
            mItemLowInfoName.setText(law.getName());
            mItemLowInfoContent.setText("\u3000\u3000" + law.getContent());
            mItemLowInfoTag.setText(law.getTag());
            if (TextUtils.isEmpty(law.getTag())) {
                mItemLowInfoTag.setVisibility(View.GONE);
            } else {
                mItemLowInfoTag.setVisibility(View.VISIBLE);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    initPopupWindow1(convertView, position, law);
                }
            });

        }


        private void initPopupWindow1(View parent, int position, Law law) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(context);
            windowView = mLayoutInflater.inflate(R.layout.pop_window, null);
            mWindowName = (TextView) windowView.findViewById(R.id.item_window_name);
            mWindowTag = (TextView) windowView.findViewById(R.id.item_window_tag);
            mWindowContent = (TextView) windowView.findViewById(R.id.item_window_content);
            popuWindow1 = new PopupWindow(windowView, 1500,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            //设置数据
            mWindowName.setText("\u3000\u3000" + law.getName());
            mWindowContent.setText("\u3000\u3000" + law.getContent());
            mWindowTag.setText(law.getTag());
            if (TextUtils.isEmpty(law.getTag())) {
                mWindowTag.setVisibility(View.GONE);
            } else {
                mWindowTag.setVisibility(View.VISIBLE);
            }
            ColorDrawable cd = new ColorDrawable(0x000000);
            popuWindow1.setBackgroundDrawable(cd);
            //产生背景变暗效果
            WindowManager.LayoutParams lp = ((DataDetailActivity) context).getWindow().getAttributes();
            lp.alpha = 0.6f;
            ((DataDetailActivity) context).getWindow().setAttributes(lp);
            ((DataDetailActivity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            popuWindow1.setOutsideTouchable(true);
            popuWindow1.setFocusable(true);
            popuWindow1.showAtLocation((View) parent.getParent(), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);

            popuWindow1.update();
            popuWindow1.setOnDismissListener(new PopupWindow.OnDismissListener() {

                //在dismiss中恢复透明度
                public void onDismiss() {
                    WindowManager.LayoutParams lp = ((DataDetailActivity) context).getWindow().getAttributes();
                    lp.alpha = 1f;
                    ((DataDetailActivity) context).getWindow().setAttributes(lp);
                    ((DataDetailActivity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                }
            });
        }


    }


}
