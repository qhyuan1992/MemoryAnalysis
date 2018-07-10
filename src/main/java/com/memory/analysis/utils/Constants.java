package com.memory.analysis.utils;

/**
 * @author cainjiang
 * @date 2018/7/5
 */
public class Constants {
    public static final int PROCESS_RESULT_OK = 0;
    public static final int PROCESS_RESULT_DEFAULT = 1;
    public static final int PROCESS_RESULT_FAIL = 2;
    public static final int PROCESS_RESULT_FAIL_INTERRRUPTED = 3;
    public static final int PROCESS_RESULT_FAIL_EXECUTION = 4;
    public static final int PROCESS_RESULT_FAIL_TIMEOUT = 5;

    public static final int HANDLE_TYPE_INSTANCE = 1;
    public static final int HANDLE_TYPE_CLASS = 2;

    public static final int HANDLE_STATUS_OK = 1;
    public static final int HANDLE_STATUS_FAIL = 0;

    // instance_result_table
    public static final String INSTANCE_RESULT_TABLE = "instance_result_table";
    public static final String INSTANCE_RESULT_TABLE_OBJECT_NAME = "object_name";
    public static final String INSTANCE_RESULT_TABLE_OBJECT_ADDRESS_ID = "object_address_id";
    public static final String INSTANCE_RESULT_TABLE_SUM_NUM = "sum_num";
    public static final String INSTANCE_RESULT_TABLE_SUM_LEAK = "sum_leak";
    public static final String INSTANCE_RESULT_TABLE_AVE_LEAK = "ave_leak";
    public static final String INSTANCE_RESULT_TABLE_MAX_LEAK = "max_leak";
    public static final String INSTANCE_RESULT_TABLE_MAX_LEAK_FILE_NAME = "max_leak_file_name";
    public static final String INSTANCE_RESULT_TABLE_GC_ROOT = "gc_root";

    // activity_result_table
    public static final String ACTIVITY_RESULT_TABLE = "activity_result_table";
    public static final String ACTIVITY_RESULT_TABLE_OBJECT_NAME = "object_name";
    public static final String ACTIVITY_RESULT_TABLE_OBJECT_ADDRESS_ID = "object_address_id";
    public static final String ACTIVITY_RESULT_TABLE_SUM_NUM = "sum_num";
    public static final String ACTIVITY_RESULT_TABLE_SUM_LEAK = "sum_leak";
    public static final String ACTIVITY_RESULT_TABLE_AVE_LEAKK = "ave_leak";
    public static final String ACTIVITY_RESULT_TABLE_MAX_LEAK_FILE_NAME = "max_leak_file_name";
    public static final String ACTIVITY_RESULT_TABLE_GC_ROOT = "gc_root";

    // class_result_table
    public static final String CLASS_RESULT_TABLE = "class_result_table";
    public static final String CLASS_RESULT_TABLE_OBJECT_NAME = "object_name";
    public static final String CLASS_RESULT_TABLE_SUM_NUM = "sum_num";
    public static final String CLASS_RESULT_TABLE_AVE_NUM = "ave_num";
    public static final String CLASS_RESULT_TABLE_MAX_NUM = "max_num";
    public static final String CLASS_RESULT_TABLE_MAX_NUM_FILE_NAME = "max_num_file_name";
    public static final String CLASS_RESULT_TABLE_SUM_RETAINED = "sum_retained";
    public static final String CLASS_RESULT_TABLE_AVE_RETAINED = "ave_retained";
    public static final String CLASS_RESULT_TABLE_MAX_RETAINED = "max_retained";
    public static final String CLASS_RESULT_TABLE_MAX_RETAINED_FILE_NAME = "max_retained_file_name";

    // handle_result_table
    public static final String HANDLE_RESULT_TABLE = "handle_result_table";
    public static final String HANDLE_RESULT_TABLE_FILE_NAME = "file_name";
    public static final String HANDLE_RESULT_TABLE_HANDLE_TYPE = "handle_type";
    public static final String HANDLE_RESULT_TABLE_STATUS = "status";

    // file type
    public static final int TYPE_ACTIVITY = 0;
    public static final int TYPE_CLASS = 1;
    public static final int TYPE_INSTANCE = 2;
    public static final int TYPE_REPORT = 3;
    public static final String INSTANCEOUTFILEPATHPATTEN = "src/main/resources/%s/%s_instance.txt";
    public static final String CLASSOUTFILEPATHPATTEN = "src/main/resources/%s/%s_class.txt";
    public static final String ACTIVITYOUTFILEPATHPATTEN = "src/main/resources/%s/%s_activity.txt";
    public static final String PARSERESULTOUTFILEPATHPATTEN = "src/main/resources/%s-parse-result.txt";

    public static String DIRPATH = "src/main/files/";
    // 处理HPROF文件超时(min)
    public static final int TIME_OUT = 5;
    public static final int TOP = 3;

    public static final String ANDROID_BACTIVITY_CLASS = "Activity";
}
