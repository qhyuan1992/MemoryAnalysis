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
