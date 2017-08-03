Simple Multi Agent System using Consul
======================================
Consul is one of the most popular tools for service discovery and registery.
This project uses Consul API(https://github.com/Ecwid/consul-api/) for ....? .

## Project Overview
Agent is main java class which has register() and lookup() method.
It has generateUniqueID() method, it creates uniqueID based on IP_Port_ServiceName.

It has 2 subclasses Ping and Pong.
Both agents uses socket programming to communicate with each other.

## Features
In this sample program, a basic level of load balancing and reactive service deregistration is performed.

Load Balancing:
There can be many pingAgents registered with consul. When pongAgent looks for a ping agent, It will be allocated with a random agent to communicate.

Reactive Deregistration:
Consul has health-checking feature. There are multiple ways to unable it. To keep things easy, I decided to use reactive de-registration. 
If pongAgent finds a stale pingAgent then it will automatically de-register it.

## How to use it
Pre-requisite:
All the machines should have ssh service enabled. I have utilized Ansible tool to create execution environment. There should be one machine running Ansible and has cloned this git repository. After provisioning machines, carefully setup hosts file. Refer to hosts_sample to get idea about it.

This installation process is divided in 2 part:
1) Setup of Consul server : I have only considered a single consul server for the purpose of this demonstration.
2) Setup of Agents

Let's say, I have 3 virtual machine. Machine-A, Machine-B and Machine-C. All of the machines needs to have ssh service.
Machine-A has ansible tool and it has cloned the repository at $HOME_DIR/agentsystem. Machine-B would be running consul service and on machine-C we will create the agents.

From Machine-A we can fire Ansible scripts to install consul and agent service.
PART 1: Installation of Consul

Update host file to include IP address of Machine-B and fire following in $HOME_DIR/agentsystem/ansible/.
~~~
ansible-playbook consul.yml
~~~

PART 2: Setting up agent system

Update host file and provide IP address of Machine-C and fire following in $HOME_DIR/agentsystem/ansible/
~~~
ansible-playbook agent.yml
~~~

Log into Machine-B:

To start Consul UI and consul server fire following command.
~~~
sudo consul agent -server -bootstrap-expect=1 -ui -client={{PUBLIC_IP}} -data-dir=/var/lib/consul -config-dir=/etc/consul.d
~~~

Log into Agent Machine C:

Change the permission of AgentSystem file which is located at $HOME_DIR/agentsystem/.
~~~
chmod 777 AgentSystem
~~~

Configure config.properties to connect with Consul Server. It is located at $HOME_DIR/agentsystem/
~~~
consul_server={{IP_ADDRESS}}
consul_port=8500
serviceAddress={{IP_ADDRESS_OF_MACHINE}}
~~~

Open AgentSystem script and replace {{PATH_OF_CONFIGURATION_FILE}} with actual path of config.properties file.
~~~
java -jar target/agentsystem-1.0-SNAPSHOT-jar-with-dependencies.jar {{PATH_OF_CONFIGURATION_FILE}} $1
~~~

Deploy agents using AgentSystem
~~~
./AgentSystem : Error should be generated indicating 
./AgentSystem PingAgent
./AgentSystem PongAgent
~~~

You can also view agents at CONSUL_SERVER_IP:8500

## Improvements
Jinja file for automatic configuration of consul_server.
Known issue of multiple pingAgent deployment.
Consul health check can be utilized to perform active service deregistration.
Stale Service removal using automated service.