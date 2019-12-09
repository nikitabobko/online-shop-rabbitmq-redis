# online-shop-rabbitmq-redis

This is study codding of online shop (for practicing and playing with RabbitMQ and Redis).  
Practice was performed as part of "Distributed Systems" course in CMC MSU.
Shop consists of two parts:
* Backend
* Frontend (CLI)

Backend and frontend communicate through RabbitMQ. Backend keeps all data in Redis.

Key features:
* Dependecy Injection (It's used here in quite interesting form I believe: I have base interfaces 
for frontend and backed let's say `UserCart`. For frontend it is implemented the way that it 
requests data from backend. Backend's version is implemented via Redis putting and getting data from it. 
So one interface for both frontend and backend. And two different implementations which injects for 
frontend and backend with different implementations via own Dependency Injection mechanism. 
All model classes built this way)
* Hearth beat mechanism. Backend periodically checks that clients are alive. If not then backend 
discards not alive user's cart
* All backend-frontend communications are done via RabbitMQ
* Backend keeps all data in Redis

# How to run

Firstly run Redis and RabbitMQ servers:
```
redis-server
rabbitmq-server
```
then in one terminal run backend:
```
./runBackend.bash
```
and in another terminal run frontend:
```
./runFrontend.bash
```
Have fun with frontend CLI :)
