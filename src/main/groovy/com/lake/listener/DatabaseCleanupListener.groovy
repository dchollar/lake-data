package com.lake.listener

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread
import groovy.transform.CompileStatic
import jakarta.servlet.ServletContextEvent
import jakarta.servlet.ServletContextListener
import jakarta.servlet.annotation.WebListener

import java.sql.Driver
import java.sql.DriverManager
import java.sql.SQLException

@CompileStatic
@WebListener
class DatabaseCleanupListener implements ServletContextListener {

    @Override
    void contextInitialized(final ServletContextEvent sce) {
    }

    @Override
    void contextDestroyed(final ServletContextEvent sce) {
        try {
            AbandonedConnectionCleanupThread.checkedShutdown()
        } catch (Exception e) {
            sce.servletContext.log('Failed to shut down MySQL cleanup thread', e)
        }

        ClassLoader cl = Thread.currentThread().contextClassLoader
        Enumeration<Driver> drivers = DriverManager.drivers
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement()
            if (driver.class.classLoader == cl) {
                try {
                    DriverManager.deregisterDriver(driver)
                } catch (SQLException e) {
                    sce.servletContext.log("Error deregistering JDBC driver ${driver}", e)
                }
            }
        }
    }

}
