package com.tencent.cain;

import org.eclipse.mat.parser.internal.SnapshotFactory;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.model.IClass;
import org.eclipse.mat.util.ConsoleProgressListener;
import org.eclipse.mat.util.IProgressListener;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author cainjiang
 * @date 2018/7/30
 */
public class MyMain {

    private static String VERSION_STRING =
            MyMain.class.getPackage().getSpecificationTitle() + " " +
                    MyMain.class.getPackage().getSpecificationVersion();

    private static void usage(String message) {
        if (message != null) {
            System.err.println("ERROR: " + message);
        }
        System.err.println("Usage:  andromat [-version] [-h|-help] <file>");
        System.err.println();
        System.err.println("\t-version          Report version number");
        System.err.println("\t-h|-help          Print this help and exit");
        System.err.println("\t<file>            The file to read");
        System.exit(1);
    }

    public static void main(String[] args) throws Exception {

//        if (args.length < 1) {
//            usage("No arguments supplied");
//        }

//        for (int i = 0;; i += 2) {
//            if (i > (args.length - 1)) {
//                usage("Option parsing error");
//            }
//            if ("-version".equals(args[i])) {
//                System.out.print(VERSION_STRING);
//                System.exit(0);
//            }
//            if ("-h".equals(args[i]) || "-help".equals(args[i])) {
//                usage(null);
//            }
//            if (i == (args.length - 1)) {
//                break;
//            }
//        }

//        String fileName = args[args.length - 1];
        String fileName = "src/main/files/standard_dump_LowMemory_18-07-27_09.36.18.hprof";

        IProgressListener listener = new ConsoleProgressListener(System.out);

        SnapshotFactory sf = new SnapshotFactory();
        ISnapshot snapshot = sf.openSnapshot(new File(fileName),
                new HashMap<String, String>(), listener);

        System.out.println(snapshot.getSnapshotInfo());
        System.out.println();

//        String[] classNames = {
//            "byte[]",
//            "java.util.HashMap",
//            "android.graphics.Bitmap"
//        };
        String[] classNames = {
                "android.graphics.Bitmap"
        };

        for (String name : classNames) {
            Collection<IClass> classes = snapshot.getClassesByName(name, false);
            if (classes == null || classes.isEmpty()) {
                System.out.println(String.format(
                        "Cannot find class %s in heap dump", name));
                continue;
            }
            assert classes.size() == 1;
            IClass clazz = classes.iterator().next();
            int[] objIds = clazz.getObjectIds();
            long minRetainedSize = snapshot.getMinRetainedSize(objIds, listener);
            System.out.println(String.format(
                    "%s instances = %d, retained size >= %d",
                    clazz.getName(), objIds.length, minRetainedSize));
        }

        Collection<IClass> classes = snapshot.getClasses();
        int[] gcRoots = snapshot.getGCRoots();
//        long retainedHeapSize = snapshot.getRetainedHeapSize(1117670);
        System.out.println("结束");
    }
}
