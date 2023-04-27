package org.cgsuite.output;

import java.awt.event.MouseEvent;

public interface InteractiveOutput extends Output {

    boolean processMouseEvent(MouseEvent evt);

}
