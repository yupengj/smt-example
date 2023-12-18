grammar GantGrammar;

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
IN : 'in';

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
COUNT : 'COUNT';

// calcexpression：计算类表达式，返回一个数值
// boolexpression：布尔类表达式，返回一个 boolean 值
// boolcalceexpression：布尔类表达式，返回一个 boolean 值，两个布尔值推导关系
statement : calcexpression  #CalcExpression
    | boolcalceexpression   #BoolCalcExpression
    | boolexpression        #BoolExpression
    ;

// 计算表达式
calcexpression : (SUM_FUN | MAX_FUN | MIN_FUN) '(' calcexpression (',' calcexpression)* ')'         #CalcFunc
    | (MOD_FUN) '(' calcexpression ',' calcexpression ')'                                           #CalcFunc
    | (FLOOR_FUN | EVEN_FUN | ODD_FUN | COUNT) '(' calcexpression ')'                               #CalcFunc
    | '(' calcexpression ')'                                                                        #CalcVar
    | VAR                                                                                           #Variable
    | NUMBER                                                                                        #Number
    | calcexpression (MULTIPLY | DIVIDE) calcexpression                                             #CalcVar
    | calcexpression (ADD | SUBTRACT) calcexpression                                                #CalcVar
    ;

// 布尔推导表达式
boolcalceexpression : boolexpression (EQUIVALENCE|IMPLICATION) boolexpression                       #BoolCalcExp
    ;

// 布尔表达式
boolexpression : calcexpression ((GE|GT|LE|LT|EQUAL|NOTEQUAL) calcexpression)+                      #BoolVar
    | NOT boolexpression                                                                            #BoolVar
    | '(' boolexpression ')'                                                                        #BoolVar
    | boolexpression (AND) boolexpression                                                           #BoolVar
    | boolexpression (OR) boolexpression                                                            #BoolVar
    | calcexpression ('[' NUMBER '~' NUMBER ']')                                                    #BoolAndRangeVar
    | calcexpression ('[' calcexpression (',' calcexpression)* ']')                                 #BoolOrArrayVar
    | calcexpression IN ('[' calcexpression (',' calcexpression)* ']')                              #BoolOrArrayVar
    ;

//数值： 正整数
NUMBER : [0-9]+;
//变量：是字母、数字、'_'的组合
VAR : [a-zA-Z0-9_.]+;
//丢弃空白字符
WS : [ \t\r\n]+ -> skip ;

fragment ESC : '\\"' | '\\\\';