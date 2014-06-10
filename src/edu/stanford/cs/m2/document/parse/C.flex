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

package edu.stanford.cs.m2.document.parse;

import edu.stanford.cs.m2.document.FilePosition;

/*
 *  This is a JFlex-compatible lexical specification of the ANSI C grammar. The
 *  specification was originally sourced from
 *  http://www.lysator.liu.se/c/ANSI-C-grammar-l.html
 */

%%

%public
%class CParser
%extends Parser
%type Symbol
%unicode
%line
%column
%char

%state STRING

%{
  StringBuffer string = new StringBuffer();

  private Symbol symbol(Token token) {
    return new Symbol(token, new FilePosition(yyline, yycolumn, yychar), yytext());
  }

  private Symbol symbol(Token token, String value) {
    return new Symbol(token, new FilePosition(yyline, yycolumn, yychar), value);
  }
%}

/* Basic character classes */
D                           = [0-9]
L                           = [a-zA-Z_]
H                           = [a-fA-F0-9]
E                           = [Ee][+-]?{D}+
FS                          = (f|F|l|L)
IS                          = (u|U|l|L)*
T                           = \r|\n|\r\n
I                           = [^\r\n]

/* Comments */

Comment                     = {TraditionalComment}
                                | {EndOfLineComment}
TraditionalComment          = "/*" [^*] ~"*/"
                                | "/*" "*"+ "/"
EndOfLineComment            = "//" {I}* {T}

%%
<YYINITIAL> {

    /* Comment */
    {Comment}               { return symbol(CToken.COMMENT); }

    /* Preprocessor directive */
    #[^\r\n]*[\r\n]         { return symbol(CToken.PREPROCESSOR); }

    /* Keywords */
    "auto"                  { return symbol(CToken.AUTO); }
    "break"                 { return symbol(CToken.BREAK); }
    "case"                  { return symbol(CToken.CASE); }
    "char"                  { return symbol(CToken.CHAR); }
    "const"                 { return symbol(CToken.CONST); }
    "continue"              { return symbol(CToken.CONTINUE); }
    "default"               { return symbol(CToken.DEFAULT); }
    "do"                    { return symbol(CToken.DO); }
    "double"                { return symbol(CToken.DOUBLE); }
    "else"                  { return symbol(CToken.ELSE); }
    "enum"                  { return symbol(CToken.ENUM); }
    "extern"                { return symbol(CToken.EXTERN); }
    "float"                 { return symbol(CToken.FLOAT); }
    "for"                   { return symbol(CToken.FOR); }
    "goto"                  { return symbol(CToken.GOTO); }
    "if"                    { return symbol(CToken.IF); }
    "int"                   { return symbol(CToken.INT); }
    "long"                  { return symbol(CToken.LONG); }
    "register"              { return symbol(CToken.REGISTER); }
    "return"                { return symbol(CToken.RETURN); }
    "short"                 { return symbol(CToken.SHORT); }
    "signed"                { return symbol(CToken.SIGNED); }
    "sizeof"                { return symbol(CToken.SIZEOF); }
    "static"                { return symbol(CToken.STATIC); }
    "struct"                { return symbol(CToken.STRUCT); }
    "switch"                { return symbol(CToken.SWITCH); }
    "typedef"               { return symbol(CToken.TYPEDEF); }
    "union"                 { return symbol(CToken.UNION); }
    "unsigned"              { return symbol(CToken.UNSIGNED); }
    "void"                  { return symbol(CToken.VOID); }
    "volatile"              { return symbol(CToken.VOLATILE); }
    "while"                 { return symbol(CToken.WHILE); }

    {L}({L}|{D})*           { return symbol(CToken.IDENTIFIER); }

    0[xX]{H}+{IS}?          { return symbol(CToken.CONSTANT); }
    0{D}+{IS}?              { return symbol(CToken.CONSTANT); }
    {D}+{IS}?               { return symbol(CToken.CONSTANT); }
    L?'(\\.|[^\\'])+'       { return symbol(CToken.CONSTANT); }

    {D}+{E}{FS}?            { return symbol(CToken.CONSTANT); }
    {D}*"."{D}+({E})?{FS}?  { return symbol(CToken.CONSTANT); }
    {D}+"."{D}*({E})?{FS}?  { return symbol(CToken.CONSTANT); }

    \"                      { string.setLength(0); yybegin(STRING); }

    "..."                   { return symbol(CToken.ELLIPSIS); }
    ">>="                   { return symbol(CToken.RIGHT_ASSIGN); }
    "<<="                   { return symbol(CToken.LEFT_ASSIGN); }
    "+="                    { return symbol(CToken.ADD_ASSIGN); }
    "-="                    { return symbol(CToken.SUB_ASSIGN); }
    "*="                    { return symbol(CToken.MUL_ASSIGN); }
    "/="                    { return symbol(CToken.DIV_ASSIGN); }
    "%="                    { return symbol(CToken.MOD_ASSIGN); }
    "&="                    { return symbol(CToken.AND_ASSIGN); }
    "^="                    { return symbol(CToken.XOR_ASSIGN); }
    "|="                    { return symbol(CToken.OR_ASSIGN); }
    ">>"                    { return symbol(CToken.RIGHT_OP); }
    "<<"                    { return symbol(CToken.LEFT_OP); }
    "++"                    { return symbol(CToken.INC_OP); }
    "--"                    { return symbol(CToken.DEC_OP); }
    "->"                    { return symbol(CToken.PTR_OP); }
    "&&"                    { return symbol(CToken.AND_OP); }
    "||"                    { return symbol(CToken.OR_OP); }
    "<="                    { return symbol(CToken.LE_OP); }
    ">="                    { return symbol(CToken.GE_OP); }
    "=="                    { return symbol(CToken.EQ_OP); }
    "!="                    { return symbol(CToken.NE_OP); }
    ";"                     { return symbol(CToken.SEMICOLON); }
    ("{"|"<%")              { return symbol(CToken.LEFT_BRACE); }
    ("}"|"%>")              { return symbol(CToken.RIGHT_BRACE); }
    ","                     { return symbol(CToken.COMMA); }
    ":"                     { return symbol(CToken.COLON); }
    "="                     { return symbol(CToken.ASSIGN); }
    "("                     { return symbol(CToken.LEFT_BRACKET); }
    ")"                     { return symbol(CToken.RIGHT_BRACKET); }
    ("["|"<:")              { return symbol(CToken.LEFT_SQBR); }
    ("]"|":>")              { return symbol(CToken.RIGHT_SQBR); }
    "."                     { return symbol(CToken.PERIOD); }
    "&"                     { return symbol(CToken.AMPERSAND); }
    "!"                     { return symbol(CToken.EXCLAMATION); }
    "~"                     { return symbol(CToken.TILDE); }
    "-"                     { return symbol(CToken.MINUS); }
    "+"                     { return symbol(CToken.PLUS); }
    "*"                     { return symbol(CToken.ASTERISK); }
    "/"                     { return symbol(CToken.SLASH); }
    "%"                     { return symbol(CToken.MOD); }
    "<"                     { return symbol(CToken.LT); }
    ">"                     { return symbol(CToken.GT); }
    "^"                     { return symbol(CToken.HAT); }
    "|"                     { return symbol(CToken.OR); }
    "?"                     { return symbol(CToken.QUESTION); }

    [ \t\v\n\f]             { /* Ignore */ }
    .                       { return symbol(CToken.UNKNOWN); }
}

<STRING> {
  \"                        {
                                yybegin(YYINITIAL);
                                return symbol(CToken.STRING_LITERAL, ' ' + string.toString() + ' ');
                            }
  [^\n\r\"\\]+              { string.append(yytext()); }
  \\t                       { string.append("\\t"); }
  \\n                       { string.append("\\n"); }
  \\r                       { string.append("\\r"); }
  \\\"                      { string.append("\\\""); }
  \\                        { string.append("\\\'"); }
}