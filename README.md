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

### 持久畫

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

或是可以使用 docker-compose.yml 檔案來啟動 Redis 服務：

```yaml
version: '3.8'

services:
  redis:
    image: redis
    container_name: my-redis
    ports:
      - "6379:6379"
```

- 因為我不想使用 Redis 原生的 CLI，而且 Redis 後來來有推出一個可以用來檢視 Redis 的 GUI 工具，叫做 RedisInsight，可以參考[RedisInsight](https://redis.io/docs/latest/operate/redisinsight/install/)。可以使用 Docker 安裝，非常方便。我們直接使用 Docker 安裝 RedisInsight：

```bash
docker run -d --name redisinsight -p 5540:5540 --name <your-redisinsight-container-name> redis/redisinsight:latest
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

接下來我們就來實作一些 Redis 的基本操作吧~

> 為了方便，以下我們就都用 `get` Method 來操作 Redis，當然你也可以用 `post` Method 來操作。

## Spring Data Redis 的基本操作

### 字串（Strings）

1. 新增一個字串到 Redis 中。

### 列表（Lists）

### 集合（Sets）

### 有序集合（Sorted sets）

### 哈希（Hashes）

### 串流（Streams）- 暫時略過

### 地理空間索引（Geospatial）- 暫時略過

### 位元圖（Bitmaps）- 暫時略過

### 位元欄位（Redis bitfields）- 暫時略過

### HyperLogLog- 暫時略過

## 使用 Lua 脚本執行 Redis 命令

## Redis 的 Pub/Sub 模式

## 持久化

## 使用 Docker 安装 Redis Cluster

## Spring boot 整合 Redis Cluster

## Redis Cluster 的基本操作

## Redis 的分布式鎖

## Reference
- [https://redis.dev.org.tw/docs](https://redis.dev.org.tw/docs)
- [https://redis.io/docs](https://redis.io/docs)
