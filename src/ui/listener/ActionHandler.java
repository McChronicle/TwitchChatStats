package ui.listener;

import db.DBHandler;
import ui.UIHandler;
import web.Browser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class ActionHandler implements ActionListener {

    private UIHandler ui;
    private Browser browser;
    private DBHandler db;

    public ActionHandler(UIHandler ui, Browser browser, DBHandler db) {
        super();
        this.ui = ui;
        this.browser = browser;
        this.db = db;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Component sourceComponent = (Component) e.getSource();
        switch (sourceComponent.getName()) {
            case "btnWatch" -> btnWatchClicked(e);
        }
    }

    private void btnWatchClicked(ActionEvent e) {

        if (this.browser.watching) {
            this.browser.watching = false;
            this.ui.btnWatch.setText("Start");
            return;
        }

        this.browser.watching = true;
        this.ui.btnWatch.setText("Stop");
        Runnable watch = () -> {
            try {
                this.browser.readMessages(this.ui.txtChannelName.getText());
            } catch (SQLException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        };
        Thread watchThread = new Thread(watch);
        watchThread.start();


    }

}
