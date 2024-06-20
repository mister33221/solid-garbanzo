-- 設置一個鍵值對
redis.call('SET', 'mykey', 'myvalue')

-- 獲取鍵的值
local value = redis.call('GET', 'mykey')

-- 返回鍵的值
return value
