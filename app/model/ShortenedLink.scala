package model

case class ShortenedLink(shortenedLink: String)

case class NewUrl(description: String, lifeTime: Long)

case class CustomNewUrl(shortUrl: String, url: String, lifeTime: Long)
