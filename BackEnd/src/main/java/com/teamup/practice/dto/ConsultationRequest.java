package com.teamup.practice.dto;

public  class ConsultationRequest {
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