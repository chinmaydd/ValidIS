### ValidIS

> Patient identification is foundational to the successful linking of patient records within the healthcare ecosystem. This has evolved over the past couple of decades with increase in health information exchange.

Citizens use various health service centres and are registered with each one of them. Each of these centres have an EMR software to manage health records belonging to a particular organization. AHIMA [suggests](http://library.ahima.org/PB/PatientIdentityHIE#.V6F0gGQrJhE) that all such organizations practice verification during frontend data capture and and also implement backend quality control.

What if they don't? Well, in the grand scheme of things we are planning to work towards a patient centric health system and patient centric processes. A very important step forward to this goal is patient record linking. Although the problem is simple, it is not very well understood.

Before we take up this task, it is important to realize the number of duplicate or similar records in a system. Although record linkage is long and complex, the first step would be to monitor database safety and integrity of data within.

ValidIS aims to be a realtime safety monitoring system for clinical information devices. The current project is a prototype developed in particular for the OSCAR EMR, although it is flexible enough to work with any EMR in particular. ValidIS will calculate DDRs through its TouchPoint Sensor based on the inital algorithms in that document.

#### Architecture

![](http://i.imgur.com/vY2MxGe.png)

The system is made up of two major components:

**Hub**

The Hub is a cloud-based system which exposes a REST API backend and a frontend(future) for the users to interact with. Features:

1. Users can create profiles on the Hub specifying their username and email-id. The Hub is safeguarded against DOS attacks through an email based identification and authentication. The user has to verify himself to the system by providing a unique authentication key sent over to him by email. Each user must have a unique username and email-id. This also prevents potential spam on creating accounts.

2. Clinical Information Systems(CIS) can be added to the Hub database by other users as well. Although this leaves the database vulnerable to multiple create requests, it also makes sure that it is not necessary for everyone to have an account on ValidIS to monitor their network.

3. Users can create what is known as a "network". A network is a construct which consists of multiple CIS. This is the core concept on which the project relies on. The network model is useful for aggregating responses from multiple CISs giving the user a brief idea of the number of duplications before he starts record linkages.

3. Users can also share this network information with other users of the system. Access control mechanisms are failt primary and more work is incoming.

4. A stable, secure API model. We have tried to strictly follow the CRUD model here. Although there is no frontend available right now, one will be released soon in the next version.

**TouchPoint Sensor**

1. The TouchPoint Sensor is a remote API based system which rests on the Clinical EMR device and calculates data duplication rates. Local duplication rates are highly important.

2. Since this is a prototype system, we have only implemented the basic and some of the intermediate algorithms as mentioned in the resource. Due to lack of plaintext data, other algorithms are untested.

3. It aggregates all the rates and exposes an endpoint for the ValidIS Hub to interact with. It requires permissions which only the Hub has. This ensures that other users do not ask for information and set up tasks in the database.

4. It also supports caching. This makes sure that there are not too many queries. Since the total number of records are way higher than the number of duplicates, it makes sense to run the calculation as a cron job every week/month rather than at every request since there is not going to be much change in the values.

The Hub talks with the TPS using JSON based requests. On request for getting network information, requests are sent to all CIS in the network and the results are then sent to the frontend for visualizations.

Frontend visualization code is fairly unstable. It is writted using d3.js for multiple line charts. The aggregated data from the TPS is written down to a JSON file which is then read into d3 to draw the svg.

#### Installation

**TPS Installation**

1. Installing OSCAR on your system. If you are installing an EMR, make sure you follow the guidelines as suggested in the manual.

2. After installation, run the `startup.rb` script which will basically send your data to the Hub by making a POST requst to the endpoint and in this way, registering your API URL and IP Address.

3. The `startup.rb` script also asks for the database username and password which are required to set up the connection to the backend.

4. Once that is setup, there is nothing to worry! You are on your way.

**Hub Installation**

1. If you would like to have a seperate hub, you can set it up on the server by installing MongoDB, Clojure and Leininghen. You would also need latest Java(Java 8) and JVM support.

2. Once you have that, you can run `lein run [port]` and start the server up.

#### Contributing

The project was developed as a prototype and is by no means and fully complete system. Some future work which is definitely possible:

1. Implement more algorithms on the backend for duplicate record identification. This is important when we have access to a large dataset of names. This could also include training a model to identify such names.

2. Making the Hub more robust and completing the frontend. Altough the backend is pretty stable, due to lack of frontend the system is quite unusable.

3. Add more visualizations.

4. Extend this for other EMRs. ValidIS is meant to be a safety monitoring system for all EMRs. It can run as a side service for all EMRs hence identifying local duplications.

5. This consists of a single TouchPoint Sensor. The main idea behind a Sensor is the fact that it can detect and report malicious content which is harmful to the integrity of patient data.

#### Documentation

To read more about each of the components, visit their trees.

* [Hub](https://github.com/chinmaydd/ValidIS/tree/master/Hub)
* [TPS](https://github.com/chinmaydd/ValidIS/tree/master/TouchPoint%20Sensor)

#### LICENSE

Released under the GNU GPL v3.0

Copyright Chinmay Deshpande and Jens H. Weber, 2016
