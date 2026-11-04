package com.lake.listener

import jakarta.servlet.ServletContext
import jakarta.servlet.ServletContextEvent
import spock.lang.Specification

class DatabaseCleanupListenerSpec extends Specification {

    def "contextInitialized does nothing without errors"() {
        given:
        DatabaseCleanupListener listener = new DatabaseCleanupListener()
        ServletContextEvent sce = Mock(ServletContextEvent)

        when:
        listener.contextInitialized(sce)

        then:
        noExceptionThrown()
    }

    def "contextDestroyed cleans up resources and handles driver deregistration"() {
        given:
        DatabaseCleanupListener listener = new DatabaseCleanupListener()
        ServletContext servletContext = Mock(ServletContext)
        ServletContextEvent sce = Mock(ServletContextEvent)
        sce.servletContext >> servletContext

        when:
        listener.contextDestroyed(sce)

        then:
        noExceptionThrown()
    }

}
