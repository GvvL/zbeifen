package com.neili.net.entry;

/**
 * Created by gvvl on 2016/7/21.
 * 数据回应实体
 */

abstract class ResponseDataBase {
    private int code;
    private String msg;
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
