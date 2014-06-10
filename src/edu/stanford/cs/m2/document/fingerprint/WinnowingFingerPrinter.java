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
package edu.stanford.cs.m2.document.fingerprint;

import edu.stanford.cs.m2.document.Document;
import edu.stanford.cs.m2.document.hash.Hash;
import java.util.LinkedList;
import java.util.List;

public class WinnowingFingerPrinter implements FingerPrinter {
    
    int Window;
    
    public WinnowingFingerPrinter(int window) {
        Window = window;
    }
    
    @Override
    public void FingerPrint(Document document) {
        List<Hash> newHashes = new LinkedList<>();
        Hash previousHash = null;
        int previousIndex = -1;
        
        for (int i = 0; i < document.Hashes.size() - Window + 1; i++) {
            
            Hash minHash = document.Hashes.get(i);
            int minIndex = 0;
            
            int offset = i;
            for (Hash hash : document.Hashes.subList(i, i + Window)) {
                if (hash.Value <= minHash.Value) {
                    minHash = hash;
                    minIndex = offset;
                }
                offset++;
            }
            
            if (i > previousIndex || previousHash == null || minHash.Value < previousHash.Value) {
                newHashes.add(minHash);
                previousIndex = minIndex;
                previousHash = minHash;
            }
        }
        
        document.Hashes = newHashes;
    }
}
