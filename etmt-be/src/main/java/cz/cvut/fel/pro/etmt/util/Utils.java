package cz.cvut.fel.pro.etmt.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
@Slf4j
public class Utils {

    public static <T> Collection<List<T>> partition(List<T> inputList, int partitionCount) {
        var avgPartSize = inputList.size() / partitionCount;
        var remainder = inputList.size() % avgPartSize;

        var partitions = new ArrayList<List<T>>();
        var partitionSize = avgPartSize;
        for (var i = 0; i < partitionCount; i++) {
            if (i + 1 == partitionCount) {
                partitionSize = avgPartSize + remainder;
            }
            partitions.add(inputList.subList(i * avgPartSize, i * avgPartSize + partitionSize));
        }

        return partitions;
    }

    public static <E> List<E> pickRandom(List<E> list, int n) {
        return new Random().ints(n, 0, list.size()).mapToObj(list::get).collect(Collectors.toList());
    }

    public static void log2DArray(Object[][] array) {
        log.info("\n" + Arrays.deepToString(array)
                .replace("], ", "]\n")
                .replace("[[", "[")
                .replace("]]", "]"));
    }

    public static void log2DList(List<List<Object>> list) {
        log.info("\n" + Arrays.deepToString(list.toArray())
                .replace("], ", "]\n")
                .replace("[[", "[")
                .replace("]]", "]"));
    }

}
