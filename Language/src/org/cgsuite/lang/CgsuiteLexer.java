// $ANTLR 3.3 Nov 30, 2010 12:45:30 C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g 2011-02-15 18:42:04

    package org.cgsuite.lang;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"unchecked","all"}) public class CgsuiteLexer extends Lexer {
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

    public CgsuiteLexer() {;} 
    public CgsuiteLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public CgsuiteLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g"; }

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:11:6: ( '+' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:11:8: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUS"

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:12:7: ( '-' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:12:9: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MINUS"

    // $ANTLR start "PLUSMINUS"
    public final void mPLUSMINUS() throws RecognitionException {
        try {
            int _type = PLUSMINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:13:11: ( '+-' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:13:13: '+-'
            {
            match("+-"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUSMINUS"

    // $ANTLR start "AST"
    public final void mAST() throws RecognitionException {
        try {
            int _type = AST;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:14:5: ( '*' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:14:7: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AST"

    // $ANTLR start "FSLASH"
    public final void mFSLASH() throws RecognitionException {
        try {
            int _type = FSLASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:15:8: ( '/' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:15:10: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FSLASH"

    // $ANTLR start "DOT"
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:16:5: ( '.' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:16:7: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOT"

    // $ANTLR start "EXP"
    public final void mEXP() throws RecognitionException {
        try {
            int _type = EXP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:17:5: ( '**' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:17:7: '**'
            {
            match("**"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EXP"

    // $ANTLR start "PERCENT"
    public final void mPERCENT() throws RecognitionException {
        try {
            int _type = PERCENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:18:9: ( '%' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:18:11: '%'
            {
            match('%'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PERCENT"

    // $ANTLR start "UNDERSCORE"
    public final void mUNDERSCORE() throws RecognitionException {
        try {
            int _type = UNDERSCORE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:19:12: ( '_' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:19:14: '_'
            {
            match('_'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "UNDERSCORE"

    // $ANTLR start "LPAREN"
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:20:8: ( '(' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:20:10: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LPAREN"

    // $ANTLR start "RPAREN"
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:21:8: ( ')' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:21:10: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RPAREN"

    // $ANTLR start "LBRACKET"
    public final void mLBRACKET() throws RecognitionException {
        try {
            int _type = LBRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:22:10: ( '[' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:22:12: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LBRACKET"

    // $ANTLR start "RBRACKET"
    public final void mRBRACKET() throws RecognitionException {
        try {
            int _type = RBRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:23:10: ( ']' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:23:12: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RBRACKET"

    // $ANTLR start "LBRACE"
    public final void mLBRACE() throws RecognitionException {
        try {
            int _type = LBRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:24:8: ( '{' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:24:10: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LBRACE"

    // $ANTLR start "RBRACE"
    public final void mRBRACE() throws RecognitionException {
        try {
            int _type = RBRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:25:8: ( '}' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:25:10: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RBRACE"

    // $ANTLR start "SQUOTE"
    public final void mSQUOTE() throws RecognitionException {
        try {
            int _type = SQUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:26:8: ( '\\'' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:26:10: '\\''
            {
            match('\''); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SQUOTE"

    // $ANTLR start "DQUOTE"
    public final void mDQUOTE() throws RecognitionException {
        try {
            int _type = DQUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:27:8: ( '\"' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:27:10: '\"'
            {
            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DQUOTE"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:28:7: ( ',' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:28:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "SEMI"
    public final void mSEMI() throws RecognitionException {
        try {
            int _type = SEMI;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:29:6: ( ';' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:29:8: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SEMI"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:30:7: ( ':' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:30:9: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLON"

    // $ANTLR start "AMPERSAND"
    public final void mAMPERSAND() throws RecognitionException {
        try {
            int _type = AMPERSAND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:31:11: ( '&' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:31:13: '&'
            {
            match('&'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AMPERSAND"

    // $ANTLR start "TILDE"
    public final void mTILDE() throws RecognitionException {
        try {
            int _type = TILDE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:32:7: ( '~' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:32:9: '~'
            {
            match('~'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TILDE"

    // $ANTLR start "BANG"
    public final void mBANG() throws RecognitionException {
        try {
            int _type = BANG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:33:6: ( '!' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:33:8: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BANG"

    // $ANTLR start "QUESTION"
    public final void mQUESTION() throws RecognitionException {
        try {
            int _type = QUESTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:34:10: ( '?' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:34:12: '?'
            {
            match('?'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "QUESTION"

    // $ANTLR start "CARET"
    public final void mCARET() throws RecognitionException {
        try {
            int _type = CARET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:35:7: ( '^' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:35:9: '^'
            {
            match('^'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CARET"

    // $ANTLR start "CARETCARET"
    public final void mCARETCARET() throws RecognitionException {
        try {
            int _type = CARETCARET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:36:12: ( '^^' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:36:14: '^^'
            {
            match("^^"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CARETCARET"

    // $ANTLR start "VEE"
    public final void mVEE() throws RecognitionException {
        try {
            int _type = VEE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:37:5: ( 'v' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:37:7: 'v'
            {
            match('v'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VEE"

    // $ANTLR start "VEEVEE"
    public final void mVEEVEE() throws RecognitionException {
        try {
            int _type = VEEVEE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:38:8: ( 'vv' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:38:10: 'vv'
            {
            match("vv"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VEEVEE"

    // $ANTLR start "EQUALS"
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:39:8: ( '==' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:39:10: '=='
            {
            match("=="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQUALS"

    // $ANTLR start "NEQ"
    public final void mNEQ() throws RecognitionException {
        try {
            int _type = NEQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:40:5: ( '!=' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:40:7: '!='
            {
            match("!="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NEQ"

    // $ANTLR start "LT"
    public final void mLT() throws RecognitionException {
        try {
            int _type = LT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:41:4: ( '<' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:41:6: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LT"

    // $ANTLR start "GT"
    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:42:4: ( '>' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:42:6: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GT"

    // $ANTLR start "LEQ"
    public final void mLEQ() throws RecognitionException {
        try {
            int _type = LEQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:43:5: ( '<=' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:43:7: '<='
            {
            match("<="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEQ"

    // $ANTLR start "GEQ"
    public final void mGEQ() throws RecognitionException {
        try {
            int _type = GEQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:44:5: ( '>=' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:44:7: '>='
            {
            match(">="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GEQ"

    // $ANTLR start "CONFUSED"
    public final void mCONFUSED() throws RecognitionException {
        try {
            int _type = CONFUSED;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:45:10: ( '<>' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:45:12: '<>'
            {
            match("<>"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CONFUSED"

    // $ANTLR start "COMPARE"
    public final void mCOMPARE() throws RecognitionException {
        try {
            int _type = COMPARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:46:9: ( '<=>' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:46:11: '<=>'
            {
            match("<=>"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMPARE"

    // $ANTLR start "RARROW"
    public final void mRARROW() throws RecognitionException {
        try {
            int _type = RARROW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:47:8: ( '->' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:47:10: '->'
            {
            match("->"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RARROW"

    // $ANTLR start "BIGRARROW"
    public final void mBIGRARROW() throws RecognitionException {
        try {
            int _type = BIGRARROW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:48:11: ( '=>' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:48:13: '=>'
            {
            match("=>"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BIGRARROW"

    // $ANTLR start "BACKSLASH"
    public final void mBACKSLASH() throws RecognitionException {
        try {
            int _type = BACKSLASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:49:11: ( '\\\\' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:49:13: '\\\\'
            {
            match('\\'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BACKSLASH"

    // $ANTLR start "REFEQUALS"
    public final void mREFEQUALS() throws RecognitionException {
        try {
            int _type = REFEQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:50:11: ( '===' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:50:13: '==='
            {
            match("==="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "REFEQUALS"

    // $ANTLR start "REFNEQ"
    public final void mREFNEQ() throws RecognitionException {
        try {
            int _type = REFNEQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:51:8: ( '!==' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:51:10: '!=='
            {
            match("!=="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "REFNEQ"

    // $ANTLR start "ASSIGN"
    public final void mASSIGN() throws RecognitionException {
        try {
            int _type = ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:52:8: ( ':=' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:52:10: ':='
            {
            match(":="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASSIGN"

    // $ANTLR start "ASN_PLUS"
    public final void mASN_PLUS() throws RecognitionException {
        try {
            int _type = ASN_PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:53:10: ( '+=' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:53:12: '+='
            {
            match("+="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASN_PLUS"

    // $ANTLR start "ASN_MINUS"
    public final void mASN_MINUS() throws RecognitionException {
        try {
            int _type = ASN_MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:54:11: ( '-=' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:54:13: '-='
            {
            match("-="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASN_MINUS"

    // $ANTLR start "ASN_TIMES"
    public final void mASN_TIMES() throws RecognitionException {
        try {
            int _type = ASN_TIMES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:55:11: ( '*=' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:55:13: '*='
            {
            match("*="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASN_TIMES"

    // $ANTLR start "ASN_DIV"
    public final void mASN_DIV() throws RecognitionException {
        try {
            int _type = ASN_DIV;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:56:9: ( '/=' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:56:11: '/='
            {
            match("/="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASN_DIV"

    // $ANTLR start "ASN_MOD"
    public final void mASN_MOD() throws RecognitionException {
        try {
            int _type = ASN_MOD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:57:9: ( '%=' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:57:11: '%='
            {
            match("%="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASN_MOD"

    // $ANTLR start "ASN_AND"
    public final void mASN_AND() throws RecognitionException {
        try {
            int _type = ASN_AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:58:9: ( '&=' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:58:11: '&='
            {
            match("&="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASN_AND"

    // $ANTLR start "ASN_OR"
    public final void mASN_OR() throws RecognitionException {
        try {
            int _type = ASN_OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:59:8: ( '|=' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:59:10: '|='
            {
            match("|="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASN_OR"

    // $ANTLR start "ASN_XOR"
    public final void mASN_XOR() throws RecognitionException {
        try {
            int _type = ASN_XOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:60:9: ( '^=' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:60:11: '^='
            {
            match("^="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASN_XOR"

    // $ANTLR start "ASN_EXP"
    public final void mASN_EXP() throws RecognitionException {
        try {
            int _type = ASN_EXP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:61:9: ( '**=' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:61:11: '**='
            {
            match("**="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASN_EXP"

    // $ANTLR start "DOTDOT"
    public final void mDOTDOT() throws RecognitionException {
        try {
            int _type = DOTDOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:62:8: ( '..' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:62:10: '..'
            {
            match(".."); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOTDOT"

    // $ANTLR start "DOTDOTDOT"
    public final void mDOTDOTDOT() throws RecognitionException {
        try {
            int _type = DOTDOTDOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:63:11: ( '...' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:63:13: '...'
            {
            match("..."); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOTDOTDOT"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:64:5: ( 'and' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:64:7: 'and'
            {
            match("and"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "BEGIN"
    public final void mBEGIN() throws RecognitionException {
        try {
            int _type = BEGIN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:65:7: ( 'begin' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:65:9: 'begin'
            {
            match("begin"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BEGIN"

    // $ANTLR start "BREAK"
    public final void mBREAK() throws RecognitionException {
        try {
            int _type = BREAK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:66:7: ( 'break' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:66:9: 'break'
            {
            match("break"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BREAK"

    // $ANTLR start "BY"
    public final void mBY() throws RecognitionException {
        try {
            int _type = BY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:67:4: ( 'by' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:67:6: 'by'
            {
            match("by"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BY"

    // $ANTLR start "CLASS"
    public final void mCLASS() throws RecognitionException {
        try {
            int _type = CLASS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:68:7: ( 'class' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:68:9: 'class'
            {
            match("class"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CLASS"

    // $ANTLR start "CLEAR"
    public final void mCLEAR() throws RecognitionException {
        try {
            int _type = CLEAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:69:7: ( 'clear' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:69:9: 'clear'
            {
            match("clear"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CLEAR"

    // $ANTLR start "CONTINUE"
    public final void mCONTINUE() throws RecognitionException {
        try {
            int _type = CONTINUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:70:10: ( 'continue' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:70:12: 'continue'
            {
            match("continue"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CONTINUE"

    // $ANTLR start "DO"
    public final void mDO() throws RecognitionException {
        try {
            int _type = DO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:71:4: ( 'do' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:71:6: 'do'
            {
            match("do"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DO"

    // $ANTLR start "ELSE"
    public final void mELSE() throws RecognitionException {
        try {
            int _type = ELSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:72:6: ( 'else' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:72:8: 'else'
            {
            match("else"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ELSE"

    // $ANTLR start "ELSEIF"
    public final void mELSEIF() throws RecognitionException {
        try {
            int _type = ELSEIF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:73:8: ( 'elseif' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:73:10: 'elseif'
            {
            match("elseif"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ELSEIF"

    // $ANTLR start "END"
    public final void mEND() throws RecognitionException {
        try {
            int _type = END;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:74:5: ( 'end' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:74:7: 'end'
            {
            match("end"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "END"

    // $ANTLR start "ENUM"
    public final void mENUM() throws RecognitionException {
        try {
            int _type = ENUM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:75:6: ( 'enum' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:75:8: 'enum'
            {
            match("enum"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ENUM"

    // $ANTLR start "EXTENDS"
    public final void mEXTENDS() throws RecognitionException {
        try {
            int _type = EXTENDS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:76:9: ( 'extends' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:76:11: 'extends'
            {
            match("extends"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EXTENDS"

    // $ANTLR start "FALSE"
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:77:7: ( 'false' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:77:9: 'false'
            {
            match("false"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FALSE"

    // $ANTLR start "FOR"
    public final void mFOR() throws RecognitionException {
        try {
            int _type = FOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:78:5: ( 'for' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:78:7: 'for'
            {
            match("for"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FOR"

    // $ANTLR start "FOREACH"
    public final void mFOREACH() throws RecognitionException {
        try {
            int _type = FOREACH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:79:9: ( 'foreach' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:79:11: 'foreach'
            {
            match("foreach"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FOREACH"

    // $ANTLR start "FROM"
    public final void mFROM() throws RecognitionException {
        try {
            int _type = FROM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:80:6: ( 'from' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:80:8: 'from'
            {
            match("from"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FROM"

    // $ANTLR start "GET"
    public final void mGET() throws RecognitionException {
        try {
            int _type = GET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:81:5: ( 'get' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:81:7: 'get'
            {
            match("get"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GET"

    // $ANTLR start "IF"
    public final void mIF() throws RecognitionException {
        try {
            int _type = IF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:82:4: ( 'if' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:82:6: 'if'
            {
            match("if"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IF"

    // $ANTLR start "IMMUTABLE"
    public final void mIMMUTABLE() throws RecognitionException {
        try {
            int _type = IMMUTABLE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:83:11: ( 'immutable' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:83:13: 'immutable'
            {
            match("immutable"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IMMUTABLE"

    // $ANTLR start "IN"
    public final void mIN() throws RecognitionException {
        try {
            int _type = IN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:84:4: ( 'in' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:84:6: 'in'
            {
            match("in"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IN"

    // $ANTLR start "JAVA"
    public final void mJAVA() throws RecognitionException {
        try {
            int _type = JAVA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:85:6: ( 'java' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:85:8: 'java'
            {
            match("java"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "JAVA"

    // $ANTLR start "METHOD"
    public final void mMETHOD() throws RecognitionException {
        try {
            int _type = METHOD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:86:8: ( 'method' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:86:10: 'method'
            {
            match("method"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "METHOD"

    // $ANTLR start "NEG"
    public final void mNEG() throws RecognitionException {
        try {
            int _type = NEG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:87:5: ( 'neg' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:87:7: 'neg'
            {
            match("neg"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NEG"

    // $ANTLR start "NIL"
    public final void mNIL() throws RecognitionException {
        try {
            int _type = NIL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:88:5: ( 'nil' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:88:7: 'nil'
            {
            match("nil"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NIL"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:89:5: ( 'not' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:89:7: 'not'
            {
            match("not"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "OP"
    public final void mOP() throws RecognitionException {
        try {
            int _type = OP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:90:4: ( 'op' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:90:6: 'op'
            {
            match("op"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OP"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:91:4: ( 'or' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:91:6: 'or'
            {
            match("or"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "POS"
    public final void mPOS() throws RecognitionException {
        try {
            int _type = POS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:92:5: ( 'pos' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:92:7: 'pos'
            {
            match("pos"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "POS"

    // $ANTLR start "PRIVATE"
    public final void mPRIVATE() throws RecognitionException {
        try {
            int _type = PRIVATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:93:9: ( 'private' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:93:11: 'private'
            {
            match("private"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PRIVATE"

    // $ANTLR start "PROPERTY"
    public final void mPROPERTY() throws RecognitionException {
        try {
            int _type = PROPERTY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:94:10: ( 'property' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:94:12: 'property'
            {
            match("property"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PROPERTY"

    // $ANTLR start "PROTECTED"
    public final void mPROTECTED() throws RecognitionException {
        try {
            int _type = PROTECTED;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:95:11: ( 'protected' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:95:13: 'protected'
            {
            match("protected"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PROTECTED"

    // $ANTLR start "PUBLIC"
    public final void mPUBLIC() throws RecognitionException {
        try {
            int _type = PUBLIC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:96:8: ( 'public' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:96:10: 'public'
            {
            match("public"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PUBLIC"

    // $ANTLR start "RETURN"
    public final void mRETURN() throws RecognitionException {
        try {
            int _type = RETURN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:97:8: ( 'return' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:97:10: 'return'
            {
            match("return"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RETURN"

    // $ANTLR start "SET"
    public final void mSET() throws RecognitionException {
        try {
            int _type = SET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:98:5: ( 'set' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:98:7: 'set'
            {
            match("set"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SET"

    // $ANTLR start "STATIC"
    public final void mSTATIC() throws RecognitionException {
        try {
            int _type = STATIC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:99:8: ( 'static' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:99:10: 'static'
            {
            match("static"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STATIC"

    // $ANTLR start "THEN"
    public final void mTHEN() throws RecognitionException {
        try {
            int _type = THEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:100:6: ( 'then' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:100:8: 'then'
            {
            match("then"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "THEN"

    // $ANTLR start "THIS"
    public final void mTHIS() throws RecognitionException {
        try {
            int _type = THIS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:101:6: ( 'this' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:101:8: 'this'
            {
            match("this"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "THIS"

    // $ANTLR start "TO"
    public final void mTO() throws RecognitionException {
        try {
            int _type = TO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:102:4: ( 'to' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:102:6: 'to'
            {
            match("to"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TO"

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:103:6: ( 'true' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:103:8: 'true'
            {
            match("true"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TRUE"

    // $ANTLR start "VAR"
    public final void mVAR() throws RecognitionException {
        try {
            int _type = VAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:104:5: ( 'var' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:104:7: 'var'
            {
            match("var"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VAR"

    // $ANTLR start "WHERE"
    public final void mWHERE() throws RecognitionException {
        try {
            int _type = WHERE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:105:7: ( 'where' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:105:9: 'where'
            {
            match("where"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHERE"

    // $ANTLR start "WHILE"
    public final void mWHILE() throws RecognitionException {
        try {
            int _type = WHILE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:106:7: ( 'while' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:106:9: 'while'
            {
            match("while"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHILE"

    // $ANTLR start "INTEGER"
    public final void mINTEGER() throws RecognitionException {
        try {
            int _type = INTEGER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:564:10: ( ( DIGIT )+ )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:564:12: ( DIGIT )+
            {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:564:12: ( DIGIT )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:564:12: DIGIT
            	    {
            	    mDIGIT(); 

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INTEGER"

    // $ANTLR start "IDENTIFIER"
    public final void mIDENTIFIER() throws RecognitionException {
        try {
            int _type = IDENTIFIER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:566:12: ( LETTER ( LETTER | DIGIT )* )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:566:14: LETTER ( LETTER | DIGIT )*
            {
            mLETTER(); 
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:566:21: ( LETTER | DIGIT )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='0' && LA2_0<='9')||(LA2_0>='A' && LA2_0<='Z')||LA2_0=='_'||(LA2_0>='a' && LA2_0<='z')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IDENTIFIER"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:568:9: ( DQUOTE (~ ( DQUOTE | BACKSLASH | '\\n' | '\\r' ) | ESCAPE_SEQ )* DQUOTE )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:568:11: DQUOTE (~ ( DQUOTE | BACKSLASH | '\\n' | '\\r' ) | ESCAPE_SEQ )* DQUOTE
            {
            mDQUOTE(); 
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:568:18: (~ ( DQUOTE | BACKSLASH | '\\n' | '\\r' ) | ESCAPE_SEQ )*
            loop3:
            do {
                int alt3=3;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='\u0000' && LA3_0<='\t')||(LA3_0>='\u000B' && LA3_0<='\f')||(LA3_0>='\u000E' && LA3_0<='!')||(LA3_0>='#' && LA3_0<='[')||(LA3_0>=']' && LA3_0<='\uFFFF')) ) {
                    alt3=1;
                }
                else if ( (LA3_0=='\\') ) {
                    alt3=2;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:568:19: ~ ( DQUOTE | BACKSLASH | '\\n' | '\\r' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;
            	case 2 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:568:51: ESCAPE_SEQ
            	    {
            	    mESCAPE_SEQ(); 

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            mDQUOTE(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "CHAR"
    public final void mCHAR() throws RecognitionException {
        try {
            int _type = CHAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:570:7: ( SQUOTE (~ ( SQUOTE | BACKSLASH | '\\n' | '\\r' ) | ESCAPE_SEQ ) SQUOTE )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:570:9: SQUOTE (~ ( SQUOTE | BACKSLASH | '\\n' | '\\r' ) | ESCAPE_SEQ ) SQUOTE
            {
            mSQUOTE(); 
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:570:16: (~ ( SQUOTE | BACKSLASH | '\\n' | '\\r' ) | ESCAPE_SEQ )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( ((LA4_0>='\u0000' && LA4_0<='\t')||(LA4_0>='\u000B' && LA4_0<='\f')||(LA4_0>='\u000E' && LA4_0<='&')||(LA4_0>='(' && LA4_0<='[')||(LA4_0>=']' && LA4_0<='\uFFFF')) ) {
                alt4=1;
            }
            else if ( (LA4_0=='\\') ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:570:17: ~ ( SQUOTE | BACKSLASH | '\\n' | '\\r' )
                    {
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:570:49: ESCAPE_SEQ
                    {
                    mESCAPE_SEQ(); 

                    }
                    break;

            }

            mSQUOTE(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CHAR"

    // $ANTLR start "SLASHES"
    public final void mSLASHES() throws RecognitionException {
        try {
            int _type = SLASHES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:572:10: ( ( SLASH )+ )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:572:12: ( SLASH )+
            {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:572:12: ( SLASH )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0=='|') ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:572:12: SLASH
            	    {
            	    mSLASH(); 

            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SLASHES"

    // $ANTLR start "DIGIT"
    public final void mDIGIT() throws RecognitionException {
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:575:8: ( '0' .. '9' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:575:10: '0' .. '9'
            {
            matchRange('0','9'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "DIGIT"

    // $ANTLR start "HEX_DIGIT"
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:578:11: ( '0' .. '9' | 'A' .. 'F' | 'a' .. 'f' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "HEX_DIGIT"

    // $ANTLR start "LETTER"
    public final void mLETTER() throws RecognitionException {
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:581:9: ( UC_LETTER | LC_LETTER | UNDERSCORE )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "LETTER"

    // $ANTLR start "UC_LETTER"
    public final void mUC_LETTER() throws RecognitionException {
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:584:11: ( 'A' .. 'Z' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:584:13: 'A' .. 'Z'
            {
            matchRange('A','Z'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "UC_LETTER"

    // $ANTLR start "LC_LETTER"
    public final void mLC_LETTER() throws RecognitionException {
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:587:11: ( 'a' .. 'z' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:587:13: 'a' .. 'z'
            {
            matchRange('a','z'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "LC_LETTER"

    // $ANTLR start "SLASH"
    public final void mSLASH() throws RecognitionException {
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:590:8: ( '|' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:590:10: '|'
            {
            match('|'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "SLASH"

    // $ANTLR start "ESCAPE_SEQ"
    public final void mESCAPE_SEQ() throws RecognitionException {
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:593:12: ( BACKSLASH ( BACKSLASH | 'n' | 'r' | 't' | SQUOTE | DQUOTE | 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT ) )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:593:14: BACKSLASH ( BACKSLASH | 'n' | 'r' | 't' | SQUOTE | DQUOTE | 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT )
            {
            mBACKSLASH(); 
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:594:6: ( BACKSLASH | 'n' | 'r' | 't' | SQUOTE | DQUOTE | 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT )
            int alt6=7;
            switch ( input.LA(1) ) {
            case '\\':
                {
                alt6=1;
                }
                break;
            case 'n':
                {
                alt6=2;
                }
                break;
            case 'r':
                {
                alt6=3;
                }
                break;
            case 't':
                {
                alt6=4;
                }
                break;
            case '\'':
                {
                alt6=5;
                }
                break;
            case '\"':
                {
                alt6=6;
                }
                break;
            case 'u':
                {
                alt6=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:594:8: BACKSLASH
                    {
                    mBACKSLASH(); 

                    }
                    break;
                case 2 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:595:8: 'n'
                    {
                    match('n'); 

                    }
                    break;
                case 3 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:596:8: 'r'
                    {
                    match('r'); 

                    }
                    break;
                case 4 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:597:8: 't'
                    {
                    match('t'); 

                    }
                    break;
                case 5 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:598:8: SQUOTE
                    {
                    mSQUOTE(); 

                    }
                    break;
                case 6 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:599:8: DQUOTE
                    {
                    mDQUOTE(); 

                    }
                    break;
                case 7 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:600:8: 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
                    {
                    match('u'); 
                    mHEX_DIGIT(); 
                    mHEX_DIGIT(); 
                    mHEX_DIGIT(); 
                    mHEX_DIGIT(); 

                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "ESCAPE_SEQ"

    // $ANTLR start "NEWLINE"
    public final void mNEWLINE() throws RecognitionException {
        try {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:605:13: ( ( '\\r' )? '\\n' )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:605:15: ( '\\r' )? '\\n'
            {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:605:15: ( '\\r' )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='\r') ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:605:15: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }

            match('\n'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "NEWLINE"

    // $ANTLR start "WHITESPACE"
    public final void mWHITESPACE() throws RecognitionException {
        try {
            int _type = WHITESPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:607:14: ( ( ' ' | '\\t' | NEWLINE )+ )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:607:16: ( ' ' | '\\t' | NEWLINE )+
            {
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:607:16: ( ' ' | '\\t' | NEWLINE )+
            int cnt8=0;
            loop8:
            do {
                int alt8=4;
                switch ( input.LA(1) ) {
                case ' ':
                    {
                    alt8=1;
                    }
                    break;
                case '\t':
                    {
                    alt8=2;
                    }
                    break;
                case '\n':
                case '\r':
                    {
                    alt8=3;
                    }
                    break;

                }

                switch (alt8) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:607:17: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;
            	case 2 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:607:23: '\\t'
            	    {
            	    match('\t'); 

            	    }
            	    break;
            	case 3 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:607:30: NEWLINE
            	    {
            	    mNEWLINE(); 

            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);

             _channel = HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHITESPACE"

    // $ANTLR start "SL_COMMENT"
    public final void mSL_COMMENT() throws RecognitionException {
        try {
            int _type = SL_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:609:14: ( '//' (~ ( '\\r' | '\\n' ) )* NEWLINE )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:609:16: '//' (~ ( '\\r' | '\\n' ) )* NEWLINE
            {
            match("//"); 

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:609:21: (~ ( '\\r' | '\\n' ) )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0>='\u0000' && LA9_0<='\t')||(LA9_0>='\u000B' && LA9_0<='\f')||(LA9_0>='\u000E' && LA9_0<='\uFFFF')) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:609:21: ~ ( '\\r' | '\\n' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            mNEWLINE(); 
             _channel = HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SL_COMMENT"

    // $ANTLR start "ML_COMMENT"
    public final void mML_COMMENT() throws RecognitionException {
        try {
            int _type = ML_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:611:12: ( '/*' (~ ( '*' ) | '*' ~ ( '/' ) )* ( '*/' )? )
            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:611:14: '/*' (~ ( '*' ) | '*' ~ ( '/' ) )* ( '*/' )?
            {
            match("/*"); 

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:611:19: (~ ( '*' ) | '*' ~ ( '/' ) )*
            loop10:
            do {
                int alt10=3;
                int LA10_0 = input.LA(1);

                if ( (LA10_0=='*') ) {
                    int LA10_1 = input.LA(2);

                    if ( ((LA10_1>='\u0000' && LA10_1<='.')||(LA10_1>='0' && LA10_1<='\uFFFF')) ) {
                        alt10=2;
                    }


                }
                else if ( ((LA10_0>='\u0000' && LA10_0<=')')||(LA10_0>='+' && LA10_0<='\uFFFF')) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:611:21: ~ ( '*' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<=')')||(input.LA(1)>='+' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;
            	case 2 :
            	    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:611:30: '*' ~ ( '/' )
            	    {
            	    match('*'); 
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='.')||(input.LA(1)>='0' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

            // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:611:45: ( '*/' )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0=='*') ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:611:45: '*/'
                    {
                    match("*/"); 


                    }
                    break;

            }

             _channel = HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ML_COMMENT"

    public void mTokens() throws RecognitionException {
        // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:8: ( PLUS | MINUS | PLUSMINUS | AST | FSLASH | DOT | EXP | PERCENT | UNDERSCORE | LPAREN | RPAREN | LBRACKET | RBRACKET | LBRACE | RBRACE | SQUOTE | DQUOTE | COMMA | SEMI | COLON | AMPERSAND | TILDE | BANG | QUESTION | CARET | CARETCARET | VEE | VEEVEE | EQUALS | NEQ | LT | GT | LEQ | GEQ | CONFUSED | COMPARE | RARROW | BIGRARROW | BACKSLASH | REFEQUALS | REFNEQ | ASSIGN | ASN_PLUS | ASN_MINUS | ASN_TIMES | ASN_DIV | ASN_MOD | ASN_AND | ASN_OR | ASN_XOR | ASN_EXP | DOTDOT | DOTDOTDOT | AND | BEGIN | BREAK | BY | CLASS | CLEAR | CONTINUE | DO | ELSE | ELSEIF | END | ENUM | EXTENDS | FALSE | FOR | FOREACH | FROM | GET | IF | IMMUTABLE | IN | JAVA | METHOD | NEG | NIL | NOT | OP | OR | POS | PRIVATE | PROPERTY | PROTECTED | PUBLIC | RETURN | SET | STATIC | THEN | THIS | TO | TRUE | VAR | WHERE | WHILE | INTEGER | IDENTIFIER | STRING | CHAR | SLASHES | WHITESPACE | SL_COMMENT | ML_COMMENT )
        int alt12=104;
        alt12 = dfa12.predict(input);
        switch (alt12) {
            case 1 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:10: PLUS
                {
                mPLUS(); 

                }
                break;
            case 2 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:15: MINUS
                {
                mMINUS(); 

                }
                break;
            case 3 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:21: PLUSMINUS
                {
                mPLUSMINUS(); 

                }
                break;
            case 4 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:31: AST
                {
                mAST(); 

                }
                break;
            case 5 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:35: FSLASH
                {
                mFSLASH(); 

                }
                break;
            case 6 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:42: DOT
                {
                mDOT(); 

                }
                break;
            case 7 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:46: EXP
                {
                mEXP(); 

                }
                break;
            case 8 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:50: PERCENT
                {
                mPERCENT(); 

                }
                break;
            case 9 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:58: UNDERSCORE
                {
                mUNDERSCORE(); 

                }
                break;
            case 10 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:69: LPAREN
                {
                mLPAREN(); 

                }
                break;
            case 11 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:76: RPAREN
                {
                mRPAREN(); 

                }
                break;
            case 12 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:83: LBRACKET
                {
                mLBRACKET(); 

                }
                break;
            case 13 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:92: RBRACKET
                {
                mRBRACKET(); 

                }
                break;
            case 14 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:101: LBRACE
                {
                mLBRACE(); 

                }
                break;
            case 15 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:108: RBRACE
                {
                mRBRACE(); 

                }
                break;
            case 16 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:115: SQUOTE
                {
                mSQUOTE(); 

                }
                break;
            case 17 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:122: DQUOTE
                {
                mDQUOTE(); 

                }
                break;
            case 18 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:129: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 19 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:135: SEMI
                {
                mSEMI(); 

                }
                break;
            case 20 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:140: COLON
                {
                mCOLON(); 

                }
                break;
            case 21 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:146: AMPERSAND
                {
                mAMPERSAND(); 

                }
                break;
            case 22 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:156: TILDE
                {
                mTILDE(); 

                }
                break;
            case 23 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:162: BANG
                {
                mBANG(); 

                }
                break;
            case 24 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:167: QUESTION
                {
                mQUESTION(); 

                }
                break;
            case 25 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:176: CARET
                {
                mCARET(); 

                }
                break;
            case 26 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:182: CARETCARET
                {
                mCARETCARET(); 

                }
                break;
            case 27 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:193: VEE
                {
                mVEE(); 

                }
                break;
            case 28 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:197: VEEVEE
                {
                mVEEVEE(); 

                }
                break;
            case 29 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:204: EQUALS
                {
                mEQUALS(); 

                }
                break;
            case 30 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:211: NEQ
                {
                mNEQ(); 

                }
                break;
            case 31 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:215: LT
                {
                mLT(); 

                }
                break;
            case 32 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:218: GT
                {
                mGT(); 

                }
                break;
            case 33 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:221: LEQ
                {
                mLEQ(); 

                }
                break;
            case 34 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:225: GEQ
                {
                mGEQ(); 

                }
                break;
            case 35 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:229: CONFUSED
                {
                mCONFUSED(); 

                }
                break;
            case 36 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:238: COMPARE
                {
                mCOMPARE(); 

                }
                break;
            case 37 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:246: RARROW
                {
                mRARROW(); 

                }
                break;
            case 38 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:253: BIGRARROW
                {
                mBIGRARROW(); 

                }
                break;
            case 39 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:263: BACKSLASH
                {
                mBACKSLASH(); 

                }
                break;
            case 40 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:273: REFEQUALS
                {
                mREFEQUALS(); 

                }
                break;
            case 41 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:283: REFNEQ
                {
                mREFNEQ(); 

                }
                break;
            case 42 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:290: ASSIGN
                {
                mASSIGN(); 

                }
                break;
            case 43 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:297: ASN_PLUS
                {
                mASN_PLUS(); 

                }
                break;
            case 44 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:306: ASN_MINUS
                {
                mASN_MINUS(); 

                }
                break;
            case 45 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:316: ASN_TIMES
                {
                mASN_TIMES(); 

                }
                break;
            case 46 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:326: ASN_DIV
                {
                mASN_DIV(); 

                }
                break;
            case 47 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:334: ASN_MOD
                {
                mASN_MOD(); 

                }
                break;
            case 48 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:342: ASN_AND
                {
                mASN_AND(); 

                }
                break;
            case 49 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:350: ASN_OR
                {
                mASN_OR(); 

                }
                break;
            case 50 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:357: ASN_XOR
                {
                mASN_XOR(); 

                }
                break;
            case 51 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:365: ASN_EXP
                {
                mASN_EXP(); 

                }
                break;
            case 52 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:373: DOTDOT
                {
                mDOTDOT(); 

                }
                break;
            case 53 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:380: DOTDOTDOT
                {
                mDOTDOTDOT(); 

                }
                break;
            case 54 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:390: AND
                {
                mAND(); 

                }
                break;
            case 55 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:394: BEGIN
                {
                mBEGIN(); 

                }
                break;
            case 56 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:400: BREAK
                {
                mBREAK(); 

                }
                break;
            case 57 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:406: BY
                {
                mBY(); 

                }
                break;
            case 58 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:409: CLASS
                {
                mCLASS(); 

                }
                break;
            case 59 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:415: CLEAR
                {
                mCLEAR(); 

                }
                break;
            case 60 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:421: CONTINUE
                {
                mCONTINUE(); 

                }
                break;
            case 61 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:430: DO
                {
                mDO(); 

                }
                break;
            case 62 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:433: ELSE
                {
                mELSE(); 

                }
                break;
            case 63 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:438: ELSEIF
                {
                mELSEIF(); 

                }
                break;
            case 64 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:445: END
                {
                mEND(); 

                }
                break;
            case 65 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:449: ENUM
                {
                mENUM(); 

                }
                break;
            case 66 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:454: EXTENDS
                {
                mEXTENDS(); 

                }
                break;
            case 67 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:462: FALSE
                {
                mFALSE(); 

                }
                break;
            case 68 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:468: FOR
                {
                mFOR(); 

                }
                break;
            case 69 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:472: FOREACH
                {
                mFOREACH(); 

                }
                break;
            case 70 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:480: FROM
                {
                mFROM(); 

                }
                break;
            case 71 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:485: GET
                {
                mGET(); 

                }
                break;
            case 72 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:489: IF
                {
                mIF(); 

                }
                break;
            case 73 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:492: IMMUTABLE
                {
                mIMMUTABLE(); 

                }
                break;
            case 74 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:502: IN
                {
                mIN(); 

                }
                break;
            case 75 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:505: JAVA
                {
                mJAVA(); 

                }
                break;
            case 76 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:510: METHOD
                {
                mMETHOD(); 

                }
                break;
            case 77 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:517: NEG
                {
                mNEG(); 

                }
                break;
            case 78 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:521: NIL
                {
                mNIL(); 

                }
                break;
            case 79 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:525: NOT
                {
                mNOT(); 

                }
                break;
            case 80 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:529: OP
                {
                mOP(); 

                }
                break;
            case 81 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:532: OR
                {
                mOR(); 

                }
                break;
            case 82 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:535: POS
                {
                mPOS(); 

                }
                break;
            case 83 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:539: PRIVATE
                {
                mPRIVATE(); 

                }
                break;
            case 84 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:547: PROPERTY
                {
                mPROPERTY(); 

                }
                break;
            case 85 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:556: PROTECTED
                {
                mPROTECTED(); 

                }
                break;
            case 86 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:566: PUBLIC
                {
                mPUBLIC(); 

                }
                break;
            case 87 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:573: RETURN
                {
                mRETURN(); 

                }
                break;
            case 88 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:580: SET
                {
                mSET(); 

                }
                break;
            case 89 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:584: STATIC
                {
                mSTATIC(); 

                }
                break;
            case 90 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:591: THEN
                {
                mTHEN(); 

                }
                break;
            case 91 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:596: THIS
                {
                mTHIS(); 

                }
                break;
            case 92 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:601: TO
                {
                mTO(); 

                }
                break;
            case 93 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:604: TRUE
                {
                mTRUE(); 

                }
                break;
            case 94 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:609: VAR
                {
                mVAR(); 

                }
                break;
            case 95 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:613: WHERE
                {
                mWHERE(); 

                }
                break;
            case 96 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:619: WHILE
                {
                mWHILE(); 

                }
                break;
            case 97 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:625: INTEGER
                {
                mINTEGER(); 

                }
                break;
            case 98 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:633: IDENTIFIER
                {
                mIDENTIFIER(); 

                }
                break;
            case 99 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:644: STRING
                {
                mSTRING(); 

                }
                break;
            case 100 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:651: CHAR
                {
                mCHAR(); 

                }
                break;
            case 101 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:656: SLASHES
                {
                mSLASHES(); 

                }
                break;
            case 102 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:664: WHITESPACE
                {
                mWHITESPACE(); 

                }
                break;
            case 103 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:675: SL_COMMENT
                {
                mSL_COMMENT(); 

                }
                break;
            case 104 :
                // C:\\Users\\asiegel\\Documents\\NetBeansProjects\\CGSuite\\Language\\src\\org\\cgsuite\\lang\\Cgsuite.g:1:686: ML_COMMENT
                {
                mML_COMMENT(); 

                }
                break;

        }

    }


    protected DFA12 dfa12 = new DFA12(this);
    static final String DFA12_eotS =
        "\1\uffff\1\64\1\67\1\72\1\76\1\100\1\102\1\103\6\uffff\1\104\1"+
        "\106\2\uffff\1\111\1\113\1\uffff\1\115\1\uffff\1\120\1\123\1\uffff"+
        "\1\130\1\132\1\uffff\1\134\21\60\11\uffff\1\u0080\6\uffff\1\u0082"+
        "\14\uffff\1\u0084\4\uffff\1\u0085\1\60\1\uffff\1\u0088\1\uffff\1"+
        "\u008a\6\uffff\3\60\1\u008e\2\60\1\u0092\7\60\1\u009b\1\60\1\u009d"+
        "\5\60\1\u00a3\1\u00a4\7\60\1\u00ae\2\60\7\uffff\1\u00b2\4\uffff"+
        "\1\u00b3\2\60\1\uffff\3\60\1\uffff\1\60\1\u00ba\3\60\1\u00bf\1\60"+
        "\1\u00c1\1\uffff\1\60\1\uffff\2\60\1\u00c5\1\u00c6\1\u00c7\2\uffff"+
        "\1\u00c8\4\60\1\u00ce\3\60\1\uffff\3\60\2\uffff\5\60\1\u00db\1\uffff"+
        "\1\u00dc\3\60\1\uffff\1\u00e0\1\uffff\1\60\1\u00e2\1\60\4\uffff"+
        "\5\60\1\uffff\1\60\1\u00ea\1\u00eb\1\u00ec\2\60\1\u00ef\1\u00f0"+
        "\1\u00f1\1\u00f2\2\60\2\uffff\1\60\1\u00f6\1\60\1\uffff\1\60\1\uffff"+
        "\7\60\3\uffff\1\u0100\1\u0101\4\uffff\1\60\1\u0103\1\60\1\uffff"+
        "\2\60\1\u0107\3\60\1\u010b\1\u010c\1\u010d\2\uffff\1\60\1\uffff"+
        "\1\u010f\1\u0110\1\60\1\uffff\1\u0112\2\60\3\uffff\1\u0115\2\uffff"+
        "\1\60\1\uffff\1\u0117\1\60\1\uffff\1\u0119\1\uffff\1\u011a\2\uffff";
    static final String DFA12_eofS =
        "\u011b\uffff";
    static final String DFA12_minS =
        "\1\11\1\55\1\75\2\52\1\56\1\75\1\60\6\uffff\2\0\2\uffff\2\75\1"+
        "\uffff\1\75\1\uffff\1\75\1\60\3\75\1\uffff\1\75\1\156\1\145\1\154"+
        "\1\157\1\154\1\141\1\145\1\146\1\141\2\145\1\160\1\157\2\145\2\150"+
        "\11\uffff\1\75\6\uffff\1\56\14\uffff\1\75\4\uffff\1\60\1\162\1\uffff"+
        "\1\75\1\uffff\1\76\6\uffff\1\144\1\147\1\145\1\60\1\141\1\156\1"+
        "\60\1\163\1\144\1\164\1\154\1\162\1\157\1\164\1\60\1\155\1\60\1"+
        "\166\1\164\1\147\1\154\1\164\2\60\1\163\1\151\1\142\2\164\1\141"+
        "\1\145\1\60\1\165\1\145\7\uffff\1\60\4\uffff\1\60\1\151\1\141\1"+
        "\uffff\1\163\1\141\1\164\1\uffff\1\145\1\60\1\155\1\145\1\163\1"+
        "\60\1\155\1\60\1\uffff\1\165\1\uffff\1\141\1\150\3\60\2\uffff\1"+
        "\60\1\166\1\160\1\154\1\165\1\60\1\164\1\156\1\163\1\uffff\1\145"+
        "\1\162\1\154\2\uffff\1\156\1\153\1\163\1\162\1\151\1\60\1\uffff"+
        "\1\60\1\156\1\145\1\141\1\uffff\1\60\1\uffff\1\164\1\60\1\157\4"+
        "\uffff\1\141\2\145\1\151\1\162\1\uffff\1\151\3\60\2\145\4\60\1\156"+
        "\1\146\2\uffff\1\144\1\60\1\143\1\uffff\1\141\1\uffff\1\144\1\164"+
        "\1\162\2\143\1\156\1\143\3\uffff\2\60\4\uffff\1\165\1\60\1\163\1"+
        "\uffff\1\150\1\142\1\60\1\145\2\164\3\60\2\uffff\1\145\1\uffff\2"+
        "\60\1\154\1\uffff\1\60\1\171\1\145\3\uffff\1\60\2\uffff\1\145\1"+
        "\uffff\1\60\1\144\1\uffff\1\60\1\uffff\1\60\2\uffff";
    static final String DFA12_maxS =
        "\1\176\1\75\1\76\2\75\1\56\1\75\1\172\6\uffff\2\uffff\2\uffff\2"+
        "\75\1\uffff\1\75\1\uffff\1\136\1\172\2\76\1\75\1\uffff\1\75\1\156"+
        "\1\171\2\157\1\170\1\162\1\145\1\156\1\141\1\145\1\157\1\162\1\165"+
        "\1\145\1\164\1\162\1\150\11\uffff\1\75\6\uffff\1\56\14\uffff\1\75"+
        "\4\uffff\1\172\1\162\1\uffff\1\75\1\uffff\1\76\6\uffff\1\144\1\147"+
        "\1\145\1\172\1\145\1\156\1\172\1\163\1\165\1\164\1\154\1\162\1\157"+
        "\1\164\1\172\1\155\1\172\1\166\1\164\1\147\1\154\1\164\2\172\1\163"+
        "\1\157\1\142\2\164\1\141\1\151\1\172\1\165\1\151\7\uffff\1\172\4"+
        "\uffff\1\172\1\151\1\141\1\uffff\1\163\1\141\1\164\1\uffff\1\145"+
        "\1\172\1\155\1\145\1\163\1\172\1\155\1\172\1\uffff\1\165\1\uffff"+
        "\1\141\1\150\3\172\2\uffff\1\172\1\166\1\164\1\154\1\165\1\172\1"+
        "\164\1\156\1\163\1\uffff\1\145\1\162\1\154\2\uffff\1\156\1\153\1"+
        "\163\1\162\1\151\1\172\1\uffff\1\172\1\156\1\145\1\141\1\uffff\1"+
        "\172\1\uffff\1\164\1\172\1\157\4\uffff\1\141\2\145\1\151\1\162\1"+
        "\uffff\1\151\3\172\2\145\4\172\1\156\1\146\2\uffff\1\144\1\172\1"+
        "\143\1\uffff\1\141\1\uffff\1\144\1\164\1\162\2\143\1\156\1\143\3"+
        "\uffff\2\172\4\uffff\1\165\1\172\1\163\1\uffff\1\150\1\142\1\172"+
        "\1\145\2\164\3\172\2\uffff\1\145\1\uffff\2\172\1\154\1\uffff\1\172"+
        "\1\171\1\145\3\uffff\1\172\2\uffff\1\145\1\uffff\1\172\1\144\1\uffff"+
        "\1\172\1\uffff\1\172\2\uffff";
    static final String DFA12_acceptS =
        "\10\uffff\1\12\1\13\1\14\1\15\1\16\1\17\2\uffff\1\22\1\23\2\uffff"+
        "\1\26\1\uffff\1\30\5\uffff\1\47\22\uffff\1\141\1\142\1\146\1\3\1"+
        "\53\1\1\1\45\1\54\1\2\1\uffff\1\55\1\4\1\56\1\147\1\150\1\5\1\uffff"+
        "\1\6\1\57\1\10\1\11\1\20\1\144\1\21\1\143\1\52\1\24\1\60\1\25\1"+
        "\uffff\1\27\1\32\1\62\1\31\2\uffff\1\33\1\uffff\1\46\1\uffff\1\43"+
        "\1\37\1\42\1\40\1\61\1\145\42\uffff\1\63\1\7\1\65\1\64\1\51\1\36"+
        "\1\34\1\uffff\1\50\1\35\1\44\1\41\3\uffff\1\71\3\uffff\1\75\10\uffff"+
        "\1\110\1\uffff\1\112\5\uffff\1\120\1\121\11\uffff\1\134\3\uffff"+
        "\1\136\1\66\6\uffff\1\100\4\uffff\1\104\1\uffff\1\107\3\uffff\1"+
        "\115\1\116\1\117\1\122\5\uffff\1\130\14\uffff\1\76\1\101\3\uffff"+
        "\1\106\1\uffff\1\113\7\uffff\1\132\1\133\1\135\2\uffff\1\67\1\70"+
        "\1\72\1\73\3\uffff\1\103\11\uffff\1\137\1\140\1\uffff\1\77\3\uffff"+
        "\1\114\3\uffff\1\126\1\127\1\131\1\uffff\1\102\1\105\1\uffff\1\123"+
        "\2\uffff\1\74\1\uffff\1\124\1\uffff\1\111\1\125";
    static final String DFA12_specialS =
        "\16\uffff\1\1\1\0\u010b\uffff}>";
    static final String[] DFA12_transitionS = {
            "\2\61\2\uffff\1\61\22\uffff\1\61\1\25\1\17\2\uffff\1\6\1\23"+
            "\1\16\1\10\1\11\1\3\1\1\1\20\1\2\1\5\1\4\12\57\1\22\1\21\1\32"+
            "\1\31\1\33\1\26\1\uffff\32\60\1\12\1\34\1\13\1\27\1\7\1\uffff"+
            "\1\36\1\37\1\40\1\41\1\42\1\43\1\44\1\60\1\45\1\46\2\60\1\47"+
            "\1\50\1\51\1\52\1\60\1\53\1\54\1\55\1\60\1\30\1\56\3\60\1\14"+
            "\1\35\1\15\1\24",
            "\1\62\17\uffff\1\63",
            "\1\66\1\65",
            "\1\70\22\uffff\1\71",
            "\1\75\4\uffff\1\74\15\uffff\1\73",
            "\1\77",
            "\1\101",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "",
            "",
            "",
            "",
            "",
            "",
            "\12\105\1\uffff\2\105\1\uffff\31\105\1\uffff\uffd8\105",
            "\12\107\1\uffff\2\107\1\uffff\ufff2\107",
            "",
            "",
            "\1\110",
            "\1\112",
            "",
            "\1\114",
            "",
            "\1\117\40\uffff\1\116",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\1\122\24\60\1\121"+
            "\4\60",
            "\1\124\1\125",
            "\1\126\1\127",
            "\1\131",
            "",
            "\1\133",
            "\1\135",
            "\1\136\14\uffff\1\137\6\uffff\1\140",
            "\1\141\2\uffff\1\142",
            "\1\143",
            "\1\144\1\uffff\1\145\11\uffff\1\146",
            "\1\147\15\uffff\1\150\2\uffff\1\151",
            "\1\152",
            "\1\153\6\uffff\1\154\1\155",
            "\1\156",
            "\1\157",
            "\1\160\3\uffff\1\161\5\uffff\1\162",
            "\1\163\1\uffff\1\164",
            "\1\165\2\uffff\1\166\2\uffff\1\167",
            "\1\170",
            "\1\171\16\uffff\1\172",
            "\1\173\6\uffff\1\174\2\uffff\1\175",
            "\1\176",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\177",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u0081",
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
            "\1\u0083",
            "",
            "",
            "",
            "",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u0086",
            "",
            "\1\u0087",
            "",
            "\1\u0089",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u008b",
            "\1\u008c",
            "\1\u008d",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u008f\3\uffff\1\u0090",
            "\1\u0091",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u0093",
            "\1\u0094\20\uffff\1\u0095",
            "\1\u0096",
            "\1\u0097",
            "\1\u0098",
            "\1\u0099",
            "\1\u009a",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u009c",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u009e",
            "\1\u009f",
            "\1\u00a0",
            "\1\u00a1",
            "\1\u00a2",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u00a5",
            "\1\u00a6\5\uffff\1\u00a7",
            "\1\u00a8",
            "\1\u00a9",
            "\1\u00aa",
            "\1\u00ab",
            "\1\u00ac\3\uffff\1\u00ad",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u00af",
            "\1\u00b0\3\uffff\1\u00b1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "",
            "",
            "",
            "",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u00b4",
            "\1\u00b5",
            "",
            "\1\u00b6",
            "\1\u00b7",
            "\1\u00b8",
            "",
            "\1\u00b9",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u00bb",
            "\1\u00bc",
            "\1\u00bd",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\4\60\1\u00be\25"+
            "\60",
            "\1\u00c0",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "",
            "\1\u00c2",
            "",
            "\1\u00c3",
            "\1\u00c4",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "",
            "",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u00c9",
            "\1\u00ca\3\uffff\1\u00cb",
            "\1\u00cc",
            "\1\u00cd",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u00cf",
            "\1\u00d0",
            "\1\u00d1",
            "",
            "\1\u00d2",
            "\1\u00d3",
            "\1\u00d4",
            "",
            "",
            "\1\u00d5",
            "\1\u00d6",
            "\1\u00d7",
            "\1\u00d8",
            "\1\u00d9",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\10\60\1\u00da\21"+
            "\60",
            "",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u00dd",
            "\1\u00de",
            "\1\u00df",
            "",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "",
            "\1\u00e1",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u00e3",
            "",
            "",
            "",
            "",
            "\1\u00e4",
            "\1\u00e5",
            "\1\u00e6",
            "\1\u00e7",
            "\1\u00e8",
            "",
            "\1\u00e9",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u00ed",
            "\1\u00ee",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u00f3",
            "\1\u00f4",
            "",
            "",
            "\1\u00f5",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u00f7",
            "",
            "\1\u00f8",
            "",
            "\1\u00f9",
            "\1\u00fa",
            "\1\u00fb",
            "\1\u00fc",
            "\1\u00fd",
            "\1\u00fe",
            "\1\u00ff",
            "",
            "",
            "",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "",
            "",
            "",
            "",
            "\1\u0102",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u0104",
            "",
            "\1\u0105",
            "\1\u0106",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u0108",
            "\1\u0109",
            "\1\u010a",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "",
            "",
            "\1\u010e",
            "",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u0111",
            "",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u0113",
            "\1\u0114",
            "",
            "",
            "",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "",
            "",
            "\1\u0116",
            "",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u0118",
            "",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "",
            ""
    };

    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;

    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( PLUS | MINUS | PLUSMINUS | AST | FSLASH | DOT | EXP | PERCENT | UNDERSCORE | LPAREN | RPAREN | LBRACKET | RBRACKET | LBRACE | RBRACE | SQUOTE | DQUOTE | COMMA | SEMI | COLON | AMPERSAND | TILDE | BANG | QUESTION | CARET | CARETCARET | VEE | VEEVEE | EQUALS | NEQ | LT | GT | LEQ | GEQ | CONFUSED | COMPARE | RARROW | BIGRARROW | BACKSLASH | REFEQUALS | REFNEQ | ASSIGN | ASN_PLUS | ASN_MINUS | ASN_TIMES | ASN_DIV | ASN_MOD | ASN_AND | ASN_OR | ASN_XOR | ASN_EXP | DOTDOT | DOTDOTDOT | AND | BEGIN | BREAK | BY | CLASS | CLEAR | CONTINUE | DO | ELSE | ELSEIF | END | ENUM | EXTENDS | FALSE | FOR | FOREACH | FROM | GET | IF | IMMUTABLE | IN | JAVA | METHOD | NEG | NIL | NOT | OP | OR | POS | PRIVATE | PROPERTY | PROTECTED | PUBLIC | RETURN | SET | STATIC | THEN | THIS | TO | TRUE | VAR | WHERE | WHILE | INTEGER | IDENTIFIER | STRING | CHAR | SLASHES | WHITESPACE | SL_COMMENT | ML_COMMENT );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA12_15 = input.LA(1);

                        s = -1;
                        if ( ((LA12_15>='\u0000' && LA12_15<='\t')||(LA12_15>='\u000B' && LA12_15<='\f')||(LA12_15>='\u000E' && LA12_15<='\uFFFF')) ) {s = 71;}

                        else s = 70;

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA12_14 = input.LA(1);

                        s = -1;
                        if ( ((LA12_14>='\u0000' && LA12_14<='\t')||(LA12_14>='\u000B' && LA12_14<='\f')||(LA12_14>='\u000E' && LA12_14<='&')||(LA12_14>='(' && LA12_14<='\uFFFF')) ) {s = 69;}

                        else s = 68;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 12, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}