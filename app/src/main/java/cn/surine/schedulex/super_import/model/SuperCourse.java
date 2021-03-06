package cn.surine.schedulex.super_import.model;

import androidx.annotation.Keep;

import cn.surine.schedulex.data.entity.BaseVm;

@Keep
public class SuperCourse extends BaseVm {
    public int day;
    public String locale;
    public String name;
    public String smartPeriod;
    public int sectionend;
    public int sectionstart;
    public String teacher;

    @Override
    public String toString() {
        return "SuperCourse{" +
                "day=" + day +
                ", locale='" + locale + '\'' +
                ", name='" + name + '\'' +
                ", smartPeriod='" + smartPeriod + '\'' +
                ", sectionend=" + sectionend +
                ", sectionstart=" + sectionstart +
                ", teacher='" + teacher + '\'' +
                '}';
    }
}
