package com.sq.system.captcha.impl;

import com.sq.system.captcha.api.CaptchaGenerator;
import com.sq.system.captcha.model.CaptchaResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class ImageCaptchaGenerator implements CaptchaGenerator {

    private final StringRedisTemplate redisTemplate;

    public ImageCaptchaGenerator(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getType() {
        return "image";
    }

    @Override
    public CaptchaResponse generate() {
        int width = 220, height = 70;
        String code = generateCode(4);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        // 抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // ====== ① 可爱风背景：粉蓝奶油渐变 ======
        Paint bg = new GradientPaint(
                0, 0, new Color(255, 225, 235),     // 奶粉
                width, height, new Color(210, 245, 255) // 奶蓝
        );
        g.setPaint(bg);
        g.fillRect(0, 0, width, height);

        // ====== ② 轻微柔光覆盖（整体更奶） ======
        g.setComposite(AlphaComposite.SrcOver.derive(0.10f));
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setComposite(AlphaComposite.SrcOver);

        // ====== ③ 泡泡圆点 ======
        for (int i = 0; i < 14; i++) {
            int r = 6 + (int) (Math.random() * 14);
            int cx = (int) (Math.random() * width);
            int cy = (int) (Math.random() * height);
            Color bubble = Math.random() > 0.5
                    ? new Color(255, 255, 255, 80)
                    : new Color(255, 170, 200, 55);
            g.setColor(bubble);
            g.fillOval(cx, cy, r, r);
        }

        // ====== ④ 云朵（很淡） ======
        for (int i = 0; i < 2; i++) {
            int x = 10 + (int) (Math.random() * (width - 90));
            int y = 6 + (int) (Math.random() * 18);
            drawCloud(g, x, y, 95, 26);
        }

        // ====== ⑤ 爪印纹理（非常淡，不干扰识别） ======
        for (int i = 0; i < 9; i++) {
            int px = (int) (Math.random() * (width - 35));
            int py = 28 + (int) (Math.random() * (height - 40));
            float scale = 0.45f + (float) Math.random() * 0.55f;
            Color paw = Math.random() > 0.5
                    ? new Color(255, 140, 170, 22)
                    : new Color(120, 200, 255, 22);
            drawTinyPaw(g, px, py, scale, paw);
        }

        // ====== ⑥ 字体：圆润一点更可爱（优先微软雅黑，其次系统默认） ======
        Font font = new Font("Microsoft YaHei", Font.BOLD, 38);
        g.setFont(font);

        // ====== ⑦ 绘制字符：轻微旋转 + 白描边 + 主色（粉/蓝随机） ======
        char[] chars = code.toCharArray();
        int startX = 26;
        int baseY = 48;
        int step = 45;

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            double angle = Math.toRadians((Math.random() - 0.5) * 14); // -7° ~ 7°
            int charX = startX + i * step;
            int charY = baseY + (int) ((Math.random() - 0.5) * 6);

            AffineTransform old = g.getTransform();
            g.rotate(angle, charX, charY);

            // 白色描边（更像卡通贴纸）
            g.setColor(new Color(255, 255, 255, 210));
            g.drawString(String.valueOf(c), charX + 2, charY + 1);
            g.drawString(String.valueOf(c), charX - 2, charY - 1);
            g.drawString(String.valueOf(c), charX + 2, charY - 1);
            g.drawString(String.valueOf(c), charX - 2, charY + 1);

            // 主字色（粉蓝随机）
            Color main = Math.random() > 0.5
                    ? new Color(255, 120, 160)
                    : new Color(60, 160, 230);
            g.setColor(main);
            g.drawString(String.valueOf(c), charX, charY);

            // 轻微阴影（柔和）
            g.setColor(new Color(0, 0, 0, 40));
            g.drawString(String.valueOf(c), charX + 1, charY + 2);

            g.setTransform(old);
        }

        g.dispose();

        // 输出 Base64 PNG
        String base64;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            boolean ok = ImageIO.write(image, "png", out);
            if (!ok) throw new IllegalStateException("ImageIO.write 返回 false，未找到 PNG Writer");
            base64 = Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("生成验证码图片失败", e);
        }

        // 写 Redis
        String uuid = UUID.randomUUID().toString();
        String key = "captcha:image:" + uuid;
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

        CaptchaResponse resp = new CaptchaResponse();
        resp.setUuid(uuid);
        resp.setType("image");
        resp.setData(base64);
        resp.setExtra(null);
        return resp;
    }

    /** 云朵 */
    private void drawCloud(Graphics2D g, int x, int y, int w, int h) {
        Composite old = g.getComposite();
        g.setComposite(AlphaComposite.SrcOver.derive(0.18f));
        g.setColor(Color.WHITE);

        int baseY = y + h / 2;
        g.fillOval(x, baseY - h / 2, w / 3, h);
        g.fillOval(x + w / 5, baseY - h, w / 3, h + 6);
        g.fillOval(x + w / 2 - 10, baseY - h / 2 - 4, w / 3, h + 4);
        g.fillRoundRect(x, baseY - h / 3, w, h / 2, h, h);

        g.setComposite(old);
    }

    /** 小爪印 */
    private void drawTinyPaw(Graphics2D g, int x, int y, float s, Color color) {
        g.setColor(color);

        int toe = Math.round(7 * s);
        int padW = Math.round(14 * s);
        int padH = Math.round(12 * s);

        g.fill(new Ellipse2D.Float(x + Math.round(0 * s),  y + Math.round(0 * s),  toe, toe));
        g.fill(new Ellipse2D.Float(x + Math.round(10 * s), y + Math.round(-2 * s), toe, toe));
        g.fill(new Ellipse2D.Float(x + Math.round(20 * s), y + Math.round(0 * s),  toe, toe));
        g.fill(new Ellipse2D.Float(x + Math.round(30 * s), y + Math.round(4 * s),  toe, toe));
        g.fill(new Ellipse2D.Float(x + Math.round(10 * s), y + Math.round(10 * s), padW, padH));
    }

    private String generateCode(int len) {
        String chars = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }

    @Override
    public boolean verify(String uuid, String input) {
        String key = "captcha:image:" + uuid;
        String real = redisTemplate.opsForValue().get(key);
        if (real == null) return false;
        redisTemplate.delete(key);
        return real.equalsIgnoreCase(input);
    }
}