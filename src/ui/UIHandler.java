package ui;

import db.DBHandler;
import ui.listener.ActionHandler;
import ui.listener.WindowEventHandler;
import web.Browser;

import javax.swing.*;
import java.awt.*;

public class UIHandler {

    // constants
    final Dimension defaultButtonDimension = new Dimension(100, 12);
    final Dimension defaultLabelDimension = new Dimension(70, 12);
    final Dimension defaultTextFieldDimension = new Dimension(135, 12);
    final int defaultWidth = 100;
    final int defaultHeight = 12;

    // main components
    private JFrame frame;
    private JPanel panel;
    private GridBagConstraints constraints;

    // components
    public JTextField txtChannelName;
    public JLabel lblStatus;
    public JButton btnWatch;

    private ActionHandler actionHandler;
    private WindowEventHandler windowEventHandler;

    public Browser browser;
    private DBHandler db;


    public UIHandler(Browser browser, DBHandler db) {

        this.browser = browser;
        this.db = db;

        frame = new JFrame("Twitch Message Tracker");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setMinimumSize(new Dimension(300, 100));
        frame.setResizable(false);

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        // event handlers
        actionHandler = new ActionHandler(this, browser, db);
        windowEventHandler = new WindowEventHandler(browser);

        //makeMainWindowUI();
        switchPanelTo("main");

        frame.addWindowListener(this.windowEventHandler);

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    public void switchPanelTo(String panelName) {
        panel.removeAll();

        switch (panelName) {
            case "main" -> this.makeMainWindowUI();
        }

        frame.revalidate();
        frame.repaint();
    }

    private void makeMainWindowUI() {

        frame.setTitle("Twitch Message Tracker");

        // components
        txtChannelName = new JTextField();
        addUIElement(txtChannelName, defaultTextFieldDimension, 0, 0);

        btnWatch = new JButton("Start");
        btnWatch.setName("btnWatch");
        btnWatch.addActionListener(this.actionHandler);
        addUIElement(btnWatch, defaultButtonDimension, 1, 0);

        lblStatus = new JLabel();
        addUIElement(lblStatus, defaultLabelDimension, 2, 0);

    }


    private void addUIElement(Component c, Dimension dim, int row, int col, int gridWidth, Insets insets) {
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        c.setPreferredSize(dim);
        constraints.gridx = col;
        constraints.gridy = row;
        constraints.gridwidth = gridWidth;
        if (insets.bottom == 0) insets.bottom = 5;
        if (insets.left == 0) insets.left = 5;
        if (insets.top == 0) insets.top = 5;
        if (insets.right == 0) insets.right = 5;
        constraints.insets = insets;  // Legt den Abstand zwischen dem Rand einer Komponente und ihren Nachbarn auf x Pixel fest
        constraints.ipadx = 10;  // Legt den horizontalen Innenabstand auf 10 Pixel fest
        constraints.ipady = 10;  // Legt den vertikalen Innenabstand auf 10 Pixel fest
        panel.add(c, constraints);
    }

    public void addUIElement(Component c, Dimension dim, int row, int col, int gridWidth) {
        addUIElement(c, dim, row, col, gridWidth, new Insets(5, 5, 5, 5));
    }

    public void addUIElement(Component c, Dimension dim, int row, int col) {
        addUIElement(c, dim, row, col, 1, new Insets(5, 5, 5, 5));
    }


}
