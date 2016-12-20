package jadex;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.Properties;

public class JadexArgsParser extends DefaultParser {
    @Override
    public CommandLine parse(Options options, String[] arguments, Properties properties, boolean stopAtNonOption) throws ParseException {
        return super.parse(options, arguments, properties, stopAtNonOption);
    }

    @Override
    protected void handleConcatenatedOptions(String token) throws ParseException {
        super.handleConcatenatedOptions(token);
    }


}
