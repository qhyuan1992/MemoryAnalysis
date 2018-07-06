package com.memory.analysis.entity;

/**
 * @author cainjiang
 * @date 2018/5/29
 */
public class HandleResultEntity {
    // 文件名
    public String fileName;
    /**
     * 处理类型
     * 1：instance和activity处理
     * 2：class处理
     */
    public int handleType;
    /**
     * 处理结果
     * 0：默认，未被成功解析
     * 1：被成功解析
     */
    public int status = 0;

    public HandleResultEntity(String fileName, int handleType, int status) {
        this.fileName = fileName;
        this.handleType = handleType;
        this.status = status;
    }

    public HandleResultEntity() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getHandleType() {
        return handleType;
    }

    public void setHandleType(int handleType) {
        this.handleType = handleType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
