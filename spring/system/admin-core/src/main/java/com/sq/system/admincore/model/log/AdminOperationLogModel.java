package com.sq.system.admincore.model.log;

import com.sq.system.admincore.entity.log.AdminOperationLogEntity;
import com.sq.system.admincore.repository.log.AdminOperationLogRepository;

public class AdminOperationLogModel {

    private final AdminOperationLogRepository adminOperationLogRepository;

    public AdminOperationLogModel(AdminOperationLogRepository operationLogRepository) {
        this.adminOperationLogRepository = operationLogRepository;
    }

    public void insert(AdminOperationLogEntity log) {
        adminOperationLogRepository.insert(log);
    }
}
