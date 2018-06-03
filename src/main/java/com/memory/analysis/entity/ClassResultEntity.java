package com.memory.analysis.entity;

/**
 * @author cainjiang
 * @date 2018/5/30
 */
public class ClassResultEntity {
    public String objectName;
    public double sumNum;
    public double aveNum;
    public double maxNum;
    public String maxNumFileName;
    public double sumRetained;
    public double aveRetained;
    public double maxRetained;
    public String maxRetainedFileName;

    public ClassResultEntity() {
    }

    public ClassResultEntity(String objectName, int sumNum, int aveNum, int maxNum, String maxNumFileName, double sumRetained, double aveRetained, double
            maxRetained, String maxRetainedFileName) {
        this.objectName = objectName;
        this.sumNum = sumNum;
        this.aveNum = aveNum;
        this.maxNum = maxNum;
        this.maxNumFileName = maxNumFileName;
        this.sumRetained = sumRetained;
        this.aveRetained = aveRetained;
        this.maxRetained = maxRetained;
        this.maxRetainedFileName = maxRetainedFileName;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public double getSumNum() {
        return sumNum;
    }

    public void setSumNum(int sumNum) {
        this.sumNum = sumNum;
    }

    public double getAveNum() {
        return aveNum;
    }

    public void setAveNum(int aveNum) {
        this.aveNum = aveNum;
    }

    public double getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public String getMaxNumFileName() {
        return maxNumFileName;
    }

    public void setMaxNumFileName(String maxNumFileName) {
        this.maxNumFileName = maxNumFileName;
    }

    public double getSumRetained() {
        return sumRetained;
    }

    public void setSumRetained(double sumRetained) {
        this.sumRetained = sumRetained;
    }

    public double getAveRetained() {
        return aveRetained;
    }

    public void setAveRetained(double aveRetained) {
        this.aveRetained = aveRetained;
    }

    public double getMaxRetained() {
        return maxRetained;
    }

    public void setMaxRetained(double maxRetained) {
        this.maxRetained = maxRetained;
    }

    public String getMaxRetainedFileName() {
        return maxRetainedFileName;
    }

    public void setMaxRetainedFileName(String maxRetainedFileName) {
        this.maxRetainedFileName = maxRetainedFileName;
    }

    @Override
    public String toString() {
        return "ClassResultEntity{" +
                "objectName='" + objectName + '\'' +
                ", sumNum=" + sumNum +
                ", aveNum=" + aveNum +
                ", maxNum=" + maxNum +
                ", maxNumFileName='" + maxNumFileName + '\'' +
                ", sumRetained=" + sumRetained +
                ", aveRetained=" + aveRetained +
                ", maxRetained=" + maxRetained +
                ", maxRetainedFileName='" + maxRetainedFileName + '\'' +
                '}';
    }
}
