package micro.examples.jar;

import micro.examples.facade.FacadeApplication;
import micro.examples.worker.DataService;

public class Runner {

    static public void main(String[] args)
    {

        if(args.length==0 || args[0].toLowerCase().contains("data")||args[0].toLowerCase().contains("backend"))
        {
            DataService.main(args);
        }
        if(args[0].toLowerCase().contains("rest")||args[0].toLowerCase().contains("facade"))
        {
            FacadeApplication.main(args);
        }
    }
}
