# Messaging System with ActiveMq

This project contains a activeMqProducer and a activeMqConsumer implementation connected to a queue of ActiveMq.

### ActiveMqProducer
- Obtains data from Tweets on Twitter containing a special term
- Sends the filtered Tweets to a queue

### ActiveMqConsumer
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