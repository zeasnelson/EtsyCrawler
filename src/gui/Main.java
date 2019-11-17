package gui;
import core.*;
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

public class Main {

    private JFrame mainFrame;

    private CardLayout card;

    private JTextField accUsrNmF;
    private JTextField searchField;
    private JTextField emailField;
    private JTextField usernameField;
    private JTextField passwordField;
    private JTextField signUpPassField;
    private JTextField signUpUserNameField;
    private JTextField minPrice;
    private JTextField maxPrice;
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
    private JComboBox sort;
    private JComboBox deleteList;

    private JButton   reset;


    private JLabel usrNameLabel;
    private JLabel accountMsgLabel;
    private JLabel msgField;
    private JLabel resLabel;
    private JLabel signInErrorLabel;


    private JPanel  mainPanel;
    private JPanel singInPanel;
    private JPanel resultsPanel;
    private JPanel accountPanel;
    private JPanel searchFilters;

    private DefaultTableModel tableModel;
    private ImageIcon resImage;

    private int currentRow;
    private UserSession currentSession;
    private EtsyUrlFormatter etsyUrlFormatter;


    public Main() {
        mainFrame = new JFrame();
        renderMainFrame();
    }

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

    private void renderMainPanel(){

        mainPanel = new JPanel();
        card      = new CardLayout();

        createSignInPanel();
        createResultsPanel();
        createAccountPanel();

        mainPanel.setLayout(card);
        mainPanel.add("signInPanel", singInPanel);
        mainPanel.add("resultsPanel", resultsPanel);
        mainPanel.add("accountPanel", accountPanel);

        card.show(mainPanel, "signInPanel");

        mainFrame.add(mainPanel);
    }

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
        JButton    signInAsAdmin = new JButton("Sign in as Admin");

        JLabel     signUpLabel      = new JLabel("Sign Up");
        JLabel     signUpUsrNmLabel = new JLabel("Enter user name: ");
        signUpUserNameField         = new JTextField();
        JLabel     signUpRuleLabel  = new JLabel("Only letters, no spaces, 15 characters!");
        JLabel     signUpPassLabel  = new JLabel("Enter password: ");
        signUpPassField             = new JPasswordField();
        JLabel     passRuleLabel    = new JLabel("Only letters or numbers, no spaces or symbols");
        JButton    signUpBtn        = new JButton("Sign Up");



        signInBtn.addActionListener(e -> signIn() );
        signUpBtn.addActionListener(e -> signUp());
        signInAsGuest.addActionListener( e -> signInUserAsGuest() );

        singInPanel.setLayout(new GridBagLayout());


        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.ipadx = 4;
        constraints.ipady = 4;
        constraints.insets = constraints.insets = new Insets(0,10,0,0);
        //Sign in section
        constraints.gridx = 0;
        constraints.gridy = 0;
        singInPanel.add(signIn, constraints);
        signIn.setForeground(Color.BLUE);

        constraints.gridx = 1;
        constraints.gridy = 0;
        singInPanel.add(signInErrorLabel, constraints);
        signInErrorLabel.setBorder( new EmptyBorder(20,0,20,0));
        signInErrorLabel.setForeground(Color.RED);

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

    private JPanel createSearchOptions(){
        searchFilters      = new JPanel();
        category = new JComboBox();
        offer    = new JComboBox();
        color    = new JComboBox();
        itemType = new JComboBox();
        shipTo   = new JComboBox();

        customizable = new JCheckBox("Customizable");
        giftWrap     = new JCheckBox("Can be gift-wrapped");
        giftCards    = new JCheckBox("Accepts Etsy gift cards");
        reset        = new JButton("reset filters");
        minPrice     = new JTextField("enter min price");
        maxPrice     = new JTextField("enter max price");
        Border blackLine = BorderFactory.createLineBorder(Color.black);
        TitledBorder border = new TitledBorder(blackLine, "Search Filters");
        border.setTitleColor(Color.blue);

        searchFilters.setLayout(new GridBagLayout());

        Dimension priceDim = new Dimension(110, 30);
        minPrice.setPreferredSize(priceDim);
        maxPrice.setPreferredSize(priceDim);

        category.addItemListener( new SearchByCategoryItemListener());
        offer.addItemListener( new SetOfferItemListener());
        color.addItemListener( new SetColorItemListener());
        itemType.addItemListener( new ItemTypeListener());
        shipTo.addItemListener( new ShipToItemListener());
        customizable.addItemListener(new CustomizeBoxListener());
        giftWrap.addItemListener( new GiftWrapListener());
        giftCards.addItemListener( new GiftCardItemListener());
        reset.addActionListener( e -> resetSearchFilters());
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

    private JPanel createNorthPanel(){
        JPanel topPanel    = new JPanel();
        searchField        = new JTextField("Search on Etsy.com");
        accountMsgLabel     = new JLabel(" ");
        JButton signOut    = new JButton("sign out");
        JButton search     = new JButton("Search");
        JButton account    = new JButton("Account");

        topPanel.setLayout(new GridBagLayout());

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

    private JPanel createCenterPanel(){
        JPanel centerPanel  = new JPanel();
        deleteList          = new JComboBox();
        JButton sendEmail   = new JButton("send");
        emailField          = new JTextField("Enter email");
        JButton viewEmailReport = new JButton("Review email report");
        sort                = new JComboBox();
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);


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

        //        emailField.addFocusListener(new FocusListener() {
//            @Override
//            public void focusGained(FocusEvent e) {
//                emailField.setText("");
//            }
//
//            @Override
//            public void focusLost(FocusEvent e) {
//                emailField.setText("Enter email");
//            }
//        })

        centerPanel.setPreferredSize(new Dimension(800, 40));
        return centerPanel;
    }

    private JPanel createSouthPanel(){

        String [] colNames = {"Description", "Category", "Price", "Date Created"};

        tableModel             = new DefaultTableModel(colNames, 0);
        JPanel lowerPanel      = new JPanel();
        JTable resultsTable    = new JTable(tableModel);
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

    private void createAccountPanel(){
        accountPanel        = new JPanel();
        JButton signOutBtn  = new JButton("Sign out");
        JButton backToResB  = new JButton("Go back to results");
        JLabel accountLabel = new JLabel("Account");
        usrNameLabel        = new JLabel("username");
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

    private void switchToSignInPanel(){
        card.show(mainPanel, "signInPanel");
        mainFrame.validate();
        mainFrame.repaint();
    }

    private void switchToResults(){
        card.show(mainPanel, "resultsPanel");

    }

    private void switchToAccountPanel(){
        if( this.currentSession.isGuestUser() ){
            msgField.setText("Guests do not have accounts");
            return;
        }
        card.show(mainPanel, "accountPanel");
    }

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

    private void changeUserPassword(JTextField text){
        String newPassword = text.getText().trim();
        boolean isValidPass = validatePassword(newPassword);
        if( isValidPass ){
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

    private void showItemPicture(int row){
        if( this.currentSession.isViewingEmailReport() ){
            msgField.setText(" ");
            return;
        }
        msgField.setText("Downloading image for selected item");
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

    private void signInUserAsGuest(){
        User guest = new User("1", "guest", "guest");
        this.currentSession = new UserSession(guest);
        this.currentSession.setIsGuestUser(true);
        this.etsyUrlFormatter = new EtsyUrlFormatter();
        switchToResults();
    }

    private void signUp(){
        String userName = signUpUserNameField.getText().trim();
        String password = signUpPassField.getText().trim();
        Boolean userNm = validateUserName(userName);
        Boolean pass   = validatePassword(password);

        if( userNm && pass ){
            if( DBDriver.userExists(signUpUserNameField.getText().trim()) ){
                signInErrorLabel.setText("User name already taken");
            }
            else{
                Boolean wasAdded = DBDriver.addNewUser(userName, password);
                if ( wasAdded ){
                    User user = DBDriver.userExists(userName, password);
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
            else if( !userNm )
                signInErrorLabel.setText("Invalid username!");
        }
    }

    private void signIn(){

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
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

    private void deleteResult(){
        if( this.currentSession.isViewingEmailReport() ){
            this.currentSession.deleteItemFromEmailReport(currentRow);
            tableModel.removeRow(currentRow);
        }
        else {
            Boolean removed = currentSession.deleteSingleResult(currentRow);
            if( removed ){
                tableModel.removeRow(currentRow);
                msgField.setText(currentSession.getSearches().get(currentSession.getCurrentSearch()).size() + " items");
            }
        }
    }

    private void deleteCurrentSearch(){
        if( this.currentSession.isViewingEmailReport()){
            msgField.setText("Searches cannot be deleted while viewing email report");
        }
        else{
            boolean deleted = currentSession.deleteCurrentSearch();
            if( deleted ) {
                tableModel.setRowCount(0);
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

    private void deleteEmailReport(){
        if( this.currentSession.isViewingEmailReport() ) {
            this.currentSession.deleteEmailReport();
            this.tableModel.setRowCount(0);
            this.msgField.setText("Report deleted and cannot be recovered");
        }
        else{
            msgField.setText("Report can only be deleted while viewing it");
        }
    }

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

    private void logOutUser(){
        if( this.currentSession.isGuestUser() ){
            File guestDir = new File("appdata/usersdata/1");
            if( guestDir.exists() ){
                String[]entries = guestDir.list();
                for(String s: entries){
                    File currentFile = new File(guestDir.getPath(),s);
                    currentFile.delete();
                }
                guestDir.delete();
            }
        }

        if( !this.currentSession.isGuestUser()){
            currentSession.storeUserActivity();
        }
        passwordField.setText("");
        signUpPassField.setText("");
        signUpUserNameField.setText("");
        searchField.setText("Search on Etsy.com");
        accNewPassField.setText("");
        tableModel.setRowCount(0);
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

    private void getData(JTextField searchQuery){
        String searchRaw = searchQuery.getText().trim();

        //dont search if nothing was entered in search box
        if( searchRaw.isEmpty() || searchRaw.equals("Search on Etsy.com")){
            return;
        }

        //Set the min/max price and search query
        this.etsyUrlFormatter.setPriceMin(getMinPrice());
        this.etsyUrlFormatter.setPriceMax(getMaxPrice());
        this.etsyUrlFormatter.setSearchQuery(searchRaw);

        String search = this.etsyUrlFormatter.getSearchQuery();
        ArrayList<Item> results = currentSession.search(search, this.etsyUrlFormatter.getFormatedUrl());
        if( results != null ){
            loadTableRows(results);
            msgField.setText(results.size() + " items downloaded");
            DefaultComboBoxModel model = (DefaultComboBoxModel)deleteList.getModel();
            if( model.getIndexOf(search) == -1 ){
                deleteList.addItem(search);
            }
        }
        else{
            msgField.setText("Error downloading, try removing search filters or Etsy blocked just us");
        }
    }

    private void loadTableRows(ArrayList<Item> results){
        if( results != null ) {
            tableModel.setRowCount(0);
            for (Item item : results) {
                if (item != null) {
                    Object[][] newRow = new Object[1][4];
                    newRow[0][0] = item.getDescription();
                    newRow[0][1] = item.getCategory();
                    newRow[0][2] = "$ " + item.getPrice();
                    newRow[0][3] = item.getTimeStamp();
                    tableModel.addRow(newRow[0]);
                }
            }
        }
    }

    private boolean validateEmailAddress(String emailAddress){
        String validEmail = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return emailAddress.matches(validEmail);
    }

    private void sendEmailTo(){

        if( emailField != null ) {
            String recipient = emailField.getText();
            String emailAddress = recipient.trim();
            boolean isValidEmail = validateEmailAddress(emailAddress);
            if( isValidEmail ){
                Email email = new Email();
                email.setRecipient(emailAddress);
                email.setHeader("Report from Etsy Crawler" );
                email.setEmailBody(this.currentSession.getEmailList(), this.currentSession.getUser().getUserName());
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

    private void showEmailReport(){
        ArrayList<Item> emailList = currentSession.getEmailList();
        if( emailList != null && emailList.size() > 0 ){
            loadTableRows(emailList);
            msgField.setText(emailList.size() + " items");
        }
        else{
            msgField.setText("Email report is empty!");
        }
    }

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

    private void sortByPrice(String sortOrder){
        this.currentSession.sortByPrice(sortOrder);
        loadTableRows(this.currentSession.getSearches().get(this.currentSession.getCurrentSearch()));
    }

    private void sortByDateASC(){
        this.currentSession.sortByDateASC();
        loadTableRows(this.currentSession.getSearches().get(this.currentSession.getCurrentSearch()));
    }

    private void sortByDateDESC(){
        this.currentSession.sortByDateDESC();
        loadTableRows(this.currentSession.getSearches().get(this.currentSession.getCurrentSearch()));
    }

    private String getMinPrice(){
        String price = this.minPrice.getText().trim();
        if( price.matches("^\\d+([,.]\\d{1,2})?$") ){
            return price;
        }
        else
            msgField.setText("Not a valid min price");
            return "";
    }

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


    //main
    public static void main(String[] args) {
        new Main();

    }//main




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
                        loadTableRows(savedSearches);
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
                        loadTableRows(results);
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
            if( item.toString().equals("Neither") ){
                etsyUrlFormatter.setFreeShipping("");
                etsyUrlFormatter.setSale("");
            }
            else if( item.toString().equals("Free Shipping only") ){
                etsyUrlFormatter.setFreeShipping("&free_shipping=true");
                etsyUrlFormatter.setSale("");
            }
            else if( item.toString().equals("Sale only") ){
                etsyUrlFormatter.setSale("&is_discounted=true");
                etsyUrlFormatter.setFreeShipping("");
            }
            else if( item.toString().equals("Free Shipping and on Sale") ){
                etsyUrlFormatter.setFreeShipping("&free_shipping=true");
                etsyUrlFormatter.setSale("&is_discounted=true");
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
