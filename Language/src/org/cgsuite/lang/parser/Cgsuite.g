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
	SQUOTE		= '\'';
	DQUOTE		= '"';
	COMMA		= ',';
	SEMI		= ';';
	COLON		= ':';
	AMPERSAND	= '&';
	TILDE		= '~';
	BANG		= '!';
	QUESTION	= '?';
	CARET       = '^';
	VEE         = 'v';
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
	DO			= 'do';
	ELSE		= 'else';
	ELSEIF		= 'elseif';
	END			= 'end';
    ENUM        = 'enum';
    ERROR       = 'error';
	EXTENDS		= 'extends';
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
    SUPER       = 'super';
    TABLEOF     = 'tableof';
	THEN		= 'then';
	THIS        = 'this';
	TO			= 'to';
	TRUE		= 'true';
    TRY         = 'try';
	VAR         = 'var';
	WHERE		= 'where';
	WHILE		= 'while';
	
	ARRAY_REFERENCE;
    ARRAY_INDEX_LIST;
	ASN_ANTECEDENT;
    DO_IN;
    ENUM_ELEMENT;
    ENUM_ELEMENT_LIST;
    EXP;
	EXPLICIT_LIST;
	EXPLICIT_MAP;
	EXPLICIT_SET;
	EXPRESSION_LIST;
	FUNCTION_CALL;
	FUNCTION_CALL_ARGUMENT_LIST;
	METHOD_PARAMETER_LIST;
	MODIFIERS;
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
	: classModifiers CLASS^ IDENTIFIER extendsClause? javaClause? declaration* END!
	;

classModifiers
    : MUTABLE* -> ^(MODIFIERS MUTABLE*)
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
	
declaration
	: varDeclaration
	| propertyDeclaration
	| methodDeclaration
	;
	
varDeclaration
    : modifiers VAR^ varInitializer (COMMA! varInitializer)* SEMI!
    ;

varInitializer
    : IDENTIFIER (ASSIGN^ functionExpression)?
    ;

propertyDeclaration
	: modifiers PROPERTY^ IDENTIFIER DOT! (GET | SET)
	  (javaClause SEMI! | statementSequence END!)
	;

proptype
    : GET
    | SET
    ;
	
methodDeclaration
	: modifiers METHOD^ generalizedId LPAREN! methodParameterList RPAREN!
	  (javaClause SEMI! | statementSequence END!)
	;
	
modifiers
	: (OVERRIDE | MUTABLE | STATIC)* -> ^(MODIFIERS OVERRIDE* MUTABLE* STATIC*)
	;

opCode
options { greedy = true; }
    : PLUS | MINUS | AST | FSLASH | PERCENT | CARET | NEG | POS
    | standardRelationalToken
    | LBRACKET RBRACKET ASSIGN -> OP[$LBRACKET, "[]:="]
    | LBRACKET RBRACKET -> OP[$LBRACKET, "[]"]
    ;
	
// TODO Require optional last
methodParameterList
	: (methodParameter (COMMA methodParameter)*)? -> ^(METHOD_PARAMETER_LIST methodParameter*)
	;
	
methodParameter
	: a=IDENTIFIER b=IDENTIFIER QUESTION expression? -> ^(QUESTION ^($b $a?) expression?)
    | a=IDENTIFIER QUESTION expression? -> ^(QUESTION $a expression?)
	| a=IDENTIFIER b=IDENTIFIER -> ^($b $a)
	| IDENTIFIER
	;

enumDeclaration
scope
{
    String name;
}
    : modifiers ENUM^ IDENTIFIER {$enumDeclaration::name = $IDENTIFIER.text;} enumElementList declaration* END!
    ;

enumElementList
    : (enumElement (COMMA enumElement)*) SEMI -> ^(ENUM_ELEMENT_LIST enumElement*)
    ;

enumElement
    : IDENTIFIER (functionCall)?
      -> ^(ENUM_ELEMENT[$IDENTIFIER] ^(ASSIGN IDENTIFIER ^(FUNCTION_CALL[$IDENTIFIER] IDENTIFIER[$IDENTIFIER, $enumDeclaration::name] functionCall?)))
    ;

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
    : (IF | inLoopAntecedent DO | doLoopAntecedent DO | BEGIN) => controlExpression statementChain?
    | (TRY) => tryStatement statementChain?
    | statement (SEMI! statementChain?)?
    | SEMI! statementChain?
    ;
	
statement
	: BREAK
	| CONTINUE
	| RETURN^ expression
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
	: addExpr (relationalToken^ relationalExpr)?
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

addExpr
	: unaryAddExpr ((PLUS^ | MINUS^) unaryAddExpr | binaryPlusMinus^)*
	;

binaryPlusMinus
    : plusMinus -> ^(PLUS plusMinus)
    ;
	
unaryAddExpr
    : plusMinus
    | MINUS multiplyExpr -> ^(UNARY_MINUS[$MINUS] multiplyExpr)
    | PLUS multiplyExpr -> ^(UNARY_PLUS[$PLUS] multiplyExpr)
    | multiplyExpr
    ;

plusMinus
options
{
    backtrack = true;
    memoize = true;
}
    : PLUSMINUS LPAREN expression (COMMA expression)* RPAREN -> ^(PLUSMINUS expression*)
    | PLUSMINUS^ multiplyExpr
    ;

multiplyExpr
	: expExpr ((AST^ | FSLASH^ | PERCENT^) expExpr)*
	;

expExpr
	: postfixExpr (CARET^ postfixExpr { $CARET.setType(EXP); })?
	;
	
unaryExpr
	: MINUS unaryExpr -> ^(UNARY_MINUS unaryExpr)
    | PLUS unaryExpr -> ^(UNARY_PLUS unaryExpr)
    | postfixExpr
	;
	
postfixExpr
	: (upstarExpr -> upstarExpr)
	  ( DOT SUPER DOT id=generalizedId { $id.tree.getToken().setText("super$" + $id.tree.getText()); } -> ^(DOT $postfixExpr $id)
      | DOT id=generalizedId  -> ^(DOT $postfixExpr $id)
	  | x=arrayReference-> ^(ARRAY_REFERENCE[((CgsuiteTree) x.getTree()).getToken()] $postfixExpr arrayReference)
	  | y=functionCall	-> ^(FUNCTION_CALL[((CgsuiteTree) y.getTree()).getToken()] $postfixExpr functionCall)
	  )*
	  ;

arrayReference
	: LBRACKET expression (COMMA expression)* RBRACKET
      -> ^(ARRAY_INDEX_LIST[$LBRACKET] expression*)
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
	| (INTEGER DOTDOT) => range
	| INTEGER
    | INF
	| STRING
	| CHAR
    | (IDENTIFIER COLON) => IDENTIFIER COLON^ explicitGame
	| generalizedId
    | PASS
    | SUPER DOT id=generalizedId { $id.tree.getToken().setText("super$" + $id.tree.getText()); } -> ^(DOT THIS[$SUPER] $id)
    | ERROR^ LPAREN! statementSequence RPAREN!
	| LPAREN! statementSequence RPAREN!
    | (LBRACE expressionList SLASHES) => explicitGame
	| (LBRACE expression? BIGRARROW) => explicitMap
	| explicitSet
	| explicitList
    | of
    | controlExpression
	;

explicitGame
    : LBRACE! slashExpression RBRACE!
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
    : ofToken LPAREN expression ( inLoopAntecedent RPAREN -> ^(DO_IN[$ofToken.tree.getToken()] ofToken inLoopAntecedent ^(STATEMENT_SEQUENCE expression))
                                | doLoopAntecedent RPAREN
                                    // TODO These errors aren't being generated quite right
                                    { if ($doLoopAntecedent.tree == null)
                                        throw new RecognitionException(input);
                                    } -> ^(DO[$ofToken.tree.getToken()] ofToken doLoopAntecedent? ^(STATEMENT_SEQUENCE expression))
                                )
    ;

ofToken
    : SETOF | LISTOF | TABLEOF
    ;

expressionList
    : (expression (COMMA expression)*)? -> ^(EXPRESSION_LIST expression*)
    ;

range
	: INTEGER DOTDOT^ INTEGER
	;

controlExpression
	: IF^ expression THEN! statementSequence elseifClause? END!
	| doLoopAntecedent DO^ statementSequence END!
	| inLoopAntecedent DO statementSequence END -> ^(DO_IN[$DO] inLoopAntecedent statementSequence)
    | BEGIN! statementSequence END!
	;

doLoopAntecedent
    : forClause? fromClause? toClause? byClause? whileClause? whereClause?
    ;

inLoopAntecedent
    : forClause inClause whereClause?
    ;

forClause
	: FOR^ IDENTIFIER
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
	| ELSE^ statementSequence
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

CHAR		: SQUOTE (~(SQUOTE|BACKSLASH|'\n'|'\r') | ESCAPE_SEQ) SQUOTE;

SLASHES		: SLASH+;

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
			  | SQUOTE
			  | DQUOTE
			  | 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
			  )
			;

fragment
NEWLINE     : '\r'? '\n';

WHITESPACE  : (' ' | '\t' | NEWLINE)+ { $channel = HIDDEN; };

SL_COMMENT  : '//' ~('\r'|'\n')* NEWLINE { $channel = HIDDEN; };

ML_COMMENT  : '/*' ( ~('*') | '*' ~('/')  )* '*/'? { $channel = HIDDEN; };
