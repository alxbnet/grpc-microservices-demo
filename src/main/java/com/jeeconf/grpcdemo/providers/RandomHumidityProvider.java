package com.jeeconf.grpcdemo.providers;

import com.jeeconf.grpcdemo.Humidity;

import java.util.Random;
import java.util.function.Supplier;

/**
 * Randomly generates {@link Humidity}.
 */
public class RandomHumidityProvider implements Supplier<Humidity> {

    private final Random random = new Random();

    @Override
    public Humidity get() {
        float humidity = random.nextInt(99) + random.nextFloat();
        return Humidity.newBuilder().setValue(humidity).build();
    }
}
