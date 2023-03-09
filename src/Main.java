import db.DBHandler;
import ui.UIHandler;
import web.Browser;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, InterruptedException {

        DBHandler db = new DBHandler();
        Browser browser = new Browser(db);

        UIHandler ui = new UIHandler(browser, db);


    }
}