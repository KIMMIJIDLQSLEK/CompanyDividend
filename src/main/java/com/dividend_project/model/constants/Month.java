package com.dividend_project.model.constants;

public enum Month {

    Jan("Jan",1),
    FEB("Feb",2),
    MAR("Mar",3),
    APR("Apr",4),
    MAY("May",5),
    JUN("Jun",6),
    JUL("Jul",7),
    AUG("Aug",8),
    SEP("Sep",9),
    OCT("Oct",10),
    NOV("Nov",11),
    DEC("Dec",12);



    private String s;
    private int num;

    Month(String s,int n){
        this.s=s;
        this.num=n;
    }

    public static int strToNum(String getS){
        for(var m:Month.values()){
            if(m.s.equals(getS)){
                return m.num;
            }
        }
        return -1;
    }


}
