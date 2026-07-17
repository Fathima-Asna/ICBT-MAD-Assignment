package com.printxpress.android.data.model;

import java.util.List;

public class Sample {
    private String id;
    private String name;
    private String designUrl;
    private String userId;
    private String adminId;
    private List<String> productIds;
    private String category;
    private String bleedMargins;
    private String colorFormats;
    private String templateUrl;

    public Sample() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignUrl() {
        return designUrl;
    }

    public void setDesignUrl(String designUrl) {
        this.designUrl = designUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBleedMargins() {
        return bleedMargins;
    }

    public void setBleedMargins(String bleedMargins) {
        this.bleedMargins = bleedMargins;
    }

    public String getColorFormats() {
        return colorFormats;
    }

    public void setColorFormats(String colorFormats) {
        this.colorFormats = colorFormats;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }
}
