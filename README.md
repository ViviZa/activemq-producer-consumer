# Messaging System with ActiveMq

This project contains a producer and a consumer implementation connected to a queue of ActiveMq.

### Producer
- Obtains data from Tweets on Twitter containing a special term
- Sends the filtered Tweets to a queue

### Consumer
- Consumes tweets from that queue
- Deserializes tweets
- Saves tweets id, creation date, consumed date and source in a database 
 
## Prerequisites:
- ActiveMq installation 
- A queue in ActiveMq named "myQueue"
- Twitter developer credentials
- Sqlite installation 
- Database named "twittertweets.db" with table "tweets" having columns: id_str, created_at, consumed_at, consumed_through

## Run the application:

- Start ActiveMq
```
    ./bin/activemq start
```
- Run main method of the project