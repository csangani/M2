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
package edu.stanford.cs.m2.document.hash;

import edu.stanford.cs.m2.document.Document;
import edu.stanford.cs.m2.document.Snippet;
import edu.stanford.cs.m2.document.parse.Symbol;
import java.util.LinkedList;
import java.util.List;

/* Many thanks to http://stackoverflow.com/questions/711770/fast-implementation-of-rolling-hash */
public class KGramRollingHasher implements Hasher {

    public int K;
    public Document Document;

    public KGramRollingHasher(int k) {
        K = k;
    }

    @Override
    public void Hash(Document document) {
        Document = document;
        
        Hash firstHash = basicHash(document.Symbols.subList(0, K));

        List<Hash> hashes = new LinkedList<>();

        hashes.add(firstHash);

        Hash previousHash = firstHash;

        for (int i = K; i < document.Symbols.size(); i++) {
            Hash newHash = new Hash((previousHash.Value * multiplier
                    + document.Symbols.get(i).Token.toInt()) % modulus,
                    new Snippet(document, document.getSymbol(i - K + 1),
                            document.getSymbol(i)));
            hashes.add(newHash);
            previousHash = newHash;
        }

        document.Hashes = hashes;
    }

    private static final int multiplier = 1103515245;
    private static final int modulus = 1 << 31;

    private Hash basicHash(List<Symbol> input) {
        int ret = 0;
        for (Symbol symbol : input) {
            ret *= multiplier;
            ret += symbol.Token.toInt();
            ret %= modulus;
        }
        return new Hash(ret, new Snippet(Document, input.get(0), input.get(input.size() - 1)));
    }
}
