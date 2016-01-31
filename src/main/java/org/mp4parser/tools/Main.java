/*
   Copyright 2016 Sebastian Annies

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.mp4parser.tools;


import org.apache.commons.io.IOUtils;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommandHandler;
import org.kohsuke.args4j.spi.SubCommands;
import org.mp4parser.tools.decrypt.DecryptCommand;
import org.mp4parser.tools.extractkid.ExtractKeyId;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {

    public static final String TOOL;

    @Argument(
            handler = SubCommandHandler.class,
            required = true,
            metaVar = "command",
            usage = "Command required. Supported commands are: [encrypt, extractkid]"
    )
    @SubCommands({
            @SubCommand(name = "decrypt", impl = DecryptCommand.class),
            @SubCommand(name = "extractkid", impl = ExtractKeyId.class)
    })
    Command command;

    @Option(name = "-v", usage = "Verbose output when enabled")
    boolean verbose;


    public static void main(String[] args) throws Exception {
        System.out.println(TOOL);
        Main m = new Main();
        CmdLineParser parser = new CmdLineParser(m);
        try {
            parser.parseArgument(args);
            m.setupLogger();
            m.command.postProcessCmdLineArgs(new CmdLineParser(m.command));
            m.command.run();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.print("tools.jar ");
            e.getParser().printSingleLineUsage(System.err);
            System.err.println();
            e.getParser().printUsage(System.err);
            System.exit(1023);
        }

    }


    static {
        String tool;
        try {
            tool = IOUtils.toString(Main.class.getResourceAsStream("/tool.txt"));
        } catch (IOException e) {
            tool = "Could not determine version";
        }
        TOOL = tool;
    }

    public Logger setupLogger() {
        Logger logger = Logger.getLogger("tools");
        InputStream stream;
        if (verbose) {
            stream = Main.class.getResourceAsStream("/log-verbose.properties");
        } else {
            stream = Main.class.getResourceAsStream("/log.properties");
        }
        try {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.setLevel(Level.FINE);
        logger.addHandler(new java.util.logging.ConsoleHandler());
        logger.setUseParentHandlers(false);

        return logger;
    }

}
