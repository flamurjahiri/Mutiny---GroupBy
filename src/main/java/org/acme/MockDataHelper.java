package org.acme;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MockDataHelper {


    public static int getRandomIndex() {
        return (int) (Math.random() * 40) + 1;
    }

    public static final List<Entity> ENTITIES = IntStream.range(0, 5000)
            .mapToObj(i -> new Entity("Entity" + i, "Entity " + i, new Ref(i)))
            .collect(Collectors.toList());
}
