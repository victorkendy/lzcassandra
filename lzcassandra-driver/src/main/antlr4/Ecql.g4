grammar Ecql;

statement: select;

select: 'SELECT' select_list 'FROM' from_specification;

select_list: ID'.'ID(','ID'.'ID)*;

from_specification: ID ID;

ID: [a-zA-Z0-9_]+;

WS: [ \r\t\n] -> skip;
