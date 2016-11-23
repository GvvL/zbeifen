package com.neili.net.entry;

import java.util.ArrayList;

/**
 * Created by gvvl on 2016/7/21.
 * 多条数据返回
 */

public class ResponseDataList<T extends Bean> extends ResponseDataBase{
    private ArrayList<T> data;

    public ArrayList<T> getData() {
        return data;
    }
    public void setData(ArrayList<T> data) {
        this.data = data;
    }
}
