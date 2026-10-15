package main.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class SessionListener implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        SessionUtil.shutdown();
    }
}
