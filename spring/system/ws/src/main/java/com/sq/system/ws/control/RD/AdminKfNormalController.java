package com.sq.system.ws.control.RD;

import com.sq.system.common.result.ResponseResult;
import com.sq.system.ws.entity.KfNormalQEntity;
import com.sq.system.ws.repository.KfNormalQRepository;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/kf/RD/normal")
public class AdminKfNormalController {
    @Resource
    private KfNormalQRepository kfNormalQRepository;

    @GetMapping("/create")
    public ResponseResult<?> create(@RequestParam Integer type,
                                  @RequestParam Integer agentId,
                                  @RequestParam String question,
                                  @RequestParam String result,
                                    @RequestParam(required = false) Long id,
                                    @RequestParam(required = false) Integer status) {


        if(type == null || type <= 0) {
            ResponseResult.fail("未知类型");
        }
        if (agentId == null || agentId <= 0) {
            ResponseResult.fail("未知客服");
        }

        if(id == null || id <= 0) {
            KfNormalQEntity kfNormalQ = new KfNormalQEntity();
            kfNormalQ.setType(type);
            kfNormalQ.setQuestion(question);
            kfNormalQ.setResult(result);
            kfNormalQ.setUserId(agentId);
            kfNormalQ.setStatus(status);
            kfNormalQ.setCreateTime(LocalDateTime.now());
            kfNormalQ.setUpdateTime(LocalDateTime.now());
            kfNormalQRepository.insert(kfNormalQ);
            return ResponseResult.success("创建成功");
        } else {
            KfNormalQEntity kfNormalQ = kfNormalQRepository.selectById(id);
            kfNormalQ.setType(type);
            kfNormalQ.setQuestion(question);
            kfNormalQ.setResult(result);
            kfNormalQ.setUserId(agentId);
            kfNormalQ.setStatus(status);
            kfNormalQ.setCreateTime(LocalDateTime.now());
            kfNormalQ.setUpdateTime(LocalDateTime.now());
            kfNormalQRepository.updateById(kfNormalQ);
            return ResponseResult.success("修改成功");
        }
    }

    @GetMapping("/delete")
    public ResponseResult<?> delete(@RequestParam Long id) {


        if(id == null || id <= 0) {
            ResponseResult.fail("未知");
        }
        kfNormalQRepository.deleteById(id);
        return ResponseResult.success("修改成功");
    }
}
