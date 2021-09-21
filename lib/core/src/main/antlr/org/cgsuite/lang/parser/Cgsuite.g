grammar Cgsuite;

options
{
    language = Java;
    output = AST;
    ASTLabelType = CommonTree;
}

tokens
{
    PLUS        = '+';
    MINUS       = '-';
    PLUSMINUS   = '+-';
    AST         = '*';
    FSLASH      = '/';
    DOT         = '.';
    PERCENT     = '%';
    LPAREN      = '(';
    RPAREN      = ')';
    LBRACKET    = '[';
    RBRACKET    = ']';
    LBRACE      = '{';
    RBRACE      = '}';
    DQUOTE      = '"';
    SQUOTE      = '\'';
    COMMA       = ',';
    SEMI        = ';';
    COLON       = ':';
    QUESTION    = '?';
    CARET       = '^';
    VEE         = 'v';
    AMPERSAND   = '&';
    EQUALS      = '==';
    NEQ         = '!=';
    LT          = '<';
    GT          = '>';
    LEQ         = '<=';
    GEQ         = '>=';
    CONFUSED    = '<>';
    LCONFUSED   = '<|';
    GCONFUSED   = '|>';
    COMPARE     = '<=>';
    RARROW      = '->';
    BIGRARROW   = '=>';
    BACKSLASH   = '\\';
    REFEQUALS   = '===';
    REFNEQ      = '!==';

    ASSIGN      = ':=';
    BAD_ASSIGN  = '=';

    DOTDOT      = '..';
    DOTDOTDOT   = '...';

    AND         = 'and';
    AS          = 'as';
    BEGIN       = 'begin';
    BREAK       = 'break';
    BY          = 'by';
    CLASS       = 'class';
    CLEAR       = 'clear';
    CONTINUE    = 'continue';
    DEF         = 'def';
    DO          = 'do';
    ELSE        = 'else';
    ELSEIF      = 'elseif';
    END         = 'end';
    ENUM        = 'enum';
    ERROR       = 'error';
    EXTENDS     = 'extends';
    EXTERNAL    = 'external';
    FALSE       = 'false';
    FINALLY     = 'finally';
    FOR         = 'for';
    FOREACH     = 'foreach';
    FROM        = 'from';
    IF          = 'if';
    IMPORT      = 'import';
    IN          = 'in';
    IS          = 'is';
    LISTOF      = 'listof';
    MAPOF       = 'mapof';
    MUTABLE     = 'mutable';
    NOT         = 'not';
    OP          = 'op';
    OR          = 'or';
    OVERRIDE    = 'override';
    PASS        = 'pass';
    RETURN      = 'return';
    SETOF       = 'setof';
    SINGLETON   = 'singleton';
    STATIC      = 'static';
    SUMOF       = 'sumof';
    SUPER       = 'super';
    SYSTEM      = 'system';
    TABLEOF     = 'tableof';
    THEN        = 'then';
    THIS        = 'this';
    TO          = 'to';
    TRUE        = 'true';
    TRY         = 'try';
    UNARY       = 'unary';
    VAR         = 'var';
    WHERE       = 'where';
    WHILE       = 'while';
    YIELD       = 'yield';

    ARRAY_REFERENCE;
    ARRAY_INDEX_LIST;
    ASN_ANTECEDENT;
    CLASS_VAR;
    COORDINATES;
    DECL_BEGIN;
    DECL_END;
    DECL_ID;
    DECL_OP;
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
    INFIX_OP;
    LOOP_SPEC;
    METHOD_PARAMETER;
    METHOD_PARAMETER_LIST;
    MODIFIERS;
    NODE_LABEL;
    PREAMBLE;
    SCRIPT;
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
    public static final int DOC_COMMENT_CHANNEL = 1;

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
      classModifiers CLASS^ IDENTIFIER LPAREN! methodParameterList RPAREN! extendsClause? declarations END
      { $IDENTIFIER.setType(DECL_ID); $END.setType(DECL_END); }
    | classModifiers CLASS^ IDENTIFIER extendsClause? declarations END
      { $IDENTIFIER.setType(DECL_ID); $END.setType(DECL_END); }
    ;

classModifiers
    : classModifier* -> ^(MODIFIERS classModifier*)
    ;

classModifier
    : MUTABLE | OVERRIDE | SINGLETON | SYSTEM
    ;

extendsClause
    : EXTENDS^ typeSpecifier (COMMA! typeSpecifier)*
    ;

typeSpecifier
    : qualifiedId
    ;

qualifiedId
    : IDENTIFIER (DOT^ IDENTIFIER)*
    ;

declarations
    : declarationChain? -> ^(DECLARATIONS declarationChain?)
    ;

declarationChain
    : (modifiers CLASS) => classDeclaration declarationChain?
    | (modifiers DEF) => defDeclaration declarationChain?
    | (modifiers VAR) => classVarDeclaration SEMI! declarationChain?
    | (STATIC) => staticDeclaration declarationChain?
    | (IF | loopAntecedent | BEGIN) => controlExpression declarationChain?
    | (TRY) => tryStatement declarationChain?
    | statement SEMI! declarationChain?
    ;

staticDeclaration
    : STATIC^ statementSequence END!
    ;
    
classVarDeclaration
    : modifiers VAR classVarInitializer (COMMA classVarInitializer)* -> ^(CLASS_VAR[$VAR] modifiers classVarInitializer*)
    ;

classVarInitializer
    : IDENTIFIER (ASSIGN^ functionExpression)? { $IDENTIFIER.setType(DECL_ID); }
    ;

defDeclaration
    : (modifiers DEF defName LPAREN methodParameterList RPAREN) =>
      modifiers DEF^ defName (LPAREN! methodParameterList RPAREN!)? asClause? defInitializer
    ;

defName
    : IDENTIFIER { $IDENTIFIER.setType(DECL_ID); }
    | OP^ (definableOpCode | definableUnaryOpCode) { $OP.setType(DECL_OP); }
    ;

definableOpCode
    : PLUS | MINUS | AST | FSLASH | PERCENT | CARET | COLON | AMPERSAND
    | standardRelationalToken
    | AND | OR | NOT
    | LBRACKET RBRACKET -> LBRACKET[$LBRACKET, "[]"]
    ;

definableUnaryOpCode
    : UNARY^ (PLUS | MINUS | PLUSMINUS | AST | CARET | VEE)
    ;

defInitializer
    : SEMI!
    | ASSIGN expression SEMI -> ^(STATEMENT_SEQUENCE expression)
    | BEGIN statementSequence END { $BEGIN.setType(DECL_BEGIN); $END.setType(DECL_END); }
    ;

modifiers
    : (OVERRIDE | MUTABLE | STATIC | EXTERNAL)* -> ^(MODIFIERS OVERRIDE* MUTABLE* STATIC* EXTERNAL*)
    ;

opCode
options { greedy = true; }
    : PLUS | MINUS | AST | FSLASH | PERCENT | CARET | COLON | AMPERSAND
    | standardRelationalToken
    | (LBRACKET RBRACKET ASSIGN) => LBRACKET RBRACKET ASSIGN -> OP[$LBRACKET, "[]:="]
    | LBRACKET RBRACKET -> OP[$LBRACKET, "[]"]
    ;
    
methodParameterList
    : (methodParameter (COMMA methodParameter)*)? -> ^(METHOD_PARAMETER_LIST methodParameter*)
    ;
    
methodParameter
    : IDENTIFIER asClause? (questionClause | DOTDOTDOT)?
      -> ^(METHOD_PARAMETER IDENTIFIER asClause? questionClause? DOTDOTDOT?)
    ;

asClause
    : AS^ typeSpecifier
    ;

questionClause
    : QUESTION^ expression?
    ;

enumDeclaration
scope
{
    String name;
}
    : classModifiers ENUM^ IDENTIFIER { $enumDeclaration::name = $IDENTIFIER.text; }
      enumElementList declarations END { $IDENTIFIER.setType(DECL_ID); $END.setType(DECL_END); }
    ;

enumElementList
    : (enumElement (COMMA enumElement)*) SEMI -> ^(DECLARATIONS enumElement*)
    ;

enumElement
    : modifiers IDENTIFIER -> ^(ENUM_ELEMENT modifiers IDENTIFIER)
    ;

script
    : topStatementSequence EOF -> ^(SCRIPT topStatementSequence)
    ;

topStatementSequence
    : topStatementChain? -> ^(STATEMENT_SEQUENCE topStatementChain?)
    ;

topStatementChain
    : (IF | loopAntecedent | BEGIN) => controlExpression topStatementChain?
    | (TRY) => tryStatement topStatementChain?
      // outerDefBlockStatement is parsed separately so that statements with def-block syntax can omit
      // the trailing semicolon (example: "def f(x) begin x + 1 end f(5)" instead of "def f(x) begin x + 1 end; f(5)")
    | (DEF IDENTIFIER LPAREN methodParameterList RPAREN BEGIN) => outerDefBlockStatement topStatementChain?
    | (outerDefStatement | statement) (SEMI topStatementChain?)?
    | SEMI topStatementChain?
    ;

outerDefStatement
    : DEF^ IDENTIFIER LPAREN! methodParameterList RPAREN! ASSIGN! expression
    ;

outerDefBlockStatement
    : DEF^ IDENTIFIER LPAREN! methodParameterList RPAREN! BEGIN! statementSequence END!
      { $BEGIN.setType(DECL_BEGIN); $END.setType(DECL_END); }
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
    | localVarDeclaration
    | expression
    ;

tryStatement
    : TRY^ statementSequence FINALLY! statementSequence END!
    ;

localVarDeclaration
    : VAR^ varInitializer
    ;

varInitializer
    : IDENTIFIER (ASSIGN^ functionExpression)?
    ;

/* TOWARDS AN IMPROVEMENT:

localVarDeclaration
    : VAR^ IDENTIFIER asClause? varAssignmentClause?
    ;

varAssignmentClause
    : assignmentToken^ assignmentExpression
    ;

*/

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
    : functionAntecedent RARROW^ functionExpression
    | orExpression
    ;

functionAntecedent
    : a=IDENTIFIER -> ^(METHOD_PARAMETER_LIST ^(METHOD_PARAMETER $a IDENTIFIER["Object"]))
    | LPAREN! methodParameterList RPAREN!
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
    : infixExpr (relationalToken^ relationalExpr)?
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

// Infix operators such as `3 Max 4`. We need to special case the potentially ambiguous
// "optional postfix" operations, ^ and *. For example, `^ Heat 2` should parse as
// `^.Heat(2)`, whereas `^ Heat` should parse as `^(Heat)` (where `Heat` is a variable).
infixExpr
    : (nonterminalUpstarExpr IDENTIFIER rangeExpr) => nonterminalUpstarExpr IDENTIFIER^ rangeExpr { $IDENTIFIER.setType(INFIX_OP); }
    | rangeExpr (IDENTIFIER^ rangeExpr { $IDENTIFIER.setType(INFIX_OP); })*
    ;

rangeExpr
    : addExpr (DOTDOT^ addExpr (DOTDOT^ addExpr)?)?
    ;

addExpr
    : sidleExpr ((PLUS^ | MINUS^) sidleExpr | binaryPlusMinus^)*
    ;

binaryPlusMinus
    : PLUSMINUS sidleExpr -> ^(PLUS[$PLUSMINUS] ^(PLUSMINUS sidleExpr))
    ;

sidleExpr
    : ordinalSumExpr (AMPERSAND^ ordinalSumExpr)?
    ;

ordinalSumExpr
    : multiplyExpr (COLON^ multiplyExpr)*
    ;

multiplyExpr
    : unaryExpr ((AST^ | FSLASH^ | PERCENT^) unaryExpr)*
    ;

unaryExpr
    : PLUSMINUS^ expExpr
    | MINUS expExpr -> ^(UNARY_MINUS[$MINUS] expExpr)
    | PLUS expExpr -> ^(UNARY_PLUS[$PLUS] expExpr)
    | expExpr
    ;

expExpr
    : postfixExpr (CARET^ expExpr { $CARET.setType(EXP); })?
    ;

postfixExpr
    : (upstarExpr -> upstarExpr)
      ( DOT id=generalizedId -> ^(DOT $postfixExpr $id)
      | x=arrayReference     -> ^(ARRAY_REFERENCE[$x.tree.getToken()] $postfixExpr arrayReference)
      | y=functionCall       -> ^(FUNCTION_CALL[$y.tree.getToken()] $postfixExpr functionCall)
      )*
      ;

arrayReference
    : LBRACKET expression RBRACKET -> ^(ARRAY_INDEX_LIST[$LBRACKET] expression)
    ;

functionCall
    : LPAREN (functionArgument (COMMA functionArgument)*)? RPAREN
      -> ^(FUNCTION_CALL_ARGUMENT_LIST[$LPAREN] functionArgument*)
    ;
        
functionArgument
    : IDENTIFIER BIGRARROW^ expression
    | expression
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

nonterminalUpstarExpr
options
{
    backtrack = true;
    memoize = true;
}
    : (CARET | MULTI_CARET | VEE | MULTI_VEE)^ AST { $AST.setType(UNARY_AST); }
    | (CARET | VEE)^ primaryExpr AST { $AST.setType(UNARY_AST); }
    | CARET | VEE
    | AST { $AST.setType(UNARY_AST); }
    ;
    
primaryExpr
    : THIS
    | TRUE
    | FALSE
    | INTEGER
    | STRING
    | PASS
    | SUPER DOT id=generalizedId { $id.tree.getToken().setText("super$" + $id.tree.getText()); } -> ^(DOT THIS[$SUPER] $id)
    | ERROR^ LPAREN! expression RPAREN!
    | (LPAREN expression COMMA) => LPAREN expression COMMA expression RPAREN -> ^(COORDINATES[$COMMA] expression*)
    | LPAREN! expression RPAREN!
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
        Tree newTree = null;
    }
    : (expressionList SLASHES) => lo=expressionList SLASHES ro=slashExpression
    {
        if ($ro.tree.token.getType() != SLASHES ||
            $ro.tree.token.getText().length() < $SLASHES.getText().length())
        {
            newTree = (CommonTree) adaptor.create($SLASHES);
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
                t = (CommonTree) adaptor.getChild(t, 0);
            }
            CommonTree tLeft  = (CommonTree) adaptor.getChild(t, 0);
            CommonTree tRight = (CommonTree) adaptor.getChild(t, 1);
            CommonTree tRightNew = (CommonTree) adaptor.create($SLASHES);
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
    : ofToken LPAREN expression forLoopAntecedent+ RPAREN
      -> ^(ofToken forLoopAntecedent+ ^(STATEMENT_SEQUENCE expression))
    | MAPOF LPAREN expression BIGRARROW expression forLoopAntecedent+ RPAREN
      -> ^(MAPOF forLoopAntecedent+ ^(STATEMENT_SEQUENCE ^(BIGRARROW expression expression)))
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
      -> ^(LOOP_SPEC[$forClause.tree.getToken()] forClause fromClause toClause? byClause? whileClause? whereClause?)
    ;

forInLoopAntecedent
    : forClause inClause whileClause? whereClause?
      -> ^(LOOP_SPEC[$forClause.tree.getToken()] forClause inClause whileClause? whereClause?)
    ;

whileLoopAntecedent
    : whileClause whereClause?
      -> ^(LOOP_SPEC[$whileClause.tree.getToken()] whileClause whereClause?)
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

INTEGER       : DIGIT+;

MULTI_CARET   : '^' ('^')+;

MULTI_VEE     : 'v' ('v')+;

IDENTIFIER    : ('v'+ (NONV | '_') | NONV) (LETTER | DIGIT | '_')*;

STRING        : DQUOTE (~(DQUOTE|BACKSLASH|'\n'|'\r') | ESCAPE_SEQ)* DQUOTE;

SLASHES       : SLASH+;

// 00A0 = non-breaking space
WHITESPACE    : (' ' | '\t' | '\u00A0' | NEWLINE)+ { $channel = HIDDEN; };

DOC_COMMENT   : '/**' ( ~('*') | '*' ~('/') )* '*/'? { $channel = DOC_COMMENT_CHANNEL; };

SL_COMMENT    : '//' ~('\r'|'\n')* NEWLINE? { $channel = HIDDEN; };

ML_COMMENT    : '/*' ( ~('*') | '*' ~('/')  )* '*/'? { $channel = HIDDEN; };

fragment
DIGIT         : '0'..'9';

fragment
HEX_DIGIT     : '0'..'9' | 'A'..'F' | 'a'..'f';

fragment
LETTER        : 'A'..'Z' | 'a'..'z';

fragment
NONV          : 'A'..'Z' | 'a'..'u' | 'w'..'z';

fragment
SLASH         : '|';

fragment
ESCAPE_SEQ    : BACKSLASH
              ( BACKSLASH
              | 'n'
              | 'r'
              | 't'
              | DQUOTE
              | 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
              )
              ;

fragment
NEWLINE       : '\r'? '\n';
