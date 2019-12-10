# edublockchain
A blockchain for educational and learning purposes :)

The aim of this project is to create our own blockchain to better understand basic blockchain concepts. 
Here it would be interesting to experiment different consensus approaches, secure transactions, try out the 51% attack, and figure out how to implement a smart contract within a blockchain.

Wanna contribute? Let's have fun together!

## Getting Started

To have the miners communicating the fresh mined blocks we use RabbitMQ (though in a real blockchain network, communication is completely decentralized).

```
sudo docker run -d -p 5672:5672 --hostname my-rabbit --name edublockchain-rabbit rabbitmq:3
```

This will start a RabbitMQ container listening on the default port of 5672. If you give wait a minute, then do ```docker logs edublockchain-rabbit```, you'll see in the output something similar to:

```
 Starting broker...2019-12-10 00:24:48.779 [info] <0.262.0>                                                                                  
 node           : rabbit@my-rabbit                                                                                                            
 home dir       : /var/lib/rabbitmq                                                                                                           
 config file(s) : /etc/rabbitmq/rabbitmq.conf                                                                                                 
 cookie hash    : usYzAqYHrXPVfEZoa3Rrsw==
 log(s)         : <stdout>
 database dir   : /var/lib/rabbitmq/mnesia/rabbit@my-rabbit
 ```
