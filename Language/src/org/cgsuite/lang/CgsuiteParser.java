// $ANTLR 3.3 Nov 30, 2010 12:45:30 C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g 2011-02-15 18:42:03

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PLUS", "MINUS", "PLUSMINUS", "AST", "FSLASH", "DOT", "EXP", "PERCENT", "UNDERSCORE", "LPAREN", "RPAREN", "LBRACKET", "RBRACKET", "LBRACE", "RBRACE", "SQUOTE", "DQUOTE", "COMMA", "SEMI", "COLON", "AMPERSAND", "TILDE", "BANG", "QUESTION", "CARET", "CARETCARET", "VEE", "VEEVEE", "EQUALS", "NEQ", "LT", "GT", "LEQ", "GEQ", "CONFUSED", "COMPARE", "RARROW", "BIGRARROW", "BACKSLASH", "REFEQUALS", "REFNEQ", "ASSIGN", "ASN_PLUS", "ASN_MINUS", "ASN_TIMES", "ASN_DIV", "ASN_MOD", "ASN_AND", "ASN_OR", "ASN_XOR", "ASN_EXP", "DOTDOT", "DOTDOTDOT", "AND", "BEGIN", "BREAK", "BY", "CLASS", "CLEAR", "CONTINUE", "DO", "ELSE", "ELSEIF", "END", "ENUM", "EXTENDS", "FALSE", "FOR", "FOREACH", "FROM", "GET", "IF", "IMMUTABLE", "IN", "JAVA", "METHOD", "NEG", "NIL", "NOT", "OP", "OR", "POS", "PRIVATE", "PROPERTY", "PROTECTED", "PUBLIC", "RETURN", "SET", "STATIC", "THEN", "THIS", "TO", "TRUE", "VAR", "WHERE", "WHILE", "ARRAY_REFERENCE", "ARRAY_INDEX_LIST", "ASN_ANTECEDENT", "EXPLICIT_LIST", "EXPLICIT_MAP", "EXPLICIT_SET", "EXPRESSION_LIST", "FUNCTION_CALL", "FUNCTION_CALL_ARGUMENT_LIST", "METHOD_PARAMETER_LIST", "MODIFIERS", "MULTI_CARET", "MULTI_VEE", "PROCEDURE_PARAMETER_LIST", "STATEMENT_SEQUENCE", "UNARY_AST", "UNARY_MINUS", "UNARY_PLUS", "IDENTIFIER", "STRING", "INTEGER", "CHAR", "SLASHES", "DIGIT", "LETTER", "ESCAPE_SEQ", "SLASH", "HEX_DIGIT", "UC_LETTER", "LC_LETTER", "NEWLINE", "WHITESPACE", "SL_COMMENT", "ML_COMMENT"
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
    public static final int EXPLICIT_LIST=103;
    public static final int EXPLICIT_MAP=104;
    public static final int EXPLICIT_SET=105;
    public static final int EXPRESSION_LIST=106;
    public static final int FUNCTION_CALL=107;
    public static final int FUNCTION_CALL_ARGUMENT_LIST=108;
    public static final int METHOD_PARAMETER_LIST=109;
    public static final int MODIFIERS=110;
    public static final int MULTI_CARET=111;
    public static final int MULTI_VEE=112;
    public static final int PROCEDURE_PARAMETER_LIST=113;
    public static final int STATEMENT_SEQUENCE=114;
    public static final int UNARY_AST=115;
    public static final int UNARY_MINUS=116;
    public static final int UNARY_PLUS=117;
    public static final int IDENTIFIER=118;
    public static final int STRING=119;
    public static final int INTEGER=120;
    public static final int CHAR=121;
    public static final int SLASHES=122;
    public static final int DIGIT=123;
    public static final int LETTER=124;
    public static final int ESCAPE_SEQ=125;
    public static final int SLASH=126;
    public static final int HEX_DIGIT=127;
    public static final int UC_LETTER=128;
    public static final int LC_LETTER=129;
    public static final int NEWLINE=130;
    public static final int WHITESPACE=131;
    public static final int SL_COMMENT=132;
    public static final int ML_COMMENT=133;

    // delegates
    // delegators


        public CgsuiteParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public CgsuiteParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
            this.state.ruleMemo = new HashMap[74+1];
             
             
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:200:1: compilationUnit : ( classDeclaration | enumDeclaration ) EOF ;
    public final CgsuiteParser.compilationUnit_return compilationUnit() throws RecognitionException {
        CgsuiteParser.compilationUnit_return retval = new CgsuiteParser.compilationUnit_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token EOF3=null;
        CgsuiteParser.classDeclaration_return classDeclaration1 = null;

        CgsuiteParser.enumDeclaration_return enumDeclaration2 = null;


        CgsuiteTree EOF3_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:201:2: ( ( classDeclaration | enumDeclaration ) EOF )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:201:4: ( classDeclaration | enumDeclaration ) EOF
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:201:4: ( classDeclaration | enumDeclaration )
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
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:201:5: classDeclaration
                    {
                    pushFollow(FOLLOW_classDeclaration_in_compilationUnit1159);
                    classDeclaration1=classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classDeclaration1.getTree());

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:201:24: enumDeclaration
                    {
                    pushFollow(FOLLOW_enumDeclaration_in_compilationUnit1163);
                    enumDeclaration2=enumDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumDeclaration2.getTree());

                    }
                    break;

            }

            EOF3=(Token)match(input,EOF,FOLLOW_EOF_in_compilationUnit1166); if (state.failed) return retval;
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:204:1: classDeclaration : CLASS IDENTIFIER ( extendsClause )? ( javaClause )? ( declaration )* END ;
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
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:205:2: ( CLASS IDENTIFIER ( extendsClause )? ( javaClause )? ( declaration )* END )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:205:4: CLASS IDENTIFIER ( extendsClause )? ( javaClause )? ( declaration )* END
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            CLASS4=(Token)match(input,CLASS,FOLLOW_CLASS_in_classDeclaration1178); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CLASS4_tree = (CgsuiteTree)adaptor.create(CLASS4);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(CLASS4_tree, root_0);
            }
            IDENTIFIER5=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classDeclaration1181); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER5_tree = (CgsuiteTree)adaptor.create(IDENTIFIER5);
            adaptor.addChild(root_0, IDENTIFIER5_tree);
            }
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:205:22: ( extendsClause )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==EXTENDS) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:205:22: extendsClause
                    {
                    pushFollow(FOLLOW_extendsClause_in_classDeclaration1183);
                    extendsClause6=extendsClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, extendsClause6.getTree());

                    }
                    break;

            }

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:205:37: ( javaClause )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==COLON) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:205:37: javaClause
                    {
                    pushFollow(FOLLOW_javaClause_in_classDeclaration1186);
                    javaClause7=javaClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, javaClause7.getTree());

                    }
                    break;

            }

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:205:49: ( declaration )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==ENUM||LA4_0==IMMUTABLE||LA4_0==METHOD||(LA4_0>=PRIVATE && LA4_0<=PUBLIC)||LA4_0==STATIC||LA4_0==VAR) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:205:49: declaration
            	    {
            	    pushFollow(FOLLOW_declaration_in_classDeclaration1189);
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

            END9=(Token)match(input,END,FOLLOW_END_in_classDeclaration1192); if (state.failed) return retval;

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:208:1: extendsClause : EXTENDS IDENTIFIER ( COMMA IDENTIFIER )* ;
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
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:209:2: ( EXTENDS IDENTIFIER ( COMMA IDENTIFIER )* )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:209:4: EXTENDS IDENTIFIER ( COMMA IDENTIFIER )*
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            EXTENDS10=(Token)match(input,EXTENDS,FOLLOW_EXTENDS_in_extendsClause1205); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EXTENDS10_tree = (CgsuiteTree)adaptor.create(EXTENDS10);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(EXTENDS10_tree, root_0);
            }
            IDENTIFIER11=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_extendsClause1208); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER11_tree = (CgsuiteTree)adaptor.create(IDENTIFIER11);
            adaptor.addChild(root_0, IDENTIFIER11_tree);
            }
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:209:24: ( COMMA IDENTIFIER )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==COMMA) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:209:25: COMMA IDENTIFIER
            	    {
            	    COMMA12=(Token)match(input,COMMA,FOLLOW_COMMA_in_extendsClause1211); if (state.failed) return retval;
            	    IDENTIFIER13=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_extendsClause1214); if (state.failed) return retval;
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:212:1: javaClause : COLON JAVA STRING ;
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
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:213:5: ( COLON JAVA STRING )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:213:7: COLON JAVA STRING
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            COLON14=(Token)match(input,COLON,FOLLOW_COLON_in_javaClause1230); if (state.failed) return retval;
            JAVA15=(Token)match(input,JAVA,FOLLOW_JAVA_in_javaClause1233); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            JAVA15_tree = (CgsuiteTree)adaptor.create(JAVA15);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(JAVA15_tree, root_0);
            }
            STRING16=(Token)match(input,STRING,FOLLOW_STRING_in_javaClause1236); if (state.failed) return retval;
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:216:1: declaration : ( varDeclaration | propertyDeclaration | methodDeclaration | enumDeclaration );
    public final CgsuiteParser.declaration_return declaration() throws RecognitionException {
        CgsuiteParser.declaration_return retval = new CgsuiteParser.declaration_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        CgsuiteParser.varDeclaration_return varDeclaration17 = null;

        CgsuiteParser.propertyDeclaration_return propertyDeclaration18 = null;

        CgsuiteParser.methodDeclaration_return methodDeclaration19 = null;

        CgsuiteParser.enumDeclaration_return enumDeclaration20 = null;



        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:217:2: ( varDeclaration | propertyDeclaration | methodDeclaration | enumDeclaration )
            int alt6=4;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:217:4: varDeclaration
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_varDeclaration_in_declaration1251);
                    varDeclaration17=varDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, varDeclaration17.getTree());

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:218:4: propertyDeclaration
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_propertyDeclaration_in_declaration1256);
                    propertyDeclaration18=propertyDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, propertyDeclaration18.getTree());

                    }
                    break;
                case 3 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:219:4: methodDeclaration
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_methodDeclaration_in_declaration1261);
                    methodDeclaration19=methodDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, methodDeclaration19.getTree());

                    }
                    break;
                case 4 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:220:7: enumDeclaration
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_enumDeclaration_in_declaration1269);
                    enumDeclaration20=enumDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumDeclaration20.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:223:1: varDeclaration : modifiers VAR IDENTIFIER SEMI ;
    public final CgsuiteParser.varDeclaration_return varDeclaration() throws RecognitionException {
        CgsuiteParser.varDeclaration_return retval = new CgsuiteParser.varDeclaration_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token VAR22=null;
        Token IDENTIFIER23=null;
        Token SEMI24=null;
        CgsuiteParser.modifiers_return modifiers21 = null;


        CgsuiteTree VAR22_tree=null;
        CgsuiteTree IDENTIFIER23_tree=null;
        CgsuiteTree SEMI24_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:224:5: ( modifiers VAR IDENTIFIER SEMI )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:224:7: modifiers VAR IDENTIFIER SEMI
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_varDeclaration1284);
            modifiers21=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers21.getTree());
            VAR22=(Token)match(input,VAR,FOLLOW_VAR_in_varDeclaration1286); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            VAR22_tree = (CgsuiteTree)adaptor.create(VAR22);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(VAR22_tree, root_0);
            }
            IDENTIFIER23=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_varDeclaration1289); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER23_tree = (CgsuiteTree)adaptor.create(IDENTIFIER23);
            adaptor.addChild(root_0, IDENTIFIER23_tree);
            }
            SEMI24=(Token)match(input,SEMI,FOLLOW_SEMI_in_varDeclaration1291); if (state.failed) return retval;

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:227:1: propertyDeclaration : modifiers PROPERTY IDENTIFIER DOT ( GET | SET ) ( javaClause SEMI | statementSequence END ) ;
    public final CgsuiteParser.propertyDeclaration_return propertyDeclaration() throws RecognitionException {
        CgsuiteParser.propertyDeclaration_return retval = new CgsuiteParser.propertyDeclaration_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token PROPERTY26=null;
        Token IDENTIFIER27=null;
        Token DOT28=null;
        Token set29=null;
        Token SEMI31=null;
        Token END33=null;
        CgsuiteParser.modifiers_return modifiers25 = null;

        CgsuiteParser.javaClause_return javaClause30 = null;

        CgsuiteParser.statementSequence_return statementSequence32 = null;


        CgsuiteTree PROPERTY26_tree=null;
        CgsuiteTree IDENTIFIER27_tree=null;
        CgsuiteTree DOT28_tree=null;
        CgsuiteTree set29_tree=null;
        CgsuiteTree SEMI31_tree=null;
        CgsuiteTree END33_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:228:2: ( modifiers PROPERTY IDENTIFIER DOT ( GET | SET ) ( javaClause SEMI | statementSequence END ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:228:4: modifiers PROPERTY IDENTIFIER DOT ( GET | SET ) ( javaClause SEMI | statementSequence END )
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_propertyDeclaration1306);
            modifiers25=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers25.getTree());
            PROPERTY26=(Token)match(input,PROPERTY,FOLLOW_PROPERTY_in_propertyDeclaration1308); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            PROPERTY26_tree = (CgsuiteTree)adaptor.create(PROPERTY26);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(PROPERTY26_tree, root_0);
            }
            IDENTIFIER27=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_propertyDeclaration1311); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER27_tree = (CgsuiteTree)adaptor.create(IDENTIFIER27);
            adaptor.addChild(root_0, IDENTIFIER27_tree);
            }
            DOT28=(Token)match(input,DOT,FOLLOW_DOT_in_propertyDeclaration1313); if (state.failed) return retval;
            set29=(Token)input.LT(1);
            if ( input.LA(1)==GET||input.LA(1)==SET ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (CgsuiteTree)adaptor.create(set29));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:229:4: ( javaClause SEMI | statementSequence END )
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
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:229:5: javaClause SEMI
                    {
                    pushFollow(FOLLOW_javaClause_in_propertyDeclaration1328);
                    javaClause30=javaClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, javaClause30.getTree());
                    SEMI31=(Token)match(input,SEMI,FOLLOW_SEMI_in_propertyDeclaration1330); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:229:24: statementSequence END
                    {
                    pushFollow(FOLLOW_statementSequence_in_propertyDeclaration1335);
                    statementSequence32=statementSequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence32.getTree());
                    END33=(Token)match(input,END,FOLLOW_END_in_propertyDeclaration1337); if (state.failed) return retval;

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:232:1: proptype : ( GET | SET );
    public final CgsuiteParser.proptype_return proptype() throws RecognitionException {
        CgsuiteParser.proptype_return retval = new CgsuiteParser.proptype_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token set34=null;

        CgsuiteTree set34_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:233:5: ( GET | SET )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            set34=(Token)input.LT(1);
            if ( input.LA(1)==GET||input.LA(1)==SET ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (CgsuiteTree)adaptor.create(set34));
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:237:1: methodDeclaration : modifiers METHOD methodName LPAREN methodParameterList RPAREN ( javaClause SEMI | statementSequence END ) ;
    public final CgsuiteParser.methodDeclaration_return methodDeclaration() throws RecognitionException {
        CgsuiteParser.methodDeclaration_return retval = new CgsuiteParser.methodDeclaration_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token METHOD36=null;
        Token LPAREN38=null;
        Token RPAREN40=null;
        Token SEMI42=null;
        Token END44=null;
        CgsuiteParser.modifiers_return modifiers35 = null;

        CgsuiteParser.methodName_return methodName37 = null;

        CgsuiteParser.methodParameterList_return methodParameterList39 = null;

        CgsuiteParser.javaClause_return javaClause41 = null;

        CgsuiteParser.statementSequence_return statementSequence43 = null;


        CgsuiteTree METHOD36_tree=null;
        CgsuiteTree LPAREN38_tree=null;
        CgsuiteTree RPAREN40_tree=null;
        CgsuiteTree SEMI42_tree=null;
        CgsuiteTree END44_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:238:2: ( modifiers METHOD methodName LPAREN methodParameterList RPAREN ( javaClause SEMI | statementSequence END ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:238:4: modifiers METHOD methodName LPAREN methodParameterList RPAREN ( javaClause SEMI | statementSequence END )
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_methodDeclaration1376);
            modifiers35=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers35.getTree());
            METHOD36=(Token)match(input,METHOD,FOLLOW_METHOD_in_methodDeclaration1378); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            METHOD36_tree = (CgsuiteTree)adaptor.create(METHOD36);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(METHOD36_tree, root_0);
            }
            pushFollow(FOLLOW_methodName_in_methodDeclaration1381);
            methodName37=methodName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, methodName37.getTree());
            LPAREN38=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_methodDeclaration1383); if (state.failed) return retval;
            pushFollow(FOLLOW_methodParameterList_in_methodDeclaration1386);
            methodParameterList39=methodParameterList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, methodParameterList39.getTree());
            RPAREN40=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_methodDeclaration1388); if (state.failed) return retval;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:239:4: ( javaClause SEMI | statementSequence END )
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
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:239:5: javaClause SEMI
                    {
                    pushFollow(FOLLOW_javaClause_in_methodDeclaration1395);
                    javaClause41=javaClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, javaClause41.getTree());
                    SEMI42=(Token)match(input,SEMI,FOLLOW_SEMI_in_methodDeclaration1397); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:239:24: statementSequence END
                    {
                    pushFollow(FOLLOW_statementSequence_in_methodDeclaration1402);
                    statementSequence43=statementSequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence43.getTree());
                    END44=(Token)match(input,END,FOLLOW_END_in_methodDeclaration1404); if (state.failed) return retval;

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:242:1: modifiers : ( PRIVATE | PROTECTED | PUBLIC | IMMUTABLE | STATIC )* -> ^( MODIFIERS ( PRIVATE )* ( PROTECTED )* ( PUBLIC )* ( IMMUTABLE )* ) ;
    public final CgsuiteParser.modifiers_return modifiers() throws RecognitionException {
        CgsuiteParser.modifiers_return retval = new CgsuiteParser.modifiers_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token PRIVATE45=null;
        Token PROTECTED46=null;
        Token PUBLIC47=null;
        Token IMMUTABLE48=null;
        Token STATIC49=null;

        CgsuiteTree PRIVATE45_tree=null;
        CgsuiteTree PROTECTED46_tree=null;
        CgsuiteTree PUBLIC47_tree=null;
        CgsuiteTree IMMUTABLE48_tree=null;
        CgsuiteTree STATIC49_tree=null;
        RewriteRuleTokenStream stream_PROTECTED=new RewriteRuleTokenStream(adaptor,"token PROTECTED");
        RewriteRuleTokenStream stream_IMMUTABLE=new RewriteRuleTokenStream(adaptor,"token IMMUTABLE");
        RewriteRuleTokenStream stream_PRIVATE=new RewriteRuleTokenStream(adaptor,"token PRIVATE");
        RewriteRuleTokenStream stream_PUBLIC=new RewriteRuleTokenStream(adaptor,"token PUBLIC");
        RewriteRuleTokenStream stream_STATIC=new RewriteRuleTokenStream(adaptor,"token STATIC");

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:243:2: ( ( PRIVATE | PROTECTED | PUBLIC | IMMUTABLE | STATIC )* -> ^( MODIFIERS ( PRIVATE )* ( PROTECTED )* ( PUBLIC )* ( IMMUTABLE )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:243:4: ( PRIVATE | PROTECTED | PUBLIC | IMMUTABLE | STATIC )*
            {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:243:4: ( PRIVATE | PROTECTED | PUBLIC | IMMUTABLE | STATIC )*
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
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:243:5: PRIVATE
            	    {
            	    PRIVATE45=(Token)match(input,PRIVATE,FOLLOW_PRIVATE_in_modifiers1419); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_PRIVATE.add(PRIVATE45);


            	    }
            	    break;
            	case 2 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:243:15: PROTECTED
            	    {
            	    PROTECTED46=(Token)match(input,PROTECTED,FOLLOW_PROTECTED_in_modifiers1423); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_PROTECTED.add(PROTECTED46);


            	    }
            	    break;
            	case 3 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:243:27: PUBLIC
            	    {
            	    PUBLIC47=(Token)match(input,PUBLIC,FOLLOW_PUBLIC_in_modifiers1427); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_PUBLIC.add(PUBLIC47);


            	    }
            	    break;
            	case 4 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:243:36: IMMUTABLE
            	    {
            	    IMMUTABLE48=(Token)match(input,IMMUTABLE,FOLLOW_IMMUTABLE_in_modifiers1431); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_IMMUTABLE.add(IMMUTABLE48);


            	    }
            	    break;
            	case 5 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:243:48: STATIC
            	    {
            	    STATIC49=(Token)match(input,STATIC,FOLLOW_STATIC_in_modifiers1435); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_STATIC.add(STATIC49);


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);



            // AST REWRITE
            // elements: PROTECTED, PRIVATE, PUBLIC, IMMUTABLE
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CgsuiteTree)adaptor.nil();
            // 243:57: -> ^( MODIFIERS ( PRIVATE )* ( PROTECTED )* ( PUBLIC )* ( IMMUTABLE )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:243:60: ^( MODIFIERS ( PRIVATE )* ( PROTECTED )* ( PUBLIC )* ( IMMUTABLE )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(MODIFIERS, "MODIFIERS"), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:243:72: ( PRIVATE )*
                while ( stream_PRIVATE.hasNext() ) {
                    adaptor.addChild(root_1, stream_PRIVATE.nextNode());

                }
                stream_PRIVATE.reset();
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:243:81: ( PROTECTED )*
                while ( stream_PROTECTED.hasNext() ) {
                    adaptor.addChild(root_1, stream_PROTECTED.nextNode());

                }
                stream_PROTECTED.reset();
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:243:92: ( PUBLIC )*
                while ( stream_PUBLIC.hasNext() ) {
                    adaptor.addChild(root_1, stream_PUBLIC.nextNode());

                }
                stream_PUBLIC.reset();
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:243:100: ( IMMUTABLE )*
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:246:1: methodName : ( OP opCode | IDENTIFIER );
    public final CgsuiteParser.methodName_return methodName() throws RecognitionException {
        CgsuiteParser.methodName_return retval = new CgsuiteParser.methodName_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token OP50=null;
        Token IDENTIFIER52=null;
        CgsuiteParser.opCode_return opCode51 = null;


        CgsuiteTree OP50_tree=null;
        CgsuiteTree IDENTIFIER52_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:247:5: ( OP opCode | IDENTIFIER )
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
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:247:7: OP opCode
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    OP50=(Token)match(input,OP,FOLLOW_OP_in_methodName1469); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OP50_tree = (CgsuiteTree)adaptor.create(OP50);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(OP50_tree, root_0);
                    }
                    pushFollow(FOLLOW_opCode_in_methodName1472);
                    opCode51=opCode();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, opCode51.getTree());

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:248:7: IDENTIFIER
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    IDENTIFIER52=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodName1480); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER52_tree = (CgsuiteTree)adaptor.create(IDENTIFIER52);
                    adaptor.addChild(root_0, IDENTIFIER52_tree);
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:251:1: opCode : ( PLUS | MINUS | AST | FSLASH | PERCENT | EXP | NEG | POS | standardRelationalToken | opAssignmentToken | LBRACKET RBRACKET ( ASSIGN )? );
    public final CgsuiteParser.opCode_return opCode() throws RecognitionException {
        CgsuiteParser.opCode_return retval = new CgsuiteParser.opCode_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token PLUS53=null;
        Token MINUS54=null;
        Token AST55=null;
        Token FSLASH56=null;
        Token PERCENT57=null;
        Token EXP58=null;
        Token NEG59=null;
        Token POS60=null;
        Token LBRACKET63=null;
        Token RBRACKET64=null;
        Token ASSIGN65=null;
        CgsuiteParser.standardRelationalToken_return standardRelationalToken61 = null;

        CgsuiteParser.opAssignmentToken_return opAssignmentToken62 = null;


        CgsuiteTree PLUS53_tree=null;
        CgsuiteTree MINUS54_tree=null;
        CgsuiteTree AST55_tree=null;
        CgsuiteTree FSLASH56_tree=null;
        CgsuiteTree PERCENT57_tree=null;
        CgsuiteTree EXP58_tree=null;
        CgsuiteTree NEG59_tree=null;
        CgsuiteTree POS60_tree=null;
        CgsuiteTree LBRACKET63_tree=null;
        CgsuiteTree RBRACKET64_tree=null;
        CgsuiteTree ASSIGN65_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:252:5: ( PLUS | MINUS | AST | FSLASH | PERCENT | EXP | NEG | POS | standardRelationalToken | opAssignmentToken | LBRACKET RBRACKET ( ASSIGN )? )
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
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:252:7: PLUS
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    PLUS53=(Token)match(input,PLUS,FOLLOW_PLUS_in_opCode1497); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PLUS53_tree = (CgsuiteTree)adaptor.create(PLUS53);
                    adaptor.addChild(root_0, PLUS53_tree);
                    }

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:252:14: MINUS
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    MINUS54=(Token)match(input,MINUS,FOLLOW_MINUS_in_opCode1501); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MINUS54_tree = (CgsuiteTree)adaptor.create(MINUS54);
                    adaptor.addChild(root_0, MINUS54_tree);
                    }

                    }
                    break;
                case 3 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:252:22: AST
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    AST55=(Token)match(input,AST,FOLLOW_AST_in_opCode1505); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    AST55_tree = (CgsuiteTree)adaptor.create(AST55);
                    adaptor.addChild(root_0, AST55_tree);
                    }

                    }
                    break;
                case 4 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:252:28: FSLASH
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    FSLASH56=(Token)match(input,FSLASH,FOLLOW_FSLASH_in_opCode1509); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FSLASH56_tree = (CgsuiteTree)adaptor.create(FSLASH56);
                    adaptor.addChild(root_0, FSLASH56_tree);
                    }

                    }
                    break;
                case 5 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:252:37: PERCENT
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    PERCENT57=(Token)match(input,PERCENT,FOLLOW_PERCENT_in_opCode1513); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PERCENT57_tree = (CgsuiteTree)adaptor.create(PERCENT57);
                    adaptor.addChild(root_0, PERCENT57_tree);
                    }

                    }
                    break;
                case 6 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:252:47: EXP
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    EXP58=(Token)match(input,EXP,FOLLOW_EXP_in_opCode1517); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EXP58_tree = (CgsuiteTree)adaptor.create(EXP58);
                    adaptor.addChild(root_0, EXP58_tree);
                    }

                    }
                    break;
                case 7 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:252:53: NEG
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    NEG59=(Token)match(input,NEG,FOLLOW_NEG_in_opCode1521); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NEG59_tree = (CgsuiteTree)adaptor.create(NEG59);
                    adaptor.addChild(root_0, NEG59_tree);
                    }

                    }
                    break;
                case 8 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:252:59: POS
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    POS60=(Token)match(input,POS,FOLLOW_POS_in_opCode1525); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    POS60_tree = (CgsuiteTree)adaptor.create(POS60);
                    adaptor.addChild(root_0, POS60_tree);
                    }

                    }
                    break;
                case 9 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:253:7: standardRelationalToken
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_standardRelationalToken_in_opCode1533);
                    standardRelationalToken61=standardRelationalToken();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, standardRelationalToken61.getTree());

                    }
                    break;
                case 10 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:254:7: opAssignmentToken
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_opAssignmentToken_in_opCode1541);
                    opAssignmentToken62=opAssignmentToken();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, opAssignmentToken62.getTree());

                    }
                    break;
                case 11 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:255:7: LBRACKET RBRACKET ( ASSIGN )?
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    LBRACKET63=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_opCode1549); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LBRACKET63_tree = (CgsuiteTree)adaptor.create(LBRACKET63);
                    adaptor.addChild(root_0, LBRACKET63_tree);
                    }
                    RBRACKET64=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_opCode1551); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RBRACKET64_tree = (CgsuiteTree)adaptor.create(RBRACKET64);
                    adaptor.addChild(root_0, RBRACKET64_tree);
                    }
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:255:25: ( ASSIGN )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==ASSIGN) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:255:25: ASSIGN
                            {
                            ASSIGN65=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_opCode1553); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            ASSIGN65_tree = (CgsuiteTree)adaptor.create(ASSIGN65);
                            adaptor.addChild(root_0, ASSIGN65_tree);
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:259:1: methodParameterList : ( methodParameter ( COMMA methodParameter )* )? -> ^( METHOD_PARAMETER_LIST ( methodParameter )* ) ;
    public final CgsuiteParser.methodParameterList_return methodParameterList() throws RecognitionException {
        CgsuiteParser.methodParameterList_return retval = new CgsuiteParser.methodParameterList_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token COMMA67=null;
        CgsuiteParser.methodParameter_return methodParameter66 = null;

        CgsuiteParser.methodParameter_return methodParameter68 = null;


        CgsuiteTree COMMA67_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_methodParameter=new RewriteRuleSubtreeStream(adaptor,"rule methodParameter");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:260:2: ( ( methodParameter ( COMMA methodParameter )* )? -> ^( METHOD_PARAMETER_LIST ( methodParameter )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:260:4: ( methodParameter ( COMMA methodParameter )* )?
            {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:260:4: ( methodParameter ( COMMA methodParameter )* )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==IDENTIFIER) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:260:5: methodParameter ( COMMA methodParameter )*
                    {
                    pushFollow(FOLLOW_methodParameter_in_methodParameterList1571);
                    methodParameter66=methodParameter();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_methodParameter.add(methodParameter66.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:260:21: ( COMMA methodParameter )*
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( (LA13_0==COMMA) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:260:22: COMMA methodParameter
                    	    {
                    	    COMMA67=(Token)match(input,COMMA,FOLLOW_COMMA_in_methodParameterList1574); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA67);

                    	    pushFollow(FOLLOW_methodParameter_in_methodParameterList1576);
                    	    methodParameter68=methodParameter();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_methodParameter.add(methodParameter68.getTree());

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
            // 260:48: -> ^( METHOD_PARAMETER_LIST ( methodParameter )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:260:51: ^( METHOD_PARAMETER_LIST ( methodParameter )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(METHOD_PARAMETER_LIST, "METHOD_PARAMETER_LIST"), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:260:75: ( methodParameter )*
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:263:1: methodParameter : (a= IDENTIFIER (b= IDENTIFIER )? QUESTION ( expression )? -> ^( QUESTION ^( $a ( $b)? ) ( expression )? ) | a= IDENTIFIER b= IDENTIFIER -> ^( $b $a) | IDENTIFIER );
    public final CgsuiteParser.methodParameter_return methodParameter() throws RecognitionException {
        CgsuiteParser.methodParameter_return retval = new CgsuiteParser.methodParameter_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token a=null;
        Token b=null;
        Token QUESTION69=null;
        Token IDENTIFIER71=null;
        CgsuiteParser.expression_return expression70 = null;


        CgsuiteTree a_tree=null;
        CgsuiteTree b_tree=null;
        CgsuiteTree QUESTION69_tree=null;
        CgsuiteTree IDENTIFIER71_tree=null;
        RewriteRuleTokenStream stream_QUESTION=new RewriteRuleTokenStream(adaptor,"token QUESTION");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:264:2: (a= IDENTIFIER (b= IDENTIFIER )? QUESTION ( expression )? -> ^( QUESTION ^( $a ( $b)? ) ( expression )? ) | a= IDENTIFIER b= IDENTIFIER -> ^( $b $a) | IDENTIFIER )
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
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:264:4: a= IDENTIFIER (b= IDENTIFIER )? QUESTION ( expression )?
                    {
                    a=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodParameter1603); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_IDENTIFIER.add(a);

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:264:18: (b= IDENTIFIER )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==IDENTIFIER) ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:264:18: b= IDENTIFIER
                            {
                            b=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodParameter1607); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_IDENTIFIER.add(b);


                            }
                            break;

                    }

                    QUESTION69=(Token)match(input,QUESTION,FOLLOW_QUESTION_in_methodParameter1610); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_QUESTION.add(QUESTION69);

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:264:40: ( expression )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( ((LA16_0>=PLUS && LA16_0<=AST)||LA16_0==LPAREN||LA16_0==LBRACKET||LA16_0==LBRACE||(LA16_0>=CARET && LA16_0<=VEEVEE)||LA16_0==BEGIN||LA16_0==BY||LA16_0==DO||(LA16_0>=FALSE && LA16_0<=FOR)||LA16_0==FROM||LA16_0==IF||(LA16_0>=NIL && LA16_0<=NOT)||(LA16_0>=THIS && LA16_0<=TRUE)||(LA16_0>=WHERE && LA16_0<=WHILE)||(LA16_0>=IDENTIFIER && LA16_0<=CHAR)) ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:264:40: expression
                            {
                            pushFollow(FOLLOW_expression_in_methodParameter1612);
                            expression70=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_expression.add(expression70.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: b, expression, QUESTION, a
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
                    // 264:52: -> ^( QUESTION ^( $a ( $b)? ) ( expression )? )
                    {
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:264:55: ^( QUESTION ^( $a ( $b)? ) ( expression )? )
                        {
                        CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                        root_1 = (CgsuiteTree)adaptor.becomeRoot(stream_QUESTION.nextNode(), root_1);

                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:264:66: ^( $a ( $b)? )
                        {
                        CgsuiteTree root_2 = (CgsuiteTree)adaptor.nil();
                        root_2 = (CgsuiteTree)adaptor.becomeRoot(stream_a.nextNode(), root_2);

                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:264:71: ( $b)?
                        if ( stream_b.hasNext() ) {
                            adaptor.addChild(root_2, stream_b.nextNode());

                        }
                        stream_b.reset();

                        adaptor.addChild(root_1, root_2);
                        }
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:264:76: ( expression )?
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
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:265:4: a= IDENTIFIER b= IDENTIFIER
                    {
                    a=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodParameter1638); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_IDENTIFIER.add(a);

                    b=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodParameter1642); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_IDENTIFIER.add(b);



                    // AST REWRITE
                    // elements: a, b
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
                    // 265:30: -> ^( $b $a)
                    {
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:265:33: ^( $b $a)
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
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:266:4: IDENTIFIER
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    IDENTIFIER71=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodParameter1657); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER71_tree = (CgsuiteTree)adaptor.create(IDENTIFIER71);
                    adaptor.addChild(root_0, IDENTIFIER71_tree);
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:269:1: enumDeclaration : modifiers ENUM IDENTIFIER ( IDENTIFIER ( COMMA IDENTIFIER )* ) ( SEMI )? END ;
    public final CgsuiteParser.enumDeclaration_return enumDeclaration() throws RecognitionException {
        CgsuiteParser.enumDeclaration_return retval = new CgsuiteParser.enumDeclaration_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token ENUM73=null;
        Token IDENTIFIER74=null;
        Token IDENTIFIER75=null;
        Token COMMA76=null;
        Token IDENTIFIER77=null;
        Token SEMI78=null;
        Token END79=null;
        CgsuiteParser.modifiers_return modifiers72 = null;


        CgsuiteTree ENUM73_tree=null;
        CgsuiteTree IDENTIFIER74_tree=null;
        CgsuiteTree IDENTIFIER75_tree=null;
        CgsuiteTree COMMA76_tree=null;
        CgsuiteTree IDENTIFIER77_tree=null;
        CgsuiteTree SEMI78_tree=null;
        CgsuiteTree END79_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:270:5: ( modifiers ENUM IDENTIFIER ( IDENTIFIER ( COMMA IDENTIFIER )* ) ( SEMI )? END )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:270:7: modifiers ENUM IDENTIFIER ( IDENTIFIER ( COMMA IDENTIFIER )* ) ( SEMI )? END
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_enumDeclaration1671);
            modifiers72=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers72.getTree());
            ENUM73=(Token)match(input,ENUM,FOLLOW_ENUM_in_enumDeclaration1673); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ENUM73_tree = (CgsuiteTree)adaptor.create(ENUM73);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(ENUM73_tree, root_0);
            }
            IDENTIFIER74=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_enumDeclaration1676); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER74_tree = (CgsuiteTree)adaptor.create(IDENTIFIER74);
            adaptor.addChild(root_0, IDENTIFIER74_tree);
            }
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:271:7: ( IDENTIFIER ( COMMA IDENTIFIER )* )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:271:8: IDENTIFIER ( COMMA IDENTIFIER )*
            {
            IDENTIFIER75=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_enumDeclaration1685); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER75_tree = (CgsuiteTree)adaptor.create(IDENTIFIER75);
            adaptor.addChild(root_0, IDENTIFIER75_tree);
            }
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:271:19: ( COMMA IDENTIFIER )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==COMMA) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:271:20: COMMA IDENTIFIER
            	    {
            	    COMMA76=(Token)match(input,COMMA,FOLLOW_COMMA_in_enumDeclaration1688); if (state.failed) return retval;
            	    IDENTIFIER77=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_enumDeclaration1691); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    IDENTIFIER77_tree = (CgsuiteTree)adaptor.create(IDENTIFIER77);
            	    adaptor.addChild(root_0, IDENTIFIER77_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);


            }

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:271:45: ( SEMI )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==SEMI) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:271:45: SEMI
                    {
                    SEMI78=(Token)match(input,SEMI,FOLLOW_SEMI_in_enumDeclaration1696); if (state.failed) return retval;

                    }
                    break;

            }

            END79=(Token)match(input,END,FOLLOW_END_in_enumDeclaration1700); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            END79_tree = (CgsuiteTree)adaptor.create(END79);
            adaptor.addChild(root_0, END79_tree);
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
    // $ANTLR end "enumDeclaration"

    public static class script_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "script"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:274:1: script : block EOF ;
    public final CgsuiteParser.script_return script() throws RecognitionException {
        CgsuiteParser.script_return retval = new CgsuiteParser.script_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token EOF81=null;
        CgsuiteParser.block_return block80 = null;


        CgsuiteTree EOF81_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:275:5: ( block EOF )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:275:7: block EOF
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_block_in_script1717);
            block80=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block80.getTree());
            EOF81=(Token)match(input,EOF,FOLLOW_EOF_in_script1719); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EOF81_tree = (CgsuiteTree)adaptor.create(EOF81);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(EOF81_tree, root_0);
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:278:1: block : statementSequence ;
    public final CgsuiteParser.block_return block() throws RecognitionException {
        CgsuiteParser.block_return retval = new CgsuiteParser.block_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        CgsuiteParser.statementSequence_return statementSequence82 = null;



        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:279:5: ( statementSequence )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:279:7: statementSequence
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_statementSequence_in_block1737);
            statementSequence82=statementSequence();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence82.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:282:1: statementSequence : ( statement )? ( SEMI ( statement )? )* -> ^( STATEMENT_SEQUENCE ( statement )* ) ;
    public final CgsuiteParser.statementSequence_return statementSequence() throws RecognitionException {
        CgsuiteParser.statementSequence_return retval = new CgsuiteParser.statementSequence_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token SEMI84=null;
        CgsuiteParser.statement_return statement83 = null;

        CgsuiteParser.statement_return statement85 = null;


        CgsuiteTree SEMI84_tree=null;
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:283:2: ( ( statement )? ( SEMI ( statement )? )* -> ^( STATEMENT_SEQUENCE ( statement )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:283:4: ( statement )? ( SEMI ( statement )? )*
            {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:283:4: ( statement )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( ((LA20_0>=PLUS && LA20_0<=AST)||LA20_0==LPAREN||LA20_0==LBRACKET||LA20_0==LBRACE||(LA20_0>=CARET && LA20_0<=VEEVEE)||(LA20_0>=BEGIN && LA20_0<=BY)||(LA20_0>=CLEAR && LA20_0<=DO)||(LA20_0>=FALSE && LA20_0<=FOR)||LA20_0==FROM||LA20_0==IF||(LA20_0>=NIL && LA20_0<=NOT)||LA20_0==RETURN||(LA20_0>=THIS && LA20_0<=TRUE)||(LA20_0>=WHERE && LA20_0<=WHILE)||(LA20_0>=IDENTIFIER && LA20_0<=CHAR)) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:283:4: statement
                    {
                    pushFollow(FOLLOW_statement_in_statementSequence1752);
                    statement83=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_statement.add(statement83.getTree());

                    }
                    break;

            }

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:283:15: ( SEMI ( statement )? )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==SEMI) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:283:16: SEMI ( statement )?
            	    {
            	    SEMI84=(Token)match(input,SEMI,FOLLOW_SEMI_in_statementSequence1756); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_SEMI.add(SEMI84);

            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:283:21: ( statement )?
            	    int alt21=2;
            	    int LA21_0 = input.LA(1);

            	    if ( ((LA21_0>=PLUS && LA21_0<=AST)||LA21_0==LPAREN||LA21_0==LBRACKET||LA21_0==LBRACE||(LA21_0>=CARET && LA21_0<=VEEVEE)||(LA21_0>=BEGIN && LA21_0<=BY)||(LA21_0>=CLEAR && LA21_0<=DO)||(LA21_0>=FALSE && LA21_0<=FOR)||LA21_0==FROM||LA21_0==IF||(LA21_0>=NIL && LA21_0<=NOT)||LA21_0==RETURN||(LA21_0>=THIS && LA21_0<=TRUE)||(LA21_0>=WHERE && LA21_0<=WHILE)||(LA21_0>=IDENTIFIER && LA21_0<=CHAR)) ) {
            	        alt21=1;
            	    }
            	    switch (alt21) {
            	        case 1 :
            	            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:283:21: statement
            	            {
            	            pushFollow(FOLLOW_statement_in_statementSequence1758);
            	            statement85=statement();

            	            state._fsp--;
            	            if (state.failed) return retval;
            	            if ( state.backtracking==0 ) stream_statement.add(statement85.getTree());

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop22;
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
            // 283:34: -> ^( STATEMENT_SEQUENCE ( statement )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:283:37: ^( STATEMENT_SEQUENCE ( statement )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(STATEMENT_SEQUENCE, "STATEMENT_SEQUENCE"), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:283:58: ( statement )*
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:286:1: statement : ( BREAK | CONTINUE | RETURN expression | CLEAR | expression );
    public final CgsuiteParser.statement_return statement() throws RecognitionException {
        CgsuiteParser.statement_return retval = new CgsuiteParser.statement_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token BREAK86=null;
        Token CONTINUE87=null;
        Token RETURN88=null;
        Token CLEAR90=null;
        CgsuiteParser.expression_return expression89 = null;

        CgsuiteParser.expression_return expression91 = null;


        CgsuiteTree BREAK86_tree=null;
        CgsuiteTree CONTINUE87_tree=null;
        CgsuiteTree RETURN88_tree=null;
        CgsuiteTree CLEAR90_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:287:2: ( BREAK | CONTINUE | RETURN expression | CLEAR | expression )
            int alt23=5;
            switch ( input.LA(1) ) {
            case BREAK:
                {
                alt23=1;
                }
                break;
            case CONTINUE:
                {
                alt23=2;
                }
                break;
            case RETURN:
                {
                alt23=3;
                }
                break;
            case CLEAR:
                {
                alt23=4;
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
                alt23=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }

            switch (alt23) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:287:4: BREAK
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    BREAK86=(Token)match(input,BREAK,FOLLOW_BREAK_in_statement1782); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BREAK86_tree = (CgsuiteTree)adaptor.create(BREAK86);
                    adaptor.addChild(root_0, BREAK86_tree);
                    }

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:288:4: CONTINUE
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    CONTINUE87=(Token)match(input,CONTINUE,FOLLOW_CONTINUE_in_statement1787); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CONTINUE87_tree = (CgsuiteTree)adaptor.create(CONTINUE87);
                    adaptor.addChild(root_0, CONTINUE87_tree);
                    }

                    }
                    break;
                case 3 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:289:4: RETURN expression
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    RETURN88=(Token)match(input,RETURN,FOLLOW_RETURN_in_statement1792); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RETURN88_tree = (CgsuiteTree)adaptor.create(RETURN88);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(RETURN88_tree, root_0);
                    }
                    pushFollow(FOLLOW_expression_in_statement1795);
                    expression89=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression89.getTree());

                    }
                    break;
                case 4 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:290:7: CLEAR
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    CLEAR90=(Token)match(input,CLEAR,FOLLOW_CLEAR_in_statement1803); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CLEAR90_tree = (CgsuiteTree)adaptor.create(CLEAR90);
                    adaptor.addChild(root_0, CLEAR90_tree);
                    }

                    }
                    break;
                case 5 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:291:4: expression
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_expression_in_statement1808);
                    expression91=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression91.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:294:1: expression : assignmentExpression ;
    public final CgsuiteParser.expression_return expression() throws RecognitionException {
        CgsuiteParser.expression_return retval = new CgsuiteParser.expression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        CgsuiteParser.assignmentExpression_return assignmentExpression92 = null;



        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:295:2: ( assignmentExpression )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:295:4: assignmentExpression
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_assignmentExpression_in_expression1819);
            assignmentExpression92=assignmentExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, assignmentExpression92.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:298:1: assignmentExpression : functionExpression ( assignmentToken assignmentExpression )? ;
    public final CgsuiteParser.assignmentExpression_return assignmentExpression() throws RecognitionException {
        CgsuiteParser.assignmentExpression_return retval = new CgsuiteParser.assignmentExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        CgsuiteParser.functionExpression_return functionExpression93 = null;

        CgsuiteParser.assignmentToken_return assignmentToken94 = null;

        CgsuiteParser.assignmentExpression_return assignmentExpression95 = null;



        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:299:2: ( functionExpression ( assignmentToken assignmentExpression )? )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:299:4: functionExpression ( assignmentToken assignmentExpression )?
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_functionExpression_in_assignmentExpression1830);
            functionExpression93=functionExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, functionExpression93.getTree());
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:299:23: ( assignmentToken assignmentExpression )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( ((LA24_0>=ASSIGN && LA24_0<=ASN_EXP)) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:299:24: assignmentToken assignmentExpression
                    {
                    pushFollow(FOLLOW_assignmentToken_in_assignmentExpression1833);
                    assignmentToken94=assignmentToken();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (CgsuiteTree)adaptor.becomeRoot(assignmentToken94.getTree(), root_0);
                    pushFollow(FOLLOW_assignmentExpression_in_assignmentExpression1836);
                    assignmentExpression95=assignmentExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, assignmentExpression95.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:302:1: assignmentToken : ( ASSIGN | opAssignmentToken );
    public final CgsuiteParser.assignmentToken_return assignmentToken() throws RecognitionException {
        CgsuiteParser.assignmentToken_return retval = new CgsuiteParser.assignmentToken_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token ASSIGN96=null;
        CgsuiteParser.opAssignmentToken_return opAssignmentToken97 = null;


        CgsuiteTree ASSIGN96_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:303:2: ( ASSIGN | opAssignmentToken )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==ASSIGN) ) {
                alt25=1;
            }
            else if ( ((LA25_0>=ASN_PLUS && LA25_0<=ASN_EXP)) ) {
                alt25=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:303:4: ASSIGN
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    ASSIGN96=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_assignmentToken1850); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ASSIGN96_tree = (CgsuiteTree)adaptor.create(ASSIGN96);
                    adaptor.addChild(root_0, ASSIGN96_tree);
                    }

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:304:4: opAssignmentToken
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_opAssignmentToken_in_assignmentToken1855);
                    opAssignmentToken97=opAssignmentToken();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, opAssignmentToken97.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:307:1: opAssignmentToken : ( ASN_PLUS | ASN_MINUS | ASN_TIMES | ASN_DIV | ASN_MOD | ASN_AND | ASN_OR | ASN_XOR | ASN_EXP );
    public final CgsuiteParser.opAssignmentToken_return opAssignmentToken() throws RecognitionException {
        CgsuiteParser.opAssignmentToken_return retval = new CgsuiteParser.opAssignmentToken_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token set98=null;

        CgsuiteTree set98_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:308:5: ( ASN_PLUS | ASN_MINUS | ASN_TIMES | ASN_DIV | ASN_MOD | ASN_AND | ASN_OR | ASN_XOR | ASN_EXP )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            set98=(Token)input.LT(1);
            if ( (input.LA(1)>=ASN_PLUS && input.LA(1)<=ASN_EXP) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (CgsuiteTree)adaptor.create(set98));
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:319:1: functionExpression : ( procedureParameterList RARROW functionExpression | controlExpression );
    public final CgsuiteParser.functionExpression_return functionExpression() throws RecognitionException {
        CgsuiteParser.functionExpression_return retval = new CgsuiteParser.functionExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token RARROW100=null;
        CgsuiteParser.procedureParameterList_return procedureParameterList99 = null;

        CgsuiteParser.functionExpression_return functionExpression101 = null;

        CgsuiteParser.controlExpression_return controlExpression102 = null;


        CgsuiteTree RARROW100_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:320:2: ( procedureParameterList RARROW functionExpression | controlExpression )
            int alt26=2;
            switch ( input.LA(1) ) {
            case IDENTIFIER:
                {
                int LA26_1 = input.LA(2);

                if ( (LA26_1==RARROW) ) {
                    alt26=1;
                }
                else if ( (LA26_1==EOF||(LA26_1>=PLUS && LA26_1<=MINUS)||(LA26_1>=AST && LA26_1<=PERCENT)||(LA26_1>=LPAREN && LA26_1<=RBRACKET)||LA26_1==RBRACE||(LA26_1>=COMMA && LA26_1<=SEMI)||(LA26_1>=EQUALS && LA26_1<=COMPARE)||LA26_1==BIGRARROW||(LA26_1>=REFEQUALS && LA26_1<=ASN_EXP)||LA26_1==AND||LA26_1==BY||(LA26_1>=DO && LA26_1<=END)||LA26_1==IN||LA26_1==OR||LA26_1==THEN||LA26_1==TO||(LA26_1>=WHERE && LA26_1<=WHILE)||LA26_1==SLASHES) ) {
                    alt26=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 1, input);

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
                        alt26=1;
                        }
                        break;
                    case RPAREN:
                        {
                        int LA26_6 = input.LA(4);

                        if ( (LA26_6==RARROW) ) {
                            alt26=1;
                        }
                        else if ( (LA26_6==EOF||(LA26_6>=PLUS && LA26_6<=MINUS)||(LA26_6>=AST && LA26_6<=PERCENT)||(LA26_6>=LPAREN && LA26_6<=RBRACKET)||LA26_6==RBRACE||(LA26_6>=COMMA && LA26_6<=SEMI)||(LA26_6>=EQUALS && LA26_6<=COMPARE)||LA26_6==BIGRARROW||(LA26_6>=REFEQUALS && LA26_6<=ASN_EXP)||LA26_6==AND||LA26_6==BY||(LA26_6>=DO && LA26_6<=END)||LA26_6==IN||LA26_6==OR||LA26_6==THEN||LA26_6==TO||(LA26_6>=WHERE && LA26_6<=WHILE)||LA26_6==SLASHES) ) {
                            alt26=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 26, 6, input);

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
                        alt26=2;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 26, 5, input);

                        throw nvae;
                    }

                    }
                    break;
                case RPAREN:
                    {
                    int LA26_6 = input.LA(3);

                    if ( (LA26_6==RARROW) ) {
                        alt26=1;
                    }
                    else if ( (LA26_6==EOF||(LA26_6>=PLUS && LA26_6<=MINUS)||(LA26_6>=AST && LA26_6<=PERCENT)||(LA26_6>=LPAREN && LA26_6<=RBRACKET)||LA26_6==RBRACE||(LA26_6>=COMMA && LA26_6<=SEMI)||(LA26_6>=EQUALS && LA26_6<=COMPARE)||LA26_6==BIGRARROW||(LA26_6>=REFEQUALS && LA26_6<=ASN_EXP)||LA26_6==AND||LA26_6==BY||(LA26_6>=DO && LA26_6<=END)||LA26_6==IN||LA26_6==OR||LA26_6==THEN||LA26_6==TO||(LA26_6>=WHERE && LA26_6<=WHILE)||LA26_6==SLASHES) ) {
                        alt26=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 26, 6, input);

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
                    alt26=2;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 2, input);

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
                alt26=2;
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
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:320:4: procedureParameterList RARROW functionExpression
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_procedureParameterList_in_functionExpression1921);
                    procedureParameterList99=procedureParameterList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, procedureParameterList99.getTree());
                    RARROW100=(Token)match(input,RARROW,FOLLOW_RARROW_in_functionExpression1923); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RARROW100_tree = (CgsuiteTree)adaptor.create(RARROW100);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(RARROW100_tree, root_0);
                    }
                    pushFollow(FOLLOW_functionExpression_in_functionExpression1926);
                    functionExpression101=functionExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, functionExpression101.getTree());

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:321:7: controlExpression
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_controlExpression_in_functionExpression1934);
                    controlExpression102=controlExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, controlExpression102.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:324:1: procedureParameterList : ( IDENTIFIER -> ^( PROCEDURE_PARAMETER_LIST IDENTIFIER ) | LPAREN ( IDENTIFIER ( COMMA IDENTIFIER )* )? RPAREN -> ^( PROCEDURE_PARAMETER_LIST ( IDENTIFIER )* ) );
    public final CgsuiteParser.procedureParameterList_return procedureParameterList() throws RecognitionException {
        CgsuiteParser.procedureParameterList_return retval = new CgsuiteParser.procedureParameterList_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token IDENTIFIER103=null;
        Token LPAREN104=null;
        Token IDENTIFIER105=null;
        Token COMMA106=null;
        Token IDENTIFIER107=null;
        Token RPAREN108=null;

        CgsuiteTree IDENTIFIER103_tree=null;
        CgsuiteTree LPAREN104_tree=null;
        CgsuiteTree IDENTIFIER105_tree=null;
        CgsuiteTree COMMA106_tree=null;
        CgsuiteTree IDENTIFIER107_tree=null;
        CgsuiteTree RPAREN108_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:325:5: ( IDENTIFIER -> ^( PROCEDURE_PARAMETER_LIST IDENTIFIER ) | LPAREN ( IDENTIFIER ( COMMA IDENTIFIER )* )? RPAREN -> ^( PROCEDURE_PARAMETER_LIST ( IDENTIFIER )* ) )
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==IDENTIFIER) ) {
                alt29=1;
            }
            else if ( (LA29_0==LPAREN) ) {
                alt29=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:325:7: IDENTIFIER
                    {
                    IDENTIFIER103=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_procedureParameterList1948); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER103);



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
                    // 325:18: -> ^( PROCEDURE_PARAMETER_LIST IDENTIFIER )
                    {
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:325:21: ^( PROCEDURE_PARAMETER_LIST IDENTIFIER )
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
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:326:7: LPAREN ( IDENTIFIER ( COMMA IDENTIFIER )* )? RPAREN
                    {
                    LPAREN104=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_procedureParameterList1964); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN104);

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:326:14: ( IDENTIFIER ( COMMA IDENTIFIER )* )?
                    int alt28=2;
                    int LA28_0 = input.LA(1);

                    if ( (LA28_0==IDENTIFIER) ) {
                        alt28=1;
                    }
                    switch (alt28) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:326:15: IDENTIFIER ( COMMA IDENTIFIER )*
                            {
                            IDENTIFIER105=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_procedureParameterList1967); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER105);

                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:326:26: ( COMMA IDENTIFIER )*
                            loop27:
                            do {
                                int alt27=2;
                                int LA27_0 = input.LA(1);

                                if ( (LA27_0==COMMA) ) {
                                    alt27=1;
                                }


                                switch (alt27) {
                            	case 1 :
                            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:326:27: COMMA IDENTIFIER
                            	    {
                            	    COMMA106=(Token)match(input,COMMA,FOLLOW_COMMA_in_procedureParameterList1970); if (state.failed) return retval; 
                            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA106);

                            	    IDENTIFIER107=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_procedureParameterList1972); if (state.failed) return retval; 
                            	    if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER107);


                            	    }
                            	    break;

                            	default :
                            	    break loop27;
                                }
                            } while (true);


                            }
                            break;

                    }

                    RPAREN108=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_procedureParameterList1978); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN108);



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
                    // 326:55: -> ^( PROCEDURE_PARAMETER_LIST ( IDENTIFIER )* )
                    {
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:326:58: ^( PROCEDURE_PARAMETER_LIST ( IDENTIFIER )* )
                        {
                        CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                        root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(PROCEDURE_PARAMETER_LIST, "PROCEDURE_PARAMETER_LIST"), root_1);

                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:326:85: ( IDENTIFIER )*
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:329:1: controlExpression : ( IF expression THEN statementSequence ( elseifClause )? END | ( forExpression )? ( fromExpression )? ( toExpression )? ( byExpression )? ( whileExpression )? ( whereExpression )? DO statementSequence END | FOR expression IN expression DO statementSequence END | orExpression );
    public final CgsuiteParser.controlExpression_return controlExpression() throws RecognitionException {
        CgsuiteParser.controlExpression_return retval = new CgsuiteParser.controlExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token IF109=null;
        Token THEN111=null;
        Token END114=null;
        Token DO121=null;
        Token END123=null;
        Token FOR124=null;
        Token IN126=null;
        Token DO128=null;
        Token END130=null;
        CgsuiteParser.expression_return expression110 = null;

        CgsuiteParser.statementSequence_return statementSequence112 = null;

        CgsuiteParser.elseifClause_return elseifClause113 = null;

        CgsuiteParser.forExpression_return forExpression115 = null;

        CgsuiteParser.fromExpression_return fromExpression116 = null;

        CgsuiteParser.toExpression_return toExpression117 = null;

        CgsuiteParser.byExpression_return byExpression118 = null;

        CgsuiteParser.whileExpression_return whileExpression119 = null;

        CgsuiteParser.whereExpression_return whereExpression120 = null;

        CgsuiteParser.statementSequence_return statementSequence122 = null;

        CgsuiteParser.expression_return expression125 = null;

        CgsuiteParser.expression_return expression127 = null;

        CgsuiteParser.statementSequence_return statementSequence129 = null;

        CgsuiteParser.orExpression_return orExpression131 = null;


        CgsuiteTree IF109_tree=null;
        CgsuiteTree THEN111_tree=null;
        CgsuiteTree END114_tree=null;
        CgsuiteTree DO121_tree=null;
        CgsuiteTree END123_tree=null;
        CgsuiteTree FOR124_tree=null;
        CgsuiteTree IN126_tree=null;
        CgsuiteTree DO128_tree=null;
        CgsuiteTree END130_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:330:2: ( IF expression THEN statementSequence ( elseifClause )? END | ( forExpression )? ( fromExpression )? ( toExpression )? ( byExpression )? ( whileExpression )? ( whereExpression )? DO statementSequence END | FOR expression IN expression DO statementSequence END | orExpression )
            int alt37=4;
            switch ( input.LA(1) ) {
            case IF:
                {
                alt37=1;
                }
                break;
            case FOR:
                {
                int LA37_2 = input.LA(2);

                if ( (LA37_2==IDENTIFIER) ) {
                    int LA37_5 = input.LA(3);

                    if ( (LA37_5==BY||LA37_5==DO||LA37_5==FROM||LA37_5==TO||(LA37_5>=WHERE && LA37_5<=WHILE)) ) {
                        alt37=2;
                    }
                    else if ( ((LA37_5>=PLUS && LA37_5<=MINUS)||(LA37_5>=AST && LA37_5<=PERCENT)||LA37_5==LPAREN||LA37_5==LBRACKET||(LA37_5>=EQUALS && LA37_5<=RARROW)||(LA37_5>=REFEQUALS && LA37_5<=ASN_EXP)||LA37_5==AND||LA37_5==IN||LA37_5==OR) ) {
                        alt37=3;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 37, 5, input);

                        throw nvae;
                    }
                }
                else if ( ((LA37_2>=PLUS && LA37_2<=AST)||LA37_2==LPAREN||LA37_2==LBRACKET||LA37_2==LBRACE||(LA37_2>=CARET && LA37_2<=VEEVEE)||LA37_2==BEGIN||LA37_2==BY||LA37_2==DO||(LA37_2>=FALSE && LA37_2<=FOR)||LA37_2==FROM||LA37_2==IF||(LA37_2>=NIL && LA37_2<=NOT)||(LA37_2>=THIS && LA37_2<=TRUE)||(LA37_2>=WHERE && LA37_2<=WHILE)||(LA37_2>=STRING && LA37_2<=CHAR)) ) {
                    alt37=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 37, 2, input);

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
                alt37=2;
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
                alt37=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);

                throw nvae;
            }

            switch (alt37) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:330:4: IF expression THEN statementSequence ( elseifClause )? END
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    IF109=(Token)match(input,IF,FOLLOW_IF_in_controlExpression2002); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IF109_tree = (CgsuiteTree)adaptor.create(IF109);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(IF109_tree, root_0);
                    }
                    pushFollow(FOLLOW_expression_in_controlExpression2005);
                    expression110=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression110.getTree());
                    THEN111=(Token)match(input,THEN,FOLLOW_THEN_in_controlExpression2007); if (state.failed) return retval;
                    pushFollow(FOLLOW_statementSequence_in_controlExpression2010);
                    statementSequence112=statementSequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence112.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:330:43: ( elseifClause )?
                    int alt30=2;
                    int LA30_0 = input.LA(1);

                    if ( ((LA30_0>=ELSE && LA30_0<=ELSEIF)) ) {
                        alt30=1;
                    }
                    switch (alt30) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:330:43: elseifClause
                            {
                            pushFollow(FOLLOW_elseifClause_in_controlExpression2012);
                            elseifClause113=elseifClause();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, elseifClause113.getTree());

                            }
                            break;

                    }

                    END114=(Token)match(input,END,FOLLOW_END_in_controlExpression2015); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:331:4: ( forExpression )? ( fromExpression )? ( toExpression )? ( byExpression )? ( whileExpression )? ( whereExpression )? DO statementSequence END
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:331:4: ( forExpression )?
                    int alt31=2;
                    int LA31_0 = input.LA(1);

                    if ( (LA31_0==FOR) ) {
                        alt31=1;
                    }
                    switch (alt31) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:331:4: forExpression
                            {
                            pushFollow(FOLLOW_forExpression_in_controlExpression2021);
                            forExpression115=forExpression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, forExpression115.getTree());

                            }
                            break;

                    }

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:331:19: ( fromExpression )?
                    int alt32=2;
                    int LA32_0 = input.LA(1);

                    if ( (LA32_0==FROM) ) {
                        alt32=1;
                    }
                    switch (alt32) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:331:19: fromExpression
                            {
                            pushFollow(FOLLOW_fromExpression_in_controlExpression2024);
                            fromExpression116=fromExpression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, fromExpression116.getTree());

                            }
                            break;

                    }

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:331:35: ( toExpression )?
                    int alt33=2;
                    int LA33_0 = input.LA(1);

                    if ( (LA33_0==TO) ) {
                        alt33=1;
                    }
                    switch (alt33) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:331:35: toExpression
                            {
                            pushFollow(FOLLOW_toExpression_in_controlExpression2027);
                            toExpression117=toExpression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, toExpression117.getTree());

                            }
                            break;

                    }

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:331:49: ( byExpression )?
                    int alt34=2;
                    int LA34_0 = input.LA(1);

                    if ( (LA34_0==BY) ) {
                        alt34=1;
                    }
                    switch (alt34) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:331:49: byExpression
                            {
                            pushFollow(FOLLOW_byExpression_in_controlExpression2030);
                            byExpression118=byExpression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, byExpression118.getTree());

                            }
                            break;

                    }

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:331:63: ( whileExpression )?
                    int alt35=2;
                    int LA35_0 = input.LA(1);

                    if ( (LA35_0==WHILE) ) {
                        alt35=1;
                    }
                    switch (alt35) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:331:63: whileExpression
                            {
                            pushFollow(FOLLOW_whileExpression_in_controlExpression2033);
                            whileExpression119=whileExpression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, whileExpression119.getTree());

                            }
                            break;

                    }

                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:331:80: ( whereExpression )?
                    int alt36=2;
                    int LA36_0 = input.LA(1);

                    if ( (LA36_0==WHERE) ) {
                        alt36=1;
                    }
                    switch (alt36) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:331:80: whereExpression
                            {
                            pushFollow(FOLLOW_whereExpression_in_controlExpression2036);
                            whereExpression120=whereExpression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, whereExpression120.getTree());

                            }
                            break;

                    }

                    DO121=(Token)match(input,DO,FOLLOW_DO_in_controlExpression2039); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DO121_tree = (CgsuiteTree)adaptor.create(DO121);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(DO121_tree, root_0);
                    }
                    pushFollow(FOLLOW_statementSequence_in_controlExpression2042);
                    statementSequence122=statementSequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence122.getTree());
                    END123=(Token)match(input,END,FOLLOW_END_in_controlExpression2044); if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:332:4: FOR expression IN expression DO statementSequence END
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    FOR124=(Token)match(input,FOR,FOLLOW_FOR_in_controlExpression2050); if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_controlExpression2053);
                    expression125=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression125.getTree());
                    IN126=(Token)match(input,IN,FOLLOW_IN_in_controlExpression2055); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IN126_tree = (CgsuiteTree)adaptor.create(IN126);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(IN126_tree, root_0);
                    }
                    pushFollow(FOLLOW_expression_in_controlExpression2058);
                    expression127=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression127.getTree());
                    DO128=(Token)match(input,DO,FOLLOW_DO_in_controlExpression2060); if (state.failed) return retval;
                    pushFollow(FOLLOW_statementSequence_in_controlExpression2063);
                    statementSequence129=statementSequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence129.getTree());
                    END130=(Token)match(input,END,FOLLOW_END_in_controlExpression2065); if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:333:4: orExpression
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_orExpression_in_controlExpression2071);
                    orExpression131=orExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, orExpression131.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:336:1: forExpression : FOR IDENTIFIER ;
    public final CgsuiteParser.forExpression_return forExpression() throws RecognitionException {
        CgsuiteParser.forExpression_return retval = new CgsuiteParser.forExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token FOR132=null;
        Token IDENTIFIER133=null;

        CgsuiteTree FOR132_tree=null;
        CgsuiteTree IDENTIFIER133_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:337:2: ( FOR IDENTIFIER )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:337:4: FOR IDENTIFIER
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            FOR132=(Token)match(input,FOR,FOLLOW_FOR_in_forExpression2082); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FOR132_tree = (CgsuiteTree)adaptor.create(FOR132);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(FOR132_tree, root_0);
            }
            IDENTIFIER133=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_forExpression2085); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER133_tree = (CgsuiteTree)adaptor.create(IDENTIFIER133);
            adaptor.addChild(root_0, IDENTIFIER133_tree);
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:340:1: fromExpression : FROM expression ;
    public final CgsuiteParser.fromExpression_return fromExpression() throws RecognitionException {
        CgsuiteParser.fromExpression_return retval = new CgsuiteParser.fromExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token FROM134=null;
        CgsuiteParser.expression_return expression135 = null;


        CgsuiteTree FROM134_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:341:2: ( FROM expression )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:341:4: FROM expression
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            FROM134=(Token)match(input,FROM,FOLLOW_FROM_in_fromExpression2097); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FROM134_tree = (CgsuiteTree)adaptor.create(FROM134);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(FROM134_tree, root_0);
            }
            pushFollow(FOLLOW_expression_in_fromExpression2100);
            expression135=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression135.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:344:1: toExpression : TO expression ;
    public final CgsuiteParser.toExpression_return toExpression() throws RecognitionException {
        CgsuiteParser.toExpression_return retval = new CgsuiteParser.toExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token TO136=null;
        CgsuiteParser.expression_return expression137 = null;


        CgsuiteTree TO136_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:345:5: ( TO expression )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:345:7: TO expression
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            TO136=(Token)match(input,TO,FOLLOW_TO_in_toExpression2115); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            TO136_tree = (CgsuiteTree)adaptor.create(TO136);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(TO136_tree, root_0);
            }
            pushFollow(FOLLOW_expression_in_toExpression2118);
            expression137=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression137.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:348:1: byExpression : BY expression ;
    public final CgsuiteParser.byExpression_return byExpression() throws RecognitionException {
        CgsuiteParser.byExpression_return retval = new CgsuiteParser.byExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token BY138=null;
        CgsuiteParser.expression_return expression139 = null;


        CgsuiteTree BY138_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:349:5: ( BY expression )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:349:7: BY expression
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            BY138=(Token)match(input,BY,FOLLOW_BY_in_byExpression2135); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            BY138_tree = (CgsuiteTree)adaptor.create(BY138);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(BY138_tree, root_0);
            }
            pushFollow(FOLLOW_expression_in_byExpression2138);
            expression139=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression139.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:352:1: whileExpression : WHILE expression ;
    public final CgsuiteParser.whileExpression_return whileExpression() throws RecognitionException {
        CgsuiteParser.whileExpression_return retval = new CgsuiteParser.whileExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token WHILE140=null;
        CgsuiteParser.expression_return expression141 = null;


        CgsuiteTree WHILE140_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:353:5: ( WHILE expression )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:353:7: WHILE expression
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            WHILE140=(Token)match(input,WHILE,FOLLOW_WHILE_in_whileExpression2156); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            WHILE140_tree = (CgsuiteTree)adaptor.create(WHILE140);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(WHILE140_tree, root_0);
            }
            pushFollow(FOLLOW_expression_in_whileExpression2159);
            expression141=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression141.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:356:1: whereExpression : WHERE expression ;
    public final CgsuiteParser.whereExpression_return whereExpression() throws RecognitionException {
        CgsuiteParser.whereExpression_return retval = new CgsuiteParser.whereExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token WHERE142=null;
        CgsuiteParser.expression_return expression143 = null;


        CgsuiteTree WHERE142_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:357:5: ( WHERE expression )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:357:7: WHERE expression
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            WHERE142=(Token)match(input,WHERE,FOLLOW_WHERE_in_whereExpression2173); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            WHERE142_tree = (CgsuiteTree)adaptor.create(WHERE142);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(WHERE142_tree, root_0);
            }
            pushFollow(FOLLOW_expression_in_whereExpression2176);
            expression143=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression143.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:360:1: elseifClause : ( ELSEIF expression THEN statementSequence ( elseifClause )? | ELSE statementSequence );
    public final CgsuiteParser.elseifClause_return elseifClause() throws RecognitionException {
        CgsuiteParser.elseifClause_return retval = new CgsuiteParser.elseifClause_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token ELSEIF144=null;
        Token THEN146=null;
        Token ELSE149=null;
        CgsuiteParser.expression_return expression145 = null;

        CgsuiteParser.statementSequence_return statementSequence147 = null;

        CgsuiteParser.elseifClause_return elseifClause148 = null;

        CgsuiteParser.statementSequence_return statementSequence150 = null;


        CgsuiteTree ELSEIF144_tree=null;
        CgsuiteTree THEN146_tree=null;
        CgsuiteTree ELSE149_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:361:2: ( ELSEIF expression THEN statementSequence ( elseifClause )? | ELSE statementSequence )
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==ELSEIF) ) {
                alt39=1;
            }
            else if ( (LA39_0==ELSE) ) {
                alt39=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }
            switch (alt39) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:361:4: ELSEIF expression THEN statementSequence ( elseifClause )?
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    ELSEIF144=(Token)match(input,ELSEIF,FOLLOW_ELSEIF_in_elseifClause2190); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ELSEIF144_tree = (CgsuiteTree)adaptor.create(ELSEIF144);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(ELSEIF144_tree, root_0);
                    }
                    pushFollow(FOLLOW_expression_in_elseifClause2193);
                    expression145=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression145.getTree());
                    THEN146=(Token)match(input,THEN,FOLLOW_THEN_in_elseifClause2195); if (state.failed) return retval;
                    pushFollow(FOLLOW_statementSequence_in_elseifClause2198);
                    statementSequence147=statementSequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence147.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:361:47: ( elseifClause )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);

                    if ( ((LA38_0>=ELSE && LA38_0<=ELSEIF)) ) {
                        alt38=1;
                    }
                    switch (alt38) {
                        case 1 :
                            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:361:47: elseifClause
                            {
                            pushFollow(FOLLOW_elseifClause_in_elseifClause2200);
                            elseifClause148=elseifClause();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, elseifClause148.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:362:4: ELSE statementSequence
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    ELSE149=(Token)match(input,ELSE,FOLLOW_ELSE_in_elseifClause2206); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ELSE149_tree = (CgsuiteTree)adaptor.create(ELSE149);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(ELSE149_tree, root_0);
                    }
                    pushFollow(FOLLOW_statementSequence_in_elseifClause2209);
                    statementSequence150=statementSequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence150.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:365:1: orExpression : andExpression ( OR orExpression )? ;
    public final CgsuiteParser.orExpression_return orExpression() throws RecognitionException {
        CgsuiteParser.orExpression_return retval = new CgsuiteParser.orExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token OR152=null;
        CgsuiteParser.andExpression_return andExpression151 = null;

        CgsuiteParser.orExpression_return orExpression153 = null;


        CgsuiteTree OR152_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:366:2: ( andExpression ( OR orExpression )? )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:366:4: andExpression ( OR orExpression )?
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_andExpression_in_orExpression2220);
            andExpression151=andExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression151.getTree());
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:366:18: ( OR orExpression )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==OR) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:366:19: OR orExpression
                    {
                    OR152=(Token)match(input,OR,FOLLOW_OR_in_orExpression2223); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OR152_tree = (CgsuiteTree)adaptor.create(OR152);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(OR152_tree, root_0);
                    }
                    pushFollow(FOLLOW_orExpression_in_orExpression2226);
                    orExpression153=orExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, orExpression153.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:369:1: andExpression : notExpr ( AND andExpression )? ;
    public final CgsuiteParser.andExpression_return andExpression() throws RecognitionException {
        CgsuiteParser.andExpression_return retval = new CgsuiteParser.andExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token AND155=null;
        CgsuiteParser.notExpr_return notExpr154 = null;

        CgsuiteParser.andExpression_return andExpression156 = null;


        CgsuiteTree AND155_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:370:2: ( notExpr ( AND andExpression )? )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:370:4: notExpr ( AND andExpression )?
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_notExpr_in_andExpression2239);
            notExpr154=notExpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, notExpr154.getTree());
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:370:12: ( AND andExpression )?
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==AND) ) {
                alt41=1;
            }
            switch (alt41) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:370:13: AND andExpression
                    {
                    AND155=(Token)match(input,AND,FOLLOW_AND_in_andExpression2242); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    AND155_tree = (CgsuiteTree)adaptor.create(AND155);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(AND155_tree, root_0);
                    }
                    pushFollow(FOLLOW_andExpression_in_andExpression2245);
                    andExpression156=andExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression156.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:373:1: notExpr : ( NOT notExpr | relationalExpr );
    public final CgsuiteParser.notExpr_return notExpr() throws RecognitionException {
        CgsuiteParser.notExpr_return retval = new CgsuiteParser.notExpr_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token NOT157=null;
        CgsuiteParser.notExpr_return notExpr158 = null;

        CgsuiteParser.relationalExpr_return relationalExpr159 = null;


        CgsuiteTree NOT157_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:374:5: ( NOT notExpr | relationalExpr )
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==NOT) ) {
                alt42=1;
            }
            else if ( ((LA42_0>=PLUS && LA42_0<=AST)||LA42_0==LPAREN||LA42_0==LBRACKET||LA42_0==LBRACE||(LA42_0>=CARET && LA42_0<=VEEVEE)||LA42_0==BEGIN||LA42_0==FALSE||LA42_0==NIL||LA42_0==THIS||LA42_0==TRUE||(LA42_0>=IDENTIFIER && LA42_0<=CHAR)) ) {
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
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:374:7: NOT notExpr
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    NOT157=(Token)match(input,NOT,FOLLOW_NOT_in_notExpr2261); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NOT157_tree = (CgsuiteTree)adaptor.create(NOT157);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(NOT157_tree, root_0);
                    }
                    pushFollow(FOLLOW_notExpr_in_notExpr2264);
                    notExpr158=notExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, notExpr158.getTree());

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:375:7: relationalExpr
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_relationalExpr_in_notExpr2272);
                    relationalExpr159=relationalExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpr159.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:378:1: relationalExpr : addExpr ( relationalToken relationalExpr )? ;
    public final CgsuiteParser.relationalExpr_return relationalExpr() throws RecognitionException {
        CgsuiteParser.relationalExpr_return retval = new CgsuiteParser.relationalExpr_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        CgsuiteParser.addExpr_return addExpr160 = null;

        CgsuiteParser.relationalToken_return relationalToken161 = null;

        CgsuiteParser.relationalExpr_return relationalExpr162 = null;



        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:379:2: ( addExpr ( relationalToken relationalExpr )? )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:379:4: addExpr ( relationalToken relationalExpr )?
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_addExpr_in_relationalExpr2286);
            addExpr160=addExpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, addExpr160.getTree());
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:379:12: ( relationalToken relationalExpr )?
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( ((LA43_0>=EQUALS && LA43_0<=COMPARE)||(LA43_0>=REFEQUALS && LA43_0<=REFNEQ)) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:379:13: relationalToken relationalExpr
                    {
                    pushFollow(FOLLOW_relationalToken_in_relationalExpr2289);
                    relationalToken161=relationalToken();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (CgsuiteTree)adaptor.becomeRoot(relationalToken161.getTree(), root_0);
                    pushFollow(FOLLOW_relationalExpr_in_relationalExpr2292);
                    relationalExpr162=relationalExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpr162.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:382:1: relationalToken : ( REFEQUALS | REFNEQ | standardRelationalToken );
    public final CgsuiteParser.relationalToken_return relationalToken() throws RecognitionException {
        CgsuiteParser.relationalToken_return retval = new CgsuiteParser.relationalToken_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token REFEQUALS163=null;
        Token REFNEQ164=null;
        CgsuiteParser.standardRelationalToken_return standardRelationalToken165 = null;


        CgsuiteTree REFEQUALS163_tree=null;
        CgsuiteTree REFNEQ164_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:383:2: ( REFEQUALS | REFNEQ | standardRelationalToken )
            int alt44=3;
            switch ( input.LA(1) ) {
            case REFEQUALS:
                {
                alt44=1;
                }
                break;
            case REFNEQ:
                {
                alt44=2;
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
                alt44=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 44, 0, input);

                throw nvae;
            }

            switch (alt44) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:383:4: REFEQUALS
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    REFEQUALS163=(Token)match(input,REFEQUALS,FOLLOW_REFEQUALS_in_relationalToken2305); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    REFEQUALS163_tree = (CgsuiteTree)adaptor.create(REFEQUALS163);
                    adaptor.addChild(root_0, REFEQUALS163_tree);
                    }

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:384:4: REFNEQ
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    REFNEQ164=(Token)match(input,REFNEQ,FOLLOW_REFNEQ_in_relationalToken2310); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    REFNEQ164_tree = (CgsuiteTree)adaptor.create(REFNEQ164);
                    adaptor.addChild(root_0, REFNEQ164_tree);
                    }

                    }
                    break;
                case 3 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:385:4: standardRelationalToken
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_standardRelationalToken_in_relationalToken2315);
                    standardRelationalToken165=standardRelationalToken();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, standardRelationalToken165.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:388:1: standardRelationalToken : ( EQUALS | NEQ | LT | GT | LEQ | GEQ | CONFUSED | COMPARE );
    public final CgsuiteParser.standardRelationalToken_return standardRelationalToken() throws RecognitionException {
        CgsuiteParser.standardRelationalToken_return retval = new CgsuiteParser.standardRelationalToken_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token set166=null;

        CgsuiteTree set166_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:389:5: ( EQUALS | NEQ | LT | GT | LEQ | GEQ | CONFUSED | COMPARE )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            set166=(Token)input.LT(1);
            if ( (input.LA(1)>=EQUALS && input.LA(1)<=COMPARE) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (CgsuiteTree)adaptor.create(set166));
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:399:1: addExpr : multiplyExpr ( ( PLUS | MINUS ) multiplyExpr )* ;
    public final CgsuiteParser.addExpr_return addExpr() throws RecognitionException {
        CgsuiteParser.addExpr_return retval = new CgsuiteParser.addExpr_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token PLUS168=null;
        Token MINUS169=null;
        CgsuiteParser.multiplyExpr_return multiplyExpr167 = null;

        CgsuiteParser.multiplyExpr_return multiplyExpr170 = null;


        CgsuiteTree PLUS168_tree=null;
        CgsuiteTree MINUS169_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:400:2: ( multiplyExpr ( ( PLUS | MINUS ) multiplyExpr )* )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:400:4: multiplyExpr ( ( PLUS | MINUS ) multiplyExpr )*
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_multiplyExpr_in_addExpr2379);
            multiplyExpr167=multiplyExpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplyExpr167.getTree());
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:400:17: ( ( PLUS | MINUS ) multiplyExpr )*
            loop46:
            do {
                int alt46=2;
                int LA46_0 = input.LA(1);

                if ( ((LA46_0>=PLUS && LA46_0<=MINUS)) ) {
                    alt46=1;
                }


                switch (alt46) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:400:18: ( PLUS | MINUS ) multiplyExpr
            	    {
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:400:18: ( PLUS | MINUS )
            	    int alt45=2;
            	    int LA45_0 = input.LA(1);

            	    if ( (LA45_0==PLUS) ) {
            	        alt45=1;
            	    }
            	    else if ( (LA45_0==MINUS) ) {
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
            	            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:400:19: PLUS
            	            {
            	            PLUS168=(Token)match(input,PLUS,FOLLOW_PLUS_in_addExpr2383); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            PLUS168_tree = (CgsuiteTree)adaptor.create(PLUS168);
            	            root_0 = (CgsuiteTree)adaptor.becomeRoot(PLUS168_tree, root_0);
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:400:27: MINUS
            	            {
            	            MINUS169=(Token)match(input,MINUS,FOLLOW_MINUS_in_addExpr2388); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            MINUS169_tree = (CgsuiteTree)adaptor.create(MINUS169);
            	            root_0 = (CgsuiteTree)adaptor.becomeRoot(MINUS169_tree, root_0);
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_multiplyExpr_in_addExpr2392);
            	    multiplyExpr170=multiplyExpr();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplyExpr170.getTree());

            	    }
            	    break;

            	default :
            	    break loop46;
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:403:1: multiplyExpr : expExpr ( ( AST | FSLASH | PERCENT ) expExpr )* ;
    public final CgsuiteParser.multiplyExpr_return multiplyExpr() throws RecognitionException {
        CgsuiteParser.multiplyExpr_return retval = new CgsuiteParser.multiplyExpr_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token AST172=null;
        Token FSLASH173=null;
        Token PERCENT174=null;
        CgsuiteParser.expExpr_return expExpr171 = null;

        CgsuiteParser.expExpr_return expExpr175 = null;


        CgsuiteTree AST172_tree=null;
        CgsuiteTree FSLASH173_tree=null;
        CgsuiteTree PERCENT174_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:404:2: ( expExpr ( ( AST | FSLASH | PERCENT ) expExpr )* )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:404:4: expExpr ( ( AST | FSLASH | PERCENT ) expExpr )*
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_expExpr_in_multiplyExpr2406);
            expExpr171=expExpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expExpr171.getTree());
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:404:12: ( ( AST | FSLASH | PERCENT ) expExpr )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( ((LA48_0>=AST && LA48_0<=FSLASH)||LA48_0==PERCENT) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:404:13: ( AST | FSLASH | PERCENT ) expExpr
            	    {
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:404:13: ( AST | FSLASH | PERCENT )
            	    int alt47=3;
            	    switch ( input.LA(1) ) {
            	    case AST:
            	        {
            	        alt47=1;
            	        }
            	        break;
            	    case FSLASH:
            	        {
            	        alt47=2;
            	        }
            	        break;
            	    case PERCENT:
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
            	            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:404:14: AST
            	            {
            	            AST172=(Token)match(input,AST,FOLLOW_AST_in_multiplyExpr2410); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            AST172_tree = (CgsuiteTree)adaptor.create(AST172);
            	            root_0 = (CgsuiteTree)adaptor.becomeRoot(AST172_tree, root_0);
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:404:21: FSLASH
            	            {
            	            FSLASH173=(Token)match(input,FSLASH,FOLLOW_FSLASH_in_multiplyExpr2415); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            FSLASH173_tree = (CgsuiteTree)adaptor.create(FSLASH173);
            	            root_0 = (CgsuiteTree)adaptor.becomeRoot(FSLASH173_tree, root_0);
            	            }

            	            }
            	            break;
            	        case 3 :
            	            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:404:31: PERCENT
            	            {
            	            PERCENT174=(Token)match(input,PERCENT,FOLLOW_PERCENT_in_multiplyExpr2420); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            PERCENT174_tree = (CgsuiteTree)adaptor.create(PERCENT174);
            	            root_0 = (CgsuiteTree)adaptor.becomeRoot(PERCENT174_tree, root_0);
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_expExpr_in_multiplyExpr2424);
            	    expExpr175=expExpr();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expExpr175.getTree());

            	    }
            	    break;

            	default :
            	    break loop48;
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:407:1: expExpr : plusminusExpr ( EXP plusminusExpr )? ;
    public final CgsuiteParser.expExpr_return expExpr() throws RecognitionException {
        CgsuiteParser.expExpr_return retval = new CgsuiteParser.expExpr_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token EXP177=null;
        CgsuiteParser.plusminusExpr_return plusminusExpr176 = null;

        CgsuiteParser.plusminusExpr_return plusminusExpr178 = null;


        CgsuiteTree EXP177_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:408:2: ( plusminusExpr ( EXP plusminusExpr )? )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:408:4: plusminusExpr ( EXP plusminusExpr )?
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_plusminusExpr_in_expExpr2437);
            plusminusExpr176=plusminusExpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, plusminusExpr176.getTree());
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:408:18: ( EXP plusminusExpr )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==EXP) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:408:19: EXP plusminusExpr
                    {
                    EXP177=(Token)match(input,EXP,FOLLOW_EXP_in_expExpr2440); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EXP177_tree = (CgsuiteTree)adaptor.create(EXP177);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(EXP177_tree, root_0);
                    }
                    pushFollow(FOLLOW_plusminusExpr_in_expExpr2443);
                    plusminusExpr178=plusminusExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, plusminusExpr178.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:411:1: plusminusExpr options {backtrack=true; memoize=true; } : ( PLUSMINUS LPAREN expression ( COMMA expression )* RPAREN -> ^( PLUSMINUS ( expression )* ) | PLUSMINUS unaryExpr -> ^( PLUSMINUS unaryExpr ) | unaryExpr );
    public final CgsuiteParser.plusminusExpr_return plusminusExpr() throws RecognitionException {
        CgsuiteParser.plusminusExpr_return retval = new CgsuiteParser.plusminusExpr_return();
        retval.start = input.LT(1);
        int plusminusExpr_StartIndex = input.index();
        CgsuiteTree root_0 = null;

        Token PLUSMINUS179=null;
        Token LPAREN180=null;
        Token COMMA182=null;
        Token RPAREN184=null;
        Token PLUSMINUS185=null;
        CgsuiteParser.expression_return expression181 = null;

        CgsuiteParser.expression_return expression183 = null;

        CgsuiteParser.unaryExpr_return unaryExpr186 = null;

        CgsuiteParser.unaryExpr_return unaryExpr187 = null;


        CgsuiteTree PLUSMINUS179_tree=null;
        CgsuiteTree LPAREN180_tree=null;
        CgsuiteTree COMMA182_tree=null;
        CgsuiteTree RPAREN184_tree=null;
        CgsuiteTree PLUSMINUS185_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_PLUSMINUS=new RewriteRuleTokenStream(adaptor,"token PLUSMINUS");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_unaryExpr=new RewriteRuleSubtreeStream(adaptor,"rule unaryExpr");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 43) ) { return retval; }
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:417:5: ( PLUSMINUS LPAREN expression ( COMMA expression )* RPAREN -> ^( PLUSMINUS ( expression )* ) | PLUSMINUS unaryExpr -> ^( PLUSMINUS unaryExpr ) | unaryExpr )
            int alt51=3;
            alt51 = dfa51.predict(input);
            switch (alt51) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:417:7: PLUSMINUS LPAREN expression ( COMMA expression )* RPAREN
                    {
                    PLUSMINUS179=(Token)match(input,PLUSMINUS,FOLLOW_PLUSMINUS_in_plusminusExpr2486); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PLUSMINUS.add(PLUSMINUS179);

                    LPAREN180=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_plusminusExpr2488); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN180);

                    pushFollow(FOLLOW_expression_in_plusminusExpr2490);
                    expression181=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression181.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:417:35: ( COMMA expression )*
                    loop50:
                    do {
                        int alt50=2;
                        int LA50_0 = input.LA(1);

                        if ( (LA50_0==COMMA) ) {
                            alt50=1;
                        }


                        switch (alt50) {
                    	case 1 :
                    	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:417:36: COMMA expression
                    	    {
                    	    COMMA182=(Token)match(input,COMMA,FOLLOW_COMMA_in_plusminusExpr2493); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA182);

                    	    pushFollow(FOLLOW_expression_in_plusminusExpr2495);
                    	    expression183=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expression.add(expression183.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop50;
                        }
                    } while (true);

                    RPAREN184=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_plusminusExpr2499); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN184);



                    // AST REWRITE
                    // elements: PLUSMINUS, expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CgsuiteTree)adaptor.nil();
                    // 417:62: -> ^( PLUSMINUS ( expression )* )
                    {
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:417:65: ^( PLUSMINUS ( expression )* )
                        {
                        CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                        root_1 = (CgsuiteTree)adaptor.becomeRoot(stream_PLUSMINUS.nextNode(), root_1);

                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:417:77: ( expression )*
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
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:418:7: PLUSMINUS unaryExpr
                    {
                    PLUSMINUS185=(Token)match(input,PLUSMINUS,FOLLOW_PLUSMINUS_in_plusminusExpr2516); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PLUSMINUS.add(PLUSMINUS185);

                    pushFollow(FOLLOW_unaryExpr_in_plusminusExpr2518);
                    unaryExpr186=unaryExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_unaryExpr.add(unaryExpr186.getTree());


                    // AST REWRITE
                    // elements: PLUSMINUS, unaryExpr
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CgsuiteTree)adaptor.nil();
                    // 418:27: -> ^( PLUSMINUS unaryExpr )
                    {
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:418:30: ^( PLUSMINUS unaryExpr )
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
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:419:7: unaryExpr
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_unaryExpr_in_plusminusExpr2534);
                    unaryExpr187=unaryExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpr187.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 43, plusminusExpr_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "plusminusExpr"

    public static class unaryExpr_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unaryExpr"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:422:1: unaryExpr : ( MINUS unaryExpr -> ^( UNARY_MINUS unaryExpr ) | PLUS unaryExpr -> ^( UNARY_PLUS unaryExpr ) | postfixExpr );
    public final CgsuiteParser.unaryExpr_return unaryExpr() throws RecognitionException {
        CgsuiteParser.unaryExpr_return retval = new CgsuiteParser.unaryExpr_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token MINUS188=null;
        Token PLUS190=null;
        CgsuiteParser.unaryExpr_return unaryExpr189 = null;

        CgsuiteParser.unaryExpr_return unaryExpr191 = null;

        CgsuiteParser.postfixExpr_return postfixExpr192 = null;


        CgsuiteTree MINUS188_tree=null;
        CgsuiteTree PLUS190_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_MINUS=new RewriteRuleTokenStream(adaptor,"token MINUS");
        RewriteRuleSubtreeStream stream_unaryExpr=new RewriteRuleSubtreeStream(adaptor,"rule unaryExpr");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:423:2: ( MINUS unaryExpr -> ^( UNARY_MINUS unaryExpr ) | PLUS unaryExpr -> ^( UNARY_PLUS unaryExpr ) | postfixExpr )
            int alt52=3;
            switch ( input.LA(1) ) {
            case MINUS:
                {
                alt52=1;
                }
                break;
            case PLUS:
                {
                alt52=2;
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
                alt52=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 52, 0, input);

                throw nvae;
            }

            switch (alt52) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:423:4: MINUS unaryExpr
                    {
                    MINUS188=(Token)match(input,MINUS,FOLLOW_MINUS_in_unaryExpr2548); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_MINUS.add(MINUS188);

                    pushFollow(FOLLOW_unaryExpr_in_unaryExpr2550);
                    unaryExpr189=unaryExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_unaryExpr.add(unaryExpr189.getTree());


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
                    // 423:20: -> ^( UNARY_MINUS unaryExpr )
                    {
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:423:23: ^( UNARY_MINUS unaryExpr )
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
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:424:7: PLUS unaryExpr
                    {
                    PLUS190=(Token)match(input,PLUS,FOLLOW_PLUS_in_unaryExpr2566); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PLUS.add(PLUS190);

                    pushFollow(FOLLOW_unaryExpr_in_unaryExpr2568);
                    unaryExpr191=unaryExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_unaryExpr.add(unaryExpr191.getTree());


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
                    // 424:22: -> ^( UNARY_PLUS unaryExpr )
                    {
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:424:25: ^( UNARY_PLUS unaryExpr )
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
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:425:7: postfixExpr
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_postfixExpr_in_unaryExpr2584);
                    postfixExpr192=postfixExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, postfixExpr192.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:428:1: postfixExpr : ( upstarExpr -> upstarExpr ) ( DOT IDENTIFIER -> ^( DOT $postfixExpr IDENTIFIER ) | x= arrayReference -> ^( ARRAY_REFERENCE[((CgsuiteTree) x.getTree()).getToken()] $postfixExpr arrayReference ) | y= functionCall -> ^( FUNCTION_CALL[((CgsuiteTree) y.getTree()).getToken()] $postfixExpr functionCall ) )* ;
    public final CgsuiteParser.postfixExpr_return postfixExpr() throws RecognitionException {
        CgsuiteParser.postfixExpr_return retval = new CgsuiteParser.postfixExpr_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token DOT194=null;
        Token IDENTIFIER195=null;
        CgsuiteParser.arrayReference_return x = null;

        CgsuiteParser.functionCall_return y = null;

        CgsuiteParser.upstarExpr_return upstarExpr193 = null;


        CgsuiteTree DOT194_tree=null;
        CgsuiteTree IDENTIFIER195_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleSubtreeStream stream_functionCall=new RewriteRuleSubtreeStream(adaptor,"rule functionCall");
        RewriteRuleSubtreeStream stream_upstarExpr=new RewriteRuleSubtreeStream(adaptor,"rule upstarExpr");
        RewriteRuleSubtreeStream stream_arrayReference=new RewriteRuleSubtreeStream(adaptor,"rule arrayReference");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:429:2: ( ( upstarExpr -> upstarExpr ) ( DOT IDENTIFIER -> ^( DOT $postfixExpr IDENTIFIER ) | x= arrayReference -> ^( ARRAY_REFERENCE[((CgsuiteTree) x.getTree()).getToken()] $postfixExpr arrayReference ) | y= functionCall -> ^( FUNCTION_CALL[((CgsuiteTree) y.getTree()).getToken()] $postfixExpr functionCall ) )* )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:429:4: ( upstarExpr -> upstarExpr ) ( DOT IDENTIFIER -> ^( DOT $postfixExpr IDENTIFIER ) | x= arrayReference -> ^( ARRAY_REFERENCE[((CgsuiteTree) x.getTree()).getToken()] $postfixExpr arrayReference ) | y= functionCall -> ^( FUNCTION_CALL[((CgsuiteTree) y.getTree()).getToken()] $postfixExpr functionCall ) )*
            {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:429:4: ( upstarExpr -> upstarExpr )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:429:5: upstarExpr
            {
            pushFollow(FOLLOW_upstarExpr_in_postfixExpr2597);
            upstarExpr193=upstarExpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_upstarExpr.add(upstarExpr193.getTree());


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
            // 429:16: -> upstarExpr
            {
                adaptor.addChild(root_0, stream_upstarExpr.nextTree());

            }

            retval.tree = root_0;}
            }

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:430:4: ( DOT IDENTIFIER -> ^( DOT $postfixExpr IDENTIFIER ) | x= arrayReference -> ^( ARRAY_REFERENCE[((CgsuiteTree) x.getTree()).getToken()] $postfixExpr arrayReference ) | y= functionCall -> ^( FUNCTION_CALL[((CgsuiteTree) y.getTree()).getToken()] $postfixExpr functionCall ) )*
            loop53:
            do {
                int alt53=4;
                switch ( input.LA(1) ) {
                case DOT:
                    {
                    alt53=1;
                    }
                    break;
                case LBRACKET:
                    {
                    alt53=2;
                    }
                    break;
                case LPAREN:
                    {
                    alt53=3;
                    }
                    break;

                }

                switch (alt53) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:430:6: DOT IDENTIFIER
            	    {
            	    DOT194=(Token)match(input,DOT,FOLLOW_DOT_in_postfixExpr2609); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(DOT194);

            	    IDENTIFIER195=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_postfixExpr2611); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER195);



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
            	    // 430:22: -> ^( DOT $postfixExpr IDENTIFIER )
            	    {
            	        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:430:25: ^( DOT $postfixExpr IDENTIFIER )
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
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:431:6: x= arrayReference
            	    {
            	    pushFollow(FOLLOW_arrayReference_in_postfixExpr2632);
            	    x=arrayReference();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_arrayReference.add(x.getTree());


            	    // AST REWRITE
            	    // elements: postfixExpr, arrayReference
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (CgsuiteTree)adaptor.nil();
            	    // 431:22: -> ^( ARRAY_REFERENCE[((CgsuiteTree) x.getTree()).getToken()] $postfixExpr arrayReference )
            	    {
            	        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:431:25: ^( ARRAY_REFERENCE[((CgsuiteTree) x.getTree()).getToken()] $postfixExpr arrayReference )
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
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:432:6: y= functionCall
            	    {
            	    pushFollow(FOLLOW_functionCall_in_postfixExpr2652);
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
            	    // 432:21: -> ^( FUNCTION_CALL[((CgsuiteTree) y.getTree()).getToken()] $postfixExpr functionCall )
            	    {
            	        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:432:24: ^( FUNCTION_CALL[((CgsuiteTree) y.getTree()).getToken()] $postfixExpr functionCall )
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
            	    break loop53;
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:436:1: arrayReference : LBRACKET expression ( COMMA expression )* RBRACKET -> ^( ARRAY_INDEX_LIST[$LBRACKET] ( expression )* ) ;
    public final CgsuiteParser.arrayReference_return arrayReference() throws RecognitionException {
        CgsuiteParser.arrayReference_return retval = new CgsuiteParser.arrayReference_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token LBRACKET196=null;
        Token COMMA198=null;
        Token RBRACKET200=null;
        CgsuiteParser.expression_return expression197 = null;

        CgsuiteParser.expression_return expression199 = null;


        CgsuiteTree LBRACKET196_tree=null;
        CgsuiteTree COMMA198_tree=null;
        CgsuiteTree RBRACKET200_tree=null;
        RewriteRuleTokenStream stream_LBRACKET=new RewriteRuleTokenStream(adaptor,"token LBRACKET");
        RewriteRuleTokenStream stream_RBRACKET=new RewriteRuleTokenStream(adaptor,"token RBRACKET");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:437:2: ( LBRACKET expression ( COMMA expression )* RBRACKET -> ^( ARRAY_INDEX_LIST[$LBRACKET] ( expression )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:437:4: LBRACKET expression ( COMMA expression )* RBRACKET
            {
            LBRACKET196=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayReference2683); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LBRACKET.add(LBRACKET196);

            pushFollow(FOLLOW_expression_in_arrayReference2685);
            expression197=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expression.add(expression197.getTree());
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:437:24: ( COMMA expression )*
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);

                if ( (LA54_0==COMMA) ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:437:25: COMMA expression
            	    {
            	    COMMA198=(Token)match(input,COMMA,FOLLOW_COMMA_in_arrayReference2688); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA198);

            	    pushFollow(FOLLOW_expression_in_arrayReference2690);
            	    expression199=expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_expression.add(expression199.getTree());

            	    }
            	    break;

            	default :
            	    break loop54;
                }
            } while (true);

            RBRACKET200=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayReference2694); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RBRACKET.add(RBRACKET200);



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
            // 438:7: -> ^( ARRAY_INDEX_LIST[$LBRACKET] ( expression )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:438:10: ^( ARRAY_INDEX_LIST[$LBRACKET] ( expression )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(ARRAY_INDEX_LIST, LBRACKET196), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:438:40: ( expression )*
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:441:1: functionCall : LPAREN ( functionArgument ( COMMA functionArgument )* )? RPAREN -> ^( FUNCTION_CALL_ARGUMENT_LIST[$LPAREN] ( functionArgument )* ) ;
    public final CgsuiteParser.functionCall_return functionCall() throws RecognitionException {
        CgsuiteParser.functionCall_return retval = new CgsuiteParser.functionCall_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token LPAREN201=null;
        Token COMMA203=null;
        Token RPAREN205=null;
        CgsuiteParser.functionArgument_return functionArgument202 = null;

        CgsuiteParser.functionArgument_return functionArgument204 = null;


        CgsuiteTree LPAREN201_tree=null;
        CgsuiteTree COMMA203_tree=null;
        CgsuiteTree RPAREN205_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_functionArgument=new RewriteRuleSubtreeStream(adaptor,"rule functionArgument");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:442:2: ( LPAREN ( functionArgument ( COMMA functionArgument )* )? RPAREN -> ^( FUNCTION_CALL_ARGUMENT_LIST[$LPAREN] ( functionArgument )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:442:4: LPAREN ( functionArgument ( COMMA functionArgument )* )? RPAREN
            {
            LPAREN201=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_functionCall2722); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN201);

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:442:11: ( functionArgument ( COMMA functionArgument )* )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( ((LA56_0>=PLUS && LA56_0<=AST)||LA56_0==LPAREN||LA56_0==LBRACKET||LA56_0==LBRACE||(LA56_0>=CARET && LA56_0<=VEEVEE)||LA56_0==BEGIN||LA56_0==BY||LA56_0==DO||(LA56_0>=FALSE && LA56_0<=FOR)||LA56_0==FROM||LA56_0==IF||(LA56_0>=NIL && LA56_0<=NOT)||(LA56_0>=THIS && LA56_0<=TRUE)||(LA56_0>=WHERE && LA56_0<=WHILE)||(LA56_0>=IDENTIFIER && LA56_0<=CHAR)) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:442:12: functionArgument ( COMMA functionArgument )*
                    {
                    pushFollow(FOLLOW_functionArgument_in_functionCall2725);
                    functionArgument202=functionArgument();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_functionArgument.add(functionArgument202.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:442:29: ( COMMA functionArgument )*
                    loop55:
                    do {
                        int alt55=2;
                        int LA55_0 = input.LA(1);

                        if ( (LA55_0==COMMA) ) {
                            alt55=1;
                        }


                        switch (alt55) {
                    	case 1 :
                    	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:442:30: COMMA functionArgument
                    	    {
                    	    COMMA203=(Token)match(input,COMMA,FOLLOW_COMMA_in_functionCall2728); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA203);

                    	    pushFollow(FOLLOW_functionArgument_in_functionCall2730);
                    	    functionArgument204=functionArgument();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_functionArgument.add(functionArgument204.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop55;
                        }
                    } while (true);


                    }
                    break;

            }

            RPAREN205=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_functionCall2736); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN205);



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
            // 443:7: -> ^( FUNCTION_CALL_ARGUMENT_LIST[$LPAREN] ( functionArgument )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:443:10: ^( FUNCTION_CALL_ARGUMENT_LIST[$LPAREN] ( functionArgument )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(FUNCTION_CALL_ARGUMENT_LIST, LPAREN201), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:443:49: ( functionArgument )*
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:446:1: functionArgument : ( IDENTIFIER BIGRARROW )? expression ;
    public final CgsuiteParser.functionArgument_return functionArgument() throws RecognitionException {
        CgsuiteParser.functionArgument_return retval = new CgsuiteParser.functionArgument_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token IDENTIFIER206=null;
        Token BIGRARROW207=null;
        CgsuiteParser.expression_return expression208 = null;


        CgsuiteTree IDENTIFIER206_tree=null;
        CgsuiteTree BIGRARROW207_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:447:2: ( ( IDENTIFIER BIGRARROW )? expression )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:447:4: ( IDENTIFIER BIGRARROW )? expression
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:447:4: ( IDENTIFIER BIGRARROW )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==IDENTIFIER) ) {
                int LA57_1 = input.LA(2);

                if ( (LA57_1==BIGRARROW) ) {
                    alt57=1;
                }
            }
            switch (alt57) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:447:5: IDENTIFIER BIGRARROW
                    {
                    IDENTIFIER206=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_functionArgument2766); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER206_tree = (CgsuiteTree)adaptor.create(IDENTIFIER206);
                    adaptor.addChild(root_0, IDENTIFIER206_tree);
                    }
                    BIGRARROW207=(Token)match(input,BIGRARROW,FOLLOW_BIGRARROW_in_functionArgument2768); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BIGRARROW207_tree = (CgsuiteTree)adaptor.create(BIGRARROW207);
                    root_0 = (CgsuiteTree)adaptor.becomeRoot(BIGRARROW207_tree, root_0);
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_expression_in_functionArgument2773);
            expression208=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression208.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:450:1: upstarExpr options {backtrack=true; memoize=true; } : ( ( CARET | CARETCARET | VEE | VEEVEE ) starExpr | ( CARET | VEE ) primaryExpr starExpr | ( CARET | VEE ) primaryExpr | starExpr | CARET | CARETCARET | VEE | VEEVEE | primaryExpr );
    public final CgsuiteParser.upstarExpr_return upstarExpr() throws RecognitionException {
        CgsuiteParser.upstarExpr_return retval = new CgsuiteParser.upstarExpr_return();
        retval.start = input.LT(1);
        int upstarExpr_StartIndex = input.index();
        CgsuiteTree root_0 = null;

        Token set209=null;
        Token set211=null;
        Token set214=null;
        Token CARET217=null;
        Token CARETCARET218=null;
        Token VEE219=null;
        Token VEEVEE220=null;
        CgsuiteParser.starExpr_return starExpr210 = null;

        CgsuiteParser.primaryExpr_return primaryExpr212 = null;

        CgsuiteParser.starExpr_return starExpr213 = null;

        CgsuiteParser.primaryExpr_return primaryExpr215 = null;

        CgsuiteParser.starExpr_return starExpr216 = null;

        CgsuiteParser.primaryExpr_return primaryExpr221 = null;


        CgsuiteTree set209_tree=null;
        CgsuiteTree set211_tree=null;
        CgsuiteTree set214_tree=null;
        CgsuiteTree CARET217_tree=null;
        CgsuiteTree CARETCARET218_tree=null;
        CgsuiteTree VEE219_tree=null;
        CgsuiteTree VEEVEE220_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 49) ) { return retval; }
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:456:5: ( ( CARET | CARETCARET | VEE | VEEVEE ) starExpr | ( CARET | VEE ) primaryExpr starExpr | ( CARET | VEE ) primaryExpr | starExpr | CARET | CARETCARET | VEE | VEEVEE | primaryExpr )
            int alt58=9;
            alt58 = dfa58.predict(input);
            switch (alt58) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:456:7: ( CARET | CARETCARET | VEE | VEEVEE ) starExpr
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    set209=(Token)input.LT(1);
                    set209=(Token)input.LT(1);
                    if ( (input.LA(1)>=CARET && input.LA(1)<=VEEVEE) ) {
                        input.consume();
                        if ( state.backtracking==0 ) root_0 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(set209), root_0);
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_starExpr_in_upstarExpr2831);
                    starExpr210=starExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, starExpr210.getTree());

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:457:7: ( CARET | VEE ) primaryExpr starExpr
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    set211=(Token)input.LT(1);
                    set211=(Token)input.LT(1);
                    if ( input.LA(1)==CARET||input.LA(1)==VEE ) {
                        input.consume();
                        if ( state.backtracking==0 ) root_0 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(set211), root_0);
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_primaryExpr_in_upstarExpr2848);
                    primaryExpr212=primaryExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primaryExpr212.getTree());
                    pushFollow(FOLLOW_starExpr_in_upstarExpr2850);
                    starExpr213=starExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, starExpr213.getTree());

                    }
                    break;
                case 3 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:458:7: ( CARET | VEE ) primaryExpr
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    set214=(Token)input.LT(1);
                    set214=(Token)input.LT(1);
                    if ( input.LA(1)==CARET||input.LA(1)==VEE ) {
                        input.consume();
                        if ( state.backtracking==0 ) root_0 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(set214), root_0);
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_primaryExpr_in_upstarExpr2867);
                    primaryExpr215=primaryExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primaryExpr215.getTree());

                    }
                    break;
                case 4 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:459:7: starExpr
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_starExpr_in_upstarExpr2875);
                    starExpr216=starExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, starExpr216.getTree());

                    }
                    break;
                case 5 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:460:7: CARET
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    CARET217=(Token)match(input,CARET,FOLLOW_CARET_in_upstarExpr2883); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CARET217_tree = (CgsuiteTree)adaptor.create(CARET217);
                    adaptor.addChild(root_0, CARET217_tree);
                    }

                    }
                    break;
                case 6 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:460:15: CARETCARET
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    CARETCARET218=(Token)match(input,CARETCARET,FOLLOW_CARETCARET_in_upstarExpr2887); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CARETCARET218_tree = (CgsuiteTree)adaptor.create(CARETCARET218);
                    adaptor.addChild(root_0, CARETCARET218_tree);
                    }

                    }
                    break;
                case 7 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:460:28: VEE
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    VEE219=(Token)match(input,VEE,FOLLOW_VEE_in_upstarExpr2891); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    VEE219_tree = (CgsuiteTree)adaptor.create(VEE219);
                    adaptor.addChild(root_0, VEE219_tree);
                    }

                    }
                    break;
                case 8 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:460:34: VEEVEE
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    VEEVEE220=(Token)match(input,VEEVEE,FOLLOW_VEEVEE_in_upstarExpr2895); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    VEEVEE220_tree = (CgsuiteTree)adaptor.create(VEEVEE220);
                    adaptor.addChild(root_0, VEEVEE220_tree);
                    }

                    }
                    break;
                case 9 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:461:7: primaryExpr
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_primaryExpr_in_upstarExpr2903);
                    primaryExpr221=primaryExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primaryExpr221.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 49, upstarExpr_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "upstarExpr"

    public static class starExpr_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "starExpr"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:464:1: starExpr options {backtrack=true; memoize=true; } : ( AST primaryExpr -> ^( UNARY_AST primaryExpr ) | AST -> UNARY_AST );
    public final CgsuiteParser.starExpr_return starExpr() throws RecognitionException {
        CgsuiteParser.starExpr_return retval = new CgsuiteParser.starExpr_return();
        retval.start = input.LT(1);
        int starExpr_StartIndex = input.index();
        CgsuiteTree root_0 = null;

        Token AST222=null;
        Token AST224=null;
        CgsuiteParser.primaryExpr_return primaryExpr223 = null;


        CgsuiteTree AST222_tree=null;
        CgsuiteTree AST224_tree=null;
        RewriteRuleTokenStream stream_AST=new RewriteRuleTokenStream(adaptor,"token AST");
        RewriteRuleSubtreeStream stream_primaryExpr=new RewriteRuleSubtreeStream(adaptor,"rule primaryExpr");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 50) ) { return retval; }
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:470:5: ( AST primaryExpr -> ^( UNARY_AST primaryExpr ) | AST -> UNARY_AST )
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==AST) ) {
                int LA59_1 = input.LA(2);

                if ( (synpred11_Cgsuite()) ) {
                    alt59=1;
                }
                else if ( (true) ) {
                    alt59=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 59, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 59, 0, input);

                throw nvae;
            }
            switch (alt59) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:470:7: AST primaryExpr
                    {
                    AST222=(Token)match(input,AST,FOLLOW_AST_in_starExpr2946); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_AST.add(AST222);

                    pushFollow(FOLLOW_primaryExpr_in_starExpr2948);
                    primaryExpr223=primaryExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_primaryExpr.add(primaryExpr223.getTree());


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
                    // 470:23: -> ^( UNARY_AST primaryExpr )
                    {
                        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:470:26: ^( UNARY_AST primaryExpr )
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
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:471:7: AST
                    {
                    AST224=(Token)match(input,AST,FOLLOW_AST_in_starExpr2964); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_AST.add(AST224);



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
                    // 471:11: -> UNARY_AST
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
            if ( state.backtracking>0 ) { memoize(input, 50, starExpr_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "starExpr"

    public static class primaryExpr_return extends ParserRuleReturnScope {
        CgsuiteTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "primaryExpr"
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:474:1: primaryExpr : ( NIL | THIS | TRUE | FALSE | ( INTEGER DOTDOT )=> range | INTEGER | STRING | CHAR | IDENTIFIER | LPAREN statementSequence RPAREN | BEGIN statementSequence END | ( LBRACE expressionList SLASHES )=> explicitGame | ( LBRACE ( expression )? BIGRARROW )=> explicitMap | explicitSet | explicitList );
    public final CgsuiteParser.primaryExpr_return primaryExpr() throws RecognitionException {
        CgsuiteParser.primaryExpr_return retval = new CgsuiteParser.primaryExpr_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token NIL225=null;
        Token THIS226=null;
        Token TRUE227=null;
        Token FALSE228=null;
        Token INTEGER230=null;
        Token STRING231=null;
        Token CHAR232=null;
        Token IDENTIFIER233=null;
        Token LPAREN234=null;
        Token RPAREN236=null;
        Token BEGIN237=null;
        Token END239=null;
        CgsuiteParser.range_return range229 = null;

        CgsuiteParser.statementSequence_return statementSequence235 = null;

        CgsuiteParser.statementSequence_return statementSequence238 = null;

        CgsuiteParser.explicitGame_return explicitGame240 = null;

        CgsuiteParser.explicitMap_return explicitMap241 = null;

        CgsuiteParser.explicitSet_return explicitSet242 = null;

        CgsuiteParser.explicitList_return explicitList243 = null;


        CgsuiteTree NIL225_tree=null;
        CgsuiteTree THIS226_tree=null;
        CgsuiteTree TRUE227_tree=null;
        CgsuiteTree FALSE228_tree=null;
        CgsuiteTree INTEGER230_tree=null;
        CgsuiteTree STRING231_tree=null;
        CgsuiteTree CHAR232_tree=null;
        CgsuiteTree IDENTIFIER233_tree=null;
        CgsuiteTree LPAREN234_tree=null;
        CgsuiteTree RPAREN236_tree=null;
        CgsuiteTree BEGIN237_tree=null;
        CgsuiteTree END239_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:475:2: ( NIL | THIS | TRUE | FALSE | ( INTEGER DOTDOT )=> range | INTEGER | STRING | CHAR | IDENTIFIER | LPAREN statementSequence RPAREN | BEGIN statementSequence END | ( LBRACE expressionList SLASHES )=> explicitGame | ( LBRACE ( expression )? BIGRARROW )=> explicitMap | explicitSet | explicitList )
            int alt60=15;
            alt60 = dfa60.predict(input);
            switch (alt60) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:475:4: NIL
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    NIL225=(Token)match(input,NIL,FOLLOW_NIL_in_primaryExpr2983); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NIL225_tree = (CgsuiteTree)adaptor.create(NIL225);
                    adaptor.addChild(root_0, NIL225_tree);
                    }

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:476:4: THIS
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    THIS226=(Token)match(input,THIS,FOLLOW_THIS_in_primaryExpr2988); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    THIS226_tree = (CgsuiteTree)adaptor.create(THIS226);
                    adaptor.addChild(root_0, THIS226_tree);
                    }

                    }
                    break;
                case 3 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:477:4: TRUE
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    TRUE227=(Token)match(input,TRUE,FOLLOW_TRUE_in_primaryExpr2993); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRUE227_tree = (CgsuiteTree)adaptor.create(TRUE227);
                    adaptor.addChild(root_0, TRUE227_tree);
                    }

                    }
                    break;
                case 4 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:478:4: FALSE
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    FALSE228=(Token)match(input,FALSE,FOLLOW_FALSE_in_primaryExpr2998); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FALSE228_tree = (CgsuiteTree)adaptor.create(FALSE228);
                    adaptor.addChild(root_0, FALSE228_tree);
                    }

                    }
                    break;
                case 5 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:479:4: ( INTEGER DOTDOT )=> range
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_range_in_primaryExpr3011);
                    range229=range();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, range229.getTree());

                    }
                    break;
                case 6 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:480:4: INTEGER
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    INTEGER230=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_primaryExpr3016); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INTEGER230_tree = (CgsuiteTree)adaptor.create(INTEGER230);
                    adaptor.addChild(root_0, INTEGER230_tree);
                    }

                    }
                    break;
                case 7 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:481:4: STRING
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    STRING231=(Token)match(input,STRING,FOLLOW_STRING_in_primaryExpr3021); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING231_tree = (CgsuiteTree)adaptor.create(STRING231);
                    adaptor.addChild(root_0, STRING231_tree);
                    }

                    }
                    break;
                case 8 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:482:4: CHAR
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    CHAR232=(Token)match(input,CHAR,FOLLOW_CHAR_in_primaryExpr3026); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR232_tree = (CgsuiteTree)adaptor.create(CHAR232);
                    adaptor.addChild(root_0, CHAR232_tree);
                    }

                    }
                    break;
                case 9 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:483:4: IDENTIFIER
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    IDENTIFIER233=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primaryExpr3031); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER233_tree = (CgsuiteTree)adaptor.create(IDENTIFIER233);
                    adaptor.addChild(root_0, IDENTIFIER233_tree);
                    }

                    }
                    break;
                case 10 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:484:4: LPAREN statementSequence RPAREN
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    LPAREN234=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_primaryExpr3036); if (state.failed) return retval;
                    pushFollow(FOLLOW_statementSequence_in_primaryExpr3039);
                    statementSequence235=statementSequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence235.getTree());
                    RPAREN236=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_primaryExpr3041); if (state.failed) return retval;

                    }
                    break;
                case 11 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:485:4: BEGIN statementSequence END
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    BEGIN237=(Token)match(input,BEGIN,FOLLOW_BEGIN_in_primaryExpr3047); if (state.failed) return retval;
                    pushFollow(FOLLOW_statementSequence_in_primaryExpr3050);
                    statementSequence238=statementSequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statementSequence238.getTree());
                    END239=(Token)match(input,END,FOLLOW_END_in_primaryExpr3052); if (state.failed) return retval;

                    }
                    break;
                case 12 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:486:7: ( LBRACE expressionList SLASHES )=> explicitGame
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_explicitGame_in_primaryExpr3071);
                    explicitGame240=explicitGame();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitGame240.getTree());

                    }
                    break;
                case 13 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:487:4: ( LBRACE ( expression )? BIGRARROW )=> explicitMap
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_explicitMap_in_primaryExpr3087);
                    explicitMap241=explicitMap();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitMap241.getTree());

                    }
                    break;
                case 14 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:488:4: explicitSet
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_explicitSet_in_primaryExpr3092);
                    explicitSet242=explicitSet();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitSet242.getTree());

                    }
                    break;
                case 15 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:489:4: explicitList
                    {
                    root_0 = (CgsuiteTree)adaptor.nil();

                    pushFollow(FOLLOW_explicitList_in_primaryExpr3097);
                    explicitList243=explicitList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitList243.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:492:1: explicitGame : LBRACE slashExpression RBRACE ;
    public final CgsuiteParser.explicitGame_return explicitGame() throws RecognitionException {
        CgsuiteParser.explicitGame_return retval = new CgsuiteParser.explicitGame_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token LBRACE244=null;
        Token RBRACE246=null;
        CgsuiteParser.slashExpression_return slashExpression245 = null;


        CgsuiteTree LBRACE244_tree=null;
        CgsuiteTree RBRACE246_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:493:5: ( LBRACE slashExpression RBRACE )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:493:7: LBRACE slashExpression RBRACE
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            LBRACE244=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_explicitGame3111); if (state.failed) return retval;
            pushFollow(FOLLOW_slashExpression_in_explicitGame3114);
            slashExpression245=slashExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, slashExpression245.getTree());
            RBRACE246=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_explicitGame3116); if (state.failed) return retval;

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:496:1: slashExpression : ( ( expressionList SLASHES )=>lo= expressionList ( SLASHES ro= slashExpression ) -> | lo= expressionList -> $lo);
    public final CgsuiteParser.slashExpression_return slashExpression() throws RecognitionException {
        CgsuiteParser.slashExpression_return retval = new CgsuiteParser.slashExpression_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token SLASHES247=null;
        CgsuiteParser.expressionList_return lo = null;

        CgsuiteParser.slashExpression_return ro = null;


        CgsuiteTree SLASHES247_tree=null;
        RewriteRuleTokenStream stream_SLASHES=new RewriteRuleTokenStream(adaptor,"token SLASHES");
        RewriteRuleSubtreeStream stream_expressionList=new RewriteRuleSubtreeStream(adaptor,"rule expressionList");
        RewriteRuleSubtreeStream stream_slashExpression=new RewriteRuleSubtreeStream(adaptor,"rule slashExpression");

                CommonTree newTree = null;
            
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:501:5: ( ( expressionList SLASHES )=>lo= expressionList ( SLASHES ro= slashExpression ) -> | lo= expressionList -> $lo)
            int alt61=2;
            alt61 = dfa61.predict(input);
            switch (alt61) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:501:7: ( expressionList SLASHES )=>lo= expressionList ( SLASHES ro= slashExpression )
                    {
                    pushFollow(FOLLOW_expressionList_in_slashExpression3163);
                    lo=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expressionList.add(lo.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:502:25: ( SLASHES ro= slashExpression )
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:502:26: SLASHES ro= slashExpression
                    {
                    SLASHES247=(Token)match(input,SLASHES,FOLLOW_SLASHES_in_slashExpression3166); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SLASHES.add(SLASHES247);

                    pushFollow(FOLLOW_slashExpression_in_slashExpression3170);
                    ro=slashExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_slashExpression.add(ro.getTree());

                    }

                    if ( state.backtracking==0 ) {

                              if ((ro!=null?((CgsuiteTree)ro.tree):null).token.getType() != SLASHES ||
                                  (ro!=null?((CgsuiteTree)ro.tree):null).token.getText().length() < SLASHES247.getText().length())
                              {
                                  newTree = (CgsuiteTree) adaptor.create(SLASHES247);
                                  adaptor.addChild(newTree, (lo!=null?((CgsuiteTree)lo.tree):null));
                                  adaptor.addChild(newTree, (ro!=null?((CgsuiteTree)ro.tree):null));
                              }
                              else
                              {
                                  CommonTree t = (ro!=null?((CgsuiteTree)ro.tree):null);
                                  while (true)
                                  {
                                      if (SLASHES247.getText().length() == t.getText().length())
                                      {
                                          throw new RuntimeException("Ambiguous pattern of slashes.");
                                      }
                                      else if (t.getChild(0).getType() != SLASHES ||
                                               t.getChild(0).getText().length() < SLASHES247.getText().length())
                                      {
                                          break;
                                      }
                                      t = (CgsuiteTree) adaptor.getChild(t, 0);
                                  }
                                  CommonTree tLeft  = (CgsuiteTree) adaptor.getChild(t, 0);
                                  CommonTree tRight = (CgsuiteTree) adaptor.getChild(t, 1);
                                  CommonTree tRightNew = (CgsuiteTree) adaptor.create(SLASHES247);
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
                    // 536:7: ->
                    {
                        adaptor.addChild(root_0, newTree);

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:537:7: lo= expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_slashExpression3191);
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
                    // 537:25: -> $lo
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:540:1: explicitMap : LBRACE ( mapEntry ( COMMA mapEntry )* | BIGRARROW ) RBRACE -> ^( EXPLICIT_MAP ( mapEntry )* ) ;
    public final CgsuiteParser.explicitMap_return explicitMap() throws RecognitionException {
        CgsuiteParser.explicitMap_return retval = new CgsuiteParser.explicitMap_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token LBRACE248=null;
        Token COMMA250=null;
        Token BIGRARROW252=null;
        Token RBRACE253=null;
        CgsuiteParser.mapEntry_return mapEntry249 = null;

        CgsuiteParser.mapEntry_return mapEntry251 = null;


        CgsuiteTree LBRACE248_tree=null;
        CgsuiteTree COMMA250_tree=null;
        CgsuiteTree BIGRARROW252_tree=null;
        CgsuiteTree RBRACE253_tree=null;
        RewriteRuleTokenStream stream_RBRACE=new RewriteRuleTokenStream(adaptor,"token RBRACE");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_BIGRARROW=new RewriteRuleTokenStream(adaptor,"token BIGRARROW");
        RewriteRuleTokenStream stream_LBRACE=new RewriteRuleTokenStream(adaptor,"token LBRACE");
        RewriteRuleSubtreeStream stream_mapEntry=new RewriteRuleSubtreeStream(adaptor,"rule mapEntry");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:541:2: ( LBRACE ( mapEntry ( COMMA mapEntry )* | BIGRARROW ) RBRACE -> ^( EXPLICIT_MAP ( mapEntry )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:541:4: LBRACE ( mapEntry ( COMMA mapEntry )* | BIGRARROW ) RBRACE
            {
            LBRACE248=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_explicitMap3210); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LBRACE.add(LBRACE248);

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:541:11: ( mapEntry ( COMMA mapEntry )* | BIGRARROW )
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( ((LA63_0>=PLUS && LA63_0<=AST)||LA63_0==LPAREN||LA63_0==LBRACKET||LA63_0==LBRACE||(LA63_0>=CARET && LA63_0<=VEEVEE)||LA63_0==BEGIN||LA63_0==BY||LA63_0==DO||(LA63_0>=FALSE && LA63_0<=FOR)||LA63_0==FROM||LA63_0==IF||(LA63_0>=NIL && LA63_0<=NOT)||(LA63_0>=THIS && LA63_0<=TRUE)||(LA63_0>=WHERE && LA63_0<=WHILE)||(LA63_0>=IDENTIFIER && LA63_0<=CHAR)) ) {
                alt63=1;
            }
            else if ( (LA63_0==BIGRARROW) ) {
                alt63=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 63, 0, input);

                throw nvae;
            }
            switch (alt63) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:541:12: mapEntry ( COMMA mapEntry )*
                    {
                    pushFollow(FOLLOW_mapEntry_in_explicitMap3213);
                    mapEntry249=mapEntry();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_mapEntry.add(mapEntry249.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:541:21: ( COMMA mapEntry )*
                    loop62:
                    do {
                        int alt62=2;
                        int LA62_0 = input.LA(1);

                        if ( (LA62_0==COMMA) ) {
                            alt62=1;
                        }


                        switch (alt62) {
                    	case 1 :
                    	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:541:22: COMMA mapEntry
                    	    {
                    	    COMMA250=(Token)match(input,COMMA,FOLLOW_COMMA_in_explicitMap3216); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA250);

                    	    pushFollow(FOLLOW_mapEntry_in_explicitMap3218);
                    	    mapEntry251=mapEntry();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_mapEntry.add(mapEntry251.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop62;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:541:41: BIGRARROW
                    {
                    BIGRARROW252=(Token)match(input,BIGRARROW,FOLLOW_BIGRARROW_in_explicitMap3224); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_BIGRARROW.add(BIGRARROW252);


                    }
                    break;

            }

            RBRACE253=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_explicitMap3227); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RBRACE.add(RBRACE253);



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
            // 541:59: -> ^( EXPLICIT_MAP ( mapEntry )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:541:62: ^( EXPLICIT_MAP ( mapEntry )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(EXPLICIT_MAP, "EXPLICIT_MAP"), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:541:77: ( mapEntry )*
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:544:1: mapEntry : expression BIGRARROW expression ;
    public final CgsuiteParser.mapEntry_return mapEntry() throws RecognitionException {
        CgsuiteParser.mapEntry_return retval = new CgsuiteParser.mapEntry_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token BIGRARROW255=null;
        CgsuiteParser.expression_return expression254 = null;

        CgsuiteParser.expression_return expression256 = null;


        CgsuiteTree BIGRARROW255_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:545:2: ( expression BIGRARROW expression )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:545:4: expression BIGRARROW expression
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            pushFollow(FOLLOW_expression_in_mapEntry3247);
            expression254=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression254.getTree());
            BIGRARROW255=(Token)match(input,BIGRARROW,FOLLOW_BIGRARROW_in_mapEntry3249); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            BIGRARROW255_tree = (CgsuiteTree)adaptor.create(BIGRARROW255);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(BIGRARROW255_tree, root_0);
            }
            pushFollow(FOLLOW_expression_in_mapEntry3252);
            expression256=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression256.getTree());

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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:548:1: explicitSet : LBRACE ( expression ( COMMA expression )* )? RBRACE -> ^( EXPLICIT_SET ( expression )* ) ;
    public final CgsuiteParser.explicitSet_return explicitSet() throws RecognitionException {
        CgsuiteParser.explicitSet_return retval = new CgsuiteParser.explicitSet_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token LBRACE257=null;
        Token COMMA259=null;
        Token RBRACE261=null;
        CgsuiteParser.expression_return expression258 = null;

        CgsuiteParser.expression_return expression260 = null;


        CgsuiteTree LBRACE257_tree=null;
        CgsuiteTree COMMA259_tree=null;
        CgsuiteTree RBRACE261_tree=null;
        RewriteRuleTokenStream stream_RBRACE=new RewriteRuleTokenStream(adaptor,"token RBRACE");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LBRACE=new RewriteRuleTokenStream(adaptor,"token LBRACE");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:549:2: ( LBRACE ( expression ( COMMA expression )* )? RBRACE -> ^( EXPLICIT_SET ( expression )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:549:4: LBRACE ( expression ( COMMA expression )* )? RBRACE
            {
            LBRACE257=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_explicitSet3263); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LBRACE.add(LBRACE257);

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:549:11: ( expression ( COMMA expression )* )?
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( ((LA65_0>=PLUS && LA65_0<=AST)||LA65_0==LPAREN||LA65_0==LBRACKET||LA65_0==LBRACE||(LA65_0>=CARET && LA65_0<=VEEVEE)||LA65_0==BEGIN||LA65_0==BY||LA65_0==DO||(LA65_0>=FALSE && LA65_0<=FOR)||LA65_0==FROM||LA65_0==IF||(LA65_0>=NIL && LA65_0<=NOT)||(LA65_0>=THIS && LA65_0<=TRUE)||(LA65_0>=WHERE && LA65_0<=WHILE)||(LA65_0>=IDENTIFIER && LA65_0<=CHAR)) ) {
                alt65=1;
            }
            switch (alt65) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:549:12: expression ( COMMA expression )*
                    {
                    pushFollow(FOLLOW_expression_in_explicitSet3266);
                    expression258=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression258.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:549:23: ( COMMA expression )*
                    loop64:
                    do {
                        int alt64=2;
                        int LA64_0 = input.LA(1);

                        if ( (LA64_0==COMMA) ) {
                            alt64=1;
                        }


                        switch (alt64) {
                    	case 1 :
                    	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:549:24: COMMA expression
                    	    {
                    	    COMMA259=(Token)match(input,COMMA,FOLLOW_COMMA_in_explicitSet3269); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA259);

                    	    pushFollow(FOLLOW_expression_in_explicitSet3271);
                    	    expression260=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expression.add(expression260.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop64;
                        }
                    } while (true);


                    }
                    break;

            }

            RBRACE261=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_explicitSet3277); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RBRACE.add(RBRACE261);



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
            // 549:52: -> ^( EXPLICIT_SET ( expression )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:549:55: ^( EXPLICIT_SET ( expression )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(EXPLICIT_SET, "EXPLICIT_SET"), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:549:70: ( expression )*
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:552:1: explicitList : LBRACKET ( expression ( COMMA expression )* )? RBRACKET -> ^( EXPLICIT_LIST ( expression )* ) ;
    public final CgsuiteParser.explicitList_return explicitList() throws RecognitionException {
        CgsuiteParser.explicitList_return retval = new CgsuiteParser.explicitList_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token LBRACKET262=null;
        Token COMMA264=null;
        Token RBRACKET266=null;
        CgsuiteParser.expression_return expression263 = null;

        CgsuiteParser.expression_return expression265 = null;


        CgsuiteTree LBRACKET262_tree=null;
        CgsuiteTree COMMA264_tree=null;
        CgsuiteTree RBRACKET266_tree=null;
        RewriteRuleTokenStream stream_LBRACKET=new RewriteRuleTokenStream(adaptor,"token LBRACKET");
        RewriteRuleTokenStream stream_RBRACKET=new RewriteRuleTokenStream(adaptor,"token RBRACKET");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:553:2: ( LBRACKET ( expression ( COMMA expression )* )? RBRACKET -> ^( EXPLICIT_LIST ( expression )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:553:4: LBRACKET ( expression ( COMMA expression )* )? RBRACKET
            {
            LBRACKET262=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_explicitList3297); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LBRACKET.add(LBRACKET262);

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:553:13: ( expression ( COMMA expression )* )?
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( ((LA67_0>=PLUS && LA67_0<=AST)||LA67_0==LPAREN||LA67_0==LBRACKET||LA67_0==LBRACE||(LA67_0>=CARET && LA67_0<=VEEVEE)||LA67_0==BEGIN||LA67_0==BY||LA67_0==DO||(LA67_0>=FALSE && LA67_0<=FOR)||LA67_0==FROM||LA67_0==IF||(LA67_0>=NIL && LA67_0<=NOT)||(LA67_0>=THIS && LA67_0<=TRUE)||(LA67_0>=WHERE && LA67_0<=WHILE)||(LA67_0>=IDENTIFIER && LA67_0<=CHAR)) ) {
                alt67=1;
            }
            switch (alt67) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:553:14: expression ( COMMA expression )*
                    {
                    pushFollow(FOLLOW_expression_in_explicitList3300);
                    expression263=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression263.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:553:25: ( COMMA expression )*
                    loop66:
                    do {
                        int alt66=2;
                        int LA66_0 = input.LA(1);

                        if ( (LA66_0==COMMA) ) {
                            alt66=1;
                        }


                        switch (alt66) {
                    	case 1 :
                    	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:553:26: COMMA expression
                    	    {
                    	    COMMA264=(Token)match(input,COMMA,FOLLOW_COMMA_in_explicitList3303); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA264);

                    	    pushFollow(FOLLOW_expression_in_explicitList3305);
                    	    expression265=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expression.add(expression265.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop66;
                        }
                    } while (true);


                    }
                    break;

            }

            RBRACKET266=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_explicitList3311); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RBRACKET.add(RBRACKET266);



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
            // 553:56: -> ^( EXPLICIT_LIST ( expression )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:553:59: ^( EXPLICIT_LIST ( expression )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(EXPLICIT_LIST, "EXPLICIT_LIST"), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:553:75: ( expression )*
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:556:1: expressionList : ( expression ( COMMA expression )* )? -> ^( EXPRESSION_LIST ( expression )* ) ;
    public final CgsuiteParser.expressionList_return expressionList() throws RecognitionException {
        CgsuiteParser.expressionList_return retval = new CgsuiteParser.expressionList_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token COMMA268=null;
        CgsuiteParser.expression_return expression267 = null;

        CgsuiteParser.expression_return expression269 = null;


        CgsuiteTree COMMA268_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:557:5: ( ( expression ( COMMA expression )* )? -> ^( EXPRESSION_LIST ( expression )* ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:557:7: ( expression ( COMMA expression )* )?
            {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:557:7: ( expression ( COMMA expression )* )?
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( ((LA69_0>=PLUS && LA69_0<=AST)||LA69_0==LPAREN||LA69_0==LBRACKET||LA69_0==LBRACE||(LA69_0>=CARET && LA69_0<=VEEVEE)||LA69_0==BEGIN||LA69_0==BY||LA69_0==DO||(LA69_0>=FALSE && LA69_0<=FOR)||LA69_0==FROM||LA69_0==IF||(LA69_0>=NIL && LA69_0<=NOT)||(LA69_0>=THIS && LA69_0<=TRUE)||(LA69_0>=WHERE && LA69_0<=WHILE)||(LA69_0>=IDENTIFIER && LA69_0<=CHAR)) ) {
                alt69=1;
            }
            switch (alt69) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:557:8: expression ( COMMA expression )*
                    {
                    pushFollow(FOLLOW_expression_in_expressionList3335);
                    expression267=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression267.getTree());
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:557:19: ( COMMA expression )*
                    loop68:
                    do {
                        int alt68=2;
                        int LA68_0 = input.LA(1);

                        if ( (LA68_0==COMMA) ) {
                            alt68=1;
                        }


                        switch (alt68) {
                    	case 1 :
                    	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:557:20: COMMA expression
                    	    {
                    	    COMMA268=(Token)match(input,COMMA,FOLLOW_COMMA_in_expressionList3338); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA268);

                    	    pushFollow(FOLLOW_expression_in_expressionList3340);
                    	    expression269=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expression.add(expression269.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop68;
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
            // 557:41: -> ^( EXPRESSION_LIST ( expression )* )
            {
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:557:44: ^( EXPRESSION_LIST ( expression )* )
                {
                CgsuiteTree root_1 = (CgsuiteTree)adaptor.nil();
                root_1 = (CgsuiteTree)adaptor.becomeRoot((CgsuiteTree)adaptor.create(EXPRESSION_LIST, "EXPRESSION_LIST"), root_1);

                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:557:62: ( expression )*
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
    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:560:1: range : INTEGER DOTDOT INTEGER ;
    public final CgsuiteParser.range_return range() throws RecognitionException {
        CgsuiteParser.range_return retval = new CgsuiteParser.range_return();
        retval.start = input.LT(1);

        CgsuiteTree root_0 = null;

        Token INTEGER270=null;
        Token DOTDOT271=null;
        Token INTEGER272=null;

        CgsuiteTree INTEGER270_tree=null;
        CgsuiteTree DOTDOT271_tree=null;
        CgsuiteTree INTEGER272_tree=null;

        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:561:2: ( INTEGER DOTDOT INTEGER )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:561:4: INTEGER DOTDOT INTEGER
            {
            root_0 = (CgsuiteTree)adaptor.nil();

            INTEGER270=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_range3367); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            INTEGER270_tree = (CgsuiteTree)adaptor.create(INTEGER270);
            adaptor.addChild(root_0, INTEGER270_tree);
            }
            DOTDOT271=(Token)match(input,DOTDOT,FOLLOW_DOTDOT_in_range3369); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            DOTDOT271_tree = (CgsuiteTree)adaptor.create(DOTDOT271);
            root_0 = (CgsuiteTree)adaptor.becomeRoot(DOTDOT271_tree, root_0);
            }
            INTEGER272=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_range3372); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            INTEGER272_tree = (CgsuiteTree)adaptor.create(INTEGER272);
            adaptor.addChild(root_0, INTEGER272_tree);
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
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:417:7: ( PLUSMINUS LPAREN expression ( COMMA expression )* RPAREN )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:417:7: PLUSMINUS LPAREN expression ( COMMA expression )* RPAREN
        {
        match(input,PLUSMINUS,FOLLOW_PLUSMINUS_in_synpred1_Cgsuite2486); if (state.failed) return ;
        match(input,LPAREN,FOLLOW_LPAREN_in_synpred1_Cgsuite2488); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred1_Cgsuite2490);
        expression();

        state._fsp--;
        if (state.failed) return ;
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:417:35: ( COMMA expression )*
        loop70:
        do {
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==COMMA) ) {
                alt70=1;
            }


            switch (alt70) {
        	case 1 :
        	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:417:36: COMMA expression
        	    {
        	    match(input,COMMA,FOLLOW_COMMA_in_synpred1_Cgsuite2493); if (state.failed) return ;
        	    pushFollow(FOLLOW_expression_in_synpred1_Cgsuite2495);
        	    expression();

        	    state._fsp--;
        	    if (state.failed) return ;

        	    }
        	    break;

        	default :
        	    break loop70;
            }
        } while (true);

        match(input,RPAREN,FOLLOW_RPAREN_in_synpred1_Cgsuite2499); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_Cgsuite

    // $ANTLR start synpred2_Cgsuite
    public final void synpred2_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:418:7: ( PLUSMINUS unaryExpr )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:418:7: PLUSMINUS unaryExpr
        {
        match(input,PLUSMINUS,FOLLOW_PLUSMINUS_in_synpred2_Cgsuite2516); if (state.failed) return ;
        pushFollow(FOLLOW_unaryExpr_in_synpred2_Cgsuite2518);
        unaryExpr();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_Cgsuite

    // $ANTLR start synpred3_Cgsuite
    public final void synpred3_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:456:7: ( ( CARET | CARETCARET | VEE | VEEVEE ) starExpr )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:456:7: ( CARET | CARETCARET | VEE | VEEVEE ) starExpr
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

        pushFollow(FOLLOW_starExpr_in_synpred3_Cgsuite2831);
        starExpr();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_Cgsuite

    // $ANTLR start synpred4_Cgsuite
    public final void synpred4_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:457:7: ( ( CARET | VEE ) primaryExpr starExpr )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:457:7: ( CARET | VEE ) primaryExpr starExpr
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

        pushFollow(FOLLOW_primaryExpr_in_synpred4_Cgsuite2848);
        primaryExpr();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_starExpr_in_synpred4_Cgsuite2850);
        starExpr();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_Cgsuite

    // $ANTLR start synpred5_Cgsuite
    public final void synpred5_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:458:7: ( ( CARET | VEE ) primaryExpr )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:458:7: ( CARET | VEE ) primaryExpr
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

        pushFollow(FOLLOW_primaryExpr_in_synpred5_Cgsuite2867);
        primaryExpr();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_Cgsuite

    // $ANTLR start synpred7_Cgsuite
    public final void synpred7_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:460:7: ( CARET )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:460:7: CARET
        {
        match(input,CARET,FOLLOW_CARET_in_synpred7_Cgsuite2883); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_Cgsuite

    // $ANTLR start synpred8_Cgsuite
    public final void synpred8_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:460:15: ( CARETCARET )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:460:15: CARETCARET
        {
        match(input,CARETCARET,FOLLOW_CARETCARET_in_synpred8_Cgsuite2887); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_Cgsuite

    // $ANTLR start synpred9_Cgsuite
    public final void synpred9_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:460:28: ( VEE )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:460:28: VEE
        {
        match(input,VEE,FOLLOW_VEE_in_synpred9_Cgsuite2891); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred9_Cgsuite

    // $ANTLR start synpred10_Cgsuite
    public final void synpred10_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:460:34: ( VEEVEE )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:460:34: VEEVEE
        {
        match(input,VEEVEE,FOLLOW_VEEVEE_in_synpred10_Cgsuite2895); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred10_Cgsuite

    // $ANTLR start synpred11_Cgsuite
    public final void synpred11_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:470:7: ( AST primaryExpr )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:470:7: AST primaryExpr
        {
        match(input,AST,FOLLOW_AST_in_synpred11_Cgsuite2946); if (state.failed) return ;
        pushFollow(FOLLOW_primaryExpr_in_synpred11_Cgsuite2948);
        primaryExpr();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred11_Cgsuite

    // $ANTLR start synpred12_Cgsuite
    public final void synpred12_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:479:4: ( INTEGER DOTDOT )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:479:5: INTEGER DOTDOT
        {
        match(input,INTEGER,FOLLOW_INTEGER_in_synpred12_Cgsuite3004); if (state.failed) return ;
        match(input,DOTDOT,FOLLOW_DOTDOT_in_synpred12_Cgsuite3006); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred12_Cgsuite

    // $ANTLR start synpred13_Cgsuite
    public final void synpred13_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:486:7: ( LBRACE expressionList SLASHES )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:486:8: LBRACE expressionList SLASHES
        {
        match(input,LBRACE,FOLLOW_LBRACE_in_synpred13_Cgsuite3062); if (state.failed) return ;
        pushFollow(FOLLOW_expressionList_in_synpred13_Cgsuite3064);
        expressionList();

        state._fsp--;
        if (state.failed) return ;
        match(input,SLASHES,FOLLOW_SLASHES_in_synpred13_Cgsuite3066); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred13_Cgsuite

    // $ANTLR start synpred14_Cgsuite
    public final void synpred14_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:487:4: ( LBRACE ( expression )? BIGRARROW )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:487:5: LBRACE ( expression )? BIGRARROW
        {
        match(input,LBRACE,FOLLOW_LBRACE_in_synpred14_Cgsuite3077); if (state.failed) return ;
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:487:12: ( expression )?
        int alt71=2;
        int LA71_0 = input.LA(1);

        if ( ((LA71_0>=PLUS && LA71_0<=AST)||LA71_0==LPAREN||LA71_0==LBRACKET||LA71_0==LBRACE||(LA71_0>=CARET && LA71_0<=VEEVEE)||LA71_0==BEGIN||LA71_0==BY||LA71_0==DO||(LA71_0>=FALSE && LA71_0<=FOR)||LA71_0==FROM||LA71_0==IF||(LA71_0>=NIL && LA71_0<=NOT)||(LA71_0>=THIS && LA71_0<=TRUE)||(LA71_0>=WHERE && LA71_0<=WHILE)||(LA71_0>=IDENTIFIER && LA71_0<=CHAR)) ) {
            alt71=1;
        }
        switch (alt71) {
            case 1 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:487:12: expression
                {
                pushFollow(FOLLOW_expression_in_synpred14_Cgsuite3079);
                expression();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }

        match(input,BIGRARROW,FOLLOW_BIGRARROW_in_synpred14_Cgsuite3082); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred14_Cgsuite

    // $ANTLR start synpred15_Cgsuite
    public final void synpred15_Cgsuite_fragment() throws RecognitionException {   
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:501:7: ( expressionList SLASHES )
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:501:8: expressionList SLASHES
        {
        pushFollow(FOLLOW_expressionList_in_synpred15_Cgsuite3148);
        expressionList();

        state._fsp--;
        if (state.failed) return ;
        match(input,SLASHES,FOLLOW_SLASHES_in_synpred15_Cgsuite3150); if (state.failed) return ;

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
    protected DFA51 dfa51 = new DFA51(this);
    protected DFA58 dfa58 = new DFA58(this);
    protected DFA60 dfa60 = new DFA60(this);
    protected DFA61 dfa61 = new DFA61(this);
    static final String DFA6_eotS =
        "\12\uffff";
    static final String DFA6_eofS =
        "\12\uffff";
    static final String DFA6_minS =
        "\6\104\4\uffff";
    static final String DFA6_maxS =
        "\6\141\4\uffff";
    static final String DFA6_acceptS =
        "\6\uffff\1\1\1\2\1\3\1\4";
    static final String DFA6_specialS =
        "\12\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\11\7\uffff\1\4\2\uffff\1\10\6\uffff\1\1\1\7\1\2\1\3\2\uffff"+
            "\1\5\4\uffff\1\6",
            "\1\11\7\uffff\1\4\2\uffff\1\10\6\uffff\1\1\1\7\1\2\1\3\2\uffff"+
            "\1\5\4\uffff\1\6",
            "\1\11\7\uffff\1\4\2\uffff\1\10\6\uffff\1\1\1\7\1\2\1\3\2\uffff"+
            "\1\5\4\uffff\1\6",
            "\1\11\7\uffff\1\4\2\uffff\1\10\6\uffff\1\1\1\7\1\2\1\3\2\uffff"+
            "\1\5\4\uffff\1\6",
            "\1\11\7\uffff\1\4\2\uffff\1\10\6\uffff\1\1\1\7\1\2\1\3\2\uffff"+
            "\1\5\4\uffff\1\6",
            "\1\11\7\uffff\1\4\2\uffff\1\10\6\uffff\1\1\1\7\1\2\1\3\2\uffff"+
            "\1\5\4\uffff\1\6",
            "",
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
            return "216:1: declaration : ( varDeclaration | propertyDeclaration | methodDeclaration | enumDeclaration );";
        }
    }
    static final String DFA51_eotS =
        "\27\uffff";
    static final String DFA51_eofS =
        "\27\uffff";
    static final String DFA51_minS =
        "\1\4\1\0\25\uffff";
    static final String DFA51_maxS =
        "\1\171\1\0\25\uffff";
    static final String DFA51_acceptS =
        "\2\uffff\1\3\22\uffff\1\1\1\2";
    static final String DFA51_specialS =
        "\1\uffff\1\0\25\uffff}>";
    static final String[] DFA51_transitionS = {
            "\2\2\1\1\1\2\5\uffff\1\2\1\uffff\1\2\1\uffff\1\2\12\uffff\4"+
            "\2\32\uffff\1\2\13\uffff\1\2\12\uffff\1\2\14\uffff\1\2\1\uffff"+
            "\1\2\25\uffff\4\2",
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

    static final short[] DFA51_eot = DFA.unpackEncodedString(DFA51_eotS);
    static final short[] DFA51_eof = DFA.unpackEncodedString(DFA51_eofS);
    static final char[] DFA51_min = DFA.unpackEncodedStringToUnsignedChars(DFA51_minS);
    static final char[] DFA51_max = DFA.unpackEncodedStringToUnsignedChars(DFA51_maxS);
    static final short[] DFA51_accept = DFA.unpackEncodedString(DFA51_acceptS);
    static final short[] DFA51_special = DFA.unpackEncodedString(DFA51_specialS);
    static final short[][] DFA51_transition;

    static {
        int numStates = DFA51_transitionS.length;
        DFA51_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA51_transition[i] = DFA.unpackEncodedString(DFA51_transitionS[i]);
        }
    }

    class DFA51 extends DFA {

        public DFA51(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 51;
            this.eot = DFA51_eot;
            this.eof = DFA51_eof;
            this.min = DFA51_min;
            this.max = DFA51_max;
            this.accept = DFA51_accept;
            this.special = DFA51_special;
            this.transition = DFA51_transition;
        }
        public String getDescription() {
            return "411:1: plusminusExpr options {backtrack=true; memoize=true; } : ( PLUSMINUS LPAREN expression ( COMMA expression )* RPAREN -> ^( PLUSMINUS ( expression )* ) | PLUSMINUS unaryExpr -> ^( PLUSMINUS unaryExpr ) | unaryExpr );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA51_1 = input.LA(1);

                         
                        int index51_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_Cgsuite()) ) {s = 21;}

                        else if ( (synpred2_Cgsuite()) ) {s = 22;}

                         
                        input.seek(index51_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 51, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA58_eotS =
        "\31\uffff";
    static final String DFA58_eofS =
        "\31\uffff";
    static final String DFA58_minS =
        "\1\7\2\0\1\uffff\2\0\23\uffff";
    static final String DFA58_maxS =
        "\1\171\2\0\1\uffff\2\0\23\uffff";
    static final String DFA58_acceptS =
        "\3\uffff\1\4\2\uffff\1\11\13\uffff\1\1\1\2\1\3\1\5\1\6\1\7\1\10";
    static final String DFA58_specialS =
        "\1\uffff\1\0\1\1\1\uffff\1\2\1\3\23\uffff}>";
    static final String[] DFA58_transitionS = {
            "\1\3\5\uffff\1\6\1\uffff\1\6\1\uffff\1\6\12\uffff\1\1\1\2\1"+
            "\4\1\5\32\uffff\1\6\13\uffff\1\6\12\uffff\1\6\14\uffff\1\6\1"+
            "\uffff\1\6\25\uffff\4\6",
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

    static final short[] DFA58_eot = DFA.unpackEncodedString(DFA58_eotS);
    static final short[] DFA58_eof = DFA.unpackEncodedString(DFA58_eofS);
    static final char[] DFA58_min = DFA.unpackEncodedStringToUnsignedChars(DFA58_minS);
    static final char[] DFA58_max = DFA.unpackEncodedStringToUnsignedChars(DFA58_maxS);
    static final short[] DFA58_accept = DFA.unpackEncodedString(DFA58_acceptS);
    static final short[] DFA58_special = DFA.unpackEncodedString(DFA58_specialS);
    static final short[][] DFA58_transition;

    static {
        int numStates = DFA58_transitionS.length;
        DFA58_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA58_transition[i] = DFA.unpackEncodedString(DFA58_transitionS[i]);
        }
    }

    class DFA58 extends DFA {

        public DFA58(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 58;
            this.eot = DFA58_eot;
            this.eof = DFA58_eof;
            this.min = DFA58_min;
            this.max = DFA58_max;
            this.accept = DFA58_accept;
            this.special = DFA58_special;
            this.transition = DFA58_transition;
        }
        public String getDescription() {
            return "450:1: upstarExpr options {backtrack=true; memoize=true; } : ( ( CARET | CARETCARET | VEE | VEEVEE ) starExpr | ( CARET | VEE ) primaryExpr starExpr | ( CARET | VEE ) primaryExpr | starExpr | CARET | CARETCARET | VEE | VEEVEE | primaryExpr );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA58_1 = input.LA(1);

                         
                        int index58_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_Cgsuite()) ) {s = 18;}

                        else if ( (synpred4_Cgsuite()) ) {s = 19;}

                        else if ( (synpred5_Cgsuite()) ) {s = 20;}

                        else if ( (synpred7_Cgsuite()) ) {s = 21;}

                         
                        input.seek(index58_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA58_2 = input.LA(1);

                         
                        int index58_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_Cgsuite()) ) {s = 18;}

                        else if ( (synpred8_Cgsuite()) ) {s = 22;}

                         
                        input.seek(index58_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA58_4 = input.LA(1);

                         
                        int index58_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_Cgsuite()) ) {s = 18;}

                        else if ( (synpred4_Cgsuite()) ) {s = 19;}

                        else if ( (synpred5_Cgsuite()) ) {s = 20;}

                        else if ( (synpred9_Cgsuite()) ) {s = 23;}

                         
                        input.seek(index58_4);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA58_5 = input.LA(1);

                         
                        int index58_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_Cgsuite()) ) {s = 18;}

                        else if ( (synpred10_Cgsuite()) ) {s = 24;}

                         
                        input.seek(index58_5);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 58, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA60_eotS =
        "\22\uffff";
    static final String DFA60_eofS =
        "\22\uffff";
    static final String DFA60_minS =
        "\1\15\4\uffff\1\0\5\uffff\1\0\6\uffff";
    static final String DFA60_maxS =
        "\1\171\4\uffff\1\0\5\uffff\1\0\6\uffff";
    static final String DFA60_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\uffff\1\7\1\10\1\11\1\12\1\13\1\uffff"+
        "\1\17\1\5\1\6\1\14\1\15\1\16";
    static final String DFA60_specialS =
        "\5\uffff\1\0\5\uffff\1\1\6\uffff}>";
    static final String[] DFA60_transitionS = {
            "\1\11\1\uffff\1\14\1\uffff\1\13\50\uffff\1\12\13\uffff\1\4"+
            "\12\uffff\1\1\14\uffff\1\2\1\uffff\1\3\25\uffff\1\10\1\6\1\5"+
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

    static final short[] DFA60_eot = DFA.unpackEncodedString(DFA60_eotS);
    static final short[] DFA60_eof = DFA.unpackEncodedString(DFA60_eofS);
    static final char[] DFA60_min = DFA.unpackEncodedStringToUnsignedChars(DFA60_minS);
    static final char[] DFA60_max = DFA.unpackEncodedStringToUnsignedChars(DFA60_maxS);
    static final short[] DFA60_accept = DFA.unpackEncodedString(DFA60_acceptS);
    static final short[] DFA60_special = DFA.unpackEncodedString(DFA60_specialS);
    static final short[][] DFA60_transition;

    static {
        int numStates = DFA60_transitionS.length;
        DFA60_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA60_transition[i] = DFA.unpackEncodedString(DFA60_transitionS[i]);
        }
    }

    class DFA60 extends DFA {

        public DFA60(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 60;
            this.eot = DFA60_eot;
            this.eof = DFA60_eof;
            this.min = DFA60_min;
            this.max = DFA60_max;
            this.accept = DFA60_accept;
            this.special = DFA60_special;
            this.transition = DFA60_transition;
        }
        public String getDescription() {
            return "474:1: primaryExpr : ( NIL | THIS | TRUE | FALSE | ( INTEGER DOTDOT )=> range | INTEGER | STRING | CHAR | IDENTIFIER | LPAREN statementSequence RPAREN | BEGIN statementSequence END | ( LBRACE expressionList SLASHES )=> explicitGame | ( LBRACE ( expression )? BIGRARROW )=> explicitMap | explicitSet | explicitList );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA60_5 = input.LA(1);

                         
                        int index60_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12_Cgsuite()) ) {s = 13;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index60_5);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA60_11 = input.LA(1);

                         
                        int index60_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred13_Cgsuite()) ) {s = 15;}

                        else if ( (synpred14_Cgsuite()) ) {s = 16;}

                        else if ( (true) ) {s = 17;}

                         
                        input.seek(index60_11);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 60, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA61_eotS =
        "\40\uffff";
    static final String DFA61_eofS =
        "\40\uffff";
    static final String DFA61_minS =
        "\1\4\35\0\2\uffff";
    static final String DFA61_maxS =
        "\1\172\35\0\2\uffff";
    static final String DFA61_acceptS =
        "\36\uffff\1\1\1\2";
    static final String DFA61_specialS =
        "\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15"+
        "\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1\32"+
        "\1\33\1\34\1\35\2\uffff}>";
    static final String[] DFA61_transitionS = {
            "\1\16\1\15\1\14\1\21\5\uffff\1\2\1\uffff\1\35\1\uffff\1\34"+
            "\1\37\11\uffff\1\17\1\20\1\22\1\23\32\uffff\1\33\1\uffff\1\7"+
            "\3\uffff\1\12\5\uffff\1\27\1\4\1\uffff\1\5\1\uffff\1\3\5\uffff"+
            "\1\24\1\13\13\uffff\1\25\1\6\1\26\1\uffff\1\11\1\10\22\uffff"+
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
            return "496:1: slashExpression : ( ( expressionList SLASHES )=>lo= expressionList ( SLASHES ro= slashExpression ) -> | lo= expressionList -> $lo);";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA61_0 = input.LA(1);

                         
                        int index61_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA61_0==IDENTIFIER) ) {s = 1;}

                        else if ( (LA61_0==LPAREN) ) {s = 2;}

                        else if ( (LA61_0==IF) ) {s = 3;}

                        else if ( (LA61_0==FOR) ) {s = 4;}

                        else if ( (LA61_0==FROM) ) {s = 5;}

                        else if ( (LA61_0==TO) ) {s = 6;}

                        else if ( (LA61_0==BY) ) {s = 7;}

                        else if ( (LA61_0==WHILE) ) {s = 8;}

                        else if ( (LA61_0==WHERE) ) {s = 9;}

                        else if ( (LA61_0==DO) ) {s = 10;}

                        else if ( (LA61_0==NOT) ) {s = 11;}

                        else if ( (LA61_0==PLUSMINUS) ) {s = 12;}

                        else if ( (LA61_0==MINUS) ) {s = 13;}

                        else if ( (LA61_0==PLUS) ) {s = 14;}

                        else if ( (LA61_0==CARET) ) {s = 15;}

                        else if ( (LA61_0==CARETCARET) ) {s = 16;}

                        else if ( (LA61_0==AST) ) {s = 17;}

                        else if ( (LA61_0==VEE) ) {s = 18;}

                        else if ( (LA61_0==VEEVEE) ) {s = 19;}

                        else if ( (LA61_0==NIL) ) {s = 20;}

                        else if ( (LA61_0==THIS) ) {s = 21;}

                        else if ( (LA61_0==TRUE) ) {s = 22;}

                        else if ( (LA61_0==FALSE) ) {s = 23;}

                        else if ( (LA61_0==INTEGER) ) {s = 24;}

                        else if ( (LA61_0==STRING) ) {s = 25;}

                        else if ( (LA61_0==CHAR) ) {s = 26;}

                        else if ( (LA61_0==BEGIN) ) {s = 27;}

                        else if ( (LA61_0==LBRACE) ) {s = 28;}

                        else if ( (LA61_0==LBRACKET) ) {s = 29;}

                        else if ( (LA61_0==SLASHES) && (synpred15_Cgsuite())) {s = 30;}

                        else if ( (LA61_0==RBRACE) ) {s = 31;}

                         
                        input.seek(index61_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA61_1 = input.LA(1);

                         
                        int index61_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_1);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA61_2 = input.LA(1);

                         
                        int index61_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_2);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA61_3 = input.LA(1);

                         
                        int index61_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_3);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA61_4 = input.LA(1);

                         
                        int index61_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_4);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA61_5 = input.LA(1);

                         
                        int index61_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_5);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA61_6 = input.LA(1);

                         
                        int index61_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_6);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA61_7 = input.LA(1);

                         
                        int index61_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_7);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA61_8 = input.LA(1);

                         
                        int index61_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_8);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA61_9 = input.LA(1);

                         
                        int index61_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_9);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA61_10 = input.LA(1);

                         
                        int index61_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_10);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA61_11 = input.LA(1);

                         
                        int index61_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_11);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA61_12 = input.LA(1);

                         
                        int index61_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_12);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA61_13 = input.LA(1);

                         
                        int index61_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_13);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA61_14 = input.LA(1);

                         
                        int index61_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_14);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA61_15 = input.LA(1);

                         
                        int index61_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_15);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA61_16 = input.LA(1);

                         
                        int index61_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_16);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA61_17 = input.LA(1);

                         
                        int index61_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_17);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA61_18 = input.LA(1);

                         
                        int index61_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_18);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA61_19 = input.LA(1);

                         
                        int index61_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_19);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA61_20 = input.LA(1);

                         
                        int index61_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_20);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA61_21 = input.LA(1);

                         
                        int index61_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_21);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA61_22 = input.LA(1);

                         
                        int index61_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_22);
                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA61_23 = input.LA(1);

                         
                        int index61_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_23);
                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA61_24 = input.LA(1);

                         
                        int index61_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_24);
                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA61_25 = input.LA(1);

                         
                        int index61_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_25);
                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA61_26 = input.LA(1);

                         
                        int index61_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_26);
                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA61_27 = input.LA(1);

                         
                        int index61_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_27);
                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA61_28 = input.LA(1);

                         
                        int index61_28 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_28);
                        if ( s>=0 ) return s;
                        break;
                    case 29 : 
                        int LA61_29 = input.LA(1);

                         
                        int index61_29 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_Cgsuite()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index61_29);
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
 

    public static final BitSet FOLLOW_classDeclaration_in_compilationUnit1159 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_enumDeclaration_in_compilationUnit1163 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_compilationUnit1166 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CLASS_in_classDeclaration1178 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_classDeclaration1181 = new BitSet(new long[]{0x0000000000800000L,0x0000000213C09038L});
    public static final BitSet FOLLOW_extendsClause_in_classDeclaration1183 = new BitSet(new long[]{0x0000000000800000L,0x0000000213C09018L});
    public static final BitSet FOLLOW_javaClause_in_classDeclaration1186 = new BitSet(new long[]{0x0000000000000000L,0x0000000213C09018L});
    public static final BitSet FOLLOW_declaration_in_classDeclaration1189 = new BitSet(new long[]{0x0000000000000000L,0x0000000213C09018L});
    public static final BitSet FOLLOW_END_in_classDeclaration1192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXTENDS_in_extendsClause1205 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_extendsClause1208 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_COMMA_in_extendsClause1211 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_extendsClause1214 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_COLON_in_javaClause1230 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_JAVA_in_javaClause1233 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_STRING_in_javaClause1236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_varDeclaration_in_declaration1251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_propertyDeclaration_in_declaration1256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclaration_in_declaration1261 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_declaration1269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_varDeclaration1284 = new BitSet(new long[]{0x0000000000000000L,0x0000000200000000L});
    public static final BitSet FOLLOW_VAR_in_varDeclaration1286 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_varDeclaration1289 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_SEMI_in_varDeclaration1291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_propertyDeclaration1306 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_PROPERTY_in_propertyDeclaration1308 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_propertyDeclaration1311 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_DOT_in_propertyDeclaration1313 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000400L});
    public static final BitSet FOLLOW_set_in_propertyDeclaration1316 = new BitSet(new long[]{0xDC000000F0C2A0F0L,0x03C0000DC4060AC9L});
    public static final BitSet FOLLOW_javaClause_in_propertyDeclaration1328 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_SEMI_in_propertyDeclaration1330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statementSequence_in_propertyDeclaration1335 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_END_in_propertyDeclaration1337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_proptype0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_methodDeclaration1376 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_METHOD_in_methodDeclaration1378 = new BitSet(new long[]{0x0000000000000000L,0x0040000000080000L});
    public static final BitSet FOLLOW_methodName_in_methodDeclaration1381 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_LPAREN_in_methodDeclaration1383 = new BitSet(new long[]{0x0000000000004000L,0x0040000000000000L});
    public static final BitSet FOLLOW_methodParameterList_in_methodDeclaration1386 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_RPAREN_in_methodDeclaration1388 = new BitSet(new long[]{0xDC000000F0C2A0F0L,0x03C0000DC4060AC9L});
    public static final BitSet FOLLOW_javaClause_in_methodDeclaration1395 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_SEMI_in_methodDeclaration1397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statementSequence_in_methodDeclaration1402 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_END_in_methodDeclaration1404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PRIVATE_in_modifiers1419 = new BitSet(new long[]{0x0000000000000002L,0x0000000013401000L});
    public static final BitSet FOLLOW_PROTECTED_in_modifiers1423 = new BitSet(new long[]{0x0000000000000002L,0x0000000013401000L});
    public static final BitSet FOLLOW_PUBLIC_in_modifiers1427 = new BitSet(new long[]{0x0000000000000002L,0x0000000013401000L});
    public static final BitSet FOLLOW_IMMUTABLE_in_modifiers1431 = new BitSet(new long[]{0x0000000000000002L,0x0000000013401000L});
    public static final BitSet FOLLOW_STATIC_in_modifiers1435 = new BitSet(new long[]{0x0000000000000002L,0x0000000013401000L});
    public static final BitSet FOLLOW_OP_in_methodName1469 = new BitSet(new long[]{0x007FC0FF00008DB0L,0x0000000000210000L});
    public static final BitSet FOLLOW_opCode_in_methodName1472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodName1480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_opCode1497 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_opCode1501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AST_in_opCode1505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FSLASH_in_opCode1509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PERCENT_in_opCode1513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXP_in_opCode1517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEG_in_opCode1521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_POS_in_opCode1525 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_standardRelationalToken_in_opCode1533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opAssignmentToken_in_opCode1541 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_opCode1549 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RBRACKET_in_opCode1551 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_opCode1553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodParameter_in_methodParameterList1571 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_COMMA_in_methodParameterList1574 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_methodParameter_in_methodParameterList1576 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodParameter1603 = new BitSet(new long[]{0x0000000008000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodParameter1607 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_QUESTION_in_methodParameter1610 = new BitSet(new long[]{0xDC000000F002A0F2L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_methodParameter1612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodParameter1638 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodParameter1642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodParameter1657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_enumDeclaration1671 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_ENUM_in_enumDeclaration1673 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_enumDeclaration1676 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_enumDeclaration1685 = new BitSet(new long[]{0x0000000000600000L,0x0000000000000008L});
    public static final BitSet FOLLOW_COMMA_in_enumDeclaration1688 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_enumDeclaration1691 = new BitSet(new long[]{0x0000000000600000L,0x0000000000000008L});
    public static final BitSet FOLLOW_SEMI_in_enumDeclaration1696 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_END_in_enumDeclaration1700 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_script1717 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_script1719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statementSequence_in_block1737 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statement_in_statementSequence1752 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_SEMI_in_statementSequence1756 = new BitSet(new long[]{0xDC000000F042A0F2L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_statement_in_statementSequence1758 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_BREAK_in_statement1782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONTINUE_in_statement1787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURN_in_statement1792 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_statement1795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CLEAR_in_statement1803 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_statement1808 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentExpression_in_expression1819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionExpression_in_assignmentExpression1830 = new BitSet(new long[]{0x007FE00000000002L});
    public static final BitSet FOLLOW_assignmentToken_in_assignmentExpression1833 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_assignmentExpression_in_assignmentExpression1836 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_assignmentToken1850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opAssignmentToken_in_assignmentToken1855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_opAssignmentToken0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_procedureParameterList_in_functionExpression1921 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_RARROW_in_functionExpression1923 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_functionExpression_in_functionExpression1926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_controlExpression_in_functionExpression1934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_procedureParameterList1948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_procedureParameterList1964 = new BitSet(new long[]{0x0000000000004000L,0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_procedureParameterList1967 = new BitSet(new long[]{0x0000000000204000L});
    public static final BitSet FOLLOW_COMMA_in_procedureParameterList1970 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_procedureParameterList1972 = new BitSet(new long[]{0x0000000000204000L});
    public static final BitSet FOLLOW_RPAREN_in_procedureParameterList1978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_controlExpression2002 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_controlExpression2005 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_THEN_in_controlExpression2007 = new BitSet(new long[]{0xDC000000F042A0F0L,0x03C0000DC4060ACFL});
    public static final BitSet FOLLOW_statementSequence_in_controlExpression2010 = new BitSet(new long[]{0x0000000000000000L,0x000000000000000EL});
    public static final BitSet FOLLOW_elseifClause_in_controlExpression2012 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_END_in_controlExpression2015 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forExpression_in_controlExpression2021 = new BitSet(new long[]{0x1000000000000000L,0x0000000C80000201L});
    public static final BitSet FOLLOW_fromExpression_in_controlExpression2024 = new BitSet(new long[]{0x1000000000000000L,0x0000000C80000001L});
    public static final BitSet FOLLOW_toExpression_in_controlExpression2027 = new BitSet(new long[]{0x1000000000000000L,0x0000000C00000001L});
    public static final BitSet FOLLOW_byExpression_in_controlExpression2030 = new BitSet(new long[]{0x0000000000000000L,0x0000000C00000001L});
    public static final BitSet FOLLOW_whileExpression_in_controlExpression2033 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000001L});
    public static final BitSet FOLLOW_whereExpression_in_controlExpression2036 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_DO_in_controlExpression2039 = new BitSet(new long[]{0xDC000000F042A0F0L,0x03C0000DC4060AC9L});
    public static final BitSet FOLLOW_statementSequence_in_controlExpression2042 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_END_in_controlExpression2044 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_controlExpression2050 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_controlExpression2053 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_IN_in_controlExpression2055 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_controlExpression2058 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_DO_in_controlExpression2060 = new BitSet(new long[]{0xDC000000F042A0F0L,0x03C0000DC4060AC9L});
    public static final BitSet FOLLOW_statementSequence_in_controlExpression2063 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_END_in_controlExpression2065 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_orExpression_in_controlExpression2071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_forExpression2082 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_forExpression2085 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_fromExpression2097 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_fromExpression2100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TO_in_toExpression2115 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_toExpression2118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BY_in_byExpression2135 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_byExpression2138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_whileExpression2156 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_whileExpression2159 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_in_whereExpression2173 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_whereExpression2176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSEIF_in_elseifClause2190 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_elseifClause2193 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_THEN_in_elseifClause2195 = new BitSet(new long[]{0xDC000000F042A0F0L,0x03C0000DC4060AC7L});
    public static final BitSet FOLLOW_statementSequence_in_elseifClause2198 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000006L});
    public static final BitSet FOLLOW_elseifClause_in_elseifClause2200 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_elseifClause2206 = new BitSet(new long[]{0xDC000000F042A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_statementSequence_in_elseifClause2209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_andExpression_in_orExpression2220 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_OR_in_orExpression2223 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_orExpression_in_orExpression2226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_notExpr_in_andExpression2239 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_AND_in_andExpression2242 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_andExpression_in_andExpression2245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notExpr2261 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_notExpr_in_notExpr2264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalExpr_in_notExpr2272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_addExpr_in_relationalExpr2286 = new BitSet(new long[]{0x000018FF00000002L});
    public static final BitSet FOLLOW_relationalToken_in_relationalExpr2289 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_relationalExpr_in_relationalExpr2292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REFEQUALS_in_relationalToken2305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REFNEQ_in_relationalToken2310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_standardRelationalToken_in_relationalToken2315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_standardRelationalToken0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplyExpr_in_addExpr2379 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_PLUS_in_addExpr2383 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_MINUS_in_addExpr2388 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_multiplyExpr_in_addExpr2392 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_expExpr_in_multiplyExpr2406 = new BitSet(new long[]{0x0000000000000982L});
    public static final BitSet FOLLOW_AST_in_multiplyExpr2410 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_FSLASH_in_multiplyExpr2415 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_PERCENT_in_multiplyExpr2420 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expExpr_in_multiplyExpr2424 = new BitSet(new long[]{0x0000000000000982L});
    public static final BitSet FOLLOW_plusminusExpr_in_expExpr2437 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_EXP_in_expExpr2440 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_plusminusExpr_in_expExpr2443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSMINUS_in_plusminusExpr2486 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_LPAREN_in_plusminusExpr2488 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_plusminusExpr2490 = new BitSet(new long[]{0x0000000000204000L});
    public static final BitSet FOLLOW_COMMA_in_plusminusExpr2493 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_plusminusExpr2495 = new BitSet(new long[]{0x0000000000204000L});
    public static final BitSet FOLLOW_RPAREN_in_plusminusExpr2499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSMINUS_in_plusminusExpr2516 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_unaryExpr_in_plusminusExpr2518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpr_in_plusminusExpr2534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unaryExpr2548 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_unaryExpr_in_unaryExpr2550 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_unaryExpr2566 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_unaryExpr_in_unaryExpr2568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_postfixExpr_in_unaryExpr2584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_upstarExpr_in_postfixExpr2597 = new BitSet(new long[]{0x000000000000A202L});
    public static final BitSet FOLLOW_DOT_in_postfixExpr2609 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_postfixExpr2611 = new BitSet(new long[]{0x000000000000A202L});
    public static final BitSet FOLLOW_arrayReference_in_postfixExpr2632 = new BitSet(new long[]{0x000000000000A202L});
    public static final BitSet FOLLOW_functionCall_in_postfixExpr2652 = new BitSet(new long[]{0x000000000000A202L});
    public static final BitSet FOLLOW_LBRACKET_in_arrayReference2683 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_arrayReference2685 = new BitSet(new long[]{0x0000000000210000L});
    public static final BitSet FOLLOW_COMMA_in_arrayReference2688 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_arrayReference2690 = new BitSet(new long[]{0x0000000000210000L});
    public static final BitSet FOLLOW_RBRACKET_in_arrayReference2694 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_functionCall2722 = new BitSet(new long[]{0xDC000000F002E0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_functionArgument_in_functionCall2725 = new BitSet(new long[]{0x0000000000204000L});
    public static final BitSet FOLLOW_COMMA_in_functionCall2728 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_functionArgument_in_functionCall2730 = new BitSet(new long[]{0x0000000000204000L});
    public static final BitSet FOLLOW_RPAREN_in_functionCall2736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_functionArgument2766 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_BIGRARROW_in_functionArgument2768 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_functionArgument2773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_upstarExpr2814 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_starExpr_in_upstarExpr2831 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_upstarExpr2839 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_primaryExpr_in_upstarExpr2848 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_starExpr_in_upstarExpr2850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_upstarExpr2858 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_primaryExpr_in_upstarExpr2867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_starExpr_in_upstarExpr2875 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARET_in_upstarExpr2883 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARETCARET_in_upstarExpr2887 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VEE_in_upstarExpr2891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VEEVEE_in_upstarExpr2895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primaryExpr_in_upstarExpr2903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AST_in_starExpr2946 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_primaryExpr_in_starExpr2948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AST_in_starExpr2964 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NIL_in_primaryExpr2983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THIS_in_primaryExpr2988 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_primaryExpr2993 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_primaryExpr2998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_in_primaryExpr3011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_primaryExpr3016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_primaryExpr3021 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHAR_in_primaryExpr3026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_primaryExpr3031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_primaryExpr3036 = new BitSet(new long[]{0xDC000000F042E0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_statementSequence_in_primaryExpr3039 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_RPAREN_in_primaryExpr3041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BEGIN_in_primaryExpr3047 = new BitSet(new long[]{0xDC000000F042A0F0L,0x03C0000DC4060AC9L});
    public static final BitSet FOLLOW_statementSequence_in_primaryExpr3050 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_END_in_primaryExpr3052 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_explicitGame_in_primaryExpr3071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_explicitMap_in_primaryExpr3087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_explicitSet_in_primaryExpr3092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_explicitList_in_primaryExpr3097 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_explicitGame3111 = new BitSet(new long[]{0xDC000000F002A0F0L,0x07C0000DC4060AC1L});
    public static final BitSet FOLLOW_slashExpression_in_explicitGame3114 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_RBRACE_in_explicitGame3116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_slashExpression3163 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_SLASHES_in_slashExpression3166 = new BitSet(new long[]{0xDC000000F002A0F0L,0x07C0000DC4060AC1L});
    public static final BitSet FOLLOW_slashExpression_in_slashExpression3170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_slashExpression3191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_explicitMap3210 = new BitSet(new long[]{0xDC000200F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_mapEntry_in_explicitMap3213 = new BitSet(new long[]{0x0000000000240000L});
    public static final BitSet FOLLOW_COMMA_in_explicitMap3216 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_mapEntry_in_explicitMap3218 = new BitSet(new long[]{0x0000000000240000L});
    public static final BitSet FOLLOW_BIGRARROW_in_explicitMap3224 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_RBRACE_in_explicitMap3227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_mapEntry3247 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_BIGRARROW_in_mapEntry3249 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_mapEntry3252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_explicitSet3263 = new BitSet(new long[]{0xDC000000F006A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_explicitSet3266 = new BitSet(new long[]{0x0000000000240000L});
    public static final BitSet FOLLOW_COMMA_in_explicitSet3269 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_explicitSet3271 = new BitSet(new long[]{0x0000000000240000L});
    public static final BitSet FOLLOW_RBRACE_in_explicitSet3277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_explicitList3297 = new BitSet(new long[]{0xDC000000F003A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_explicitList3300 = new BitSet(new long[]{0x0000000000210000L});
    public static final BitSet FOLLOW_COMMA_in_explicitList3303 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_explicitList3305 = new BitSet(new long[]{0x0000000000210000L});
    public static final BitSet FOLLOW_RBRACKET_in_explicitList3311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList3335 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_COMMA_in_expressionList3338 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_expressionList3340 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_INTEGER_in_range3367 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_DOTDOT_in_range3369 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_INTEGER_in_range3372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSMINUS_in_synpred1_Cgsuite2486 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_LPAREN_in_synpred1_Cgsuite2488 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_synpred1_Cgsuite2490 = new BitSet(new long[]{0x0000000000204000L});
    public static final BitSet FOLLOW_COMMA_in_synpred1_Cgsuite2493 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_synpred1_Cgsuite2495 = new BitSet(new long[]{0x0000000000204000L});
    public static final BitSet FOLLOW_RPAREN_in_synpred1_Cgsuite2499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSMINUS_in_synpred2_Cgsuite2516 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_unaryExpr_in_synpred2_Cgsuite2518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred3_Cgsuite2814 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_starExpr_in_synpred3_Cgsuite2831 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred4_Cgsuite2839 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_primaryExpr_in_synpred4_Cgsuite2848 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_starExpr_in_synpred4_Cgsuite2850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred5_Cgsuite2858 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_primaryExpr_in_synpred5_Cgsuite2867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARET_in_synpred7_Cgsuite2883 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARETCARET_in_synpred8_Cgsuite2887 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VEE_in_synpred9_Cgsuite2891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VEEVEE_in_synpred10_Cgsuite2895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AST_in_synpred11_Cgsuite2946 = new BitSet(new long[]{0xDC000000F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_primaryExpr_in_synpred11_Cgsuite2948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_synpred12_Cgsuite3004 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_DOTDOT_in_synpred12_Cgsuite3006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_synpred13_Cgsuite3062 = new BitSet(new long[]{0xDC000000F002A0F0L,0x07C0000DC4060AC1L});
    public static final BitSet FOLLOW_expressionList_in_synpred13_Cgsuite3064 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_SLASHES_in_synpred13_Cgsuite3066 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_synpred14_Cgsuite3077 = new BitSet(new long[]{0xDC000200F002A0F0L,0x03C0000DC4060AC1L});
    public static final BitSet FOLLOW_expression_in_synpred14_Cgsuite3079 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_BIGRARROW_in_synpred14_Cgsuite3082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_synpred15_Cgsuite3148 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_SLASHES_in_synpred15_Cgsuite3150 = new BitSet(new long[]{0x0000000000000002L});

}