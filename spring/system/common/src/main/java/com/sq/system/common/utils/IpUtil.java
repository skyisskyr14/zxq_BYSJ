package com.sq.system.common.utils;

import org.lionsoul.ip2region.xdb.Searcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import jakarta.servlet.http.HttpServletRequest; // ✅ 正确
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP 工具类：支持获取客户端真实 IP、归属地定位（基于 ip2region.xdb）
 */
public class IpUtil {

    private static final Logger logger = LoggerFactory.getLogger(IpUtil.class);
    private static final String LOCAL_IP = "127.0.0.1";
    private static Searcher searcher;

    static {
        try {
            // 加载 ip2region.xdb 文件（必须放在 resources/ip2region/ip2region.xdb）
            InputStream is = IpUtil.class.getResourceAsStream("/ip2region/ip2region.xdb");
            byte[] dbBinStr = FileCopyUtils.copyToByteArray(is);
            searcher = Searcher.newWithBuffer(dbBinStr);
            logger.info("IP2Region 数据加载成功");
        } catch (Exception e) {
            logger.error("IP2Region 初始化失败", e);
        }
    }

    /**
     * 获取客户端真实 IP 地址（支持代理穿透）
     */
    public static String getIp(HttpServletRequest request) {
        String[] headers = {
                "X-Original-Forwarded-For", "X-Forwarded-For", "x-forwarded-for",
                "Proxy-Client-IP", "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        String ip = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(ip)) return LOCAL_IP;
        return ip;
    }

    /**
     * 根据 IP 获取归属地信息（例如：中国|华东|江苏省|南京市|电信）
     */
    public static String getIp2region(String ip) {
        if (searcher == null) {
            return "未知归属地";
        }
        try {
            String region = searcher.search(ip);
            if (region != null) {
                return region.replace("|0", "").replace("0|", "");
            }
        } catch (Exception e) {
            logger.warn("IP2Region 查询失败: {}", ip, e);
        }
        return "未知归属地";
    }

    /**
     * 获取本机 IP 地址
     */
    public static String getHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return LOCAL_IP;
        }
    }

    /**
     * 获取主机名
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "未知";
        }
    }
}
