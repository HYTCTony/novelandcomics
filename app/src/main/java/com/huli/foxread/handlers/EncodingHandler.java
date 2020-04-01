package com.huli.foxread.handlers;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Ryan Tang
 */
public final class EncodingHandler {
    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int PADDING_SIZE_MIN = 8; // 最小留白长度, 单位: px

    public static Bitmap createQRCode(String str, int widthAndHeight) throws WriterException {
        //设置生成二维码的字符编码类型UTF-8
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //把要生成二维码的字符串读取到位图矩阵当中
        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        //将位图矩阵的数据存储到一维数组pixels中
        int[] pixels = new int[width * height];

        boolean isFirstBlackPoint = false;
        int startX = 0;
        int startY = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = BLACK;
                    if (isFirstBlackPoint == false) {
                        isFirstBlackPoint = true;
                        startX = x;
                        startY = y;
                    }
                } else {
                    pixels[y * width + x] = WHITE;
                }
            }
        }
        //生成一个空的bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
/**
 * 参数1:写到位图中的颜色值
 * 参数2:从一维数组pixels中读取的第一个颜色值的索引
 * 参数3:位图的宽度
 * 参数4:被写入位图中第一个像素的X坐标
 * 参数5:被写入位图中第一个像素的Y坐标
 * 参数6:从pixels[]中拷贝的每行的颜色个数
 * 参数7：写入到位图中的行数
 */
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);


        // 剪切中间的二维码区域，减少padding区域
        if (startX <= PADDING_SIZE_MIN) return bitmap;

        int x1 = startX - PADDING_SIZE_MIN;
        int y1 = startY - PADDING_SIZE_MIN;
        if (x1 < 0 || y1 < 0) return bitmap;

        int w1 = width - x1 * 2;
        int h1 = height - y1 * 2;

        Bitmap bitmapQR = Bitmap.createBitmap(bitmap, x1, y1, w1, h1);


        return bitmapQR;
    }


    /**
     * 生成二维码Bitmap
     *
     * @param data     文本内容
     * @param logoBm   二维码中心的Logo图标（可以为null）
     * @return 合成后的bitmap
     */
    public static Bitmap createQRImage(String data, Bitmap logoBm, int widthAndHeight) {
        try {
            if (data == null || "".equals(data)) {
                return null;
            }

            //配置参数
            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            //设置空白边距的宽度
            hints.put(EncodeHintType.MARGIN, 1); //default is 4

            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            //将位图矩阵的数据存储到一维数组pixels中
            int[] pixels = new int[width * height];

            boolean isFirstBlackPoint = false;
            int startX = 0;
            int startY = 0;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = BLACK;
                        if (isFirstBlackPoint == false) {
                            isFirstBlackPoint = true;
                            startX = x;
                            startY = y;
                        }
                    } else {
                        pixels[y * width + x] = WHITE;
                    }
                }
            }
            //生成一个空的bitmap
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
/**
 * 参数1:写到位图中的颜色值
 * 参数2:从一维数组pixels中读取的第一个颜色值的索引
 * 参数3:位图的宽度
 * 参数4:被写入位图中第一个像素的X坐标
 * 参数5:被写入位图中第一个像素的Y坐标
 * 参数6:从pixels[]中拷贝的每行的颜色个数
 * 参数7：写入到位图中的行数
 */
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            if (logoBm != null) {
                bitmap = addLogo(bitmap, logoBm);
            }

            return bitmap;
            //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
            //return bitmap != null && bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 把两张图合并成一个
     *
     * @param src:画布背景的图
     * @param logo：把此图放在画布背景的图上
     * @return 备注：logo的大小不能大于src的五分之一
     */
    public static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }
}
