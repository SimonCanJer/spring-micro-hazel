# spring-micro-hazel
The project extends  Hazelcast based micro-hazel tool https://github.com/SimonCanJer/spring-micro-hazel
by an add to spring and demonstrated application of Hazelcast Queues for microservices model
The task is very simple- simples to do model, where all the domain logic is implemented in another service.The project includes two submodules.
 - a module providing a tookit and API as an extension for the Hazelcast messaging and and handling mechanism, which simplifies declaration 
of expected set of request classes, and, additionally,populates url of a REST(for example) end point for all another services (microservices) inside  instance of  Hazelcast which serves any distributed service model (federation in our terms). This can be used for REST access (if needed) or/and for gateway.
  Yet one related API component is a SpringApplication
 based class which initializes and helds alive a Spring Application instance (without Tomcat), which creates  micro-hazel consumers, initializes
 (under carpet) listener threads, connect processors to polled queues. See(micro.hazel.server.MicrohazelStandaloneApplication)
 The class also refers (see ComponentsToScan) to the micro.hazel.config.MicrohazelWizard configuration class,which makes absolute majority of the under carpet work to connect the system and initialize processes.
- Example of application, which uses the toolkit and API for a simple task is  add and retrieve To DO appointments. The example includes
 REST controller, which uses  API to declare and send request for asynchronous execution  (see micro.examples.facade.Controller,micro.examples.facade.FacadeApplication), The FacadeApplication class routes configuration path to MicrohazelWizard, which additionally registers the exposed REST end point (see handling onContextRead
 in MicrohazelWizard).  The controller of the REST application simply sends asyncronous request to micro-hazel based workflow,
 using exposed API and returns Mono class objects, where result will be placed to. Real implementation of 
 worklfow is done in the package micro.examples.worker. The class ConfigFlow exposes  the bean of
  the ProcessorProvider type(it is just wrapping mechanism, which uses underlaying processor registratration
  in the micro-hazel project). There are the two processors: classes ProcessPutNote and ProcessQuery, which process
  request messages. Clases of the requests are defined in the micro.examples.ipc package. Both of the processor classes use SpringData (see  micro.example.IToDoRepository). The example is configured for in  memory H2 database and hibernate provider
   (see related properties).  The micro.examples.jar exposed a parametrized starer class, which starts either
   main of FacadeApplication (for facade REST server),   or main of DataService, which starts micro-hazel listeners and 
   consuming queues. Registrtaion of URI which is handled in the micro.examples.Controller is not used here, but in the 
   https://github.com/SimonCanJer/zuul-hazel-route
   
   Setting up and installing.
   - The project's pom refers to jars, which are to be obtained  by running mvn install in the
   directory of the first project https://github.com/SimonCanJer/microhazle.
   The scripts install.win.bat and install.linux.bat of the current provides install and lounching the system.
   Note, both of the scripts require a parameter, which is name of directory, where the micro-hazel project will be 
   installed in.
 
  
              
