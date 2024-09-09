package sample.cafekiosk.learning;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class GuavaLearningTest {

    @DisplayName("주어진 개수만큼 List를 파티셔닝한다.")
    @Test
    void partitionLearningTest() {
     //given
        List<Integer> integers = List.of(1,2,3,4,5,6);

     //when
        List<List<Integer>> partition = Lists.partition(integers, 3);

        //then
        assertThat(partition).hasSize(2)
                .isEqualTo(List.of(
                        List.of(1,2,3), List.of(4,5,6)
                ));
    }

    @DisplayName("주어진 개수만큼 List를 파티셔닝한다.")
    @Test
    void partitionLearningTest2(){
        //given
        List<Integer> integers = List.of(1,2,3,4,5,6);

        //when
        List<List<Integer>> partition = Lists.partition(integers, 4);

        //then
        assertThat(partition).hasSize(2)
                .isEqualTo(List.of(
                        List.of(1,2,3,4), List.of(5,6)
                ));
    }

    @DisplayName("멀티맵 기능 확인")
    @Test
    void multiMapLearningTest(){
     //given
        Multimap<String, String> multimap = ArrayListMultimap.create();
        // multimap은 하나의 키에 여러 개의 값을 넣을 수 있다.
        multimap.put("coffee", "아메리카노");
        multimap.put("coffee", "카페라떼");
        multimap.put("coffee", "카푸치노");
        multimap.put("bakery", "크루아상");
        multimap.put("bakery", "식빵");


     //when
        Collection<String> strings = multimap.get("coffee");

     //then
        assertThat(strings).hasSize(3)
                .isEqualTo(List.of("아메리카노", "카페라떼", "카푸치노"));
    }

    @DisplayName("멀티맵 기능 확인")
    @TestFactory
    Collection<DynamicTest> multiMapLearningTest2(){
        //given
        Multimap<String, String> multimap = ArrayListMultimap.create();
        // multimap은 하나의 키에 여러 개의 값을 넣을 수 있다.
        multimap.put("coffee", "아메리카노");
        multimap.put("coffee", "카페라떼");
        multimap.put("coffee", "카푸치노");
        multimap.put("bakery", "크루아상");
        multimap.put("bakery", "식빵");

        return List.of(
                DynamicTest.dynamicTest("1개 value 삭제", () -> {
                    //when
                    multimap.remove("coffee", "카푸치노");
                    //then
                    Collection<String> results = multimap.get("coffee");
                    assertThat(results).hasSize(2)
                            .isEqualTo(List.of("아메리카노", "카페라떼"));
                }),
                DynamicTest.dynamicTest("1개 key 삭제", () -> {
                    //when
                    multimap.removeAll("coffee");
                    //then
                    Collection<String> results = multimap.get("coffee");
                    assertThat(results).isEmpty();
                })
        );
    }
}
