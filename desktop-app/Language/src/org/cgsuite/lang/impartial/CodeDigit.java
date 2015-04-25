/*
 * CodeDigit.java
 *
 * Created on February 15, 2006, 9:56 AM
 * $Id: CodeDigit.java,v 1.4 2007/04/09 23:51:51 asiegel Exp $
 */

/* ****************************************************************************

    Combinatorial Game Suite - A program to analyze combinatorial games
    Copyright (C) 2003-06  Aaron Siegel (asiegel@users.sourceforge.net)
    http://cgsuite.sourceforge.net/

    Combinatorial Game Suite is free software; you can redistribute it
    and/or modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2 of the
    License, or (at your option) any later version.

    Combinatorial Game Suite is distributed in the hope that it will be
    useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Combinatorial Game Suite; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA

**************************************************************************** */

package org.cgsuite.lang.impartial;

import java.util.Arrays;

/**
 * A generalized digit.  Normally, characters <code>'0'</code> to
 * <code>'9'</code> represent the numbers from 0-9, but some
 * applications require extensions, while retaining the usual behavior
 * of using a string of digits to represent a sequence of numbers.
 *
 * The most common extension is representing a larger range of digit
 * values.  It is common to use letters <code>'A'</code> to
 * <code>'F'</code> or even <code>'A'</code> to <code>'Z'</code> to
 * represent the digits 10-15 or 10-35, and for this purpose we would
 * usually prefer to remain case-insensitive.  The BASE64 mime type
 * uses characters <code>'0'</code> to <code>'9'</code>,
 * <code>'A'</code> to <CODE>'Z'</CODE>, <code>'a'</code> to
 * <code>'z'</code>, <code>'+'</code>, and <code>'/'</code> to
 * represent digit 0-63; these are of course case-sensitive.
 *
 * Letters may be used for other purposes in a sequence, and even this
 * range of values may not be sufficient, so we introduce <em>extended
 * digits</em> of the form <code>&amp;<em>&lt;digits&gt;</em>;</code>
 * to represent a digits with a value given by the decimal number
 * <code><em>&lt;digits&gt;</em></code>.  We generally allow the
 * number to be represented in hexadecimal as
 * <code>&amp;x<em>&lt;hexdigits&gt;</em>;</code> and other bases or
 * representations may be supported if needed.
 *
 * Another application that arose in
 * {@link org.cgsuite.impartial.TBCode} is to represent an
 * <em>annotated digit</em>, which may have extra characters or
 * generalized digits appended to the digit as modifiers.  For this
 * purpose, the <code>CodeDigit</code> class supports a string of
 * <em>modifier characters</em> that may be used as suffixes or to
 * separate a sequence of generalized digits in a parenthesized
 * string.
 *
 * A flexible method of specifying the options of this class is
 * supplied with an {@link Options} object.
 *
 * @author  Dan Hoey
 * @version $Revision: 1.4 $ $Date: 2007/04/09 23:51:51 $
 * @since 1.0
 */
public final class CodeDigit
{
    /**
     * Bundle of options for parsing and printing
     * <code>CodeDigits</code> objects.  This specifies what
     * digits can be coded as single digits and what modifiers can be
     * used to represent sequences of digits.
     */
    public static class Options
    {
        private int     radix;
        private boolean caseFold;
        private char[]  modifierCharacters;
        private char[]  parentheses;
        private int nullVal;
        private int missingVal;
        private int escapeRadix;
        /**
         * Constructs a group of options.
         * @param radix     Number of digit values to be represented
         *                  as single characters.
         * @param caseFold  <code>true</code> if
         *                  upper-case and lower-case alphabetic digits
         *                  are to be treated as the same,
         *                  <code>false</code> if case is significant
         *                  in the digit value.
         * @param modifierCharacters String of characters to be used
         *                  as digit modifiers.
         * @param parentheses String of two characters to be used for
         *                  grouping, or <code>null</code> if no
         *                  grouping is used.
         * @param nullVal   int that is used when the digit is
         *                  missing from a modifier.
         * @param missingVal int that is used when the corresponding
         *                  modifier is not present.
         * @param escapeRadix radix to use when printing ampersand
         *                  escapes.  Supported values are 10 and 16.
         * @throws IllegalArgumentException if <code>radix</code> is
         *                  greater than 64 (or 36, if
         *                  <code>caseFold</code> is true) or if
         *                  <code>parentheses</code> is not null,
         *                  empty, or of length 2, or if
         *                  <code>escapeRadix</code> is not 10 or 16.
         */
        public Options(int radix,
                       boolean caseFold,
                       String modifierCharacters,
                       String parentheses,
                       int nullVal,
                       int missingVal,
                       int escapeRadix)
        {
            if (radix > 64)
            {
                throw new IllegalArgumentException
                    ("Can't represent "+(radix-1)+
                     " as a single character");
            }
            this.radix = radix;
            if (caseFold && radix > 36) 
            {
                throw new IllegalArgumentException
                    ("Can't represent "+(radix-1)+
                     " as a single character ignoring case");
            }
            this.caseFold = caseFold;
            if (modifierCharacters == null)
            {
                this.modifierCharacters = new char[0];
            }
            else
            {
                this.modifierCharacters = modifierCharacters.toCharArray();
            }
            if (parentheses==null || parentheses.length() == 0)
            {
                this.parentheses=null;
            }
            else if (parentheses.length() != 2)
            {
                throw new IllegalArgumentException
                    ("parentheses string "+parentheses+
                     " must be empty or length 2");
            }
            else
            {
                this.parentheses=parentheses.toCharArray();
            }
            this.nullVal = nullVal;
            this.missingVal = missingVal;
            if (escapeRadix != 10 && escapeRadix != 16) 
            {
                throw new IllegalArgumentException
                    ("escape Radix "+escapeRadix+" not supported");
            }
            this.escapeRadix = escapeRadix;
        }
    }
    ////////////////////////////////////////////////////////////////
    // Instance variables
    /**
     * Array holding an integer or integers representing a generalized
     * digit.  The <code>digitVal[0]</code> entry represents the basic
     * component, and the <code>digitVal[<em>k</em>+1]<code>
     * represents the component introduced by the
     * <code>modifierCharacters[<em>k</em>]</code> modifier.
     */
    public int digitVal[];
    /**
     * Position in a string beyond the current generalized digit, set
     * by the String constructor for <code>CodeDigit</code>.
     */
    public int breakPosition;
    /**
     * <code>CodeDigits.Options</code> structure for controlling
     * String conversions to and from this generalized digit.
     */
    Options options;
    
    ////////////////////////////////////////////////////////////////
    // Public constants
    /**
     * Options for reading in a generalized decimal digit.  Ampersand
     * escapes for digits greater than 9 are printed in hexadecimal.
     */
    public static Options defaultOptions
        = new Options(10,true,"",null,-1,0,16);
    /**
     * Options for reading in a generalized decimal digit.  Ampersand
     * escapes for digits greater than 9 are printed in decimal.
     */
    public static Options decimalOptions
        = new Options(10,true,"",null,-1,0,10);
    /**
     * Options for reading in a generalized hexdecimal digit.
     */
    public static Options hexOptions
        = new Options(16,true,"",null,-1,0,16);
    /**
     * Options for reading in a generalized using digits and
     * case-folded letters, for any base up to 36.
     */
    public static Options base36Options
        = new Options(36,true,"",null,-1,0,16);
    /**
     * Options for reading in a generalized base64 digit, using
     * digits, upper-case, lower-case, and the special characters
     * <code>'+'</code> and <code>'/'</code> as in the MIME base64
     * encoding.
     */
    public static Options base64Options
        = new Options(64,false,"",null,-1,0,16);
    ////////////////////////////////////////////////////////////////
    // Private constant
    static final String digitsTo64 =
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz+/";

    ////////////////////////////////////////////////////////////////
    //
    /**
     * Create a single-component code digit with default options.
     * @param val the value of the digit.
     */
    public CodeDigit(int val)
    {
        this(val,defaultOptions);
    }

    /**
     * Create a single-component code digit with specified options.
     * @param val the value of the digit.
     * @param options the options used for string conversions of this
     * digit.
     */
    public CodeDigit(int val, Options options)
    {
        this.options=options;
        digitVal = initDigitVal(options);
        digitVal[0] = val;
    }
    /**
     * Create a multiple-component digit with specified options.  The
     * options must specify at least <code>val.length - 1</code>
     * modifier characters.
     *
     * @param val the component values of the digit.
     * @param options the options used for string conversions of this
     *        digit.
     * @throws IllegalArgumentException if too many values are given.
     */
    public CodeDigit(int[] val, Options options)
    {
        this.options=options;
        digitVal = initDigitVal(options);
        if (val.length > digitVal.length)
        {
            throw new IllegalArgumentException
                (Arrays.toString(val) +
                 " too long for options with " +
                 options.modifierCharacters.length +
                 " modifier characters");
        }
        System.arraycopy(val,0, digitVal,0, val.length);
    }

    /**
     * Create a <code>digitVal</code> vector compatible with the given
     * <code>options<code> and initialized to indicate missing values.
     */
    private static int[] initDigitVal(Options options) 
    {
        int[] dV = new int[1 + options.modifierCharacters.length];
        Arrays.fill(dV,options.missingVal);
        return dV;
    }

    /**
     * Parses a CodeDigit from the given <code>position</code> in the
     * <code>string</code>.  Stores the first position beyond the
     * CodeDigit in the <code>breakPosition</code> instance variable.
     *
     * @param   string    String containing the generalized digit
     * @param   position  Position in string of beginning of
     *                    generalized digit.
     * @param   options   Options structure indicating how to parse
     * @throws  NumberFormatException if the <code>string</code> does
     *          not have a valid <code>CodeDigit</code> at
     *          <code>position</code>.
     */
    public CodeDigit(String string,
                     int position,
                     Options options) 
    {
        this.options = options;
        this.digitVal = initDigitVal(options);
        if(position >= string.length())
        {
            throw new NumberFormatException
                ("no generalized digit at end");
        }

        breakPosition = position;
        if (options.parentheses != null
            && string.charAt(breakPosition)==options.parentheses[0])
        {
            // Parse parenthesized digit
            
            breakPosition++;

            for (int digitNum=0;
                 digitNum <= options.modifierCharacters.length;
                 ++digitNum)
            {
                if (breakPosition >= string.length()) {
                    throw new NumberFormatException
                        ("unterminated parenthesized digit");
                }
                if (digitNum != 0)
                {
                    if (string.charAt(breakPosition) !=
                        options.modifierCharacters[digitNum-1])
                    {
                        continue;
                    }
                    breakPosition++;
                }
                parseOneDigit(string,digitNum);
            }
            if (breakPosition >= string.length() ||
                string.charAt(breakPosition)!=options.parentheses[1])
            {
                throw new NumberFormatException
                    ("unterminated parenthesized digit");
            }
            breakPosition++;
        }
        else
        {
            parseOneDigit(string,0);
            for (int digitNum=1;
                 digitNum <= options.modifierCharacters.length
                     && breakPosition < string.length(); 
                 ++digitNum)
            {
                if (string.charAt(breakPosition) ==
                    options.modifierCharacters[digitNum-1])
                {
                    digitVal[digitNum] = options.nullVal;
                    breakPosition++;
                }
            }
        }
        if (breakPosition == position)
        {
            throw new NumberFormatException
                ("No number found ("+string.substring(position)+")");
        }
    }

    /**
     * Parses <code>string</code> starting at
     * <code>breakPosition</code> as the <code>digitNum</code>th
     * component of this generalized digit.
     *
     * @param   string    String containing the digit
     * @param   digitNum  Index of the component.
     * 
     * @throws  NumberFormatException if the <code>string</code> does
     *          not have a valid digit starting at
     *          <code>breakPosition</code>.
     */
    private void parseOneDigit(String string, int digitNum)
    {
        if (breakPosition >= string.length()) 
        {
            digitVal[digitNum] = options.nullVal;
            return;
        }
        char ch=string.charAt(breakPosition);
        digitVal[digitNum] = 0;

        if (ch == '&')
        {
            if (++breakPosition >= string.length() - 2)
            {
                throw new NumberFormatException("& at end");
            }
            char ach=string.charAt(breakPosition);
            Options digitOptions=decimalOptions;
            if (ach=='x' || ach=='X') 
            {
                digitOptions=hexOptions;
                ach=string.charAt(++breakPosition);
            }
            while (ach != ';') 
            {
                int dig = parseDigitChar(ach, digitOptions);
                digitVal[digitNum] =
                    digitVal[digitNum] * digitOptions.radix + dig;
                if (++breakPosition >= string.length()) 
                {
                    throw new NumberFormatException("unterminated &...");
                }
                ach=string.charAt(breakPosition);
            }
            ++breakPosition;
        }
        else try
        {
            digitVal[digitNum]=parseDigitChar(ch, options);
            breakPosition++;
        }
            catch (NumberFormatException e)
            {
                digitVal[digitNum]=options.nullVal;
            }
    }

    /**
     * Parses the character <code>ch</code> as a single-character
     * digit according to <code>options</code>.
     *
     * @param   ch        character to parse
     * @param   options   options determining what digits are
     *                    acceptable, and whether case is
     *                    significant.
     * @return  value of character as a digit.
     * @throws  NumberFormatException if character is not valid.
     */
    private static int parseDigitChar(char ch, Options options) 
    {
        int val = Character.digit(ch, options.radix);

        if (! options.caseFold)
        {
            val = digitsTo64.indexOf(ch);

            if (val >= options.radix) 
            {
                val = -1;
            }
        }
        
        if (val == -1) 
        {
            throw new NumberFormatException
                ("Illegal digit character: " + ch);
        }
        return val;
    }

    /**
     * Creates a parseable version of this <code>CodeDigit</code>.
     */
    public String toString()
    {
        return appendToStringBuilder(new StringBuilder()).toString();
    }
    
    /**
     * Append a textual representation of this <code>CodeDigit</code>
     * constraints to <code>sb</code>.  If all but the first
     * <code>digitVal</code> are <code>nullVal</code> or
     * <code>missingVal</code>, this can be done without parentheses.
     * Othewise, parenthesization must be used.
     *
     * @param sb   {@link StringBuilder} to receive the textual
     *              representation.
     * @return <code>sb</code> with the text appended.
     * @throws IllegalArgumentException if parentheses are required by
     *         <code>digitVal</code> but not permitted by the
     *         <code>options</code>.
     */
    public StringBuilder appendToStringBuilder(StringBuilder sb)
    {
        boolean parenthesize = false;

        for(int i=1; i<=options.modifierCharacters.length; ++i) 
        {
            if(digitVal[i] != options.nullVal
               && digitVal[i] != options.missingVal)
            {
                parenthesize = true;
                break;
            }
        }
        if (parenthesize)
        {
            if (options.parentheses == null) 
            {
                throw new IllegalArgumentException
                    ("Can't express codeDigit "
                     + Arrays.toString(digitVal)
                     + " without parentheses");
            }
            sb.append(options.parentheses[0]);
        }
        for(int j = 0;
            j <= options.modifierCharacters.length;
            ++j) 
        {
            appendOneDigit(j, sb, j==0 && !parenthesize);
        }
        if (parenthesize)
        {
            sb.append(options.parentheses[1]);
        }
        return sb;
    }

    /**
     * Append a textual representation of
     * <code>digitVal[digitNum]</code> to <code>sb</code>.  If
     * <code>digitVal[digitNum]</code> is <code>missingVal</code>,
     * nothing is appended unless <code>forceZero</code> is
     * specified.  Otherwise, if <code>digitNum</code> is nonzero, the
     * output is preceded by
     * <code>options.modifierCharacters[digitNum-1]</code>.
     *
     * @param   digitNum  The index of the digit to be represented.
     * @param   sb <code>StringBuilder</code> to receive the textual
     *              representation.
     * @param   forceZero true if something must be appended, even for
     *          missing values.
     * @return <code>sb</code> with the text appended.
     * @throws  IndexOutOfBoundsException if digitNum is less than 0
     *          or greater than
     *          <code>options.modifierCharacters.length</code>.
     */
    public StringBuilder appendOneDigit(int digitNum,
                                        StringBuilder sb,
                                        boolean forceZero)
    {
        if (digitVal[digitNum]==options.missingVal && !forceZero)
        {
            return sb;
        }
        if (digitNum>0) 
        {
            sb.append(options.modifierCharacters[digitNum-1]);
        }
        if (digitNum>0 && digitVal[digitNum]==options.nullVal) 
        {
            return sb;
        }
        if (digitVal[digitNum] < options.radix)
        {
            sb.append(digitsTo64.charAt(digitVal[digitNum]));
        }
        else
        {
            sb.append("&");
            if (options.escapeRadix == 16) 
            {
                sb.append('x');
                sb.append(Integer.toHexString(digitVal[digitNum]));
            }
            else
            {
                sb.append(Integer.toString(digitVal[digitNum]));
            }
            sb.append(';');
        }
        return sb;
    }
}
