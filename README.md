Simple Multi Agent System using Consul
======================================
Consul is one of most popular tool for service discovery and registery.
This project consists uses Consul API(https://github.com/Ecwid/consul-api/).

## Project Overview
Agent is main java class which has register() and lookup() method.
It has generateUniqueID() method, it creates uniqueID based on IP_Port_ServiceName.

It has 2 subclasses Ping and Pong.
Both agents uses socket programming to communicate with each other.

## Features
In this sample program, a basic level of load balancing and reactive service deregistration is performed.

Load Balancing:
There can be many pingAgents registered with consul. When pongAgent looks for ping agent, I allocate random agent to it.

Reactive Deregistration:
Consul has health-cheking feature. There are multiple ways to unable it. To keep things easy, I decided to use reactive deregistration. If pongAgent find stale pingAgent then it deregisters it.

## How to use it
Install consul on a machine, as I utilize RESTful web services of Consul make sure it's 8500 port is open.

cosul agent -server -bootstrap-expect=1 -ui -client=PUBLIC_IP -data-dir=/var/lib/consul -conf-dir=/etc/consul.d

You can use this program to start only single agent.
## Issues
Consul health check can be utilized to perform active service deregistration.

Agents needs to communicate with each other using JSON.