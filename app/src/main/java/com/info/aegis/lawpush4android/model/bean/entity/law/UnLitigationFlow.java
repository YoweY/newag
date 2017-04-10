package com.info.aegis.lawpush4android.model.bean.entity.law;

import java.util.List;

/**
 * Created by SFS on 2017/1/12.
 * Description : 非诉流程
 */

public class UnLitigationFlow {
    private List<FlowStep> steps;

    public List<FlowStep> getSteps() {
        return steps;
    }

    public void setSteps(List<FlowStep> steps) {
        this.steps = steps;
    }

}
