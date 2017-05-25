package com.jeeconf.grpcdemo.providers;

import com.jeeconf.grpcdemo.Speed;
import com.jeeconf.grpcdemo.Wind;

import java.util.Random;
import java.util.function.Supplier;

/**
 * Randomly generates {@link Wind}.
 */
public class RandomWindProvider implements Supplier<Wind> {

    private final Random random = new Random();

    @Override
    public Wind get() {
        float direction = random.nextInt(359) + random.nextFloat();
        float speedValue = random.nextInt(70) + random.nextFloat();
        Speed speed = Speed.newBuilder().setValue(speedValue).setUnits(Speed.Units.KMH).build();

        return Wind.newBuilder()
                .setDirection(direction)
                .setSpeed(speed)
                .build();
    }
}
