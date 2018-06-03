package com.memory.analysis.entity;

/**
 * @author cainjiang
 * @date 2018/5/29
 */
public class HandleFileEntity {
    public String fileName;
    public int status;

    public HandleFileEntity() {
    }

    public HandleFileEntity(String fileName, int status) {
        this.fileName = fileName;
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "HandleFileEntity{" +
                "fileName='" + fileName + '\'' +
                ", status=" + status +
                '}';
    }
}
