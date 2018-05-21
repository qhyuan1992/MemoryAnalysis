package com.memory.analysis.process;

import com.memory.analysis.leak.HahaHelper;
import com.squareup.haha.perflib.ArrayInstance;
import com.squareup.haha.perflib.ClassInstance;
import net.sf.image4j.codec.bmp.BMPDecoder;
import net.sf.image4j.codec.ico.ICODecoder;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
    public static BufferedImage arrayToImage(final byte[] source, final int width, final int height) {
        BufferedImage bImageFromConvert = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int i = (x + (width * y)) * 4;
                int r = source[i] & 0xFF;
                int g = source[i + 1] & 0xFF;
                int b = source[i + 2] & 0xFF;
                int a = source[i + 3] & 0xFF;
                bImageFromConvert.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
            }
        }

        return bImageFromConvert;
    }

    public static byte[] imageToArray(BufferedImage image) {

        final int[] pixels = new int[image.getWidth() * image.getHeight()];
        final byte[] bytes = new byte[image.getWidth() * image.getHeight() * 4];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                final int pixel = pixels[y * image.getWidth() + x];
                final int i = (x + (image.getWidth() * y)) * 4;
                bytes[i] = (byte) ((pixel >> 16) & 0xFF);     // Red component
                bytes[i + 1] = (byte) ((pixel >> 8) & 0xFF);      // Green component
                bytes[i + 2] = (byte) (pixel & 0xFF);               // Blue component
                bytes[i + 3] = (byte) ((pixel >> 24) & 0xFF);    // Alpha component. Only for RGBA
            }
        }

        return bytes;
    }

    public static BufferedImage bufferToImage(ByteBuffer buffer, int width, int height) {
        BufferedImage bImageFromConvert = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int i = (x + (width * y)) * 4;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                int a = buffer.get(i + 3) & 0xFF;
                bImageFromConvert.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
            }
        }

        return bImageFromConvert;
    }

    public static ByteBuffer imageToBuffer(BufferedImage image) {

        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4).order(ByteOrder.nativeOrder()); //4 for RGBA, 3 for RGB

        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));               // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }

        buffer.flip();

        return buffer;
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public static BufferedImage flipImage(BufferedImage bi) {
        BufferedImage flipped = new BufferedImage(
                bi.getWidth(),
                bi.getHeight(),
                bi.getType());
        AffineTransform tran = AffineTransform.getTranslateInstance(0, bi.getHeight());
        AffineTransform flip = AffineTransform.getScaleInstance(1d, -1d);
        tran.concatenate(flip);

        Graphics2D g = flipped.createGraphics();
        g.setTransform(tran);
        g.drawImage(bi, 0, 0, null);
        g.dispose();

        return flipped;
    }

    public static BufferedImage loadImage(File file) {
        BufferedImage image = null;
        if (file.getName().endsWith("png")) {
            try {
                image = ImageIO.read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (file.getName().endsWith("ico")) {
            try {
                image = ICODecoder.read(file).get(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (file.getName().endsWith("bmp")) {
            try {
                image = BMPDecoder.read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return image;
    }


}
