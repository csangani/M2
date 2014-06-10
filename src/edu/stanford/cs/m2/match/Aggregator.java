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
import edu.stanford.cs.m2.document.parse.Symbol;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Aggregator {

    public void Aggregate(List<Match> matches) {
        Map<Document, Map<Document, List<Match>>> pairTable = new HashMap<>();

        for (Match match : matches) {
            if (!pairTable.containsKey(match.Snippet1.Document)) {
                pairTable.put(match.Snippet1.Document, new HashMap<>());
            }

            if (!pairTable.get(match.Snippet1.Document).containsKey(match.Snippet2.Document)) {
                pairTable.get(match.Snippet1.Document).put(match.Snippet2.Document, new LinkedList<>());
            }

            pairTable.get(match.Snippet1.Document).get(match.Snippet2.Document).add(match);
        }

        boolean change = true;
        while (change) {
            change = false;
            Set<Match> deleteSet = new HashSet<>();
            Set<Match> addSet = new HashSet<>();

            for (Document document1 : pairTable.keySet()) {
                if (change) {
                    break;
                }

                for (Document document2 : pairTable.get(document1).keySet()) {
                    if (change) {
                        break;
                    }

                    List<Match> m = pairTable.get(document1).get(document2);

                    for (Match m1 : m) {
                        if (change) {
                            break;
                        }

                        for (Match m2 : m) {
                            if (change) {
                                break;
                            }

                            if (!m1.equals(m2)) {

                                // Combine if the sum of the length of the two snippets
                                // is at least as big as the length from the beginning
                                // of the earlier snippet to the end of the latest
                                // snippet for both documents
                                int m1snippet1Length = m1.Snippet1.End.FilePosition.Character + m1.Snippet1.End.Value.length() - m1.Snippet1.Begin.FilePosition.Character;
                                int m1snippet2Length = m1.Snippet2.End.FilePosition.Character + m1.Snippet2.End.Value.length() - m1.Snippet2.Begin.FilePosition.Character;
                                int m2snippet1Length = m2.Snippet1.End.FilePosition.Character + m2.Snippet1.End.Value.length() - m2.Snippet1.Begin.FilePosition.Character;
                                int m2snippet2Length = m2.Snippet2.End.FilePosition.Character + m2.Snippet2.End.Value.length() - m2.Snippet2.Begin.FilePosition.Character;

                                Symbol d1MinBegin = (m1.Snippet1.Begin.FilePosition.Character <= m2.Snippet1.Begin.FilePosition.Character) ? m1.Snippet1.Begin : m2.Snippet1.Begin;
                                Symbol d1MaxEnd = (m1.Snippet1.End.FilePosition.Character + m1.Snippet1.End.Value.length() >= m2.Snippet1.End.FilePosition.Character + m2.Snippet1.End.Value.length()) ? m1.Snippet1.End : m2.Snippet1.End;
                                Symbol d2MinBegin = (m1.Snippet2.Begin.FilePosition.Character <= m2.Snippet2.Begin.FilePosition.Character) ? m1.Snippet2.Begin : m2.Snippet2.Begin;
                                Symbol d2MaxEnd = (m1.Snippet2.End.FilePosition.Character + m1.Snippet2.End.Value.length() >= m2.Snippet2.End.FilePosition.Character + m2.Snippet2.End.Value.length()) ? m1.Snippet2.End : m2.Snippet2.End;

                                if (m1snippet1Length + m2snippet1Length >= (d1MaxEnd.FilePosition.Character - d1MaxEnd.Value.length()) - d1MinBegin.FilePosition.Character
                                        && m1snippet2Length + m2snippet2Length >= (d2MaxEnd.FilePosition.Character - d2MaxEnd.Value.length()) - d2MinBegin.FilePosition.Character) {
                                    // Combine the matches

                                    Match newMatch = new Match(new Snippet(m1.Snippet1.Document, d1MinBegin, d1MaxEnd), new Snippet(m1.Snippet2.Document, d2MinBegin, d2MaxEnd));

                                    if (!newMatch.equals(m1) && !newMatch.equals(m2)) {
                                        addSet.add(newMatch);
                                        deleteSet.add(m1);
                                        deleteSet.add(m2);

                                        change = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (Match match : deleteSet) {
                matches.remove(match);
                pairTable.get(match.Snippet1.Document).get(match.Snippet2.Document).remove(match);
            }

            for (Match match : addSet) {
                if (!matches.contains(match)) {
                    matches.add(match);
                    pairTable.get(match.Snippet1.Document).get(match.Snippet2.Document).add(match);
                }
            }
        }
    }
}
