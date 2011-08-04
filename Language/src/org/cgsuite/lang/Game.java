package org.cgsuite.lang;

import org.cgsuite.lang.explorer.DefaultEditorPanel;
import org.cgsuite.lang.explorer.EditorPanel;
import org.cgsuite.lang.game.InverseGame;
import org.cgsuite.lang.game.SumGame;


public class Game extends CgsuiteObject
{
    public Game(CgsuiteClass type)
    {
        super(type);
    }

    @Override
    public Game simplify()
    {
        return this;
    }

    public InverseGame buildInverse()
    {
        return new InverseGame(this);
    }

    public SumGame buildSum(Game other)
    {
        if (other instanceof SumGame)
            return ((SumGame) other).buildSum(this);
        else
            return new SumGame(this, other);
    }

    public CgsuiteCollection getLeftOptions()
    {
        return (CgsuiteCollection) invokeMethod("LeftOptions$get");
    }

    public CgsuiteCollection getRightOptions()
    {
        return (CgsuiteCollection) invokeMethod("RightOptions$get");
    }

    public EditorPanel toEditor()
    {
        return new DefaultEditorPanel(this);
    }
}
