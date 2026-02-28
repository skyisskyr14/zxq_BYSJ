package com.sq.system.admincore.model.log;

import com.sq.system.admincore.entity.log.FloodBlockLogEntity;
import com.sq.system.admincore.repository.log.FloodBlockLogRepository;
import jakarta.annotation.Resource;


public class FloodBlockLogModel {

    @Resource
    private FloodBlockLogRepository floodBlockLogRepository;

    public void insert(FloodBlockLogEntity log) {
        floodBlockLogRepository.insert(log);
    }
}
