package com.teamup.practice.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 简化版：直接使用API Key调用千问API
 * 无需复杂签名，更简单可靠
 */
public class CarConsultationService {

    // DashScope API配置 - 使用API Key认证
    private static final String DASHSCOPE_API_KEY = "sk-71c01cdd82344618b30311351cc71dbf";
    private static final String DASHSCOPE_API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
    private static final String MODEL_NAME = "qwen-max";

    // 内存存储结构
    private final Map<Long, List<ConsultationRecord>> userConsultations = new ConcurrentHashMap<>();
    private final Map<Long, ConsultationRequest> pendingRequests = new ConcurrentHashMap<>();
    private long nextRequestId = 1;

    // HTTP客户端
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(15))
            .build();

    // 线程池
    private final ExecutorService apiExecutor = Executors.newFixedThreadPool(5);

    /**
     * 提交购车咨询请求（异步处理）
     */
    public long submitConsultation(long userId, ConsultationRequest request) {
        validateRequest(request);

        long requestId = nextRequestId++;
        pendingRequests.put(requestId, request);

        CompletableFuture.supplyAsync(() -> {
            try {
                return generateLLMAdviceFromQwen(request);
            } catch (Exception e) {
                System.err.println("千问API调用失败: " + e.getMessage());
                e.printStackTrace();
                return "⚠️ 智能咨询系统暂时不可用，请稍后再试。\n错误详情: " + e.getMessage();
            }
        }, apiExecutor).thenAccept(advice -> {
            ConsultationRecord record = new ConsultationRecord(
                    requestId,
                    userId,
                    request,
                    advice,
                    LocalDateTime.now()
            );

            userConsultations.computeIfAbsent(userId, k -> new ArrayList<>())
                    .add(record);

            pendingRequests.remove(requestId);
        });

        return requestId;
    }

    /**
     * 使用API Key直接调用千问API
     */
    private String generateLLMAdviceFromQwen(ConsultationRequest request) throws Exception {
        String prompt = buildProfessionalPrompt(request);
        String requestBody = buildApiRequest(prompt);

        // 创建带API Key认证的请求
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(DASHSCOPE_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + DASHSCOPE_API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // 发送请求
        System.out.println("正在发送API请求到: " + DASHSCOPE_API_URL);
        System.out.println("请求体: " + requestBody);

        HttpResponse<String> response = httpClient.send(
                httpRequest,
                HttpResponse.BodyHandlers.ofString()
        );

        // 详细记录响应
        System.out.println("API响应状态: " + response.statusCode());
        System.out.println("API响应体: " + response.body());

        // 处理响应
        if (response.statusCode() != 200) {
            throw new RuntimeException("API调用失败: " + response.statusCode() +
                    ", 响应: " + response.body());
        }

        JSONObject jsonResponse = JSON.parseObject(response.body());

        // 检查是否有错误
        if (jsonResponse.containsKey("code")) {
            String code = jsonResponse.getString("code");
            String message = jsonResponse.getString("message");
            throw new RuntimeException("API错误 [" + code + "]: " + message);
        }

        // 解析响应 - 修正为正确的字段路径
        JSONObject output = jsonResponse.getJSONObject("output");
        if (output == null) {
            throw new RuntimeException("API响应缺少output字段: " + response.body());
        }

        JSONArray choices = output.getJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("API响应缺少choices数据: " + response.body());
        }

        JSONObject firstChoice = choices.getJSONObject(0);
        JSONObject message = firstChoice.getJSONObject("message");
        if (message == null) {
            throw new RuntimeException("API响应缺少message字段: " + response.body());
        }

        String content = message.getString("content");
        if (content == null || content.isEmpty()) {
            throw new RuntimeException("API响应缺少content内容: " + response.body());
        }

        return formatProfessionalAdvice(content);
    }

    /**
     * 构建API请求体 - 修正为DashScope API要求的格式
     */
    private String buildApiRequest(String prompt) {
        JSONObject requestObj = new JSONObject();
        requestObj.put("model", MODEL_NAME);

        // DashScope API要求的messages格式
        JSONArray messages = new JSONArray();

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", prompt);

        messages.add(userMsg);

        // 正确的请求结构
        JSONObject input = new JSONObject();
        input.put("messages", messages);

        requestObj.put("input", input);
        requestObj.put("parameters", buildParameters()); // 添加参数

        return requestObj.toJSONString();
    }

    /**
     * 构建模型参数
     */
    private JSONObject buildParameters() {
        JSONObject params = new JSONObject();
        params.put("temperature", 0.6); // 控制生成文本的随机性
        params.put("top_p", 0.8);        // 核采样参数
        params.put("max_tokens", 2048);  // 最大生成长度
        params.put("result_format", "message"); // 返回格式
        return params;
    }

    /**
     * 构建专业提示词
     */
    private String buildProfessionalPrompt(ConsultationRequest request) {
        return "你是一位拥有10年经验的汽车销售总监，专注于为客户提供专业的购车决策支持。请根据以下用户需求提供详细、专业的购车建议：\n\n" +
                "【用户需求】\n" +
                "- 预算范围：¥" + request.getBudgetMin() + "万 - ¥" + request.getBudgetMax() + "万\n" +
                "- 偏好车型：" + request.getPreferredVehicleType() + "\n" +
                "- 主要使用场景：" + request.getUsageScenario() + "\n" +
                (request.getFuelPreference() != null ? "- 燃料类型偏好：" + request.getFuelPreference() + "\n" : "") +
                (request.getBrandPreference() != null ? "- 品牌偏好：" + request.getBrandPreference() + "\n" : "") +
                "\n" +
                "【输出要求】\n" +
                "1. 推荐3款最符合需求的车型（按优先级排序），包含：\n" +
                "   - 车型全称与年款\n" +
                "   - 指导价（精确到小数点后1位）\n" +
                "   - 核心优势（结合用户场景）\n" +
                "   - 潜在不足（客观说明）\n" +
                "2. 提供专业的预算规划建议：\n" +
                "   - 首付比例建议（20%-40%）\n" +
                "   - 月供计算示例（36期）\n" +
                "   - 保险/税费/养车成本估算\n" +
                "3. 行业洞察：\n" +
                "   - 当前市场优惠信息\n" +
                "   - 未来3个月价格走势预测\n" +
                "   - 同价位车型对比分析\n" +
                "4. 专业提示：\n" +
                "   - 试驾重点检查项\n" +
                "   - 购车合同注意事项\n" +
                "   - 金融方案选择建议\n" +
                "\n" +
                "【注意事项】\n" +
                "- 建议必须具体、可操作，避免笼统描述\n" +
                "- 价格数据需符合2023-2024年市场行情\n" +
                "- 对比分析需包含3个以上维度\n" +
                "- 使用专业但易懂的语言，避免过度营销话术\n" +
                "- 严格遵守中国《汽车销售管理办法》";
    }

    /**
     * 格式化专业建议
     */
    private String formatProfessionalAdvice(String rawAdvice) {
        return "【AI购车顾问 | 专业版 v2.1】\n" +
                "※ 基于Qwen-Max大模型生成，数据更新至2024年7月\n" +
                "※ 本建议仅供参考，最终决策请结合实车体验\n" +
                "※ 市场价格可能波动，建议到店确认最新优惠\n\n" +
                rawAdvice +
                "\n\n【免责声明】\n" +
                "本建议由AI生成，不构成任何购车承诺。汽车价格受地区、配置、库存等因素影响，" +
                "实际价格以经销商报价为准。建议在购车前进行实车体验并与专业销售顾问沟通。";
    }

    /**
     * 请求参数验证
     */
    private void validateRequest(ConsultationRequest request) {
        if (request.getBudgetMin() <= 0 || request.getBudgetMax() <= 0) {
            throw new IllegalArgumentException("预算范围必须大于0");
        }
        if (request.getBudgetMin() > request.getBudgetMax()) {
            throw new IllegalArgumentException("最低预算不能高于最高预算");
        }
        if (request.getPreferredVehicleType() == null || request.getPreferredVehicleType().isEmpty()) {
            throw new IllegalArgumentException("必须选择偏好车型");
        }
        if (request.getUsageScenario() == null || request.getUsageScenario().isEmpty()) {
            throw new IllegalArgumentException("必须指定使用场景");
        }
    }

    /**
     * 获取用户咨询历史记录
     */
    public List<ConsultationRecord> getConsultationHistory(long userId) {
        return Optional.ofNullable(userConsultations.get(userId))
                .map(list -> list.stream()
                        .sorted(Comparator.comparing(ConsultationRecord::getConsultationTime).reversed())
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    /**
     * 获取单条咨询记录详情
     */
    public ConsultationRecord getConsultationDetail(long userId, long requestId) {
        return getConsultationHistory(userId).stream()
                .filter(record -> record.getRequestId() == requestId)
                .findFirst()
                .orElse(null);
    }

    // ============== 数据模型 ==============

    /**
     * 购车咨询请求数据结构
     */
    public static class ConsultationRequest {
        private double budgetMin;
        private double budgetMax;
        private String preferredVehicleType;
        private String usageScenario;
        private String fuelPreference;
        private String brandPreference;

        // 构造方法
        public ConsultationRequest(double budgetMin, double budgetMax,
                                   String preferredVehicleType, String usageScenario) {
            this.budgetMin = budgetMin;
            this.budgetMax = budgetMax;
            this.preferredVehicleType = preferredVehicleType;
            this.usageScenario = usageScenario;
        }

        // Getters
        public double getBudgetMin() { return budgetMin; }
        public double getBudgetMax() { return budgetMax; }
        public String getBudgetRange() {
            return String.format("%.1f-%.1f万", budgetMin, budgetMax);
        }
        public String getPreferredVehicleType() { return preferredVehicleType; }
        public String getUsageScenario() { return usageScenario; }
        public String getFuelPreference() { return fuelPreference; }
        public String getBrandPreference() { return brandPreference; }

        // Setters
        public void setFuelPreference(String fuelPreference) { this.fuelPreference = fuelPreference; }
        public void setBrandPreference(String brandPreference) { this.brandPreference = brandPreference; }
    }

    /**
     * 咨询记录数据结构
     */
    public static class ConsultationRecord {
        private final long requestId;
        private final long userId;
        private final ConsultationRequest request;
        private final String advice;
        private final LocalDateTime consultationTime;

        public ConsultationRecord(long requestId, long userId, ConsultationRequest request,
                                  String advice, LocalDateTime consultationTime) {
            this.requestId = requestId;
            this.userId = userId;
            this.request = request;
            this.advice = advice;
            this.consultationTime = consultationTime;
        }

        // Getters
        public long getRequestId() { return requestId; }
        public long getUserId() { return userId; }
        public ConsultationRequest getRequest() { return request; }
        public String getAdvice() { return advice; }
        public LocalDateTime getConsultationTime() { return consultationTime; }
    }

    // ============== 使用示例 ==============
    public static void main(String[] args) {
        CarConsultationService service = new CarConsultationService();

        // 模拟用户ID
        long userId = 1001;

        // 创建咨询请求
        ConsultationRequest request = new ConsultationRequest(18.0, 25.0, "SUV", "家庭");
        request.setFuelPreference("混动");
        request.setBrandPreference("国产");

        System.out.println("🚗 正在提交购车咨询请求...");
        System.out.println("预算: ¥" + request.getBudgetMin() + "万 - ¥" + request.getBudgetMax() + "万");
        System.out.println("需求: " + request.getPreferredVehicleType() + " | " +
                request.getUsageScenario() + " | " +
                request.getFuelPreference() + " | " +
                request.getBrandPreference());

        // 提交咨询（异步）
        long requestId = service.submitConsultation(userId, request);
        System.out.println("\n⏳ 咨询请求已提交，正在等待AI生成建议...");
        System.out.println("记录ID: " + requestId);

        // 等待API响应
        try {
            System.out.print("正在处理");
            for (int i = 0; i < 300; i++) {
                Thread.sleep(1000);
                System.out.print(".");
            }
            System.out.println();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 查看历史记录
        List<ConsultationRecord> history = service.getConsultationHistory(userId);
        if (!history.isEmpty()) {
            ConsultationRecord record = history.get(0);
            System.out.println("\n" + "=".repeat(60));
            System.out.println("✅ 咨询结果 (生成时间: " + record.getConsultationTime() + ")");
            System.out.println("=".repeat(60));
            System.out.println(record.getAdvice());
            System.out.println("=".repeat(60));
        } else {
            System.out.println("\n⚠️ 未获取到咨询结果，请检查API配置或网络连接");
        }
    }
}
