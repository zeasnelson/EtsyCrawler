package core;


/**
 * This class is used to format the link when different search filters are applied by the user.
 * It is specifically designed for Etsy.com
 */

public class EtsyUrlFormatter {
    /**
     * To store the host, etsy.com in this case
     */
    private String hostURL;
    /**
     * To store the search key word
     */
    private String searchQuery;

    /**
     * to store the category
     */
    private String category;
    /**
     * free shipping flag
     */
    private String freeShipping;
    /**
     * items on sale flag
     */
    private String sale;
    /**
     * items color flag
     */
    private String color;
    /**
     * item type flag
     */
    private String itemType;
    /**
     * if the item is customizable flag
     */
    private String customizable;
    /**
     * if item can be wrapped flag
     */
    private String giftWrappable;
    /**
     * if item is etsy giftable
     */
    private String etsyGiftable;
    /**
     * ship to country flag
     */
    private String shipTo;

    /**
     * item min price flag
     */
    private String priceMin;

    /**
     * item max price flag
     */
    private String priceMax;

    /**
     * init all fields
     */
    public EtsyUrlFormatter(){
        this.hostURL = "https://www.etsy.com/search";
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

    /**
     * Concatenates all fields of this class to create a final url with all filters specified
     * @return a formatted url
     */
    public String getFormatedUrl(){
        return (  hostURL
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



    //Getters and setters for all fields

    public String getSearchQuery() {
        return searchQuery;
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
        //search queries that have spaces must be separated by %20, Etsy url format rule
        this.searchQuery = trim.replaceAll(" ", "%20");
    }

}
