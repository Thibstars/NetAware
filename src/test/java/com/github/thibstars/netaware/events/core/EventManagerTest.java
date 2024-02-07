package com.github.thibstars.netaware.events.core;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
class EventManagerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventManagerTest.class);

    private EventManager eventManager;

    @BeforeEach
    void setUp() {
        this.eventManager = new EventManager();
    }

    @Test
    void shouldRemoveHandler() {
        AtomicBoolean receivedEvent = new AtomicBoolean();

        EventHandler<TestEvent> eventEventHandler = event -> {
            LOGGER.info("Received a test event!");
            receivedEvent.set(true);
        };
        eventManager.registerHandler(TestEvent.class, eventEventHandler);

        eventManager.removeHandler(TestEvent.class, eventEventHandler);

        eventManager.dispatch(new TestEvent(this));

        Assertions.assertFalse(
                receivedEvent.get(),
                "Event must not be received, as handler should have been removed."
        );
    }

    static class TestEvent extends Event {

        protected TestEvent(Object source) {
            super(source);
        }
    }
}