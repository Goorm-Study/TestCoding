# Persistence Layer 테스트 코드

https://github.com/imzero238/Item-service/blob/master/src/test/java/com/ecommerce/itemservice/domain/item/repository/ItemRepositoryTest.java

## Test1 Non-unique한 값으로 해당하는 모든 객체 조회

- 검증 기준: 가져온 모든 객체가 같은 값(조회 기준)이어야 함

```java
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByName(String name);  // name 필드 non-unique
}
```
- name 필드는 non-unique 함
- findAllByName 으로 해당하는 모든 객체 조회

### allMatch
```java
@Test
void non_unique_값에_해당하는_모든_상품_조회() {
    // given & when
    final String itemName = "apple";
    final int numberOfItems = 2;
    final long price = 1000L;
    IntStream.range(0, numberOfItems)
            .forEach(i -> {
                Item item = getItem(itemName, (long) i + 100, price);
                itemRepository.save(item);
            });


    // then
    List<Item> items = itemRepository.findAllByName(itemName);
    assertThat(items)
            .hasSize(numberOfItems)  // 2
            .allMatch(item -> item.getName().equals(itemName));
}
```
- apple 객체 2개 생성
- 아이템명이 apple인 객체를 GET

### extracting + containsOnly

```java
assertThat(items)
    .hasSize(numberOfItems)    // 2
    .extracting(Item::getName)  
    .containsOnly(itemName);  // apple
```

## Test2 존재하지 않은 값으로 조회

- 검증 기준: 존재하지 않은 값으로 조회 시 값이 반환되면 안 됨

```java
@Test
void 존재하지_않은_값으로_조회() {
    // given & when
    final String itemName = "apple";
    final int numberOfItems = 2;
    final long price = 1000L;
    IntStream.range(0, numberOfItems)
            .forEach(i -> {
                Item item = getItem(itemName, (long) i + 100, price);
                itemRepository.save(item);
            });

    // then
    final String searchName = "kiwi";
    List<Item> items = itemRepository.findAllByName(searchName);
    assertThat(items).isEmpty();
}
```
- apple 객체 2개 생성
- kiwi 객체를 GET 시도

## Test3 SQL In 절 테스트

```java
List<Item> findAllByNameIn(List<String> name);
```
- 여러 조건에 속하는 모든 객체 조회

```java
@Test
void findAllBy_In_Test () {
    // given
    final String itemName1 = "apple";
    final int numberOfItem1 = 2;
    final long price = 1000L;

    final String itemName2 = "kiwi";
    final int numberOfItem2 = 5;

    List<Item> items = Stream.concat(
                    IntStream.range(0, numberOfItem1)
                            .mapToObj(i -> getItem(itemName1, (long) i + 100, price)),
                    IntStream.range(0, numberOfItem2)
                            .mapToObj(i -> getItem(itemName2, (long) i + 100, price)))
            .toList();

    // when
    itemRepository.saveAll(items);
```
- 아이템명이 apple 인 객체 2개 생성
- 아이템명이 kiwi 인 객체 5개 생성

```java
// then
List<Item> savedItems = itemRepository.findAllByNameIn(List.of(itemName1, itemName2));  // apple, kiwi
assertThat(savedItems)
                .hasSize(numberOfItem1 + numberOfItem2)  // 2(apple) + 5(kiwi)
                .filteredOn(item -> item.getName().equals(itemName1)) // apple
                .hasSize(numberOfItem1);  // 2(apple)

assertThat(savedItems).filteredOn(item -> item.getName().equals(itemName2))  // kiwi
                .hasSize(numberOfItem2);  // 5(kiwi)
```
- 아이템명이 apple, kiwi 인 모든 객체 GET


## Test4 Containing Test

```java
List<Item> findAllByNameContaining(String name);
```
- 아이템명: domesticApple, foreignApple
- Apple 중복

```java
@Test
void findAllBy_Containing_Test() {
    // given
    final String itemName = "Apple";
    final long price = 1000L;

    // 아이템명: domesticApple
    final String countryOfOrigin1 = "domestic";
    final int numberOfItem1 = 2;
    List<Item> items = IntStream.range(0, numberOfItem1)
            .mapToObj(i -> getItem(countryOfOrigin1 + itemName, (long) i + 100, price))
            .collect(Collectors.toList());

    // 아이템명: foreignApple
    final String countryOfOrigin2 = "foreign";
    final int numberOfItem2 = 3;

    items.addAll(IntStream.range(0, numberOfItem2)
            .mapToObj(i -> getItem(countryOfOrigin2 + itemName, (long) i + 100, price))
            .toList());

    // when
    itemRepository.saveAll(items);
```
- domesticApple 객체 2개 생성
- foreignApple 객체 3개 생성

```java
// then
List<Item> savedItems = itemRepository.findAllByNameContaining(itemName);  // itemName: Apple
assertThat(savedItems)
                .hasSize(numberOfItem1 + numberOfItem2)  // 2(domesticApple) + 3(foreignApple)
                .extracting(Item::getName)
                .containsOnly(countryOfOrigin1 + itemName, countryOfOrigin2 + itemName);  // domesticApple, foreignApple

assertThat(savedItems)
                .filteredOn(item -> item.getName().equals(countryOfOrigin2 + itemName))  //  foreignApple
                .hasSize(numberOfItem2);  // 3(foreignApple)
```
- 아이템명에 Apple이 포함되면 GET

## Test5 Between Test

- 특정 가격 범위에 속한 모든 객체 조회

```java
@Test
void findAllBy_Between_Test() {
    // given
    final String itemName1 = "apple";
    final int numberOfItem1 = 2;
    final List<Long> prices1 = List.of(800L, 2000L);

    final String itemName2 = "kiwi";
    final int numberOfItem2 = 3;
    final List<Long> prices2 = List.of(1800L, 2000L, 12000L);

    List<Item> items = Stream.concat(
                    IntStream.range(0, numberOfItem1)
                            .mapToObj(i -> getItem(itemName1, (long) i + 100, prices1.get(i))),
                    IntStream.range(0, numberOfItem2)
                            .mapToObj(i -> getItem(itemName2, (long) i + 100, prices2.get(i))))
            .toList();

    // when
    itemRepository.saveAll(items);
```
- apple 객체 2개 생성 (각 800원, 2000원)
- kiwi 객체 3개 생성 (각 1000원, 2000원, 3000원)

```java
// then
final long lowestPrice = 1000, highestPrice = 2000;
List<Item> savedItems = itemRepository.findAllByPriceBetween(lowestPrice, highestPrice);
long count = Stream.concat(prices1.stream(), prices2.stream())
        .filter(price -> price >= lowestPrice && price <= highestPrice)  
        .count();
assertThat(savedItems.size()).isEqualTo(count);  // 1(apple) + 2(kiwi)

assertThat(savedItems)
                .extracting(Item::getPrice)
                .allMatch(p -> p >= lowestPrice && p <= highestPrice);

assertThat(savedItems)
                .extracting(Item::getName)
                .containsOnly(itemName1, itemName2);  // apple, kiwi
```
- 1000원 이상, 2000원 이하인 모든 객체 GET
- apple 객체는 1개, kiwi 객체는 2개

## Test6 Containing and Between Test

```java
List<Item> findALLByNameContainingAndPriceBetween(String name, long lowestPrice, long highestPrice);
```
- 아이템명에 특정 문자가 포함 && price 데이터가 특정 범위를 만족하는지

```java
@Test
void findAllBy_Containing_and_Between_Test() {
    // given
    final String itemName1 = "apple";
    final int numberOfItem1 = 2;
    final List<Long> prices1 = List.of(800L, 1500L);
    List<Item> items = IntStream.range(0, numberOfItem1)
            .mapToObj(i -> getItem(itemName1, (long) i + 100, prices1.get(i)))
            .collect(Collectors.toList());

    final String itemName2 = "kiwi";
    final int numberOfItem2 = 4;
    final List<Long> prices2 = List.of(1800L, 2000L, 800L, 300L);
    items.addAll(IntStream.range(0, numberOfItem2)
            .mapToObj(i -> getItem(itemName2, (long) i + 100, prices2.get(i)))
            .toList());

    // when
    itemRepository.saveAll(items);
```
- apple 객체 2개 생성 (각 800원, 1500원)
- kiwi 객체 4개 생성 (각 1800원, 2000원, 800원, 300원)

```java
// then
final String itemName1 = "apple";
final long lowestPrice = 800, highestPrice = 1500;

// apple 객체 중 800원 이상, 1500원 이하인 객체 개수
long count = prices1.stream() 
        .filter(price -> price >= lowestPrice && price <= highestPrice)
        .count();

List<Item> savedItems = itemRepository.findALLByNameContainingAndPriceBetween(itemName1, // apple
                                                            lowestPrice, highestPrice);  // 800, 1500

assertThat(savedItems)
                .hasSize((int) count)
                .extracting(Item::getPrice)
                .allMatch(p -> p >= lowestPrice && p <= highestPrice);

assertThat(savedItems)
                .extracting(Item::getName)
                .containsOnly(itemName1);
```
- 아이템명에 apple이 속하면서
- 가격은 800원 이상, 1500원 이하인 객체 GET