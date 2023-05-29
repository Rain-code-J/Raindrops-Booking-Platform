package com.rain.yygh.enums;

public enum StatusEnum {
    LOCK(0,"锁定"),
    NORMAL(1,"正常")
    ;
    private Integer status;
    private String name;

    public static String getStatusNameByStatus(Integer status){
        StatusEnum[] values = StatusEnum.values();
        for (StatusEnum value : values) {
            if (value.getStatus().intValue() == status.intValue()){
                return value.getName();
            }
        }
        return "";
    }

    StatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
