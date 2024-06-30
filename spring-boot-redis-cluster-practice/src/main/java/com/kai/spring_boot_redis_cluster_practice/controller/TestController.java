package com.kai.spring_boot_redis_cluster_practice.controller;

import com.kai.spring_boot_redis_cluster_practice.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @Operation(
            summary = "Test sharding",
            description = """
            數據分片 (Data Sharding)
            
            Redis Cluster 使用 16384 個哈希槽來分配數據。我們可以測試數據是否確實分佈在不同的節點上。
            
            測試案例：
            
            寫入大量數據
            檢查每個節點的數據分佈
            """)
    @GetMapping("/test-sharding")
    public Map<String, Long> testSharding(@Parameter(description = "The number of keys to write") @RequestParam(defaultValue = "1000") int keyCount) {
        return testService.testSharding(keyCount);
    }
//
    @Operation(
            summary = "Test availability、Automatic Failover Step 1",
            description = """
            高可用性 (High Availability)，自動故障轉移 (Automatic Failover)

            Redis Cluster 通過主從複制確保高可用性。我們可以測試當一個主節點故障時，系統是否仍然可用。

            測試案例：

            寫入數據
            手動關閉一個主節點
            嘗試讀取數據
            """)
    @GetMapping("/test-availability")
    public String test() {
        return testService.testAvailability();
    }

    @Operation(
            summary = "Test availability、Automatic Failover Step 2",
            description = """
            手動關閉某一個主節點後，重啟一下spring boot，再次嘗試讀取數據，會發現數據依然可以讀取，且由另外一個主節點提供服務。
            至於為什麼要重啟 spring boot 才能看到效果，我不是很確定。
            可能是因為一開始連線的時候，確定好有哪些可用節點後，分配好了槽，所以即使後來有節點掛掉，他還是依照一開始的分配好的節點來回覆，
            而重啟 spring boot 可能是重新連線，重新分配槽，所以才能看到效果。但實際上就算你不重啟還是可以取得資料，只是他仍然顯示資料是從舊的節點取回。
            測試完畢後，如果你想要重新啟動節點，請把剛剛關掉的節點啟動，然後再重新跑一次 redis-cluster-creator container。
            """)
    @GetMapping("/test-availability-2")
    public String testAvailability2() {
        return testService.testAvailability2();
    }

    @Operation(
            summary = "Test read/write splitting",
            description = """
            讀寫分離 (Read/Write Splitting)

            Redis Cluster 支持從主節點寫入數據，從從節點讀取數據，這可以提高讀取性能。

            測試案例：
            一次寫操作和多次讀操作，並記錄每次操作使用的節點。
            獲取當前操作使用的 Redis 節點的信息，包括節點 ID 和角色（主節點或副本節點）。
            """)
    @GetMapping("/test-read-write-splitting")
    public Map<String, String> testReadWriteSplitting(@RequestParam String key, @RequestParam String value, @RequestParam(defaultValue = "10") int readCount) {
//        TODO
        return null;
    }
}
