package com.sq.system.ws.entity;


import com.sq.system.ws.enums.ReadSide;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "kf_conv_read", uniqueConstraints = {
        @UniqueConstraint(name="uk_conv_side", columnNames = {"conv_id","side"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KfConvRead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="conv_id", nullable = false)
    private Long convId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ReadSide side;

    @Column(name="last_read_msg_id")
    private Long lastReadMsgId;

    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
