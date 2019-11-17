package core;

public class EtsyUrlFormatter {
    private String mainURL;
    private String searchQuery;
    private String category;
    private String freeShipping;
    private String sale;
    private String color;
    private String itemType;
    private String customizable;
    private String giftWrappable;
    private String etsyGiftable;
    private String shipTo;
    private String priceMin;
    private String priceMax;

    public EtsyUrlFormatter(){
        this.mainURL = "https://www.etsy.com/search";
        this.searchQuery = "";
        this.category = "";
        this.freeShipping = "";
        this.sale = "";
        this.color = "";
        this.itemType = "";
        this.customizable = "";
        this.giftWrappable = "";
        this.etsyGiftable = "";
        this.shipTo = "";
        this.priceMin = "";
        this.priceMax = "";
    }

    public String getFormatedUrl(){
        return (  mainURL
                + itemType
                + category
                +"?q="+searchQuery
                + "&explicit=1"
                + color
                + freeShipping
                + sale
                + shipTo
                + customizable
                + giftWrappable
                + etsyGiftable
                + (priceMin.isEmpty()?"":"&min="+priceMin.replace(",","%2C"))
                + (priceMax.isEmpty()?"":"&max="+priceMax.replace(",","%2C"))
        );

    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public String getMainURL() {
        return mainURL;
    }

    public String getCategory() {
        return category;
    }

    public String getFreeShipping() {
        return freeShipping;
    }

    public String getSale() {
        return sale;
    }

    public String getPriceMin() {
        return priceMin;
    }

    public String getPriceMax() {
        return priceMax;
    }

    public String getColor() {
        return color;
    }

    public String getItemType() {
        return itemType;
    }

    public String getCustomizable() {
        return customizable;
    }

    public String getGiftWrappable() {
        return giftWrappable;
    }

    public String getEtsyGiftable() {
        return etsyGiftable;
    }

    public String getShipTo() {
        return shipTo;
    }

    public void setCategory(String category) {
        this.category = category.trim();
    }

    public void setFreeShipping(String freeShipping) {
        this.freeShipping = freeShipping;
    }

    public void setSale(String sale) {
        this.sale = sale;
    }

    public void setPriceMin(String priceMin) {
        this.priceMin = priceMin;
    }

    public void setPriceMax(String priceMax) {
        this.priceMax = priceMax;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public void setCustomizable(String customizable) {
        this.customizable = customizable;
    }

    public void setGiftWrappable(String giftWrappable) {
        this.giftWrappable = giftWrappable;
    }

    public void setEtsyGiftable(String etsyGiftable) {
        this.etsyGiftable = etsyGiftable;
    }

    public void setShipTo(String shipTo) {
        this.shipTo = shipTo;
    }

    public void setSearchQuery(String searchQuery) {
        String trim = searchQuery.trim();
        this.searchQuery = trim.replaceAll(" ", "%20");
    }

}
