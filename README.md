# Short Links service
### How to build and run
- you need scala 2.13, java 8
- git clone https://github.com/mutualEvg/shortener.git
- cd to service directory
- sbt run
- visit ```localhost:9000```
- copy shortened link after you got it and paste to ```localhost:9000/go/<shortLink>```

#### Service features

Create random short links with size of 10 symbols that matches to `^[a-zA-Z0-9]+$`
Default short link lifetime is 2 days

#### My assumptions

- Assumption: short links will have size of 10 symbols for now

I used play framework because it includes backend instruments and 
templates for frontend as well.

I could use pure html with js calls to backend, but I believe that play has better security 
in this case.

- Assumption: Let's say that the storage is a map 
(good complexity to find a value by a key).

`Additionally, if a URL has already been shortened by the system, and it is entered a
second time, the first shortened URL should be provided back to the user.`
For this case I used duplicated items one for each link 
(one pair to get a real link by shortened url, one pair to find existing shor link
by normal link). So in this case we can quickly find existing shortened link and return it.

- Assumption: in the future we can have a huge number of links and many api's will handle it.

- Assumption: I thought about expiration time. For this particular case I used google guava because it has
expirations for its values (I could implement it in scala, but just used guava).
So I started and implemented some backend api, that can work with timed shortened links (you can
find the description below). Unit tests are also here.

In the future or when we need it this guava library can be changed to some distributed
cache (for ex. Redis or Hazelcast).

#### Usage cases
- to create short link go to ```localhost:9000/urls```
- to use created short link go to ```localhost:9000/go/{shorlink}``` in browser

#### API
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