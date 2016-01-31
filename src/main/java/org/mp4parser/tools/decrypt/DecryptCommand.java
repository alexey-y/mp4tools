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
package org.mp4parser.tools.decrypt;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.mp4parser.tools.Command;
import org.mp4parser.tools.Hex;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DecryptCommand implements Command {
    @Argument(required = true, metaVar = "IN", index = 0)
    File input;

    @Argument(required = true, metaVar = "OUT", index = 1)
    File output;

    @Option(name = "--keys", usage = "Use keyid:key to supply keys for decryption")
    List<String> hexKeys;
    Map<UUID, SecretKey> keys;

    public int run() {
        return 0;
    }

    public void postProcessCmdLineArgs(CmdLineParser cmdLineParser) throws CmdLineException {
        Pattern p = Pattern.compile("(\\p{XDigit}{8}-?\\p{XDigit}{4}-?\\p{XDigit}{4}-?\\p{XDigit}{4}-?\\p{XDigit}{12}):(\\p{XDigit}{32})");
        for (String hexKey : hexKeys) {
            Matcher m = p.matcher(hexKey);
            if (m.matches()) {
                keys.put(UUID.fromString(m.group(1)), new SecretKeySpec(Hex.decodeHex(m.group(2)), "AES"));
            } else {
                throw new CmdLineException(String.format("--key option %s doesn't match the required keyid:key pattern: de305d54-75b4-431b-adb2-eb6b9e546014:de305d5475b4431badb2eb6b9e546014", hexKey));
            }
        }

    }
}
