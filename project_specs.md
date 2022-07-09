# Distributed Systems project specifications

## _Distributed Electronic Voting_

_Distributed Electronic Voting_ is a distributed web application that allows people to participate in an election and express their vote via an electronic device; instead of having to use voting papers to be collected and sent to the central headquarters, the system will be able to collect the results from each polling station and compute the outcome of the election.

## 1 Functional requirements

### User

* An anonymous user should be able to login to the platform via secure PGP key.
* A logged user should be able to express their voting preference via web UI on the electronic device present in the polling station.

### Admin
* An admin user should be able to login to the admin dashboard via username and password.
* An admin user should be able to open the vote.
* An admin user should be able to temporarily suspend the vote.
* An admin user should be able to close the vote.
* An admin user should be able to view statistics related to the vote such as the turnout.
* An admin user should be able to manually add people to the list of the voters in particular cases (military, etc.)

### System
* The system should remember which users already expressed a vote.
* The system should aggregate the votes expressed in the single polling station.
* The system should aggregate the total counts of vote for each candidate coming from each of the polling stations.

## 2 Non-functional requirements

* The system should be able to keep track of each vote in a secure manner.
* The system should always stay in a consistent state without missing votes because of dataraces.

## 3 System architecture
* Clients: web application implemented via Java Servlets. Each polling booth connects to a local Erlang server
* Servers: one local erlang server for each polling station and one single master Erlang server that collects data from each polling station.
