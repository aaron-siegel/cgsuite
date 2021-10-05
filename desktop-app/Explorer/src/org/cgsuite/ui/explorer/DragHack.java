/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.explorer;

// Java mouseClicked is broken on high resolution MacOS displays (sigh). The drag sensitivity
// is too high (and does not respect the awt.dnd.drag.threshold system property), causing
// even slight movements of the mouse to register as drags, not clicks. This workaround
// treats a mouseReleased as a click if EITHER: the mouse was pressed for less than 150 ms,
// OR the cursor has moved by less than 7 pixels in both the x and y directions.

public class DragHack {

    public final static long MIN_DRAG_DURATION_NANOS = 150 * 1000000L;
    public final static int DRAG_SENSITIVITY = 7;

    private long mousePressedNanoTime;
    private int mousePressedX;
    private int mousePressedY;
    private boolean isDragging;

    public void registerMousePressed(int x, int y) {
        mousePressedNanoTime = System.nanoTime();
        mousePressedX = x;
        mousePressedY = y;
        isDragging = false;
    }

    public void registerMouseDragged(int x, int y) {
        if (System.nanoTime() - mousePressedNanoTime >= MIN_DRAG_DURATION_NANOS &&
            (Math.abs(x - mousePressedX) >= DRAG_SENSITIVITY || Math.abs(y - mousePressedY) >= DRAG_SENSITIVITY)) {
            isDragging = true;
        }
    }

    public boolean isDragging() {
        return isDragging;
    }

}
