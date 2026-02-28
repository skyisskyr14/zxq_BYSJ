package com.sq.system.ws.entity;

import com.sq.system.ws.enums.MsgSide;
import com.sq.system.ws.enums.MsgType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "kf_message", indexes = {
        @Index(name="idx_msg_conv_paging", columnList = "conv_id,id"),
        @Index(name="idx_msg_client", columnList = "client_msg_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KfMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="conv_id", nullable = false)
    private Long convId;

    @Column(name="server_msg_id")
    private Long serverMsgId;

    @Column(name="client_msg_id", length = 64)
    private String clientMsgId;

    @Enumerated(EnumType.STRING)
    @Column(name="from_side", nullable = false, length = 64)
    private MsgSide fromSide;

    @Enumerated(EnumType.STRING)
    @Column(name="msg_type", nullable = false, length = 64)
    private MsgType msgType;

    @Lob
    private String text;                  // 文本

    @Column(name="payload_json", columnDefinition = "json")
    private String payloadJson;           // 图片/文件元数据（JSON 字符串存）

    @Column(name="quote_msg_id")
    private Long quoteMsgId;

    @Column(nullable = false)
    private Boolean revoked;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;
}
