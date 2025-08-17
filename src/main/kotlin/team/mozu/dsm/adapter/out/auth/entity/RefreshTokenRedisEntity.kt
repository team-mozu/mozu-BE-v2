package team.mozu.dsm.adapter.out.auth.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import org.springframework.data.redis.core.index.Indexed

@RedisHash
class RefreshTokenRedisEntity(

    @Id
    val organCode: String,

    @Indexed
    val token: String,

    @TimeToLive
    val timeToLive: Long
)
