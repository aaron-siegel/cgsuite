/*
 * StyledTextOutput.java
 *
 * Created on December 12, 2002, 8:06 PM
 * $Id: StyledTextOutput.java,v 1.23 2007/08/13 20:43:45 asiegel Exp $
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

package org.cgsuite.lang.output;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GraphicAttribute;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.io.PrintWriter;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * An abstract representation of styled character data.  Each
 * <code>StyledTextOutput</code> contains a body of text marked up with
 * abstract attributes.  When the output is written, the attributes are
 * interpreted variably depending on the output {@link Output.Mode Mode}.
 * For example, the subscript attribute is ignored when generating
 * plain text.
 * <p>
 * Text can be marked up with style attributes, such as location and size
 * modifiers, as well as an attribute restricting the output modes under which
 * that text is displayed.  This makes it possible to specify alternate
 * representations for different output modes.
 * <p>
 * It is possible to embed other <code>Output</code> objects, including images,
 * tables, and existing styled text, within a <code>StyledTextOutput</code>.
 * See the <code>append*</code> methods for details.
 * 
 * @author  Aaron Siegel
 * @version $Revision: 1.23 $ $Date: 2007/08/13 20:43:45 $
 */
public class StyledTextOutput extends AbstractOutput
{
    private static Map<FontKey,Font> fontCache;
    private static Map<FontKey,AffineTransform> transformCache;
    private static Font SANS_SERIF_FONT, MONOSPACED_FONT, SYMBOL_FONT;
    
    static
    {
        fontCache = new HashMap<FontKey,Font>();
        transformCache = new HashMap<FontKey,AffineTransform>();
        try {
            SANS_SERIF_FONT = new Font("Arial", Font.PLAIN, 20);
        } catch (Exception exc) {
            SANS_SERIF_FONT = new Font("SansSerif", Font.PLAIN, 20);
        }

        MONOSPACED_FONT = new Font("Monospaced", Font.PLAIN, 12);

        /*
        for (String fontName : java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            System.out.println(fontName);
        }
        */
        try
        {
            SYMBOL_FONT = Font.createFont(
                Font.TRUETYPE_FONT,
                StyledTextOutput.class.getResourceAsStream("resources/ebtsy10.ttf")
                ).deriveFont(20.0f);
        }
        catch (Exception exc)
        {
            throw new RuntimeException("Could not load symbol fonts.", exc);
        }
    }
    
    /**
     * An individual style attribute.  Style attributes indicate the font face,
     * size, location (subscript/superscript/etc), and color of styled text.
     *
     * @author  Aaron Siegel
     * @version $Revision: 1.23 $ $Date: 2007/08/13 20:43:45 $
     * @since   0.7
     */
    public enum Style
    {
        /**
         * A "plain text" font face.  Text with this attribute will be displayed
         * in a Sans Serif font on the screen, and as plain text in LaTeX.
         */
        FACE_TEXT,
        /**
         * A "math mode" font face.  Text with this attribute will be displayed
         * in a large Sans Serif font on the screen, and in math mode in LaTeX.
         */
        FACE_MATH,
        /**
         * A monospaced font face.  Text with this attribute will be displayed
         * in a monospaced (Courier) font.
         */
        FACE_MONOSPACED,
        
        /** The numerator of a fraction. */
        LOCATION_NUMERATOR,
        /** The denominator of a fraction. */
        LOCATION_DENOMINATOR,
        /** Standard subscript. */
        LOCATION_SUBSCRIPT,
        /** Standard superscript. */
        LOCATION_SUPERSCRIPT,
        /** The upper limit of an integral symbol. */
        LOCATION_UPPER_LIMIT,
        /** The lower limit of an integral symbol. */
        LOCATION_LOWER_LIMIT,
        
        /** A large font size. */
        SIZE_LARGE,
        /** A huge font size. */
        SIZE_HUGE,
        
        /** A style attribute that corresponds to {@link Color#black}. **/
        COLOR_BLACK      (Color.black),
        /** A style attribute that corresponds to {@link Color#blue}. **/
        COLOR_BLUE       (Color.blue),
        /** A style attribute that corresponds to {@link Color#cyan}. **/
        COLOR_CYAN       (Color.cyan),
        /** A style attribute that corresponds to {@link Color#darkGray}. **/
        COLOR_DARK_GRAY  (Color.darkGray),
        /** A style attribute that corresponds to {@link Color#gray}. **/
        COLOR_GRAY       (Color.gray),
        /** A style attribute that corresponds to {@link Color#green}. **/
        COLOR_GREEN      (Color.green),
        /** A style attribute that corresponds to {@link Color#lightGray}. **/
        COLOR_LIGHT_GRAY (Color.lightGray),
        /** A style attribute that corresponds to {@link Color#magenta}. **/
        COLOR_MAGENTA    (Color.magenta),
        /** A style attribute that corresponds to {@link Color#orange}. **/
        COLOR_ORANGE     (Color.orange),
        /** A style attribute that corresponds to {@link Color#pink}. **/
        COLOR_PINK       (Color.pink),
        /** A style attribute that corresponds to {@link Color#red}. **/
        COLOR_RED        (Color.red),
        /** A style attribute that corresponds to {@link Color#white}. **/
        COLOR_WHITE      (Color.white),
        /** A style attribute that corresponds to {@link Color#yellow}. **/
        COLOR_YELLOW     (Color.yellow);
        
        /** The set of all <code>FACE</code> attributes. */
        public final static EnumSet<Style> FACES = EnumSet.range(FACE_TEXT, FACE_MONOSPACED);
        /** The set of all <code>LOCATION</code> attributes. */
        public final static EnumSet<Style> LOCATIONS = EnumSet.range(LOCATION_NUMERATOR, LOCATION_LOWER_LIMIT);
        /** The set of all <code>LOCATION</code> attributes, except
            {@link #LOCATION_NUMERATOR} and {@link #LOCATION_DENOMINATOR}. */
        public final static EnumSet<Style> TRUE_LOCATIONS = EnumSet.range(LOCATION_SUBSCRIPT, LOCATION_LOWER_LIMIT);
        /** The set of all <code>SIZE</code> attributes. */
        public final static EnumSet<Style> SIZES = EnumSet.range(SIZE_LARGE, SIZE_HUGE);
        /** The set of all <code>COLOR</code> attributes. */
        public final static EnumSet<Style> COLORS = EnumSet.range(COLOR_BLACK, COLOR_YELLOW);
        
        private Color color;
        
        Style()
        {
            this(null);
        }
        
        Style(Color color)
        {
            this.color = color;
        }
        
        Color color()
        {
            return color;
        }
    }
    
    /**
     * An abstract symbol.  Symbols can be embedded in blocks of styled text.
     * They will be translated into an appropriate display when the output is
     * written (depending on the output mode).
     *
     * @author  Aaron Siegel
     * @version $Revision: 1.23 $ $Date: 2007/08/13 20:43:45 $
     * @since   0.7
     */
    public enum Symbol
    {
        /** A single up arrow. */
        UP              (SYMBOL_FONT, '\42', "^", "\\Up"),
        /** A double up arrow. */
        DOUBLE_UP       (SYMBOL_FONT, '\52', "^^", "\\Upup"),
        /** A triple up arrow (not yet supported). */
        TRIPLE_UP       (SANS_SERIF_FONT, '!', "^^^", "\\Uuparrow"),
        /** A quadruple up arrow (not yet supported). */
        QUADRUPLE_UP    (SANS_SERIF_FONT, '!', "^^^^", "\\UUparrow"),
        /** A single down arrow. */
        DOWN            (SYMBOL_FONT, '\43', "v", "\\Down"),
        /** A double down arrow. */
        DOUBLE_DOWN     (SYMBOL_FONT, '\53', "vv", "\\Downdown"),
        /** A triple down arrow (not yet supported). */
        TRIPLE_DOWN     (SANS_SERIF_FONT, '!', "vvv", "\\Ddownarrow"),
        /** A quadruple down arrow (not yet supported). */
        QUADRUPLE_DOWN  (SANS_SERIF_FONT, '!', "vvvv", "\\DDownarrow"),
        /** An asterisk, suitable for nimbers. */
        STAR            (SYMBOL_FONT, '\244', "*", "\\Star"),
        /** A tiny symbol. */
        TINY            (SANS_SERIF_FONT, '+', "+", "\\Tinychar"),
        /** A miny symbol. */
        MINY            (SANS_SERIF_FONT, '-', "-", "\\Minychar"),
        /** A plus-or-minus sign. */
        PLUS_MINUS      (SANS_SERIF_FONT, '\261', "+-", "\\pm"),
        /** A right arrow. */
        RIGHT_ARROW     (SYMBOL_FONT, '\41', "->", "\\to"),
        /** A big right arrow. */
        BIG_RIGHT_ARROW (SYMBOL_FONT, '\51', "=>", "\\bigrarrow"),
        /** An integral sign. */
        INTEGRAL        (SYMBOL_FONT, 's', "INTEGRAL", "\\int"),
        /** An infinity symbol. */
        INFINITY        (SYMBOL_FONT, '1', "Infinity", "\\infty"),
        /** A left angle bracket. */
        LANGLE          (SYMBOL_FONT, 'h', "<", "\\langle"),
        /** A right angle bracket. */
        RANGLE          (SYMBOL_FONT, 'i', ">", "\\rangle"),
        /** A dash. */
        DASH            (SYMBOL_FONT, '\241', "-", "---"),
        /** A calligraphic uppercase A. */
        CALA            (SYMBOL_FONT, 'A', "A", "\\mathcal{A}"),
        /** A calligraphic uppercase B. */
        CALB            (SYMBOL_FONT, 'B', "B", "\\mathcal{B}"),
        /** A calligraphic uppercase C. */
        CALC            (SYMBOL_FONT, 'C', "C", "\\mathcal{C}"),
        /** A calligraphic uppercase D. */
        CALD            (SYMBOL_FONT, 'D', "D", "\\mathcal{D}"),
        /** A calligraphic uppercase E. */
        CALE            (SYMBOL_FONT, 'E', "E", "\\mathcal{E}"),
        /** A calligraphic uppercase F. */
        CALF            (SYMBOL_FONT, 'F', "F", "\\mathcal{F}"),
        /** A calligraphic uppercase G. */
        CALG            (SYMBOL_FONT, 'G', "G", "\\mathcal{G}"),
        /** A calligraphic uppercase H. */
        CALH            (SYMBOL_FONT, 'H', "H", "\\mathcal{H}"),
        /** A calligraphic uppercase I. */
        CALI            (SYMBOL_FONT, 'I', "I", "\\mathcal{I}"),
        /** A calligraphic uppercase J. */
        CALJ            (SYMBOL_FONT, 'J', "J", "\\mathcal{J}"),
        /** A calligraphic uppercase K. */
        CALK            (SYMBOL_FONT, 'K', "K", "\\mathcal{K}"),
        /** A calligraphic uppercase L. */
        CALL            (SYMBOL_FONT, 'L', "L", "\\mathcal{L}"),
        /** A calligraphic uppercase M. */
        CALM            (SYMBOL_FONT, 'M', "M", "\\mathcal{M}"),
        /** A calligraphic uppercase N. */
        CALN            (SYMBOL_FONT, 'N', "N", "\\mathcal{N}"),
        /** A calligraphic uppercase O. */
        CALO            (SYMBOL_FONT, 'O', "O", "\\mathcal{O}"),
        /** A calligraphic uppercase P. */
        CALP            (SYMBOL_FONT, 'P', "P", "\\mathcal{P}"),
        /** A calligraphic uppercase Q. */
        CALQ            (SYMBOL_FONT, 'Q', "Q", "\\mathcal{Q}"),
        /** A calligraphic uppercase R. */
        CALR            (SYMBOL_FONT, 'R', "R", "\\mathcal{R}"),
        /** A calligraphic uppercase S. */
        CALS            (SYMBOL_FONT, 'S', "S", "\\mathcal{S}"),
        /** A calligraphic uppercase T. */
        CALT            (SYMBOL_FONT, 'T', "T", "\\mathcal{T}"),
        /** A calligraphic uppercase U. */
        CALU            (SYMBOL_FONT, 'U', "U", "\\mathcal{U}"),
        /** A calligraphic uppercase V. */
        CALV            (SYMBOL_FONT, 'V', "V", "\\mathcal{V}"),
        /** A calligraphic uppercase W. */
        CALW            (SYMBOL_FONT, 'W', "W", "\\mathcal{W}"),
        /** A calligraphic uppercase X. */
        CALX            (SYMBOL_FONT, 'X', "X", "\\mathcal{X}"),
        /** A calligraphic uppercase Y. */
        CALY            (SYMBOL_FONT, 'Y', "Y", "\\mathcal{Y}"),
        /** A calligraphic uppercase Z. */
        CALZ            (SYMBOL_FONT, 'Z', "Z", "\\mathcal{Z}"),
        ;
        
        private Font font;
        private char character;
        private String textString;
        private String latexString;
        
        private Symbol(Font font, char character, String textString, String latexString)
        {
            this.font = font;
            this.character = character;
            this.textString = textString;
            this.latexString = latexString;
        }
        
        /**
         * Gets a font that can display this symbol.
         *
         * @return  A font that can display this symbol.
         */
        public Font getFont()
        {
            return font;
        }
        
        /**
         * Gets the character for this symbol.  This symbol can be displayed
         * as the corresponding character in the font returned by
         * {@link #getFont() getFont}.
         *
         * @return  The character for this symbol.
         */
        public char getCharacter()
        {
            return character;
        }
        
        /**
         * Gets a plain-text representation of this symbol.
         */
        public String getPlainTextString()
        {
            return textString;
        }
        
        /**
         * Gets a LaTeX fragment that produces this symbol in math mode.
         */
        public String getLatexString()
        {
            return latexString;
        }
        
        /**
         * Maps a standard <code>char</code> to a calligraphic letter.  For
         * example, <code>cal(T)</code> returns {@link #CALT}.
         * <code>ch</code> must be a standard uppercase ASCII letter
         * ('A'-'Z').
         * 
         * @param   ch An uppercase ASCII letter ('A'-'Z')
         * @return  The corresponding <code>CAL*</code> constant
         *          ({@link #CALA}-{@link #CALZ})
         * @throws  IllegalArgumentException if <code>ch</code> is not an
         *          uppercase letter.
         */
        public static Symbol cal(char ch)
        {
            if (ch >= 'A' && ch <= 'Z')
            {
                return Enum.valueOf(Symbol.class, "CAL" + ch);
            }
            else
            {
                throw new IllegalArgumentException("ch must be an ASCII capital letter ('A'-'Z').");
            }
        }
    }
    
    private final static int MAX_STO_LENGTH = Short.MAX_VALUE;
    
    private static FontRenderContext screenFrc;
    
    private static FontRenderContext getScreenFrc()
    {
        if (screenFrc == null)
        {
            screenFrc = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
                .createGraphics().getFontRenderContext();
        }
        return screenFrc;
    }
    
    private Block topBlock;
    private LineBreakMeasurer measurer;
    
    /**
     * Constructs a new <code>StyledTextOutput</code> that is initially empty.
     */
    public StyledTextOutput()
    {
        init(null);
    }
    
    private void init(Block initialBlock)
    {
        topBlock = new Block();
        if (initialBlock != null)
        {
            topBlock.subBlocks.add(initialBlock);
        }
    }
    
    /**
     * Constructs a new <code>StyledTextOutput</code> beginning with
     * <code>text</code>, formatted according to <code>styles</code>.
     *
     * @param   styles The styles of <code>text</code>.
     * @param   text The initial text of this output.
     */
    public StyledTextOutput(EnumSet<Style> styles, String text)
    {
        init(new Block(styles, text));
    }
    
    /**
     * Constructs a new <code>StyledTextOutput</code> beginning with
     * <code>text</code>.
     */
    public StyledTextOutput(String text)
    {
        init(new Block(text));
    }
    
    private void append(Block block)
    {
        topBlock.subBlocks.add(block);
        measurer = null;
    }
    
    /**
     * Appends <code>text</code> to the end of this output with the specified
     * style and display mode attributes.
     * <p>
     * The text will be formatted according to <code>styles</code>, and it will
     * only be displayed if the display mode matches one of the specified
     * modes.
     * 
     * @param   styles The styles of <code>text</code>.
     * @param   modes The display modes to use.
     * @param   text The text to append.
     */
    public void appendText(EnumSet<Style> styles, EnumSet<Mode> modes, String text)
    {
        append(new Block(styles, modes, text));
    }
    
    /**
     * Appends <code>text</code> to the end of this output with the specified
     * style attributes.
     * <p>
     * The text will be formatted according to <code>styles</code>, and it will
     * be displayed in any display mode.  This method is equivalent to:
     * <p>
     * <code>appendText(styles, EnumSet.allOf(Mode.class), text)</code>
     * 
     * @param   styles The styles of <code>text</code>.
     * @param   text The text to append.
     */
    public void appendText(EnumSet<Style> styles, String text)
    {
        appendText(styles, EnumSet.allOf(Mode.class), text);
    }
    
    /**
     * Appends <code>text</code> to the end of this output in the specified
     * display mode.  The text will only be displayed in that display mode.
     * <p>
     * This method is equivalent to:
     * <p>
     * <code>appendText(EnumSet.noneOf(Style.class), EnumSet.of(mode), text)</code>
     * 
     * @param   mode The mode in which to display <code>text</code>.
     * @param   text The text to append.
     */
    public void appendText(Mode mode, String text)
    {
        appendText(EnumSet.noneOf(Style.class), EnumSet.of(mode), text);
    }
    
    /**
     * Appends <code>text</code> to the end of this output.
     * <p>
     * The text will be formatted as plain text, and it will
     * be displayed in any display mode.  This method is equivalent to:
     * <p>
     * <code>appendText(EnumSet.noneOf(Style.class), EnumSet.allOf(Mode.class), text)</code>
     * 
     * @param   text The text to append.
     */
    public void appendText(String text)
    {
        appendText(EnumSet.noneOf(Style.class), EnumSet.allOf(Mode.class), text);
    }

    /**
     * Appends additional output to the end of this text.  The output will only
     * be displayed if the display mode matches one of the specified modes.
     * <p>
     * If <code>output</code> is another <code>StyledTextOutput</code>, then
     * its text and style attributes will simply be copied.  If
     * <code>output</code> is a different form of output (for example, an
     * {@link ImageOutput}, then it will be
     * embedded in this text at the indicated location.
     *
     * @param   styles Additional styles to apply to the output.
     * @param   modes The display modes in which to display the output.
     * @param   output The additional output to embed in this one.
     */
    public void appendOutput(EnumSet<Style> styles, EnumSet<Mode> modes, Output output)
    {
        if (output instanceof StyledTextOutput)
        {
            Block block = new Block(styles, modes);
            if (styles.isEmpty())
            {
                block.subBlocks.addAll(((StyledTextOutput) output).topBlock.subBlocks);
            }
            else
            {
                block.subBlocks.add(((StyledTextOutput) output).topBlock);
            }
            append(block);
        }
        else
        {
            append(new Block(output));
        }
    }
    
    /**
     * Appends additional output to the end of this text.  This method
     * is equivalent to:
     * <p>
     * <code>appendOutput(styles, EnumSet.allOf(Mode.class), output)</code>
     *
     * @param   styles Additional styles to apply to the output.
     * @param   output The additional output to embed in this one.
     */
    public void appendOutput(EnumSet<Style> styles, Output output)
    {
        appendOutput(styles, EnumSet.allOf(Mode.class), output);
    }
    
    /**
     * Appends additional output to the end of this text.  The output will only
     * be displayed in the specified mode.  This method is equivalent to:
     * <p>
     * <code>appendOutput(EnumSet.noneOf(Style.class), EnumSet.of(mode), output)</code>
     *
     * @param   mode The display mode in which to display the output.
     * @param   output The additional output to embed in this one.
     */
    public void appendOutput(Mode mode, Output output)
    {
        appendOutput(EnumSet.noneOf(Style.class), EnumSet.of(mode), output);
    }
    
    /**
     * Appends additional output to the end of this text.  
     * This method is equivalent to:
     * <p>
     * <code>appendOutput(EnumSet.noneOf(Style.class), EnumSet.allOf(Mode.class), output)</code>
     *
     * @param   output The additional output to embed in this one.
     */
    public void appendOutput(Output output)
    {
        appendOutput(EnumSet.noneOf(Style.class), EnumSet.allOf(Mode.class), output);
    }
    
    /**
     * Appends <code>text</code> to the end of this output with the
     * {@link Style#FACE_MATH FACE_MATH} attribute.  This is purely a convenience
     * method and is equivalent to:
     * <p>
     * <code>appendText(EnumSet.of(FACE_MATH), text)</code>
     *
     * @param   text The text to append.
     */
    public void appendMath(String text)
    {
        appendText(EnumSet.of(Style.FACE_MATH), EnumSet.allOf(Mode.class), text);
    }
    
    /**
     * Appends an abstract <code>Symbol</code> to the end of this output.
     * <p>
     * The symbol will be formatted according to <code>styles</code>, and it
     * will only be displayed if the display mode matches one of
     * the specified modes.
     * 
     * @param   styles The styles to apply to the symbol.
     * @param   modes The display modes in which to display the symbol.
     * @param   symbol The symbol to append.
     */
    public void appendSymbol(EnumSet<Style> styles, EnumSet<Mode> modes, Symbol symbol)
    {
        append(new Block(styles, modes, symbol));
    }
    
    /**
     * Appends an abstract <code>Symbol</code> to the end of this output.
     * The symbol will be formatted according to <code>styles</code>.
     * <p>
     * <code>appendSymbol(styles, EnumSet.allOf(Mode.class), symbol)</code>
     * 
     * @param   styles The styles to apply to the symbol.
     * @param   symbol The symbol to append.
     */
    public void appendSymbol(EnumSet<Style> styles, Symbol symbol)
    {
        appendSymbol(styles, EnumSet.allOf(Mode.class), symbol);
    }
    
    /**
     * Appends an abstract <code>Symbol</code> to the end of this output.
     * The symbol will only be displayed in the specified display mode.
     * This method is equivalent to:
     * <p>
     * <code>appendSymbol(EnumSet.noneOf(Style.class), EnumSet.of(mode), symbol)</code>
     * 
     * @param   mode The display mode in which to display the symbol.
     * @param   symbol The symbol to append.
     */
    public void appendSymbol(Mode mode, Symbol symbol)
    {
        appendSymbol(EnumSet.noneOf(Style.class), EnumSet.of(mode), symbol);
    }
    
    /**
     * Appends an abstract <code>Symbol</code> to the end of this output.
     * This method is equivalent to:
     * <p>
     * <code>appendSymbol(EnumSet.noneOf(Style.class), EnumSet.allOf(Mode.class), symbol)</code>
     * 
     * @param   symbol The symbol to append.
     */
    public void appendSymbol(Symbol symbol)
    {
        appendSymbol(EnumSet.of(Style.FACE_MATH), EnumSet.allOf(Mode.class), symbol);
    }
    
    /**
     * Gets a set containing all style attributes that appear anywhere in
     * this <code>StyledTextOutput</code>.
     *
     * @return  The set of style attributes.
     */
    public EnumSet<Style> allStyles()
    {
        EnumSet<Style> styles = EnumSet.noneOf(Style.class);
        buildAllStyles(styles, topBlock);
        return styles;
    }
    
    private static void buildAllStyles(EnumSet<Style> styles, Block block)
    {
        styles.addAll(block.styles);
        if (block.subBlocks != null)
        {
            for (Block sub : block.subBlocks)
            {
                buildAllStyles(styles, sub);
            }
        }
    }
    
    /**
     * Gets an iterator over this text output.
     *
     * @param   maxLength The maximum number of characters to include
     *          in the iteration.
     * @return  An iterator over this text output.
     */
    public AttributedCharacterIterator characterIterator(int maxLength)
    {
        if (maxLength == -1 || maxLength > MAX_STO_LENGTH)
        {
            maxLength = MAX_STO_LENGTH + 6;
        }
        return new STOCharacterIterator(topBlock, maxLength);
    }
    
    /**
     * Counts the number of characters that will be displayed when this
     * <code>StyledTextOutput</code> is fully written out in
     * {@link Output.Mode#GRAPHICAL GRAPHICAL} mode.
     */
    public int characterCount()
    {
        return Math.min(MAX_STO_LENGTH, topBlock.totalLength(Mode.GRAPHICAL));
    }
    
    @Override
    public void write(PrintWriter out, Mode mode)
    {
        if (mode != Mode.PLAIN_TEXT)
        {
            throw new UnsupportedOperationException();
        }
        write(out, mode, topBlock);
    }
    
    private static void write(PrintWriter out, Mode mode, Block block)
    {
        if (block.modes.contains(mode))
        {
            if (block.text != null)
            {
                out.print(block.text);
            }
            else if (block.embeddedOutput != null)
            {
                block.embeddedOutput.write(out, mode);
            }
            else if (block.symbol != null)
            {
                out.print(block.symbol.getPlainTextString());
            }
            else
            {
                for (Block sub : block.subBlocks)
                {
                    write(out, mode, sub);
                }
            }
        }
    }
    
    // Cached layout data for rapid repainting
    
    private List<LayoutInfo> layouts = new ArrayList<LayoutInfo>();
    private int activeMeasurerLength;
    private int widthOfLayouts;
    private Dimension size;
    
    @Override
    public Dimension getSize(int preferredWidth)
    {
        return getSize(preferredWidth, -1);
    }
    
    public Dimension getSize(int preferredWidth, int maxLength)
    {
        updateLayouts(getScreenFrc(), preferredWidth == 0 ? 1 << 20 : preferredWidth, maxLength);
        return size;
    }
    
    @Override
    public void paint(Graphics2D g, int preferredWidth)
    {
        paint(g, preferredWidth, -1);
    }
    
    public void paint(Graphics2D g, int preferredWidth, int maxLength)
    {
        updateLayouts(g.getFontRenderContext(), preferredWidth == 0 ? Integer.MAX_VALUE : preferredWidth, maxLength);
        g = (Graphics2D) g.create(0, 0, size.width, size.height);
        Rectangle clipRect = g.getClipBounds();
        g.setBackground(Color.white);
        g.setColor(Color.white);
        g.fill(clipRect);
        g.setColor(Color.black);
        for (LayoutInfo info : layouts)
        {
            if (info.bottom > clipRect.y &&
                info.top < clipRect.y + clipRect.height)
            {
                info.layout.draw(g, 0.0f, 1.0f + info.top + info.layout.getAscent());
            }
        }
    }
    
    private void updateLayouts(FontRenderContext frc, int pixelWidth, int maxLength)
    {
        if (characterCount() == 0)
        {
            layouts.clear();
        }
        else if (measurer == null || activeMeasurerLength < characterCount())
        {
            AttributedCharacterIterator activeIterator = characterIterator(maxLength);
            activeMeasurerLength = activeIterator.getEndIndex();
            measurer = new LineBreakMeasurer(activeIterator, new CgBreakIterator(), frc);
            layouts.clear();
        }
        else if (pixelWidth != widthOfLayouts)
        {
            measurer.setPosition(0);
            layouts.clear();
            widthOfLayouts = pixelWidth;
        }
        if (activeMeasurerLength == 0)
        {
            size = new Dimension(0, 0);
            return;
        }
        while (measurer.getPosition() < activeMeasurerLength)
        {
            layouts.add(new LayoutInfo(measurer.nextLayout((float) (pixelWidth - 1))));
        }
        
        int width = (layouts.size() == 1 ? (int) layouts.get(0).layout.getVisibleAdvance() + 1 : pixelWidth);
        
        // Determine the height
        float y = 0.0f;
        for (LayoutInfo info : layouts)
        {
            info.top = y;
            y += info.layout.getAscent() + info.layout.getLeading() + 5;
            info.bottom = y;
        }
        int height = (int) y + 2;
        size = new Dimension(width, height);
    }
    
    private static class LayoutInfo
    {
        TextLayout layout;
        float top;
        float bottom;
        
        LayoutInfo(TextLayout layout)
        {
            this.layout = layout;
        }
    }
    
    private static final Set<Attribute> ATTRIBUTE_KEYS;
    
    static
    {
        Set<Attribute> attributeKeys = new HashSet<Attribute>();
        attributeKeys.add(TextAttribute.FONT);
        attributeKeys.add(TextAttribute.CHAR_REPLACEMENT);
        attributeKeys.add(TextAttribute.FOREGROUND);
        ATTRIBUTE_KEYS = Collections.unmodifiableSet(attributeKeys);
    }
    
    private static class STOCharacterIterator implements AttributedCharacterIterator
    {
        final int maxLength;
        
        Stack<Block> blockStack;
        Stack<Integer> curPosStack;
        int index;
        
        private STOCharacterIterator(int maxLength)
        {
            this.maxLength = maxLength;
            blockStack = new Stack<Block>();
            curPosStack = new Stack<Integer>();
        }
        
        STOCharacterIterator(Block topBlock, int maxLength)
        {
            this(maxLength);
            blockStack.add(topBlock);
            curPosStack.add(-1);
            index = -1;
            next();
        }
        
        @Override
        public STOCharacterIterator clone()
        {
            STOCharacterIterator clone = new STOCharacterIterator(maxLength);
            clone.blockStack = new Stack<Block>();
            clone.blockStack.addAll(blockStack);
            clone.curPosStack = new Stack<Integer>();
            clone.curPosStack.addAll(curPosStack);
            return clone;
        }
        
        @Override
        public char current()
        {
            if (blockStack.size() == 1 || index == maxLength)
            {
                return DONE;
            }
            
            Block block = blockStack.peek();
            int pos = curPosStack.peek();
            
            if (block.text != null)
            {
                return block.text.charAt(pos);
            }
            else if (block.embeddedOutput != null)
            {
                assert pos == 0;
                return '?';
            }
            else if (block.symbol != null)
            {
                assert pos == 0;
                return block.symbol.getCharacter();
            }
            else
            {
                return '?';
            }
        }
        
        @Override
        public char first()
        {
            index = -1;
            while (blockStack.size() > 1)
            {
                blockStack.pop();
                curPosStack.pop();
            }
            curPosStack.set(0, -1);
            return next();
        }

        @Override
        public int getBeginIndex()
        {
            return 0;
        }

        @Override
        public int getEndIndex()
        {
            return Math.min(maxLength, blockStack.firstElement().totalLength(Mode.GRAPHICAL));
        }

        @Override
        public int getIndex()
        {
            return index;
        }

        @Override
        public char last()
        {
            return setIndex(getEndIndex()-1);
        }

        @Override
        public final char next()
        {
            advance();
            return current();
        }
        
        private void advance()
        {
            index++;
            curPosStack.push(curPosStack.pop()+1);
            
            while (curPosStack.peek() == blockStack.peek().length() && blockStack.size() > 1 ||
                   curPosStack.peek() < blockStack.peek().length() &&
                   (!blockStack.peek().modes.contains(Mode.GRAPHICAL) || blockStack.peek().subBlocks != null))
            {
                if (curPosStack.peek() == blockStack.peek().length())
                {
                    blockStack.pop();
                    curPosStack.pop();
                    curPosStack.push(curPosStack.pop()+1);
                }
                else if (blockStack.peek().modes.contains(Mode.GRAPHICAL))
                {
                    blockStack.push(blockStack.peek().subBlocks.get(curPosStack.peek()));
                    curPosStack.push(0);
                }
                else
                {
                    // Just skip this one.
                    curPosStack.push(curPosStack.pop()+1);
                }
            }
        }
        
        @Override
        public char previous()
        {
            retreat();
            return current();
        }
        
        private void retreat()
        {
            index--;
            curPosStack.push(curPosStack.pop()-1);
            
            while (curPosStack.peek() == -1 && blockStack.size() > 1 ||
                   curPosStack.peek() < blockStack.peek().length() &&
                   (!blockStack.peek().modes.contains(Mode.GRAPHICAL) || blockStack.peek().subBlocks != null))
            {
                if (curPosStack.peek() == -1)
                {
                    blockStack.pop();
                    curPosStack.pop();
                    curPosStack.push(curPosStack.pop()-1);
                }
                else if (blockStack.peek().modes.contains(Mode.GRAPHICAL))
                {
                    blockStack.push(blockStack.peek().subBlocks.get(curPosStack.peek()));
                    curPosStack.push(blockStack.peek().length()-1);
                }
                else
                {
                    curPosStack.push(curPosStack.pop()-1);
                }
            }
        }

        @Override
        public char setIndex(int newIndex)
        {
            while (index < newIndex)
            {
                advance();
            }
            while (index > newIndex)
            {
                retreat();
            }
            return current();
        }
        
        public EnumSet<Style> currentStyles()
        {
            EnumSet<Style> styles = EnumSet.noneOf(Style.class);
            for (Block block : blockStack)
            {
                styles.addAll(block.styles);
            }
            return styles;
        }
        
        @Override
        public Set<Attribute> getAllAttributeKeys()
        {
            return ATTRIBUTE_KEYS;
        }
        
        private static boolean IS_VERSION_6 =
            (System.getProperty("java.version").compareTo("1.6") >= 0);
        
        @Override
        public Object getAttribute(Attribute attribute)
        {
            if (attribute.equals(TextAttribute.FONT))
            {
                Font font = getFont(currentStyles(), blockStack.peek().symbol);
                // This is a RIDICULOUS HACK to make it work with Java version 6.  Hopefully
                // I'll figure out a better solution at some point.
                if (IS_VERSION_6 && index > 0)
                {
                    retreat();
                    AffineTransform prev = getTransform(currentStyles(), blockStack.peek().symbol);
                    advance();
                    AffineTransform adjusted = AffineTransform.getTranslateInstance(-prev.getTranslateX(), -prev.getTranslateY());
                    adjusted.concatenate(getTransform(currentStyles(), blockStack.peek().symbol));
                    font = font.deriveFont(adjusted);
                }
                return font;
            }
            else if (attribute.equals(TextAttribute.FOREGROUND))
            {
                for (Style style : currentStyles())
                {
                    if (style.color() != null)
                    {
                        return style.color();
                    }
                }
                return Color.black;
            }
            else if (attribute.equals(TextAttribute.CHAR_REPLACEMENT))
            {
                if (blockStack.peek().embeddedOutput == null)
                {
                    return null;
                }
                else
                {
                    return new OutputGraphicAttribute(blockStack.peek().embeddedOutput);
                }
            }
            else
            {
                return null;
            }
        }
        
        @Override
        public Map<Attribute,Object> getAttributes()
        {
            Map<Attribute,Object> attributes =
                new HashMap<Attribute,Object>();
            for (Attribute attribute : ATTRIBUTE_KEYS)
            {
                attributes.put(attribute, getAttribute(attribute));
            }
            return attributes;
        }

        @Override
        public int getRunLimit()
        {
            return index + 1;
        }

        @Override
        public int getRunLimit(Set<? extends Attribute> set)
        {
            return getRunLimit();
        }

        @Override
        public int getRunLimit(Attribute attribute)
        {
            return getRunLimit();
        }

        @Override
        public int getRunStart()
        {
            return index - curPosStack.peek();
        }

        @Override
        public int getRunStart(Attribute attribute)
        {
            return getRunStart();
        }

        @Override
        public int getRunStart(Set set)
        {
            return getRunStart();
        }
    }
    
    private static class OutputGraphicAttribute extends GraphicAttribute
    {
        Output output;
        
        OutputGraphicAttribute(Output output)
        {
            super(GraphicAttribute.ROMAN_BASELINE);
            this.output = output;
        }
        
        @Override
        public void draw(Graphics2D g, float x, float y)
        {
            Dimension size = output.getSize(0);
            output.paint((Graphics2D) g.create((int) x, (int) (y-getAscent()), size.width, size.height), 0);
        }
        
        @Override
        public float getAdvance()
        {
            return output.getSize(0).width;
        }
        
        @Override
        public float getAscent()
        {
            return output.getSize(0).height;
        }
        
        @Override
        public float getDescent()
        {
            return 0.0f;
        }
    }
    
    private static class Block
    {
        EnumSet<Style> styles;
        EnumSet<Mode> modes;
        String text;
        Output embeddedOutput;
        Symbol symbol;
        List<Block> subBlocks;
        
        public Block(String text)
        {
            this(EnumSet.noneOf(Style.class), text);
        }
        
        public Block(Symbol symbol)
        {
            this(EnumSet.noneOf(Style.class), symbol);
        }
        
        public Block(Block ... subBlocks)
        {
            this(EnumSet.noneOf(Style.class), subBlocks);
        }
        
        public Block(Output embeddedOutput)
        {
            this(EnumSet.allOf(Mode.class), embeddedOutput);
        }
        
        public Block(EnumSet<Style> styles, String text)
        {
            this(styles, EnumSet.allOf(Mode.class), text);
        }
        
        public Block(EnumSet<Style> styles, Symbol symbol)
        {
            this(styles, EnumSet.allOf(Mode.class), symbol);
        }
        
        public Block(EnumSet<Style> styles, Block ... subBlocks)
        {
            this(styles, EnumSet.allOf(Mode.class), subBlocks);
        }
        
        public Block(EnumSet<Style> styles, EnumSet<Mode> modes, String text)
        {
            this.styles = styles;
            this.modes = modes;
            this.text = text;
        }
        
        public Block(EnumSet<Style> styles, EnumSet<Mode> modes, Symbol symbol)
        {
            this.styles = styles;
            this.modes = modes;
            this.symbol = symbol;
        }
        
        public Block(EnumSet<Style> styles, EnumSet<Mode> modes, Block ... subBlocks)
        {
            this.styles = styles;
            this.modes = modes;
            this.subBlocks = new ArrayList<Block>(subBlocks.length);
            this.subBlocks.addAll(Arrays.asList(subBlocks));
        }
        
        public Block(EnumSet<Mode> modes, Output embeddedOutput)
        {
            this.styles = EnumSet.noneOf(Style.class);
            this.modes = modes;
            this.embeddedOutput = embeddedOutput;
        }
        
        public int length()
        {
            if (text != null)
            {
                return text.length();
            }
            else if (subBlocks != null)
            {
                return subBlocks.size();
            }
            else
            {
                return 1;
            }
        }
        
        public int totalLength(Mode mode)
        {
            if (mode != null && !modes.contains(mode))
            {
                // Not in this display mode.
                return 0;
            }
            else if (subBlocks == null)
            {
                return length();
            }
            else
            {
                int length = 0;
                for (Block sub : subBlocks)
                {
                    length += sub.totalLength(mode);
                }
                return length;
            }
        }
    }
    
    /**
     * Gets a <code>Font</code> that can display the specified symbol with
     * the specified style attributes.  All style attributes will be
     * pre-applied to the font, except for <code>COLOR</code> attributes.
     * An appropriate {@link AffineTransform} will be applied to
     * resolve the <code>LOCATION</code> attribute (if any).
     * 
     * @param   styles The style attributes to apply to the font.
     * @param   symbol The symbol to use, or <code>null</code> for plain text.
     * @return  The corresponding <code>Font</code>.
     */
    public static Font getFont(EnumSet<Style> styles, Symbol symbol)
    {
        FontKey key = new FontKey(styles, symbol);
        if (fontCache.containsKey(key))
        {
            return fontCache.get(key);
        }
        
        Font font;
        
        if (symbol == null)
        {
            font = (styles.contains(Style.FACE_MONOSPACED) ? MONOSPACED_FONT : SANS_SERIF_FONT);
        }
        else
        {
            font = symbol.getFont();
        }
        
        font = font.deriveFont(getTransform(styles, symbol));
        
        fontCache.put(key, font);
        return font;
    }
    
    private static AffineTransform getTransform(EnumSet<Style> styles, Symbol symbol)
    {
        FontKey key = new FontKey(styles, symbol);
        if (transformCache.containsKey(key))
        {
            return transformCache.get(key);
        }
        
        AffineTransform affineTransform = new AffineTransform();
        
        if (styles.contains(Style.LOCATION_UPPER_LIMIT))
        {
            affineTransform.translate(0.5, -12.0);
            affineTransform.scale(0.5, 0.5);
        }
        else if (styles.contains(Style.LOCATION_LOWER_LIMIT))
        {
            affineTransform.translate(-10.0, 6.0);
            affineTransform.scale(0.5, 0.5);
        }
        else if (styles.contains(Style.LOCATION_SUPERSCRIPT))
        {
            affineTransform.translate(0.0, -8.0);
            affineTransform.scale(0.5, 0.5);
        }
        else if (styles.contains(Style.LOCATION_SUBSCRIPT))
        {
            affineTransform.translate(0.0, 0.5);
            affineTransform.scale(0.5, 0.5);
        }
        else if (styles.contains(Style.LOCATION_NUMERATOR))
        {
            affineTransform.translate(0.0, -8.0);
            affineTransform.scale(0.5, 0.5);
        }
        else if (styles.contains(Style.LOCATION_DENOMINATOR))
        {
            affineTransform.translate(0.0, 0.5);
            affineTransform.scale(0.5, 0.5);
        }
        
        if (symbol == null && styles.contains(Style.SIZE_HUGE) || symbol == Symbol.INTEGRAL)
        {
            affineTransform.scale(1.4, 1.4);
        }
        else if (symbol == null && !styles.contains(Style.FACE_MATH) && !styles.contains(Style.FACE_MONOSPACED) && !styles.contains(Style.SIZE_LARGE))
        {
            affineTransform.scale(0.75, 0.75);
        }
        
        if (symbol != null && symbol.getFont() == SYMBOL_FONT && symbol != Symbol.INTEGRAL)
        {
            affineTransform.translate(0.0, -1.0);
        }
        
        transformCache.put(key, affineTransform);
        return affineTransform;
    }
    
    private static class FontKey
    {
        EnumSet<Style> styles;
        Symbol symbol;
        
        FontKey(EnumSet<Style> styles, Symbol symbol)
        {
            this.styles = EnumSet.copyOf(styles);
            this.symbol = symbol;
        }
        
        @Override
        public int hashCode()
        {
            return styles.hashCode() ^ (symbol == null ? 0 : symbol.hashCode());
        }
        
        @Override
        public boolean equals(Object obj)
        {
            return obj instanceof FontKey && styles.equals(((FontKey) obj).styles) &&
                (symbol == null && ((FontKey) obj).symbol == null ||
                 symbol != null && symbol.equals(((FontKey) obj).symbol));
        }
    }
}

