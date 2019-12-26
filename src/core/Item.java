package core;

/**
 * Class to represent a single item extracted from Etsy.com
 *
 */
public class Item implements Comparable<Item> {
    /**
     * To store this item's description
     */
    private String description;

    /**
     * To store this item's category
     */
    private String category;

    /**
     * To store this item's price
     */
    private String price;

    /**
     * To store this item's image source
     */
    private String imgScr;

    /**
     * To store this item's image id
     */
    private Integer imgId;

    /**
     * To store this item's time stamp
     */
    private String timeStamp;

    /**
     * To store user id of the user that searched for this item
     */
    private String userId;

    /**
     * To store the search query that generated this item
     */
    private String searchQuery;

    /**
     * Initialize all variables
     * @param description This item's description
     * @param category This item's category
     * @param price This item's price
     * @param imgScr This item's image source
     * @param timeStamp This item's timestamp
     */
    public Item(String description, String category, String price, String imgScr, String timeStamp) {
        this.description = description;
        this.timeStamp = timeStamp;
        this.category = category;
        this.imgScr = imgScr;
        this.price = price;
        this.imgId = null;
    }

    /**
     * Override the compare method to be able to add or delete from array lists
     * @param otherItem The other item to which this item will be compared to
     * @return
     */
    @Override
    public int compareTo(Item otherItem) {
        Double price = Double.parseDouble(this.getPrice().trim().replaceAll(",", ""));
        Double otherPrice = Double.parseDouble(otherItem.getPrice().trim().replaceAll(",", ""));
        return price.compareTo(otherPrice);
    }

    /**
     * Check if this item is equal to another item of the same type
     * @param other
     * @return
     */
    public boolean equals(Object other){

        // If the object is compared with itself then return true
        if( this == other ){
            return true;
        }

        /* Check if other is an instance of Item or not*/
        if (!(other instanceof Item)) {
            return false;
        }

        //Compare objects
        Item otherItem = (Item)other;
        return (   this.description.equals(otherItem.description)
                && this.category.equals(otherItem.category)
                && this.price.equals(otherItem.price));
    }

    @Override
    public String toString() {
        return "Core.Item{" +
                " userId='" + userId + '\'' +
                " searchQuery='" + searchQuery + '\'' +
                " description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", price='" + price + '\'' +
                ", imgScr='" + imgScr + '\'' +
                ", imgId='" + imgId + '\'' +
                '}';
    }


    //getters and setters
    public String getUserId() {
        return userId;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public String getImgScr() {
        return imgScr;
    }

    public void setImgScr(String imgScr) {
        this.imgScr = imgScr;
    }

    public String getDescription() {
        return description;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getCategory() {
        return category;
    }

    public String getPrice() {
        return price;
    }

    public Integer getImgId() {
        return imgId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImgId(Integer imgId) {
        this.imgId = imgId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }



}
