## quick testdrive project of Zeebe with Spring Boot + Camel

###Quickstart
* start up Zeebe 
* deploy the BPMN file `test-correlation.bpmn` from the root of this repo 
* edit `docker-compose.yml` changing the `extra_host` parameter and making the host alias `zeebe` point to your Zeebe broker's host
* `docker-compose up`
* `curl localhost:5000/test/start/1000/0`
* `curl localhost:5000/test/correlate/1000/0`

###Notes
the available REST endpoints are following this pattern:
http://localhost:5000/test/start/<NR>/<TX_ID>

where `NR` is the number of iterations and `TX_ID` is the business key to start from. 

Eg. `curl localhost:5000/test/start/1000/0` will start 1000 workflow instances, with business keys starting from 0 and ending with 999.
 

