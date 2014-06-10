/*
 *
 *  Copyright (c) 2014, Stanford University
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. All advertising materials mentioning features or use of this software
 *     must display the following acknowledgement:
 *     This product includes software developed by the <organization>.
 *  4. Neither the name of the <organization> nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  
 *  AUTHOR: Chirag Sangani (csangani@stanford.edu)
 *
 */
package edu.stanford.cs.m2.intf;

import edu.stanford.cs.m2.document.Document;
import edu.stanford.cs.m2.document.fingerprint.WinnowingFingerPrinter;
import edu.stanford.cs.m2.document.hash.KGramRollingHasher;
import edu.stanford.cs.m2.document.language.Language;
import edu.stanford.cs.m2.document.language.Support;
import edu.stanford.cs.m2.document.language.exception.UnknownLanguageException;
import edu.stanford.cs.m2.match.Aggregator;
import edu.stanford.cs.m2.match.Match;
import edu.stanford.cs.m2.match.Matcher;
import edu.stanford.cs.m2.match.NPartiteMatcher;
import edu.stanford.cs.m2.util.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CLI {

    CommandLine Command;
    Options Options;

    public CLI Build(String[] args) throws ParseException {
        CommandLineParser parser = new GnuParser();
        Options = BuildOptions();
        Command = parser.parse(Options, args);
        return this;
    }

    public void Execute() throws IOException {
        Language language = Language.valueOf(Command.getOptionValue("l"));
        String[] files1 = Command.getOptionValues("f1");
        String[] files2 = Command.getOptionValues("f2");
        
        List<List<Document>> groups = new LinkedList<>();

        List<Document> documents = new LinkedList<>();

        for (String file : files1) {
            try {
                Document document = new Document(file, File.Read(file, Charset.forName("US-ASCII")), language);
                documents.add(document);
                document.Parse(Support.getParser(language, document));
                document.Filter(Support.getFilter(language));
                document.Hash(new KGramRollingHasher(10));
                document.FingerPrint(new WinnowingFingerPrinter(3));
            } catch (UnknownLanguageException ex) {
                Logger.getLogger(CLI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        groups.add(documents);

        documents = new LinkedList<>();

        for (String file : files2) {
            try {
                Document document = new Document(file, File.Read(file, Charset.forName("US-ASCII")), language);
                documents.add(document);
                document.Parse(Support.getParser(language, document));
                document.Filter(Support.getFilter(language));
                document.Hash(new KGramRollingHasher(10));
                document.FingerPrint(new WinnowingFingerPrinter(3));
            } catch (UnknownLanguageException ex) {
                Logger.getLogger(CLI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        groups.add(documents);
        
        Matcher matcher = new NPartiteMatcher(groups);
        
        List<Match> matches = matcher.Match();
        
        new Aggregator().Aggregate(matches);
        
        for (Match match: matches) {
            System.out.println(match.Snippet1.Document.Name + ": " + match.Snippet1.Document.Content.substring(match.Snippet1.Begin.FilePosition.Character, match.Snippet1.End.FilePosition.Character + match.Snippet1.End.Value.length()));
            System.out.println(match.Snippet2.Document.Name + ": " + match.Snippet2.Document.Content.substring(match.Snippet2.Begin.FilePosition.Character, match.Snippet2.End.FilePosition.Character + match.Snippet2.End.Value.length()));
            System.out.println("------------------------------------");
        }
    }

    public Options BuildOptions() {
        Options options = new Options();

        Option language = OptionBuilder
                .withArgName("language")
                .hasArg()
                .withDescription("Language of source files")
                .isRequired()
                .withLongOpt("language")
                .create("l");
        options.addOption(language);

        Option files1 = OptionBuilder
                .withArgName("files1")
                .hasArgs()
                .withDescription("Path to source files")
                .isRequired()
                .withLongOpt("files1")
                .create("f1");
        options.addOption(files1);

        Option files2 = OptionBuilder
                .withArgName("files2")
                .hasArgs()
                .withDescription("Path to source files")
                .isRequired()
                .withLongOpt("files2")
                .create("f2");
        options.addOption(files2);

        return options;
    }

    public static void main(String[] args) throws Exception {
        CLI cli = new CLI();
        try {
            cli.Build(args).Execute();
        } catch (ParseException e) {
            new HelpFormatter().printHelp("m2 -l <language> -f1 <files> -f2 <files>", cli.Options);
        }
    }
}
