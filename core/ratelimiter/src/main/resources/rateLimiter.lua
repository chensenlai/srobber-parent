-- KEY[1]=限流key
-- ARGV[1]=过期毫秒数expire
-- ARGV[2]=单位时间允许执行次数

local limitKey = KEYS[1]
local limitExpireMill = ARGV[1]
local limitTimes = ARGV[2]

local val = redis.call('incr', limitKey)
local pttl = redis.call('pttl', limitKey)

if val == 1 then
    redis.call('pexpire', limitKey, tonumber(limitExpireMill))
else
    if pttl == -1 then
        redis.call('pexpire', limitKey, tonumber(limitExpireMill))
    end
end

if val > tonumber(limitTimes) then
    return 0
end

return 1