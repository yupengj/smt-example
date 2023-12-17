grammar GantGrammar;

@header {
    package com.gantang.grammar.parser;
}

EQUIVALENCE : '<=>';
IMPLICATION : '=>';
AND : '&';
OR : '|';
NOT : '!';
GE : '>=';
GT : '>';
LE : '<=';
LT : '<';
EQUAL : '=';
NOTEQUAL : '<>';
MULTIPLY : '*';
DIVIDE : '/';
ADD : '+';
SUBTRACT : '-';

SUM_FUN : 'SUM';
MAX_FUN : 'MAX';
MIN_FUN : 'MIN';
MOD_FUN : 'MOD';
FLOOR_FUN : 'FLOOR';
EVEN_FUN : 'EVEN';
ODD_FUN : 'ODD';

// calcexpression：计算类表达式，返回一个数值
// boolexpression：布尔类表达式，返回一个 boolean 值
statement : calcexpression              #CalcExpression
    | boolexpression                    #BoolExpression
    ;

calcexpression : (SUM_FUN | MAX_FUN | MIN_FUN) '(' calcexpression (',' calcexpression)* ')'         #CalcFunc
    | (MOD_FUN) '(' calcexpression ',' calcexpression ')'                                           #CalcFunc
    | (FLOOR_FUN | EVEN_FUN | ODD_FUN) '(' calcexpression ')'                                       #CalcFunc
    | '(' calcexpression ')'                                                                        #CalcVar
    | VAR                                                                                           #Variable
    | NUMBER                                                                                        #Number
    | calcexpression  (MULTIPLY | DIVIDE) calcexpression                                            #CalcVar
    | calcexpression (ADD | SUBTRACT) calcexpression                                                #CalcVar
    ;

boolexpression : boolexpression ((EQUIVALENCE|IMPLICATION) boolexpression)+         #BooleExp
    | calcexpression ((EQUIVALENCE|IMPLICATION) calcexpression)+                     #BooleExp
    | calcexpression ((AND|OR) calcexpression)+                                     #BooleExp
    | calcexpression ((GE|GT|LE|LT|EQUAL|NOTEQUAL) calcexpression)+                 #BooleExp
    | boolexpression ((AND|OR) boolexpression)+                                     #BooleExp
    | NOT VAR                                                                       #BooleNotExp
    | NOT boolexpression                                                            #BooleNotExp
    | NOT calcexpression                                                            #BooleNotExp
    | '('boolexpression')'                                                          #BooleExp
    | '('calcexpression')'                                                          #BooleExp
    | VAR                                                                           #BoolVariable
    | calcexpression ('[' NUMBER (',' NUMBER)* ']')                                 #CalcEqual
    | calcexpression ('[' NUMBER '~' NUMBER ']')                                    #CalcRange
    ;
//数值： 正整数
NUMBER : [0-9]+;
//变量：是字母、数字、'_'的组合
VAR : [a-zA-Z0-9_.]+;
//丢弃空白字符
WS : [ \t\r\n]+ -> skip ;

fragment DIGIT : [0-9]+;
fragment ESC : '\\"' | '\\\\';