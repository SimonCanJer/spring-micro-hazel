# spring-micro-hazel
The project extends  Hazelcast based micro-hazel tool https://github.com/SimonCanJer/spring-micro-hazel
by spring inherent adds and demonstrated application of Hazelcast Queues based microservice mechanism 
for very simple task of to do model.The project includes two submodules.
 - a module providing a tookit and API as an extension for the Hazelcast messaging and and handling mechanism, which simplifies declaration 
of expected set of request classes, and, additionally,populating url of a REST(for example) services among
 all another services (microservices) inside and instance of related Hazelcast worklfow model. This is for case when we need a REST end points on a facade
 of any asynchronous backend system. Yet one related API component is a SpringApplication
 based class which initializes and helds alive a Spring Application instance (without Tomcat), which creates  micro-hazel consumers, initializes
 (under carpet) listener threads, connect processors. See(micro.hazel.server.MicrohazelStandaloneApplication)
 The class also refers (see ComponentsToScan) to the micro.hazel.config.MicrohazelWizard configuration class,which makes absolute majority of under
 carpet work to connect the system and initialize processes.
- example of application, which used of the toolkit and API for a simple task: add and retrieve To DO appointments. The example includes
 REST controller, which uses  API and related annotations to declare and send request for asynchronous execution 
 (see micro.examples.facade.Controller,micro.examples.facade.FacadeApplication), The FacadeApplication class refers also MicrohazelWizard, which also
 registers the exposed REST end point (see handling onContextRead
 in MicrohazelWizard).  The controller simply sends asyncronous request to micro-hazel based workflow,
 using exposed API and returns Mono class objects, where result will be placed to. Real implementation of 
 worklfow is done in the package micro.examples.worker. The class ConfigFlow exposes  the bean of
  the ProcessorProvider type(it is just wrapping mechanism, which uses underlaying processor registratration
  of  the micro-hazel project). The classes ProcessPutNote and ProcessQuery are the classes of processors, which process
  request messages, which are defined in the micro.examples.ipc package. Both of the processor classes use SpringData (see
  micro.example.IToDoRepository). The example is configured for to memory H2 database and hibernate provider
   (see related properties).  The micro.examples.jar exposed a parametrized starer class, which starts either
   FacadeApplication (for facade REST server),   or main of DataService, which started micro-hazel listeners and 
   consuming queues. Registrtaion of URI which is handled in the micro.examples.Controller is not used here, but in the 
   https://github.com/SimonCanJer/zuul-hazel-route
   
   Setting up and installing.
   - The project's pom refers to jars, which are installed in the local maven repository by running mvn install in the
   directory of the project https://github.com/SimonCanJer/microhazle
   The scripts install.win.bat and install.linux.bat provides install and lounching the system.
   Note, both of the scripts require a parameter, which is name of directory, where the micro-hazel project will be 
   installed in.
 
  
              