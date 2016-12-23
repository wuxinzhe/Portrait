package com.magic.rent.tools;

/**
 * 知识产权声明:本文件自创建起,其内容的知识产权即归属于原作者,任何他人不可擅自复制或模仿.
 * 创建者: wu   创建时间: 2016/12/15
 * 类说明: 缩略图类（通用） 本java类能将jpg、bmp、png、gif图片文件，进行等比或非等比的大小转换。
 * 更新记录：
 */

import com.magic.rent.exception.custom.BusinessException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

public class CompressTools {
    private File file; // 文件对象
    private String inputDir; // 输入图路径
    private String outputDir; // 输出图路径
    private String inputFileName; // 输入图文件名
    private String outputFileName; // 输出图文件名
    private int outputWidth = 100; // 默认输出图片宽
    private int outputHeight = 100; // 默认输出图片高
    private boolean proportion = true; // 是否等比缩放标记(默认为等比缩放)
    private static Logger logger = LoggerFactory.getLogger(CompressTools.class);


    public CompressTools() {
    }

    public CompressTools(boolean proportion) {
        this.proportion = proportion;
    }

    /**
     * 设置输入参数
     *
     * @param inputDir
     * @param inputFileName
     * @return
     */
    public CompressTools setInputInfo(String inputDir, String inputFileName) {
        this.inputDir = inputDir;
        this.inputFileName = inputFileName;
        return this;
    }

    /**
     * 设置输出参数
     *
     * @param outputDir
     * @param outputFileName
     * @param outputHeight
     * @param outputWidth
     * @param proportion
     * @return
     */
    public CompressTools setOutputInfo(String outputDir, String outputFileName, int outputHeight, int outputWidth, boolean proportion) {
        this.outputDir = outputDir;
        this.outputFileName = outputFileName;
        this.outputWidth = outputWidth;
        this.outputHeight = outputHeight;
        this.proportion = proportion;
        return this;
    }


    // 图片处理
    public boolean compress() throws Exception {
        //获得源文件
        file = new File(inputDir);
        if (!file.exists()) {
            throw new BusinessException("文件不存在！");
        }
        Image img = ImageIO.read(file);
        // 判断图片格式是否正确
        if (img.getWidth(null) == -1) {
            System.out.println(" can't read,retry!" + "<BR>");
            return false;
        } else {
            int newWidth;
            int newHeight;
            // 判断是否是等比缩放
            if (this.proportion) {
                // 为等比缩放计算输出的图片宽度及高度
                double rate1 = ((double) img.getWidth(null)) / (double) outputWidth + 0.1;
                double rate2 = ((double) img.getHeight(null)) / (double) outputHeight + 0.1;
                // 根据缩放比率大的进行缩放控制
                double rate = rate1 > rate2 ? rate1 : rate2;
                newWidth = (int) (((double) img.getWidth(null)) / rate);
                newHeight = (int) (((double) img.getHeight(null)) / rate);
            } else {
                newWidth = outputWidth; // 输出的图片宽度
                newHeight = outputHeight; // 输出的图片高度
            }
            long start = System.currentTimeMillis();
            BufferedImage tag = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            /*
             * Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的
             * 优先级比速度高 生成的图片质量比较好 但速度慢
             */
            tag.getGraphics().drawImage(img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
            FileOutputStream out = new FileOutputStream(outputDir);

            // JPEGImageEncoder可适用于其他图片类型的转换
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(tag);
            out.close();
            long time = System.currentTimeMillis() - start;
            logger.info("[输出路径]：" + outputDir + "\t[图片名称]：" + outputFileName + "\t[压缩前大小]：" + getPicSize() + "\t[耗时]：" + time + "毫秒");
            return true;
        }
    }


    /**
     * 简单压缩方法，压缩后图片将直接覆盖源文件
     *
     * @param images
     * @return
     * @throws Exception
     */
    public boolean simpleCompress(File images) throws Exception {
        setInputInfo(images.getPath(), images.getName());
        setOutputInfo(images.getPath(), images.getName(), 300, 300, true);
        return compress();
    }

    /**
     * 获取图片大小，单位KB
     *
     * @return
     */
    private String getPicSize() {
        return file.length() / 1024 + "KB";
    }

    public static void main(String[] args) throws Exception {
        CompressTools compressTools = new CompressTools();
        compressTools.setInputInfo("/Users/wu/Downloads/background.jpg", "background.jpg");
        compressTools.setOutputInfo("/Users/wu/Downloads/background2.jpg", "background2.jpg", 633, 1920, false);
        compressTools.compress();
    }

}