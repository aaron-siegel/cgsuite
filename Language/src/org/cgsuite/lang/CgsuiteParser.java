// $ANTLR 3.3 Nov 30, 2010 12:45:30 C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g 2011-02-18 14:07:18

    package org.cgsuite.lang;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

@SuppressWarnings({"unchecked","all"}) public class CgsuiteParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PLUS", "MINUS", "PLUSMINUS", "AST", "FSLASH", "DOT", "EXP", "PERCENT", "UNDERSCORE", "LPAREN", "RPAREN", "LBRACKET", "RBRACKET", "LBRACE", "RBRACE", "SQUOTE", "DQUOTE", "COMMA", "SEMI", "COLON", "AMPERSAND", "TILDE", "BANG", "QUESTION", "CARET", "CARETCARET", "VEE", "VEEVEE", "EQUALS", "NEQ", "LT", "GT", "LEQ", "GEQ", "CONFUSED", "COMPARE", "RARROW", "BIGRARROW", "BACKSLASH", "REFEQUALS", "REFNEQ", "ASSIGN", "ASN_PLUS", "ASN_MINUS", "ASN_TIMES", "ASN_DIV", "ASN_MOD", "ASN_AND", "ASN_OR", "ASN_XOR", "ASN_EXP", "DOTDOT", "DOTDOTDOT", "AND", "BEGIN", "BREAK", "BY", "CLASS", "CLEAR", "CONTINUE", "DO", "ELSE", "ELSEIF", "END", "ENUM", "EXTENDS", "FALSE", "FOR", "FOREACH", "FROM", "GET", "IF", "IMMUTABLE", "IN", "JAVA", "METHOD", "NEG", "NIL", "NOT", "OP", "OR", "POS", "PRIVATE", "PROPERTY", "PROTECTED", "PUBLIC", "RETURN", "SET", "STATIC", "THEN", "THIS", "TO", "TRUE", "VAR", "WHERE", "WHILE", "ARRAY_REFERENCE", "ARRAY_INDEX_LIST", "ASN_ANTECEDENT", "ENUM_ELEMENT", "ENUM_ELEMENT_LIST", "EXPLICIT_LIST", "EXPLICIT_MAP", "EXPLICIT_SET", "EXPRESSION_LIST", "FUNCTION_CALL", "FUNCTION_CALL_ARGUMENT_LIST", "METHOD_PARAMETER_LIST", "MODIFIERS", "MULTI_CARET", "MULTI_VEE", "PROCEDURE_PARAMETER_LIST", "STATEMENT_SEQUENCE", "UNARY_AST", "UNARY_MINUS", "UNARY_PLUS", "IDENTIFIER", "STRING", "INTEGER", "CHAR", "SLASHES", "DIGIT", "LETTER", "ESCAPE_SEQ", "SLASH", "HEX_DIGIT", "UC_LETTER", "LC_LETTER", "NEWLINE", "WHITESPACE", "SL_COMMENT", "ML_COMMENT"
    };
    public static final int EOF=-1;
    public static final int PLUS=4;
    public static final int MINUS=5;
    public static final int PLUSMINUS=6;
    public static final int AST=7;
    public static final int FSLASH=8;
    public static final int DOT=9;
    public static final int EXP=10;
    public static final int PERCENT=11;
    public static final int UNDERSCORE=12;
    public static final int LPAREN=13;
    public static final int RPAREN=14;
    public static final int LBRACKET=15;
    public static final int RBRACKET=16;
    public static final int LBRACE=17;
    public static final int RBRACE=18;
    public static final int SQUOTE=19;
    public static final int DQUOTE=20;
    public static final int COMMA=21;
    public static final int SEMI=22;
    public static final int COLON=23;
    public static final int AMPERSAND=24;
    public static final int TILDE=25;
    public static final int BANG=26;
    public static final int QUESTION=27;
    public static final int CARET=28;
    public static final int CARETCARET=29;
    public static final int VEE=30;
    public static final int VEEVEE=31;
    public static final int EQUALS=32;
    public static final int NEQ=33;
    public static final int LT=34;
    public static final int GT=35;
    public static final int LEQ=36;
    public static final int GEQ=37;
    public static final int CONFUSED=38;
    public static final int COMPARE=39;
    public static final int RARROW=40;
    public static final int BIGRARROW=41;
    public static final int BACKSLASH=42;
    public static final int REFEQUALS=43;
    public static final int REFNEQ=44;
    public static final int ASSIGN=45;
    public static final int ASN_PLUS=46;
    public static final int ASN_MINUS=47;
    public static final int ASN_TIMES=48;
    public static final int ASN_DIV=49;
    public static final int ASN_MOD=50;
    public static final int ASN_AND=51;
    public static final int ASN_OR=52;
    public static final int ASN_XOR=53;
    public static final int ASN_EXP=54;
    public static final int DOTDOT=55;
    public static final int DOTDOTDOT=56;
    public static final int AND=57;
    public static final int BEGIN=58;
    public static final int BREAK=59;
    public static final int BY=60;
    public static final int CLASS=61;
    public static final int CLEAR=62;
    public static final int CONTINUE=63;
    public static final int DO=64;
    public static final int ELSE=65;
    public static final int ELSEIF=66;
    public static final int END=67;
    public static final int ENUM=68;
    public static final int EXTENDS=69;
    public static final int FALSE=70;
    public static final int FOR=71;
    public static final int FOREACH=72;
    public static final int FROM=73;
    public static final int GET=74;
    public static final int IF=75;
    public static final int IMMUTABLE=76;
    public static final int IN=77;
    public static final int JAVA=78;
    public static final int METHOD=79;
    public static final int NEG=80;
    public static final int NIL=81;
    public static final int NOT=82;
    public static final int OP=83;
    public static final int OR=84;
    public static final int POS=85;
    public static final int PRIVATE=86;
    public static final int PROPERTY=87;
    public static final int PROTECTED=88;
    public static final int PUBLIC=89;
    public static final int RETURN=90;
    public static final int SET=91;
    public static final int STATIC=92;
    public static final int THEN=93;
    public static final int THIS=94;
    public static final int TO=95;
    public static final int TRUE=96;
    public static final int VAR=97;
    public static final int WHERE=98;
    public static final int WHILE=99;
    public static final int ARRAY_REFERENCE=100;
    public static final int ARRAY_INDEX_LIST=101;
    public static final int ASN_ANTECEDENT=102;
    public static final int ENUM_ELEMENT=103;
    public static final int ENUM_ELEMENT_LIST=104;
    public static final int EXPLICIT_LIST=105;
    public static final int EXPLICIT_MAP=106;
    public static final int EXPLICIT_SET=107;
    public static final int EXPRESSION_LIST=108;
    public static final int FUNCTION_CALL=109;
    public static final int FUNCTION_CALL_ARGUMENT_LIST=110;
    public static final int METHOD_PARAMETER_LIST=111;
    public static final int MODIFIERS=112;
    public static final int MULTI_CARET=113;
    public static final int MULTI_VEE=114;
    public static final int PROCEDURE_PARAMETER_LIST=115;
    public static final int STATEMENT_SEQUENCE=116;
    public static final int UNARY_AST=117;
    public static final int UNARY_MINUS=118;
    public static final int UNARY_PLUS=119;
    public static final int IDENTIFIER=120;
    public static final int STRING=121;
    public static final int INTEGER=122;
    public static final int CHAR=123;
    public static final int SLASHES=124;
    public static final int DIGIT=125;
    public static final int LETTER=126;
    public static final int ESCAPE_SEQ=127;
    public static final int SLASH=128;
    public static final int HEX_DIGIT=129;
    public static final int UC_LETTER=130;
    public static final int LC_LETTER=131;
    public static final int NEWLINE=132;
    public static final int WHITESPACE=133;
    public static final int SL_COMMENT=134;
    public static final int ML_COMMENT=135;

    // delegates
    // delegators


        public CgsuiteParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public CgsuiteParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
            this.state.ruleMemo = new HashMap[76+1];
             
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return CgsuiteParser.tokenNames; }
    public String getGrammarFileName() { return "C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g"; }


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
        }


    public static class compilationUnit_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "compilationUnit"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:202:1: compilationUnit : ( classDeclaration | enumDeclaration ) EOF ;
    public final CgsuiteParser.compilationUnit_return compilationUnit() throws RecognitionException {
        CgsuiteParser.compilationUnit_return retval = new CgsuiteParser.compilationUnit_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token EOF3=null;
        CgsuiteParser.classDeclaration_return classDeclaration1 = null;

        CgsuiteParser.enumDeclaration_return enumDeclaration2 = null;


        CgsuiteTree EOF3_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:203:2: ( ( classDeclaration | enumDeclaration ) EOF )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:203:4: ( classDeclaration | enumDeclaration ) EOF
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:203:4: ( classDeclaration | enumDeclaration )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==CLASS) ) {
                alt1=1;
            }
            else if ( (LA1_0==ENUM||LA1_0==IMMUTABLE||LA1_0==METHOD||(LA1_0>=PRIVATE && LA1_0<=PUBLIC)||LA1_0==STATIC||LA1_0==VAR) ) {
                alt1=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:203:5: classDeclaration
                    {
                    pushFollow(FOLLOW_classDeclaration_in_compilationUnit1173);
                    classDeclaration1=classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classDeclaration1.getTree());

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:203:24: enumDeclaration
                    {
                    pushFollow(FOLLOW_enumDeclaration_in_compilationUnit1177);
                    enumDeclaration2=enumDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumDeclaration2.getTree());

                    }
                    break;

            }

            EOF3=(Token)match(input,EOF,FOLLOW_EOF_in_compilationUnit1180); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EOF3_tree = (CgsuiteTree)adaptor.create(EOF3);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(EOF3_tree, root_0);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "compilationUnit"

    public static class classDeclaration_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "classDeclaration"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:206:1: classDeclaration : CLASS IDENTIFIER ( extendsClause )? ( javaClause )? ( declaration )* END ;
    public final CgsuiteParser.classDeclaration_return classDeclaration() throws RecognitionException {
        CgsuiteParser.classDeclaration_return retval = new CgsuiteParser.classDeclaration_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token CLASS4=null;
        Token IDENTIFIER5=null;
        Token END9=null;
        CgsuiteParser.extendsClause_return extendsClause6 = null;

        CgsuiteParser.javaClause_return javaClause7 = null;

        CgsuiteParser.declaration_return declaration8 = null;


        CgsuiteTree CLASS4_tree=null;
        CgsuiteTree IDENTIFIER5_tree=null;
        CgsuiteTree END9_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:207:2: ( CLASS IDENTIFIER ( extendsClause )? ( javaClause )? ( declaration )* END )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:207:4: CLASS IDENTIFIER ( extendsClause )? ( javaClause )? ( declaration )* END
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            CLASS4=(Token)match(input,CLASS,FOLLOW_CLASS_in_classDeclaration1192); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CLASS4_tree = (CgsuiteTree)adaptor.create(CLASS4);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(CLASS4_tree, root_0);
            }
            IDENTIFIER5=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classDeclaration1195); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER5_tree = (CgsuiteTree)adaptor.create(IDENTIFIER5);
            adaptor.addChild(root_0, IDENTIFIER5_tree);
            }
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:207:22: ( extendsClause )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==EXTENDS) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:207:22: extendsClause
                    {
                    pushFollow(FOLLOW_extendsClause_in_classDeclaration1197);
                    extendsClause6=extendsClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, extendsClause6.getTree());

                    }
                    break;

            }

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:207:37: ( javaClause )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==COLON) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:207:37: javaClause
                    {
                    pushFollow(FOLLOW_javaClause_in_classDeclaration1200);
                    javaClause7=javaClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, javaClause7.getTree());

                    }
                    break;

            }

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:207:49: ( declaration )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==ENUM||LA4_0==IMMUTABLE||LA4_0==METHOD||(LA4_0>=PRIVATE && LA4_0<=PUBLIC)||LA4_0==STATIC||LA4_0==VAR) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:207:49: declaration
            	    {
            	    pushFollow(FOLLOW_declaration_in_classDeclaration1203);
            	    declaration8=declaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, declaration8.getTree());

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            END9=(Token)match(input,END,FOLLOW_END_in_classDeclaration1206); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "classDeclaration"

    public static class extendsClause_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "extendsClause"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:210:1: extendsClause : EXTENDS IDENTIFIER ( COMMA IDENTIFIER )* ;
    public final CgsuiteParser.extendsClause_return extendsClause() throws RecognitionException {
        CgsuiteParser.extendsClause_return retval = new CgsuiteParser.extendsClause_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token EXTENDS10=null;
        Token IDENTIFIER11=null;
        Token COMMA12=null;
        Token IDENTIFIER13=null;

        CgsuiteTree EXTENDS10_tree=null;
        CgsuiteTree IDENTIFIER11_tree=null;
        CgsuiteTree COMMA12_tree=null;
        CgsuiteTree IDENTIFIER13_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:211:2: ( EXTENDS IDENTIFIER ( COMMA IDENTIFIER )* )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:211:4: EXTENDS IDENTIFIER ( COMMA IDENTIFIER )*
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            EXTENDS10=(Token)match(input,EXTENDS,FOLLOW_EXTENDS_in_extendsClause1219); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EXTENDS10_tree = (CgsuiteTree)adaptor.create(EXTENDS10);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(EXTENDS10_tree, root_0);
            }
            IDENTIFIER11=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_extendsClause1222); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER11_tree = (CgsuiteTree)adaptor.create(IDENTIFIER11);
            adaptor.addChild(root_0, IDENTIFIER11_tree);
            }
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:211:24: ( COMMA IDENTIFIER )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==COMMA) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:211:25: COMMA IDENTIFIER
            	    {
            	    COMMA12=(Token)match(input,COMMA,FOLLOW_COMMA_in_extendsClause1225); if (state.failed) return retval;
            	    IDENTIFIER13=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_extendsClause1228); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    IDENTIFIER13_tree = (CgsuiteTree)adaptor.create(IDENTIFIER13);
            	    adaptor.addChild(root_0, IDENTIFIER13_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "extendsClause"

    public static class javaClause_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "javaClause"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:214:1: javaClause : COLON JAVA STRING ;
    public final CgsuiteParser.javaClause_return javaClause() throws RecognitionException {
        CgsuiteParser.javaClause_return retval = new CgsuiteParser.javaClause_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token COLON14=null;
        Token JAVA15=null;
        Token STRING16=null;

        CgsuiteTree COLON14_tree=null;
        CgsuiteTree JAVA15_tree=null;
        CgsuiteTree STRING16_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:215:5: ( COLON JAVA STRING )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:215:7: COLON JAVA STRING
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            COLON14=(Token)match(input,COLON,FOLLOW_COLON_in_javaClause1244); if (state.failed) return retval;
            JAVA15=(Token)match(input,JAVA,FOLLOW_JAVA_in_javaClause1247); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            JAVA15_tree = (CgsuiteTree)adaptor.create(JAVA15);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(JAVA15_tree, root_0);
            }
            STRING16=(Token)match(input,STRING,FOLLOW_STRING_in_javaClause1250); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            STRING16_tree = (CgsuiteTree)adaptor.create(STRING16);
            adaptor.addChild(root_0, STRING16_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "javaClause"

    public static class declaration_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "declaration"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:218:1: declaration : ( varDeclaration | propertyDeclaration | methodDeclaration );
    public final CgsuiteParser.declaration_return declaration() throws RecognitionException {
        CgsuiteParser.declaration_return retval = new CgsuiteParser.declaration_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        CgsuiteParser.varDeclaration_return varDeclaration17 = null;

        CgsuiteParser.propertyDeclaration_return propertyDeclaration18 = null;

        CgsuiteParser.methodDeclaration_return methodDeclaration19 = null;



        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:219:2: ( varDeclaration | propertyDeclaration | methodDeclaration )
            int alt6=3;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:219:4: varDeclaration
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_varDeclaration_in_declaration1265);
                    varDeclaration17=varDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, varDeclaration17.getTree());

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:220:4: propertyDeclaration
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_propertyDeclaration_in_declaration1270);
                    propertyDeclaration18=propertyDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, propertyDeclaration18.getTree());

                    }
                    break;
                case 3 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:221:4: methodDeclaration
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_methodDeclaration_in_declaration1275);
                    methodDeclaration19=methodDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, methodDeclaration19.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "declaration"

    public static class varDeclaration_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "varDeclaration"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:224:1: varDeclaration : modifiers VAR IDENTIFIER SEMI ;
    public final CgsuiteParser.varDeclaration_return varDeclaration() throws RecognitionException {
        CgsuiteParser.varDeclaration_return retval = new CgsuiteParser.varDeclaration_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token VAR21=null;
        Token IDENTIFIER22=null;
        Token SEMI23=null;
        CgsuiteParser.modifiers_return modifiers20 = null;


        CgsuiteTree VAR21_tree=null;
        CgsuiteTree IDENTIFIER22_tree=null;
        CgsuiteTree SEMI23_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:225:5: ( modifiers VAR IDENTIFIER SEMI )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:225:7: modifiers VAR IDENTIFIER SEMI
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_varDeclaration1290);
            modifiers20=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers20.getTree());
            VAR21=(Token)match(input,VAR,FOLLOW_VAR_in_varDeclaration1292); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            VAR21_tree = (CgsuiteTree)adaptor.create(VAR21);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(VAR21_tree, root_0);
            }
            IDENTIFIER22=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_varDeclaration1295); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER22_tree = (CgsuiteTree)adaptor.create(IDENTIFIER22);
            adaptor.addChild(root_0, IDENTIFIER22_tree);
            }
            SEMI23=(Token)match(input,SEMI,FOLLOW_SEMI_in_varDeclaration1297); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "varDeclaration"

    public static class propertyDeclaration_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "propertyDeclaration"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:228:1: propertyDeclaration : modifiers PROPERTY IDENTIFIER DOT ( GET | SET ) ( javaClause SEMI | statementSequence END ) ;
    public final CgsuiteParser.propertyDeclaration_return propertyDeclaration() throws RecognitionException {
        CgsuiteParser.propertyDeclaration_return retval = new CgsuiteParser.propertyDeclaration_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token PROPERTY25=null;
        Token IDENTIFIER26=null;
        Token DOT27=null;
        Token set28=null;
        Token SEMI30=null;
        Token END32=null;
        CgsuiteParser.modifiers_return modifiers24 = null;

        CgsuiteParser.javaClause_return javaClause29 = null;

        CgsuiteParser.statementSequence_return statementSequence31 = null;


        CgsuiteTree PROPERTY25_tree=null;
        CgsuiteTree IDENTIFIER26_tree=null;
        CgsuiteTree DOT27_tree=null;
        CgsuiteTree set28_tree=null;
        CgsuiteTree SEMI30_tree=null;
        CgsuiteTree END32_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:229:2: ( modifiers PROPERTY IDENTIFIER DOT ( GET | SET ) ( javaClause SEMI | statementSequence END ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:229:4: modifiers PROPERTY IDENTIFIER DOT ( GET | SET ) ( javaClause SEMI | statementSequence END )
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_propertyDeclaration1312);
            modifiers24=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers24.getTree());
            PROPERTY25=(Token)match(input,PROPERTY,FOLLOW_PROPERTY_in_propertyDeclaration1314); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            PROPERTY25_tree = (CgsuiteTree)adaptor.create(PROPERTY25);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(PROPERTY25_tree, root_0);
            }
            IDENTIFIER26=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_propertyDeclaration1317); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER26_tree = (CgsuiteTree)adaptor.create(IDENTIFIER26);
            adaptor.addChild(root_0, IDENTIFIER26_tree);
            }
            DOT27=(Token)match(input,DOT,FOLLOW_DOT_in_propertyDeclaration1319); if (state.failed) return retval;
            set28=(Token)input.LT(1);
            if ( input.LA(1)==GET||input.LA(1)==SET ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (CgsuiteTree)adaptor.create(set28));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:230:4: ( javaClause SEMI | statementSequence END )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==COLON) ) {
                alt7=1;
            }
            else if ( ((LA7_0>=PLUS && LA7_0<=AST)||LA7_0==LPAREN||LA7_0==LBRACKET||LA7_0==LBRACE||LA7_0==SEMI||(LA7_0>=CARET && LA7_0<=VEEVEE)||(LA7_0>=BEGIN && LA7_0<=BY)||(LA7_0>=CLEAR && LA7_0<=DO)||LA7_0==END||(LA7_0>=FALSE && LA7_0<=FOR)||LA7_0==FROM||LA7_0==IF||(LA7_0>=NIL && LA7_0<=NOT)||LA7_0==RETURN||(LA7_0>=THIS && LA7_0<=TRUE)||(LA7_0>=WHERE && LA7_0<=WHILE)||(LA7_0>=IDENTIFIER && LA7_0<=CHAR)) ) {
                alt7=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:230:5: javaClause SEMI
                    {
                    pushFollow(FOLLOW_javaClause_in_propertyDeclaration1334);
                    javaClause29=javaClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, javaClause29.getTree());
                    SEMI30=(Token)match(input,SEMI,FOLLOW_SEMI_in_propertyDeclaration1336); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:230:24: statementSequence END
                    {
                    pushFollow(FOLLOW_statementSequence_in_propertyDeclaration1341);
                    statementSequence31=statementSequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence31.getTree());
                    END32=(Token)match(input,END,FOLLOW_END_in_propertyDeclaration1343); if (state.failed) return retval;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "propertyDeclaration"

    public static class proptype_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "proptype"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:233:1: proptype : ( GET | SET );
    public final CgsuiteParser.proptype_return proptype() throws RecognitionException {
        CgsuiteParser.proptype_return retval = new CgsuiteParser.proptype_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token set33=null;

        CgsuiteTree set33_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:234:5: ( GET | SET )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            set33=(Token)input.LT(1);
            if ( input.LA(1)==GET||input.LA(1)==SET ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (CgsuiteTree)adaptor.create(set33));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "proptype"

    public static class methodDeclaration_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "methodDeclaration"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:238:1: methodDeclaration : modifiers METHOD methodName LPAREN methodParameterList RPAREN ( javaClause SEMI | statementSequence END ) ;
    public final CgsuiteParser.methodDeclaration_return methodDeclaration() throws RecognitionException {
        CgsuiteParser.methodDeclaration_return retval = new CgsuiteParser.methodDeclaration_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token METHOD35=null;
        Token LPAREN37=null;
        Token RPAREN39=null;
        Token SEMI41=null;
        Token END43=null;
        CgsuiteParser.modifiers_return modifiers34 = null;

        CgsuiteParser.methodName_return methodName36 = null;

        CgsuiteParser.methodParameterList_return methodParameterList38 = null;

        CgsuiteParser.javaClause_return javaClause40 = null;

        CgsuiteParser.statementSequence_return statementSequence42 = null;


        CgsuiteTree METHOD35_tree=null;
        CgsuiteTree LPAREN37_tree=null;
        CgsuiteTree RPAREN39_tree=null;
        CgsuiteTree SEMI41_tree=null;
        CgsuiteTree END43_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:239:2: ( modifiers METHOD methodName LPAREN methodParameterList RPAREN ( javaClause SEMI | statementSequence END ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:239:4: modifiers METHOD methodName LPAREN methodParameterList RPAREN ( javaClause SEMI | statementSequence END )
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_methodDeclaration1382);
            modifiers34=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers34.getTree());
            METHOD35=(Token)match(input,METHOD,FOLLOW_METHOD_in_methodDeclaration1384); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            METHOD35_tree = (CgsuiteTree)adaptor.create(METHOD35);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(METHOD35_tree, root_0);
            }
            pushFollow(FOLLOW_methodName_in_methodDeclaration1387);
            methodName36=methodName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, methodName36.getTree());
            LPAREN37=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_methodDeclaration1389); if (state.failed) return retval;
            pushFollow(FOLLOW_methodParameterList_in_methodDeclaration1392);
            methodParameterList38=methodParameterList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, methodParameterList38.getTree());
            RPAREN39=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_methodDeclaration1394); if (state.failed) return retval;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:240:4: ( javaClause SEMI | statementSequence END )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==COLON) ) {
                alt8=1;
            }
            else if ( ((LA8_0>=PLUS && LA8_0<=AST)||LA8_0==LPAREN||LA8_0==LBRACKET||LA8_0==LBRACE||LA8_0==SEMI||(LA8_0>=CARET && LA8_0<=VEEVEE)||(LA8_0>=BEGIN && LA8_0<=BY)||(LA8_0>=CLEAR && LA8_0<=DO)||LA8_0==END||(LA8_0>=FALSE && LA8_0<=FOR)||LA8_0==FROM||LA8_0==IF||(LA8_0>=NIL && LA8_0<=NOT)||LA8_0==RETURN||(LA8_0>=THIS && LA8_0<=TRUE)||(LA8_0>=WHERE && LA8_0<=WHILE)||(LA8_0>=IDENTIFIER && LA8_0<=CHAR)) ) {
                alt8=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:240:5: javaClause SEMI
                    {
                    pushFollow(FOLLOW_javaClause_in_methodDeclaration1401);
                    javaClause40=javaClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, javaClause40.getTree());
                    SEMI41=(Token)match(input,SEMI,FOLLOW_SEMI_in_methodDeclaration1403); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:240:24: statementSequence END
                    {
                    pushFollow(FOLLOW_statementSequence_in_methodDeclaration1408);
                    statementSequence42=statementSequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence42.getTree());
                    END43=(Token)match(input,END,FOLLOW_END_in_methodDeclaration1410); if (state.failed) return retval;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "methodDeclaration"

    public static class modifiers_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "modifiers"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:243:1: modifiers : ( PRIVATE | PROTECTED | PUBLIC | IMMUTABLE | STATIC )* -> ^( MODIFIERS ( PRIVATE )* ( PROTECTED )* ( PUBLIC )* ( IMMUTABLE )* ) ;
    public final CgsuiteParser.modifiers_return modifiers() throws RecognitionException {
        CgsuiteParser.modifiers_return retval = new CgsuiteParser.modifiers_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token PRIVATE44=null;
        Token PROTECTED45=null;
        Token PUBLIC46=null;
        Token IMMUTABLE47=null;
        Token STATIC48=null;

        CgsuiteTree PRIVATE44_tree=null;
        CgsuiteTree PROTECTED45_tree=null;
        CgsuiteTree PUBLIC46_tree=null;
        CgsuiteTree IMMUTABLE47_tree=null;
        CgsuiteTree STATIC48_tree=null;
        RewriteRuleTokenStream stream_PROTECTED=new RewriteRuleTokenStream(adaptor,"token PROTECTED");
        RewriteRuleTokenStream stream_IMMUTABLE=new RewriteRuleTokenStream(adaptor,"token IMMUTABLE");
        RewriteRuleTokenStream stream_PRIVATE=new RewriteRuleTokenStream(adaptor,"token PRIVATE");
        RewriteRuleTokenStream stream_PUBLIC=new RewriteRuleTokenStream(adaptor,"token PUBLIC");
        RewriteRuleTokenStream stream_STATIC=new RewriteRuleTokenStream(adaptor,"token STATIC");

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:244:2: ( ( PRIVATE | PROTECTED | PUBLIC | IMMUTABLE | STATIC )* -> ^( MODIFIERS ( PRIVATE )* ( PROTECTED )* ( PUBLIC )* ( IMMUTABLE )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:244:4: ( PRIVATE | PROTECTED | PUBLIC | IMMUTABLE | STATIC )*
            {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:244:4: ( PRIVATE | PROTECTED | PUBLIC | IMMUTABLE | STATIC )*
            loop9:
            do {
                int alt9=6;
                switch ( input.LA(1) ) {
                case PRIVATE:
                    {
                    alt9=1;
                    }
                    break;
                case PROTECTED:
                    {
                    alt9=2;
                    }
                    break;
                case PUBLIC:
                    {
                    alt9=3;
                    }
                    break;
                case IMMUTABLE:
                    {
                    alt9=4;
                    }
                    break;
                case STATIC:
                    {
                    alt9=5;
                    }
                    break;

                }

                switch (alt9) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:244:5: PRIVATE
            	    {
            	    PRIVATE44=(Token)match(input,PRIVATE,FOLLOW_PRIVATE_in_modifiers1425); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_PRIVATE.add(PRIVATE44);


            	    }
            	    break;
            	case 2 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:244:15: PROTECTED
            	    {
            	    PROTECTED45=(Token)match(input,PROTECTED,FOLLOW_PROTECTED_in_modifiers1429); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_PROTECTED.add(PROTECTED45);


            	    }
            	    break;
            	case 3 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:244:27: PUBLIC
            	    {
            	    PUBLIC46=(Token)match(input,PUBLIC,FOLLOW_PUBLIC_in_modifiers1433); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_PUBLIC.add(PUBLIC46);


            	    }
            	    break;
            	case 4 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:244:36: IMMUTABLE
            	    {
            	    IMMUTABLE47=(Token)match(input,IMMUTABLE,FOLLOW_IMMUTABLE_in_modifiers1437); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_IMMUTABLE.add(IMMUTABLE47);


            	    }
            	    break;
            	case 5 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:244:48: STATIC
            	    {
            	    STATIC48=(Token)match(input,STATIC,FOLLOW_STATIC_in_modifiers1441); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_STATIC.add(STATIC48);


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);



            // AST REWRITE
            // elements: PROTECTED, IMMUTABLE, PRIVATE, PUBLIC
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CgsuiteTree)adaptor.nil();
            // 244:57: -> ^( MODIFIERS ( PRIVATE )* ( PROTECTED )* ( PUBLIC )* ( IMMUTABLE )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:244:60: ^( MODIFIERS ( PRIVATE )* ( PROTECTED )* ( PUBLIC )* ( IMMUTABLE )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(MODIFIERS, "MODIFIERS"), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:244:72: ( PRIVATE )*
                while ( stream_PRIVATE.hasNext() ) {
                    adaptor.addChild(root_1, stream_PRIVATE.nextNode());

                }
                stream_PRIVATE.reset();
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:244:81: ( PROTECTED )*
                while ( stream_PROTECTED.hasNext() ) {
                    adaptor.addChild(root_1, stream_PROTECTED.nextNode());

                }
                stream_PROTECTED.reset();
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:244:92: ( PUBLIC )*
                while ( stream_PUBLIC.hasNext() ) {
                    adaptor.addChild(root_1, stream_PUBLIC.nextNode());

                }
                stream_PUBLIC.reset();
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:244:100: ( IMMUTABLE )*
                while ( stream_IMMUTABLE.hasNext() ) {
                    adaptor.addChild(root_1, stream_IMMUTABLE.nextNode());

                }
                stream_IMMUTABLE.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "modifiers"

    public static class methodName_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "methodName"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:247:1: methodName : ( OP opCode | IDENTIFIER );
    public final CgsuiteParser.methodName_return methodName() throws RecognitionException {
        CgsuiteParser.methodName_return retval = new CgsuiteParser.methodName_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token OP49=null;
        Token IDENTIFIER51=null;
        CgsuiteParser.opCode_return opCode50 = null;


        CgsuiteTree OP49_tree=null;
        CgsuiteTree IDENTIFIER51_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:248:5: ( OP opCode | IDENTIFIER )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==OP) ) {
                alt10=1;
            }
            else if ( (LA10_0==IDENTIFIER) ) {
                alt10=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:248:7: OP opCode
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    OP49=(Token)match(input,OP,FOLLOW_OP_in_methodName1475); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OP49_tree = (CgsuiteTree)adaptor.create(OP49);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(OP49_tree, root_0);
                    }
                    pushFollow(FOLLOW_opCode_in_methodName1478);
                    opCode50=opCode();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, opCode50.getTree());

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:249:7: IDENTIFIER
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    IDENTIFIER51=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodName1486); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER51_tree = (CgsuiteTree)adaptor.create(IDENTIFIER51);
                    adaptor.addChild(root_0, IDENTIFIER51_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "methodName"

    public static class opCode_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "opCode"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:252:1: opCode : ( PLUS | MINUS | AST | FSLASH | PERCENT | EXP | NEG | POS | standardRelationalToken | opAssignmentToken | LBRACKET RBRACKET ( ASSIGN )? );
    public final CgsuiteParser.opCode_return opCode() throws RecognitionException {
        CgsuiteParser.opCode_return retval = new CgsuiteParser.opCode_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token PLUS52=null;
        Token MINUS53=null;
        Token AST54=null;
        Token FSLASH55=null;
        Token PERCENT56=null;
        Token EXP57=null;
        Token NEG58=null;
        Token POS59=null;
        Token LBRACKET62=null;
        Token RBRACKET63=null;
        Token ASSIGN64=null;
        CgsuiteParser.standardRelationalToken_return standardRelationalToken60 = null;

        CgsuiteParser.opAssignmentToken_return opAssignmentToken61 = null;


        CgsuiteTree PLUS52_tree=null;
        CgsuiteTree MINUS53_tree=null;
        CgsuiteTree AST54_tree=null;
        CgsuiteTree FSLASH55_tree=null;
        CgsuiteTree PERCENT56_tree=null;
        CgsuiteTree EXP57_tree=null;
        CgsuiteTree NEG58_tree=null;
        CgsuiteTree POS59_tree=null;
        CgsuiteTree LBRACKET62_tree=null;
        CgsuiteTree RBRACKET63_tree=null;
        CgsuiteTree ASSIGN64_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:253:5: ( PLUS | MINUS | AST | FSLASH | PERCENT | EXP | NEG | POS | standardRelationalToken | opAssignmentToken | LBRACKET RBRACKET ( ASSIGN )? )
            int alt12=11;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt12=1;
                }
                break;
            case MINUS:
                {
                alt12=2;
                }
                break;
            case AST:
                {
                alt12=3;
                }
                break;
            case FSLASH:
                {
                alt12=4;
                }
                break;
            case PERCENT:
                {
                alt12=5;
                }
                break;
            case EXP:
                {
                alt12=6;
                }
                break;
            case NEG:
                {
                alt12=7;
                }
                break;
            case POS:
                {
                alt12=8;
                }
                break;
            case EQUALS:
            case NEQ:
            case LT:
            case GT:
            case LEQ:
            case GEQ:
            case CONFUSED:
            case COMPARE:
                {
                alt12=9;
                }
                break;
            case ASN_PLUS:
            case ASN_MINUS:
            case ASN_TIMES:
            case ASN_DIV:
            case ASN_MOD:
            case ASN_AND:
            case ASN_OR:
            case ASN_XOR:
            case ASN_EXP:
                {
                alt12=10;
                }
                break;
            case LBRACKET:
                {
                alt12=11;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:253:7: PLUS
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    PLUS52=(Token)match(input,PLUS,FOLLOW_PLUS_in_opCode1503); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PLUS52_tree = (CgsuiteTree)adaptor.create(PLUS52);
                    adaptor.addChild(root_0, PLUS52_tree);
                    }

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:253:14: MINUS
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    MINUS53=(Token)match(input,MINUS,FOLLOW_MINUS_in_opCode1507); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MINUS53_tree = (CgsuiteTree)adaptor.create(MINUS53);
                    adaptor.addChild(root_0, MINUS53_tree);
                    }

                    }
                    break;
                case 3 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:253:22: AST
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    AST54=(Token)match(input,AST,FOLLOW_AST_in_opCode1511); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    AST54_tree = (CgsuiteTree)adaptor.create(AST54);
                    adaptor.addChild(root_0, AST54_tree);
                    }

                    }
                    break;
                case 4 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:253:28: FSLASH
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    FSLASH55=(Token)match(input,FSLASH,FOLLOW_FSLASH_in_opCode1515); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FSLASH55_tree = (CgsuiteTree)adaptor.create(FSLASH55);
                    adaptor.addChild(root_0, FSLASH55_tree);
                    }

                    }
                    break;
                case 5 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:253:37: PERCENT
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    PERCENT56=(Token)match(input,PERCENT,FOLLOW_PERCENT_in_opCode1519); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PERCENT56_tree = (CgsuiteTree)adaptor.create(PERCENT56);
                    adaptor.addChild(root_0, PERCENT56_tree);
                    }

                    }
                    break;
                case 6 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:253:47: EXP
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    EXP57=(Token)match(input,EXP,FOLLOW_EXP_in_opCode1523); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EXP57_tree = (CgsuiteTree)adaptor.create(EXP57);
                    adaptor.addChild(root_0, EXP57_tree);
                    }

                    }
                    break;
                case 7 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:253:53: NEG
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    NEG58=(Token)match(input,NEG,FOLLOW_NEG_in_opCode1527); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NEG58_tree = (CgsuiteTree)adaptor.create(NEG58);
                    adaptor.addChild(root_0, NEG58_tree);
                    }

                    }
                    break;
                case 8 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:253:59: POS
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    POS59=(Token)match(input,POS,FOLLOW_POS_in_opCode1531); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    POS59_tree = (CgsuiteTree)adaptor.create(POS59);
                    adaptor.addChild(root_0, POS59_tree);
                    }

                    }
                    break;
                case 9 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:254:7: standardRelationalToken
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_standardRelationalToken_in_opCode1539);
                    standardRelationalToken60=standardRelationalToken();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, standardRelationalToken60.getTree());

                    }
                    break;
                case 10 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:255:7: opAssignmentToken
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_opAssignmentToken_in_opCode1547);
                    opAssignmentToken61=opAssignmentToken();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, opAssignmentToken61.getTree());

                    }
                    break;
                case 11 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:256:7: LBRACKET RBRACKET ( ASSIGN )?
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    LBRACKET62=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_opCode1555); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LBRACKET62_tree = (CgsuiteTree)adaptor.create(LBRACKET62);
                    adaptor.addChild(root_0, LBRACKET62_tree);
                    }
                    RBRACKET63=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_opCode1557); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RBRACKET63_tree = (CgsuiteTree)adaptor.create(RBRACKET63);
                    adaptor.addChild(root_0, RBRACKET63_tree);
                    }
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:256:25: ( ASSIGN )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==ASSIGN) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:256:25: ASSIGN
                            {
                            ASSIGN64=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_opCode1559); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            ASSIGN64_tree = (CgsuiteTree)adaptor.create(ASSIGN64);
                            adaptor.addChild(root_0, ASSIGN64_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "opCode"

    public static class methodParameterList_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "methodParameterList"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:260:1: methodParameterList : ( methodParameter ( COMMA methodParameter )* )? -> ^( METHOD_PARAMETER_LIST ( methodParameter )* ) ;
    public final CgsuiteParser.methodParameterList_return methodParameterList() throws RecognitionException {
        CgsuiteParser.methodParameterList_return retval = new CgsuiteParser.methodParameterList_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token COMMA66=null;
        CgsuiteParser.methodParameter_return methodParameter65 = null;

        CgsuiteParser.methodParameter_return methodParameter67 = null;


        CgsuiteTree COMMA66_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_methodParameter=new RewriteRuleSubtreeStream(adaptor,"rule methodParameter");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:261:2: ( ( methodParameter ( COMMA methodParameter )* )? -> ^( METHOD_PARAMETER_LIST ( methodParameter )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:261:4: ( methodParameter ( COMMA methodParameter )* )?
            {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:261:4: ( methodParameter ( COMMA methodParameter )* )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==IDENTIFIER) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:261:5: methodParameter ( COMMA methodParameter )*
                    {
                    pushFollow(FOLLOW_methodParameter_in_methodParameterList1577);
                    methodParameter65=methodParameter();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_methodParameter.add(methodParameter65.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:261:21: ( COMMA methodParameter )*
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( (LA13_0==COMMA) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:261:22: COMMA methodParameter
                    	    {
                    	    COMMA66=(Token)match(input,COMMA,FOLLOW_COMMA_in_methodParameterList1580); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA66);

                    	    pushFollow(FOLLOW_methodParameter_in_methodParameterList1582);
                    	    methodParameter67=methodParameter();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_methodParameter.add(methodParameter67.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop13;
                        }
                    } while (true);


                    }
                    break;

            }



            // AST REWRITE
            // elements: methodParameter
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CgsuiteTree)adaptor.nil();
            // 261:48: -> ^( METHOD_PARAMETER_LIST ( methodParameter )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:261:51: ^( METHOD_PARAMETER_LIST ( methodParameter )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(METHOD_PARAMETER_LIST, "METHOD_PARAMETER_LIST"), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:261:75: ( methodParameter )*
                while ( stream_methodParameter.hasNext() ) {
                    adaptor.addChild(root_1, stream_methodParameter.nextTree());

                }
                stream_methodParameter.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "methodParameterList"

    public static class methodParameter_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "methodParameter"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:264:1: methodParameter : (a= IDENTIFIER (b= IDENTIFIER )? QUESTION ( expression )? -> ^( QUESTION ^( $a ( $b)? ) ( expression )? ) | a= IDENTIFIER b= IDENTIFIER -> ^( $b $a) | IDENTIFIER );
    public final CgsuiteParser.methodParameter_return methodParameter() throws RecognitionException {
        CgsuiteParser.methodParameter_return retval = new CgsuiteParser.methodParameter_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token a=null;
        Token b=null;
        Token QUESTION68=null;
        Token IDENTIFIER70=null;
        CgsuiteParser.expression_return expression69 = null;


        CgsuiteTree a_tree=null;
        CgsuiteTree b_tree=null;
        CgsuiteTree QUESTION68_tree=null;
        CgsuiteTree IDENTIFIER70_tree=null;
        RewriteRuleTokenStream stream_QUESTION=new RewriteRuleTokenStream(adaptor,"token QUESTION");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:265:2: (a= IDENTIFIER (b= IDENTIFIER )? QUESTION ( expression )? -> ^( QUESTION ^( $a ( $b)? ) ( expression )? ) | a= IDENTIFIER b= IDENTIFIER -> ^( $b $a) | IDENTIFIER )
            int alt17=3;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==IDENTIFIER) ) {
                switch ( input.LA(2) ) {
                case IDENTIFIER:
                    {
                    int LA17_2 = input.LA(3);

                    if ( (LA17_2==RPAREN||LA17_2==COMMA) ) {
                        alt17=2;
                    }
                    else if ( (LA17_2==QUESTION) ) {
                        alt17=1;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 17, 2, input);

                        throw nvae;
                    }
                    }
                    break;
                case QUESTION:
                    {
                    alt17=1;
                    }
                    break;
                case RPAREN:
                case COMMA:
                    {
                    alt17=3;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 17, 1, input);

                    throw nvae;
                }

            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:265:4: a= IDENTIFIER (b= IDENTIFIER )? QUESTION ( expression )?
                    {
                    a=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodParameter1609); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_IDENTIFIER.add(a);

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:265:18: (b= IDENTIFIER )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==IDENTIFIER) ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:265:18: b= IDENTIFIER
                            {
                            b=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodParameter1613); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_IDENTIFIER.add(b);


                            }
                            break;

                    }

                    QUESTION68=(Token)match(input,QUESTION,FOLLOW_QUESTION_in_methodParameter1616); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_QUESTION.add(QUESTION68);

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:265:40: ( expression )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( ((LA16_0>=PLUS && LA16_0<=AST)||LA16_0==LPAREN||LA16_0==LBRACKET||LA16_0==LBRACE||(LA16_0>=CARET && LA16_0<=VEEVEE)||LA16_0==BEGIN||LA16_0==BY||LA16_0==DO||(LA16_0>=FALSE && LA16_0<=FOR)||LA16_0==FROM||LA16_0==IF||(LA16_0>=NIL && LA16_0<=NOT)||(LA16_0>=THIS && LA16_0<=TRUE)||(LA16_0>=WHERE && LA16_0<=WHILE)||(LA16_0>=IDENTIFIER && LA16_0<=CHAR)) ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:265:40: expression
                            {
                            pushFollow(FOLLOW_expression_in_methodParameter1618);
                            expression69=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_expression.add(expression69.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: expression, a, QUESTION, b
                    // token labels: b, a
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_b=new RewriteRuleTokenStream(adaptor,"token b",b);
                    RewriteRuleTokenStream stream_a=new RewriteRuleTokenStream(adaptor,"token a",a);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CgsuiteTree)adaptor.nil();
                    // 265:52: -> ^( QUESTION ^( $a ( $b)? ) ( expression )? )
                    {
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:265:55: ^( QUESTION ^( $a ( $b)? ) ( expression )? )
                        {
                        CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                        root_1 = (CgsuiteTree)adaptor.becomeRoot(stream_QUESTION.nextNode(), root_1);

                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:265:66: ^( $a ( $b)? )
                        {
                        CgsuiteTree root_2 = (CgsuiteTree)adaptor.nil();
                        root_2 = (CgsuiteTree)adaptor.becomeRoot(stream_a.nextNode(), root_2);

                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:265:71: ( $b)?
                        if ( stream_b.hasNext() ) {
                            adaptor.addChild(root_2, stream_b.nextNode());

                        }
                        stream_b.reset();

                        adaptor.addChild(root_1, root_2);
                        }
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:265:76: ( expression )?
                        if ( stream_expression.hasNext() ) {
                            adaptor.addChild(root_1, stream_expression.nextTree());

                        }
                        stream_expression.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:266:4: a= IDENTIFIER b= IDENTIFIER
                    {
                    a=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodParameter1644); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_IDENTIFIER.add(a);

                    b=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodParameter1648); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_IDENTIFIER.add(b);



                    // AST REWRITE
                    // elements: b, a
                    // token labels: b, a
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_b=new RewriteRuleTokenStream(adaptor,"token b",b);
                    RewriteRuleTokenStream stream_a=new RewriteRuleTokenStream(adaptor,"token a",a);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CgsuiteTree)adaptor.nil();
                    // 266:30: -> ^( $b $a)
                    {
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:266:33: ^( $b $a)
                        {
                        CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                        root_1 = (CgsuiteTree)adaptor.becomeRoot(stream_b.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_a.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:267:4: IDENTIFIER
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    IDENTIFIER70=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodParameter1663); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER70_tree = (CgsuiteTree)adaptor.create(IDENTIFIER70);
                    adaptor.addChild(root_0, IDENTIFIER70_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "methodParameter"

    public static class enumDeclaration_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enumDeclaration"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:270:1: enumDeclaration : modifiers ENUM IDENTIFIER enumElementList ( declaration )* END ;
    public final CgsuiteParser.enumDeclaration_return enumDeclaration() throws RecognitionException {
        CgsuiteParser.enumDeclaration_return retval = new CgsuiteParser.enumDeclaration_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token ENUM72=null;
        Token IDENTIFIER73=null;
        Token END76=null;
        CgsuiteParser.modifiers_return modifiers71 = null;

        CgsuiteParser.enumElementList_return enumElementList74 = null;

        CgsuiteParser.declaration_return declaration75 = null;


        CgsuiteTree ENUM72_tree=null;
        CgsuiteTree IDENTIFIER73_tree=null;
        CgsuiteTree END76_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:271:5: ( modifiers ENUM IDENTIFIER enumElementList ( declaration )* END )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:271:7: modifiers ENUM IDENTIFIER enumElementList ( declaration )* END
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_enumDeclaration1677);
            modifiers71=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers71.getTree());
            ENUM72=(Token)match(input,ENUM,FOLLOW_ENUM_in_enumDeclaration1679); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ENUM72_tree = (CgsuiteTree)adaptor.create(ENUM72);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(ENUM72_tree, root_0);
            }
            IDENTIFIER73=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_enumDeclaration1682); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER73_tree = (CgsuiteTree)adaptor.create(IDENTIFIER73);
            adaptor.addChild(root_0, IDENTIFIER73_tree);
            }
            pushFollow(FOLLOW_enumElementList_in_enumDeclaration1684);
            enumElementList74=enumElementList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, enumElementList74.getTree());
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:271:50: ( declaration )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==ENUM||LA18_0==IMMUTABLE||LA18_0==METHOD||(LA18_0>=PRIVATE && LA18_0<=PUBLIC)||LA18_0==STATIC||LA18_0==VAR) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:271:50: declaration
            	    {
            	    pushFollow(FOLLOW_declaration_in_enumDeclaration1686);
            	    declaration75=declaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, declaration75.getTree());

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

            END76=(Token)match(input,END,FOLLOW_END_in_enumDeclaration1689); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "enumDeclaration"

    public static class enumElementList_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enumElementList"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:274:1: enumElementList : ( enumElement ( COMMA enumElement )* ) SEMI -> ^( ENUM_ELEMENT_LIST ( enumElement )* ) ;
    public final CgsuiteParser.enumElementList_return enumElementList() throws RecognitionException {
        CgsuiteParser.enumElementList_return retval = new CgsuiteParser.enumElementList_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token COMMA78=null;
        Token SEMI80=null;
        CgsuiteParser.enumElement_return enumElement77 = null;

        CgsuiteParser.enumElement_return enumElement79 = null;


        CgsuiteTree COMMA78_tree=null;
        CgsuiteTree SEMI80_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleSubtreeStream stream_enumElement=new RewriteRuleSubtreeStream(adaptor,"rule enumElement");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:275:5: ( ( enumElement ( COMMA enumElement )* ) SEMI -> ^( ENUM_ELEMENT_LIST ( enumElement )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:275:7: ( enumElement ( COMMA enumElement )* ) SEMI
            {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:275:7: ( enumElement ( COMMA enumElement )* )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:275:8: enumElement ( COMMA enumElement )*
            {
            pushFollow(FOLLOW_enumElement_in_enumElementList1708);
            enumElement77=enumElement();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_enumElement.add(enumElement77.getTree());
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:275:20: ( COMMA enumElement )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==COMMA) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:275:21: COMMA enumElement
            	    {
            	    COMMA78=(Token)match(input,COMMA,FOLLOW_COMMA_in_enumElementList1711); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA78);

            	    pushFollow(FOLLOW_enumElement_in_enumElementList1713);
            	    enumElement79=enumElement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_enumElement.add(enumElement79.getTree());

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);


            }

            SEMI80=(Token)match(input,SEMI,FOLLOW_SEMI_in_enumElementList1718); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMI.add(SEMI80);



            // AST REWRITE
            // elements: enumElement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CgsuiteTree)adaptor.nil();
            // 275:47: -> ^( ENUM_ELEMENT_LIST ( enumElement )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:275:50: ^( ENUM_ELEMENT_LIST ( enumElement )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(ENUM_ELEMENT_LIST, "ENUM_ELEMENT_LIST"), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:275:70: ( enumElement )*
                while ( stream_enumElement.hasNext() ) {
                    adaptor.addChild(root_1, stream_enumElement.nextTree());

                }
                stream_enumElement.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "enumElementList"

    public static class enumElement_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "enumElement"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:278:1: enumElement : IDENTIFIER ( LPAREN ( expression ( COMMA expression )* )? RPAREN )? -> ^( ENUM_ELEMENT[$IDENTIFIER] IDENTIFIER ( expression )* ) ;
    public final CgsuiteParser.enumElement_return enumElement() throws RecognitionException {
        CgsuiteParser.enumElement_return retval = new CgsuiteParser.enumElement_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token IDENTIFIER81=null;
        Token LPAREN82=null;
        Token COMMA84=null;
        Token RPAREN86=null;
        CgsuiteParser.expression_return expression83 = null;

        CgsuiteParser.expression_return expression85 = null;


        CgsuiteTree IDENTIFIER81_tree=null;
        CgsuiteTree LPAREN82_tree=null;
        CgsuiteTree COMMA84_tree=null;
        CgsuiteTree RPAREN86_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:279:5: ( IDENTIFIER ( LPAREN ( expression ( COMMA expression )* )? RPAREN )? -> ^( ENUM_ELEMENT[$IDENTIFIER] IDENTIFIER ( expression )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:279:7: IDENTIFIER ( LPAREN ( expression ( COMMA expression )* )? RPAREN )?
            {
            IDENTIFIER81=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_enumElement1744); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER81);

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:279:18: ( LPAREN ( expression ( COMMA expression )* )? RPAREN )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==LPAREN) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:279:19: LPAREN ( expression ( COMMA expression )* )? RPAREN
                    {
                    LPAREN82=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_enumElement1747); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN82);

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:279:26: ( expression ( COMMA expression )* )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);

                    if ( ((LA21_0>=PLUS && LA21_0<=AST)||LA21_0==LPAREN||LA21_0==LBRACKET||LA21_0==LBRACE||(LA21_0>=CARET && LA21_0<=VEEVEE)||LA21_0==BEGIN||LA21_0==BY||LA21_0==DO||(LA21_0>=FALSE && LA21_0<=FOR)||LA21_0==FROM||LA21_0==IF||(LA21_0>=NIL && LA21_0<=NOT)||(LA21_0>=THIS && LA21_0<=TRUE)||(LA21_0>=WHERE && LA21_0<=WHILE)||(LA21_0>=IDENTIFIER && LA21_0<=CHAR)) ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:279:27: expression ( COMMA expression )*
                            {
                            pushFollow(FOLLOW_expression_in_enumElement1750);
                            expression83=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_expression.add(expression83.getTree());
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:279:38: ( COMMA expression )*
                            loop20:
                            do {
                                int alt20=2;
                                int LA20_0 = input.LA(1);

                                if ( (LA20_0==COMMA) ) {
                                    alt20=1;
                                }


                                switch (alt20) {
                            	case 1 :
                            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:279:39: COMMA expression
                            	    {
                            	    COMMA84=(Token)match(input,COMMA,FOLLOW_COMMA_in_enumElement1753); if (state.failed) return retval; 
                            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA84);

                            	    pushFollow(FOLLOW_expression_in_enumElement1755);
                            	    expression85=expression();

                            	    state._fsp--;
                            	    if (state.failed) return retval;
                            	    if ( state.backtracking==0 ) stream_expression.add(expression85.getTree());

                            	    }
                            	    break;

                            	default :
                            	    break loop20;
                                }
                            } while (true);


                            }
                            break;

                    }

                    RPAREN86=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_enumElement1761); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN86);


                    }
                    break;

            }



            // AST REWRITE
            // elements: expression, IDENTIFIER
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CgsuiteTree)adaptor.nil();
            // 279:69: -> ^( ENUM_ELEMENT[$IDENTIFIER] IDENTIFIER ( expression )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:279:72: ^( ENUM_ELEMENT[$IDENTIFIER] IDENTIFIER ( expression )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(ENUM_ELEMENT, IDENTIFIER81), root_1);

                adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:279:111: ( expression )*
                while ( stream_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_expression.nextTree());

                }
                stream_expression.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "enumElement"

    public static class script_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "script"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:282:1: script : block EOF ;
    public final CgsuiteParser.script_return script() throws RecognitionException {
        CgsuiteParser.script_return retval = new CgsuiteParser.script_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token EOF88=null;
        CgsuiteParser.block_return block87 = null;


        CgsuiteTree EOF88_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:283:5: ( block EOF )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:283:7: block EOF
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_block_in_script1792);
            block87=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block87.getTree());
            EOF88=(Token)match(input,EOF,FOLLOW_EOF_in_script1794); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EOF88_tree = (CgsuiteTree)adaptor.create(EOF88);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(EOF88_tree, root_0);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "script"

    public static class block_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "block"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:286:1: block : statementSequence ;
    public final CgsuiteParser.block_return block() throws RecognitionException {
        CgsuiteParser.block_return retval = new CgsuiteParser.block_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        CgsuiteParser.statementSequence_return statementSequence89 = null;



        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:287:5: ( statementSequence )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:287:7: statementSequence
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_statementSequence_in_block1812);
            statementSequence89=statementSequence();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence89.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "block"

    public static class statementSequence_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "statementSequence"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:290:1: statementSequence : ( statement )? ( SEMI ( statement )? )* -> ^( STATEMENT_SEQUENCE ( statement )* ) ;
    public final CgsuiteParser.statementSequence_return statementSequence() throws RecognitionException {
        CgsuiteParser.statementSequence_return retval = new CgsuiteParser.statementSequence_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token SEMI91=null;
        CgsuiteParser.statement_return statement90 = null;

        CgsuiteParser.statement_return statement92 = null;


        CgsuiteTree SEMI91_tree=null;
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:291:2: ( ( statement )? ( SEMI ( statement )? )* -> ^( STATEMENT_SEQUENCE ( statement )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:291:4: ( statement )? ( SEMI ( statement )? )*
            {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:291:4: ( statement )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( ((LA23_0>=PLUS && LA23_0<=AST)||LA23_0==LPAREN||LA23_0==LBRACKET||LA23_0==LBRACE||(LA23_0>=CARET && LA23_0<=VEEVEE)||(LA23_0>=BEGIN && LA23_0<=BY)||(LA23_0>=CLEAR && LA23_0<=DO)||(LA23_0>=FALSE && LA23_0<=FOR)||LA23_0==FROM||LA23_0==IF||(LA23_0>=NIL && LA23_0<=NOT)||LA23_0==RETURN||(LA23_0>=THIS && LA23_0<=TRUE)||(LA23_0>=WHERE && LA23_0<=WHILE)||(LA23_0>=IDENTIFIER && LA23_0<=CHAR)) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:291:4: statement
                    {
                    pushFollow(FOLLOW_statement_in_statementSequence1827);
                    statement90=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_statement.add(statement90.getTree());

                    }
                    break;

            }

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:291:15: ( SEMI ( statement )? )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==SEMI) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:291:16: SEMI ( statement )?
            	    {
            	    SEMI91=(Token)match(input,SEMI,FOLLOW_SEMI_in_statementSequence1831); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_SEMI.add(SEMI91);

            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:291:21: ( statement )?
            	    int alt24=2;
            	    int LA24_0 = input.LA(1);

            	    if ( ((LA24_0>=PLUS && LA24_0<=AST)||LA24_0==LPAREN||LA24_0==LBRACKET||LA24_0==LBRACE||(LA24_0>=CARET && LA24_0<=VEEVEE)||(LA24_0>=BEGIN && LA24_0<=BY)||(LA24_0>=CLEAR && LA24_0<=DO)||(LA24_0>=FALSE && LA24_0<=FOR)||LA24_0==FROM||LA24_0==IF||(LA24_0>=NIL && LA24_0<=NOT)||LA24_0==RETURN||(LA24_0>=THIS && LA24_0<=TRUE)||(LA24_0>=WHERE && LA24_0<=WHILE)||(LA24_0>=IDENTIFIER && LA24_0<=CHAR)) ) {
            	        alt24=1;
            	    }
            	    switch (alt24) {
            	        case 1 :
            	            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:291:21: statement
            	            {
            	            pushFollow(FOLLOW_statement_in_statementSequence1833);
            	            statement92=statement();

            	            state._fsp--;
            	            if (state.failed) return retval;
            	            if ( state.backtracking==0 ) stream_statement.add(statement92.getTree());

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);



            // AST REWRITE
            // elements: statement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CgsuiteTree)adaptor.nil();
            // 291:34: -> ^( STATEMENT_SEQUENCE ( statement )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:291:37: ^( STATEMENT_SEQUENCE ( statement )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(STATEMENT_SEQUENCE, "STATEMENT_SEQUENCE"), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:291:58: ( statement )*
                while ( stream_statement.hasNext() ) {
                    adaptor.addChild(root_1, stream_statement.nextTree());

                }
                stream_statement.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "statementSequence"

    public static class statement_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "statement"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:294:1: statement : ( BREAK | CONTINUE | RETURN expression | CLEAR | expression );
    public final CgsuiteParser.statement_return statement() throws RecognitionException {
        CgsuiteParser.statement_return retval = new CgsuiteParser.statement_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token BREAK93=null;
        Token CONTINUE94=null;
        Token RETURN95=null;
        Token CLEAR97=null;
        CgsuiteParser.expression_return expression96 = null;

        CgsuiteParser.expression_return expression98 = null;


        CgsuiteTree BREAK93_tree=null;
        CgsuiteTree CONTINUE94_tree=null;
        CgsuiteTree RETURN95_tree=null;
        CgsuiteTree CLEAR97_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:295:2: ( BREAK | CONTINUE | RETURN expression | CLEAR | expression )
            int alt26=5;
            switch ( input.LA(1) ) {
            case BREAK:
                {
                alt26=1;
                }
                break;
            case CONTINUE:
                {
                alt26=2;
                }
                break;
            case RETURN:
                {
                alt26=3;
                }
                break;
            case CLEAR:
                {
                alt26=4;
                }
                break;
            case PLUS:
            case MINUS:
            case PLUSMINUS:
            case AST:
            case LPAREN:
            case LBRACKET:
            case LBRACE:
            case CARET:
            case CARETCARET:
            case VEE:
            case VEEVEE:
            case BEGIN:
            case BY:
            case DO:
            case FALSE:
            case FOR:
            case FROM:
            case IF:
            case NIL:
            case NOT:
            case THIS:
            case TO:
            case TRUE:
            case WHERE:
            case WHILE:
            case IDENTIFIER:
            case STRING:
            case INTEGER:
            case CHAR:
                {
                alt26=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;
            }

            switch (alt26) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:295:4: BREAK
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    BREAK93=(Token)match(input,BREAK,FOLLOW_BREAK_in_statement1857); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BREAK93_tree = (CgsuiteTree)adaptor.create(BREAK93);
                    adaptor.addChild(root_0, BREAK93_tree);
                    }

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:296:4: CONTINUE
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    CONTINUE94=(Token)match(input,CONTINUE,FOLLOW_CONTINUE_in_statement1862); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CONTINUE94_tree = (CgsuiteTree)adaptor.create(CONTINUE94);
                    adaptor.addChild(root_0, CONTINUE94_tree);
                    }

                    }
                    break;
                case 3 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:297:4: RETURN expression
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    RETURN95=(Token)match(input,RETURN,FOLLOW_RETURN_in_statement1867); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RETURN95_tree = (CgsuiteTree)adaptor.create(RETURN95);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(RETURN95_tree, root_0);
                    }
                    pushFollow(FOLLOW_expression_in_statement1870);
                    expression96=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression96.getTree());

                    }
                    break;
                case 4 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:298:7: CLEAR
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    CLEAR97=(Token)match(input,CLEAR,FOLLOW_CLEAR_in_statement1878); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CLEAR97_tree = (CgsuiteTree)adaptor.create(CLEAR97);
                    adaptor.addChild(root_0, CLEAR97_tree);
                    }

                    }
                    break;
                case 5 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:299:4: expression
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_expression_in_statement1883);
                    expression98=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression98.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "statement"

    public static class expression_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expression"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:302:1: expression : assignmentExpression ;
    public final CgsuiteParser.expression_return expression() throws RecognitionException {
        CgsuiteParser.expression_return retval = new CgsuiteParser.expression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        CgsuiteParser.assignmentExpression_return assignmentExpression99 = null;



        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:303:2: ( assignmentExpression )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:303:4: assignmentExpression
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_assignmentExpression_in_expression1894);
            assignmentExpression99=assignmentExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, assignmentExpression99.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "expression"

    public static class assignmentExpression_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "assignmentExpression"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:306:1: assignmentExpression : functionExpression ( assignmentToken assignmentExpression )? ;
    public final CgsuiteParser.assignmentExpression_return assignmentExpression() throws RecognitionException {
        CgsuiteParser.assignmentExpression_return retval = new CgsuiteParser.assignmentExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        CgsuiteParser.functionExpression_return functionExpression100 = null;

        CgsuiteParser.assignmentToken_return assignmentToken101 = null;

        CgsuiteParser.assignmentExpression_return assignmentExpression102 = null;



        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:307:2: ( functionExpression ( assignmentToken assignmentExpression )? )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:307:4: functionExpression ( assignmentToken assignmentExpression )?
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_functionExpression_in_assignmentExpression1905);
            functionExpression100=functionExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, functionExpression100.getTree());
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:307:23: ( assignmentToken assignmentExpression )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( ((LA27_0>=ASSIGN && LA27_0<=ASN_EXP)) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:307:24: assignmentToken assignmentExpression
                    {
                    pushFollow(FOLLOW_assignmentToken_in_assignmentExpression1908);
                    assignmentToken101=assignmentToken();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (CgsuiteTree)adaptor.becomeRoot(assignmentToken101.getTree(), root_0);
                    pushFollow(FOLLOW_assignmentExpression_in_assignmentExpression1911);
                    assignmentExpression102=assignmentExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, assignmentExpression102.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "assignmentExpression"

    public static class assignmentToken_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "assignmentToken"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:310:1: assignmentToken : ( ASSIGN | opAssignmentToken );
    public final CgsuiteParser.assignmentToken_return assignmentToken() throws RecognitionException {
        CgsuiteParser.assignmentToken_return retval = new CgsuiteParser.assignmentToken_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token ASSIGN103=null;
        CgsuiteParser.opAssignmentToken_return opAssignmentToken104 = null;


        CgsuiteTree ASSIGN103_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:311:2: ( ASSIGN | opAssignmentToken )
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==ASSIGN) ) {
                alt28=1;
            }
            else if ( ((LA28_0>=ASN_PLUS && LA28_0<=ASN_EXP)) ) {
                alt28=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:311:4: ASSIGN
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    ASSIGN103=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_assignmentToken1925); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ASSIGN103_tree = (CgsuiteTree)adaptor.create(ASSIGN103);
                    adaptor.addChild(root_0, ASSIGN103_tree);
                    }

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:312:4: opAssignmentToken
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_opAssignmentToken_in_assignmentToken1930);
                    opAssignmentToken104=opAssignmentToken();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, opAssignmentToken104.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "assignmentToken"

    public static class opAssignmentToken_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "opAssignmentToken"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:315:1: opAssignmentToken : ( ASN_PLUS | ASN_MINUS | ASN_TIMES | ASN_DIV | ASN_MOD | ASN_AND | ASN_OR | ASN_XOR | ASN_EXP );
    public final CgsuiteParser.opAssignmentToken_return opAssignmentToken() throws RecognitionException {
        CgsuiteParser.opAssignmentToken_return retval = new CgsuiteParser.opAssignmentToken_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token set105=null;

        CgsuiteTree set105_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:316:5: ( ASN_PLUS | ASN_MINUS | ASN_TIMES | ASN_DIV | ASN_MOD | ASN_AND | ASN_OR | ASN_XOR | ASN_EXP )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            set105=(Token)input.LT(1);
            if ( (input.LA(1)>=ASN_PLUS && input.LA(1)<=ASN_EXP) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (CgsuiteTree)adaptor.create(set105));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "opAssignmentToken"

    public static class functionExpression_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "functionExpression"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:327:1: functionExpression : ( procedureParameterList RARROW functionExpression | controlExpression );
    public final CgsuiteParser.functionExpression_return functionExpression() throws RecognitionException {
        CgsuiteParser.functionExpression_return retval = new CgsuiteParser.functionExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token RARROW107=null;
        CgsuiteParser.procedureParameterList_return procedureParameterList106 = null;

        CgsuiteParser.functionExpression_return functionExpression108 = null;

        CgsuiteParser.controlExpression_return controlExpression109 = null;


        CgsuiteTree RARROW107_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:328:2: ( procedureParameterList RARROW functionExpression | controlExpression )
            int alt29=2;
            switch ( input.LA(1) ) {
            case IDENTIFIER:
                {
                int LA29_1 = input.LA(2);

                if ( (LA29_1==RARROW) ) {
                    alt29=1;
                }
                else if ( (LA29_1==EOF||(LA29_1>=PLUS && LA29_1<=MINUS)||(LA29_1>=AST && LA29_1<=PERCENT)||(LA29_1>=LPAREN && LA29_1<=RBRACKET)||LA29_1==RBRACE||(LA29_1>=COMMA && LA29_1<=SEMI)||(LA29_1>=EQUALS && LA29_1<=COMPARE)||LA29_1==BIGRARROW||(LA29_1>=REFEQUALS && LA29_1<=ASN_EXP)||LA29_1==AND||LA29_1==BY||(LA29_1>=DO && LA29_1<=END)||LA29_1==IN||LA29_1==OR||LA29_1==THEN||LA29_1==TO||(LA29_1>=WHERE && LA29_1<=WHILE)||LA29_1==SLASHES) ) {
                    alt29=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 29, 1, input);

                    throw nvae;
                }
                }
                break;
            case LPAREN:
                {
                switch ( input.LA(2) ) {
                case IDENTIFIER:
                    {
                    switch ( input.LA(3) ) {
                    case COMMA:
                        {
                        alt29=1;
                        }
                        break;
                    case RPAREN:
                        {
                        int LA29_6 = input.LA(4);

                        if ( (LA29_6==RARROW) ) {
                            alt29=1;
                        }
                        else if ( (LA29_6==EOF||(LA29_6>=PLUS && LA29_6<=MINUS)||(LA29_6>=AST && LA29_6<=PERCENT)||(LA29_6>=LPAREN && LA29_6<=RBRACKET)||LA29_6==RBRACE||(LA29_6>=COMMA && LA29_6<=SEMI)||(LA29_6>=EQUALS && LA29_6<=COMPARE)||LA29_6==BIGRARROW||(LA29_6>=REFEQUALS && LA29_6<=ASN_EXP)||LA29_6==AND||LA29_6==BY||(LA29_6>=DO && LA29_6<=END)||LA29_6==IN||LA29_6==OR||LA29_6==THEN||LA29_6==TO||(LA29_6>=WHERE && LA29_6<=WHILE)||LA29_6==SLASHES) ) {
                            alt29=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 29, 6, input);

                            throw nvae;
                        }
                        }
                        break;
                    case PLUS:
                    case MINUS:
                    case AST:
                    case FSLASH:
                    case DOT:
                    case EXP:
                    case PERCENT:
                    case LPAREN:
                    case LBRACKET:
                    case SEMI:
                    case EQUALS:
                    case NEQ:
                    case LT:
                    case GT:
                    case LEQ:
                    case GEQ:
                    case CONFUSED:
                    case COMPARE:
                    case RARROW:
                    case REFEQUALS:
                    case REFNEQ:
                    case ASSIGN:
                    case ASN_PLUS:
                    case ASN_MINUS:
                    case ASN_TIMES:
                    case ASN_DIV:
                    case ASN_MOD:
                    case ASN_AND:
                    case ASN_OR:
                    case ASN_XOR:
                    case ASN_EXP:
                    case AND:
                    case OR:
                        {
                        alt29=2;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 29, 5, input);

                        throw nvae;
                    }

                    }
                    break;
                case RPAREN:
                    {
                    int LA29_6 = input.LA(3);

                    if ( (LA29_6==RARROW) ) {
                        alt29=1;
                    }
                    else if ( (LA29_6==EOF||(LA29_6>=PLUS && LA29_6<=MINUS)||(LA29_6>=AST && LA29_6<=PERCENT)||(LA29_6>=LPAREN && LA29_6<=RBRACKET)||LA29_6==RBRACE||(LA29_6>=COMMA && LA29_6<=SEMI)||(LA29_6>=EQUALS && LA29_6<=COMPARE)||LA29_6==BIGRARROW||(LA29_6>=REFEQUALS && LA29_6<=ASN_EXP)||LA29_6==AND||LA29_6==BY||(LA29_6>=DO && LA29_6<=END)||LA29_6==IN||LA29_6==OR||LA29_6==THEN||LA29_6==TO||(LA29_6>=WHERE && LA29_6<=WHILE)||LA29_6==SLASHES) ) {
                        alt29=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 29, 6, input);

                        throw nvae;
                    }
                    }
                    break;
                case PLUS:
                case MINUS:
                case PLUSMINUS:
                case AST:
                case LPAREN:
                case LBRACKET:
                case LBRACE:
                case SEMI:
                case CARET:
                case CARETCARET:
                case VEE:
                case VEEVEE:
                case BEGIN:
                case BREAK:
                case BY:
                case CLEAR:
                case CONTINUE:
                case DO:
                case FALSE:
                case FOR:
                case FROM:
                case IF:
                case NIL:
                case NOT:
                case RETURN:
                case THIS:
                case TO:
                case TRUE:
                case WHERE:
                case WHILE:
                case STRING:
                case INTEGER:
                case CHAR:
                    {
                    alt29=2;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 29, 2, input);

                    throw nvae;
                }

                }
                break;
            case PLUS:
            case MINUS:
            case PLUSMINUS:
            case AST:
            case LBRACKET:
            case LBRACE:
            case CARET:
            case CARETCARET:
            case VEE:
            case VEEVEE:
            case BEGIN:
            case BY:
            case DO:
            case FALSE:
            case FOR:
            case FROM:
            case IF:
            case NIL:
            case NOT:
            case THIS:
            case TO:
            case TRUE:
            case WHERE:
            case WHILE:
            case STRING:
            case INTEGER:
            case CHAR:
                {
                alt29=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:328:4: procedureParameterList RARROW functionExpression
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_procedureParameterList_in_functionExpression1996);
                    procedureParameterList106=procedureParameterList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, procedureParameterList106.getTree());
                    RARROW107=(Token)match(input,RARROW,FOLLOW_RARROW_in_functionExpression1998); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RARROW107_tree = (CgsuiteTree)adaptor.create(RARROW107);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(RARROW107_tree, root_0);
                    }
                    pushFollow(FOLLOW_functionExpression_in_functionExpression2001);
                    functionExpression108=functionExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, functionExpression108.getTree());

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:329:7: controlExpression
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_controlExpression_in_functionExpression2009);
                    controlExpression109=controlExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, controlExpression109.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "functionExpression"

    public static class procedureParameterList_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "procedureParameterList"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:332:1: procedureParameterList : ( IDENTIFIER -> ^( PROCEDURE_PARAMETER_LIST IDENTIFIER ) | LPAREN ( IDENTIFIER ( COMMA IDENTIFIER )* )? RPAREN -> ^( PROCEDURE_PARAMETER_LIST ( IDENTIFIER )* ) );
    public final CgsuiteParser.procedureParameterList_return procedureParameterList() throws RecognitionException {
        CgsuiteParser.procedureParameterList_return retval = new CgsuiteParser.procedureParameterList_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token IDENTIFIER110=null;
        Token LPAREN111=null;
        Token IDENTIFIER112=null;
        Token COMMA113=null;
        Token IDENTIFIER114=null;
        Token RPAREN115=null;

        CgsuiteTree IDENTIFIER110_tree=null;
        CgsuiteTree LPAREN111_tree=null;
        CgsuiteTree IDENTIFIER112_tree=null;
        CgsuiteTree COMMA113_tree=null;
        CgsuiteTree IDENTIFIER114_tree=null;
        CgsuiteTree RPAREN115_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:333:5: ( IDENTIFIER -> ^( PROCEDURE_PARAMETER_LIST IDENTIFIER ) | LPAREN ( IDENTIFIER ( COMMA IDENTIFIER )* )? RPAREN -> ^( PROCEDURE_PARAMETER_LIST ( IDENTIFIER )* ) )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==IDENTIFIER) ) {
                alt32=1;
            }
            else if ( (LA32_0==LPAREN) ) {
                alt32=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:333:7: IDENTIFIER
                    {
                    IDENTIFIER110=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_procedureParameterList2023); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER110);



                    // AST REWRITE
                    // elements: IDENTIFIER
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CgsuiteTree)adaptor.nil();
                    // 333:18: -> ^( PROCEDURE_PARAMETER_LIST IDENTIFIER )
                    {
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:333:21: ^( PROCEDURE_PARAMETER_LIST IDENTIFIER )
                        {
                        CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                        root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(PROCEDURE_PARAMETER_LIST, "PROCEDURE_PARAMETER_LIST"), root_1);

                        adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:334:7: LPAREN ( IDENTIFIER ( COMMA IDENTIFIER )* )? RPAREN
                    {
                    LPAREN111=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_procedureParameterList2039); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN111);

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:334:14: ( IDENTIFIER ( COMMA IDENTIFIER )* )?
                    int alt31=2;
                    int LA31_0 = input.LA(1);

                    if ( (LA31_0==IDENTIFIER) ) {
                        alt31=1;
                    }
                    switch (alt31) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:334:15: IDENTIFIER ( COMMA IDENTIFIER )*
                            {
                            IDENTIFIER112=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_procedureParameterList2042); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER112);

                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:334:26: ( COMMA IDENTIFIER )*
                            loop30:
                            do {
                                int alt30=2;
                                int LA30_0 = input.LA(1);

                                if ( (LA30_0==COMMA) ) {
                                    alt30=1;
                                }


                                switch (alt30) {
                            	case 1 :
                            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:334:27: COMMA IDENTIFIER
                            	    {
                            	    COMMA113=(Token)match(input,COMMA,FOLLOW_COMMA_in_procedureParameterList2045); if (state.failed) return retval; 
                            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA113);

                            	    IDENTIFIER114=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_procedureParameterList2047); if (state.failed) return retval; 
                            	    if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER114);


                            	    }
                            	    break;

                            	default :
                            	    break loop30;
                                }
                            } while (true);


                            }
                            break;

                    }

                    RPAREN115=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_procedureParameterList2053); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN115);



                    // AST REWRITE
                    // elements: IDENTIFIER
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CgsuiteTree)adaptor.nil();
                    // 334:55: -> ^( PROCEDURE_PARAMETER_LIST ( IDENTIFIER )* )
                    {
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:334:58: ^( PROCEDURE_PARAMETER_LIST ( IDENTIFIER )* )
                        {
                        CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                        root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(PROCEDURE_PARAMETER_LIST, "PROCEDURE_PARAMETER_LIST"), root_1);

                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:334:85: ( IDENTIFIER )*
                        while ( stream_IDENTIFIER.hasNext() ) {
                            adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());

                        }
                        stream_IDENTIFIER.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "procedureParameterList"

    public static class controlExpression_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "controlExpression"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:337:1: controlExpression : ( IF expression THEN statementSequence ( elseifClause )? END | ( forExpression )? ( fromExpression )? ( toExpression )? ( byExpression )? ( whileExpression )? ( whereExpression )? DO statementSequence END | FOR expression IN expression DO statementSequence END | orExpression );
    public final CgsuiteParser.controlExpression_return controlExpression() throws RecognitionException {
        CgsuiteParser.controlExpression_return retval = new CgsuiteParser.controlExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token IF116=null;
        Token THEN118=null;
        Token END121=null;
        Token DO128=null;
        Token END130=null;
        Token FOR131=null;
        Token IN133=null;
        Token DO135=null;
        Token END137=null;
        CgsuiteParser.expression_return expression117 = null;

        CgsuiteParser.statementSequence_return statementSequence119 = null;

        CgsuiteParser.elseifClause_return elseifClause120 = null;

        CgsuiteParser.forExpression_return forExpression122 = null;

        CgsuiteParser.fromExpression_return fromExpression123 = null;

        CgsuiteParser.toExpression_return toExpression124 = null;

        CgsuiteParser.byExpression_return byExpression125 = null;

        CgsuiteParser.whileExpression_return whileExpression126 = null;

        CgsuiteParser.whereExpression_return whereExpression127 = null;

        CgsuiteParser.statementSequence_return statementSequence129 = null;

        CgsuiteParser.expression_return expression132 = null;

        CgsuiteParser.expression_return expression134 = null;

        CgsuiteParser.statementSequence_return statementSequence136 = null;

        CgsuiteParser.orExpression_return orExpression138 = null;


        CgsuiteTree IF116_tree=null;
        CgsuiteTree THEN118_tree=null;
        CgsuiteTree END121_tree=null;
        CgsuiteTree DO128_tree=null;
        CgsuiteTree END130_tree=null;
        CgsuiteTree FOR131_tree=null;
        CgsuiteTree IN133_tree=null;
        CgsuiteTree DO135_tree=null;
        CgsuiteTree END137_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:338:2: ( IF expression THEN statementSequence ( elseifClause )? END | ( forExpression )? ( fromExpression )? ( toExpression )? ( byExpression )? ( whileExpression )? ( whereExpression )? DO statementSequence END | FOR expression IN expression DO statementSequence END | orExpression )
            int alt40=4;
            switch ( input.LA(1) ) {
            case IF:
                {
                alt40=1;
                }
                break;
            case FOR:
                {
                int LA40_2 = input.LA(2);

                if ( (LA40_2==IDENTIFIER) ) {
                    int LA40_5 = input.LA(3);

                    if ( (LA40_5==BY||LA40_5==DO||LA40_5==FROM||LA40_5==TO||(LA40_5>=WHERE && LA40_5<=WHILE)) ) {
                        alt40=2;
                    }
                    else if ( ((LA40_5>=PLUS && LA40_5<=MINUS)||(LA40_5>=AST && LA40_5<=PERCENT)||LA40_5==LPAREN||LA40_5==LBRACKET||(LA40_5>=EQUALS && LA40_5<=RARROW)||(LA40_5>=REFEQUALS && LA40_5<=ASN_EXP)||LA40_5==AND||LA40_5==IN||LA40_5==OR) ) {
                        alt40=3;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 40, 5, input);

                        throw nvae;
                    }
                }
                else if ( ((LA40_2>=PLUS && LA40_2<=AST)||LA40_2==LPAREN||LA40_2==LBRACKET||LA40_2==LBRACE||(LA40_2>=CARET && LA40_2<=VEEVEE)||LA40_2==BEGIN||LA40_2==BY||LA40_2==DO||(LA40_2>=FALSE && LA40_2<=FOR)||LA40_2==FROM||LA40_2==IF||(LA40_2>=NIL && LA40_2<=NOT)||(LA40_2>=THIS && LA40_2<=TRUE)||(LA40_2>=WHERE && LA40_2<=WHILE)||(LA40_2>=STRING && LA40_2<=CHAR)) ) {
                    alt40=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 2, input);

                    throw nvae;
                }
                }
                break;
            case BY:
            case DO:
            case FROM:
            case TO:
            case WHERE:
            case WHILE:
                {
                alt40=2;
                }
                break;
            case PLUS:
            case MINUS:
            case PLUSMINUS:
            case AST:
            case LPAREN:
            case LBRACKET:
            case LBRACE:
            case CARET:
            case CARETCARET:
            case VEE:
            case VEEVEE:
            case BEGIN:
            case FALSE:
            case NIL:
            case NOT:
            case THIS:
            case TRUE:
            case IDENTIFIER:
            case STRING:
            case INTEGER:
            case CHAR:
                {
                alt40=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 40, 0, input);

                throw nvae;
            }

            switch (alt40) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:338:4: IF expression THEN statementSequence ( elseifClause )? END
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    IF116=(Token)match(input,IF,FOLLOW_IF_in_controlExpression2077); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IF116_tree = (CgsuiteTree)adaptor.create(IF116);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(IF116_tree, root_0);
                    }
                    pushFollow(FOLLOW_expression_in_controlExpression2080);
                    expression117=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression117.getTree());
                    THEN118=(Token)match(input,THEN,FOLLOW_THEN_in_controlExpression2082); if (state.failed) return retval;
                    pushFollow(FOLLOW_statementSequence_in_controlExpression2085);
                    statementSequence119=statementSequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence119.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:338:43: ( elseifClause )?
                    int alt33=2;
                    int LA33_0 = input.LA(1);

                    if ( ((LA33_0>=ELSE && LA33_0<=ELSEIF)) ) {
                        alt33=1;
                    }
                    switch (alt33) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:338:43: elseifClause
                            {
                            pushFollow(FOLLOW_elseifClause_in_controlExpression2087);
                            elseifClause120=elseifClause();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, elseifClause120.getTree());

                            }
                            break;

                    }

                    END121=(Token)match(input,END,FOLLOW_END_in_controlExpression2090); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:339:4: ( forExpression )? ( fromExpression )? ( toExpression )? ( byExpression )? ( whileExpression )? ( whereExpression )? DO statementSequence END
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:339:4: ( forExpression )?
                    int alt34=2;
                    int LA34_0 = input.LA(1);

                    if ( (LA34_0==FOR) ) {
                        alt34=1;
                    }
                    switch (alt34) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:339:4: forExpression
                            {
                            pushFollow(FOLLOW_forExpression_in_controlExpression2096);
                            forExpression122=forExpression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, forExpression122.getTree());

                            }
                            break;

                    }

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:339:19: ( fromExpression )?
                    int alt35=2;
                    int LA35_0 = input.LA(1);

                    if ( (LA35_0==FROM) ) {
                        alt35=1;
                    }
                    switch (alt35) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:339:19: fromExpression
                            {
                            pushFollow(FOLLOW_fromExpression_in_controlExpression2099);
                            fromExpression123=fromExpression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, fromExpression123.getTree());

                            }
                            break;

                    }

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:339:35: ( toExpression )?
                    int alt36=2;
                    int LA36_0 = input.LA(1);

                    if ( (LA36_0==TO) ) {
                        alt36=1;
                    }
                    switch (alt36) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:339:35: toExpression
                            {
                            pushFollow(FOLLOW_toExpression_in_controlExpression2102);
                            toExpression124=toExpression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, toExpression124.getTree());

                            }
                            break;

                    }

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:339:49: ( byExpression )?
                    int alt37=2;
                    int LA37_0 = input.LA(1);

                    if ( (LA37_0==BY) ) {
                        alt37=1;
                    }
                    switch (alt37) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:339:49: byExpression
                            {
                            pushFollow(FOLLOW_byExpression_in_controlExpression2105);
                            byExpression125=byExpression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, byExpression125.getTree());

                            }
                            break;

                    }

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:339:63: ( whileExpression )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);

                    if ( (LA38_0==WHILE) ) {
                        alt38=1;
                    }
                    switch (alt38) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:339:63: whileExpression
                            {
                            pushFollow(FOLLOW_whileExpression_in_controlExpression2108);
                            whileExpression126=whileExpression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, whileExpression126.getTree());

                            }
                            break;

                    }

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:339:80: ( whereExpression )?
                    int alt39=2;
                    int LA39_0 = input.LA(1);

                    if ( (LA39_0==WHERE) ) {
                        alt39=1;
                    }
                    switch (alt39) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:339:80: whereExpression
                            {
                            pushFollow(FOLLOW_whereExpression_in_controlExpression2111);
                            whereExpression127=whereExpression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, whereExpression127.getTree());

                            }
                            break;

                    }

                    DO128=(Token)match(input,DO,FOLLOW_DO_in_controlExpression2114); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DO128_tree = (CgsuiteTree)adaptor.create(DO128);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(DO128_tree, root_0);
                    }
                    pushFollow(FOLLOW_statementSequence_in_controlExpression2117);
                    statementSequence129=statementSequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence129.getTree());
                    END130=(Token)match(input,END,FOLLOW_END_in_controlExpression2119); if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:340:4: FOR expression IN expression DO statementSequence END
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    FOR131=(Token)match(input,FOR,FOLLOW_FOR_in_controlExpression2125); if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_controlExpression2128);
                    expression132=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression132.getTree());
                    IN133=(Token)match(input,IN,FOLLOW_IN_in_controlExpression2130); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IN133_tree = (CgsuiteTree)adaptor.create(IN133);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(IN133_tree, root_0);
                    }
                    pushFollow(FOLLOW_expression_in_controlExpression2133);
                    expression134=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression134.getTree());
                    DO135=(Token)match(input,DO,FOLLOW_DO_in_controlExpression2135); if (state.failed) return retval;
                    pushFollow(FOLLOW_statementSequence_in_controlExpression2138);
                    statementSequence136=statementSequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence136.getTree());
                    END137=(Token)match(input,END,FOLLOW_END_in_controlExpression2140); if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:341:4: orExpression
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_orExpression_in_controlExpression2146);
                    orExpression138=orExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, orExpression138.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "controlExpression"

    public static class forExpression_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "forExpression"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:344:1: forExpression : FOR IDENTIFIER ;
    public final CgsuiteParser.forExpression_return forExpression() throws RecognitionException {
        CgsuiteParser.forExpression_return retval = new CgsuiteParser.forExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token FOR139=null;
        Token IDENTIFIER140=null;

        CgsuiteTree FOR139_tree=null;
        CgsuiteTree IDENTIFIER140_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:345:2: ( FOR IDENTIFIER )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:345:4: FOR IDENTIFIER
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            FOR139=(Token)match(input,FOR,FOLLOW_FOR_in_forExpression2157); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FOR139_tree = (CgsuiteTree)adaptor.create(FOR139);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(FOR139_tree, root_0);
            }
            IDENTIFIER140=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_forExpression2160); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER140_tree = (CgsuiteTree)adaptor.create(IDENTIFIER140);
            adaptor.addChild(root_0, IDENTIFIER140_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "forExpression"

    public static class fromExpression_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "fromExpression"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:348:1: fromExpression : FROM expression ;
    public final CgsuiteParser.fromExpression_return fromExpression() throws RecognitionException {
        CgsuiteParser.fromExpression_return retval = new CgsuiteParser.fromExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token FROM141=null;
        CgsuiteParser.expression_return expression142 = null;


        CgsuiteTree FROM141_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:349:2: ( FROM expression )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:349:4: FROM expression
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            FROM141=(Token)match(input,FROM,FOLLOW_FROM_in_fromExpression2172); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FROM141_tree = (CgsuiteTree)adaptor.create(FROM141);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(FROM141_tree, root_0);
            }
            pushFollow(FOLLOW_expression_in_fromExpression2175);
            expression142=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression142.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "fromExpression"

    public static class toExpression_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "toExpression"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:352:1: toExpression : TO expression ;
    public final CgsuiteParser.toExpression_return toExpression() throws RecognitionException {
        CgsuiteParser.toExpression_return retval = new CgsuiteParser.toExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token TO143=null;
        CgsuiteParser.expression_return expression144 = null;


        CgsuiteTree TO143_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:353:5: ( TO expression )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:353:7: TO expression
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            TO143=(Token)match(input,TO,FOLLOW_TO_in_toExpression2190); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            TO143_tree = (CgsuiteTree)adaptor.create(TO143);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(TO143_tree, root_0);
            }
            pushFollow(FOLLOW_expression_in_toExpression2193);
            expression144=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression144.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "toExpression"

    public static class byExpression_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "byExpression"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:356:1: byExpression : BY expression ;
    public final CgsuiteParser.byExpression_return byExpression() throws RecognitionException {
        CgsuiteParser.byExpression_return retval = new CgsuiteParser.byExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token BY145=null;
        CgsuiteParser.expression_return expression146 = null;


        CgsuiteTree BY145_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:357:5: ( BY expression )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:357:7: BY expression
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            BY145=(Token)match(input,BY,FOLLOW_BY_in_byExpression2210); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            BY145_tree = (CgsuiteTree)adaptor.create(BY145);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(BY145_tree, root_0);
            }
            pushFollow(FOLLOW_expression_in_byExpression2213);
            expression146=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression146.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "byExpression"

    public static class whileExpression_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "whileExpression"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:360:1: whileExpression : WHILE expression ;
    public final CgsuiteParser.whileExpression_return whileExpression() throws RecognitionException {
        CgsuiteParser.whileExpression_return retval = new CgsuiteParser.whileExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token WHILE147=null;
        CgsuiteParser.expression_return expression148 = null;


        CgsuiteTree WHILE147_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:361:5: ( WHILE expression )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:361:7: WHILE expression
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            WHILE147=(Token)match(input,WHILE,FOLLOW_WHILE_in_whileExpression2231); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            WHILE147_tree = (CgsuiteTree)adaptor.create(WHILE147);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(WHILE147_tree, root_0);
            }
            pushFollow(FOLLOW_expression_in_whileExpression2234);
            expression148=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression148.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "whileExpression"

    public static class whereExpression_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "whereExpression"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:364:1: whereExpression : WHERE expression ;
    public final CgsuiteParser.whereExpression_return whereExpression() throws RecognitionException {
        CgsuiteParser.whereExpression_return retval = new CgsuiteParser.whereExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token WHERE149=null;
        CgsuiteParser.expression_return expression150 = null;


        CgsuiteTree WHERE149_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:365:5: ( WHERE expression )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:365:7: WHERE expression
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            WHERE149=(Token)match(input,WHERE,FOLLOW_WHERE_in_whereExpression2248); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            WHERE149_tree = (CgsuiteTree)adaptor.create(WHERE149);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(WHERE149_tree, root_0);
            }
            pushFollow(FOLLOW_expression_in_whereExpression2251);
            expression150=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression150.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "whereExpression"

    public static class elseifClause_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "elseifClause"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:368:1: elseifClause : ( ELSEIF expression THEN statementSequence ( elseifClause )? | ELSE statementSequence );
    public final CgsuiteParser.elseifClause_return elseifClause() throws RecognitionException {
        CgsuiteParser.elseifClause_return retval = new CgsuiteParser.elseifClause_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token ELSEIF151=null;
        Token THEN153=null;
        Token ELSE156=null;
        CgsuiteParser.expression_return expression152 = null;

        CgsuiteParser.statementSequence_return statementSequence154 = null;

        CgsuiteParser.elseifClause_return elseifClause155 = null;

        CgsuiteParser.statementSequence_return statementSequence157 = null;


        CgsuiteTree ELSEIF151_tree=null;
        CgsuiteTree THEN153_tree=null;
        CgsuiteTree ELSE156_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:369:2: ( ELSEIF expression THEN statementSequence ( elseifClause )? | ELSE statementSequence )
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==ELSEIF) ) {
                alt42=1;
            }
            else if ( (LA42_0==ELSE) ) {
                alt42=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:369:4: ELSEIF expression THEN statementSequence ( elseifClause )?
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    ELSEIF151=(Token)match(input,ELSEIF,FOLLOW_ELSEIF_in_elseifClause2265); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ELSEIF151_tree = (CgsuiteTree)adaptor.create(ELSEIF151);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(ELSEIF151_tree, root_0);
                    }
                    pushFollow(FOLLOW_expression_in_elseifClause2268);
                    expression152=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression152.getTree());
                    THEN153=(Token)match(input,THEN,FOLLOW_THEN_in_elseifClause2270); if (state.failed) return retval;
                    pushFollow(FOLLOW_statementSequence_in_elseifClause2273);
                    statementSequence154=statementSequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence154.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:369:47: ( elseifClause )?
                    int alt41=2;
                    int LA41_0 = input.LA(1);

                    if ( ((LA41_0>=ELSE && LA41_0<=ELSEIF)) ) {
                        alt41=1;
                    }
                    switch (alt41) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:369:47: elseifClause
                            {
                            pushFollow(FOLLOW_elseifClause_in_elseifClause2275);
                            elseifClause155=elseifClause();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, elseifClause155.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:370:4: ELSE statementSequence
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    ELSE156=(Token)match(input,ELSE,FOLLOW_ELSE_in_elseifClause2281); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ELSE156_tree = (CgsuiteTree)adaptor.create(ELSE156);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(ELSE156_tree, root_0);
                    }
                    pushFollow(FOLLOW_statementSequence_in_elseifClause2284);
                    statementSequence157=statementSequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence157.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "elseifClause"

    public static class orExpression_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "orExpression"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:373:1: orExpression : andExpression ( OR orExpression )? ;
    public final CgsuiteParser.orExpression_return orExpression() throws RecognitionException {
        CgsuiteParser.orExpression_return retval = new CgsuiteParser.orExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token OR159=null;
        CgsuiteParser.andExpression_return andExpression158 = null;

        CgsuiteParser.orExpression_return orExpression160 = null;


        CgsuiteTree OR159_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:374:2: ( andExpression ( OR orExpression )? )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:374:4: andExpression ( OR orExpression )?
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_andExpression_in_orExpression2295);
            andExpression158=andExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression158.getTree());
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:374:18: ( OR orExpression )?
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==OR) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:374:19: OR orExpression
                    {
                    OR159=(Token)match(input,OR,FOLLOW_OR_in_orExpression2298); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OR159_tree = (CgsuiteTree)adaptor.create(OR159);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(OR159_tree, root_0);
                    }
                    pushFollow(FOLLOW_orExpression_in_orExpression2301);
                    orExpression160=orExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, orExpression160.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "orExpression"

    public static class andExpression_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "andExpression"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:377:1: andExpression : notExpr ( AND andExpression )? ;
    public final CgsuiteParser.andExpression_return andExpression() throws RecognitionException {
        CgsuiteParser.andExpression_return retval = new CgsuiteParser.andExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token AND162=null;
        CgsuiteParser.notExpr_return notExpr161 = null;

        CgsuiteParser.andExpression_return andExpression163 = null;


        CgsuiteTree AND162_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:378:2: ( notExpr ( AND andExpression )? )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:378:4: notExpr ( AND andExpression )?
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_notExpr_in_andExpression2314);
            notExpr161=notExpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, notExpr161.getTree());
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:378:12: ( AND andExpression )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==AND) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:378:13: AND andExpression
                    {
                    AND162=(Token)match(input,AND,FOLLOW_AND_in_andExpression2317); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    AND162_tree = (CgsuiteTree)adaptor.create(AND162);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(AND162_tree, root_0);
                    }
                    pushFollow(FOLLOW_andExpression_in_andExpression2320);
                    andExpression163=andExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression163.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "andExpression"

    public static class notExpr_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "notExpr"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:381:1: notExpr : ( NOT notExpr | relationalExpr );
    public final CgsuiteParser.notExpr_return notExpr() throws RecognitionException {
        CgsuiteParser.notExpr_return retval = new CgsuiteParser.notExpr_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token NOT164=null;
        CgsuiteParser.notExpr_return notExpr165 = null;

        CgsuiteParser.relationalExpr_return relationalExpr166 = null;


        CgsuiteTree NOT164_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:382:5: ( NOT notExpr | relationalExpr )
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==NOT) ) {
                alt45=1;
            }
            else if ( ((LA45_0>=PLUS && LA45_0<=AST)||LA45_0==LPAREN||LA45_0==LBRACKET||LA45_0==LBRACE||(LA45_0>=CARET && LA45_0<=VEEVEE)||LA45_0==BEGIN||LA45_0==FALSE||LA45_0==NIL||LA45_0==THIS||LA45_0==TRUE||(LA45_0>=IDENTIFIER && LA45_0<=CHAR)) ) {
                alt45=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 45, 0, input);

                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:382:7: NOT notExpr
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    NOT164=(Token)match(input,NOT,FOLLOW_NOT_in_notExpr2336); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NOT164_tree = (CgsuiteTree)adaptor.create(NOT164);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(NOT164_tree, root_0);
                    }
                    pushFollow(FOLLOW_notExpr_in_notExpr2339);
                    notExpr165=notExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, notExpr165.getTree());

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:383:7: relationalExpr
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_relationalExpr_in_notExpr2347);
                    relationalExpr166=relationalExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpr166.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "notExpr"

    public static class relationalExpr_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "relationalExpr"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:386:1: relationalExpr : addExpr ( relationalToken relationalExpr )? ;
    public final CgsuiteParser.relationalExpr_return relationalExpr() throws RecognitionException {
        CgsuiteParser.relationalExpr_return retval = new CgsuiteParser.relationalExpr_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        CgsuiteParser.addExpr_return addExpr167 = null;

        CgsuiteParser.relationalToken_return relationalToken168 = null;

        CgsuiteParser.relationalExpr_return relationalExpr169 = null;



        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:387:2: ( addExpr ( relationalToken relationalExpr )? )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:387:4: addExpr ( relationalToken relationalExpr )?
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_addExpr_in_relationalExpr2361);
            addExpr167=addExpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, addExpr167.getTree());
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:387:12: ( relationalToken relationalExpr )?
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( ((LA46_0>=EQUALS && LA46_0<=COMPARE)||(LA46_0>=REFEQUALS && LA46_0<=REFNEQ)) ) {
                alt46=1;
            }
            switch (alt46) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:387:13: relationalToken relationalExpr
                    {
                    pushFollow(FOLLOW_relationalToken_in_relationalExpr2364);
                    relationalToken168=relationalToken();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (CgsuiteTree)adaptor.becomeRoot(relationalToken168.getTree(), root_0);
                    pushFollow(FOLLOW_relationalExpr_in_relationalExpr2367);
                    relationalExpr169=relationalExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpr169.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "relationalExpr"

    public static class relationalToken_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "relationalToken"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:390:1: relationalToken : ( REFEQUALS | REFNEQ | standardRelationalToken );
    public final CgsuiteParser.relationalToken_return relationalToken() throws RecognitionException {
        CgsuiteParser.relationalToken_return retval = new CgsuiteParser.relationalToken_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token REFEQUALS170=null;
        Token REFNEQ171=null;
        CgsuiteParser.standardRelationalToken_return standardRelationalToken172 = null;


        CgsuiteTree REFEQUALS170_tree=null;
        CgsuiteTree REFNEQ171_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:391:2: ( REFEQUALS | REFNEQ | standardRelationalToken )
            int alt47=3;
            switch ( input.LA(1) ) {
            case REFEQUALS:
                {
                alt47=1;
                }
                break;
            case REFNEQ:
                {
                alt47=2;
                }
                break;
            case EQUALS:
            case NEQ:
            case LT:
            case GT:
            case LEQ:
            case GEQ:
            case CONFUSED:
            case COMPARE:
                {
                alt47=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 47, 0, input);

                throw nvae;
            }

            switch (alt47) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:391:4: REFEQUALS
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    REFEQUALS170=(Token)match(input,REFEQUALS,FOLLOW_REFEQUALS_in_relationalToken2380); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    REFEQUALS170_tree = (CgsuiteTree)adaptor.create(REFEQUALS170);
                    adaptor.addChild(root_0, REFEQUALS170_tree);
                    }

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:392:4: REFNEQ
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    REFNEQ171=(Token)match(input,REFNEQ,FOLLOW_REFNEQ_in_relationalToken2385); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    REFNEQ171_tree = (CgsuiteTree)adaptor.create(REFNEQ171);
                    adaptor.addChild(root_0, REFNEQ171_tree);
                    }

                    }
                    break;
                case 3 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:393:4: standardRelationalToken
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_standardRelationalToken_in_relationalToken2390);
                    standardRelationalToken172=standardRelationalToken();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, standardRelationalToken172.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "relationalToken"

    public static class standardRelationalToken_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "standardRelationalToken"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:396:1: standardRelationalToken : ( EQUALS | NEQ | LT | GT | LEQ | GEQ | CONFUSED | COMPARE );
    public final CgsuiteParser.standardRelationalToken_return standardRelationalToken() throws RecognitionException {
        CgsuiteParser.standardRelationalToken_return retval = new CgsuiteParser.standardRelationalToken_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token set173=null;

        CgsuiteTree set173_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:397:5: ( EQUALS | NEQ | LT | GT | LEQ | GEQ | CONFUSED | COMPARE )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            set173=(Token)input.LT(1);
            if ( (input.LA(1)>=EQUALS && input.LA(1)<=COMPARE) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (CgsuiteTree)adaptor.create(set173));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "standardRelationalToken"

    public static class addExpr_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "addExpr"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:407:1: addExpr : multiplyExpr ( ( PLUS | MINUS ) multiplyExpr )* ;
    public final CgsuiteParser.addExpr_return addExpr() throws RecognitionException {
        CgsuiteParser.addExpr_return retval = new CgsuiteParser.addExpr_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token PLUS175=null;
        Token MINUS176=null;
        CgsuiteParser.multiplyExpr_return multiplyExpr174 = null;

        CgsuiteParser.multiplyExpr_return multiplyExpr177 = null;


        CgsuiteTree PLUS175_tree=null;
        CgsuiteTree MINUS176_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:408:2: ( multiplyExpr ( ( PLUS | MINUS ) multiplyExpr )* )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:408:4: multiplyExpr ( ( PLUS | MINUS ) multiplyExpr )*
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_multiplyExpr_in_addExpr2454);
            multiplyExpr174=multiplyExpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplyExpr174.getTree());
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:408:17: ( ( PLUS | MINUS ) multiplyExpr )*
            loop49:
            do {
                int alt49=2;
                int LA49_0 = input.LA(1);

                if ( ((LA49_0>=PLUS && LA49_0<=MINUS)) ) {
                    alt49=1;
                }


                switch (alt49) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:408:18: ( PLUS | MINUS ) multiplyExpr
            	    {
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:408:18: ( PLUS | MINUS )
            	    int alt48=2;
            	    int LA48_0 = input.LA(1);

            	    if ( (LA48_0==PLUS) ) {
            	        alt48=1;
            	    }
            	    else if ( (LA48_0==MINUS) ) {
            	        alt48=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 48, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt48) {
            	        case 1 :
            	            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:408:19: PLUS
            	            {
            	            PLUS175=(Token)match(input,PLUS,FOLLOW_PLUS_in_addExpr2458); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            PLUS175_tree = (CgsuiteTree)adaptor.create(PLUS175);
            	            root_0 = (CgsuiteTree)adaptor.becomeRoot(PLUS175_tree, root_0);
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:408:27: MINUS
            	            {
            	            MINUS176=(Token)match(input,MINUS,FOLLOW_MINUS_in_addExpr2463); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            MINUS176_tree = (CgsuiteTree)adaptor.create(MINUS176);
            	            root_0 = (CgsuiteTree)adaptor.becomeRoot(MINUS176_tree, root_0);
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_multiplyExpr_in_addExpr2467);
            	    multiplyExpr177=multiplyExpr();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplyExpr177.getTree());

            	    }
            	    break;

            	default :
            	    break loop49;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "addExpr"

    public static class multiplyExpr_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "multiplyExpr"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:411:1: multiplyExpr : expExpr ( ( AST | FSLASH | PERCENT ) expExpr )* ;
    public final CgsuiteParser.multiplyExpr_return multiplyExpr() throws RecognitionException {
        CgsuiteParser.multiplyExpr_return retval = new CgsuiteParser.multiplyExpr_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token AST179=null;
        Token FSLASH180=null;
        Token PERCENT181=null;
        CgsuiteParser.expExpr_return expExpr178 = null;

        CgsuiteParser.expExpr_return expExpr182 = null;


        CgsuiteTree AST179_tree=null;
        CgsuiteTree FSLASH180_tree=null;
        CgsuiteTree PERCENT181_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:412:2: ( expExpr ( ( AST | FSLASH | PERCENT ) expExpr )* )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:412:4: expExpr ( ( AST | FSLASH | PERCENT ) expExpr )*
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_expExpr_in_multiplyExpr2481);
            expExpr178=expExpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expExpr178.getTree());
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:412:12: ( ( AST | FSLASH | PERCENT ) expExpr )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( ((LA51_0>=AST && LA51_0<=FSLASH)||LA51_0==PERCENT) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:412:13: ( AST | FSLASH | PERCENT ) expExpr
            	    {
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:412:13: ( AST | FSLASH | PERCENT )
            	    int alt50=3;
            	    switch ( input.LA(1) ) {
            	    case AST:
            	        {
            	        alt50=1;
            	        }
            	        break;
            	    case FSLASH:
            	        {
            	        alt50=2;
            	        }
            	        break;
            	    case PERCENT:
            	        {
            	        alt50=3;
            	        }
            	        break;
            	    default:
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 50, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt50) {
            	        case 1 :
            	            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:412:14: AST
            	            {
            	            AST179=(Token)match(input,AST,FOLLOW_AST_in_multiplyExpr2485); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            AST179_tree = (CgsuiteTree)adaptor.create(AST179);
            	            root_0 = (CgsuiteTree)adaptor.becomeRoot(AST179_tree, root_0);
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:412:21: FSLASH
            	            {
            	            FSLASH180=(Token)match(input,FSLASH,FOLLOW_FSLASH_in_multiplyExpr2490); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            FSLASH180_tree = (CgsuiteTree)adaptor.create(FSLASH180);
            	            root_0 = (CgsuiteTree)adaptor.becomeRoot(FSLASH180_tree, root_0);
            	            }

            	            }
            	            break;
            	        case 3 :
            	            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:412:31: PERCENT
            	            {
            	            PERCENT181=(Token)match(input,PERCENT,FOLLOW_PERCENT_in_multiplyExpr2495); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            PERCENT181_tree = (CgsuiteTree)adaptor.create(PERCENT181);
            	            root_0 = (CgsuiteTree)adaptor.becomeRoot(PERCENT181_tree, root_0);
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_expExpr_in_multiplyExpr2499);
            	    expExpr182=expExpr();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expExpr182.getTree());

            	    }
            	    break;

            	default :
            	    break loop51;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "multiplyExpr"

    public static class expExpr_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expExpr"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:415:1: expExpr : plusminusExpr ( EXP plusminusExpr )? ;
    public final CgsuiteParser.expExpr_return expExpr() throws RecognitionException {
        CgsuiteParser.expExpr_return retval = new CgsuiteParser.expExpr_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token EXP184=null;
        CgsuiteParser.plusminusExpr_return plusminusExpr183 = null;

        CgsuiteParser.plusminusExpr_return plusminusExpr185 = null;


        CgsuiteTree EXP184_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:416:2: ( plusminusExpr ( EXP plusminusExpr )? )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:416:4: plusminusExpr ( EXP plusminusExpr )?
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_plusminusExpr_in_expExpr2512);
            plusminusExpr183=plusminusExpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, plusminusExpr183.getTree());
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:416:18: ( EXP plusminusExpr )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==EXP) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:416:19: EXP plusminusExpr
                    {
                    EXP184=(Token)match(input,EXP,FOLLOW_EXP_in_expExpr2515); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EXP184_tree = (CgsuiteTree)adaptor.create(EXP184);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(EXP184_tree, root_0);
                    }
                    pushFollow(FOLLOW_plusminusExpr_in_expExpr2518);
                    plusminusExpr185=plusminusExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, plusminusExpr185.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "expExpr"

    public static class plusminusExpr_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "plusminusExpr"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:419:1: plusminusExpr options {backtrack=true; memoize=true; } : ( PLUSMINUS LPAREN expression ( COMMA expression )* RPAREN -> ^( PLUSMINUS ( expression )* ) | PLUSMINUS unaryExpr -> ^( PLUSMINUS unaryExpr ) | unaryExpr );
    public final CgsuiteParser.plusminusExpr_return plusminusExpr() throws RecognitionException {
        CgsuiteParser.plusminusExpr_return retval = new CgsuiteParser.plusminusExpr_return();
        retval.start = input.LT(1);
        int plusminusExpr_StartIndex = input.index();
        CgsuiteTree root_0 = null;

        Token PLUSMINUS186=null;
        Token LPAREN187=null;
        Token COMMA189=null;
        Token RPAREN191=null;
        Token PLUSMINUS192=null;
        CgsuiteParser.expression_return expression188 = null;

        CgsuiteParser.expression_return expression190 = null;

        CgsuiteParser.unaryExpr_return unaryExpr193 = null;

        CgsuiteParser.unaryExpr_return unaryExpr194 = null;


        CgsuiteTree PLUSMINUS186_tree=null;
        CgsuiteTree LPAREN187_tree=null;
        CgsuiteTree COMMA189_tree=null;
        CgsuiteTree RPAREN191_tree=null;
        CgsuiteTree PLUSMINUS192_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_PLUSMINUS=new RewriteRuleTokenStream(adaptor,"token PLUSMINUS");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_unaryExpr=new RewriteRuleSubtreeStream(adaptor,"rule unaryExpr");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 45) ) { return retval; }
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:425:5: ( PLUSMINUS LPAREN expression ( COMMA expression )* RPAREN -> ^( PLUSMINUS ( expression )* ) | PLUSMINUS unaryExpr -> ^( PLUSMINUS unaryExpr ) | unaryExpr )
            int alt54=3;
            alt54 = dfa54.predict(input);
            switch (alt54) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:425:7: PLUSMINUS LPAREN expression ( COMMA expression )* RPAREN
                    {
                    PLUSMINUS186=(Token)match(input,PLUSMINUS,FOLLOW_PLUSMINUS_in_plusminusExpr2561); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PLUSMINUS.add(PLUSMINUS186);

                    LPAREN187=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_plusminusExpr2563); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN187);

                    pushFollow(FOLLOW_expression_in_plusminusExpr2565);
                    expression188=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression188.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:425:35: ( COMMA expression )*
                    loop53:
                    do {
                        int alt53=2;
                        int LA53_0 = input.LA(1);

                        if ( (LA53_0==COMMA) ) {
                            alt53=1;
                        }


                        switch (alt53) {
                    	case 1 :
                    	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:425:36: COMMA expression
                    	    {
                    	    COMMA189=(Token)match(input,COMMA,FOLLOW_COMMA_in_plusminusExpr2568); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA189);

                    	    pushFollow(FOLLOW_expression_in_plusminusExpr2570);
                    	    expression190=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expression.add(expression190.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop53;
                        }
                    } while (true);

                    RPAREN191=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_plusminusExpr2574); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN191);



                    // AST REWRITE
                    // elements: expression, PLUSMINUS
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CgsuiteTree)adaptor.nil();
                    // 425:62: -> ^( PLUSMINUS ( expression )* )
                    {
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:425:65: ^( PLUSMINUS ( expression )* )
                        {
                        CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                        root_1 = (CgsuiteTree)adaptor.becomeRoot(stream_PLUSMINUS.nextNode(), root_1);

                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:425:77: ( expression )*
                        while ( stream_expression.hasNext() ) {
                            adaptor.addChild(root_1, stream_expression.nextTree());

                        }
                        stream_expression.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:426:7: PLUSMINUS unaryExpr
                    {
                    PLUSMINUS192=(Token)match(input,PLUSMINUS,FOLLOW_PLUSMINUS_in_plusminusExpr2591); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PLUSMINUS.add(PLUSMINUS192);

                    pushFollow(FOLLOW_unaryExpr_in_plusminusExpr2593);
                    unaryExpr193=unaryExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_unaryExpr.add(unaryExpr193.getTree());


                    // AST REWRITE
                    // elements: unaryExpr, PLUSMINUS
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CgsuiteTree)adaptor.nil();
                    // 426:27: -> ^( PLUSMINUS unaryExpr )
                    {
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:426:30: ^( PLUSMINUS unaryExpr )
                        {
                        CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                        root_1 = (CgsuiteTree)adaptor.becomeRoot(stream_PLUSMINUS.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_unaryExpr.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:427:7: unaryExpr
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_unaryExpr_in_plusminusExpr2609);
                    unaryExpr194=unaryExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpr194.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 45, plusminusExpr_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "plusminusExpr"

    public static class unaryExpr_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unaryExpr"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:430:1: unaryExpr : ( MINUS unaryExpr -> ^( UNARY_MINUS unaryExpr ) | PLUS unaryExpr -> ^( UNARY_PLUS unaryExpr ) | postfixExpr );
    public final CgsuiteParser.unaryExpr_return unaryExpr() throws RecognitionException {
        CgsuiteParser.unaryExpr_return retval = new CgsuiteParser.unaryExpr_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token MINUS195=null;
        Token PLUS197=null;
        CgsuiteParser.unaryExpr_return unaryExpr196 = null;

        CgsuiteParser.unaryExpr_return unaryExpr198 = null;

        CgsuiteParser.postfixExpr_return postfixExpr199 = null;


        CgsuiteTree MINUS195_tree=null;
        CgsuiteTree PLUS197_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_MINUS=new RewriteRuleTokenStream(adaptor,"token MINUS");
        RewriteRuleSubtreeStream stream_unaryExpr=new RewriteRuleSubtreeStream(adaptor,"rule unaryExpr");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:431:2: ( MINUS unaryExpr -> ^( UNARY_MINUS unaryExpr ) | PLUS unaryExpr -> ^( UNARY_PLUS unaryExpr ) | postfixExpr )
            int alt55=3;
            switch ( input.LA(1) ) {
            case MINUS:
                {
                alt55=1;
                }
                break;
            case PLUS:
                {
                alt55=2;
                }
                break;
            case AST:
            case LPAREN:
            case LBRACKET:
            case LBRACE:
            case CARET:
            case CARETCARET:
            case VEE:
            case VEEVEE:
            case BEGIN:
            case FALSE:
            case NIL:
            case THIS:
            case TRUE:
            case IDENTIFIER:
            case STRING:
            case INTEGER:
            case CHAR:
                {
                alt55=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }

            switch (alt55) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:431:4: MINUS unaryExpr
                    {
                    MINUS195=(Token)match(input,MINUS,FOLLOW_MINUS_in_unaryExpr2623); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_MINUS.add(MINUS195);

                    pushFollow(FOLLOW_unaryExpr_in_unaryExpr2625);
                    unaryExpr196=unaryExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_unaryExpr.add(unaryExpr196.getTree());


                    // AST REWRITE
                    // elements: unaryExpr
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CgsuiteTree)adaptor.nil();
                    // 431:20: -> ^( UNARY_MINUS unaryExpr )
                    {
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:431:23: ^( UNARY_MINUS unaryExpr )
                        {
                        CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                        root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(UNARY_MINUS, "UNARY_MINUS"), root_1);

                        adaptor.addChild(root_1, stream_unaryExpr.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:432:7: PLUS unaryExpr
                    {
                    PLUS197=(Token)match(input,PLUS,FOLLOW_PLUS_in_unaryExpr2641); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PLUS.add(PLUS197);

                    pushFollow(FOLLOW_unaryExpr_in_unaryExpr2643);
                    unaryExpr198=unaryExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_unaryExpr.add(unaryExpr198.getTree());


                    // AST REWRITE
                    // elements: unaryExpr
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CgsuiteTree)adaptor.nil();
                    // 432:22: -> ^( UNARY_PLUS unaryExpr )
                    {
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:432:25: ^( UNARY_PLUS unaryExpr )
                        {
                        CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                        root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(UNARY_PLUS, "UNARY_PLUS"), root_1);

                        adaptor.addChild(root_1, stream_unaryExpr.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:433:7: postfixExpr
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_postfixExpr_in_unaryExpr2659);
                    postfixExpr199=postfixExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, postfixExpr199.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "unaryExpr"

    public static class postfixExpr_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "postfixExpr"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:436:1: postfixExpr : ( upstarExpr -> upstarExpr ) ( DOT IDENTIFIER -> ^( DOT $postfixExpr IDENTIFIER ) | x= arrayReference -> ^( ARRAY_REFERENCE[((CgsuiteTree) x.getTree()).getToken()] $postfixExpr arrayReference ) | y= functionCall -> ^( FUNCTION_CALL[((CgsuiteTree) y.getTree()).getToken()] $postfixExpr functionCall ) )* ;
    public final CgsuiteParser.postfixExpr_return postfixExpr() throws RecognitionException {
        CgsuiteParser.postfixExpr_return retval = new CgsuiteParser.postfixExpr_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token DOT201=null;
        Token IDENTIFIER202=null;
        CgsuiteParser.arrayReference_return x = null;

        CgsuiteParser.functionCall_return y = null;

        CgsuiteParser.upstarExpr_return upstarExpr200 = null;


        CgsuiteTree DOT201_tree=null;
        CgsuiteTree IDENTIFIER202_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleSubtreeStream stream_functionCall=new RewriteRuleSubtreeStream(adaptor,"rule functionCall");
        RewriteRuleSubtreeStream stream_upstarExpr=new RewriteRuleSubtreeStream(adaptor,"rule upstarExpr");
        RewriteRuleSubtreeStream stream_arrayReference=new RewriteRuleSubtreeStream(adaptor,"rule arrayReference");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:437:2: ( ( upstarExpr -> upstarExpr ) ( DOT IDENTIFIER -> ^( DOT $postfixExpr IDENTIFIER ) | x= arrayReference -> ^( ARRAY_REFERENCE[((CgsuiteTree) x.getTree()).getToken()] $postfixExpr arrayReference ) | y= functionCall -> ^( FUNCTION_CALL[((CgsuiteTree) y.getTree()).getToken()] $postfixExpr functionCall ) )* )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:437:4: ( upstarExpr -> upstarExpr ) ( DOT IDENTIFIER -> ^( DOT $postfixExpr IDENTIFIER ) | x= arrayReference -> ^( ARRAY_REFERENCE[((CgsuiteTree) x.getTree()).getToken()] $postfixExpr arrayReference ) | y= functionCall -> ^( FUNCTION_CALL[((CgsuiteTree) y.getTree()).getToken()] $postfixExpr functionCall ) )*
            {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:437:4: ( upstarExpr -> upstarExpr )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:437:5: upstarExpr
            {
            pushFollow(FOLLOW_upstarExpr_in_postfixExpr2672);
            upstarExpr200=upstarExpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_upstarExpr.add(upstarExpr200.getTree());


            // AST REWRITE
            // elements: upstarExpr
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CgsuiteTree)adaptor.nil();
            // 437:16: -> upstarExpr
            {
                adaptor.addChild(root_0, stream_upstarExpr.nextTree());

            }

            retval.tree = root_0;}
            }

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:438:4: ( DOT IDENTIFIER -> ^( DOT $postfixExpr IDENTIFIER ) | x= arrayReference -> ^( ARRAY_REFERENCE[((CgsuiteTree) x.getTree()).getToken()] $postfixExpr arrayReference ) | y= functionCall -> ^( FUNCTION_CALL[((CgsuiteTree) y.getTree()).getToken()] $postfixExpr functionCall ) )*
            loop56:
            do {
                int alt56=4;
                switch ( input.LA(1) ) {
                case DOT:
                    {
                    alt56=1;
                    }
                    break;
                case LBRACKET:
                    {
                    alt56=2;
                    }
                    break;
                case LPAREN:
                    {
                    alt56=3;
                    }
                    break;

                }

                switch (alt56) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:438:6: DOT IDENTIFIER
            	    {
            	    DOT201=(Token)match(input,DOT,FOLLOW_DOT_in_postfixExpr2684); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(DOT201);

            	    IDENTIFIER202=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_postfixExpr2686); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER202);



            	    // AST REWRITE
            	    // elements: DOT, IDENTIFIER, postfixExpr
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (CgsuiteTree)adaptor.nil();
            	    // 438:22: -> ^( DOT $postfixExpr IDENTIFIER )
            	    {
            	        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:438:25: ^( DOT $postfixExpr IDENTIFIER )
            	        {
            	        CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
            	        root_1 = (CgsuiteTree)adaptor.becomeRoot(stream_DOT.nextNode(), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;
            	case 2 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:439:6: x= arrayReference
            	    {
            	    pushFollow(FOLLOW_arrayReference_in_postfixExpr2707);
            	    x=arrayReference();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_arrayReference.add(x.getTree());


            	    // AST REWRITE
            	    // elements: arrayReference, postfixExpr
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (CgsuiteTree)adaptor.nil();
            	    // 439:22: -> ^( ARRAY_REFERENCE[((CgsuiteTree) x.getTree()).getToken()] $postfixExpr arrayReference )
            	    {
            	        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:439:25: ^( ARRAY_REFERENCE[((CgsuiteTree) x.getTree()).getToken()] $postfixExpr arrayReference )
            	        {
            	        CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
            	        root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(ARRAY_REFERENCE, ((CgsuiteTree) x.getTree()).getToken()), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_arrayReference.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;
            	case 3 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:440:6: y= functionCall
            	    {
            	    pushFollow(FOLLOW_functionCall_in_postfixExpr2727);
            	    y=functionCall();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_functionCall.add(y.getTree());


            	    // AST REWRITE
            	    // elements: functionCall, postfixExpr
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (CgsuiteTree)adaptor.nil();
            	    // 440:21: -> ^( FUNCTION_CALL[((CgsuiteTree) y.getTree()).getToken()] $postfixExpr functionCall )
            	    {
            	        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:440:24: ^( FUNCTION_CALL[((CgsuiteTree) y.getTree()).getToken()] $postfixExpr functionCall )
            	        {
            	        CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
            	        root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(FUNCTION_CALL, ((CgsuiteTree) y.getTree()).getToken()), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_functionCall.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop56;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "postfixExpr"

    public static class arrayReference_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arrayReference"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:444:1: arrayReference : LBRACKET expression ( COMMA expression )* RBRACKET -> ^( ARRAY_INDEX_LIST[$LBRACKET] ( expression )* ) ;
    public final CgsuiteParser.arrayReference_return arrayReference() throws RecognitionException {
        CgsuiteParser.arrayReference_return retval = new CgsuiteParser.arrayReference_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token LBRACKET203=null;
        Token COMMA205=null;
        Token RBRACKET207=null;
        CgsuiteParser.expression_return expression204 = null;

        CgsuiteParser.expression_return expression206 = null;


        CgsuiteTree LBRACKET203_tree=null;
        CgsuiteTree COMMA205_tree=null;
        CgsuiteTree RBRACKET207_tree=null;
        RewriteRuleTokenStream stream_LBRACKET=new RewriteRuleTokenStream(adaptor,"token LBRACKET");
        RewriteRuleTokenStream stream_RBRACKET=new RewriteRuleTokenStream(adaptor,"token RBRACKET");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:445:2: ( LBRACKET expression ( COMMA expression )* RBRACKET -> ^( ARRAY_INDEX_LIST[$LBRACKET] ( expression )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:445:4: LBRACKET expression ( COMMA expression )* RBRACKET
            {
            LBRACKET203=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayReference2758); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LBRACKET.add(LBRACKET203);

            pushFollow(FOLLOW_expression_in_arrayReference2760);
            expression204=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expression.add(expression204.getTree());
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:445:24: ( COMMA expression )*
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);

                if ( (LA57_0==COMMA) ) {
                    alt57=1;
                }


                switch (alt57) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:445:25: COMMA expression
            	    {
            	    COMMA205=(Token)match(input,COMMA,FOLLOW_COMMA_in_arrayReference2763); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA205);

            	    pushFollow(FOLLOW_expression_in_arrayReference2765);
            	    expression206=expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_expression.add(expression206.getTree());

            	    }
            	    break;

            	default :
            	    break loop57;
                }
            } while (true);

            RBRACKET207=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayReference2769); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RBRACKET.add(RBRACKET207);



            // AST REWRITE
            // elements: expression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CgsuiteTree)adaptor.nil();
            // 446:7: -> ^( ARRAY_INDEX_LIST[$LBRACKET] ( expression )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:446:10: ^( ARRAY_INDEX_LIST[$LBRACKET] ( expression )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(ARRAY_INDEX_LIST, LBRACKET203), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:446:40: ( expression )*
                while ( stream_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_expression.nextTree());

                }
                stream_expression.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "arrayReference"

    public static class functionCall_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "functionCall"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:449:1: functionCall : LPAREN ( functionArgument ( COMMA functionArgument )* )? RPAREN -> ^( FUNCTION_CALL_ARGUMENT_LIST[$LPAREN] ( functionArgument )* ) ;
    public final CgsuiteParser.functionCall_return functionCall() throws RecognitionException {
        CgsuiteParser.functionCall_return retval = new CgsuiteParser.functionCall_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token LPAREN208=null;
        Token COMMA210=null;
        Token RPAREN212=null;
        CgsuiteParser.functionArgument_return functionArgument209 = null;

        CgsuiteParser.functionArgument_return functionArgument211 = null;


        CgsuiteTree LPAREN208_tree=null;
        CgsuiteTree COMMA210_tree=null;
        CgsuiteTree RPAREN212_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_functionArgument=new RewriteRuleSubtreeStream(adaptor,"rule functionArgument");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:450:2: ( LPAREN ( functionArgument ( COMMA functionArgument )* )? RPAREN -> ^( FUNCTION_CALL_ARGUMENT_LIST[$LPAREN] ( functionArgument )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:450:4: LPAREN ( functionArgument ( COMMA functionArgument )* )? RPAREN
            {
            LPAREN208=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_functionCall2797); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN208);

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:450:11: ( functionArgument ( COMMA functionArgument )* )?
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( ((LA59_0>=PLUS && LA59_0<=AST)||LA59_0==LPAREN||LA59_0==LBRACKET||LA59_0==LBRACE||(LA59_0>=CARET && LA59_0<=VEEVEE)||LA59_0==BEGIN||LA59_0==BY||LA59_0==DO||(LA59_0>=FALSE && LA59_0<=FOR)||LA59_0==FROM||LA59_0==IF||(LA59_0>=NIL && LA59_0<=NOT)||(LA59_0>=THIS && LA59_0<=TRUE)||(LA59_0>=WHERE && LA59_0<=WHILE)||(LA59_0>=IDENTIFIER && LA59_0<=CHAR)) ) {
                alt59=1;
            }
            switch (alt59) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:450:12: functionArgument ( COMMA functionArgument )*
                    {
                    pushFollow(FOLLOW_functionArgument_in_functionCall2800);
                    functionArgument209=functionArgument();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_functionArgument.add(functionArgument209.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:450:29: ( COMMA functionArgument )*
                    loop58:
                    do {
                        int alt58=2;
                        int LA58_0 = input.LA(1);

                        if ( (LA58_0==COMMA) ) {
                            alt58=1;
                        }


                        switch (alt58) {
                    	case 1 :
                    	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:450:30: COMMA functionArgument
                    	    {
                    	    COMMA210=(Token)match(input,COMMA,FOLLOW_COMMA_in_functionCall2803); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA210);

                    	    pushFollow(FOLLOW_functionArgument_in_functionCall2805);
                    	    functionArgument211=functionArgument();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_functionArgument.add(functionArgument211.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop58;
                        }
                    } while (true);


                    }
                    break;

            }

            RPAREN212=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_functionCall2811); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN212);



            // AST REWRITE
            // elements: functionArgument
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CgsuiteTree)adaptor.nil();
            // 451:7: -> ^( FUNCTION_CALL_ARGUMENT_LIST[$LPAREN] ( functionArgument )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:451:10: ^( FUNCTION_CALL_ARGUMENT_LIST[$LPAREN] ( functionArgument )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(FUNCTION_CALL_ARGUMENT_LIST, LPAREN208), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:451:49: ( functionArgument )*
                while ( stream_functionArgument.hasNext() ) {
                    adaptor.addChild(root_1, stream_functionArgument.nextTree());

                }
                stream_functionArgument.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "functionCall"

    public static class functionArgument_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "functionArgument"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:454:1: functionArgument : ( IDENTIFIER BIGRARROW )? expression ;
    public final CgsuiteParser.functionArgument_return functionArgument() throws RecognitionException {
        CgsuiteParser.functionArgument_return retval = new CgsuiteParser.functionArgument_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token IDENTIFIER213=null;
        Token BIGRARROW214=null;
        CgsuiteParser.expression_return expression215 = null;


        CgsuiteTree IDENTIFIER213_tree=null;
        CgsuiteTree BIGRARROW214_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:455:2: ( ( IDENTIFIER BIGRARROW )? expression )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:455:4: ( IDENTIFIER BIGRARROW )? expression
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:455:4: ( IDENTIFIER BIGRARROW )?
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==IDENTIFIER) ) {
                int LA60_1 = input.LA(2);

                if ( (LA60_1==BIGRARROW) ) {
                    alt60=1;
                }
            }
            switch (alt60) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:455:5: IDENTIFIER BIGRARROW
                    {
                    IDENTIFIER213=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_functionArgument2841); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER213_tree = (CgsuiteTree)adaptor.create(IDENTIFIER213);
                    adaptor.addChild(root_0, IDENTIFIER213_tree);
                    }
                    BIGRARROW214=(Token)match(input,BIGRARROW,FOLLOW_BIGRARROW_in_functionArgument2843); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BIGRARROW214_tree = (CgsuiteTree)adaptor.create(BIGRARROW214);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(BIGRARROW214_tree, root_0);
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_expression_in_functionArgument2848);
            expression215=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression215.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "functionArgument"

    public static class upstarExpr_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "upstarExpr"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:458:1: upstarExpr options {backtrack=true; memoize=true; } : ( ( CARET | CARETCARET | VEE | VEEVEE ) starExpr | ( CARET | VEE ) primaryExpr starExpr | ( CARET | VEE ) primaryExpr | starExpr | CARET | CARETCARET | VEE | VEEVEE | primaryExpr );
    public final CgsuiteParser.upstarExpr_return upstarExpr() throws RecognitionException {
        CgsuiteParser.upstarExpr_return retval = new CgsuiteParser.upstarExpr_return();
        retval.start = input.LT(1);
        int upstarExpr_StartIndex = input.index();
        CgsuiteTree root_0 = null;

        Token set216=null;
        Token set218=null;
        Token set221=null;
        Token CARET224=null;
        Token CARETCARET225=null;
        Token VEE226=null;
        Token VEEVEE227=null;
        CgsuiteParser.starExpr_return starExpr217 = null;

        CgsuiteParser.primaryExpr_return primaryExpr219 = null;

        CgsuiteParser.starExpr_return starExpr220 = null;

        CgsuiteParser.primaryExpr_return primaryExpr222 = null;

        CgsuiteParser.starExpr_return starExpr223 = null;

        CgsuiteParser.primaryExpr_return primaryExpr228 = null;


        CgsuiteTree set216_tree=null;
        CgsuiteTree set218_tree=null;
        CgsuiteTree set221_tree=null;
        CgsuiteTree CARET224_tree=null;
        CgsuiteTree CARETCARET225_tree=null;
        CgsuiteTree VEE226_tree=null;
        CgsuiteTree VEEVEE227_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 51) ) { return retval; }
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:464:5: ( ( CARET | CARETCARET | VEE | VEEVEE ) starExpr | ( CARET | VEE ) primaryExpr starExpr | ( CARET | VEE ) primaryExpr | starExpr | CARET | CARETCARET | VEE | VEEVEE | primaryExpr )
            int alt61=9;
            alt61 = dfa61.predict(input);
            switch (alt61) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:464:7: ( CARET | CARETCARET | VEE | VEEVEE ) starExpr
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    set216=(Token)input.LT(1);
                    set216=(Token)input.LT(1);
                    if ( (input.LA(1)>=CARET && input.LA(1)<=VEEVEE) ) {
                        input.consume();
                        if ( state.backtracking==0 ) root_0 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(set216), root_0);
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_starExpr_in_upstarExpr2906);
                    starExpr217=starExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, starExpr217.getTree());

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:465:7: ( CARET | VEE ) primaryExpr starExpr
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    set218=(Token)input.LT(1);
                    set218=(Token)input.LT(1);
                    if ( input.LA(1)==CARET||input.LA(1)==VEE ) {
                        input.consume();
                        if ( state.backtracking==0 ) root_0 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(set218), root_0);
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_primaryExpr_in_upstarExpr2923);
                    primaryExpr219=primaryExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primaryExpr219.getTree());
                    pushFollow(FOLLOW_starExpr_in_upstarExpr2925);
                    starExpr220=starExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, starExpr220.getTree());

                    }
                    break;
                case 3 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:466:7: ( CARET | VEE ) primaryExpr
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    set221=(Token)input.LT(1);
                    set221=(Token)input.LT(1);
                    if ( input.LA(1)==CARET||input.LA(1)==VEE ) {
                        input.consume();
                        if ( state.backtracking==0 ) root_0 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(set221), root_0);
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_primaryExpr_in_upstarExpr2942);
                    primaryExpr222=primaryExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primaryExpr222.getTree());

                    }
                    break;
                case 4 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:467:7: starExpr
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_starExpr_in_upstarExpr2950);
                    starExpr223=starExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, starExpr223.getTree());

                    }
                    break;
                case 5 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:468:7: CARET
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    CARET224=(Token)match(input,CARET,FOLLOW_CARET_in_upstarExpr2958); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CARET224_tree = (CgsuiteTree)adaptor.create(CARET224);
                    adaptor.addChild(root_0, CARET224_tree);
                    }

                    }
                    break;
                case 6 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:468:15: CARETCARET
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    CARETCARET225=(Token)match(input,CARETCARET,FOLLOW_CARETCARET_in_upstarExpr2962); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CARETCARET225_tree = (CgsuiteTree)adaptor.create(CARETCARET225);
                    adaptor.addChild(root_0, CARETCARET225_tree);
                    }

                    }
                    break;
                case 7 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:468:28: VEE
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    VEE226=(Token)match(input,VEE,FOLLOW_VEE_in_upstarExpr2966); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    VEE226_tree = (CgsuiteTree)adaptor.create(VEE226);
                    adaptor.addChild(root_0, VEE226_tree);
                    }

                    }
                    break;
                case 8 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:468:34: VEEVEE
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    VEEVEE227=(Token)match(input,VEEVEE,FOLLOW_VEEVEE_in_upstarExpr2970); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    VEEVEE227_tree = (CgsuiteTree)adaptor.create(VEEVEE227);
                    adaptor.addChild(root_0, VEEVEE227_tree);
                    }

                    }
                    break;
                case 9 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:469:7: primaryExpr
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_primaryExpr_in_upstarExpr2978);
                    primaryExpr228=primaryExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primaryExpr228.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 51, upstarExpr_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "upstarExpr"

    public static class starExpr_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "starExpr"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:472:1: starExpr options {backtrack=true; memoize=true; } : ( AST primaryExpr -> ^( UNARY_AST primaryExpr ) | AST -> UNARY_AST );
    public final CgsuiteParser.starExpr_return starExpr() throws RecognitionException {
        CgsuiteParser.starExpr_return retval = new CgsuiteParser.starExpr_return();
        retval.start = input.LT(1);
        int starExpr_StartIndex = input.index();
        CgsuiteTree root_0 = null;

        Token AST229=null;
        Token AST231=null;
        CgsuiteParser.primaryExpr_return primaryExpr230 = null;


        CgsuiteTree AST229_tree=null;
        CgsuiteTree AST231_tree=null;
        RewriteRuleTokenStream stream_AST=new RewriteRuleTokenStream(adaptor,"token AST");
        RewriteRuleSubtreeStream stream_primaryExpr=new RewriteRuleSubtreeStream(adaptor,"rule primaryExpr");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 52) ) { return retval; }
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:478:5: ( AST primaryExpr -> ^( UNARY_AST primaryExpr ) | AST -> UNARY_AST )
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==AST) ) {
                int LA62_1 = input.LA(2);

                if ( (synpred11_Cgsuite()) ) {
                    alt62=1;
                }
                else if ( (true) ) {
                    alt62=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 62, 0, input);

                throw nvae;
            }
            switch (alt62) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:478:7: AST primaryExpr
                    {
                    AST229=(Token)match(input,AST,FOLLOW_AST_in_starExpr3021); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_AST.add(AST229);

                    pushFollow(FOLLOW_primaryExpr_in_starExpr3023);
                    primaryExpr230=primaryExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_primaryExpr.add(primaryExpr230.getTree());


                    // AST REWRITE
                    // elements: primaryExpr
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CgsuiteTree)adaptor.nil();
                    // 478:23: -> ^( UNARY_AST primaryExpr )
                    {
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:478:26: ^( UNARY_AST primaryExpr )
                        {
                        CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                        root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(UNARY_AST, "UNARY_AST"), root_1);

                        adaptor.addChild(root_1, stream_primaryExpr.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:479:7: AST
                    {
                    AST231=(Token)match(input,AST,FOLLOW_AST_in_starExpr3039); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_AST.add(AST231);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CgsuiteTree)adaptor.nil();
                    // 479:11: -> UNARY_AST
                    {
                        adaptor.addChild(root_0, (CgsuiteTree)adaptor.create(UNARY_AST, "UNARY_AST"));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 52, starExpr_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "starExpr"

    public static class primaryExpr_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "primaryExpr"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:482:1: primaryExpr : ( NIL | THIS | TRUE | FALSE | ( INTEGER DOTDOT )=> range | INTEGER | STRING | CHAR | IDENTIFIER | LPAREN statementSequence RPAREN | BEGIN statementSequence END | ( LBRACE expressionList SLASHES )=> explicitGame | ( LBRACE ( expression )? BIGRARROW )=> explicitMap | explicitSet | explicitList );
    public final CgsuiteParser.primaryExpr_return primaryExpr() throws RecognitionException {
        CgsuiteParser.primaryExpr_return retval = new CgsuiteParser.primaryExpr_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token NIL232=null;
        Token THIS233=null;
        Token TRUE234=null;
        Token FALSE235=null;
        Token INTEGER237=null;
        Token STRING238=null;
        Token CHAR239=null;
        Token IDENTIFIER240=null;
        Token LPAREN241=null;
        Token RPAREN243=null;
        Token BEGIN244=null;
        Token END246=null;
        CgsuiteParser.range_return range236 = null;

        CgsuiteParser.statementSequence_return statementSequence242 = null;

        CgsuiteParser.statementSequence_return statementSequence245 = null;

        CgsuiteParser.explicitGame_return explicitGame247 = null;

        CgsuiteParser.explicitMap_return explicitMap248 = null;

        CgsuiteParser.explicitSet_return explicitSet249 = null;

        CgsuiteParser.explicitList_return explicitList250 = null;


        CgsuiteTree NIL232_tree=null;
        CgsuiteTree THIS233_tree=null;
        CgsuiteTree TRUE234_tree=null;
        CgsuiteTree FALSE235_tree=null;
        CgsuiteTree INTEGER237_tree=null;
        CgsuiteTree STRING238_tree=null;
        CgsuiteTree CHAR239_tree=null;
        CgsuiteTree IDENTIFIER240_tree=null;
        CgsuiteTree LPAREN241_tree=null;
        CgsuiteTree RPAREN243_tree=null;
        CgsuiteTree BEGIN244_tree=null;
        CgsuiteTree END246_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:483:2: ( NIL | THIS | TRUE | FALSE | ( INTEGER DOTDOT )=> range | INTEGER | STRING | CHAR | IDENTIFIER | LPAREN statementSequence RPAREN | BEGIN statementSequence END | ( LBRACE expressionList SLASHES )=> explicitGame | ( LBRACE ( expression )? BIGRARROW )=> explicitMap | explicitSet | explicitList )
            int alt63=15;
            alt63 = dfa63.predict(input);
            switch (alt63) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:483:4: NIL
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    NIL232=(Token)match(input,NIL,FOLLOW_NIL_in_primaryExpr3058); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NIL232_tree = (CgsuiteTree)adaptor.create(NIL232);
                    adaptor.addChild(root_0, NIL232_tree);
                    }

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:484:4: THIS
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    THIS233=(Token)match(input,THIS,FOLLOW_THIS_in_primaryExpr3063); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    THIS233_tree = (CgsuiteTree)adaptor.create(THIS233);
                    adaptor.addChild(root_0, THIS233_tree);
                    }

                    }
                    break;
                case 3 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:485:4: TRUE
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    TRUE234=(Token)match(input,TRUE,FOLLOW_TRUE_in_primaryExpr3068); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRUE234_tree = (CgsuiteTree)adaptor.create(TRUE234);
                    adaptor.addChild(root_0, TRUE234_tree);
                    }

                    }
                    break;
                case 4 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:486:4: FALSE
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    FALSE235=(Token)match(input,FALSE,FOLLOW_FALSE_in_primaryExpr3073); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FALSE235_tree = (CgsuiteTree)adaptor.create(FALSE235);
                    adaptor.addChild(root_0, FALSE235_tree);
                    }

                    }
                    break;
                case 5 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:487:4: ( INTEGER DOTDOT )=> range
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_range_in_primaryExpr3086);
                    range236=range();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, range236.getTree());

                    }
                    break;
                case 6 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:488:4: INTEGER
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    INTEGER237=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_primaryExpr3091); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INTEGER237_tree = (CgsuiteTree)adaptor.create(INTEGER237);
                    adaptor.addChild(root_0, INTEGER237_tree);
                    }

                    }
                    break;
                case 7 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:489:4: STRING
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    STRING238=(Token)match(input,STRING,FOLLOW_STRING_in_primaryExpr3096); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING238_tree = (CgsuiteTree)adaptor.create(STRING238);
                    adaptor.addChild(root_0, STRING238_tree);
                    }

                    }
                    break;
                case 8 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:490:4: CHAR
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    CHAR239=(Token)match(input,CHAR,FOLLOW_CHAR_in_primaryExpr3101); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR239_tree = (CgsuiteTree)adaptor.create(CHAR239);
                    adaptor.addChild(root_0, CHAR239_tree);
                    }

                    }
                    break;
                case 9 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:491:4: IDENTIFIER
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    IDENTIFIER240=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primaryExpr3106); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER240_tree = (CgsuiteTree)adaptor.create(IDENTIFIER240);
                    adaptor.addChild(root_0, IDENTIFIER240_tree);
                    }

                    }
                    break;
                case 10 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:492:4: LPAREN statementSequence RPAREN
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    LPAREN241=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_primaryExpr3111); if (state.failed) return retval;
                    pushFollow(FOLLOW_statementSequence_in_primaryExpr3114);
                    statementSequence242=statementSequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence242.getTree());
                    RPAREN243=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_primaryExpr3116); if (state.failed) return retval;

                    }
                    break;
                case 11 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:493:4: BEGIN statementSequence END
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    BEGIN244=(Token)match(input,BEGIN,FOLLOW_BEGIN_in_primaryExpr3122); if (state.failed) return retval;
                    pushFollow(FOLLOW_statementSequence_in_primaryExpr3125);
                    statementSequence245=statementSequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence245.getTree());
                    END246=(Token)match(input,END,FOLLOW_END_in_primaryExpr3127); if (state.failed) return retval;

                    }
                    break;
                case 12 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:494:7: ( LBRACE expressionList SLASHES )=> explicitGame
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_explicitGame_in_primaryExpr3146);
                    explicitGame247=explicitGame();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitGame247.getTree());

                    }
                    break;
                case 13 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:495:4: ( LBRACE ( expression )? BIGRARROW )=> explicitMap
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_explicitMap_in_primaryExpr3162);
                    explicitMap248=explicitMap();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitMap248.getTree());

                    }
                    break;
                case 14 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:496:4: explicitSet
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_explicitSet_in_primaryExpr3167);
                    explicitSet249=explicitSet();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitSet249.getTree());

                    }
                    break;
                case 15 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:497:4: explicitList
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_explicitList_in_primaryExpr3172);
                    explicitList250=explicitList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitList250.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "primaryExpr"

    public static class explicitGame_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "explicitGame"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:500:1: explicitGame : LBRACE slashExpression RBRACE ;
    public final CgsuiteParser.explicitGame_return explicitGame() throws RecognitionException {
        CgsuiteParser.explicitGame_return retval = new CgsuiteParser.explicitGame_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token LBRACE251=null;
        Token RBRACE253=null;
        CgsuiteParser.slashExpression_return slashExpression252 = null;


        CgsuiteTree LBRACE251_tree=null;
        CgsuiteTree RBRACE253_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:501:5: ( LBRACE slashExpression RBRACE )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:501:7: LBRACE slashExpression RBRACE
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            LBRACE251=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_explicitGame3186); if (state.failed) return retval;
            pushFollow(FOLLOW_slashExpression_in_explicitGame3189);
            slashExpression252=slashExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, slashExpression252.getTree());
            RBRACE253=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_explicitGame3191); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "explicitGame"

    public static class slashExpression_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "slashExpression"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:504:1: slashExpression : ( ( expressionList SLASHES )=>lo= expressionList ( SLASHES ro= slashExpression ) -> | lo= expressionList -> $lo);
    public final CgsuiteParser.slashExpression_return slashExpression() throws RecognitionException {
        CgsuiteParser.slashExpression_return retval = new CgsuiteParser.slashExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token SLASHES254=null;
        CgsuiteParser.expressionList_return lo = null;

        CgsuiteParser.slashExpression_return ro = null;


        CgsuiteTree SLASHES254_tree=null;
        RewriteRuleTokenStream stream_SLASHES=new RewriteRuleTokenStream(adaptor,"token SLASHES");
        RewriteRuleSubtreeStream stream_expressionList=new RewriteRuleSubtreeStream(adaptor,"rule expressionList");
        RewriteRuleSubtreeStream stream_slashExpression=new RewriteRuleSubtreeStream(adaptor,"rule slashExpression");

                CommonTree newTree = null;
            
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:509:5: ( ( expressionList SLASHES )=>lo= expressionList ( SLASHES ro= slashExpression ) -> | lo= expressionList -> $lo)
            int alt64=2;
            alt64 = dfa64.predict(input);
            switch (alt64) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:509:7: ( expressionList SLASHES )=>lo= expressionList ( SLASHES ro= slashExpression )
                    {
                    pushFollow(FOLLOW_expressionList_in_slashExpression3238);
                    lo=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expressionList.add(lo.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:510:25: ( SLASHES ro= slashExpression )
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:510:26: SLASHES ro= slashExpression
                    {
                    SLASHES254=(Token)match(input,SLASHES,FOLLOW_SLASHES_in_slashExpression3241); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SLASHES.add(SLASHES254);

                    pushFollow(FOLLOW_slashExpression_in_slashExpression3245);
                    ro=slashExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_slashExpression.add(ro.getTree());

                    }

                    if ( state.backtracking==0 ) {

                              if ((ro!=null?((CgsuiteTree)ro.tree):null).token.getType() != SLASHES ||
                                  (ro!=null?((CgsuiteTree)ro.tree):null).token.getText().length() < SLASHES254.getText().length())
                              {
                                  newTree = (CgsuiteTree) adaptor.create(SLASHES254);
                                  adaptor.addChild(newTree, (lo!=null?((CgsuiteTree)lo.tree):null));
                                  adaptor.addChild(newTree, (ro!=null?((CgsuiteTree)ro.tree):null));
                              }
                              else
                              {
                                  CommonTree t = (ro!=null?((CgsuiteTree)ro.tree):null);
                                  while (true)
                                  {
                                      if (SLASHES254.getText().length() == t.getText().length())
                                      {
                                          throw new RuntimeException("Ambiguous pattern of slashes.");
                                      }
                                      else if (t.getChild(0).getType() != SLASHES ||
                                               t.getChild(0).getText().length() < SLASHES254.getText().length())
                                      {
                                          break;
                                      }
                                      t = (CgsuiteTree) adaptor.getChild(t, 0);
                                  }
                                  CommonTree tLeft  = (CgsuiteTree) adaptor.getChild(t, 0);
                                  CommonTree tRight = (CgsuiteTree) adaptor.getChild(t, 1);
                                  CommonTree tRightNew = (CgsuiteTree) adaptor.create(SLASHES254);
                                  adaptor.addChild(tRightNew, (lo!=null?((CgsuiteTree)lo.tree):null));
                                  adaptor.addChild(tRightNew, tLeft);
                                  adaptor.setChild(t, 0, tRightNew);
                                  adaptor.setChild(t, 1, tRight);
                                  newTree = (ro!=null?((CgsuiteTree)ro.tree):null);
                              }
                          
                    }


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CgsuiteTree)adaptor.nil();
                    // 544:7: ->
                    {
                        adaptor.addChild(root_0, newTree);

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:545:7: lo= expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_slashExpression3266);
                    lo=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expressionList.add(lo.getTree());


                    // AST REWRITE
                    // elements: lo
                    // token labels: 
                    // rule labels: lo, retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_lo=new RewriteRuleSubtreeStream(adaptor,"rule lo",lo!=null?lo.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CgsuiteTree)adaptor.nil();
                    // 545:25: -> $lo
                    {
                        adaptor.addChild(root_0, stream_lo.nextTree());

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "slashExpression"

    public static class explicitMap_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "explicitMap"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:548:1: explicitMap : LBRACE ( mapEntry ( COMMA mapEntry )* | BIGRARROW ) RBRACE -> ^( EXPLICIT_MAP ( mapEntry )* ) ;
    public final CgsuiteParser.explicitMap_return explicitMap() throws RecognitionException {
        CgsuiteParser.explicitMap_return retval = new CgsuiteParser.explicitMap_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token LBRACE255=null;
        Token COMMA257=null;
        Token BIGRARROW259=null;
        Token RBRACE260=null;
        CgsuiteParser.mapEntry_return mapEntry256 = null;

        CgsuiteParser.mapEntry_return mapEntry258 = null;


        CgsuiteTree LBRACE255_tree=null;
        CgsuiteTree COMMA257_tree=null;
        CgsuiteTree BIGRARROW259_tree=null;
        CgsuiteTree RBRACE260_tree=null;
        RewriteRuleTokenStream stream_RBRACE=new RewriteRuleTokenStream(adaptor,"token RBRACE");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_BIGRARROW=new RewriteRuleTokenStream(adaptor,"token BIGRARROW");
        RewriteRuleTokenStream stream_LBRACE=new RewriteRuleTokenStream(adaptor,"token LBRACE");
        RewriteRuleSubtreeStream stream_mapEntry=new RewriteRuleSubtreeStream(adaptor,"rule mapEntry");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:549:2: ( LBRACE ( mapEntry ( COMMA mapEntry )* | BIGRARROW ) RBRACE -> ^( EXPLICIT_MAP ( mapEntry )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:549:4: LBRACE ( mapEntry ( COMMA mapEntry )* | BIGRARROW ) RBRACE
            {
            LBRACE255=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_explicitMap3285); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LBRACE.add(LBRACE255);

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:549:11: ( mapEntry ( COMMA mapEntry )* | BIGRARROW )
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( ((LA66_0>=PLUS && LA66_0<=AST)||LA66_0==LPAREN||LA66_0==LBRACKET||LA66_0==LBRACE||(LA66_0>=CARET && LA66_0<=VEEVEE)||LA66_0==BEGIN||LA66_0==BY||LA66_0==DO||(LA66_0>=FALSE && LA66_0<=FOR)||LA66_0==FROM||LA66_0==IF||(LA66_0>=NIL && LA66_0<=NOT)||(LA66_0>=THIS && LA66_0<=TRUE)||(LA66_0>=WHERE && LA66_0<=WHILE)||(LA66_0>=IDENTIFIER && LA66_0<=CHAR)) ) {
                alt66=1;
            }
            else if ( (LA66_0==BIGRARROW) ) {
                alt66=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 66, 0, input);

                throw nvae;
            }
            switch (alt66) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:549:12: mapEntry ( COMMA mapEntry )*
                    {
                    pushFollow(FOLLOW_mapEntry_in_explicitMap3288);
                    mapEntry256=mapEntry();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_mapEntry.add(mapEntry256.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:549:21: ( COMMA mapEntry )*
                    loop65:
                    do {
                        int alt65=2;
                        int LA65_0 = input.LA(1);

                        if ( (LA65_0==COMMA) ) {
                            alt65=1;
                        }


                        switch (alt65) {
                    	case 1 :
                    	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:549:22: COMMA mapEntry
                    	    {
                    	    COMMA257=(Token)match(input,COMMA,FOLLOW_COMMA_in_explicitMap3291); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA257);

                    	    pushFollow(FOLLOW_mapEntry_in_explicitMap3293);
                    	    mapEntry258=mapEntry();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_mapEntry.add(mapEntry258.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop65;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:549:41: BIGRARROW
                    {
                    BIGRARROW259=(Token)match(input,BIGRARROW,FOLLOW_BIGRARROW_in_explicitMap3299); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_BIGRARROW.add(BIGRARROW259);


                    }
                    break;

            }

            RBRACE260=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_explicitMap3302); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RBRACE.add(RBRACE260);



            // AST REWRITE
            // elements: mapEntry
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CgsuiteTree)adaptor.nil();
            // 549:59: -> ^( EXPLICIT_MAP ( mapEntry )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:549:62: ^( EXPLICIT_MAP ( mapEntry )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(EXPLICIT_MAP, "EXPLICIT_MAP"), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:549:77: ( mapEntry )*
                while ( stream_mapEntry.hasNext() ) {
                    adaptor.addChild(root_1, stream_mapEntry.nextTree());

                }
                stream_mapEntry.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "explicitMap"

    public static class mapEntry_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "mapEntry"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:552:1: mapEntry : expression BIGRARROW expression ;
    public final CgsuiteParser.mapEntry_return mapEntry() throws RecognitionException {
        CgsuiteParser.mapEntry_return retval = new CgsuiteParser.mapEntry_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token BIGRARROW262=null;
        CgsuiteParser.expression_return expression261 = null;

        CgsuiteParser.expression_return expression263 = null;


        CgsuiteTree BIGRARROW262_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:553:2: ( expression BIGRARROW expression )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:553:4: expression BIGRARROW expression
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_expression_in_mapEntry3322);
            expression261=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression261.getTree());
            BIGRARROW262=(Token)match(input,BIGRARROW,FOLLOW_BIGRARROW_in_mapEntry3324); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            BIGRARROW262_tree = (CgsuiteTree)adaptor.create(BIGRARROW262);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(BIGRARROW262_tree, root_0);
            }
            pushFollow(FOLLOW_expression_in_mapEntry3327);
            expression263=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression263.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "mapEntry"

    public static class explicitSet_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "explicitSet"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:556:1: explicitSet : LBRACE ( expression ( COMMA expression )* )? RBRACE -> ^( EXPLICIT_SET ( expression )* ) ;
    public final CgsuiteParser.explicitSet_return explicitSet() throws RecognitionException {
        CgsuiteParser.explicitSet_return retval = new CgsuiteParser.explicitSet_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token LBRACE264=null;
        Token COMMA266=null;
        Token RBRACE268=null;
        CgsuiteParser.expression_return expression265 = null;

        CgsuiteParser.expression_return expression267 = null;


        CgsuiteTree LBRACE264_tree=null;
        CgsuiteTree COMMA266_tree=null;
        CgsuiteTree RBRACE268_tree=null;
        RewriteRuleTokenStream stream_RBRACE=new RewriteRuleTokenStream(adaptor,"token RBRACE");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LBRACE=new RewriteRuleTokenStream(adaptor,"token LBRACE");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:557:2: ( LBRACE ( expression ( COMMA expression )* )? RBRACE -> ^( EXPLICIT_SET ( expression )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:557:4: LBRACE ( expression ( COMMA expression )* )? RBRACE
            {
            LBRACE264=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_explicitSet3338); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LBRACE.add(LBRACE264);

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:557:11: ( expression ( COMMA expression )* )?
            int alt68=2;
            int LA68_0 = input.LA(1);

            if ( ((LA68_0>=PLUS && LA68_0<=AST)||LA68_0==LPAREN||LA68_0==LBRACKET||LA68_0==LBRACE||(LA68_0>=CARET && LA68_0<=VEEVEE)||LA68_0==BEGIN||LA68_0==BY||LA68_0==DO||(LA68_0>=FALSE && LA68_0<=FOR)||LA68_0==FROM||LA68_0==IF||(LA68_0>=NIL && LA68_0<=NOT)||(LA68_0>=THIS && LA68_0<=TRUE)||(LA68_0>=WHERE && LA68_0<=WHILE)||(LA68_0>=IDENTIFIER && LA68_0<=CHAR)) ) {
                alt68=1;
            }
            switch (alt68) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:557:12: expression ( COMMA expression )*
                    {
                    pushFollow(FOLLOW_expression_in_explicitSet3341);
                    expression265=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression265.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:557:23: ( COMMA expression )*
                    loop67:
                    do {
                        int alt67=2;
                        int LA67_0 = input.LA(1);

                        if ( (LA67_0==COMMA) ) {
                            alt67=1;
                        }


                        switch (alt67) {
                    	case 1 :
                    	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:557:24: COMMA expression
                    	    {
                    	    COMMA266=(Token)match(input,COMMA,FOLLOW_COMMA_in_explicitSet3344); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA266);

                    	    pushFollow(FOLLOW_expression_in_explicitSet3346);
                    	    expression267=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expression.add(expression267.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop67;
                        }
                    } while (true);


                    }
                    break;

            }

            RBRACE268=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_explicitSet3352); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RBRACE.add(RBRACE268);



            // AST REWRITE
            // elements: expression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CgsuiteTree)adaptor.nil();
            // 557:52: -> ^( EXPLICIT_SET ( expression )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:557:55: ^( EXPLICIT_SET ( expression )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(EXPLICIT_SET, "EXPLICIT_SET"), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:557:70: ( expression )*
                while ( stream_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_expression.nextTree());

                }
                stream_expression.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "explicitSet"

    public static class explicitList_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "explicitList"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:560:1: explicitList : LBRACKET ( expression ( COMMA expression )* )? RBRACKET -> ^( EXPLICIT_LIST ( expression )* ) ;
    public final CgsuiteParser.explicitList_return explicitList() throws RecognitionException {
        CgsuiteParser.explicitList_return retval = new CgsuiteParser.explicitList_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token LBRACKET269=null;
        Token COMMA271=null;
        Token RBRACKET273=null;
        CgsuiteParser.expression_return expression270 = null;

        CgsuiteParser.expression_return expression272 = null;


        CgsuiteTree LBRACKET269_tree=null;
        CgsuiteTree COMMA271_tree=null;
        CgsuiteTree RBRACKET273_tree=null;
        RewriteRuleTokenStream stream_LBRACKET=new RewriteRuleTokenStream(adaptor,"token LBRACKET");
        RewriteRuleTokenStream stream_RBRACKET=new RewriteRuleTokenStream(adaptor,"token RBRACKET");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:561:2: ( LBRACKET ( expression ( COMMA expression )* )? RBRACKET -> ^( EXPLICIT_LIST ( expression )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:561:4: LBRACKET ( expression ( COMMA expression )* )? RBRACKET
            {
            LBRACKET269=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_explicitList3372); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LBRACKET.add(LBRACKET269);

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:561:13: ( expression ( COMMA expression )* )?
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( ((LA70_0>=PLUS && LA70_0<=AST)||LA70_0==LPAREN||LA70_0==LBRACKET||LA70_0==LBRACE||(LA70_0>=CARET && LA70_0<=VEEVEE)||LA70_0==BEGIN||LA70_0==BY||LA70_0==DO||(LA70_0>=FALSE && LA70_0<=FOR)||LA70_0==FROM||LA70_0==IF||(LA70_0>=NIL && LA70_0<=NOT)||(LA70_0>=THIS && LA70_0<=TRUE)||(LA70_0>=WHERE && LA70_0<=WHILE)||(LA70_0>=IDENTIFIER && LA70_0<=CHAR)) ) {
                alt70=1;
            }
            switch (alt70) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:561:14: expression ( COMMA expression )*
                    {
                    pushFollow(FOLLOW_expression_in_explicitList3375);
                    expression270=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression270.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:561:25: ( COMMA expression )*
                    loop69:
                    do {
                        int alt69=2;
                        int LA69_0 = input.LA(1);

                        if ( (LA69_0==COMMA) ) {
                            alt69=1;
                        }


                        switch (alt69) {
                    	case 1 :
                    	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:561:26: COMMA expression
                    	    {
                    	    COMMA271=(Token)match(input,COMMA,FOLLOW_COMMA_in_explicitList3378); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA271);

                    	    pushFollow(FOLLOW_expression_in_explicitList3380);
                    	    expression272=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expression.add(expression272.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop69;
                        }
                    } while (true);


                    }
                    break;

            }

            RBRACKET273=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_explicitList3386); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RBRACKET.add(RBRACKET273);



            // AST REWRITE
            // elements: expression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CgsuiteTree)adaptor.nil();
            // 561:56: -> ^( EXPLICIT_LIST ( expression )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:561:59: ^( EXPLICIT_LIST ( expression )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(EXPLICIT_LIST, "EXPLICIT_LIST"), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:561:75: ( expression )*
                while ( stream_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_expression.nextTree());

                }
                stream_expression.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "explicitList"

    public static class expressionList_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expressionList"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:564:1: expressionList : ( expression ( COMMA expression )* )? -> ^( EXPRESSION_LIST ( expression )* ) ;
    public final CgsuiteParser.expressionList_return expressionList() throws RecognitionException {
        CgsuiteParser.expressionList_return retval = new CgsuiteParser.expressionList_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token COMMA275=null;
        CgsuiteParser.expression_return expression274 = null;

        CgsuiteParser.expression_return expression276 = null;


        CgsuiteTree COMMA275_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:565:5: ( ( expression ( COMMA expression )* )? -> ^( EXPRESSION_LIST ( expression )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:565:7: ( expression ( COMMA expression )* )?
            {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:565:7: ( expression ( COMMA expression )* )?
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( ((LA72_0>=PLUS && LA72_0<=AST)||LA72_0==LPAREN||LA72_0==LBRACKET||LA72_0==LBRACE||(LA72_0>=CARET && LA72_0<=VEEVEE)||LA72_0==BEGIN||LA72_0==BY||LA72_0==DO||(LA72_0>=FALSE && LA72_0<=FOR)||LA72_0==FROM||LA72_0==IF||(LA72_0>=NIL && LA72_0<=NOT)||(LA72_0>=THIS && LA72_0<=TRUE)||(LA72_0>=WHERE && LA72_0<=WHILE)||(LA72_0>=IDENTIFIER && LA72_0<=CHAR)) ) {
                alt72=1;
            }
            switch (alt72) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:565:8: expression ( COMMA expression )*
                    {
                    pushFollow(FOLLOW_expression_in_expressionList3410);
                    expression274=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression274.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:565:19: ( COMMA expression )*
                    loop71:
                    do {
                        int alt71=2;
                        int LA71_0 = input.LA(1);

                        if ( (LA71_0==COMMA) ) {
                            alt71=1;
                        }


                        switch (alt71) {
                    	case 1 :
                    	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:565:20: COMMA expression
                    	    {
                    	    COMMA275=(Token)match(input,COMMA,FOLLOW_COMMA_in_expressionList3413); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA275);

                    	    pushFollow(FOLLOW_expression_in_expressionList3415);
                    	    expression276=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expression.add(expression276.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop71;
                        }
                    } while (true);


                    }
                    break;

            }



            // AST REWRITE
            // elements: expression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CgsuiteTree)adaptor.nil();
            // 565:41: -> ^( EXPRESSION_LIST ( expression )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:565:44: ^( EXPRESSION_LIST ( expression )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(EXPRESSION_LIST, "EXPRESSION_LIST"), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:565:62: ( expression )*
                while ( stream_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_expression.nextTree());

                }
                stream_expression.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "expressionList"

    public static class range_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "range"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:568:1: range : INTEGER DOTDOT INTEGER ;
    public final CgsuiteParser.range_return range() throws RecognitionException {
        CgsuiteParser.range_return retval = new CgsuiteParser.range_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token INTEGER277=null;
        Token DOTDOT278=null;
        Token INTEGER279=null;

        CgsuiteTree INTEGER277_tree=null;
        CgsuiteTree DOTDOT278_tree=null;
        CgsuiteTree INTEGER279_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:569:2: ( INTEGER DOTDOT INTEGER )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:569:4: INTEGER DOTDOT INTEGER
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            INTEGER277=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_range3442); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            INTEGER277_tree = (CgsuiteTree)adaptor.create(INTEGER277);
            adaptor.addChild(root_0, INTEGER277_tree);
            }
            DOTDOT278=(Token)match(input,DOTDOT,FOLLOW_DOTDOT_in_range3444); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            DOTDOT278_tree = (CgsuiteTree)adaptor.create(DOTDOT278);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(DOTDOT278_tree, root_0);
            }
            INTEGER279=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_range3447); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            INTEGER279_tree = (CgsuiteTree)adaptor.create(INTEGER279);
            adaptor.addChild(root_0, INTEGER279_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CgsuiteTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CgsuiteTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "range"

    // $ANTLR start synpred1_Cgsuite
    public final void synpred1_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:425:7: ( PLUSMINUS LPAREN expression ( COMMA expression )* RPAREN )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:425:7: PLUSMINUS LPAREN expression ( COMMA expression )* RPAREN
        {
        match(input,PLUSMINUS,FOLLOW_PLUSMINUS_in_synpred1_Cgsuite2561); if (state.failed) return ;
        match(input,LPAREN,FOLLOW_LPAREN_in_synpred1_Cgsuite2563); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred1_Cgsuite2565);
        expression();

        state._fsp--;
        if (state.failed) return ;
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:425:35: ( COMMA expression )*
        loop73:
        do {
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==COMMA) ) {
                alt73=1;
            }


            switch (alt73) {
        	case 1 :
        	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:425:36: COMMA expression
        	    {
        	    match(input,COMMA,FOLLOW_COMMA_in_synpred1_Cgsuite2568); if (state.failed) return ;
        	    pushFollow(FOLLOW_expression_in_synpred1_Cgsuite2570);
        	    expression();

        	    state._fsp--;
        	    if (state.failed) return ;

        	    }
        	    break;

        	default :
        	    break loop73;
            }
        } while (true);

        match(input,RPAREN,FOLLOW_RPAREN_in_synpred1_Cgsuite2574); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_Cgsuite

    // $ANTLR start synpred2_Cgsuite
    public final void synpred2_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:426:7: ( PLUSMINUS unaryExpr )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:426:7: PLUSMINUS unaryExpr
        {
        match(input,PLUSMINUS,FOLLOW_PLUSMINUS_in_synpred2_Cgsuite2591); if (state.failed) return ;
        pushFollow(FOLLOW_unaryExpr_in_synpred2_Cgsuite2593);
        unaryExpr();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_Cgsuite

    // $ANTLR start synpred3_Cgsuite
    public final void synpred3_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:464:7: ( ( CARET | CARETCARET | VEE | VEEVEE ) starExpr )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:464:7: ( CARET | CARETCARET | VEE | VEEVEE ) starExpr
        {
        if ( (input.LA(1)>=CARET && input.LA(1)<=VEEVEE) ) {
            input.consume();
            state.errorRecovery=false;state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }

        pushFollow(FOLLOW_starExpr_in_synpred3_Cgsuite2906);
        starExpr();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_Cgsuite

    // $ANTLR start synpred4_Cgsuite
    public final void synpred4_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:465:7: ( ( CARET | VEE ) primaryExpr starExpr )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:465:7: ( CARET | VEE ) primaryExpr starExpr
        {
        if ( input.LA(1)==CARET||input.LA(1)==VEE ) {
            input.consume();
            state.errorRecovery=false;state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }

        pushFollow(FOLLOW_primaryExpr_in_synpred4_Cgsuite2923);
        primaryExpr();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_starExpr_in_synpred4_Cgsuite2925);
        starExpr();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_Cgsuite

    // $ANTLR start synpred5_Cgsuite
    public final void synpred5_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:466:7: ( ( CARET | VEE ) primaryExpr )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:466:7: ( CARET | VEE ) primaryExpr
        {
        if ( input.LA(1)==CARET||input.LA(1)==VEE ) {
            input.consume();
            state.errorRecovery=false;state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }

        pushFollow(FOLLOW_primaryExpr_in_synpred5_Cgsuite2942);
        primaryExpr();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_Cgsuite

    // $ANTLR start synpred7_Cgsuite
    public final void synpred7_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:468:7: ( CARET )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:468:7: CARET
        {
        match(input,CARET,FOLLOW_CARET_in_synpred7_Cgsuite2958); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_Cgsuite

    // $ANTLR start synpred8_Cgsuite
    public final void synpred8_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:468:15: ( CARETCARET )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:468:15: CARETCARET
        {
        match(input,CARETCARET,FOLLOW_CARETCARET_in_synpred8_Cgsuite2962); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_Cgsuite

    // $ANTLR start synpred9_Cgsuite
    public final void synpred9_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:468:28: ( VEE )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:468:28: VEE
        {
        match(input,VEE,FOLLOW_VEE_in_synpred9_Cgsuite2966); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred9_Cgsuite

    // $ANTLR start synpred10_Cgsuite
    public final void synpred10_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:468:34: ( VEEVEE )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:468:34: VEEVEE
        {
        match(input,VEEVEE,FOLLOW_VEEVEE_in_synpred10_Cgsuite2970); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred10_Cgsuite

    // $ANTLR start synpred11_Cgsuite
    public final void synpred11_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:478:7: ( AST primaryExpr )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:478:7: AST primaryExpr
        {
        match(input,AST,FOLLOW_AST_in_synpred11_Cgsuite3021); if (state.failed) return ;
        pushFollow(FOLLOW_primaryExpr_in_synpred11_Cgsuite3023);
        primaryExpr();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred11_Cgsuite

    // $ANTLR start synpred12_Cgsuite
    public final void synpred12_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:487:4: ( INTEGER DOTDOT )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:487:5: INTEGER DOTDOT
        {
        match(input,INTEGER,FOLLOW_INTEGER_in_synpred12_Cgsuite3079); if (state.failed) return ;
        match(input,DOTDOT,FOLLOW_DOTDOT_in_synpred12_Cgsuite3081); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred12_Cgsuite

    // $ANTLR start synpred13_Cgsuite
    public final void synpred13_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:494:7: ( LBRACE expressionList SLASHES )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:494:8: LBRACE expressionList SLASHES
        {
        match(input,LBRACE,FOLLOW_LBRACE_in_synpred13_Cgsuite3137); if (state.failed) return ;
        pushFollow(FOLLOW_expressionList_in_synpred13_Cgsuite3139);
        expressionList();

        state._fsp--;
        if (state.failed) return ;
        match(input,SLASHES,FOLLOW_SLASHES_in_synpred13_Cgsuite3141); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred13_Cgsuite

    // $ANTLR start synpred14_Cgsuite
    public final void synpred14_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:495:4: ( LBRACE ( expression )? BIGRARROW )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:495:5: LBRACE ( expression )? BIGRARROW
        {
        match(input,LBRACE,FOLLOW_LBRACE_in_synpred14_Cgsuite3152); if (state.failed) return ;
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:495:12: ( expression )?
        int alt74=2;
        int LA74_0 = input.LA(1);

        if ( ((LA74_0>=PLUS && LA74_0<=AST)||LA74_0==LPAREN||LA74_0==LBRACKET||LA74_0==LBRACE||(LA74_0>=CARET && LA74_0<=VEEVEE)||LA74_0==BEGIN||LA74_0==BY||LA74_0==DO||(LA74_0>=FALSE && LA74_0<=FOR)||LA74_0==FROM||LA74_0==IF||(LA74_0>=NIL && LA74_0<=NOT)||(LA74_0>=THIS && LA74_0<=TRUE)||(LA74_0>=WHERE && LA74_0<=WHILE)||(LA74_0>=IDENTIFIER && LA74_0<=CHAR)) ) {
            alt74=1;
        }
        switch (alt74) {
            case 1 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:495:12: expression
                {
                pushFollow(FOLLOW_expression_in_synpred14_Cgsuite3154);
                expression();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }

        match(input,BIGRARROW,FOLLOW_BIGRARROW_in_synpred14_Cgsuite3157); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred14_Cgsuite

    // $ANTLR start synpred15_Cgsuite
    public final void synpred15_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:509:7: ( expressionList SLASHES )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:509:8: expressionList SLASHES
        {
        pushFollow(FOLLOW_expressionList_in_synpred15_Cgsuite3223);
        expressionList();

        state._fsp--;
        if (state.failed) return ;
        match(input,SLASHES,FOLLOW_SLASHES_in_synpred15_Cgsuite3225); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred15_Cgsuite

    // Delegated rules

    public final boolean synpred11_Cgsuite() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred11_Cgsuite_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred15_Cgsuite() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred15_Cgsuite_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred4_Cgsuite() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_Cgsuite_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred7_Cgsuite() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_Cgsuite_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred10_Cgsuite() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred10_Cgsuite_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_Cgsuite() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_Cgsuite_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_Cgsuite() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_Cgsuite_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred13_Cgsuite() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred13_Cgsuite_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred14_Cgsuite() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred14_Cgsuite_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_Cgsuite() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_Cgsuite_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_Cgsuite() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_Cgsuite_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_Cgsuite() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_Cgsuite_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred9_Cgsuite() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred9_Cgsuite_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred12_Cgsuite() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred12_Cgsuite_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA6 dfa6 = new DFA6(this);
    protected DFA54 dfa54 = new DFA54(this);
    protected DFA61 dfa61 = new DFA61(this);
    protected DFA63 dfa63 = new DFA63(this);
    protected DFA64 dfa64 = new DFA64(this);
    static final String DFA6_eotS =
        "\11\uffff";
    static final String DFA6_eofS =
        "\11\uffff";
    static final String DFA6_minS =
        "\6\114\3\uffff";
    static final String DFA6_maxS =
        "\6\141\3\uffff";
    static final String DFA6_acceptS =
        "\6\uffff\1\1\1\2\1\3";
    static final String DFA6_specialS =
        "\11\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\4\2\uffff\1\10\6\uffff\1\1\1\7\1\2\1\3\2\uffff\1\5\4\uffff"+
            "\1\6",
            "\1\4\2\uffff\1\10\6\uffff\1\1\1\7\1\2\1\3\2\uffff\1\5\4\uffff"+
            "\1\6",
            "\1\4\2\uffff\1\10\6\uffff\1\1\1\7\1\2\1\3\2\uffff\1\5\4\uffff"+
            "\1\6",
            "\1\4\2\uffff\1\10\6\uffff\1\1\1\7\1\2\1\3\2\uffff\1\5\4\uffff"+
            "\1\6",
            "\1\4\2\uffff\1\10\6\uffff\1\1\1\7\1\2\1\3\2\uffff\1\5\4\uffff"+
            "\1\6",
            "\1\4\2\uffff\1\10\6\uffff\1\1\1\7\1\2\1\3\2\uffff\1\5\4\uffff"+
            "\1\6",
            "",
            "",
            ""
    };

    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min = DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max = DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
    }

    class DFA6 extends DFA {

        public DFA6(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 6;
            this.eot = DFA6_eot;
            this.eof = DFA6_eof;
            this.min = DFA6_min;
            this.max = DFA6_max;
            this.accept = DFA6_accept;
            this.special = DFA6_special;
            this.transition = DFA6_transition;
        }
        public String getDescription() {
            return "218:1: declaration : ( varDeclaration | propertyDeclaration | methodDeclaration );";
        }
    }
    static final String DFA54_eotS =
        "\27\uffff";
    static final String DFA54_eofS =
        "\27\uffff";
    static final String DFA54_minS =
        "\1\4\1\0\25\uffff";
    static final String DFA54_maxS =
        "\1\173\1\0\25\uffff";
    static final String DFA54_acceptS =
        "\2\uffff\1\3\22\uffff\1\1\1\2";
    static final String DFA54_specialS =
        "\1\uffff\1\0\25\uffff}>";
    static final String[] DFA54_transitionS = {
            "\2\2\1\1\1\2\5\uffff\1\2\1\uffff\1\2\1\uffff\1\2\12\uffff\4"+
            "\2\32\uffff\1\2\13\uffff\1\2\12\uffff\1\2\14\uffff\1\2\1\uffff"+
            "\1\2\27\uffff\4\2",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA54_eot = DFA.unpackEncodedString(DFA54_eotS);
    static final short[] DFA54_eof = DFA.unpackEncodedString(DFA54_eofS);
    static final char[] DFA54_min = DFA.unpackEncodedStringToUnsignedChars(DFA54_minS);
    static final char[] DFA54_max = DFA.unpackEncodedStringToUnsignedChars(DFA54_maxS);
    static final short[] DFA54_accept = DFA.unpackEncodedString(DFA54_acceptS);
    static final short[] DFA54_special = DFA.unpackEncodedString(DFA54_specialS);
    static final short[][] DFA54_transition;

    static {
        int numStates = DFA54_transitionS.length;
        DFA54_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA54_transition[i] = DFA.unpackEncodedString(DFA54_transitionS[i]);
        }
    }

    class DFA54 extends DFA {

        public DFA54(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 54;
            this.eot = DFA54_eot;
            this.eof = DFA54_eof;
            this.min = DFA54_min;
            this.max = DFA54_max;
            this.accept = DFA54_accept;
            this.special = DFA54_special;
            this.transition = DFA54_transition;
        }
        public String getDescription() {
            return "419:1: plusminusExpr options {backtrack=true; memoize=true; } : ( PLUSMINUS LPAREN expression ( COMMA expression )* RPAREN -> ^( PLUSMINUS ( expression )* ) | PLUSMINUS unaryExpr -> ^( PLUSMINUS unaryExpr ) | unaryExpr );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA54_1 = input.LA(1);

                         
                        int index54_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_Cgsuite()) ) {s = 21;}

                        else if ( (synpred2_Cgsuite()) ) {s = 22;}

                         
                        input.seek(index54_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 54, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA61_eotS =
        "\31\uffff";
    static final String DFA61_eofS =
        "\31\uffff";
    static final String DFA61_minS =
        "\1\7\2\0\1\uffff\2\0\23\uffff";
    static final String DFA61_maxS =
        "\1\173\2\0\1\uffff\2\0\23\uffff";
    static final String DFA61_acceptS =
        "\3\uffff\1\4\2\uffff\1\11\13\uffff\1\1\1\2\1\3\1\5\1\6\1\7\1\10";
    static final String DFA61_specialS =
        "\1\uffff\1\0\1\1\1\uffff\1\2\1\3\23\uffff}>";
    static final String[] DFA61_transitionS = {
            "\1\3\5\uffff\1\6\1\uffff\1\6\1\uffff\1\6\12\uffff\1\1\1\2\1"+
            "\4\1\5\32\uffff\1\6\13\uffff\1\6\12\uffff\1\6\14\uffff\1\6\1"+
            "\uffff\1\6\27\uffff\4\6",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA61_eot = DFA.unpackEncodedString(DFA61_eotS);
    static final short[] DFA61_eof = DFA.unpackEncodedString(DFA61_eofS);
    static final char[] DFA61_min = DFA.unpackEncodedStringToUnsignedChars(DFA61_minS);
    static final char[] DFA61_max = DFA.unpackEncodedStringToUnsignedChars(DFA61_maxS);
    static final short[] DFA61_accept = DFA.unpackEncodedString(DFA61_acceptS);
    static final short[] DFA61_special = DFA.unpackEncodedString(DFA61_specialS);
    static final short[][] DFA61_transition;

    static {
        int numStates = DFA61_transitionS.length;
        DFA61_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA61_transition[i] = DFA.unpackEncodedString(DFA61_transitionS[i]);
        }
    }

    class DFA61 extends DFA {

        public DFA61(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 61;
            this.eot = DFA61_eot;
            this.eof = DFA61_eof;
            this.min = DFA61_min;
            this.max = DFA61_max;
            this.accept = DFA61_accept;
            this.special = DFA61_special;
            this.transition = DFA61_transition;
        }
        public String getDescription() {
            return "458:1: upstarExpr options {backtrack=true; memoize=true; } : ( ( CARET | CARETCARET | VEE | VEEVEE ) starExpr | ( CARET | VEE ) primaryExpr starExpr | ( CARET | VEE ) primaryExpr | starExpr | CARET | CARETCARET | VEE | VEEVEE | primaryExpr );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA61_1 = input.LA(1);

                         
                        int index61_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_Cgsuite()) ) {s = 18;}

                        else if ( (synpred4_Cgsuite()) ) {s = 19;}

                        else if ( (synpred5_Cgsuite()) ) {s = 20;}

                        else if ( (synpred7_Cgsuite()) ) {s = 21;}

                         
                        input.seek(index61_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA61_2 = input.LA(1);

                         
                        int index61_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_Cgsuite()) ) {s = 18;}

                        else if ( (synpred8_Cgsuite()) ) {s = 22;}

                         
                        input.seek(index61_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA61_4 = input.LA(1);

                         
                        int index61_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_Cgsuite()) ) {s = 18;}

                        else if ( (synpred4_Cgsuite()) ) {s = 19;}

                        else if ( (synpred5_Cgsuite()) ) {s = 20;}

                        else if ( (synpred9_Cgsuite()) ) {s = 23;}

                         
                        input.seek(index61_4);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA61_5 = input.LA(1);

                         
                        int index61_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_Cgsuite()) ) {s = 18;}

                        else if ( (synpred10_Cgsuite()) ) {s = 24;}

                         
                        input.seek(index61_5);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 61, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA63_eotS =
        "\22\uffff";
    static final String DFA63_eofS =
        "\22\uffff";
    static final String DFA63_minS =
        "\1\15\4\uffff\1\0\5\uffff\1\0\6\uffff";
    static final String DFA63_maxS =
        "\1\173\4\uffff\1\0\5\uffff\1\0\6\uffff";
    static final String DFA63_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\uffff\1\7\1\10\1\11\1\12\1\13\1\uffff"+
        "\1\17\1\5\1\6\1\14\1\15\1\16";
    static final String DFA63_specialS =
        "\5\uffff\1\0\5\uffff\1\1\6\uffff}>";
    static final String[] DFA63_transitionS = {
            "\1\11\1\uffff\1\14\1\uffff\1\13\50\uffff\1\12\13\uffff\1\4"+
            "\12\uffff\1\1\14\uffff\1\2\1\uffff\1\3\27\uffff\1\10\1\6\1\5"+
            "\1\7",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA63_eot = DFA.unpackEncodedString(DFA63_eotS);
    static final short[] DFA63_eof = DFA.unpackEncodedString(DFA63_eofS);
    static final char[] DFA63_min = DFA.unpackEncodedStringToUnsignedChars(DFA63_minS);
    static final char[] DFA63_max = DFA.unpackEncodedStringToUnsignedChars(DFA63_maxS);
    static final short[] DFA63_accept = DFA.unpackEncodedString(DFA63_acceptS);
    static final short[] DFA63_special = DFA.unpackEncodedString(DFA63_specialS);
    static final short[][] DFA63_transition;

    static {
        int numStates = DFA63_transitionS.length;
        DFA63_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA63_transition[i] = DFA.unpackEncodedString(DFA63_transitionS[i]);
        }
    }

    class DFA63 extends DFA {

        public DFA63(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 63;
            this.eot = DFA63_eot;
            this.eof = DFA63_eof;
            this.min = DFA63_min;
            this.max = DFA63_max;
            this.accept = DFA63_accept;
            this.special = DFA63_special;
            this.transition = DFA63_transition;
        }
        public String getDescription() {
            return "482:1: primaryExpr : ( NIL | THIS | TRUE | FALSE | ( INTEGER DOTDOT )=> range | INTEGER | STRING | CHAR | IDENTIFIER | LPAREN statementSequence RPAREN | BEGIN statementSequence END | ( LBRACE expressionList SLASHES )=> explicitGame | ( LBRACE ( expression )? BIGRARROW )=> explicitMap | explicitSet | explicitList );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA63_5 = input.LA(1);

                         
                        int index63_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12_Cgsuite()) ) {s = 13;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index63_5);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA63_11 = input.LA(1);

                         
                        int index63_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred13_Cgsuite()) ) {s = 15;}

                        else if ( (synpred14_Cgsuite()) ) {s = 16;}

                        else if ( (true) ) {s = 17;}

                         
                        input.seek(index63_11);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 63, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA64_eotS =
        "\40\uffff";
    static final String DFA64_eofS =
        "\40\uffff";
    static final String DFA64_minS =
        "\1\4\35\0\2\uffff";
    static final String DFA64_maxS =
        "\1\174\35\0\2\uffff";
    static final String DFA64_acceptS =
        "\36\uffff\1\1\1\2";
    static final String DFA64_specialS =
        "\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15"+
        "\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1\32"+
        "\1\33\1\34\1\35\2\uffff}>";
    static final String[] DFA64_transitionS = {
            "\1\16\1\15\1\14\1\21\5\uffff\1\2\1\uffff\1\35\1\uffff\1\34"+
            "\1\37\11\uffff\1\17\1\20\1\22\1\23\32\uffff\1\33\1\uffff\1\7"+
            "\3\uffff\1\12\5\uffff\1\27\1\4\1\uffff\1\5\1\uffff\1\3\5\uffff"+
            "\1\24\1\13\13\uffff\1\25\1\6\1\26\1\uffff\1\11\1\10\24\uffff"+
            "\1\1\1\31\1\30\1\32\1\36",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA64_eot = DFA.unpackEncodedString(DFA64_eotS);
    static final short[] DFA64_eof = DFA.unpackEncodedString(DFA64_eofS);
    static final char[] DFA64_min = DFA.unpackEncodedStringToUnsignedChars(DFA64_minS);
    static final char[] DFA64_max = DFA.unpackEncodedStringToUnsignedChars(DFA64_maxS);
    static final short[] DFA64_accept = DFA.unpackEncodedString(DFA64_acceptS);
    static final short[] DFA64_special = DFA.unpackEncodedString(DFA64_specialS);
    static final short[][] DFA64_transition;

    static {
        int numStates = DFA64_transitionS.length;
        DFA64_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA64_transition[i] = DFA.unpackEncodedString(DFA64_transitionS[i]);
        }
    }

    class DFA64 extends DFA {

        public DFA64(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 64;
            this.eot = DFA64_eot;
            this.eof = DFA64_eof;
            this.min = DFA64_min;
            this.max = DFA64_max;
            this.accept = DFA64_accept;
            this.special = DFA64_special;
            this.transition = DFA64_transition;
        }
        public String getDescription() {
            return "504:1: slashExpression : ( ( expressionList SLASHES )=>lo= expressionList ( SLASHES ro= slashExpression ) -> | lo= expressionList -> $lo);";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA64_0 = input.LA(1);

                         
                        int index64_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA64_0==IDENTIFIER) ) {s = 1;}

                        else if ( (LA64_0==LPAREN) ) {s = 2;}

                        else if ( (LA64_0==IF) ) {s = 3;}

                        else if ( (LA64_0==FOR) ) {s = 4;}

                        else if ( (LA64_0==FROM) ) {s = 5;}

                        else if ( (LA64_0==TO) ) {s = 6;}

                        else if ( (LA64_0==BY) ) {s = 7;}

                        else if ( (LA64_0==WHILE) ) {s = 8;}

                        else if ( (LA64_0==WHERE) ) {s = 9;}

                        else if ( (LA64_0==DO) ) {s = 10;}

                        else if ( (LA64_0==NOT) ) {s = 11;}

                        else if ( (LA64_0==PLUSMINUS) ) {s = 12;}

                        else if ( (LA64_0==MINUS) ) {s = 13;}

                        else if ( (LA64_0==PLUS) ) {s = 14;}

                        else if ( (LA64_0==CARET) ) {s = 15;}

                        else if ( (LA64_0==CARETCARET) ) {s = 16;}

                        else if ( (LA64_0==AST) ) {s = 17;}

                        else if ( (LA64_0==VEE) ) {s = 18;}

                        else if ( (LA64_0==VEEVEE) ) {s = 19;}

                        else if ( (LA64_0==NIL) ) {s = 20;}

                        else if ( (LA64_0==THIS) ) {s = 21;}

                        else if ( (LA64_0==TRUE) ) {s = 22;}

                        else if ( (LA64_0==FALSE) ) {s = 23;}

                        else if ( (LA64_0==INTEGER) ) {s = 24;}

                        else if ( (LA64_0==STRING) ) {s = 25;}

                        else if ( (LA64_0==CHAR) ) {s = 26;}

                        else if ( (LA64_0==BEGIN) ) {s = 27;}

                        else if ( (LA64_0==LBRACE) ) {s = 28;}

                        else if ( (LA64_0==LBRACKET) ) {s = 29;}

                        else if ( (LA64_0==SLASHES) && (synpred15_Cgsuite())) {s = 30;}

                        else if ( (LA64_0==RBRACE) ) {s = 31;}

                         
                        input.seek(index64_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA64_1 = input.LA(1);

                         
                        int index64_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_1);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA64_2 = input.LA(1);

                         
                        int index64_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_2);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA64_3 = input.LA(1);

                         
                        int index64_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_3);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA64_4 = input.LA(1);

                         
                        int index64_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_4);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA64_5 = input.LA(1);

                         
                        int index64_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_5);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA64_6 = input.LA(1);

                         
                        int index64_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_6);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA64_7 = input.LA(1);

                         
                        int index64_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_7);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA64_8 = input.LA(1);

                         
                        int index64_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_8);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA64_9 = input.LA(1);

                         
                        int index64_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_9);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA64_10 = input.LA(1);

                         
                        int index64_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_10);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA64_11 = input.LA(1);

                         
                        int index64_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_11);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA64_12 = input.LA(1);

                         
                        int index64_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_12);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA64_13 = input.LA(1);

                         
                        int index64_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_13);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA64_14 = input.LA(1);

                         
                        int index64_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_14);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA64_15 = input.LA(1);

                         
                        int index64_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_15);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA64_16 = input.LA(1);

                         
                        int index64_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_16);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA64_17 = input.LA(1);

                         
                        int index64_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_17);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA64_18 = input.LA(1);

                         
                        int index64_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_18);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA64_19 = input.LA(1);

                         
                        int index64_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_19);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA64_20 = input.LA(1);

                         
                        int index64_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_20);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA64_21 = input.LA(1);

                         
                        int index64_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_21);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA64_22 = input.LA(1);

                         
                        int index64_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_22);
                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA64_23 = input.LA(1);

                         
                        int index64_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_23);
                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA64_24 = input.LA(1);

                         
                        int index64_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_24);
                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA64_25 = input.LA(1);

                         
                        int index64_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_25);
                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA64_26 = input.LA(1);

                         
                        int index64_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_26);
                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA64_27 = input.LA(1);

                         
                        int index64_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_27);
                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA64_28 = input.LA(1);

                         
                        int index64_28 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_28);
                        if ( s>=0 ) return s;
                        break;
                    case 29 : 
                        int LA64_29 = input.LA(1);

                         
                        int index64_29 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index64_29);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 64, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_classDeclaration_in_compilationUnit1173 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_enumDeclaration_in_compilationUnit1177 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_compilationUnit1180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CLASS_in_classDeclaration1192 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_classDeclaration1195 = new BitSet(new long[]{0x0000000000800000L,0x0000000213C09028L});
    public static final BitSet FOLLOW_extendsClause_in_classDeclaration1197 = new BitSet(new long[]{0x0000000000800000L,0x0000000213C09008L});
    public static final BitSet FOLLOW_javaClause_in_classDeclaration1200 = new BitSet(new long[]{0x0000000000000000L,0x0000000213C09008L});
    public static final BitSet FOLLOW_declaration_in_classDeclaration1203 = new BitSet(new long[]{0x0000000000000000L,0x0000000213C09008L});
    public static final BitSet FOLLOW_END_in_classDeclaration1206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXTENDS_in_extendsClause1219 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_extendsClause1222 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_COMMA_in_extendsClause1225 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_extendsClause1228 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_COLON_in_javaClause1244 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_JAVA_in_javaClause1247 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L});
    public static final BitSet FOLLOW_STRING_in_javaClause1250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_varDeclaration_in_declaration1265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_propertyDeclaration_in_declaration1270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclaration_in_declaration1275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_varDeclaration1290 = new BitSet(new long[]{0x0000000000000000L,0x0000000200000000L});
    public static final BitSet FOLLOW_VAR_in_varDeclaration1292 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_varDeclaration1295 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_SEMI_in_varDeclaration1297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_propertyDeclaration1312 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_PROPERTY_in_propertyDeclaration1314 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_propertyDeclaration1317 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_DOT_in_propertyDeclaration1319 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000400L});
    public static final BitSet FOLLOW_set_in_propertyDeclaration1322 = new BitSet(new long[]{0xDC000000F0C2A0F0L,0x0F00000DC4060AC9L});
    public static final BitSet FOLLOW_javaClause_in_propertyDeclaration1334 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_SEMI_in_propertyDeclaration1336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statementSequence_in_propertyDeclaration1341 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_END_in_propertyDeclaration1343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_proptype0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_methodDeclaration1382 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_METHOD_in_methodDeclaration1384 = new BitSet(new long[]{0x0000000000000000L,0x0100000000080000L});
    public static final BitSet FOLLOW_methodName_in_methodDeclaration1387 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_LPAREN_in_methodDeclaration1389 = new BitSet(new long[]{0x0000000000004000L,0x0100000000000000L});
    public static final BitSet FOLLOW_methodParameterList_in_methodDeclaration1392 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_RPAREN_in_methodDeclaration1394 = new BitSet(new long[]{0xDC000000F0C2A0F0L,0x0F00000DC4060AC9L});
    public static final BitSet FOLLOW_javaClause_in_methodDeclaration1401 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_SEMI_in_methodDeclaration1403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statementSequence_in_methodDeclaration1408 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_END_in_methodDeclaration1410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PRIVATE_in_modifiers1425 = new BitSet(new long[]{0x0000000000000002L,0x0000000013401000L});
    public static final BitSet FOLLOW_PROTECTED_in_modifiers1429 = new BitSet(new long[]{0x0000000000000002L,0x0000000013401000L});
    public static final BitSet FOLLOW_PUBLIC_in_modifiers1433 = new BitSet(new long[]{0x0000000000000002L,0x0000000013401000L});
    public static final BitSet FOLLOW_IMMUTABLE_in_modifiers1437 = new BitSet(new long[]{0x0000000000000002L,0x0000000013401000L});
    public static final BitSet FOLLOW_STATIC_in_modifiers1441 = new BitSet(new long[]{0x0000000000000002L,0x0000000013401000L});
    public static final BitSet FOLLOW_OP_in_methodName1475 = new BitSet(new long[]{0x007FC0FF00008DB0L,0x0000000000210000L});
    public static final BitSet FOLLOW_opCode_in_methodName1478 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodName1486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_opCode1503 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_opCode1507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AST_in_opCode1511 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FSLASH_in_opCode1515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PERCENT_in_opCode1519 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXP_in_opCode1523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEG_in_opCode1527 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_POS_in_opCode1531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_standardRelationalToken_in_opCode1539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opAssignmentToken_in_opCode1547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_opCode1555 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RBRACKET_in_opCode1557 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_opCode1559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodParameter_in_methodParameterList1577 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_COMMA_in_methodParameterList1580 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_methodParameter_in_methodParameterList1582 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodParameter1609 = new BitSet(new long[]{0x0000000008000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodParameter1613 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_QUESTION_in_methodParameter1616 = new BitSet(new long[]{0xDC000000F002A0F2L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_methodParameter1618 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodParameter1644 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodParameter1648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodParameter1663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_enumDeclaration1677 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_ENUM_in_enumDeclaration1679 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_enumDeclaration1682 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_enumElementList_in_enumDeclaration1684 = new BitSet(new long[]{0x0000000000000000L,0x0000000213C09008L});
    public static final BitSet FOLLOW_declaration_in_enumDeclaration1686 = new BitSet(new long[]{0x0000000000000000L,0x0000000213C09008L});
    public static final BitSet FOLLOW_END_in_enumDeclaration1689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumElement_in_enumElementList1708 = new BitSet(new long[]{0x0000000000600000L});
    public static final BitSet FOLLOW_COMMA_in_enumElementList1711 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_enumElement_in_enumElementList1713 = new BitSet(new long[]{0x0000000000600000L});
    public static final BitSet FOLLOW_SEMI_in_enumElementList1718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_enumElement1744 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_LPAREN_in_enumElement1747 = new BitSet(new long[]{0xDC000000F002E0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_enumElement1750 = new BitSet(new long[]{0x0000000000204000L});
    public static final BitSet FOLLOW_COMMA_in_enumElement1753 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_enumElement1755 = new BitSet(new long[]{0x0000000000204000L});
    public static final BitSet FOLLOW_RPAREN_in_enumElement1761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_script1792 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_script1794 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statementSequence_in_block1812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statement_in_statementSequence1827 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_SEMI_in_statementSequence1831 = new BitSet(new long[]{0xDC000000F042A0F2L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_statement_in_statementSequence1833 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_BREAK_in_statement1857 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONTINUE_in_statement1862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURN_in_statement1867 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_statement1870 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CLEAR_in_statement1878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_statement1883 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentExpression_in_expression1894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionExpression_in_assignmentExpression1905 = new BitSet(new long[]{0x007FE00000000002L});
    public static final BitSet FOLLOW_assignmentToken_in_assignmentExpression1908 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_assignmentExpression_in_assignmentExpression1911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_assignmentToken1925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opAssignmentToken_in_assignmentToken1930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_opAssignmentToken0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_procedureParameterList_in_functionExpression1996 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_RARROW_in_functionExpression1998 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_functionExpression_in_functionExpression2001 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_controlExpression_in_functionExpression2009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_procedureParameterList2023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_procedureParameterList2039 = new BitSet(new long[]{0x0000000000004000L,0x0100000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_procedureParameterList2042 = new BitSet(new long[]{0x0000000000204000L});
    public static final BitSet FOLLOW_COMMA_in_procedureParameterList2045 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_procedureParameterList2047 = new BitSet(new long[]{0x0000000000204000L});
    public static final BitSet FOLLOW_RPAREN_in_procedureParameterList2053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_controlExpression2077 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_controlExpression2080 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_THEN_in_controlExpression2082 = new BitSet(new long[]{0xDC000000F042A0F0L,0x0F00000DC4060ACFL});
    public static final BitSet FOLLOW_statementSequence_in_controlExpression2085 = new BitSet(new long[]{0x0000000000000000L,0x000000000000000EL});
    public static final BitSet FOLLOW_elseifClause_in_controlExpression2087 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_END_in_controlExpression2090 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forExpression_in_controlExpression2096 = new BitSet(new long[]{0x1000000000000000L,0x0000000C80000201L});
    public static final BitSet FOLLOW_fromExpression_in_controlExpression2099 = new BitSet(new long[]{0x1000000000000000L,0x0000000C80000001L});
    public static final BitSet FOLLOW_toExpression_in_controlExpression2102 = new BitSet(new long[]{0x1000000000000000L,0x0000000C00000001L});
    public static final BitSet FOLLOW_byExpression_in_controlExpression2105 = new BitSet(new long[]{0x0000000000000000L,0x0000000C00000001L});
    public static final BitSet FOLLOW_whileExpression_in_controlExpression2108 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000001L});
    public static final BitSet FOLLOW_whereExpression_in_controlExpression2111 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_DO_in_controlExpression2114 = new BitSet(new long[]{0xDC000000F042A0F0L,0x0F00000DC4060AC9L});
    public static final BitSet FOLLOW_statementSequence_in_controlExpression2117 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_END_in_controlExpression2119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_controlExpression2125 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_controlExpression2128 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_IN_in_controlExpression2130 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_controlExpression2133 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_DO_in_controlExpression2135 = new BitSet(new long[]{0xDC000000F042A0F0L,0x0F00000DC4060AC9L});
    public static final BitSet FOLLOW_statementSequence_in_controlExpression2138 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_END_in_controlExpression2140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_orExpression_in_controlExpression2146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_forExpression2157 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_forExpression2160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_fromExpression2172 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_fromExpression2175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TO_in_toExpression2190 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_toExpression2193 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BY_in_byExpression2210 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_byExpression2213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_whileExpression2231 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_whileExpression2234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_in_whereExpression2248 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_whereExpression2251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSEIF_in_elseifClause2265 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_elseifClause2268 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_THEN_in_elseifClause2270 = new BitSet(new long[]{0xDC000000F042A0F0L,0x0F00000DC4060AC7L});
    public static final BitSet FOLLOW_statementSequence_in_elseifClause2273 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000006L});
    public static final BitSet FOLLOW_elseifClause_in_elseifClause2275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_elseifClause2281 = new BitSet(new long[]{0xDC000000F042A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_statementSequence_in_elseifClause2284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_andExpression_in_orExpression2295 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_OR_in_orExpression2298 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_orExpression_in_orExpression2301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_notExpr_in_andExpression2314 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_AND_in_andExpression2317 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_andExpression_in_andExpression2320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notExpr2336 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_notExpr_in_notExpr2339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalExpr_in_notExpr2347 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_addExpr_in_relationalExpr2361 = new BitSet(new long[]{0x000018FF00000002L});
    public static final BitSet FOLLOW_relationalToken_in_relationalExpr2364 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_relationalExpr_in_relationalExpr2367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REFEQUALS_in_relationalToken2380 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REFNEQ_in_relationalToken2385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_standardRelationalToken_in_relationalToken2390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_standardRelationalToken0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplyExpr_in_addExpr2454 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_PLUS_in_addExpr2458 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_MINUS_in_addExpr2463 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_multiplyExpr_in_addExpr2467 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_expExpr_in_multiplyExpr2481 = new BitSet(new long[]{0x0000000000000982L});
    public static final BitSet FOLLOW_AST_in_multiplyExpr2485 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_FSLASH_in_multiplyExpr2490 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_PERCENT_in_multiplyExpr2495 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expExpr_in_multiplyExpr2499 = new BitSet(new long[]{0x0000000000000982L});
    public static final BitSet FOLLOW_plusminusExpr_in_expExpr2512 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_EXP_in_expExpr2515 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_plusminusExpr_in_expExpr2518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSMINUS_in_plusminusExpr2561 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_LPAREN_in_plusminusExpr2563 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_plusminusExpr2565 = new BitSet(new long[]{0x0000000000204000L});
    public static final BitSet FOLLOW_COMMA_in_plusminusExpr2568 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_plusminusExpr2570 = new BitSet(new long[]{0x0000000000204000L});
    public static final BitSet FOLLOW_RPAREN_in_plusminusExpr2574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSMINUS_in_plusminusExpr2591 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_unaryExpr_in_plusminusExpr2593 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpr_in_plusminusExpr2609 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unaryExpr2623 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_unaryExpr_in_unaryExpr2625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_unaryExpr2641 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_unaryExpr_in_unaryExpr2643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_postfixExpr_in_unaryExpr2659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_upstarExpr_in_postfixExpr2672 = new BitSet(new long[]{0x000000000000A202L});
    public static final BitSet FOLLOW_DOT_in_postfixExpr2684 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_postfixExpr2686 = new BitSet(new long[]{0x000000000000A202L});
    public static final BitSet FOLLOW_arrayReference_in_postfixExpr2707 = new BitSet(new long[]{0x000000000000A202L});
    public static final BitSet FOLLOW_functionCall_in_postfixExpr2727 = new BitSet(new long[]{0x000000000000A202L});
    public static final BitSet FOLLOW_LBRACKET_in_arrayReference2758 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_arrayReference2760 = new BitSet(new long[]{0x0000000000210000L});
    public static final BitSet FOLLOW_COMMA_in_arrayReference2763 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_arrayReference2765 = new BitSet(new long[]{0x0000000000210000L});
    public static final BitSet FOLLOW_RBRACKET_in_arrayReference2769 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_functionCall2797 = new BitSet(new long[]{0xDC000000F002E0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_functionArgument_in_functionCall2800 = new BitSet(new long[]{0x0000000000204000L});
    public static final BitSet FOLLOW_COMMA_in_functionCall2803 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_functionArgument_in_functionCall2805 = new BitSet(new long[]{0x0000000000204000L});
    public static final BitSet FOLLOW_RPAREN_in_functionCall2811 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_functionArgument2841 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_BIGRARROW_in_functionArgument2843 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_functionArgument2848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_upstarExpr2889 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_starExpr_in_upstarExpr2906 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_upstarExpr2914 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_primaryExpr_in_upstarExpr2923 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_starExpr_in_upstarExpr2925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_upstarExpr2933 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_primaryExpr_in_upstarExpr2942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_starExpr_in_upstarExpr2950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARET_in_upstarExpr2958 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARETCARET_in_upstarExpr2962 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VEE_in_upstarExpr2966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VEEVEE_in_upstarExpr2970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primaryExpr_in_upstarExpr2978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AST_in_starExpr3021 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_primaryExpr_in_starExpr3023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AST_in_starExpr3039 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NIL_in_primaryExpr3058 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THIS_in_primaryExpr3063 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_primaryExpr3068 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_primaryExpr3073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_in_primaryExpr3086 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_primaryExpr3091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_primaryExpr3096 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHAR_in_primaryExpr3101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_primaryExpr3106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_primaryExpr3111 = new BitSet(new long[]{0xDC000000F042E0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_statementSequence_in_primaryExpr3114 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_RPAREN_in_primaryExpr3116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BEGIN_in_primaryExpr3122 = new BitSet(new long[]{0xDC000000F042A0F0L,0x0F00000DC4060AC9L});
    public static final BitSet FOLLOW_statementSequence_in_primaryExpr3125 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_END_in_primaryExpr3127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_explicitGame_in_primaryExpr3146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_explicitMap_in_primaryExpr3162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_explicitSet_in_primaryExpr3167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_explicitList_in_primaryExpr3172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_explicitGame3186 = new BitSet(new long[]{0xDC000000F002A0F0L,0x1F00000DC4060AC1L});
    public static final BitSet FOLLOW_slashExpression_in_explicitGame3189 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_RBRACE_in_explicitGame3191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_slashExpression3238 = new BitSet(new long[]{0x0000000000000000L,0x1000000000000000L});
    public static final BitSet FOLLOW_SLASHES_in_slashExpression3241 = new BitSet(new long[]{0xDC000000F002A0F0L,0x1F00000DC4060AC1L});
    public static final BitSet FOLLOW_slashExpression_in_slashExpression3245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_slashExpression3266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_explicitMap3285 = new BitSet(new long[]{0xDC000200F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_mapEntry_in_explicitMap3288 = new BitSet(new long[]{0x0000000000240000L});
    public static final BitSet FOLLOW_COMMA_in_explicitMap3291 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_mapEntry_in_explicitMap3293 = new BitSet(new long[]{0x0000000000240000L});
    public static final BitSet FOLLOW_BIGRARROW_in_explicitMap3299 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_RBRACE_in_explicitMap3302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_mapEntry3322 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_BIGRARROW_in_mapEntry3324 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_mapEntry3327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_explicitSet3338 = new BitSet(new long[]{0xDC000000F006A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_explicitSet3341 = new BitSet(new long[]{0x0000000000240000L});
    public static final BitSet FOLLOW_COMMA_in_explicitSet3344 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_explicitSet3346 = new BitSet(new long[]{0x0000000000240000L});
    public static final BitSet FOLLOW_RBRACE_in_explicitSet3352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_explicitList3372 = new BitSet(new long[]{0xDC000000F003A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_explicitList3375 = new BitSet(new long[]{0x0000000000210000L});
    public static final BitSet FOLLOW_COMMA_in_explicitList3378 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_explicitList3380 = new BitSet(new long[]{0x0000000000210000L});
    public static final BitSet FOLLOW_RBRACKET_in_explicitList3386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList3410 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_COMMA_in_expressionList3413 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_expressionList3415 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_INTEGER_in_range3442 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_DOTDOT_in_range3444 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_INTEGER_in_range3447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSMINUS_in_synpred1_Cgsuite2561 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_LPAREN_in_synpred1_Cgsuite2563 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_synpred1_Cgsuite2565 = new BitSet(new long[]{0x0000000000204000L});
    public static final BitSet FOLLOW_COMMA_in_synpred1_Cgsuite2568 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_synpred1_Cgsuite2570 = new BitSet(new long[]{0x0000000000204000L});
    public static final BitSet FOLLOW_RPAREN_in_synpred1_Cgsuite2574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSMINUS_in_synpred2_Cgsuite2591 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_unaryExpr_in_synpred2_Cgsuite2593 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred3_Cgsuite2889 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_starExpr_in_synpred3_Cgsuite2906 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred4_Cgsuite2914 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_primaryExpr_in_synpred4_Cgsuite2923 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_starExpr_in_synpred4_Cgsuite2925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred5_Cgsuite2933 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_primaryExpr_in_synpred5_Cgsuite2942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARET_in_synpred7_Cgsuite2958 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARETCARET_in_synpred8_Cgsuite2962 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VEE_in_synpred9_Cgsuite2966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VEEVEE_in_synpred10_Cgsuite2970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AST_in_synpred11_Cgsuite3021 = new BitSet(new long[]{0xDC000000F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_primaryExpr_in_synpred11_Cgsuite3023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_synpred12_Cgsuite3079 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_DOTDOT_in_synpred12_Cgsuite3081 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_synpred13_Cgsuite3137 = new BitSet(new long[]{0xDC000000F002A0F0L,0x1F00000DC4060AC1L});
    public static final BitSet FOLLOW_expressionList_in_synpred13_Cgsuite3139 = new BitSet(new long[]{0x0000000000000000L,0x1000000000000000L});
    public static final BitSet FOLLOW_SLASHES_in_synpred13_Cgsuite3141 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_synpred14_Cgsuite3152 = new BitSet(new long[]{0xDC000200F002A0F0L,0x0F00000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_synpred14_Cgsuite3154 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_BIGRARROW_in_synpred14_Cgsuite3157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_synpred15_Cgsuite3223 = new BitSet(new long[]{0x0000000000000000L,0x1000000000000000L});
    public static final BitSet FOLLOW_SLASHES_in_synpred15_Cgsuite3225 = new BitSet(new long[]{0x0000000000000002L});

}