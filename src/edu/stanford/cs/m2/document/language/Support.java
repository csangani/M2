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
package edu.stanford.cs.m2.document.language;

import edu.stanford.cs.m2.document.Document;
import edu.stanford.cs.m2.document.filter.CFilter;
import edu.stanford.cs.m2.document.filter.Filter;
import edu.stanford.cs.m2.document.language.exception.UnknownLanguageException;
import edu.stanford.cs.m2.document.parse.CParser;
import edu.stanford.cs.m2.document.parse.CToken;
import edu.stanford.cs.m2.document.parse.Parser;
import edu.stanford.cs.m2.util.StringStreamConverter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public class Support {

    public static Parser getParser(Language lang, Document document) throws UnknownLanguageException {
        Reader reader = new InputStreamReader(StringStreamConverter.toStream(document.Content), Charset.forName("US-ASCII"));
        switch (lang) {
            case C:
                return new CParser(reader);
            default:
                throw new UnknownLanguageException(lang);
        }
    }

    public static Filter getFilter(Language lang) throws UnknownLanguageException {
        switch (lang) {
            case C:
                return new CFilter();
            default:
                throw new UnknownLanguageException(lang);
        }
    }
    
    public static int getTokenEnumSize(Language lang) throws UnknownLanguageException {
        switch (lang) {
            case C:
                return CToken.values().length;
            default:
                throw new UnknownLanguageException(lang);
        }
    }
}
