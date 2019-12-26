package gui;
import core.*;
import manager.Admin;
import manager.UserSession;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;


/**
 * The main user interface to display all extracted data from Etsy.com
 * The GUI is made of 3 main components:
 *      - Sign In panel
 *          -presented to all users upon GUI start up
 *
 *      - Admin panel
 *          - Restore searched data from logged file
 *          - view user's data
 *          - delete user's accounts
 *
 *      - Results panel
 *          - search on Etsy
 *          - search by filters
 *          - sort by price, category
 *          - create and send email report
 *          - delete individual searched and entire searches
 *          - Account Panel
 *              - ability to change password
 */
public class GUI {

    /**
     * Main JFrame where all components will be rendered
     */
    private JFrame mainFrame;

    /**
     * CardLayout which enables the ability to switch between different panels in the GUI
     * Switch between SignIn panel, Admin Panel, AccountPanel
     */
    private CardLayout card;

    //All text fields
    //account user name
    private JTextField accUsrNmF;
    //to type search query
    private JTextField searchField;
    //to type email to send report to
    private JTextField emailField;
    //username text box at sign in panel
    private JTextField usernameField;
    //password text box at sign in panel
    private JTextField passwordField;
    //sign up password text box at sign in panel
    private JTextField signUpPassField;
    //sign up username text box at sign in panel
    private JTextField signUpUserNameField;
    //text box for minimum price to specify filter
    private JTextField minPrice;
    //text box for max price to specify filter
    private JTextField maxPrice;
    //text box to type new password at account panel
    private JTextField accNewPassField;
    
    //Search filters components
    private JComboBox category ;
    private JComboBox offer    ;
    private JComboBox color    ;
    private JComboBox itemType ;
    private JComboBox shipTo   ;
    private JCheckBox customizable ;
    private JCheckBox giftWrap     ;
    private JCheckBox giftCards    ;
    private JComboBox deleteList;


    //to show errors or messages in the user panel
    private JLabel accountMsgLabel;
    //to show errors or messages in the user panel
    private JLabel msgField;
    //to show images for specific results
    private JLabel resLabel;
    //to show errors in the sign in panel
    private JLabel signInErrorLabel;
    private JLabel adminMsgLabel;


    //Jpanels for main components
    private JPanel  mainPanel;
    private JPanel singInPanel;
    private JPanel resultsPanel;
    private JPanel accountPanel;
    private JPanel adminPanel;

    //tabels to store data in diferent components of the GUI
    //To data downloaded from etsy
    private DefaultTableModel resultsTblModel;
    //To show data from log files in the admin panel
    private DefaultTableModel adminTblModel;
    //To show all users that have search history in the admin panel
    private DefaultTableModel adminUsersListModel;
    //To show all registered users in the admin panel
    private DefaultTableModel adminAllUsersListTable;

    //To show all extracted items
    private JTable resultsTable;
    //To show all users that have search history
    private JTable usersListTable;
    //to show all registered users
    private JTable allUsersTable;

    //To display images from results
    private ImageIcon resImage;

    //restore all deleted data for a specific user
    private JButton reconstructUser;
    //to view data from a specific user
    private JButton adminViewUserData;
    //to recover a selected item in the table for a specific user
    private JButton adminRecoverSelectedItem;
    //To delete a user's account
    private JButton adminDeleteSelectedUser;
    //To view the data deleted by a specific user
    private JButton adminViewUserDeletedData;
    //reconstruct all data deleted by all users
    private JButton reconstructAll;


    //To store the index of the current selected row users panel table
    private int currentRow;
    //To store teh index of the current select row in teh admin panel table
    private int adminTableRowIndex;
    //To store the id of the user in a selected row in the admins panel table
    private String selectedUserId;
    //To manage the session of the user currently logged in
    private UserSession currentSession;
    //to manage data when logged in as admin
    private Admin admin;
    //To format the Etsy url when different filters are applied
    private EtsyUrlFormatter etsyUrlFormatter;


    /**
     * Crate the main JFRAME
     */
    public GUI() {
        mainFrame = new JFrame();
        renderMainFrame();
    }

    //initialize the main JFrame
    private void renderMainFrame(){
        msgField = new JLabel(" ");
        mainFrame.setTitle("Etsy Crawler");
        mainFrame.setDefaultCloseOperation(mainFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        renderMainPanel();
        mainFrame.pack();
        mainFrame.setResizable(true);
        // mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    /**
     * Render all different components
     *  - SignInPanel
     *  - ResultsPanel
     *  - AccountPanel
     *  - AdminPanel
     */
    private void renderMainPanel(){
        mainPanel = new JPanel();
        card      = new CardLayout();

        createSignInPanel();
        createResultsPanel();
        createAccountPanel();
        createAdminPanel();

        mainPanel.setLayout(card);
        mainPanel.add("signInPanel", singInPanel);
        mainPanel.add("resultsPanel", resultsPanel);
        mainPanel.add("accountPanel", accountPanel);
        mainPanel.add("adminPanel", adminPanel);

        card.show(mainPanel, "signInPanel");

        mainFrame.add(mainPanel);
    }

    /**
     * render teh signInPanel
     */
    private void createSignInPanel(){
        singInPanel              = new JPanel();
        signInErrorLabel         = new JLabel();
        usernameField            = new JTextField(15);
        passwordField            = new JPasswordField(10);
        JLabel signIn            = new JLabel("Sign In");
        JButton signInBtn        = new JButton("Sign in");
        JButton signInAsGuest    = new JButton("Sign in as guest");
        JLabel userNameLabel     = new JLabel("username:");
        JLabel passwordLabel     = new JLabel("password:");
        JButton signInAsAdmin    = new JButton("Sign in as Admin");

        JLabel     signUpLabel      = new JLabel("Sign Up");
        JLabel     signUpUsrNmLabel = new JLabel("Enter user name: ");
        signUpUserNameField         = new JTextField();
        JLabel     signUpRuleLabel  = new JLabel("Only letters, no spaces, 15 characters!");
        JLabel     signUpPassLabel  = new JLabel("Enter password: ");
        signUpPassField             = new JPasswordField();
        JLabel     passRuleLabel    = new JLabel("Only letters or numbers, no spaces or symbols");
        JButton    signUpBtn        = new JButton("Sign Up");

        //Action listeners, button clicks
        signInBtn.addActionListener(e -> signIn() );
        signUpBtn.addActionListener(e -> signUp());
        signInAsGuest.addActionListener( e -> signInUserAsGuest() );
        signInAsAdmin.addActionListener( e -> switchToAdminPanel());

        singInPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.ipadx = 4;
        constraints.ipady = 4;
        constraints.insets = new Insets(0,10,0,0);
        //Sign in section
        constraints.gridx = 0;
        constraints.gridy = 0;
        singInPanel.add(signIn, constraints);
        signIn.setForeground(Color.BLUE);

        constraints.gridwidth = 2;
        constraints.gridx = 1;
        constraints.gridy = 0;
        singInPanel.add(signInErrorLabel, constraints);
        signInErrorLabel.setBorder( new EmptyBorder(20,0,20,0));
        signInErrorLabel.setForeground(Color.RED);

        constraints.gridwidth = 1;
        constraints.gridx = 0;
        constraints.gridy = 1;
        singInPanel.add(userNameLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        singInPanel.add(usernameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        singInPanel.add(passwordLabel, constraints);


        constraints.gridx = 1;
        constraints.gridy = 2;
        singInPanel.add(passwordField, constraints);

        constraints.insets.bottom = 10;
        constraints.insets.top = 5;
        constraints.gridx = 0;
        constraints.gridy = 4;
        singInPanel.add(signInBtn, constraints);

        constraints.gridx = 1;
        constraints.gridy = 4;
        singInPanel.add(signInAsGuest, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        singInPanel.add(signInAsAdmin, constraints);

        //Sign up section
        constraints.insets.bottom = 0;
        constraints.insets.top = 0;
        constraints.gridx = 0;
        constraints.gridy = 6;
        singInPanel.add(signUpLabel, constraints);
        signUpLabel.setBorder( new EmptyBorder(30,0,10,0));
        signUpLabel.setForeground(Color.BLUE);

        constraints.gridx = 0;
        constraints.gridy = 8;
        singInPanel.add(signUpUsrNmLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 8;
        singInPanel.add(signUpUserNameField, constraints);


        constraints.gridx = 2;
        constraints.gridy = 8;
        singInPanel.add(signUpRuleLabel, constraints);
        signUpRuleLabel.setBorder( new EmptyBorder(0,20,0,0));

        constraints.gridx = 0;
        constraints.gridy = 9;
        singInPanel.add(signUpPassLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 9;
        singInPanel.add(signUpPassField, constraints);

        constraints.gridx = 2;
        constraints.gridy = 9;
        singInPanel.add(passRuleLabel, constraints);
        passRuleLabel.setBorder( new EmptyBorder(0,20,10,0));

        constraints.insets.top = 5;
        constraints.gridx = 0;
        constraints.gridy = 10;
        singInPanel.add(signUpBtn, constraints);

        singInPanel.setBorder( new EmptyBorder(20,20,20,20));
        singInPanel.setBackground(Color.WHITE);
    }

    private void createResultsPanel(){
        resultsPanel    = new JPanel();

        resultsPanel.setLayout(new BorderLayout());

        JPanel topPanel = createNorthPanel();
        JPanel centerPanel = createCenterPanel();
        JPanel lowerPanel = createSouthPanel();

        resultsPanel.add(topPanel, BorderLayout.NORTH);
        resultsPanel.add(centerPanel, BorderLayout.CENTER);
        resultsPanel.add(lowerPanel, BorderLayout.SOUTH);
    }

    private void createAdminPanel(){
        adminPanel        = new JPanel();

        JPanel optsPanel       = createAdminButtons();
        JPanel adminUsersPanel = createUsersListPanel();
        JPanel adminResTable   = createAdminUsersTable();

        adminPanel.setLayout(new BorderLayout());
        adminPanel.add(adminUsersPanel, BorderLayout.WEST);
        adminPanel.add(adminResTable, BorderLayout.CENTER);
        adminPanel.add(optsPanel, BorderLayout.EAST);
    }

    //Render the all buttons used in the admins panel
    private JPanel createAdminButtons(){
        JPanel optsPanel               = new JPanel();
        JButton viewAllDeletedSearches = new JButton("View all deleted searches");
        reconstructAll                 = new JButton("Recover all deleted data");
        adminRecoverSelectedItem       = new JButton("Recover selected result");

        adminViewUserData              = new JButton("View saved data");
        adminViewUserDeletedData       = new JButton("View deleted searches");
        reconstructUser                = new JButton("Recover deleted data");

        adminDeleteSelectedUser        = new JButton("delete account");

        JButton signOut                = new JButton("Sign out");
        Dimension buttonSize           = new Dimension(230, 30);


        //click listeners
        reconstructAll.addActionListener( e -> reconstructAllUsersData());
        viewAllDeletedSearches.addActionListener( e -> loadAllDeletedSearches());
        adminRecoverSelectedItem.addActionListener( e -> deleteResultFromDeleteHistory());
        adminViewUserData.addActionListener(e -> viewSelectedUserSavedData());
        adminViewUserDeletedData.addActionListener( e -> getUserDeletedDataById());
        reconstructUser.addActionListener(e -> reconstructSelectedUserData());
        adminDeleteSelectedUser.addActionListener( e -> deleteSelectedUser());

        //set button sizes
        signOut.addActionListener(e -> signOutAdmin());
        adminViewUserData.setPreferredSize(buttonSize);
        adminViewUserDeletedData.setPreferredSize(buttonSize);
        adminRecoverSelectedItem.setPreferredSize(buttonSize);
        adminDeleteSelectedUser.setPreferredSize(buttonSize);
        reconstructAll.setPreferredSize(buttonSize);
        reconstructUser.setPreferredSize(buttonSize);
        viewAllDeletedSearches.setPreferredSize(buttonSize);


        optsPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,10,5,10);
        c.gridx = 0;
        c.gridy = 0;
        optsPanel.add(viewAllDeletedSearches, c);
        c.gridx = 0;
        c.gridy = 1;
        optsPanel.add(reconstructAll, c);
        c.insets.bottom = 40;
        c.gridx = 0;
        c.gridy = 2;
        optsPanel.add(adminRecoverSelectedItem, c);

        c.insets.bottom = 5;
        c.gridx = 0;
        c.gridy = 3;
        optsPanel.add(adminViewUserData, c);
        c.gridx = 0;
        c.gridy = 4;
        optsPanel.add(adminViewUserDeletedData, c);
        c.insets.bottom = 40;
        c.gridx = 0;
        c.gridy = 5;
        optsPanel.add(reconstructUser, c);
        c.insets.bottom = 40;
        c.gridx = 0;
        c.gridy = 6;
        optsPanel.add(adminDeleteSelectedUser, c);

        c.gridx = 0;
        c.gridy = 7;
        optsPanel.add(signOut, c);


        return optsPanel;
    }

    /**
     * Create the admins panel
     * @return JPanel
     */
    private JPanel createAdminUsersTable(){

        String [] colNames = {"User id", "Description", "Category", "Price", "Date Created"};

        JPanel tablePanel      = new JPanel();
        adminTblModel          = new DefaultTableModel(colNames, 0);
        JTable adminResTable   = new JTable(adminTblModel);
        JScrollPane scrollpane = new JScrollPane(adminResTable);

        adminMsgLabel          = new JLabel(" ");

        scrollpane.setPreferredSize(new Dimension(850, 650));
        scrollpane.setMinimumSize(adminResTable.getPreferredSize());
        adminResTable.setRowHeight(30);

        //Action listener for the table
        adminResTable.getSelectionModel().addListSelectionListener(e -> {
                if( !e.getValueIsAdjusting() ){
                    if (adminResTable.getSelectedRow() > -1) {
                        adminTableRowIndex = adminResTable.getSelectedRow();
                    }
                }
            }
        );

        //set prefered column size for the admin results table
        adminResTable.getColumnModel().getColumn(0).setPreferredWidth(2);
        adminResTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        adminResTable.getColumnModel().getColumn(2).setPreferredWidth(5);
        adminResTable.getColumnModel().getColumn(3).setPreferredWidth(30);
        adminResTable.getColumnModel().getColumn(4).setPreferredWidth(30);

        tablePanel.setLayout(new GridBagLayout());
        GridBagConstraints tableCons = new GridBagConstraints();
        tableCons.insets = new Insets(0,0,20,0);
        tableCons.gridx = 0;
        tableCons.gridy = 0;
        adminMsgLabel.setForeground(Color.BLUE);
        adminMsgLabel.setFont( new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        tablePanel.add(adminMsgLabel, tableCons);

        tableCons.insets.bottom = 0;
        tableCons.gridx = 0;
        tableCons.gridy = 1;
        tablePanel.add(scrollpane, tableCons);

        return tablePanel;
    }

    /**
     * Render the admins Users tables
     * @return JPanel
     */
    private JPanel createUsersListPanel(){
        String [] users = {"user id", "user name"};
        JPanel usersPanel      = new JPanel();
        adminUsersListModel    = new DefaultTableModel(users, 0);
        usersListTable         = new JTable(adminUsersListModel);
        JScrollPane activeUserScrollPane = new JScrollPane(usersListTable);

        adminAllUsersListTable = new DefaultTableModel(users, 0);
        allUsersTable          = new JTable(adminAllUsersListTable);
        JScrollPane allUsersScrollPane = new JScrollPane(allUsersTable);

        //Listener for the table that shows users that have search history
        allUsersTable.getSelectionModel().addListSelectionListener(e -> {
                    if( !e.getValueIsAdjusting() ){
                        if (allUsersTable.getSelectedRow() > -1) {
                            int row = allUsersTable.getSelectedRow();
                            Object o  = allUsersTable.getValueAt(row, 0);
                            selectedUserId = (String)o;
                            adminTableRowIndex = row;
                            String userId = (String)allUsersTable.getValueAt(row, 0);
                            System.out.println(userId);
                            adminViewUserData.setText("View saved data");
                            reconstructUser.setText("Recover deleted data");
                            adminViewUserDeletedData.setText("View deleted searches");
                            adminViewUserData.setEnabled(false);
                            reconstructUser.setEnabled(false);
                            adminViewUserDeletedData.setEnabled(false);
                            adminDeleteSelectedUser.setEnabled(true);
                            usersListTable.clearSelection();
                            adminMsgLabel.setText(usersListTable.getValueAt(row, 1).toString());
                        }
                    }
                }
        );


        //Action listener for the table that shows all registered users
        usersListTable.getSelectionModel().addListSelectionListener(e -> {
                if( !e.getValueIsAdjusting() ){
                    if (usersListTable.getSelectedRow() > -1) {
                        adminTblModel.setRowCount(0);
                        int row = usersListTable.getSelectedRow();
                        Object o  = usersListTable.getValueAt(row, 0);
                        selectedUserId = (String)o;
                        String userName = (String)usersListTable.getValueAt(row, 1);
                        adminViewUserData.setText("View " + userName + " saved data");
                        reconstructUser.setText("Recover " + userName + " deleted data");
                        adminViewUserDeletedData.setText("View " + userName + " deleted searches");
                        adminViewUserData.setEnabled(true);
                        reconstructUser.setEnabled(false);
                        adminViewUserDeletedData.setEnabled(true);
                        adminDeleteSelectedUser.setEnabled(false);
                        adminRecoverSelectedItem.setEnabled(false);
                        reconstructAll.setEnabled(false);
                        allUsersTable.clearSelection();
                        adminMsgLabel.setText(usersListTable.getValueAt(row, 1).toString());
                    }
                }
            }
        );


        JLabel activeUsersLabel = new JLabel("Users with search history");
        JLabel allUsersLabel    = new JLabel("All Users");

        activeUserScrollPane.setPreferredSize(new Dimension(150, 200));
        activeUserScrollPane.setMinimumSize(usersPanel.getPreferredSize());
        usersListTable.setRowHeight(30);

        allUsersScrollPane.setPreferredSize(new Dimension(150, 330));
        allUsersScrollPane.setMinimumSize(usersPanel.getPreferredSize());
        allUsersTable.setRowHeight(30);

        usersPanel.setLayout(new GridBagLayout());
        GridBagConstraints usersCons = new GridBagConstraints();
        usersCons.insets = new Insets(0,10,5,10);
        usersCons.gridx = 0;
        usersCons.gridy = 0;
        activeUsersLabel.setForeground(Color.BLUE);
        usersPanel.add(activeUsersLabel, usersCons);

        usersCons.insets.bottom = 20;
        usersCons.gridx = 0;
        usersCons.gridy = 1;
        usersPanel.add(activeUserScrollPane, usersCons);

        usersCons.insets.bottom = 5;
        usersCons.gridx = 0;
        usersCons.gridy = 2;
        allUsersLabel.setForeground(Color.BLUE);
        usersPanel.add(allUsersLabel, usersCons);

        usersCons.gridx = 0;
        usersCons.gridy = 3;
        usersPanel.add(allUsersScrollPane, usersCons);

        return usersPanel;
    }

    /**
     * Create the search filter options panel
     * @return JPanel
     */
    @SuppressWarnings("unchecked")
    private JPanel createSearchOptions(){
        JPanel searchFilters = new JPanel();
        category = new JComboBox();
        offer    = new JComboBox();
        color    = new JComboBox();
        itemType = new JComboBox();
        shipTo   = new JComboBox();

        customizable = new JCheckBox("Customizable");
        giftWrap     = new JCheckBox("Can be gift-wrapped");
        giftCards    = new JCheckBox("Accepts Etsy gift cards");
        //buttons
        //reset search filters
        JButton reset = new JButton("reset filters");
        minPrice     = new JTextField("enter min price");
        maxPrice     = new JTextField("enter max price");
        Border blackLine = BorderFactory.createLineBorder(Color.black);
        TitledBorder border = new TitledBorder(blackLine, "Search Filters");
        border.setTitleColor(Color.blue);

        searchFilters.setLayout(new GridBagLayout());

        Dimension priceDim = new Dimension(110, 30);
        minPrice.setPreferredSize(priceDim);
        maxPrice.setPreferredSize(priceDim);

        //action listeners, button cliks
        category.addItemListener( new SearchByCategoryItemListener());
        offer.addItemListener( new SetOfferItemListener());
        color.addItemListener( new SetColorItemListener());
        itemType.addItemListener( new ItemTypeListener());
        shipTo.addItemListener( new ShipToItemListener());
        customizable.addItemListener(new CustomizeBoxListener());
        giftWrap.addItemListener( new GiftWrapListener());
        giftCards.addItemListener( new GiftCardItemListener());
        reset.addActionListener(e -> resetSearchFilters());
        minPrice.addFocusListener( new MinPriceFocusListener());
        maxPrice.addFocusListener( new MaxPriceFocusListener());

        category.addItem("CATEGORY");
        category.addItem("Everything");
        category.addItem("home-and-living");
        category.addItem("bags-and-purses");
        category.addItem("art-and-collectibles");
        category.addItem("accessories");
        category.addItem("paper-and-party-supplies");
        category.addItem("jewelry");
        category.addItem("weddings");
        category.addItem("toys-and-games");
        category.addItem("electronics-and-accessories");
        category.addItem("pet-supplies");
        category.addItem("clothing");
        category.addItem("books-movies-and-music");
        category.addItem("bath-and-beauty");

        offer.addItem("SPECIAL OFFER");
        offer.addItem("Free Shipping only");
        offer.addItem("Sale only");
        offer.addItem("Free Shipping and on Sale");
        offer.addItem("Neither");

        color.addItem("COLOR");
        color.addItem("Any color");
        color.addItem("black");
        color.addItem("blue");
        color.addItem("gray");
        color.addItem("green");
        color.addItem("orange");
        color.addItem("red");
        color.addItem("pink");
        color.addItem("white");
        color.addItem("yellow");

        itemType.addItem("ITEM TYPE");
        itemType.addItem("Both");
        itemType.addItem("Handmade");
        itemType.addItem("Vintage");

        shipTo.addItem("SHIP TO");
        shipTo.addItem("Anywhere");
        shipTo.addItem("USA");
        shipTo.addItem("China");
        shipTo.addItem("Italy");
        shipTo.addItem("Turkey");
        shipTo.addItem("Mexico");
        shipTo.addItem("Germany");
        shipTo.addItem("Thailand");
        shipTo.addItem("UK");

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(10,0,0,5);

        //row 1
        c.gridy = 0;
        c.gridx = 0;
        searchFilters.add(category, c);
        c.gridy = 0;
        c.gridx = 1;
        searchFilters.add(offer, c);
        c.gridy = 0;
        c.gridx = 2;
        searchFilters.add(color, c);
        c.gridy = 0;
        c.gridx = 3;
        searchFilters.add(itemType, c);
        c.gridy = 0;
        c.gridx = 4;
        searchFilters.add(shipTo, c);

        c.insets.left = 20;
        c.gridy = 0;
        c.gridx = 5;
        searchFilters.add(reset, c);

        //row 2
        c.insets.left = 0;
        c.gridy = 1;
        c.gridx = 0;
        searchFilters.add(customizable, c);
        c.gridy = 1;
        c.gridx = 1;
        searchFilters.add(giftWrap, c);
        c.gridy = 1;
        c.gridx = 2;
        searchFilters.add(giftCards, c);

        c.gridy = 1;
        c.gridx = 3;
        searchFilters.add(minPrice, c);
        c.gridy = 1;
        c.gridx = 4;
        searchFilters.add(maxPrice, c);


        searchFilters.setBorder( border);

        return searchFilters;
    }

    //Creates the search bar, account and log out button panel
    private JPanel createNorthPanel(){
        JPanel topPanel    = new JPanel();
        searchField        = new JTextField("Search on Etsy.com");
        accountMsgLabel     = new JLabel(" ");
        JButton signOut    = new JButton("sign out");
        JButton search     = new JButton("Search");
        JButton account    = new JButton("Account");

        topPanel.setLayout(new GridBagLayout());

        //action listeners
        signOut.addActionListener(e -> logOutUser() );
        search.addActionListener( e -> getData(searchField));
        account.addActionListener( e -> switchToAccountPanel());
        searchField.addFocusListener(new SearchFieldFocusListener());

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10,0,0,20);

        c.gridy = 0;
        c.gridx = 0;
        searchField.setPreferredSize(new Dimension(500, 25));
        searchField.setBorder(new LineBorder(Color.BLACK));
        topPanel.add(searchField, c);

        c.gridy = 0;
        c.gridx = 1;
        topPanel.add(search, c);

        c.gridy = 0;
        c.gridx = 2;
        topPanel.add(signOut, c);

        c.gridy = 0;
        c.gridx = 3;
        topPanel.add(account, c);

        c.gridwidth = 8;
        c.gridy = 1;
        c.gridx = 0;
        topPanel.add(createSearchOptions(), c);

        return topPanel;
    }

    //Create the panel to sort and send email report
    @SuppressWarnings("unchecked")
    private JPanel createCenterPanel(){
        JPanel centerPanel  = new JPanel();
        deleteList          = new JComboBox();
        JButton sendEmail   = new JButton("send");
        emailField          = new JTextField("Enter email");
        JButton viewEmailReport = new JButton("Review email report");
        JComboBox sort          = new JComboBox();

        //action listeners
        emailField.addFocusListener(new EmailFieldFocusListener());
        deleteList.addItemListener(new HistoryItemSelectedListener());
        sendEmail.addActionListener( e -> sendEmailTo());
        viewEmailReport.addActionListener( e -> showEmailReport());
        sort.addItemListener( new SortItemListener());

        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(15,10,0,10);
        c.gridy = 0;
        c.gridx = 0;
        deleteList.addItem("View History");
        deleteList.addItem("All searches");
        centerPanel.add(deleteList, c);

        c.insets.right = 50;
        c.gridy = 0;
        c.gridx = 1;
        sort.addItem("Sort by");
        sort.addItem("by price: ASC");
        sort.addItem("by price: DESC");
        sort.addItem("by date: ASC");
        sort.addItem("by date: DESC");
        centerPanel.add(sort, c);

        c.insets.right = 10;
        c.gridy = 0;
        c.gridx = 2;
        centerPanel.add(viewEmailReport, c);

        c.gridy = 0;
        c.gridx = 3;
        emailField.setPreferredSize(new Dimension(200, 30));
        emailField.setMaximumSize(emailField.getPreferredSize());
        emailField.setMinimumSize(emailField.getPreferredSize());
        emailField.setBorder(new EmptyBorder(5,10,5,10));
        centerPanel.add(emailField, c);

        c.gridy = 0;
        c.gridx = 4;
        centerPanel.add(sendEmail, c);


        centerPanel.setPreferredSize(new Dimension(800, 40));
        return centerPanel;
    }

    /**
     * Create the panel that display results downloaded from searches
     * @return the panel
     */
    private JPanel createSouthPanel(){

        String [] colNames = {"Description", "Category", "Price", "Date Created"};

        resultsTblModel        = new DefaultTableModel(colNames, 0);
        JPanel lowerPanel      = new JPanel();
        resultsTable           = new JTable(resultsTblModel);
        JScrollPane scrollPane = new JScrollPane(resultsTable);

        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        resultsTable.getColumnModel().getColumn(1).setPreferredWidth(30);
        resultsTable.getColumnModel().getColumn(2).setPreferredWidth(5);
        resultsTable.getColumnModel().getColumn(3).setPreferredWidth(30);
        resultsTable.setRowHeight(30);


        resultsTable.getSelectionModel().addListSelectionListener(e -> {
            if( !e.getValueIsAdjusting() ){
                if (resultsTable.getSelectedRow() > -1) {
                    currentRow = resultsTable.getSelectedRow();
                    showItemPicture(resultsTable.getSelectedRow());
                }
            }
        }

        );

        scrollPane.setPreferredSize(new Dimension(900, 460));
        lowerPanel.setLayout(new BorderLayout());

        JButton deleteRes = new JButton("Delete selected result");
        JButton deleteEntireSearch = new JButton("Delete current entire search");

        JPanel imgPanel     = new JPanel();
        resImage = new ImageIcon("appdata/images/tempImg.png");
        resLabel = new JLabel(resImage);

        JButton addToEmailReport = new JButton("Add selected item to email report");
        JButton addCurrentSearchToEmailReport = new JButton("Add current search to email report");
        JButton deleteEmailReport = new JButton("Delete email report");

        addToEmailReport.addActionListener( e -> addItemToEmailReport());
        addCurrentSearchToEmailReport.addActionListener( e -> addCurrentSearchToEmailReport());
        deleteEmailReport.addActionListener(e -> deleteEmailReport());

        deleteRes.addActionListener( e -> deleteResult() );
        deleteEntireSearch.addActionListener(e -> deleteCurrentSearch());

        imgPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(3,3,3,3);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 0;
        constraints.gridy = 0;
        imgPanel.add(resLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        imgPanel.add(addCurrentSearchToEmailReport, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        imgPanel.add(addToEmailReport, constraints);

        constraints.insets.bottom = 20;
        constraints.gridx = 0;
        constraints.gridy = 3;
        imgPanel.add(deleteEmailReport, constraints);

        constraints.insets.bottom = 0;
        constraints.gridx = 0;
        constraints.gridy = 4;
        imgPanel.add(deleteEntireSearch, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        imgPanel.add(deleteRes, constraints);


        imgPanel.setBorder( new EmptyBorder(20,20,20,20));
        imgPanel.setPreferredSize(new Dimension(340, 280));

        //just to created the padding on the left of table
        JLabel etsyLabel = new JLabel("        ");
        msgField.setBorder(new EmptyBorder(20, 40, 20, 40));
        msgField.setForeground(Color.BLUE);

        JLabel separator = new JLabel("  ");//only used to separate a component
        lowerPanel.add(separator, BorderLayout.NORTH);
        lowerPanel.add(scrollPane, BorderLayout.CENTER);
        lowerPanel.add(imgPanel, BorderLayout.EAST);
        lowerPanel.add(etsyLabel, BorderLayout.WEST);
        lowerPanel.add(msgField, BorderLayout.SOUTH);
        return lowerPanel;
    }

    //Create the account panel
    private void createAccountPanel(){
        accountPanel        = new JPanel();
        JButton signOutBtn  = new JButton("Sign out");
        JButton backToResB  = new JButton("Go back to results");
        JLabel accountLabel = new JLabel("Account");
        //labels to show messages
        //to show the username at the user panel
        JLabel usrNameLabel = new JLabel("username");
        accUsrNmF           = new JTextField();
        JLabel changePassL  = new JLabel("Change password");
        JLabel newPassL     = new JLabel("Enter new password");
        accNewPassField     = new JTextField();
        JButton changePassB = new JButton("Change password");
        JLabel passRule     = new JLabel("Only letters or numbers, no spaces or symbols");

        signOutBtn.addActionListener( e -> logOutUser() );
        changePassB.addActionListener( e -> changeUserPassword(accNewPassField));
        backToResB.addActionListener( e -> switchToResults() );

        accountPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.ipadx = 10;
        c.ipady = 10;
        c.anchor = GridBagConstraints.WEST;

        c.gridy = 0;
        c.gridx = 0;
        c.gridwidth = 2;
        accountMsgLabel.setBorder( new EmptyBorder(0,0,30,0));
        accountPanel.add(accountMsgLabel, c);

        c.gridwidth = 1;
        c.gridy = 1;
        c.gridx = 0;
        accountPanel.add(signOutBtn, c);

        c.gridy = 1;
        c.gridx = 1;
        accountPanel.add(backToResB, c);

        c.gridy = 2;
        c.gridx = 0;
        accountLabel.setBorder( new EmptyBorder(30,0,2,0));
        accountLabel.setForeground(Color.BLUE);
        accountPanel.add(accountLabel, c);

        c.gridy = 3;
        c.gridx = 0;
        usrNameLabel.setBorder( new EmptyBorder(20,0,20,0));
        accountPanel.add(usrNameLabel, c);

        c.gridy = 3;
        c.gridx = 1;
        accUsrNmF.setPreferredSize(new Dimension(150, 15));
        accUsrNmF.setEditable(false);
        accountPanel.add(accUsrNmF, c);

        c.gridy = 4;
        c.gridx = 0;
        changePassL.setBorder( new EmptyBorder(30,0,2,0));
        changePassL.setForeground(Color.BLUE);
        accountPanel.add(changePassL, c);

        c.gridy = 5;
        c.gridx = 0;
        newPassL.setBorder( new EmptyBorder(20,0,20,0));
        accountPanel.add(newPassL, c);

        c.gridy = 5;
        c.gridx = 1;
        accNewPassField.setPreferredSize(new Dimension(150, 15));
        accountPanel.add(accNewPassField, c);

        c.gridy = 5;
        c.gridx = 2;
        passRule.setBorder( new EmptyBorder(20,20,20,0));
        accountPanel.add(passRule, c);

        c.gridy = 6;
        c.gridx = 0;
        accountPanel.add(changePassB, c);

    }

    /**
     * allows to switch to the sign in panel
     */
    private void switchToSignInPanel(){
        card.show(mainPanel, "signInPanel");
        mainFrame.validate();
        mainFrame.repaint();
    }

    /**
     * allows to switch to the user panel
     */
    private void switchToResults(){
        card.show(mainPanel, "resultsPanel");

    }

    /**
     * allows to switch to the account panel
     */
    private void switchToAccountPanel(){
        if( this.currentSession.isGuestUser() ){
            msgField.setText("Guests do not have accounts");
            return;
        }
        card.show(mainPanel, "accountPanel");
    }

    /**
     * Allows to switch to the admin panel
     */
    private void switchToAdminPanel(){

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if( !username.equals("admin") && !password.equals("admin")){
            signInErrorLabel.setText("You are not admin! Admin username: admin -- Admin pass: admin");
            return;
        }

        admin = new Admin();
        loadAdminResultsTable();
        loadAllUsersTable();

        //disable buttons
        adminViewUserDeletedData.setEnabled(false);
        reconstructUser.setEnabled(false);
        adminViewUserData.setEnabled(false);
        adminRecoverSelectedItem.setEnabled(false);
        adminDeleteSelectedUser.setEnabled(false);
        reconstructAll.setEnabled(false);

        card.show(mainPanel, "adminPanel");
    }

    /**
     * When switching to the admin's panel, load all users that have search history
     */
    private void loadAdminResultsTable(){
        //request data to admin object
        ArrayList<User> users = admin.getActiveUsers();
        //fill table
        if( users != null ){
            adminUsersListModel.setRowCount(0);
            for (User user : users) {
                if (user != null) {
                    Object[][] newRow = new Object[1][2];
                    newRow[0][0] = user.getUserId();
                    newRow[0][1] = user.getUserName();
                    adminUsersListModel.addRow(newRow[0]);
                }
            }
        }
    }

    /**
     * When switching to the admin's panel, load all users registered users
     */
    private void loadAllUsersTable(){
        //get all users
        ArrayList<User> users = admin.getAllRegisteredUsers();
        //fill table
        if( users != null ){
            adminAllUsersListTable.setRowCount(0);
            for (User user : users) {
                if (user != null) {
                    Object[][] newRow = new Object[1][2];
                    newRow[0][0] = user.getUserId();
                    newRow[0][1] = user.getUserName();
                    adminAllUsersListTable.addRow(newRow[0]);
                }
            }
        }
    }

    /**
     * Reset all search filters
     */
    private void resetSearchFilters(){

        //reset components
        category.setSelectedIndex(0);
        offer.setSelectedIndex(0);
        color.setSelectedIndex(0);
        itemType.setSelectedIndex(0);
        shipTo.setSelectedIndex(0);
        customizable.setSelected(false);
        giftWrap.setSelected(false);
        giftCards.setSelected(false);
        minPrice.setText("enter min price");
        maxPrice.setText("enter max price");

        //reset the EtsyFormatter object as well
        etsyUrlFormatter = new EtsyUrlFormatter();
    }

    /**
     * change user password
     * @param text JTextfield with the new password
     */
    private void changeUserPassword(JTextField text){
        //get the password
        String newPassword = text.getText().trim();
        //validate password
        boolean isValidPass = validatePassword(newPassword);
        if( isValidPass ){
            //query database to updata password
            int didChange = this.currentSession.getUser().changePassword(newPassword);
            if( didChange == 1 ){
                accountMsgLabel.setText("Password changed!");
            }
            else if( didChange == 2 ){
                accountMsgLabel.setText("Enter a different password");
            }
            else if( didChange == 0 ){
                accountMsgLabel.setText("Password could not be changed");
            }
        }
        else
            accountMsgLabel.setText("Invalid password");
    }

    /**
     * to show the picture of a specific result
     * @param row index of selected row
     */
    private void showItemPicture(int row){
        //don't load pictures if the user is viewing the email report
        if( this.currentSession.isViewingEmailReport() ){
            msgField.setText(" ");
            return;
        }
        msgField.setText("Downloading image for selected item");
        //download and get the downloaded image id
        String imgId = currentSession.downloadItemImg(row);

        String dir;
        if( imgId == null ){
            dir = "appdata/images/etsy.png";
            msgField.setText("Error downloading image. Showing etsy logo instead");
        }
        else if( currentSession.isGuestUser() ){
            dir = "appdata/usersdata/1/"+imgId;
        }
        else {
            dir = "appdata/images/" + imgId;
        }

        resImage = new ImageIcon(dir);
        resLabel.setIcon(resImage);
    }

    /**
     * Create a UserSeasion as a guest
     * The db has a default user id of 1 in the db
     * data by the guest is not stored but logged
     */
    private void signInUserAsGuest(){
        User guest = new User("1", "guest", "guest");
        this.currentSession = new UserSession(guest);
        this.currentSession.setIsGuestUser(true);
        this.etsyUrlFormatter = new EtsyUrlFormatter();
        switchToResults();
    }

    /**
     * To enable user registration
     */
    private void signUp(){
        //get password and username entered by user
        String userName = signUpUserNameField.getText().trim();
        String password = signUpPassField.getText().trim();
        //validate password and username
        Boolean userNm = validateUserName(userName);
        Boolean pass   = validatePassword(password);

        if( userNm && pass ){
            //check if the user already exists in the db
            if( DBDriver.userExists(signUpUserNameField.getText().trim()) ){
                signInErrorLabel.setText("User name already taken");
            }
            else{
                //add user to the db
                Boolean wasAdded = DBDriver.addNewUser(userName, password);
                if ( wasAdded != null &&  wasAdded ){
                    User user = DBDriver.userExists(userName, password);
                    //sign in user
                    setUpUser(user);
                    switchToResults();
                }
                else{
                    signInErrorLabel.setText("Error, try again");
                }
            }
        }
        else {
            if( !pass ){
                signInErrorLabel.setText("Invalid password!");
            }
            else
                signInErrorLabel.setText("Invalid username!");
        }
    }

    /**
     * Enable user to sign in
     */
    private void signIn(){

        //get username and password entered
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        //check if user exists in db
        User user = DBDriver.userExists(username, password);
        if( username.isEmpty() || password.isEmpty() ){
            signInErrorLabel.setText("Enter a username");
        }
        else if ( user != null ){
            setUpUser(user);
            switchToResults();
        }
        else{
            signInErrorLabel.setText("Invalid username or password");
        }
    }

    /**
     * enable admin to sign out
     */
    private void signOutAdmin(){
        //in case the admin viewed all search history but did not recover
        //we need to write that history back to file
        admin.saveDeletedHistory();
        switchToSignInPanel();
        adminRecoverSelectedItem.setEnabled(false);
        adminTableRowIndex = -1;
        adminTblModel.setRowCount(0);
        adminMsgLabel.setText(" ");
        admin = null;
    }

    /**
     * Set default values for new user sessions
     * @param user A User object, must have user Id
     */
    @SuppressWarnings("unchecked")
    private void setUpUser(User user){
        this.etsyUrlFormatter = new EtsyUrlFormatter();
        this.currentSession = new UserSession(user);
        this.currentSession.loadHistory();
        this.resImage = new ImageIcon("appdata/images/tempImg.png");
        this.resLabel.setIcon(resImage);
        this.accUsrNmF.setText(user.getUserName());
        DefaultComboBoxModel model = (DefaultComboBoxModel)deleteList.getModel();
        model.addAll(currentSession.getSearchHistory().keySet());
    }

    /**
     * Enable the admin to restore a specific result that was deleted by the user
     */
    private void deleteResultFromDeleteHistory(){
        boolean wasDeleted = admin.restoreSelectedResult(adminTableRowIndex);
        if( wasDeleted ){
            adminTblModel.removeRow(adminTableRowIndex);
            adminMsgLabel.setText("Recovered 1 item");
        }
        else
            adminMsgLabel.setText("There is nothing to recover");
    }

    /**
     * Enable user to delete a selected result in the table
     */
    private void deleteResult(){
        //delete from email report
        if( this.currentSession.isViewingEmailReport() ){
            this.currentSession.deleteItemFromEmailReport(currentRow);
            resultsTblModel.removeRow(currentRow);
        }
        else {
            //delete from searched data
            Boolean removed = currentSession.deleteSingleResult(currentRow);
            if( removed ){
                resultsTblModel.removeRow(currentRow);
                resultsTable.getSelectionModel().clearSelection();
                msgField.setText(currentSession.getSearches().get(currentSession.getCurrentSearch()).size() + " items");
                String  dir = "appdata/images/etsy.png";
                resImage = new ImageIcon(dir);
                resLabel.setIcon(resImage);
            }
        }
    }

    /**
     * Allow user to delete all results by search under a specific search
     * ex. user serached for car and received 50 items.
     * This delete all 50 items
     */
    @SuppressWarnings("unchecked")
    private void deleteCurrentSearch(){
        //don't allow when the user is viewing the email report
        if( this.currentSession.isViewingEmailReport()){
            msgField.setText("Searches cannot be deleted while viewing email report");
        }
        else{
            //delete
            boolean deleted = currentSession.deleteCurrentSearch();
            if( deleted ) {
                resultsTblModel.setRowCount(0);
                if( !currentSession.getCurrentSearch().equals("absolutelyEverythingFromAllTime")) {
                    DefaultComboBoxModel model = (DefaultComboBoxModel) deleteList.getModel();
                    int index = model.getIndexOf(currentSession.getCurrentSearch());
                    model.removeElementAt(index);
                }
                else if(currentSession.getCurrentSearch().equals("absolutelyEverythingFromAllTime") ){
                    DefaultComboBoxModel model = (DefaultComboBoxModel) deleteList.getModel();
                    model.removeAllElements();
                    model.addElement("View History:");
                    model.addElement("All searches");
                    msgField.setText("Deleted all search history");
                }
            }
        }
    }

    /**
     * allow the user to delete the email report
     */
    private void deleteEmailReport(){
        if( this.currentSession.isViewingEmailReport() ) {
            this.currentSession.deleteEmailReport();
            this.resultsTblModel.setRowCount(0);
            this.msgField.setText("Report deleted and cannot be recovered");
        }
        else{
            msgField.setText("Report can only be deleted while viewing it");
        }
    }

    /**
     * To valid a user name entered by the ser
     * @param userName the username
     * @return true if valid, false otherwise
     */
    private boolean validateUserName(String userName){
        if( userName.length() > 15 || userName.isEmpty() ){
            return false;
        }
        char[] chars = userName.toCharArray();
        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * validate password entered by user
     * @param pass the password entered by user
     * @return true if valid, false otherwise
     */
    private boolean validatePassword(String pass){
        String password = pass.replaceAll("\\s+$", "");
        if( password.trim().isEmpty() ){
            return false;
        }
        char[] chars = password.toCharArray();
        for (char c : chars) {
            if(!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * enable user to log out
     */
    @SuppressWarnings("unchecked")
    private void logOutUser(){
        if( this.currentSession.isGuestUser() ){
            File guestDir = new File("appdata/usersdata/1");
            if( guestDir.exists() ){
                String[]entries = guestDir.list();
                if( entries == null ){
                    return;
                }
                for(String s: entries){
                    File currentFile = new File(guestDir.getPath(),s);
                    currentFile.delete();
                }
                //delete activity if is a guest
                guestDir.delete();
            }
        }

        if( !this.currentSession.isGuestUser()){
            //if is a registered user, log activity
            currentSession.storeUserActivity(false);
        }

        //reset everything
        passwordField.setText("");
        signUpPassField.setText("");
        signUpUserNameField.setText("");
        searchField.setText("Search on Etsy.com");
        accNewPassField.setText("");
        resultsTblModel.setRowCount(0);
        searchField.setText("");
        msgField.setText("");
        emailField.setText("Enter Email");
        currentSession = null;
        resImage = new ImageIcon("appdata/images/tempImg.png");
        resLabel.setIcon(resImage);
        resetSearchFilters();
        DefaultComboBoxModel model = (DefaultComboBoxModel) deleteList.getModel();
        model.removeAllElements();

        model.addElement("View History:");
        model.addElement("All searches");
        deleteList.setSelectedIndex(0);
        switchToSignInPanel();
        currentSession = null;
    }

    /**
     * Request data from etsy
     * @param searchQuery the search keyword/s
     */
    @SuppressWarnings("unchecked")
    private void getData(JTextField searchQuery){
        String searchRaw = searchQuery.getText().trim();

        //dont search if nothing was entered in search box
        if( searchRaw.isEmpty() || searchRaw.equals("Search on Etsy.com")){
            return;
        }

        //get formatted url
        //Set the min/max price and search query
        this.etsyUrlFormatter.setPriceMin(getMinPrice());
        this.etsyUrlFormatter.setPriceMax(getMaxPrice());
        this.etsyUrlFormatter.setSearchQuery(searchRaw);
        String search = this.etsyUrlFormatter.getSearchQuery();

        //request data
        ArrayList<Item> results = currentSession.search(search, this.etsyUrlFormatter.getFormatedUrl());
        if( results != null ){
            loadResultsTableRows(results);
            msgField.setText(results.size() + " items downloaded");
            DefaultComboBoxModel model = (DefaultComboBoxModel)deleteList.getModel();
            if( model.getIndexOf(search) == -1 ){
                deleteList.addItem(search);
            }
            //log data
            currentSession.logData(FileOutput.INSERT, results, search);
        }
        else{
            msgField.setText("Error downloading, try removing search filters or Etsy blocked just us");
        }
    }

    /**
     * Load the results table with data received from Etsy.com
     * @param results An ArrayList<Itemn> with all items
     */
    private void loadResultsTableRows(ArrayList<Item> results){
        if( results != null ) {
            resultsTblModel.setRowCount(0);
            for (Item item : results) {
                if (item != null) {
                    Object[][] newRow = new Object[1][4];
                    newRow[0][0] = item.getDescription();
                    newRow[0][1] = item.getCategory();
                    newRow[0][2] = "$ " + item.getPrice();
                    newRow[0][3] = item.getTimeStamp();
                    resultsTblModel.addRow(newRow[0]);
                }
            }
        }
    }

    /**
     * To validate an email address
     * @param emailAddress an email address
     * @return true if valid, false otherwise
     */
    private boolean validateEmailAddress(String emailAddress){
        String validEmail = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return emailAddress.matches(validEmail);
    }

    /**
     * Send email report
     */
    private void sendEmailTo(){

        if( emailField != null ) {
            //get entered email address
            String recipient = emailField.getText();
            String emailAddress = recipient.trim();
            //validate email address
            boolean isValidEmail = validateEmailAddress(emailAddress);
            if( isValidEmail ){
                //set up email
                Email email = new Email();
                email.setRecipient(emailAddress);
                email.setHeader("Report from Etsy Crawler" );
                email.setEmailBody(this.currentSession.getEmailList(), this.currentSession.getUser().getUserName());
                //send email
                Boolean emailSent = email.sendEmail();
                if(emailSent){
                    msgField.setText("Email was sent successfully");
                }
                else
                    msgField.setText("Email could no be sent. Double check recipient email or google just blocked my temp email account(` .`)");
            }
            else
                msgField.setText("Enter a valid email address!");
        }
    }

    /**
     * Allow user to view the email report
     */
    private void showEmailReport(){
        ArrayList<Item> emailList = currentSession.getEmailList();
        if( emailList != null && emailList.size() > 0 ){
            loadResultsTableRows(emailList);
            msgField.setText(emailList.size() + " items");
        }
        else{
            msgField.setText("Email report is empty!");
        }
    }

    /**
     * Allow the user to add a selected item in the results table, to the email report
     */
    private void addItemToEmailReport(){
        if( this.currentSession.isViewingEmailReport() ){
            msgField.setText("Items cannot be added to from email report to email report");
        }
        else {
            boolean wasAdded = currentSession.addItemToEmailReport(currentRow);
            if( !wasAdded ){
                msgField.setText("Error while adding selected item to email report");
            }
            else
                msgField.setText("Item added to report");
        }
    }

    /**
     * Add an entire search to the email report
     */
    private void addCurrentSearchToEmailReport(){
        if( this.currentSession.isViewingEmailReport() ){
            msgField.setText("Cannot add to email report while viewing email report");
        }
        else{
            boolean wasAdded = currentSession.addCurrentSearchToEmailReport();
            if( !wasAdded ){
                msgField.setText("Error while adding current search to email report");
            }
            else
                msgField.setText("Added to report");
        }
    }

    /**
     * sort results by price in ascending or descending order
     * @param sortOrder string specifying ASC or DESC order
     */
    private void sortByPrice(String sortOrder){
        this.currentSession.sortByPrice(sortOrder);
        loadResultsTableRows(this.currentSession.getSearches().get(this.currentSession.getCurrentSearch()));
    }

    /**
     * sort results by date in ascending order
     */
    private void sortByDateASC(){
        this.currentSession.sortByDateASC();
        loadResultsTableRows(this.currentSession.getSearches().get(this.currentSession.getCurrentSearch()));
    }

    /**
     * sort results by date in descending order
     */
    private void sortByDateDESC(){
        this.currentSession.sortByDateDESC();
        loadResultsTableRows(this.currentSession.getSearches().get(this.currentSession.getCurrentSearch()));
    }

    /**
     * Get the minimum price filter
     * @return string representing the min price
     */
    private String getMinPrice(){
        String price = this.minPrice.getText().trim();
        if( price.matches("^\\d+([,.]\\d{1,2})?$") ){
            return price;
        }
        else
            msgField.setText("Not a valid min price");
            return "";
    }

    /**
     * Get the max price filter
     * @return string representing the max price
     */
    private String getMaxPrice(){
        String price = this.maxPrice.getText().trim();
        if( price.matches("^\\d+([,.]\\d{1,2})?$") ){
            return price;
        }
        else {
            msgField.setText("Not a valid max price");
            return "";
        }
    }

    /**
     * Allow admin to view all deleted searches from all users
     */
    private void loadAllDeletedSearches(){
        ArrayList<Item> deletedItems = admin.getAllDeletedHistory();
        if( deletedItems != null && !deletedItems.isEmpty() ){
            adminTblModel.setRowCount(0);
            for( Item item : deletedItems ){
                Object[][] newRow = new Object[1][5];
                newRow[0][0] = item.getUserId().equals("1") ? "guest" : item.getUserId();
                newRow[0][1] = item.getDescription();
                newRow[0][2] = item.getCategory();
                newRow[0][3] = "$ " + item.getPrice();
                newRow[0][4] = item.getTimeStamp();
                adminTblModel.addRow(newRow[0]);
            }
            //disable buttons
            adminRecoverSelectedItem.setEnabled(true);
            reconstructAll.setEnabled(true);
            adminMsgLabel.setText("Viewing " + deletedItems.size() + " items deleted by all users");
            adminViewUserData.setEnabled(false);
            adminViewUserDeletedData.setEnabled(false);
            reconstructUser.setEnabled(false);
            allUsersTable.getSelectionModel().clearSelection();
            usersListTable.getSelectionModel().clearSelection();
        }
        else
            adminMsgLabel.setText("Load data first or there is no deleted data by users");

    }

    /**
     * Enable the ability to reconstruct all deleted data for all users
     */
    private void reconstructAllUsersData(){
        if( admin.getDeletedData() == null ){
            adminMsgLabel.setText("Load data first or there is no deleted data by users");
            return;
        }

        //disable buttons
        adminViewUserData.setEnabled(false);
        adminRecoverSelectedItem.setEnabled(false);
        adminViewUserDeletedData.setEnabled(false);
        adminMsgLabel.setText("Recovered " + admin.getDeletedData().size() + " items");

        //get the data
        admin.restoreAllDeletedHistory();
        adminTblModel.setRowCount(0);
        adminRecoverSelectedItem.setEnabled(false);
    }

    /**
     * Allow the admin to view all data searched by a specific user
     */
    private void viewSelectedUserSavedData(){

            ArrayList<Item> items = admin.getSelectedUserData(selectedUserId);
            if( items != null && !items.isEmpty()){
                adminTblModel.setRowCount(0);
                for (Item item : items) {
                    if (item != null) {
                        Object[][] newRow = new Object[1][5];
                        newRow[0][0] = item.getUserId();
                        newRow[0][1] = item.getDescription();
                        newRow[0][2] = item.getCategory();
                        newRow[0][3] = "$ " + item.getPrice();
                        newRow[0][4] = item.getTimeStamp();
                        adminTblModel.addRow(newRow[0]);
                    }
                }
                adminMsgLabel.setText("Viewing " + items.size() + " items: saved user data, only the user can delete");
            }
            else
                adminMsgLabel.setText("Load data first or this user does not have any data saved");
    }

    /**
     * Get all data deleted by user id
     */
    private void getUserDeletedDataById(){
        if( !selectedUserId.isEmpty() ) {
            admin.loadUserDeletedDataById(selectedUserId);
            ArrayList<Item> results = admin.getDeletedData();
            if( results != null && !results.isEmpty() ){
                adminTblModel.setRowCount(0);
                for (Item item : results) {
                    if (item != null) {
                        Object[][] newRow = new Object[1][5];
                        newRow[0][0] = item.getUserId();
                        newRow[0][1] = item.getDescription();
                        newRow[0][2] = item.getCategory();
                        newRow[0][3] = "$ " + item.getPrice();
                        newRow[0][4] = item.getTimeStamp();
                        adminTblModel.addRow(newRow[0]);
                    }
                }
                reconstructUser.setEnabled(true);
                adminMsgLabel.setText("Viewing " + results.size() + " items");
            }
        }
    }

    /**
     * Allow the admin to restore a selected item from a specific user
     */
    private void reconstructSelectedUserData(){
        if( !selectedUserId.isEmpty() ){
            admin.recoverSelectedUserDeletedData(selectedUserId);
            reconstructUser.setEnabled(false);
            reconstructUser.setText("Recovered deleted data");
            adminTblModel.setRowCount(0);
            adminMsgLabel.setText("Recovered 1 items");
        }
        else
            adminMsgLabel.setText("Load data first or there is no deleted data by this user");
    }

    /**
     * Allow the admin to delete specific user accounts
     */
    private void deleteSelectedUser(){
        if( !selectedUserId.isEmpty() ){
            DBDriver.deleteUserByUserId(selectedUserId);
            adminAllUsersListTable.removeRow(adminTableRowIndex);

            adminMsgLabel.setText("Account deleted (` -`)");
        }

        else
            adminMsgLabel.setText("could not delete user " );
    }

    /**
     * Private action listeners for different components
     */
    private class SearchFieldFocusListener implements FocusListener{
        @Override
        public void focusGained(FocusEvent e) {
            searchField.setText("");
        }

        @Override
        public void focusLost(FocusEvent e) {
            if( searchField.getText().isEmpty() )
                searchField.setText("Search on Etsy.com");
        }
    }

    private class EmailFieldFocusListener implements FocusListener{
        @Override
        public void focusGained(FocusEvent e) {
            emailField.setText("");
        }

        @Override
        public void focusLost(FocusEvent e) {
            if( emailField.getText().isEmpty() )
                emailField.setText("Enter Email");
        }
    }


    private class HistoryItemSelectedListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                Object search = event.getItem();
                if( search.toString().equals("View History") || currentSession == null){
                    return;
                }
                else if( event.getItem().equals("All searches")){
                    ArrayList<Item> savedSearches =currentSession.getAllSearches();
                    if( savedSearches != null ){
                        loadResultsTableRows(savedSearches);
                        msgField.setText("Viewing all " + savedSearches.size() + " searches");
                    }
                    else{
                        msgField.setText("Could no load all searches");
                    }
                }
                else if( !search.toString().trim().isEmpty()) {
                    ArrayList<Item> results;
                    results = currentSession.search(search.toString(), currentSession.getSearchHistory().get(search.toString()));

                    if (results != null) {
                        loadResultsTableRows(results);
                        msgField.setText(results.size() + " items");
                    } else {
                        msgField.setText("Error reading history. Might be corrupted or file was deleted");
                    }
                }
            }
        }
    }

    private class SortItemListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object item = e.getItem();
                switch (item.toString()){
                    case "by price: ASC":
                        msgField.setText("Items sorted by price in ascending order");
                        sortByPrice("ASC");
                        break;
                    case "by price: DESC":
                        msgField.setText("Items sorted by price in descending order");
                        sortByPrice("DESC");
                        break;
                    case "by date: ASC":
                        msgField.setText("Items sorted by created date in ascending order");
                        sortByDateASC();
                        break;
                    case "by date: DESC":
                        msgField.setText("Items sorted by created date in descending order");
                        sortByDateDESC();
                        break;
                }
            }
        }
    }

    private class SearchByCategoryItemListener implements ItemListener{
        @Override
        public void itemStateChanged(ItemEvent e) {
            if( e.getStateChange() == ItemEvent.SELECTED ){
                Object item = e.getItem();
                if( !item.toString().equals("CATEGORY") ){
                    if( item.toString().equals("Everything") ){
                        etsyUrlFormatter.setCategory("");
                    }
                    else if( !item.toString().equals("CATEGORY")) {
                        etsyUrlFormatter.setCategory("/"+item.toString());
                    }
                }
            }
        }
    }

    private class SetOfferItemListener implements ItemListener{
        @Override
        public void itemStateChanged(ItemEvent e) {
            Object item = e.getItem();
            switch (item.toString()) {
                case "Neither":
                    etsyUrlFormatter.setFreeShipping("");
                    etsyUrlFormatter.setSale("");
                    break;
                case "Free Shipping only":
                    etsyUrlFormatter.setFreeShipping("&free_shipping=true");
                    etsyUrlFormatter.setSale("");
                    break;
                case "Sale only":
                    etsyUrlFormatter.setSale("&is_discounted=true");
                    etsyUrlFormatter.setFreeShipping("");
                    break;
                case "Free Shipping and on Sale":
                    etsyUrlFormatter.setFreeShipping("&free_shipping=true");
                    etsyUrlFormatter.setSale("&is_discounted=true");
                    break;
            }
        }
    }

    private class SetColorItemListener implements ItemListener{

        @Override
        public void itemStateChanged(ItemEvent e) {
            Object item = e.getItem();
            switch (item.toString()){
                case "Any color":
                    etsyUrlFormatter.setColor("");
                    break;
                case "black":
                    etsyUrlFormatter.setColor("&attr_1=1");
                    break;
                case "yellow":
                    etsyUrlFormatter.setColor("&attr_1=11");
                    break;
                case "gray":
                    etsyUrlFormatter.setColor("&attr_1=5");
                    break;
                case "green":
                    etsyUrlFormatter.setColor("&attr_1=4");
                    break;
                case "orange":
                    etsyUrlFormatter.setColor("&attr_1=6");
                    break;
                case "red":
                    etsyUrlFormatter.setColor("&attr_1=9");
                    break;
                case "pink":
                    etsyUrlFormatter.setColor("&attr_1=7");
                    break;
                case "white":
                    etsyUrlFormatter.setColor("&attr_1=10");
                    break;
            }

        }
    }

    private class ItemTypeListener implements ItemListener{

        @Override
        public void itemStateChanged(ItemEvent e) {
            Object item = e.getItem();
            switch (item.toString()){
                case "Both":
                    etsyUrlFormatter.setItemType("");
                    break;
                case "Handmade":
                    etsyUrlFormatter.setItemType("/handmade");
                    break;
                case "Vintage":
                    etsyUrlFormatter.setItemType("/vintage");
                    break;
            }
        }
    }

    private class ShipToItemListener implements ItemListener{

        @Override
        public void itemStateChanged(ItemEvent e) {
            Object item = e.getItem();
            switch (item.toString()){
                case "Anywhere":
                    etsyUrlFormatter.setShipTo("");
                    break;
                case "USA":
                    etsyUrlFormatter.setShipTo("&locationQuery=6252001");
                    break;
                case "China":
                    etsyUrlFormatter.setShipTo("&locationQuery=1814991");
                    break;
                case "Italy":
                    etsyUrlFormatter.setShipTo("&locationQuery=3175395");
                    break;
                case "Turkey":
                    etsyUrlFormatter.setShipTo("&locationQuery=298795");
                    break;
                case "Mexico":
                    etsyUrlFormatter.setShipTo("&locationQuery=3996063");
                    break;
                case "Germany":
                    etsyUrlFormatter.setShipTo("&locationQuery=2921044");
                    break;
                case "Thailand":
                    etsyUrlFormatter.setShipTo("&locationQuery=1605651");
                    break;
                case "UK":
                    etsyUrlFormatter.setShipTo("&locationQuery=2635167");
                    break;
            }
        }
    }

    private class CustomizeBoxListener implements ItemListener{

        @Override
        public void itemStateChanged(ItemEvent e) {
            if(e.getStateChange() == ItemEvent.SELECTED){
                etsyUrlFormatter.setCustomizable("&customizable=true");
            }
            else if(e.getStateChange() == ItemEvent.DESELECTED){
                etsyUrlFormatter.setCustomizable("");
            }

        }
    }

    private class GiftWrapListener implements ItemListener{

        @Override
        public void itemStateChanged(ItemEvent e) {
            if(e.getStateChange() == ItemEvent.SELECTED){
                etsyUrlFormatter.setGiftWrappable("&gift_wrap=true");
            }
            else if(e.getStateChange() == ItemEvent.DESELECTED){
                etsyUrlFormatter.setGiftWrappable("");
            }

        }
    }

    private class GiftCardItemListener implements ItemListener{

        @Override
        public void itemStateChanged(ItemEvent e) {
            if(e.getStateChange() == ItemEvent.SELECTED){
                etsyUrlFormatter.setEtsyGiftable("&gift_card=true");
            }
            else if(e.getStateChange() == ItemEvent.DESELECTED){
                etsyUrlFormatter.setEtsyGiftable("");
            }

        }
    }

    private class MinPriceFocusListener implements FocusListener{
        @Override
        public void focusGained(FocusEvent e) {
            minPrice.setText("");
        }

        @Override
        public void focusLost(FocusEvent e) {
            if( minPrice.getText().isEmpty() )
                minPrice.setText("enter min price");
        }
    }

    private class MaxPriceFocusListener implements FocusListener{
        @Override
        public void focusGained(FocusEvent e) {
            maxPrice.setText("");
        }

        @Override
        public void focusLost(FocusEvent e) {
            if( maxPrice.getText().isEmpty() )
                maxPrice.setText("enter max price");
        }
    }


}
