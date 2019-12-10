# edublockchain
A blockchain for educational and learning purposes :)

The aim of this project is to create our own blockchain to better understand basic blockchain concepts. 
Here it would be interesting to experiment different consensus approaches, secure transactions, try out the 51% attack, and figure out how to implement a smart contract within a blockchain.

Wanna contribute? Let's have fun together!

## Getting Started

To have the miners communicating the fresh mined blocks we use RabbitMQ (though in a real blockchain network, communication is completely decentralized).

```
sudo docker run -d --hostname my-rabbit --name edublockchain-rabbit rabbitmq:3
```
