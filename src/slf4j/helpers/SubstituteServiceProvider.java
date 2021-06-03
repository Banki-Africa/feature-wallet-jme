package slf4j.helpers;

import slf4j.ILoggerFactory;
import slf4j.IMarkerFactory;
import slf4j.spi.MDCAdapter;
import slf4j.spi.SLF4JServiceProvider;

public class SubstituteServiceProvider implements SLF4JServiceProvider {
    private SubstituteLoggerFactory loggerFactory = new SubstituteLoggerFactory();
    private IMarkerFactory markerFactory = new BasicMarkerFactory();
    private MDCAdapter mdcAdapter = new BasicMDCAdapter();

    @Override
    public ILoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    public SubstituteLoggerFactory getSubstituteLoggerFactory() {
        return loggerFactory;
    }

    @Override
    public IMarkerFactory getMarkerFactory() {
        return markerFactory;
    }


    @Override
    public MDCAdapter getMDCAdapter() {
        return mdcAdapter;
    }


    @Override
    public String getRequesteApiVersion() {
       throw new UnsupportedOperationException();
    }
    
    @Override
    public void initialize() {
    	
    }
}
