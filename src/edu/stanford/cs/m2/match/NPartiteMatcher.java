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
package edu.stanford.cs.m2.match;

import edu.stanford.cs.m2.document.Document;
import edu.stanford.cs.m2.document.Snippet;
import edu.stanford.cs.m2.document.hash.Hash;
import edu.stanford.cs.m2.document.language.Language;
import edu.stanford.cs.m2.document.language.Support;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NPartiteMatcher implements Matcher {
    
    public List<List<Document>> Groups;
    
    public NPartiteMatcher(List<List<Document>> groups) {
        Groups = groups;
    }
    
    @Override
    public List<Match> Match() {

        // Computing approximate hash table size
        int size = 0;
        for (List<Document> group : Groups) {
            for (Document document : group) {
                size += document.Hashes.size();
            }
        }

        // Create the master match table list
        List<Map<Integer, List<Snippet>>> matchTableList = new LinkedList<>();
        
        for (List<Document> group : Groups) {
            Map<Integer, List<Snippet>> MatchTable = new HashMap<>(size);
            
            for (Document document : group) {
                for (Hash hash : document.Hashes) {

                    // Set up lists for hash key (if they don't exist)
                    if (!MatchTable.containsKey(hash.Value)) {
                        List<Snippet> newList = new LinkedList<>();
                        MatchTable.put(hash.Value, newList);
                    }
                    
                    MatchTable.get(hash.Value).add(hash.Snippet);
                }
            }
            
            matchTableList.add(MatchTable);
        }
        
        List<Match> matchList = new LinkedList<>();

        // Generate matches
        for (int i = 0; i < Groups.size(); i++) {
            for (int j = i; j < Groups.size(); j++) {
                if (i != j || Groups.size() == 1) {
                    Set<Integer> hashKeys = matchTableList.get(i).keySet();
                    hashKeys.retainAll(matchTableList.get(j).keySet());
                    for (int hashKey : hashKeys) {
                        for (Snippet snippet1 : matchTableList.get(i).get(hashKey)) {
                            for (Snippet snippet2 : matchTableList.get(j).get(hashKey)) {
                                if (!snippet1.equals(snippet2)) {
                                    matchList.add(new Match(snippet1, snippet2));
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return matchList;
    }
}
