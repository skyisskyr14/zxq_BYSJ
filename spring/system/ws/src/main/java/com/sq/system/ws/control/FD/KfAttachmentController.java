package com.sq.system.ws.control.FD;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sq.system.ws.entity.KfAttachmentEntity;
import com.sq.system.ws.repository.KfAttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RestController
@RequestMapping("/upload/kf/FD")
@RequiredArgsConstructor
public class KfAttachmentController {

    private final KfAttachmentRepository attachmentRepository;

    private static final ObjectMapper M = new ObjectMapper(); // ← 新增这行
    private static final AtomicLong SID = new AtomicLong(1);

    @Value("${app.kf.base-dir}")
    private String baseDir; // 物理存储根目录

    @Value("${app.kf.public-host}")
    private String publicHost; // 对外访问域名（需配置静态资源映射 /uploads/** -> baseDir）

    @PostMapping("/user")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId,
            @RequestParam("agentId") String agentId
    ) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "msg", "文件为空"));
        }

        String mime = file.getContentType();
        long size = file.getSize();

// 确保目录存在
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path dir = Paths.get(baseDir, String.valueOf(userId), datePath);
        Files.createDirectories(dir);

// 生成唯一文件名
        // 原始文件名（防止 null）
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }

// 生成唯一文件名（带后缀，保证下载时类型正确）
        String filename = agentId + "_" + System.currentTimeMillis() + ext;

// 存储路径
        Path dest = dir.resolve(filename);
        file.transferTo(dest);

// 相对路径（用来拼接 URL）
        String relative = String.join("/", String.valueOf(userId), datePath, filename).replace("\\", "/");

// 返回给前端的访问 URL
        String url = publicHost.replaceAll("/$", "") + "/" + relative;

// 保存数据库

        KfAttachmentEntity entity = new KfAttachmentEntity();
        entity.setUserId(Long.valueOf(userId));
        entity.setAgentId(Long.valueOf(agentId));
        entity.setFilename(originalName); // 保留原始文件名，前端展示时友好
        entity.setMime(mime);
        entity.setSize(size);
        entity.setUrl(url);
        entity.setKind(mime != null && mime.startsWith("image/") ? "IMAGE" : "FILE");
        entity.setCreateTime(LocalDateTime.now());
        attachmentRepository.insert(entity);

        // HTTP 返回也把 clientMsgId 带回去，便于前端 onUploadSuccess 读取
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "msg", "上传成功",
                "data", entity
        ));
    }

    @PostMapping("/admin")
    public ResponseEntity<?> uploadAdmin(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam("agentId") Long agentId
    ) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "msg", "文件为空"));
        }

        String mime = file.getContentType();
        long size = file.getSize();

// 确保目录存在
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path dir = Paths.get(baseDir, String.valueOf(userId), datePath);
        Files.createDirectories(dir);

// 生成唯一文件名
        // 原始文件名（防止 null）
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }

// 生成唯一文件名（带后缀，保证下载时类型正确）
        String filename = agentId + "_" + System.currentTimeMillis() + ext;

// 存储路径
        Path dest = dir.resolve(filename);
        file.transferTo(dest);

// 相对路径（用来拼接 URL）
        String relative = String.join("/", String.valueOf(userId), datePath, filename).replace("\\", "/");

// 返回给前端的访问 URL
        String url = publicHost.replaceAll("/$", "") + "/" + relative;

// 保存数据库
        KfAttachmentEntity entity = new KfAttachmentEntity();
        entity.setUserId(userId);
        entity.setAgentId(agentId);
        entity.setFilename(originalName); // 保留原始文件名，前端展示时友好
        entity.setMime(mime);
        entity.setSize(size);
        entity.setUrl(url);
        entity.setKind(mime != null && mime.startsWith("image/") ? "IMAGE" : "FILE");
        entity.setCreateTime(LocalDateTime.now());
        attachmentRepository.insert(entity);

        // HTTP 返回也把 clientMsgId 带回去，便于前端 onUploadSuccess 读取
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "msg", "上传成功",
                "data", entity
        ));
    }

}
