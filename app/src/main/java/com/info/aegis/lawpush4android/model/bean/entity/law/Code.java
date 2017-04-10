package com.info.aegis.lawpush4android.model.bean.entity.law;

import java.util.Date;

/**
 * Created by Administrator on 2016/10/19.
 */
public class Code implements Cloneable {

    private String id;
    private String fdep;
    private Date fdate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFdep() {
        return fdep;
    }

    public void setFdep(String fdep) {
        this.fdep = fdep;
    }

    public Date getFdate() {
        return fdate;
    }

    public void setFdate(Date fdate) {
        this.fdate = fdate;
    }

}
