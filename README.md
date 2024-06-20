# [Redis] Redis with Spring Boot

## Redis 簡介

Redis是一種開源的，基於內存的資料結構儲存系統，也就是在RAM（隨機存取記憶體）中儲存和操作資料。RAM是一種揮發性記憶體，這意味著當電源關閉時，其中的資料會被清除。然而，由於RAM可以提供非常快速的讀寫速度，因此Redis可以提供高效能的資料存取。

這與傳統的基於磁碟的資料庫（如MySQL或PostgreSQL）有所不同，傳統的資料庫主要在磁碟上儲存資料，雖然磁碟的讀寫速度比RAM慢，但磁碟是非揮發性的，即使在電源關閉後，資料仍然可以保留。

### 資料結構

Redis支援多種資料結構，包括：

- 字串（Strings）：最基本的資料結構，可以儲存任何形式的字串，包括整數和浮點數。
- 列表（Lists）：一種有序的字串集合，可以在列表的頭部或尾部添加元素。
- 集合（Sets）：一種無序且不重複的字串集合。
- 有序集合（Sorted sets）：與集合類似，但每個元素都會關聯一個分數，Redis根據分數對元素進行排序。
- 哈希（Hashes）：一種字串對字串的映射，適合儲存物件。

然而後期也發展出了 Redis stack，也新增了一些資料結構，包括：

- 串流(Streams)：作用類似於僅附加記錄檔。串流有助於按事件發生的順序記錄事件，然後將其聯合處理。
- 地理空間索引(Geospatial)：可用於尋找特定地理半徑或邊界框內的位址。
- 位元圖(Bitmaps)：讓你可以在字串上執行位元運算。
- 位元欄位(Redis bitfields)：可以有效率地對字串值編碼多個計數器。位元欄位提供原子取得、設定和遞增運算，並支援不同的溢位政策。
- HyperLogLog：資料結構提供大型集合基數（即元素數量）的機率估計。

雖然很重要，但本篇主要會把重點放在跟 Spring Boot 整合 Redis 的部分，如果想了解更多的話可以參考[Redis英文官方文件](https://redis.io/docs/latest/develop/data-types/)，或是[Redis中文官方文件](https://redis.dev.org.tw/docs/data-types/)。

### 持久化

Redis提供了兩種持久化方法：

- RDB（Redis Database）：
    在指定的時間內，將資料集快照儲存到磁碟上。例如，可以設定每5分鐘進行一次快照儲存，將資料集儲存到磁碟上。RDB持久化是將Redis在記憶體中的資料集序列化到磁碟上的一個二進制文件中。
    - 優點：資料恢復快速。
    - 缺點：如果Redis意外關閉，可能會丟失最後一次快照之後的資料。例如，當設定為每5分鐘進行一次快照，當上一次快照結束後，又經過了4分鐘，還沒有進行下一次快照，此時Redis意外關閉，則這4分鐘內的資料將會丟失。
- AOF（Append Only File）：記錄從服務器接收到的每一條寫命令，並將這些命令記錄到磁碟上的日誌文件中。當Redis重新啟動時，可以通過重新執行這些命令來恢復資料集。
    - 優點：資料恢復更可靠。
    - 缺點：AOF日誌文件通常比RDB快照文件大，因此AOF持久化的恢復速度可能比RDB持久化慢。

### 應用場景

快取：由於Redis的高速讀寫性能，它常常被用作應用的快取層，來加速應用的響應速度。
任務隊列：Redis的列表和有序集合資料結構非常適合實現任務隊列。
發布/訂閱：Redis支援發布/訂閱模式，可以用於實現實時訊息系統。
計數器：Redis的INCR和DECR命令可以用來實現計數器功能。

## 使用 Docker 安装 Redis(單體)

Redis官方提供了一個Redis的Docker映像，可以通過Docker Hub下載。要安裝Redis，只需運行以下命令：

```bash
docker run --name my-redis -d -p 6379:6379 --name <your-redis-container-name> redis
```

- 因為我不想使用 Redis 原生的 CLI，而且 Redis 後來來有推出一個可以用來檢視 Redis 的 GUI 工具，叫做 RedisInsight，可以參考[RedisInsight](https://redis.io/docs/latest/operate/redisinsight/install/)。可以使用 Docker 安裝，非常方便。我們直接使用 Docker 安裝 RedisInsight：

```bash
docker run -d --name redisinsight -p 5540:5540 --name <your-redisinsight-container-name> redis/redisinsight:latest
```

- 我把以上的 redis 和 redisinsight 用 docker compose 包起來，這樣我直接在專案目錄底下使用 `docker-compose up -d` 就可以一次啟動兩個容器了。

```yaml
version: '3.8'

services:
  redis:
    image: redis
    container_name: my-redis
    ports:
      - "6379:6379"

  redisinsight :
    image: redis/redisinsight:latest
    container_name: my-redisinsight
    ports:
      - "5540:5540"
    depends_on:
      - redis
```

- 然後我們可以透過瀏覽器，輸入 `http://localhost:5540` 來進入 RedisInsight 的介面，使用上都非常直覺，我就不介紹囉。

> 不過要注意，如果你的 Redis 是使用 Docker 安裝，且 RedisInsight 也是使用 Docker 安裝在本機上，則 RedisInsight 連接 Redis 的時候，host 要填寫 `host.docker.internal`，而不是 `localhost`。主要是因為 Docker 的網路問題，我們就不在這邊詳細說明了。有興趣的話可以參考[這篇文章](https://docs.docker.com/desktop/networking/#use-cases-and-workarounds)或是[這篇文章](https://docs.docker.com/network/)。

## Spring boot 整合 Redis

我們就不廢話，直接來囉!

1. 先到 [Spring Initializr](https://start.spring.io/) 創建一個新的 Spring Boot 專案，
    - Project: Maven Project
    - Language: Java
    - Spring Boot: 3.3.0
    - Packaging: Jar
    - Java: 17
    - Dependencies 選擇 `Spring Web` 、`Spring Data Redis`、`Spring Boot DevTools`、`Lombok`，然後點擊 `Generate` 下載專案。
2. 解壓縮下載的專案，然後使用 IntelliJ IDEA 或是 Eclipse 打開專案。
3. 到 `pom.xml` 檔案中加入 `swagger` 的相關依賴，方便我們測試 API。
```xml
<!--swagger-->
<dependency>
   <groupId>org.springdoc</groupId>
   <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
   <version>2.0.2</version>
</dependency>
```
4. 建立一個 `Controller` 類別，並且加上 `@RestController` 註解，並注入 `MyService` 類別。
```java
@RestController
public class MyController {
    
        private final MyService myService;
    
        public MyController(MyService myService) {
            this.myService = myService;
        }
}
```
5. 建立一個 'MyService' 類別，並且加上 `@Service` 註解，並注入 `StringRedisTemplate` 類別。
```java
@Service
public class MyService {

    private final StringRedisTemplate stringRedisTemplate;

    public MyService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
}
```
6. 開一個資料夾叫做 `config`，然後建立 `SwaggerConfig` 類別，並加上 `@Configuration` 註解。
```java
@OpenAPIDefinition(
        info = @Info(
                title = "Spring Boot integration with single node Redis practice",
                version = "0.0"
        )
)
@Configuration
public class SwaggerConfig {
}
```
7. 到 `application.properties` 檔案中加入 Redis 的連線設定。
```properties
spring.redis.host=localhost
spring.redis.port=6379
```
8. 目前我們的資料夾結構應該是這樣的：
```
src
├── main
│   ├── java
│   │   ├── com
│   │   │   ├── kai
│   │   │   │   ├── spring_boot_redis_practice
│   │   │   │   │   ├── config
│   │   │   │   │   │   └── SwaggerConfig.java
│   │   │   │   │   ├── MyController.java
│   │   │   │   │   └── MyService.java
│   │   │   │   └── SpringBootRedisPracticeApplication.java
│   │   └── resources
│   │       ├── application.properties
下略
```
9. 到瀏覽器輸入 `localhost:5540` 進入前面開好的 RedisInsight，方便我們觀察 Redis 的資料。
10. 再到瀏覽器輸入 `localhost:8080/swagger-ui.html` 進入 Swagger 的介面，方便我們測試 API。

這樣所有基礎需求都寫完了，接下來我們就來實作一些 Redis 的基本操作吧~

> 為了方便，以下我們就都用 `get` Method 來操作 Redis。

## Spring Data Redis 的基本操作

### CRUD（用 Strings 舉例）

- 新增一個字串到 Redis 中。
    - Controller
    ```java
    @Operation(summary = "Save", description = "Save a key-value pair")
    @Tag(name = "Key-Value")
    @GetMapping("/save")
    public void save(@Parameter(description = "The key") String key, @Parameter(description = "The value") String value) {
        service.save(key, value);
    }
    ```
    - Service
    ```java
    public void save(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }
    ```
- 從 Redis 中取得一個字串。
    - Controller
    ```java
    @Operation(summary = "Get", description = "Gets a value by key")
    @Tag(name = "Key-Value")
    @GetMapping("/get")
    public String get(@Parameter(description = "The key") String key) {
        return service.get(key);
    }
    ```
    - Service
    ```java
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
    ```
- 更新 Redis 中的一個字串。其實就是新增一個字串，只是 key 已經存在。所以跟新增一個字串的方法一樣。
    - Controller
    ```java
    @Operation(summary = "Update", description = "Update a key-value pair")
    @Tag(name = "Key-Value")
    @GetMapping("/update")
    public void update(@Parameter(description = "The key") String key, @Parameter(description = "The value") String value) {
        service.update(key, value);
    }
    ```
    - Service
    ```java
    public void update(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }
    ```
- 刪除 Redis 中的一個字串。
    - Controller
    ```java
    @Operation(summary = "Delete", description = "Deletes a key-value pair")
    @Tag(name = "Key-Value")
    @GetMapping("/delete")
    public void delete(String key) {
        service.delete(key);
    }
    ```
    - Service
    ```java
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }
    ```
- 是否存在 Redis 中的一個字串。
    - Controller
    ```java
    @Operation(summary = "Exists", description = "Checks if a key exists")
    @Tag(name = "Key-Value")
    @GetMapping("/exists")
    public boolean exists(String key) {
        return service.exists(key);
    }
    ```
    - Service
    ```java
    public boolean exists(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }
    ```
- 儲存時，設定TTL（Time To Live），也就是 expire time。
    - Controller
    ```java
    @Operation(summary = "Get Expire", description = "Gets the expiration time of a key")
    @Tag(name = "Key-Value")
    @GetMapping("/getExpire")
    public long getExpire(String key) {
        return service.getExpire(key);
    }
    ```
    - Service
    ```java
    public void saveWithExpire(String key, String value, long seconds) {
        stringRedisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }
    ```
- 取得 TTL（Time To Live）。
    - Controller
    ```java
   @Operation(summary = "Get Expire", description = "Gets the expiration time of a key")
    @Tag(name = "Key-Value")
    @GetMapping("/getExpire")
    public long getExpire(String key) {
        return service.getExpire(key);
    }
    ```
    - Service
    ```java
    public long getExpire(String key) {
        Optional<Long> duration = Optional.ofNullable(stringRedisTemplate.getExpire(key)); // 因為 getExpire() 回傳時，可能已經過期了，所以用 Optional 來處理。
        return duration.orElse(0L); // 如果 duration 是 null(已經過期所以拿不到)，則回傳 0。
    }
    ```
- 如果找不到 key，則新增一個字串到 Redis 中。如果找的到 key，則不做任何事。
    - Controller
    ```java
    @Operation(summary = "Save If Absent", description = "Save a key-value pair if the key does not exist")
    @Tag(name = "Key-Value")
    @GetMapping("/saveIfAbsent")
    public void saveIfAbsent(String key, String value) {
        service.saveIfAbsent(key, value);
    }
    ```
    - Service
    ```java
    public void saveIfAbsent(String key, String value) {
        stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }
    ```
- 計數器-增加(Increment)。
    - Controller
    ```java
    @Operation(summary = "Increment", description = "Increments a key by a delta")
    @Tag(name = "Counter")
    @GetMapping("/increment")
    public void increment(String key, long delta) {
        service.increment(key, delta);
    }
    ```
    - Service
    ```java
    public void increment(String key, long delta) {
        stringRedisTemplate.opsForValue().increment(key, delta);
    }
    ```
- 計數器-減少(Decrement)。
    - Controller
    ```java
    @Operation(summary = "Decrement", description = "Decrements a key by a delta")
    @Tag(name = "Counter")
    @GetMapping("/decrement")
    public void decrement(String key, long delta) {
        service.decrement(key, delta);
    }
    ```
    - Service
    ```java
    public void decrement(String key, long delta) {
        stringRedisTemplate.opsForValue().decrement(key, delta);
    }
    ```
- 把新的字串加到舊的字串後面，假設我原本的字串是 `Hello`，我想要加上 `World`，則結果就是 `HelloWorld`。
    - Controller
    ```java
    @Operation(summary = "Append", description = "Appends a value to a key")
    @Tag(name = "String")
    @GetMapping("/append")
    public long append(String key, String value) {
        return service.append(key, value);
    }
    ```
    - Service
    ```java
    public long append(String key, String value) {
        return stringRedisTemplate.opsForValue().append(key, value); // 可以由回傳值來得知新字串的長度。
    }
    ```
- 使用 Range 取得字串的子字串。例如，我有一個字串， key 是 `hw`，value 是 `HelloWorld`，我想要取得 `World`，則可以使用 `range("hw", 5, 10)`。
    - Controller
    ```java
    @Operation(summary = "Get Range", description = "Gets a range of values from a key")
    @Tag(name = "Other")
    @GetMapping("/getRange")
    public String getRange(String key, long start, long end) {
        return service.getRange(key, start, end);
    }
    ```
    - Service
    ```java
    public String getRange(String key, long start, long end) {
        return stringRedisTemplate.opsForValue().get(key, start, end);
    }
    ```
- 使用 Range 去取代字串中的子字串。例如，我有一個字串， key 是 `hw`，value 是 `HelloWorld`，我使用了`setRange("hw", 0, "Hi")`。則結果就是 `HilloWorld`。
    - Controller
    ```java
    @Operation(summary = "Set Range", description = "Sets a range of values to a key")
    @Tag(name = "Other")
    @GetMapping("/setRange")
    public void setRange(String key, String value, long offset) {
        service.setRange(key, value, offset);
    }
    ```
    - Service
    ```java
    public void setRange(String key, String value, long offset) {
        stringRedisTemplate.opsForValue().set(key, value, offset);
    }
    ```
- 如果我想要一次取得多個 key 的 value
    - Controller
    ```java
    @Operation(summary = "Multi Get", description = "Gets multiple values by key")
    @Tag(name = "Key-Value")
    @GetMapping("/multiGet")
    public List<String> multiGet(String key1, String key2) {
        return service.multiGet(key1, key2);
    }

    ```
    - Service
    ```java
    public List<String> multiGet(String key1, String key2) {
        List<String> keys = new ArrayList<>();
        keys.add(key1);
        keys.add(key2);
        return stringRedisTemplate.opsForValue().multiGet(keys);
    }
    ```
- 在物件導向的 Java 中，我們通常會使用物件來儲存資料，但 Redis 是一個 key-value 的資料庫，所以我們可以使用 `Hash` 來儲存物件。
    - Controller
    ```java
    @Operation(summary = "Save Hash", description = "Save a hash")
    @Tag(name = "Hash")
    @GetMapping("/saveHash")
    public void saveHash(String key, String name, String description, Integer likes, Integer visitors) {
        service.saveHash(key, name, description, likes, visitors);
    }
    ```
    - Service
    ```java
    public void saveHash(String key, String name, String description, Integer likes, Integer visitors) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("description", description);
        map.put("likes", likes.toString());
        map.put("visitors", visitors.toString());
        stringRedisTemplate.opsForHash().putAll(key, map);
    }
    ```
- 取得 Hash 中的所有欄位。
    - Controller
    ```java
    @Operation(summary = "Get hash", description = "Gets a hash by key")
    @Tag(name = "Hash")
    @GetMapping("/getHash")
    public Map<Object, Object> getHash(String key) {
        return service.getHash(key);
    }
    ```
    - Service
    ```java
    public Map<Object, Object> getHash(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }
    ```
- 取得 Hash 中的某個欄位。
    - Controller
    ```java
    @Operation(summary = "Get a field value from a hash", description = "Gets a field value from a hash")
    @Tag(name = "Hash")
    @GetMapping("/getHashFieldValue")
    public String getHashFieldValue(String key, String field) {
        return service.getHashValue(key, field);
    }
    ```
    - Service
    ```java
    public String getHashValue(String key, String field) {
        return (String) stringRedisTemplate.opsForHash().get(key, field);
    }
    ```

### 列表（Lists）

- 新增一個列表到 Redis 中。如果你有一個列表想要加到現有的列表後面，也一樣使用這個方法。
    - Controller
    ```java
    @Operation(summary = "Save a list", description = "Save a list")
    @Tag(name = "List")
    @PostMapping("/saveList")
    public void saveList(String key, @RequestBody ArrayList<String> value) {
        service.saveList(key, value);
    }
    ```
    - Service
    ```java
    public void saveList(String key, ArrayList<String> values) {
        stringRedisTemplate.opsForList().rightPushAll(key, values);
    }
    ```
- 取得列表中的所有值。
    - Controller
    ```java
    @Operation(summary = "Get a list", description = "Gets a list by key")
    @Tag(name = "List")
    @GetMapping("/getList")
    public List<String> getList(String key) {
        return service.getList(key);
    }
    ```
    - Service
    ```java
    public List<String> getList(String key) {
        return stringRedisTemplate.opsForList().range(key, 0, -1);
    }
    ```
- 新增一個值到列表的尾部
    - Controller
    ```java
    @Operation(summary = "Add a value to the end of a list", description = "Add a value to the end of a list")
    @Tag(name = "List")
    @GetMapping("/addAValueToEndOfList")
    public void addAValueToEndOfList(String key, String value) {
        service.addAValueToEndOfList(key, value);
    }
    ```
    - Service
    ```java
    public void addAValueToEndOfList(String key, String value) {
        stringRedisTemplate.opsForList().rightPush(key, value);
    }
    ```
- 新增一個值到列表的頭部
    - Controller
    ```java
    @Operation(summary = "Add a value to the beginning of a list", description = "Add a value to the beginning of a list")
    @Tag(name = "List")
    @GetMapping("/addAValueToBeginningOfList")
    public void addAValueToBeginningOfList(String key, String value) {
        service.addAValueToBeginningOfList(key, value);
    }
    ```
    - Service
    ```java
    @Operation(summary = "Add a value to the beginning of a list", description = "Add a value to the beginning of a list")
    @Tag(name = "List")
    @GetMapping("/addAValueToBeginningOfList")
    public void addAValueToBeginningOfList(String key, String value) {
        service.addAValueToBeginningOfList(key, value);
    }
    ```
- 從列表的尾部 pop 取得一個值，並且於列表中移除。
    - Controller
    ```java
    @Operation(summary = "Pop a value from the end of a list", description = "Pop a value from the end of a list. After popping, the value is removed from the list.")
    @Tag(name = "List")
    @GetMapping("/popAValueFromEndOfList")
    public String popAValueFromEndOfList(String key) {
        return service.popAValueFromEndOfList(key);
    }
    ```
    - Service
    ```java
    public String popAValueFromEndOfList(String key) {
        return stringRedisTemplate.opsForList().rightPop(key);
    }
    ```
- 從列表的頭部 pop 取得一個值，並且於列表中移除。
    - Controller
    ```java
    @Operation(summary = "Pop a value from the beginning of a list", description = "Pop a value from the beginning of a list. After popping, the value is removed from the list.")
    @Tag(name = "List")
    @GetMapping("/popAValueFromBeginningOfList")
    public String popAValueFromBeginningOfList(String key) {
        return service.popAValueFromBeginningOfList(key);
    }
    ```
    - Service
    ```java
    public String popAValueFromBeginningOfList(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }
    ```
- 取得某個 index 的值。
    - Controller
    ```java
   @Operation(summary = "Get a value from a list by index", description = "Get a value from a list by index")
    @Tag(name = "List")
    @GetMapping("/getAValueFromListByIndex")
    public String getAValueFromListByIndex(String key, long index) {
        return service.getAValueFromListByIndex(key, index);
    }
    ```
    - Service
    ```java
    public String getAValueFromListByIndex(String key, long index) {
        return stringRedisTemplate.opsForList().index(key, index);
    }
    ```
- 以 index 從列表中移除某個值。
    - Controller
    ```java
    @Operation(summary = "Remove a value from a list by index", description = "Remove a value from a list by index")
    @Tag(name = "List")
    @GetMapping("/removeAValueFromListByIndex")
    public void removeAValueFromListByIndex(String key, long index, String value) {
        service.removeAValueFromListByIndex(key, index, value);
    }
    ```
    - Service
    ```java
    public void removeAValueFromListByIndex(String key, long index, String value) {
        stringRedisTemplate.opsForList().remove(key, index, value);
    }
    ```
- 移除列表
    - Controller
    ```java
    @Operation(summary = "Remove a list", description = "Remove a list")
    @Tag(name = "List")
    @GetMapping("/removeList")
    public void removeList(String key) {
        service.removeList(key);
    }
    ```
    - Service
    ```java
    public void removeList(String key) {
        stringRedisTemplate.delete(key);
    }
    ```

### 集合（Sets）

- 新增一個集合到 Redis 中。
    - Controller
    ```java
    @Operation(summary = "Save a set", description = "Save a set")
    @Tag(name = "Set")
    @PostMapping("/saveSet")
    public void saveSet(String key, @RequestBody ArrayList<String> value) {
        service.saveSet(key, value);
    }
    ```
    - Service
    ```java
    public void saveSet(String key, ArrayList<String> value) {
        stringRedisTemplate.opsForSet().add(key, value.toArray(new String[0]));
    }
    ```
- 取得集合中的所有值。
    - Controller
    ```java
    @Operation(summary = "Get a set", description = "Gets a set by key")
    @Tag(name = "Set")
    @GetMapping("/getSet")
    public List<String> getSet(String key) {
        return service.getSet(key);
    }
    ```
    - Service
    ```java
    public List<String> getSet(String key) {
        return new ArrayList<>(Objects.requireNonNull(stringRedisTemplate.opsForSet().members(key)));
    }
    ```
- 從集合中移除某個值。
    - Controller
    ```java
    @Operation(summary = "Remove a value from a set", description = "Remove a value from a set")
    @Tag(name = "Set")
    @GetMapping("/removeAValueFromSet")
    public void removeAValueFromSet(String key, String value) {
        service.removeAValueFromSet(key, value);
    }
    ```
    - Service
    ```java
    public void removeAValueFromSet(String key, String value) {
        stringRedisTemplate.opsForSet().remove(key, value);
    }
    ```
- 比較兩個集合的差異。
    - Controller
    ```java
   @Operation(summary = "Compare two sets and get the difference", description = "Compare two sets and get the difference")
    @Tag(name = "Set")
    @GetMapping("/difference")
    public Set<String> difference(String key1, String key2) {
        return service.difference(key1, key2);
    }
    ```
    - Service
    ```java
    public Set<String> difference(String key1, String key2) {
        return stringRedisTemplate.opsForSet().
    }
    ```
- Spring data redis 提供給 Set 的方法還有很多，例如 `union`、`intersect`、`difference`、`isMember`、`size`、`members` 等等，這邊就不一一列舉了。有用到再查就好囉!

### 有序集合（Sorted sets）- 暫時略過

### 哈希（Hashes）

- 新增一個 hash 到 Redis 中。
    - Controller
    ```java
    @Operation(summary = "Save Hash", description = "Save a hash")
    @Tag(name = "Hash")
    @GetMapping("/saveHash")
    public void saveHash(String key, String name, String description, Integer likes, Integer visitors) {
        service.saveHash(key, name, description, likes, visitors);
    }
    ```
    - Service
    ```java
    public void saveHash(String key, String name, String description, Integer likes, Integer visitors) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("description", description);
        map.put("likes", likes.toString());
        map.put("visitors", visitors.toString());
        stringRedisTemplate.opsForHash().putAll(key, map);
    }
    ```
- 取得 hash 中的所有欄位。
    - Controller
    ```java
    @Operation(summary = "Get hash", description = "Gets a hash by key")
    @Tag(name = "Hash")
    @GetMapping("/getHash")
    public Map<Object, Object> getHash(String key) {
        return service.getHash(key);
    }
    ```
    - Service
    ```java
    public Map<Object, Object> getHash(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }
    ```
- 取得 hash 中的某個欄位。
    - Controller
    ```java
    @Operation(summary = "Get a field value from a hash", description = "Gets a field value from a hash")
    @Tag(name = "Hash")
    @GetMapping("/getHashFieldValue")
    public String getHashFieldValue(String key, String field) {
        return service.getHashValue(key, field);
    }
    ```
    - Service
    ```java
    public String getHashValue(String key, String field) {
        return (String) stringRedisTemplate.opsForHash().get(key, field);
    }
    ```
- 刪除 hash 中的某個欄位。
    - Controller
    ```java
    @Operation(summary = "Delete a field from a hash", description = "Delete a field from a hash")
    @Tag(name = "Hash")
    @GetMapping("/deleteHashField")
    public void deleteHashField(String key, String field) {
        service.deleteHashField(key, field);
    }
    ```
    - Service
    ```java
    public void deleteHashField(String key, String field) {
        stringRedisTemplate.opsForHash().delete(key, field);
    }
    ```
- 判斷 hash 中是否存在某個欄位。
    - Controller
    ```java
    @Operation(summary = "Exists a field in a hash", description = "Exists a field in a hash")
    @Tag(name = "Hash")
    @GetMapping("/existsHashField")
    public boolean existsHashField(String key, String field) {
        return service.existsHashField(key, field);
    }
    ```
    - Service
    ```java
    public boolean existsHashField(String key, String field) {
        return stringRedisTemplate.opsForHash().hasKey(key, field);
    }
    ```
- 刪除 hash。
    - Controller
    ```java
    @Operation(summary = "Delete a hash", description = "Delete a hash")
    @Tag(name = "Hash")
    @GetMapping("/deleteHash")
    public void deleteHash(String key) {
        service.deleteHash(key);
    }
    ```
    - Service
    ```java
    public void deleteHash(String key) {
        stringRedisTemplate.delete(key);
    }
    ```

### 串流（Streams）- 暫時略過

### 地理空間索引（Geospatial）- 暫時略過

### 位元圖（Bitmaps）- 暫時略過

### 位元欄位（Redis bitfields）- 暫時略過

### HyperLogLog- 暫時略過

## 使用 Lua 脚本執行 Redis 命令

當我們在使用 Spring boot 整合 Redis 時，有時會需要執行一些複雜的 Redis 命令操作，如果我們都使用 Spring data Redis 提供的方法，會遇到幾個問題：
- 需要多次進出 Redis，破壞了原子性。
- 需要多次網路請求，增加了網路延遲與開銷。
- 邏輯過於複雜，無法使用 Spring data Redis 提供的方法。
那麼，這時候我們可以使用 Lua 脚本來執行 Redis 命令，Lua 脚本可以保證原子性，並且可以減少網路請求。

那我們要怎麼在 Spring boot 中使用 Lua 脚本呢？我們可以使用 `StringRedisTemplate` 的 `execute` 方法來執行 Lua 脚本。

1. 先新開一個 'LuaController' 類別，並且加上 `@RestController` 註解，並注入 `LuaService` 類別。
```java
@RestController
public class LuaController {

    public final LuaService luaService;

    public LuaController(LuaService luaService) {
        this.luaService = luaService;
    }
    
}
```
2. 新開一個 'LuaService' 類別，並且加上 `@Service` 註解，並注入 `StringRedisTemplate` 類別。
```java
@Service
public class LuaService {

    private final StringRedisTemplate stringRedisTemplate;

    private final DefaultRedisScript<String> redisScript; 

    public LuaService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        redisScript = new DefaultRedisScript<>();
    }
}
```
3. 在存放靜態資源的資料夾 `resources` 中，新增一個 Lua 脚本檔案，例如 `test.lua`。
4. 在 `test.lua` 中寫入 Lua 脚本。
```lua
-- 設置一個鍵值對
redis.call('SET', 'mykey', 'myvalue')

-- 獲取鍵的值
local value = redis.call('GET', 'mykey')

-- 返回鍵的值
return value
```
5. 在 `LuaService` 類別中新增一個方法，用來執行 Lua 脚本。
```java
    public String executeLuaScript() {
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("test.lua")));
        redisScript.setResultType(String.class);
        return stringRedisTemplate.execute(redisScript, Collections.emptyList());
    }
```
6. 在 `LuaController` 類別中新增一個方法，用來執行 Lua 脚本。
```java
    @Operation(summary = "Execute Lua script", description = "Execute Lua script")
    @Tag(name = "Lua")
    @GetMapping("/executeLuaScript")
    public String executeLuaScript() {
        return luaService.executeLuaScript();
    }
```

7. 最後我們就到我們的 Swagger 介面中，測試我們的 API 吧!觸發以後就會回傳 `myvalue` 囉!
8. 也可以到我們的 RedisInsight 中，查看是否有成功設置 `mykey` 的值。

## Redis 的 Pub/Sub 模式

## 持久化

## 使用 Docker 安装 Redis Cluster

## Spring boot 整合 Redis Cluster

## Redis Cluster 的基本操作

## Redis 的分布式鎖

## Reference
- [https://redis.dev.org.tw/docs](https://redis.dev.org.tw/docs)
- [https://redis.io/docs](https://redis.io/docs)
