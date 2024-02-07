package com.github.thibstars.netaware.utils;

import com.github.thibstars.netaware.exceptions.TargetCpuUtilisationOutOfBoundsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Thibault Helsmoortel
 */
class OptimalThreadPoolSizeCalculatorTest {

    private OptimalThreadPoolSizeCalculator calculator;

    @BeforeEach
    void setUp() {
        this.calculator = new OptimalThreadPoolSizeCalculator();
    }

    @Test
    void shouldGetOptimalThreadPoolSize() {
        int waitTime = 500;
        int serviceTime = 250;
        int result = calculator.get(waitTime, serviceTime);

        int availableCores = Runtime.getRuntime().availableProcessors();
        int expected = (int) (availableCores * (1 + (double) waitTime / serviceTime));

        Assertions.assertEquals(expected, result, "Result must be correct.");
    }

    @Test
    void shouldGetOptimalThreadPoolSizeWithSpecifiedTargetCpuUtilisation() {
        int waitTime = 500;
        int serviceTime = 250;
        double targetCpuUtilisation = 0.22;
        int result = calculator.get(targetCpuUtilisation, waitTime, serviceTime);

        int availableCores = Runtime.getRuntime().availableProcessors();
        int expected = (int) (availableCores * targetCpuUtilisation * (1 + (double) waitTime / serviceTime));

        Assertions.assertEquals(expected, result);
    }

    @Test
    void shouldThrowExceptionWhenTargetCpuUtilisationIsNotWithinBounds() {
        int waitTime = 500;
        int serviceTime = 250;
        Assertions.assertThrows(
                TargetCpuUtilisationOutOfBoundsException.class,
                () -> calculator.get(-3.1, waitTime, serviceTime)
        );
        Assertions.assertThrows(
                TargetCpuUtilisationOutOfBoundsException.class,
                () -> calculator.get(62.5, waitTime, serviceTime)
        );
    }
}