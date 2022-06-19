package service

import com.google.common.cache.{Cache, CacheBuilder}

import java.time.{LocalDateTime, ZoneOffset}
import java.util.concurrent.{Callable, TimeUnit}
import javax.inject.Singleton
import scala.collection.mutable

@Singleton
class CustomCache {
  val cacheCache = new mutable.HashMap[String, TimedCache]()

  def put(key: String, value: String, lifeTime: Long): Option[TimedCache] = {
    val cache = new TimedCache(lifeTime)
    cache.put(key, value)
    cacheCache.put(key, cache)
  }

  def get(key: String): Option[String] = {
    cacheCache.get(key) match {
      case Some(value) => Some(value.get(key, GCacheLoader(key)))
      case None => None // if there is None it will be Bad Request instead of Some("Cache is outdated")
    }
  }

  def size(): Int = cacheCache.size

  def values: Set[String] = cacheCache
    .values
    .toList
    .flatMap(cache => cache.cache.asMap().values().toArray().toList)
    .toSet
    .asInstanceOf[Set[String]]

  def clear(): cacheCache.type = cacheCache.filterInPlace((k, v)
    => v.created.plusSeconds(v.lifeTime).isAfter(LocalDateTime.now()))
}

class TimedCache(val lifeTime: Long) {
  val created: LocalDateTime = LocalDateTime.now()
  /*
   * We need this correction if input Long number is bigger then 365241780471 -> maxDayEpoch
   * actually for clear cache method
   */
  val lifetimeCorrected: Long =
    if (created.plusSeconds(lifeTime).toEpochSecond(ZoneOffset.of("Z")) > 365241780471L) 365241780471L
    else lifeTime

  val cache: Cache[String, String] = getCache(lifetimeCorrected)

  def getCache(time: Long): Cache[String, String] = {
    CacheBuilder.newBuilder()
      .recordStats()
      .expireAfterWrite(time, TimeUnit.SECONDS)
      .maximumSize(10)
      .asInstanceOf[CacheBuilder[String, String]]
      .build[String, String]
  }

  def put(key: String, value: String): Unit = cache.put(key, value)

  def get(key: String, loader: Callable[String]): String = cache.get(key, loader)
}
