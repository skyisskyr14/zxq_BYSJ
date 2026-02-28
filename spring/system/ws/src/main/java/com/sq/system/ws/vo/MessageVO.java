package com.sq.system.ws.vo;

import com.sq.system.ws.enums.MsgSide;
import com.sq.system.ws.enums.MsgType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageVO {
    private Long id;

    private Integer conversationType; // 0/1
    private Long groupId;
    private Long convId;

    private Long serverMsgId;
    private String clientMsgId;

    private MsgSide fromSide;         // USER/AGENT
    private MsgType msgType;          // TEXT/IMAGE/FILE...

    private String text;
    private String payloadJson;

    private Long quoteMsgId;
    private Boolean revoked;

    private LocalDateTime createdAt;

}

