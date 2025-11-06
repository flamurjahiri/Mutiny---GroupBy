package org.acme;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Main {

    public static final List<String> ENTITIES = IntStream.range(0, 5000).mapToObj(i -> "Entity " + i).collect(Collectors.toList());


    public static void main(String[] args) {

        var mergeFive = Multi.createFrom().iterable(ENTITIES)
                .group().by(i -> i.split(" ")[1])
                .onItem().transformToUni(r -> {

                    System.out.printf("Emitting mergeFive item: %s\n", r.key());

                    return r.collect().in(HashMap::new, (a, b) -> {
                        a.computeIfAbsent(b, k -> "Random generator " + r.key());
                    });

                }).merge(5).collect().last()
//                .ifNoItem()
//                .after(Duration.ofSeconds(20))
//                .failWith(new RuntimeException("could not emit all items items"))
                .onItemOrFailure().invoke((i, f) -> System.out.printf("mergeFive %s\n", f == null ? "succeeded" : "failed with error: %s".formatted(f.getMessage())));


        var mergeAll = Multi.createFrom().iterable(ENTITIES)
                .group().by(i -> i.split(" ")[1])
                .onItem().transformToUni(r -> {

                    System.out.printf("Emitting mergeAll item: %s\n", r.key());

                    return r.collect().in(HashMap::new, (a, b) -> {
                        a.computeIfAbsent(b, k -> "Random generator " + r.key());
            });
        }).merge(Integer.MAX_VALUE).collect().last().onItemOrFailure().invoke((i, f) -> System.out.printf("mergeAll %s\n", f == null ? "succeeded" : "failed"));


        Uni.combine().all().unis(mergeFive, mergeAll).withUni((a, c) -> {
            System.out.print("all completed");
            return Uni.createFrom().voidItem();
        }).await().indefinitely();


    }


}
