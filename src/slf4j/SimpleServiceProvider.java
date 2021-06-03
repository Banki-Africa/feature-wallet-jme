package slf4j.simple;

import slf4j.ILoggerFactory;
import slf4j.IMarkerFactory;
import slf4j.helpers.BasicMarkerFactory;
import slf4j.helpers.NOPMDCAdapter;
import slf4j.spi.MDCAdapter;
import slf4j.spi.SLF4JServiceProvider;

public class SimpleServiceProvider implements SLF4JServiceProvider {

    /**
     * Declare the version of the SLF4J API this implementation is compiled against. 
     * The value of this field is modified with each major release. 
     */
    // to avoid constant folding by the compiler, this field must *not* be final
    public static String REQUESTED_API_VERSION = "1.8.99"; // !final

    private ILoggerFactory loggerFactory; 
    private IMarkerFactory markerFactory;
    private MDCAdapter mdcAdapter;
                    
    public ILoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    public IMarkerFactory getMarkerFactory() {
        return markerFactory;
    }

    public MDCAdapter getMDCAdapter() {
        return mdcAdapter;
    }

    public String getRequesteApiVersion() {
        return REQUESTED_API_VERSION;
    }


    @Override
    public void initialize() {

          loggerFactory = new SimpleLoggerFactory();
          markerFactory = new BasicMarkerFactory();
          mdcAdapter = new NOPMDCAdapter();
    }
    
}
