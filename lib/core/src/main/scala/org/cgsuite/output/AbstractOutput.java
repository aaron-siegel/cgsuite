/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.output;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author asiegel
 */
public abstract class AbstractOutput implements Output
{
    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        write(new PrintWriter(sw), Mode.PLAIN_TEXT);
        return sw.toString();
    }

    @Override
    public BufferedImage toImage(int preferredWidth) {
        Dimension size = getSize(preferredWidth);
        BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        paint((Graphics2D) image.getGraphics(), preferredWidth);
        return image;
    }

    @Override
    public Output toOutput() {
        return this;
    }

}
