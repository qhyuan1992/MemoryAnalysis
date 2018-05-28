#LeakCanary使用的包裹泄露的类
``` java
package com.squareup.leakcanary;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import static com.squareup.leakcanary.Preconditions./checkNotNull/;

//**/*@see*/{/*@link*/HeapDump#referenceKey}. *//
final class KeyedWeakReference extends WeakReference<Object> {
  public final String key;
  public final String name;

  KeyedWeakReference(Object referent, String key, String name,
      ReferenceQueue<Object> referenceQueue) {
    super(/checkNotNull/(referent, "referent"), /checkNotNull/(referenceQueue, "referenceQueue"));
    this.key = /checkNotNull/(key, "key");
    this.name = /checkNotNull/(name, "name");
  }
}
```

#APM使用
```java
package com.tencent.magnifiersdk.memory;
public class LeakInspector {
    public static class InspectUUID extends Recyclable {
        private WeakReference<Object> weakObj;
        private String classname = “”;
        private String digest = “”;
        private char[] uuid = null;
        private String toString = “”;

        public InspectUUID() {
        }

        public void recycle() {
            this.weakObj = null;
            this.digest = “”;
            this.uuid = null;
            this.classname = “”;
            this.toString = “”;
        }

        public String toString() {
            if(TextUtils.isEmpty(this.toString)) {
                StringBuilder sb = new StringBuilder(64);
                sb.append(this.classname);
                sb.append(“@“);
                if(this.uuid != null) {
                    sb.append(this.uuid);
                }

                if(!TextUtils.isEmpty(this.digest)) {
                    sb.append(“_”);
                    sb.append(this.digest);
                }

                this.toString = sb.toString();
            }

            return this.toString;
        }
    }
}
```

# 数据库使用

使用result_table.sql这个文件新建在本地使用数据库软件新建一个数据库:

数据库：auto_memory_analyze_result_db

表：result_table

DBUtil中使用的是我本地新建的数据库，其中：

数据库用户名：root

密码：jiangyu12315

这个可以根据情况自己新建用户并设置密码，在使用是修改DBUtil中相应的代码即可：

```java
//URL指向要访问的数据库名auto_memory_analyze_result_db
    public static final String URL = "jdbc:mysql://localhost:3306/auto_memory_analyze_result_db?useUnicode=true&characterEncoding=utf-8";
    //MySQL配置时的用户名root
    public static final String USER = "root";
    //MySQL配置时的密码jiang12315
    public static final String PASSWORD = "jiang12315";
```

**ps：**

这里将需要分析的hprof文件拷贝到resources目录下即可