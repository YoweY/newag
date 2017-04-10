package com.info.aegis.lawpush4android.presenter.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.model.bean.entity.law.DataDetail;
import com.info.aegis.lawpush4android.view.holder.CaseHolder;
import com.info.aegis.lawpush4android.view.holder.ConsultHolder;
import com.info.aegis.lawpush4android.view.holder.EvidenceHolder;
import com.info.aegis.lawpush4android.view.holder.LitigationCostHolder;
import com.info.aegis.lawpush4android.view.holder.LitigationHolder;
import com.info.aegis.lawpush4android.view.holder.LitigationFlowHolder;
import com.info.aegis.lawpush4android.view.holder.LawInfoHolder;
import com.info.aegis.lawpush4android.view.holder.PointHolder;
import com.info.aegis.lawpush4android.view.iml.DataDetailActivity;


/**
 * Created by 申富生 on 2016/12/19.
 * Description :  左侧 内容的Adapter
 */

public class ContentAdapter extends BaseAdapter {

    /**
     * 法律知识
     */
    public static final int LOWINFO = 0;
    /**
     * 诉讼请求
     */
    public static final int LITIGATION = 1;
    /**
     * 证据分析
     */
    public static final int EVIDENCE = 2;
    /**
     * 相关案例
     */
    public static final int CASE = 3;
    /**
     * 司法观点
     */
    public static final int POINT = 4;
    /**
     * 诉讼成本
     */
    public static final int LITIGATIONCOST=5;
    /**
     * 诉讼流程
     */
    public static final int LITIGATIONFLOW = 6;
    /**
     * 人工咨询
     */
    public static final int CONSULT = 7;
    private DataDetailActivity dataDetailActivity;
    private String[] datas;
    private DataDetail detail;
    private int width;
    private String qid;

    public ContentAdapter(DataDetailActivity dataDetailActivity, String[] datas, DataDetail detail) {
        this.dataDetailActivity = dataDetailActivity;
        this.datas = datas;
        this.detail = detail;
    }

    @Override
    public int getCount() {
        return datas.length;
    }

    @Override
    public Object getItem(int position) {
        return datas[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount() + datas.length;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == CONSULT) {
            return CONSULT;
        }
        if (position == LITIGATIONFLOW) {
            return LITIGATIONFLOW;
        }
        if (position==LITIGATIONCOST){
            return LITIGATIONCOST;
        }
        if (position == POINT) {
            return POINT;
        }
        if (position == CASE) {
            return CASE;
        }
        if (position == EVIDENCE) {
            return EVIDENCE;
        }
        if (position == LITIGATION) {
            return LITIGATION;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LawInfoHolder lawInfoHolder = null;
        EvidenceHolder evidenceHolder = null;
        CaseHolder caseHolder = null;
        LitigationHolder litigationHolder = null;
        PointHolder pointHolder = null;
        LitigationFlowHolder litigationFlowHolder = null;
        ConsultHolder consultHolder = null;
        LitigationCostHolder costHolder=null;
        if (convertView == null) {
            switch (getItemViewType(position)) {
                case 0:
                    convertView = View.inflate(dataDetailActivity, R.layout.item_law, null);
                    lawInfoHolder = new LawInfoHolder(dataDetailActivity, convertView, detail.getLaws());
                    convertView.setTag(lawInfoHolder);
                    break;
                case LITIGATION:
                    convertView = View.inflate(dataDetailActivity, R.layout.item_litigation, null);
                    litigationHolder = new LitigationHolder(dataDetailActivity,convertView ,detail.getClaims());
                    convertView.setTag(litigationHolder);
                    break;
                case EVIDENCE:
                    convertView = View.inflate(dataDetailActivity, R.layout.item_evidence, null);
                    evidenceHolder = new EvidenceHolder(dataDetailActivity,convertView, detail.getEvidences());
                    convertView.setTag(evidenceHolder);
                    break;
                case CASE:
                    convertView = View.inflate(dataDetailActivity, R.layout.item_case_sub, null);
                    caseHolder = new CaseHolder(dataDetailActivity, convertView,detail.getGuides(),detail.getCases());

                    convertView.setTag(caseHolder);
                    break;
                case POINT:
                    convertView = View.inflate(dataDetailActivity, R.layout.item_point, null);
                    pointHolder = new PointHolder(dataDetailActivity, convertView, detail.getPoints());
                    convertView.setTag(pointHolder);
                    break;
                case  LITIGATIONCOST:
                    convertView=View.inflate(dataDetailActivity,R.layout.item_cost,null);
                    costHolder=new LitigationCostHolder(convertView);
                    convertView.setTag(costHolder);
                    break;
                case LITIGATIONFLOW:
                    convertView = View.inflate(dataDetailActivity, R.layout.item_litigating_flow, null);
                    litigationFlowHolder = new LitigationFlowHolder(convertView,dataDetailActivity,detail.getLitigationFlowFlow(),detail.getUnLitigationFlow(),width);
                    convertView.setTag(litigationFlowHolder);
                    break;
                case CONSULT:
                    convertView = View.inflate(dataDetailActivity, R.layout.item_consult, null);
                    consultHolder = new ConsultHolder(convertView,dataDetailActivity,qid);
                    convertView.setTag(consultHolder);
                    break;
                default:
                    break;
            }


        } else {
            switch (getItemViewType(position)) {
                case 0:
                    lawInfoHolder = (LawInfoHolder) convertView.getTag();
                    break;
                case LITIGATION:
                    litigationHolder = (LitigationHolder) convertView.getTag();
                    break;
                case EVIDENCE:
                    evidenceHolder = (EvidenceHolder) convertView.getTag();
                    break;
                case CASE:
                    caseHolder = (CaseHolder) convertView.getTag();
                    break;
                case POINT:
                    pointHolder = (PointHolder) convertView.getTag();
                    break;
                case LITIGATIONCOST:
                    costHolder= (LitigationCostHolder) convertView.getTag();
                    break;
                case LITIGATIONFLOW:
                    litigationFlowHolder = (LitigationFlowHolder) convertView.getTag();
                    break;
                case CONSULT:
                    consultHolder = (ConsultHolder) convertView.getTag();
                    break;
                default:
                    break;
            }

        }

        return convertView;
    }


    public void setWidth(int width) {

        this.width = width;
    }

    public void setQid(String qid) {

        this.qid = qid;
    }
}
