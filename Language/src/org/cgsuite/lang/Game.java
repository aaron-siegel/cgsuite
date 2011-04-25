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

    public CgsuiteSet getLeftOptions()
    {
        return (CgsuiteSet) invokeMethod("LeftOptions$get");
    }

    public CgsuiteSet getRightOptions()
    {
        return (CgsuiteSet) invokeMethod("RightOptions$get");
    }

    public EditorPanel toEditor()
    {
        return new DefaultEditorPanel(this);
    }
}
