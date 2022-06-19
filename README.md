# Short Links service
### How to build
- you need scala 2.13/2.12, java 8
- git clone https://github.com/mutualEvg/LinksSortener.git
- cd to service directory
- sbt run
- visit ```localhost:9000```
#### Service features


Create random short links with size of 10 symbols that matches to `^[a-zA-Z0-9]+$`
Default short link lifetime is 2 days


#### Usage cases

- to use created short link go to ```localhost:9000/go/{shorlink}```
- to get example url use `curl localhost:8080/shorturls/qwerty1235` or short url after `localhost:8080/shorturls/{YOUR_SHORT_URL}`
- to create new random link with you need to provide long link in body. The example request for this functionality is 
  `curl -v -d '{"description": "https://teitter.com", "lifeTime": 10}' -H 'Content-Type: application/json' -X POST localhost:8080/shorturls`.
  In response that you'll receive the short url is present. Default lifetime for urls created is 2 days.<br/>
  To get default lifetime you need to pass `"lifeTime": 0` in body
- to create new custom link with you need to provide your own short link, long link and your custom lifetime in body. The example is
  `curl -v -d '{"shortUrl": "qwerty6543", "url": "https://teitter.com", "lifeTime": 10}' -H 'Content-Type: application/json' -X POST localhost:8080/shorturls/custom`
  So in this request body
  
  { <br/>
    "shortUrl": "qwerty6543", -- this is your own short url<br/>
    "url": "https://teitter.com", -- this is the long url<br/>
    "lifeTime": 10 -- lifetime for your short url<br/>
  }
  
- There is now automatic cleaner in this api, so sometimes you need to clear cache from outdated links
  (in api answer you'll not see it, but the data structure is still here and to clean resources
  you can clear it) <br/>
  For this purpose please use next command <br/>
  `curl -X POST 'localhost:8080/shorturls/clear'`

### Technology stack

- play2 framework
- scala 2.13
- google Guava library 
- scala test