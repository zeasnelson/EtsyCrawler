package core;

public class Item implements Comparable<Item> {
    private String description;
    private String category;
    private String price;
    private String imgScr;
    private Integer imgId;
    private String timeStamp;

    public Item(String description, String category, String price, String imgScr, String timeStamp) {
        this.description = description;
        this.timeStamp = timeStamp;
        this.category = category;
        this.imgScr = imgScr;
        this.price = price;
        this.imgId = null;
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

    @Override
    public String toString() {
        return "Core.Item{" +
                " description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", price='" + price + '\'' +
                ", imgScr='" + imgScr + '\'' +
                '}';
    }

    @Override
    public int compareTo(Item otherItem) {
        Double price = Double.parseDouble(this.getPrice().trim().replaceAll(",", ""));
        Double otherPrice = Double.parseDouble(otherItem.getPrice().trim().replaceAll(",", ""));
        return price.compareTo(otherPrice);
    }
}
