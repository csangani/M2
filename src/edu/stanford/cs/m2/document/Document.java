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
 *  THIS SOFTWARE IS PROVIDED BFY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
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
package edu.stanford.cs.m2.document;

import edu.stanford.cs.m2.document.filter.Filter;
import edu.stanford.cs.m2.document.fingerprint.FingerPrinter;
import edu.stanford.cs.m2.document.hash.Hash;
import edu.stanford.cs.m2.document.hash.Hasher;
import edu.stanford.cs.m2.document.language.Language;
import edu.stanford.cs.m2.document.parse.Parser;
import edu.stanford.cs.m2.document.parse.Symbol;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Document {
    
    public String Name;
    public String Content;
    public Language Language;
    public List<Symbol> Symbols;
    public List<Hash> Hashes;
    
    public Document(String name, String content, Language language) {
        Name = name;
        Content = content;
        Language = language;
    }
    
    public Symbol getSymbol(int position) {
        try {
            return Symbols.get(position);
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new IllegalArgumentException(String.valueOf(position));
        }
    }
    
    public Symbol getSymbol(FilePosition position) {
        for (Symbol symbol : Symbols) {
            if (symbol.FilePosition.equals(position)) {
                return symbol;
            }
        }
        throw new IllegalArgumentException(position.toString());
    }
    
    public void Parse(Parser parser) throws IOException {
        List<Symbol> list = new LinkedList<>();
        Symbol nextSymbol = parser.yylex();
        while (nextSymbol != null) {
            list.add(nextSymbol);
            nextSymbol = parser.yylex();
        }
        Symbols = list;
    }
    
    public void Filter(Filter filter) {
        filter.Filter(this);
    }
    
    public void Hash(Hasher hasher) {
        hasher.Hash(this);
    }
    
    public void FingerPrint(FingerPrinter fingerPrinter) {
        fingerPrinter.FingerPrint(this);
    }
    
    public boolean equals(Document document) {
        return Name.equals(document.Name);
    } 
}
