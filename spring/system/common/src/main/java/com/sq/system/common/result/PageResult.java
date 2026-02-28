package com.sq.system.common.result;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private List<T> list;
    private long total;
    private long pageNum;
    private long pageSize;

    public PageResult(List<T> list, long total, long pageNum, long pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public static <T> PageResult<T> of(com.baomidou.mybatisplus.core.metadata.IPage<T> page) {
        return new PageResult<>(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
    }

    public static <T> PageResult<T> of(com.baomidou.mybatisplus.core.metadata.IPage<?> page, List<T> voList) {
        return new PageResult<>(
                voList,
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
    }

}
