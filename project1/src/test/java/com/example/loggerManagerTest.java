package com.example;

import java.util.logging.Logger;

import static org.junit.Assert.assertSame;
import org.junit.Test;

import com.example.utils.LoggerManager;

public class loggerManagerTest {
    @Test
    public void testGetLogger_ThreadSafe() throws InterruptedException {
        String category = "testCategory";
        Logger[] logger1 = new Logger[1];
        Logger[] logger2 = new Logger[1];

        Thread thread1 = new Thread(() -> logger1[0] = LoggerManager.getLogger(category));
        Thread thread2 = new Thread(() -> logger2[0] = LoggerManager.getLogger(category));
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        assertSame(logger1[0], logger2[0]);
    }
}
