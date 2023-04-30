/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.output;

import javax.swing.*;
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

    @Override
    public OutputBox box() {
        return new OutputBox(this);
    }

    public void display(int preferredWidth) {
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        OutputBox box = box();
        box.setWorksheetWidth(preferredWidth);
        frame.getContentPane().add(box);
        frame.pack();
        frame.setVisible(true);
    }

}
