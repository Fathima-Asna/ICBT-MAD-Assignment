package com.printxpress.android.data.model;

public class OrderItemRequest {
    private String productId;
    private Integer quantity;
    private String designUrl;
    private String customText;

    public OrderItemRequest() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDesignUrl() {
        return designUrl;
    }

    public void setDesignUrl(String designUrl) {
        this.designUrl = designUrl;
    }

    public String getCustomText() {
        return customText;
    }

    public void setCustomText(String customText) {
        this.customText = customText;
    }
}
