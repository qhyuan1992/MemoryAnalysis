package com.tencent.cain;

import org.eclipse.mat.parser.internal.SnapshotFactory;
import org.eclipse.mat.parser.model.PrimitiveArrayImpl;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.model.IClass;
import org.eclipse.mat.snapshot.model.IObject;
import org.eclipse.mat.util.ConsoleProgressListener;
import org.eclipse.mat.util.IProgressListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author cainjiang
 * @date 2018/7/31
 */
public class BTMain {
    public static void main(String[] args) throws Exception {
//        String arg = args[args.length - 1];
//        String fileName = arg;
        String fileName = "src/main/files/standard_dump_LowMemory_18-07-26_23.37.09.hprof";
        String outPath = "src/main/out/";
        File file = new File(outPath);
        if (!file.exists())
        {
            file.mkdirs();
        }
        IProgressListener listener = new ConsoleProgressListener(System.out);
        SnapshotFactory sf = new SnapshotFactory();
        ISnapshot snapshot = sf.openSnapshot(new File(fileName), new HashMap<String, String>(), listener);
        System.out.println(snapshot.getSnapshotInfo());
        System.out.println();
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
            System.out.println(String.format("%s instances = %d, retained size >= %d", clazz.getName(), objIds.length, minRetainedSize));
            for (int i = 0; i < objIds.length; i++) {
                IObject bmp = snapshot.getObject(objIds[i]);
                String address = Long.toHexString(snapshot.mapIdToAddress(objIds[i]));
                int height = ((Integer) bmp.resolveValue("mHeight")).intValue();
                int width = ((Integer) bmp.resolveValue("mWidth")).intValue();
                byte[] buffer;
                PrimitiveArrayImpl array = (PrimitiveArrayImpl) bmp.resolveValue("mBuffer");
                if ((height <= 0) || (width <= 0)) {
                    System.out.println(String.format("Bitmap address=%s has bad height %d or width %d!", address, height, width));
                    continue;
                }
                if (array == null) {
                    System.out.println(String.format("Bitmap address=%s has null buffer value!", address));
                    continue;
                }
                buffer = (byte[]) array.getValueArray();
                File outputFile = new File(outPath+ address + ".data");
                FileOutputStream outputStream  =new FileOutputStream(outputFile);
                outputStream.write(buffer);
                outputStream.close();
//                int[] rgba = new int[width * height];
//                for (int j = 0; j < width * height; j++) {
//                    rgba[j] = ((buffer[j * 4] << 16) | (buffer[j * 4 + 1] << 8) | (buffer[j * 4 + 2]) | (buffer[j * 4 + 3] << 24));
//                }
//                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_565_RGB);
//                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//                image.setRGB(0, 0, width, height, rgba, 0, width);
//                try {
//                    File outputfile = new File("bmp_" + address + ".png");
//                    ImageIO.write(image, "png", outputfile);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                if(address.equals("23b1d4c0")){
                    System.out.println("获取到了最大的图像");
                }
                System.out.println(String.format("id=%d, address=%s, height=%d, width=%d, size=%d", objIds[i], address, height, width, buffer.length));
            }
        }
    }
}
