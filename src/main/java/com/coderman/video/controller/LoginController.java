package com.coderman.video.controller;

import com.coderman.video.constant.CommonConstant;
import com.coderman.video.model.ImageCode;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

/**
 * @author ：zhangyukang
 * @date ：2025/05/20 10:20
 */
@Controller
public class LoginController {

    @ApiOperation(value = "用户登录页面", notes = "登录页面")
    @GetMapping(value = "/login")
    public String loginPage() {
        return "login";
    }

    @ApiOperation(value = "获取验证码", notes = "获取验证码")
    @GetMapping("/code/image")
    public void createCode(HttpServletResponse response) throws IOException {
        // 1. 生成验证码
        ImageCode imageCode = createImageCode();

        // 2. 存入 session
        Objects.requireNonNull(RequestContextHolder.getRequestAttributes())
                .setAttribute(CommonConstant.SESSION_KEY_IMAGE_CODE, imageCode.getCode(), RequestAttributes.SCOPE_SESSION);

        // 3. 设置响应头
        response.setContentType("image/jpeg");
        response.setHeader("Cache-Control", "no-store, no-cache");

        // 4. 输出图片流
        ImageIO.write(imageCode.getImage(), "jpeg", response.getOutputStream());
    }

    /**
     * 创建验证码图像
     */
    private ImageCode createImageCode() {
        int width = 100;
        int height = 36;
        int length = 4;
        int expireIn = 60;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        Random random = new Random();

        // 背景
        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, width, height);

        // 干扰线
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }

        // 字体
        g.setFont(new Font("Arial", Font.BOLD, 24));

        // 验证码字符
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String rand = String.valueOf(random.nextInt(10));
            codeBuilder.append(rand);
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(rand, 20 * i + 10, 26); // 位置微调
        }

        g.dispose();
        return new ImageCode(image, codeBuilder.toString(), expireIn);
    }

    /**
     * 获取随机颜色
     */
    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        fc = Math.min(fc, 255);
        bc = Math.min(bc, 255);
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

}

