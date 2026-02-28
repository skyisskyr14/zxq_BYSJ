package com.sq.system.common.result.dto;

import lombok.Data;

@Data
public class PageRequest {

    private long pageNum = 1;    // 默认页码
    private long pageSize = 10;  // 默认每页数量
}
