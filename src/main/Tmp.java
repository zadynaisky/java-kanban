package main;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Tmp {
    public static void main(String[] args) {
        List<LocalDateTime> list = new ArrayList<>();
        list.add(LocalDateTime.parse("2025-01-01T00:00:00"));
        list.add(LocalDateTime.parse("2025-01-02T00:00:00"));
        list.add(LocalDateTime.parse("2025-01-03T00:00:00"));

        list.stream().min(Comparator.naturalOrder()).ifPresent(System.out::println);
    }
}
