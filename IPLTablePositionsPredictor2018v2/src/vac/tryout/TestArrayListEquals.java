package vac.tryout;

import java.util.*;

import com.sun.javafx.image.IntPixelGetter;

public class TestArrayListEquals {

    public static void main(String args[]) {
        HashSet<Integer> list1 = new HashSet<>();
        list1.add(1);
        list1.add(2);

        HashSet<Integer> list2 = new HashSet<>();
        list2.add(2);
        list2.add(1);
        list2.add(3);

        testListEquals(list1, list2);
    }

    private static void testListEquals(Collection<Integer> list1, Collection<Integer> list2) {
        System.out.println(list1.equals(list2));
    }
}
