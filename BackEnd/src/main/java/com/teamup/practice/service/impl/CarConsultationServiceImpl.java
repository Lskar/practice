package com.teamup.practice.service.impl;

import com.teamup.practice.dto.ConsultationRequest;
import com.teamup.practice.service.CarConsultationService;
import com.teamup.practice.utils.CarConsultationUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarConsultationServiceImpl implements CarConsultationService {
    @Override
    public String consultation(ConsultationRequest request) {
        // 创建 CarConsultationUtils 实例
        CarConsultationUtils utils = new CarConsultationUtils();

        // 模拟用户ID（实际应用中应该从安全上下文获取）
        long userId = 1L;

        // 提交咨询请求并获取请求ID（异步处理）
        long requestId = utils.submitConsultation(userId, request);

        // 等待API响应完成（模拟最长等待约5分钟）
        try {
            // 循环等待，直到获取到结果或者超时
            long startTime = System.currentTimeMillis();
            long timeout = 300000; // 5分钟超时
            CarConsultationUtils.ConsultationRecord record = null;

            while (System.currentTimeMillis() - startTime < timeout) {
                Thread.sleep(1000);

                // 查询用户的咨询历史，查找对应记录
                List<CarConsultationUtils.ConsultationRecord> history = utils.getConsultationHistory(userId);
                record = history.stream()
                        .filter(r -> r.getRequestId() == requestId)
                        .findFirst()
                        .orElse(null);

                if (record != null) {
                    break;
                }
            }

            // 如果找到了记录，返回咨询建议
            if (record != null) {
                return record.getAdvice();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 如果未获取到咨询结果，返回默认提示
        return "⚠️ 未获取到咨询结果，请稍后再试";
    }

}
