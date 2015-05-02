grammar Cgsuite;

options
{
    language = Java;
    output = AST;
    ASTLabelType = CgsuiteTree;
}

tokens
{
	PLUS		= '+';
	MINUS		= '-';
	PLUSMINUS   = '+-';
	AST         = '*';
	FSLASH		= '/';
	DOT			= '.';
	PERCENT		= '%';
	LPAREN		= '(';
	RPAREN		= ')';
	LBRACKET	= '[';
	RBRACKET	= ']';
	LBRACE		= '{';
	RBRACE		= '}';
	DQUOTE		= '"';
    SQUOTE      = '\'';
	COMMA		= ',';
	SEMI		= ';';
	COLON		= ':';
	QUESTION	= '?';
	CARET       = '^';
	VEE         = 'v';
    AMPERSAND   = '&';
	EQUALS		= '==';
	NEQ			= '!=';
	LT			= '<';
	GT			= '>';
	LEQ			= '<=';
	GEQ			= '>=';
	CONFUSED    = '<>';
    LCONFUSED   = '<|';
    GCONFUSED   = '|>';
	COMPARE		= '<=>';
	RARROW		= '->';
	BIGRARROW	= '=>';
	BACKSLASH	= '\\';
	REFEQUALS	= '===';
	REFNEQ		= '!==';
	
	ASSIGN		= ':=';
    /*
	ASN_PLUS	= '+=';
	ASN_MINUS	= '-=';
	ASN_TIMES	= '*=';
	ASN_DIV		= '/=';
	ASN_MOD		= '%=';
	ASN_AND		= '&=';
	ASN_OR		= '|=';
	ASN_EXP		= '^=';
    */
    BAD_ASSIGN  = '=';
	
	DOTDOT		= '..';
	DOTDOTDOT   = '...';

	AND			= 'and';
	BEGIN		= 'begin';
	BREAK		= 'break';
	BY			= 'by';
    CLASS       = 'class';
    CLEAR       = 'clear';
	CONTINUE	= 'continue';
	DEF         = 'def';
	DO			= 'do';
	ELSE		= 'else';
	ELSEIF		= 'elseif';
	END			= 'end';
    ENUM        = 'enum';
    ERROR       = 'error';
	EXTENDS		= 'extends';
	EXTERNAL    = 'external';
	FALSE		= 'false';
    FINALLY     = 'finally';
	FOR			= 'for';
	FOREACH		= 'foreach';
	FROM		= 'from';
	GET         = 'get';
	IF			= 'if';
    IMPORT		= 'import';
	IN			= 'in';
    INF         = 'inf';
    IS          = 'is';
	JAVA        = 'java';
    LISTOF      = 'listof';
	METHOD		= 'method';
    MUTABLE     = 'mutable';
    NEG         = 'neg';
    NIL         = 'nil';
	NOT			= 'not';
	OP          = 'op';
	OR			= 'or';
    OVERRIDE    = 'override';
    PASS        = 'pass';
	POS         = 'pos';
	PROPERTY	= 'property';
	RETURN		= 'return';
	SET         = 'set';
    SETOF       = 'setof';
    STATIC      = 'static';
    SUMOF       = 'sumof';
    SUPER       = 'super';
    SYSTEM      = 'system';
    TABLEOF     = 'tableof';
	THEN		= 'then';
	THIS        = 'this';
	TO			= 'to';
	TRUE		= 'true';
    TRY         = 'try';
	VAR         = 'var';
	WHERE		= 'where';
	WHILE		= 'while';
	YIELD       = 'yield';
	
	ARRAY_REFERENCE;
    ARRAY_INDEX_LIST;
	ASN_ANTECEDENT;
	COORDINATES;
	DECLARATIONS;
    ENUM_ELEMENT;
    ENUM_ELEMENT_LIST;
    EXP;
	EXPLICIT_LIST;
	EXPLICIT_MAP;
	EXPLICIT_SET;
	EXPRESSION_LIST;
	FUNCTION_CALL;
	FUNCTION_CALL_ARGUMENT_LIST;
	LOOP_SPEC;
	METHOD_PARAMETER_LIST;
	MODIFIERS;
    NODE_LABEL;
    PREAMBLE;
    PROCEDURE_PARAMETER_LIST;
	STATEMENT_SEQUENCE;
    UNARY_AST;
	UNARY_MINUS;
	UNARY_PLUS;
}

@lexer::header
{
    package org.cgsuite.lang.parser;

    import org.cgsuite.lang.parser.CgsuiteParser.SyntaxError;
}

@lexer::members
{
    private List<SyntaxError> errors = new ArrayList<SyntaxError>();

    @Override
    public String getErrorMessage(RecognitionException re, String[] tokenNames)
    {
        String message = super.getErrorMessage(re, tokenNames);
        errors.add(new SyntaxError(re, message));
        return message;
    }

    @Override
    public void emitErrorMessage(String message)
    {
    }

    public List<SyntaxError> getErrors()
    {
        return errors;
    }
}

@header
{
    package org.cgsuite.lang.parser;
}

@members
{
    private List<SyntaxError> errors = new ArrayList<SyntaxError>();

    @Override
    public String getErrorMessage(RecognitionException re, String[] tokenNames)
    {
        String message = super.getErrorMessage(re, tokenNames);
        errors.add(new SyntaxError(re, message));
        return message;
    }

    @Override
    public void emitErrorMessage(String message)
    {
    }

    public List<SyntaxError> getErrors()
    {
        return errors;
    }

    public String getErrorMessageString()
    {
        StringBuilder str = new StringBuilder();
        for (SyntaxError error : errors)
        {
            str.append(error.getMessage());
            str.append("\n");
        }
        return str.toString();
    }

    public static class SyntaxError
    {
        private RecognitionException re;
        private String message;

        public SyntaxError(RecognitionException re, String message)
        {
            this.re = re;
            this.message = message;
        }

        public RecognitionException getException()
        {
            return re;
        }

        public String getMessage()
        {
            return message;
        }

        @Override
        public String toString()
        {
            return "SyntaxError[" + message + "]";
        }
    }
}

compilationUnit
	: cuDeclaration
    | script
	;

cuDeclaration
    : preamble (classDeclaration | enumDeclaration) EOF^
    ;

preamble
    : (importStatement SEMI)* -> ^(PREAMBLE importStatement*)
    ;

importStatement
    : IMPORT^ importClause
    ;

importClause
    : qualifiedId (DOT^ AST)?
    ;

classDeclaration
    : (classModifiers CLASS IDENTIFIER LPAREN methodParameterList RPAREN) =>
	  classModifiers CLASS^ IDENTIFIER LPAREN! methodParameterList RPAREN! extendsClause? declarations END!
	| classModifiers CLASS^ IDENTIFIER extendsClause? declarations END!
	;

classModifiers
    : classModifier* -> ^(MODIFIERS classModifier*)
    ;

classModifier
    : MUTABLE | SYSTEM
    ;
	
extendsClause
	: EXTENDS^ qualifiedId (COMMA! qualifiedId)*
	;

qualifiedId
    : IDENTIFIER (DOT^ IDENTIFIER)*
    ;

javaClause
    : COLON! JAVA^ STRING
    ;

declarations
	: declaration* -> ^(DECLARATIONS declaration*)
	;

declaration
	: (modifiers DEF) => defDeclaration
	| (modifiers VAR) => varDeclaration
	| staticDeclaration
	;

staticDeclaration
    : STATIC^ statementSequence END!
    ;
	
varDeclaration
    : modifiers VAR^ varInitializer (COMMA! varInitializer)* SEMI!
    ;

varInitializer
    : IDENTIFIER (ASSIGN^ functionExpression)?
    ;

propertyDeclaration
	: modifiers PROPERTY^ IDENTIFIER DOT! (GET | setterClause)
	  (javaClause SEMI! | statementSequence END!)
	;

setterClause
    : SET LPAREN requiredParameter RPAREN -> ^(SET ^(METHOD_PARAMETER_LIST requiredParameter))
    ;
	
methodDeclaration
	: modifiers METHOD^ generalizedId LPAREN! methodParameterList RPAREN!
	  (javaClause SEMI! | statementSequence END!)
	;

defDeclaration
    : (modifiers DEF IDENTIFIER LPAREN methodParameterList RPAREN) => modifiers DEF^ IDENTIFIER LPAREN! methodParameterList RPAREN! defInitializer
    | modifiers DEF^ IDENTIFIER defInitializer
    ;

defInitializer
    : (ASSIGN! expression)? SEMI!
    | statementSequence END!
    ;

modifiers
	: (OVERRIDE | MUTABLE | STATIC | EXTERNAL)* -> ^(MODIFIERS OVERRIDE* MUTABLE* STATIC* EXTERNAL*)
	;

opCode
options { greedy = true; }
    : PLUS | MINUS | AST | FSLASH | PERCENT | CARET | COLON | AMPERSAND | NEG | POS
    | standardRelationalToken
    | (LBRACKET RBRACKET ASSIGN) => LBRACKET RBRACKET ASSIGN -> OP[$LBRACKET, "[]:="]
    | LBRACKET RBRACKET -> OP[$LBRACKET, "[]"]
    ;
	
methodParameterList
	: (methodParameter (COMMA methodParameter)*)? -> ^(METHOD_PARAMETER_LIST methodParameter*)
	;
	
methodParameter
    : optionalParameter
    | requiredParameter
    ;

optionalParameter
	: a=IDENTIFIER b=IDENTIFIER QUESTION expression? -> ^(QUESTION ^($b $a?) expression?)
    | a=IDENTIFIER QUESTION expression? -> ^(QUESTION ^($a IDENTIFIER["Object"]) expression?)
    ;

requiredParameter
	: a=IDENTIFIER b=IDENTIFIER DOTDOTDOT? -> ^($b $a DOTDOTDOT?)
	| a=IDENTIFIER DOTDOTDOT? -> ^($a IDENTIFIER["Object"] DOTDOTDOT?)
	;

enumDeclaration
scope
{
    String name;
}
    : classModifiers ENUM^ IDENTIFIER {$enumDeclaration::name = $IDENTIFIER.text;} enumElementList declarations END!
    ;

enumElementList
    : (enumElement (COMMA enumElement)*) SEMI -> ^(ENUM_ELEMENT_LIST enumElement*)
    ;

enumElement
    : modifiers IDENTIFIER^
    ;
/*
enumElement
    : IDENTIFIER (functionCall)?
      -> ^(ENUM_ELEMENT[$IDENTIFIER] ^(ASSIGN IDENTIFIER ^(FUNCTION_CALL[$IDENTIFIER] IDENTIFIER[$IDENTIFIER, $enumDeclaration::name] functionCall?)))
    ;
*/
script
    : block EOF^
    ;

block
    : statementSequence
    ;
	
statementSequence
	: statementChain? -> ^(STATEMENT_SEQUENCE statementChain?)
	;

statementChain
    : (IF | loopAntecedent | BEGIN) => controlExpression statementChain?
    | (TRY) => tryStatement statementChain?
    | statement (SEMI statementChain?)?
    | SEMI statementChain?
    ;
	
statement
	: BREAK
	| CONTINUE
	| RETURN^ expression?
    | CLEAR
    | tryStatement
    | expression
    ;

tryStatement
    : TRY^ statementSequence FINALLY! statementSequence END!
    ;

expression
	: assignmentExpression
	;

assignmentExpression
	: functionExpression (assignmentToken^ assignmentExpression)?
	;
	
assignmentToken
	: ASSIGN
	;
	
functionExpression
	: procedureParameterList RARROW^ functionExpression
    | orExpression
	;

procedureParameterList
    : IDENTIFIER -> ^(PROCEDURE_PARAMETER_LIST IDENTIFIER)
    | LPAREN (IDENTIFIER (COMMA IDENTIFIER)*)? RPAREN -> ^(PROCEDURE_PARAMETER_LIST IDENTIFIER*)
    ;
	
orExpression
	: andExpression (OR^ orExpression)?
	;

andExpression
	: notExpr (AND^ andExpression)?
	;

notExpr
    : NOT^ notExpr
    | isExpr
    ;

isExpr
    : relationalExpr (IS^ relationalExpr)?
    ;

relationalExpr
	: rangeExpr (relationalToken^ relationalExpr)?
	;

relationalToken
	: REFEQUALS
	| REFNEQ
	| standardRelationalToken
	;
	
standardRelationalToken
    : EQUALS
	| NEQ
	| LT
	| GT
	| LEQ
	| GEQ
    | CONFUSED
    | LCONFUSED
    | GCONFUSED
	| COMPARE
	;

rangeExpr
    : addExpr (DOTDOT^ addExpr)?
    ;

addExpr
	: unaryAddExpr ((PLUS^ | MINUS^) unaryAddExpr | binaryPlusMinus^)*
	;

binaryPlusMinus
    : plusMinus -> ^(PLUS plusMinus)
    ;
	
unaryAddExpr
    : plusMinus
    | MINUS sidleExpr -> ^(UNARY_MINUS[$MINUS] sidleExpr)
    | PLUS sidleExpr -> ^(UNARY_PLUS[$PLUS] sidleExpr)
    | sidleExpr
    ;

plusMinus
options
{
    backtrack = true;
    memoize = true;
}
    : PLUSMINUS LPAREN expression (COMMA expression)* RPAREN -> ^(PLUSMINUS expression*)
    | PLUSMINUS^ sidleExpr
    ;

sidleExpr
    : ordinalSumExpr (AMPERSAND^ ordinalSumExpr)?
    ;

ordinalSumExpr
    : multiplyExpr (COLON^ multiplyExpr)*
    ;

multiplyExpr
	: expExpr ((AST^ | FSLASH^ | PERCENT^) expExpr)*
	;

expExpr
	: postfixExpr (CARET^ postfixExpr { $CARET.setType(EXP); })?
	;
	
postfixExpr
	: (upstarExpr -> upstarExpr)
	  ( DOT SUPER DOT id=generalizedId { $id.tree.getToken().setText("super$" + $id.tree.getText()); } -> ^(DOT $postfixExpr $id)
      | DOT id=generalizedId  -> ^(DOT $postfixExpr $id)
	  | x=arrayReference-> ^(ARRAY_REFERENCE[$x.tree.getToken()] $postfixExpr arrayReference)
	  | y=functionCall	-> ^(FUNCTION_CALL[$y.tree.getToken()] $postfixExpr functionCall)
	  )*
	  ;

arrayReference
	: LBRACKET! expression RBRACKET!
	;

functionCall
	: LPAREN (functionArgument (COMMA functionArgument)*)? RPAREN
      -> ^(FUNCTION_CALL_ARGUMENT_LIST[$LPAREN] functionArgument*)
	;
		
functionArgument
	: (IDENTIFIER BIGRARROW^)? expression
	;
	
upstarExpr
options
{
    backtrack = true;
    memoize = true;
}
    : (CARET | MULTI_CARET | VEE | MULTI_VEE)^ starExpr
    | (CARET | VEE)^ primaryExpr starExpr
    | (CARET | VEE)^ primaryExpr
    | starExpr
    | CARET | MULTI_CARET | VEE | MULTI_VEE
    | primaryExpr
    ;

starExpr
options
{
    backtrack = true;
    memoize = true;
}
    : AST primaryExpr -> ^(UNARY_AST[$AST] primaryExpr)
    | AST -> UNARY_AST[$AST]
    ;
	
primaryExpr
	: NIL
	| THIS
	| TRUE
	| FALSE
	| INTEGER
    | INF
	| STRING
    | PASS
    | SUPER DOT id=generalizedId { $id.tree.getToken().setText("super$" + $id.tree.getText()); } -> ^(DOT THIS[$SUPER] $id)
    | ERROR^ LPAREN! expression RPAREN!
    | (LPAREN expression COMMA) => LPAREN expression COMMA expression RPAREN -> ^(COORDINATES expression*)
	| LPAREN! statementSequence RPAREN!
    | (IDENTIFIER? SQUOTE? LBRACE expressionList SLASHES) => explicitGame
	| (LBRACE expression? BIGRARROW) => explicitMap
	| generalizedId
	| explicitSet
	| explicitList
    | of
    | controlExpression
	;

explicitGame
    : IDENTIFIER explicitGameBraces -> ^(NODE_LABEL IDENTIFIER explicitGameBraces)
    | explicitGameBraces
    ;

explicitGameBraces
    : SQUOTE^ LBRACE! slashExpression RBRACE! SQUOTE!
    | LBRACE! slashExpression RBRACE!
    ;

slashExpression
    @init
    {
        CommonTree newTree = null;
    }
    : (expressionList SLASHES) => lo=expressionList SLASHES ro=slashExpression
    {
        if ($ro.tree.token.getType() != SLASHES ||
            $ro.tree.token.getText().length() < $SLASHES.getText().length())
        {
            newTree = (CgsuiteTree) adaptor.create($SLASHES);
            adaptor.addChild(newTree, $lo.tree);
            adaptor.addChild(newTree, $ro.tree);
        }
        else
        {
            CommonTree t = $ro.tree;
            while (true)
            {
                if ($SLASHES.getText().length() == t.getText().length())
                {
                    throw new RecognitionException(input);  // Ambiguous pattern of slashes.
                }
                else if (t.getChild(0).getType() != SLASHES ||
                         t.getChild(0).getText().length() < $SLASHES.getText().length())
                {
                    break;
                }
                t = (CgsuiteTree) adaptor.getChild(t, 0);
            }
            CommonTree tLeft  = (CgsuiteTree) adaptor.getChild(t, 0);
            CommonTree tRight = (CgsuiteTree) adaptor.getChild(t, 1);
            CommonTree tRightNew = (CgsuiteTree) adaptor.create($SLASHES);
            adaptor.addChild(tRightNew, $lo.tree);
            adaptor.addChild(tRightNew, tLeft);
            adaptor.setChild(t, 0, tRightNew);
            adaptor.setChild(t, 1, tRight);
            newTree = $ro.tree;
        }
    } -> {newTree}
    | lo=expressionList -> $lo
    ;

explicitMap
	: LBRACE (mapEntry (COMMA mapEntry)* | BIGRARROW) RBRACE -> ^(EXPLICIT_MAP mapEntry*)
	;

mapEntry
	: expression BIGRARROW^ expression
	;

explicitSet
	: LBRACE (expression (COMMA expression)*)? RBRACE -> ^(EXPLICIT_SET expression*)
	;

explicitList
	: LBRACKET (expression (COMMA expression)*)? RBRACKET -> ^(EXPLICIT_LIST expression*)
	;

of
    : ofToken LPAREN expression ( (forClause IN) => forInLoopAntecedent RPAREN -> ^(DO[$ofToken.tree.getToken()] ofToken forInLoopAntecedent ^(STATEMENT_SEQUENCE expression))
                                | forFromLoopAntecedent RPAREN
                                    // TODO These errors aren't being generated quite right
                                    { if ($forFromLoopAntecedent.tree == null)
                                        throw new RecognitionException(input);
                                    } -> ^(DO[$ofToken.tree.getToken()] ofToken forFromLoopAntecedent? ^(STATEMENT_SEQUENCE expression))
                                )
    ;

ofToken
    : SETOF | LISTOF | TABLEOF | SUMOF
    ;

expressionList
    : (expression (COMMA expression)*)? -> ^(EXPRESSION_LIST expression*)
    ;

controlExpression
	: IF^ expression THEN! statementSequence elseifClause? END!
	| loopAntecedent (forLoopAntecedent)* (DO^ | YIELD^) statementSequence END!
    | BEGIN! statementSequence END!
	;

loopAntecedent
    : forLoopAntecedent
    | whileLoopAntecedent
    ;

forLoopAntecedent
    : (forClause FROM) => forFromLoopAntecedent
    | forInLoopAntecedent
    ;

forFromLoopAntecedent
    : forClause fromClause toClause? byClause? whileClause? whereClause?
      -> ^(LOOP_SPEC forClause fromClause toClause? byClause? whileClause? whereClause?)
    ;

forInLoopAntecedent
    : forClause inClause whileClause? whereClause?
      -> ^(LOOP_SPEC forClause inClause whileClause? whereClause?)
    ;

whileLoopAntecedent
    : whileClause whereClause?
      -> ^(LOOP_SPEC whileClause whereClause?)
    ;

forClause
    : FOR^ expression
    ;

fromClause
    : FROM^ expression
    ;

toClause
    : TO^ expression
    ;

byClause
    : BY^ expression
    ;
	
whileClause
    : WHILE^ expression
	;

whereClause
    : WHERE^ expression
    ;

inClause
    : IN^ expression
    ;

elseifClause
	: ELSEIF^ expression THEN! statementSequence elseifClause?
	| ELSE! statementSequence
	;

generalizedId
    : IDENTIFIER
    | OP opc=opCode -> IDENTIFIER[$OP, $OP.getText() + " " + $opc.tree.getText()]
    ;

INTEGER		: DIGIT+;

MULTI_CARET : '^' ('^')+;

MULTI_VEE   : 'v' ('v')+;

IDENTIFIER	: 'v'* NONV (LETTER | DIGIT)*;

STRING		: DQUOTE (~(DQUOTE|BACKSLASH|'\n'|'\r') | ESCAPE_SEQ)* DQUOTE;

SLASHES		: SLASH+;

// 00A0 = non-breaking space
WHITESPACE  : (' ' | '\t' | '\u00A0' | NEWLINE)+ { $channel = HIDDEN; };

SL_COMMENT  : '//' ~('\r'|'\n')* NEWLINE? { $channel = HIDDEN; };

ML_COMMENT  : '/*' ( ~('*') | '*' ~('/')  )* '*/'? { $channel = HIDDEN; };

fragment
DIGIT		: '0'..'9';

fragment
HEX_DIGIT	: '0'..'9' | 'A'..'F' | 'a'..'f';

fragment
LETTER		: 'A'..'Z' | 'a'..'z' | '_';

fragment
NONV        : 'A'..'Z' | 'a'..'u' | 'w'..'z' | '_';

fragment
SLASH		: '|';

fragment
ESCAPE_SEQ	: BACKSLASH
			  ( BACKSLASH
			  | 'n'
			  | 'r'
			  | 't'
			  | DQUOTE
			  | 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
			  )
			;

fragment
NEWLINE     : '\r'? '\n';
