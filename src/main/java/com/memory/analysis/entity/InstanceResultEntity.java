package com.memory.analysis.entity;

import com.memory.analysis.utils.FormatUtil;

/**
 * @author cainjiang
 * @date 2018/5/28
 */
public class InstanceResultEntity {
    public String objectName;
    public String objectAddressID;
    public int num;
    // 总共泄漏的内存数，单位字节数
    public double sumLeak;
    // 平均泄漏的内存数，单位字节数
    public double aveLeak;
    // 最大泄漏的内存数，单位字节数
    public double maxLeak;
    // 最大泄漏文件名
    public String maxLeakFileName;
    public String gcRoot;

    public InstanceResultEntity() {
    }

    public InstanceResultEntity(String objectName, String objectAddressID, int num, double sumLeak, double aveLeak, double maxLeak, String maxLeakFileName,
                                String gcRoot) {
        this.objectName = objectName;
        this.objectAddressID = objectAddressID;
        this.num = num;
        this.sumLeak = sumLeak;
        this.aveLeak = aveLeak;
        this.maxLeak = maxLeak;
        this.maxLeakFileName = maxLeakFileName;
        this.gcRoot = gcRoot;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectAddressID() {
        return objectAddressID;
    }

    public void setObjectAddressID(String objectAddressID) {
        this.objectAddressID = objectAddressID;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public double getSumLeak() {
        return sumLeak;
    }

    public void setSumLeak(double sumLeak) {
        this.sumLeak = sumLeak;
    }

    public double getAveLeak() {
        return aveLeak;
    }

    public void setAveLeak(double aveLeak) {
        this.aveLeak = aveLeak;
    }

    public double getMaxLeak() {
        return maxLeak;
    }

    public void setMaxLeak(double maxLeak) {
        this.maxLeak = maxLeak;
    }

    public String getMaxLeakFileName() {
        return maxLeakFileName;
    }

    public void setMaxLeakFileName(String maxLeakFileName) {
        this.maxLeakFileName = maxLeakFileName;
    }

    public String getGcRoot() {
        return gcRoot;
    }

    public void setGcRoot(String gcRoot) {
        this.gcRoot = gcRoot;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(maxLeakFileName).
                append("\n").
                append(objectName).
                append(" reference ").
                append(FormatUtil.formatByteSize((long) maxLeak)).
                append(" ").
                append(objectAddressID).
                append("\n").
                append(gcRoot).
                append("\n");
        return builder.toString();
    }
}
