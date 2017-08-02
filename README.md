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
Prerequisite:
All the machines should have ssh service enabled. I have utilized Ansible tool to create execution environment. There should be one machine running Ansible and has cloned this git repository. After provisioning machines, carefully setup hosts file. Refer to hosts_sample to get idea about it.

This installation process is divided in 2 part:
1) Setup of Consul server : I have only considered a single consul server for the purpose of demonstration.
2) Setup of Agents

PART 1: Installation of Consul

This needs to be executed only on single machine.

~~~
ansible-playbook consul.yml
~~~
 
As I utilize RESTful web services fire following command:

~~~
sudo consul agent -server -bootstrap-expect=1 -ui -client=PUBLIC_IP -data-dir=/var/lib/consul -config-dir=/etc/consul.d
~~~

PART 2: Setting up agent system

As there can be multiple agents, it can be executed on any number of machines. Following will clone the repository.
~~~
ansible-playbook agent.yml
~~~

Change the permission of AgentSystem
~~~
chmod 777 AgentSystem
~~~

Configure config.properties to connect with Consul Server. It is located at $HOME_DIR/agentsystem/src/main/resources
~~~
consul_server={{IP_ADDRESS}}
~~~

Deploy agents using AgentSystem
~~~
./AgentSystem : Error should be generated indicating 
./AgentSystem PingAgent : 
~~~



## Improvements
Jinja file for automatic configuration of consul_server.
Known issue of multiple pingAgent deployment.
Consul health check can be utilized to perform active service deregistration.

