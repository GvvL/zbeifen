package com.neili.net.entry;

/**
 * Created by gvvl on 2016/7/21.
 */
public class ResponseDataItem<T> extends ResponseDataBase{
    private T data;
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }

}
