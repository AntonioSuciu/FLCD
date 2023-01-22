%{
#include "y.tab.h"
#include <stdlib.h>
#include <stdio.h>
%}

%option noyywrap
%option caseless

identifier (_|[a-zA-Z])([a-zA-Z]|[0-9])*
integer (0|([-+]?([1-9][0-9]*)))
string [a-zA-Z ]*
constchar "\'"[a-zA-Z ]"\'"
conststring \"{string}\"
separator " "|"("|")"|"["|"]"|"{"|"}"|";"|"\n"|"\t"|"\r"
whitespace " "|"\n"|"\t"|"\r"
operator "+"|"-"|"*"|"/"|"<="|"<"|">="|">"|"!="|"=="|"="|"%"
reserved "char"|"string"|"int"|"const"|"if"|"else"|"read"|"write"|"while"|"end"|
invalid_id {integer}{identifier}
invalid_string (\'{string}\')|(\'{string})|({string}\')|(\"{string})|({string}\")
invalid_integer ([-+]?0{integer}|[-+]0)
error ({invalid_id}|{invalid_string}|{invalid_integer}|.)

%%

{whitespace} ;
"(" {printf("%s\n",yytext); return PRD;}
")" {printf("%s\n",yytext); return PRI;}
"[" {printf("%s\n",yytext); return PPD;}
"]" {printf("%s\n",yytext); return PPI;}
"{" {printf("%s\n",yytext); return AD;}
"}" {printf("%s\n",yytext); return AI;}
";" {printf("%s\n",yytext); return COLON;}
"<=" {printf("%s\n",yytext); return LE;}
">=" {printf("%s\n",yytext); return GE;}
"==" {printf("%s\n",yytext); return EQ;}
"!=" {printf("%s\n",yytext); return NE;}
"+" {printf("%s\n",yytext); return PLUS;}
"-" {printf("%s\n",yytext); return MINUS;}
"*" {printf("%s\n",yytext); return MULTIPLY;}
"/" {printf("%s\n",yytext); return DIV;}
"<" {printf("%s\n",yytext); return LT;}
">" {printf("%s\n",yytext); return GT;}
"=" {printf("%s\n",yytext); return ASSIGN;}
"%" {printf("%s\n",yytext); return MOD;}
"char" {printf("%s\n",yytext); return CHAR;}
"string" {printf("%s\n",yytext); return STRING;}
"int" {printf("%s\n",yytext); return INT;}
"if" {printf("%s\n",yytext); return IF;}
"else" {printf("%s\n",yytext); return ELSE;}
"read" {printf("%s\n",yytext); return READ;}
"write" {printf("%s\n",yytext); return WRITE;}
"while" {printf("%s\n",yytext); return WHILE;}
{identifier} {printf("%s\n",yytext); return IDENTIFIER;}
{integer} {printf("%s\n",yytext); return CONSTNR;}
{constchar} {printf("%s\n",yytext); return CONSTCHAR;}
{conststring} {printf("%s\n",yytext); return CONSTSTRING;}
{error} printf("Error: %s\n", yytext);

%%