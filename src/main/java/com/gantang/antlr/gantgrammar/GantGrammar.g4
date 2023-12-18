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

// BoolExp：
//  两边都是 bool 则进行布尔约束
//  任意一边有 int 把 int 换成 int>0（代表有无）后进行布尔约束
//BoolCalcExp：
//  两边都是 bool 则进行布尔约束
//  两边都是 int 则使用数值约束
//  两边类型不相同
//    如果一边是&：f1+f2=f3&f4 自动转成 (f1+f2=f3)&(f1+f2=f3)
//    如果一边是|：f1+f2=f3&f4 自动转成 (f1+f2=f3)|(f1+f2=f3)
//BoolVar:
//  两边都是 bool 则进行布尔约束
//  任意一边有 int 把 int 换成 int>0（代表有无）后进行布尔约束
calcexpression : (SUM_FUN | MAX_FUN | MIN_FUN) '(' calcexpression (',' calcexpression)* ')'         #CalcFunc
    | (MOD_FUN) '(' calcexpression ',' calcexpression ')'                                           #CalcFunc
    | (FLOOR_FUN | EVEN_FUN | ODD_FUN) '(' calcexpression ')'                                       #CalcFunc
    | NOT calcexpression                                                                            #BooleVar
    | '(' calcexpression ')'                                                                        #CalcVar
    | VAR                                                                                           #Variable
    | NUMBER                                                                                        #Number
    | calcexpression (MULTIPLY | DIVIDE) calcexpression                                             #CalcVar
    | calcexpression (ADD | SUBTRACT) calcexpression                                                #CalcVar
    | calcexpression (AND) calcexpression                                                           #BoolVar
    | calcexpression (OR) calcexpression                                                            #BoolVar
    | calcexpression ((GE|GT|LE|LT|EQUAL|NOTEQUAL) calcexpression)+                                 #BoolCalcExp
    | calcexpression ('[' NUMBER (',' NUMBER)* ']')                                                 #BoolArrayExp
    | calcexpression ('[' NUMBER '~' NUMBER ']')                                                    #BoolRangeExp
    ;

boolexpression : calcexpression (EQUIVALENCE|IMPLICATION) calcexpression                            #BoolExp
    ;

//数值： 正整数
NUMBER : [0-9]+;
//变量：是字母、数字、'_'的组合
VAR : [a-zA-Z0-9_.]+;
//丢弃空白字符
WS : [ \t\r\n]+ -> skip ;

fragment DIGIT : [0-9]+;
fragment ESC : '\\"' | '\\\\';