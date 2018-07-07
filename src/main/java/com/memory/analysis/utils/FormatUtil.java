package com.memory.analysis.utils;

import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;

public class FormatUtil {
    public static final int KB = 1024;
    public static final int MB = 1024 * KB;

    public static String formatByteSize(long size){
        long kb = 1024;
        long mb = kb*1024;
        long gb = mb*1024;
        if (size >= gb){
            return String.format("%.2f GB",(float)size/gb);
        }else if (size >= mb){
            float f = (float) size/mb;
            return String.format(f > 100 ?"%.2f MB":"%.2f MB",f);
        }else if (size > kb){
            float f = (float) size / kb;
            return String.format(f>100?"%.2f KB":"%.2f KB",f);
        }else {
            return String.format("%d B",size);
        }
    }

    /**
     * 将double类型数据转换为百分比格式，并保留小数点前IntegerDigits位和小数点后FractionDigits位
     */
    public static String formatPercent(double d){
        int FractionDigits = 2;
        NumberFormat nf = java.text.NumberFormat.getPercentInstance();
//        nf.setMaximumIntegerDigits(IntegerDigits);//小数点前保留几位
        nf.setMinimumFractionDigits(FractionDigits);// 小数点后保留几位
        String str = nf.format(d);
        return str;
    }

    public static boolean realEqual(String str1, String str2) {
        return str1.contains(str2) || str2.contains(str1);
    }

    public static String formatAddr(long id) {
        return "0x" + Long.toHexString(id);
    }

    /**
     * 保留小数点后n位
     *
     * @param n
     * @param source
     * @return
     */
    public static double formatDouble(int n, double source) {
        BigDecimal bigDecimal = new BigDecimal(source);
        double target = bigDecimal.setScale(n, BigDecimal.ROUND_HALF_UP).doubleValue();
        return target;
    }

    public static String generateFilepath(int type, String fileName) {
        String result = null;
        switch (type) {
            case Constants.TYPE_ACTIVITY:
                result = String.format(Constants.ACTIVITYOUTFILEPATHPATTEN, fileName, fileName);
                break;
            case Constants.TYPE_CLASS:
                result = String.format(Constants.CLASSOUTFILEPATHPATTEN, fileName, fileName);
                break;
            case Constants.TYPE_INSTANCE:
                result = String.format(Constants.INSTANCEOUTFILEPATHPATTEN, fileName, fileName);
                break;
            case Constants.TYPE_REPORT:
                result = String.format(Constants.PARSERESULTOUTFILEPATHPATTEN,fileName);
        }
        return result;
    }

    public static String getFileName(File file) {
        return file.getName().substring(0, file.getName().lastIndexOf("."));
    }
}
