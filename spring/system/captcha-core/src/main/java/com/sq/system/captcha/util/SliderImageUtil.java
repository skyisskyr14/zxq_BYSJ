package com.sq.system.captcha.util;

import javax.imageio.ImageIO; // Java 原生图像读写工具
import java.awt.*; // 图形绘制支持（颜色、图像等）
import java.awt.image.BufferedImage; // 图像缓冲区类
import java.io.ByteArrayOutputStream; // 内存输出流（用于 Base64 编码）
import java.io.InputStream; // 输入流，用于读取资源文件
import java.util.Base64; // 编码工具（Base64）
import java.util.Random; // 用于生成随机坐标

public class SliderImageUtil {

    // 内部类，用于返回生成结果
    public static class SliderResult {
        public String backgroundBase64; // 背景图（缺口后的）
        public String sliderBase64;     // 滑块图（从缺口处抠出来）
        public int x;                   // 滑块的正确 x 坐标
    }

    public static SliderResult createSliderCaptcha() throws Exception {
        // 加载背景图（你可以替换为自己的路径）
        InputStream bgStream = SliderImageUtil.class.getResourceAsStream("/captcha/bg/1.jpg"); // 从资源目录加载背景图
        BufferedImage background = ImageIO.read(bgStream); // 读取为图像对象

        // 加载滑块遮罩模板（mask 区域定义滑块形状）
        InputStream maskStream = SliderImageUtil.class.getResourceAsStream("/captcha/slider/mask.png"); // 加载遮罩图（黑色区域表示滑块轮廓）
        BufferedImage mask = ImageIO.read(maskStream); // 读取为图像对象

        int width = mask.getWidth();   // 滑块宽度
        int height = mask.getHeight(); // 滑块高度
        int maxX = background.getWidth() - width - 20; // 最大允许 x 坐标（防止超出背景图边界）
        int maxY = background.getHeight() - height - 20; // 最大允许 y 坐标

        Random random = new Random();
        int x = 30 + random.nextInt(maxX - 30); // 滑块的 x 起始点，留一定边距
        int y = 30 + random.nextInt(maxY - 30); // 滑块的 y 起始点

        // 创建透明滑块图层（支持透明背景）
        BufferedImage slider = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // 从背景图中根据遮罩图剪裁出滑块，同时在背景图中留下透明缺口
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int maskRgb = mask.getRGB(i, j); // 获取遮罩图当前像素
                if ((maskRgb & 0xFFFFFF) < 0xCCCCCC) { // 黑色部分（暗色区域）表示需要剪裁
                    int rgb = background.getRGB(x + i, y + j); // 从背景图抠出对应像素
                    slider.setRGB(i, j, rgb); // 设置到滑块图
                    background.setRGB(x + i, y + j, new Color(255, 255, 255, 100).getRGB()); // 把背景图相应位置涂成透明白（缺口）
                }
            }
        }

        // 构造返回对象
        SliderResult result = new SliderResult();
        result.backgroundBase64 = toBase64(background); // 编码为 Base64（背景图）
        result.sliderBase64 = toBase64(slider);         // 编码为 Base64（滑块图）
        result.x = x; // 记录正确的 x 坐标（前端校验用）
        return result;
    }

    // 工具方法：将 BufferedImage 转为 Base64 字符串
    private static String toBase64(BufferedImage img) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream(); // 内存输出流
        ImageIO.write(img, "png", out); // 将图像写入输出流
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray()); // 编码为 Base64 并返回
    }
}
