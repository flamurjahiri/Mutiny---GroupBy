package org.acme;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.time.Duration;
import java.util.HashMap;

import static org.acme.MockDataHelper.ENTITIES;

public class Main {


    public static void main(String[] args) {

        var mergeFive = Multi.createFrom().iterable(ENTITIES)
                .group().by(i -> i.ref.id).
                onItem()
                .transformToUni(r -> {

                    System.out.printf("Emitting mergeFive item: %s\n", r.key());

                    return r.collect().in(HashMap::new, (a, b) -> {
                        a.computeIfAbsent(b.id, k -> "Random generator " + r.key());
                    });

                }).merge(5)
                .collect()
                .last()
//                .ifNoItem()
//                .after(Duration.ofSeconds(20))
//                .failWith(new RuntimeException("could not emit all items items"))
                .onItemOrFailure()
                .invoke((i, f) -> System.out.printf("mergeFive %s\n", f == null ? "succeeded" : "failed with error: %s".formatted(f.getMessage())));


        var mergeAll = Multi.createFrom().iterable(ENTITIES)
                .group().by(i -> i.ref.id).
                onItem()
                .transformToUni(r -> {

                    System.out.printf("Emitting mergeAll item: %s\n", r.key());

                    return r.collect().in(HashMap::new, (a, b) -> {
                        a.computeIfAbsent(b.id, k -> "Random generator " + r.key());
                    });

                }).merge(Integer.MAX_VALUE)
                .collect()
                .last()
                .onItemOrFailure()
                .invoke((i, f) -> System.out.printf("mergeAll %s\n", f == null ? "succeeded" : "failed"));


        Uni.combine().all().unis(mergeFive, mergeAll).withUni((a, c) -> {
            System.out.print("all completed");
            return Uni.createFrom().voidItem();
        }).await().indefinitely();


    }


}
