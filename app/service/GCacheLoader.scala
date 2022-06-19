package service

import play.api.Logger

import java.util.UUID
import java.util.concurrent.Callable

case class GCacheLoader(key: String) extends Callable[String] {
  val logger: Logger = Logger(this.getClass)

  override def call(): String = {
    logger.info("Cache expired")
    s"Link outdated: $key-${UUID.randomUUID().toString}"
  }
}