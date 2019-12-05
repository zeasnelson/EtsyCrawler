package core;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;


/**
 * This class parses a properly formatted html file.
 * The html file is read character by character as a stream by using the Core.FileInput class.
 * As the file is read, the parser extracts tags, tags's attributes and inner html.
 * Every time a new tag is extracted, it is added to a tree which in the end,
 * represents all the html read in tree form.
 * This tree is easily traversed to get all the items. Each item has a description, category and prize.
 *
 * Tags, tags' attributes and inner html are extracted by using a DFA technique. Each state is simulated by if statements
 * to redirect program flow according to data being read
 *
 * @author Nelson Zeas
 */
public class HMTLParser {

    /**
     * To read the file to be parsed
     */
    private FileInput reader;

    /**
     * Root node, to represent the opening HTML parent tag
     */
    private TagNode root;

    /**
     * To store the current parsed tag name
     */
    private String tagName;

    /**
     * Contain the current character ASCII code read from the file reader
     */
    private int charCode;

    /**
     * Set to ignore specific tags, ignored tags are not added to the tree
     */
    private Set<String> ignoredTags;

    /**
     * Set to store all html special void tags
     */
    private Set<String> voidTags;


    /**
     * Initialize all variables
     * @param inputFileName The name of the file to be parsed
     */
    public HMTLParser(String inputFileName) {
        reader = new FileInput(inputFileName);
        root = new TagNode();
        initIgnoredTagsSet();
        initVoidTags();
    }


    /**
     * There are two syntax for HTML void tags:
     *  - <img />
     *  - <img >
     *  Results received from Etsy use both ways and is more difficult to detect if a tag is void or not.
     *  All these void tags are added to a map, to easily detect if it is a void tag, there aren't too many.
     */
    private void initVoidTags(){
        voidTags = new HashSet<>();
        voidTags.add("input");
        voidTags.add("img");
        voidTags.add("area");
        voidTags.add("base");
        voidTags.add("br");
        voidTags.add("col");
        voidTags.add("hr");
        voidTags.add("link");
        voidTags.add("meta");
        voidTags.add("param");
        voidTags.add("command");
        voidTags.add("keygen");
        voidTags.add("source");
        voidTags.add("path");
    }


    /**
     * A HashSet to store all the tags that will be ignored when parsing the HTML file.
     * If a tag is ignored, all its children are also ignored.
     * This process saves memory because all these tags will not be added to the tree.
     * Ignored tags do not contain relevant data for this project
     */
    private void initIgnoredTagsSet(){
        ignoredTags = new HashSet<>();
        ignoredTags.add("head");
        ignoredTags.add("noscript");
        ignoredTags.add("script");
        ignoredTags.add("footer");
        ignoredTags.add("option");
        ignoredTags.add("button");
        ignoredTags.add("input");
        ignoredTags.add("path");
        ignoredTags.add("label");
        ignoredTags.add("svg");
    }


    /**
     * Build the tree.
     */
    public void parse(){
        //newNode saves the new tag to be added to the tree
        TagNode newNode, parent;
        //Parent contains a reference to the last added tag
        root = parent = null;

        while ( charCode != -1 ){

            newNode = getNextNode();
            //don't add ignored tags to the tree
            //discard those tags = read file  but not add
            if( newNode != null && ignoredTags.contains(newNode.getTagName()) && newNode.isOpeningTag() ){
                if( !newNode.isVoidTag() )
                    discardTags(newNode.getTagName());
            }
            //when the tree is first built, the root tag will be null so the first node has to be added as the root
            else if( newNode != null ){
                if ( parent == null ){
                    root = newNode;
                    parent = newNode;
                }
                //adds the newNode to the tree
                else if( newNode.isOpeningTag() ){
                    newNode.setParentTag(parent);
                    parent.addChild(newNode);
                    //void tags cannot have children, so parent does not have to point to new node
                    if( !newNode.isVoidTag() ){
                        parent = newNode;
                    }
                }
                //if it is a closing tag, then return to the previous node in the tree.
                else if( !newNode.isOpeningTag() ){
                        parent = parent.getParentTag();
                }
            }
        }
    }


    /**
     * Get the next node from the tree
     * @return A TagNode with tag name and attributes if any
     */
    public TagNode getNextNode(){
        TagNode node = null;
        //A node always starts with <
        if( charCode != '<' )
            charCode = reader.getNextNonWhiteSpace();
        if( charCode == '<' ){
            node = parseTag();
        }
        //if the next character after the tag name has been read is not <
        //It means the tag has a value such as <a> hello </a>.
        //Here after <a> is parsed, the next character is ' ' space. So it means it has a value
        charCode = reader.getNextNonWhiteSpace();
        if( charCode != '<' ){
            if( node != null ) {
                String innerHTML = getTagInnerHTML();
                node.setInnerHTML(innerHTML);
            }
        }
        return node;
    }


    /**
     * Extract a closing tag.
     * A closing tag aways ends with </[tagName]> assuming the document contains valid HTML
     * @return The closing tag' name
     */
    private TagNode getClosingTag(){
        tagName = "";
        if( charCode == '/' ){
            charCode = reader.getNextChar();
            while (charCode != -1  && charCode != '>'){
                tagName += (char)charCode;
                charCode = reader.getNextChar();
            }
            if( charCode == '>' ){
                TagNode newTagNode = new TagNode(tagName);
                newTagNode.setOpeningTag(false);
                return newTagNode;
            }
        }
        return null;
    }


    /**
     * Extract an opening tag.
     * <[tagName] [attributes]>.
     * To extract an opening tag name, read chars or letters until ' ' space if found or > is found
     * If space if found, tag has attributes. If > is found, tag does not have attributes
     * @return TagNode with the tag name and attributes if any
     */
    private TagNode getOpeningTag(){
        tagName = "";
        if( Character.isLetter(charCode) ){
            tagName += (char)charCode;
            charCode = reader.getNextChar();
            while (charCode != -1 && Character.isLetterOrDigit(charCode) && charCode != '>'){
                if( (char)charCode != '/' )
                    tagName += (char)charCode;
                charCode = reader.getNextChar();
            }

            //create new Core.TagNode with the tag name
            //check if it is a void tag
            Boolean isVoidTag = voidTags.contains(tagName.trim());
            TagNode newTagNode = new TagNode(tagName);
            newTagNode.setVoidTag(isVoidTag);
            newTagNode.setOpeningTag(true);

            //if there is no '>' after tag name, then those are attributes
            //always extract tag class name and the source url for images
            //tag ids have higher priority thant class names.
            // That is, if an id is found first, the class name is ignored.
            //Saving these attributes is useful to search for the data, and to download images later
            if( charCode != '>' ){
                String att = null;
                if( tagName.equals("img") ){
                    att = getImgSrc();
                } else{
                    att = getTagIDorClass();
                }
                newTagNode.setTagAtt( att == null ? "" : att );
            }
            return  newTagNode;
        }
        return null;
    }


    /**
     * Reads a file until the end of a comment is  found.
     * Comments are not useful, therefore not saved
     * HTML comments syntax <!--[comment] -->
     * @return True if a comment was discarded, false otherwise
     */
    private Boolean discardComment(){
        tagName = "";
        if( charCode == '!'){
            while ( !tagName.endsWith("-->") && !tagName.endsWith("html>") && charCode != -1 ){
                charCode = reader.getNextChar();
                tagName += (char)charCode;
            }
        }
        if( tagName.endsWith("-->") || tagName.endsWith("html>") )
            return true;
        return false;
    }


    /**
     * Method to ignore all specified tags, including all children
     * These tags were  specified in the ignoredTags HashSet
     * @param tagName The name of the tag to be ignore
     */
    private void discardTags(String tagName){
        //stack necessary to detect if a tag has children of the same type
        Stack<String> s = new Stack<>();
        s.push(tagName);

        TagNode discardedNode;
        while ( !s.isEmpty() && charCode != -1 ){
            discardedNode = getNextNode();
            if( discardedNode != null ) {
                String newTagName = discardedNode.getTagName();
                if( newTagName.equals(tagName) && discardedNode.isOpeningTag() ){
                    s.push(newTagName);
                }
                else if( newTagName.equals(tagName) && !discardedNode.isOpeningTag() ){
                    s.pop();
                }
            }
        }
    }


    /**
     * Method to extract the innerHTML from a tag
     * <a> Hello </a> Here "Hello" is the innerHTML
     * @return the innerHTML
     */
    private String getTagInnerHTML(){
        String innerHTML = "" ;
        while (charCode != -1 && charCode != '<' ){
            //avoid saving whitespaces, it will make txt look unformatted and messy
            if( Character.isWhitespace(charCode) ) {
                innerHTML += " ";
                charCode = reader.getNextNonWhiteSpace();
            }
            else {
                innerHTML += (char) charCode;
                charCode = reader.getNextChar();
            }
        }
        return innerHTML;
    }


    /**
     * Method to redirect parsing to either extract a new opening tag, closing tag or discard a comment
     * @return a tag name
     */
    private TagNode parseTag(){
        charCode = reader.getNextChar();
        if( charCode == '!' ) {
            discardComment();
            return null;
        }
        else if( charCode == '/') {
            return getClosingTag();
        }
        else if( Character.isLetter(charCode) ){
            return getOpeningTag();
        }
        return null;
    }


    /**
     * Extracts the src from an img tag.
     * This method is implemented as a DFA. The DFA states are simulated by the nested if statements
     * @return the source url or null if none found
     */
    private String getImgSrc(){
        String src = "";
        Boolean found = false;
        while ( charCode != -1 && charCode != '>' ){
            if( !found ) {
                if (charCode == 's') {
                    charCode = reader.getNextChar();
                    if (charCode == 'r') {
                        charCode = reader.getNextChar();
                        if (charCode == 'c') {
                            charCode = reader.getNextChar();
                            if (charCode == '=') {
                                charCode = reader.getNextChar();
                                if (charCode == '\'' || charCode == '\"') {
                                    charCode = reader.getNextNonWhiteSpace();
                                    while (charCode != -1 && charCode != '\'' && charCode != '\"' ) {
                                        src += (char) charCode;
                                        found = true;
                                        charCode = reader.getNextChar();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            charCode = reader.getNextChar();
        }
        return src.trim();
    }


    /**
     * Extracts either the tag id or the class name.
     * If the id is found first, then the id is saved,
     * if the class is found first, then the class is saved
     * @return The tag id or class name if available, null otherwise
     */
    private String getTagIDorClass(){
        String att = null;
        while ( charCode != -1 && charCode != '>' ){
            if( att == null ) {
                //disable extracting id, not necessary
//                if (charCode == 'i') {
//                    att = getTagId();
//                }
                if( charCode == 'c' ){
                    att = getTagClassName();
                }
            }
            charCode = reader.getNextChar();
        }
        return att;
    }


    /**
     * Extract the tag class name
     * This method is implemented as a DFA. The DFA states are simulated by the nested if statements
     * @return The class name or null if not found
     */
    private String getTagClassName(){
        String className = "";
        charCode = reader.getNextChar();
        if( charCode == 'l' ){
            charCode = reader.getNextChar();
            if( charCode == 'a' ){
                charCode = reader.getNextChar();
                if( charCode == 's' ){
                    charCode = reader.getNextChar();
                    if( charCode == 's' ){
                        charCode = reader.getNextChar();
                        if( charCode == '=' ){
                            charCode = reader.getNextChar();
                            if( charCode == '\"' || charCode == '\'' ){
                                charCode = reader.getNextNonWhiteSpace();
                                while (charCode != -1 && charCode != ' ' && charCode != '\'' && charCode != '\"' ){
                                    className += (char)charCode;
                                    charCode = reader.getNextChar();
                                }
                                return className.trim();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }


    /**
     * Extract the tag id
     * This method is implemented as a DFA. The DFA states are simulated by the nested if statements
     * @return The id name or null if not found
     */
    private String getTagId(){
        String id = "";
        charCode = reader.getNextChar();
        if( charCode == 'd' ){
            charCode = reader.getNextChar();
            if( charCode == '=' ){
                charCode = reader.getNextChar();
                if( charCode == '\'' || charCode == '\"' ){
                    charCode = reader.getNextNonWhiteSpace();
                    while (charCode != -1 && charCode != ' ' && charCode != '\'' && charCode != '\"' && charCode != '>'){
                        id += (char)charCode;
                        charCode = reader.getNextChar();
                    }
                    return id.trim();
                }
                else return null;
            }
            return null;
        }
        return null;
    }


    /**
     * HELPER method to print tree in html form, that is:
     * html
     *      head
     *          title
     *      body
     *          div
     * Note: it does not print closing tags
     */
    public void printTreeIndented(){
        printTreeIndented(root, 0);
    }


    /**
     * method to print tree in html form, that is:
     * html
     *      head
     *          title
     *      body
     *          div
     * Note: it does not print closing tags
     */
    private void printTreeIndented(TagNode root, int i){
        if( root != null ){
            printLine(i);
            System.out.println(root.getTagName() + " " + root.getTagAtt() + " " + root.getInnerHTML());
            for( int n = 0; n < root.getChildren().size(); n++ ){
                printTreeIndented(root.getChild(n), i+1);
            }
        }
    }


    /**
     * Helper method to print an empty line and help indent the html tree
     * @param size size of the empty line
     */
    public void printLine(int size){
        for( int i = 0; i < size; i++){
            System.out.print("   ");
        }
    }



    /**
     * All the data that needs to be extracted is inside an
     * ul (unordered list) with class name "responsive-listing-grid"
     * This unordered list has 64 children tags, which are individual results that will be extracted
     * The tree is traversed by Breadth First Search
     * @return A unordered list Core.TagNode
     */
    private TagNode getAllItems(String tagName, String tagAttribute){
        if( root == null )
            return null;
        Queue<TagNode> q = new LinkedList<>();
        q.add(root);
        while( !q.isEmpty() ){
            int n = q.size();
            while( n > 0 ){
                TagNode node = q.remove();
                if( node!=null && node.getTagName() != null && node.getTagAtt() != null ){
                    if( node.getTagName().equals(tagName) && node.getTagAtt().equals(tagAttribute) )
                        return node;
                }
                for (TagNode i : node.getChildren())
                    q.add(i);
                n--;
            }
        }
        return null;
    }

    /**
     * Helper function to extract the information from a li tag.
     * Each li tag contains an item that needs to be extracted
     */
    public ArrayList<Item> getAllItems(){
        TagNode ul = getAllItems("ul", "responsive-listing-grid");
        ArrayList<Item> itemsList = new ArrayList<>(64);//almost always 64 results
        if( ul.getChildren() != null ){

            for( int i = 0; i < ul.getChildren().size(); i++ ){
                if( getItem(ul.getChild(i) )!= null) {
                    itemsList.add(getItem(ul.getChild(i)));
                }
            }
        }
        return itemsList;
    }

    /**
     *  Each li tag contains one item that needs to be extracted.
    *  Each li tag contains multiple children, and each tag needs to be searched
    *  to extract the img src, description, category and price.
     * @param liTag li tag to be searched
     * @return a Core.Item obj with the img src, description, category and price or null if not found
     */
        public Item getItem(TagNode liTag){
            if( root == null )
                return null;
            //to store the img src, description, category and price
            String description, category, price, imgScr;
            description = category = price = imgScr = "";

            //to stop searching when the img src, description, category and price are found
            Boolean foundCat, foundP, foundScr, foundD;
            foundCat = foundP = foundScr = foundD = false;

            Queue<TagNode> q = new LinkedList<>();
            q.add(liTag);
        while( !q.isEmpty() ){
            int n = q.size();
            while( n > 0 ){
                //Stop searching when al the necessary info has been found
                if( foundCat && foundD && foundP && foundScr ){
                    //also insert a timestamp to know its create date and time
                    String timeStamp = new Timestamp(System.currentTimeMillis()).toString();
                    return new Item(description, category, price, imgScr, timeStamp);
                }
                TagNode tag = q.remove();
                if( tag.getTagName().equals("img") ){
                    imgScr = tag.getTagAtt();
                    foundScr = true;
                }
                else if( tag.getTagName().equals("h2") ){
                    description = tag.getInnerHTML();
                    foundD = true;
                }
                else if( tag.getTagName().equals("span")  ){
                    if( tag.getTagAtt() != null && tag.getTagAtt().equals("currency-value")) {
                        price = tag.getInnerHTML();
                        foundP = true;
                    }
                }
                else if( tag.getTagName().equals("p") ){
                    if( tag.getTagAtt() != null && tag.getTagAtt().equals("text-gray-lighter") ){
                        category = tag.getInnerHTML();
                        foundCat = true;
                    }
                }
                for (TagNode i : tag.getChildren())
                    q.add(i);
                n--;
            }
        }
        return null;
    }


}
