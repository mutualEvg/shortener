package model

case class ShortenedLink(shortenedLink: String)

case class NewUrl(url: String, lifeTime: Long)

case class CustomNewUrl(shortUrl: String, url: String, lifeTime: Long)
