package ui.listener;

import web.Browser;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowEventHandler extends WindowAdapter {

    private Browser browser;

    public WindowEventHandler(Browser browser) {
        this.browser = browser;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        browser.quit();
        JFrame frame = (JFrame) e.getSource();
        frame.dispose();
    }

}
