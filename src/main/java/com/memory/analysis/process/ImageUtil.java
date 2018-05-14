package com.memory.analysis.process;

import com.memory.analysis.leak.HahaHelper;
import com.squareup.haha.perflib.ArrayInstance;
import com.squareup.haha.perflib.ClassInstance;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ImageUtil {
    public static final String ANDROID_BITMAP_CLASS = "android.graphics.Bitmap";

    public static void getImage(ClassInstance bitmapInstance) throws IOException {
        int width = 0;
        int height = 0;
        byte[] data = null;

        final List<ClassInstance.FieldValue> values = HahaHelper.classInstanceValues(bitmapInstance);

        for (ClassInstance.FieldValue fieldValue : values) {
            if ("mWidth".equals(fieldValue.getField().getName())) {
                width = (Integer) fieldValue.getValue();
            } else if ("mHeight".equals(fieldValue.getField().getName())) {
                height = (Integer) fieldValue.getValue();
            } else if ("mBuffer".equals(fieldValue.getField().getName())) {
                ArrayInstance arrayInstance = (ArrayInstance) fieldValue.getValue();
                Object[] boxedBytes = arrayInstance.getValues();
                data = new byte[boxedBytes.length];
                for (int i = 0; i < data.length; i++) {
                    data[i] = (Byte) boxedBytes[i];
                }
            }
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        /*for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int offset = 4 * (row * width + col);

                int byte3 = 0xff & data[offset++];
                int byte2 = 0xff & data[offset++];
                int byte1 = 0xff & data[offset++];
                int byte0 = 0xff & data[offset++];

                int alpha = byte0;
                int red = byte1;
                int green = byte2;
                int blue = byte3;

                int pixel = (alpha << 24) | (blue << 16) | (green << 8) | red;

                image.setRGB(col, row, pixel);
            }
        }*/

        int[] rgba = new int[width*height];
        for(int j = 0; j < width*height; j++)
        {
            rgba[j] = ((data[j*4]<<16) | (data[j*4+1]<<8) | (data[j*4+2]) | (data[j*4+3]<<24));
        }
        image.setRGB(0, 0, width, height, rgba, 0, width);

        final OutputStream inb = new FileOutputStream("bitmap-0x" + Integer.toHexString((int) bitmapInstance.getId()) + ".png");
        final ImageWriter wrt = ImageIO.getImageWritersByFormatName("png").next();
        final ImageOutputStream imageOutput = ImageIO.createImageOutputStream(inb);
        wrt.setOutput(imageOutput);
        wrt.write(image);
        inb.close();
    }
}
