package core;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a single html tag
 * Each tag node can also store a reference to other child tags
 * Provides all basic tag features such as:
 *  - is an opening or closing tag
 *  - is a void tag
 *  - tag name
 *  - tag attributes
 *  - inner html
 *  - reference to the parent tag
 *  - an array list that contains all children of this tag
 *
 *
 * @author Nelson Zeas
 */
public class TagNode {

    /**
     * To store a tag attribute
     */
    private String tagAtt;

    /**
     * To store the inner HTL of a tag
     */
    private  String innerHTML;

    /**
     * To store a tag name
     */
    private String tagName;

    /**
     * To store if a tag is an opening tag or a closing tag
     */
    private Boolean isOpeningTag;

    /**
     * To store a reference to the parent of this tag
     * necessary to build the tree
     */
    private TagNode parentTag;

    /**
     * To store references to all children of this tag
     */
    private  List<TagNode> tagList;

    /**
     * To store if this tag is a void tag or not
     * void tags are those tags that do not use a closing tag
     */
    private Boolean isVoidTag;

    /**
     * initialize all variables to null
     */
    public TagNode(){
        this.tagName = "";
        this.initVariables();
    }

    /**
     * Creates an empty tagNode
     * @param tagName name of the html tag
     */
    public TagNode(String tagName){
        this.tagName   = tagName;
        this.initVariables();
    }

    /**
     * Initialize all variables
     */
    private void initVariables(){
        this.tagList = new ArrayList<>(10);
        this.isOpeningTag = false;
        this.parentTag = null;
        this.parentTag = null;
        this.isVoidTag = false;
        this.innerHTML = "";
    }

    /**
     * Get the inner html of this tag
     * @return the inner html as string
     */
    public String getInnerHTML() {
        return innerHTML;
    }

    /**
     * Set the inner html of this tag
     * @param innerHTML the inner html as a tag
     */
    public void setInnerHTML(String innerHTML) {
        this.innerHTML = innerHTML;
    }

    /**
     * @return true or false
     */
    public Boolean isVoidTag() {
        return isVoidTag;
    }

    /**
     * Set whether this tag is a void tag or not
     * true = void tag
     * false = not a void tag
     * @param voidTag
     */
    public void setVoidTag(Boolean voidTag) {
        isVoidTag = voidTag;
    }

    /**
     * Return the parent of this tag
     * @return the parent as Core.TagNode Object
     */
    public TagNode getParentTag() {
        return parentTag;
    }

    /**
     * Set this tag's parent
     * @param parentTag the parent as Core.TagNode object
     */
    public void setParentTag(TagNode parentTag) {
        this.parentTag = parentTag;
    }

    /**
     * Get this tag's attribute
     * @return
     */
    public String getTagAtt() {
        return tagAtt;
    }

    /**
     * Set this tag's attribute
     * @param tagAtt the attribute as string
     */
    public void setTagAtt(String tagAtt) {
        this.tagAtt = tagAtt;
    }

    /**
     * Sets the tag name
     * @param tagName the tag name
     */
    public void setTagName(String tagName) {
        this.tagName = tagName.trim();
    }

    /**
     * Return whether this is an opening tag
     * @return true if is an opening tag, false otherwise
     */
    public Boolean isOpeningTag() {
        return isOpeningTag;
    }

    /**
     * Set this tag as an opening tag
     * @param openingTag true or false
     */
    public void setOpeningTag(Boolean openingTag) {
        isOpeningTag = openingTag;
    }

    /**
     * @return returns the tag name
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Add a child to this tag
     * @param tagNode the child as a Core.TagNode object
     */
    public void addChild(TagNode tagNode){
        this.tagList.add(tagNode);
    }

    /**
     * return a list of child html tags
     * @return
     */
    public List<TagNode> getChildren() {
        return tagList;
    }

    /**
     * Returns a child tag from the array list
     * @param childIndex index of the child
     * @return the child node located at index
     */
    public TagNode getChild(Integer childIndex){
        if( !tagList.isEmpty() ){
            return tagList.get(childIndex);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Core.TagNode{" +
                " tagName='" + tagName + '\'' +
                ", tagAtt='" + tagAtt + '\'' +
                ", innerHTML='" + innerHTML + '\'' +
                '}';
    }
}
