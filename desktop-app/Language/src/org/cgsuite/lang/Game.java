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

    public Game negate()
    {
        return new InverseGame(this);
    }

    public Game add(Game other)
    {
        if (other instanceof SumGame)
        {
            return ((SumGame) other).add(this, true);
        }
        else
        {
            return new SumGame(this, other);
        }
    }
    
    public Game subtract(Game other)
    {
        return new SumGame(this, other.negate());
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
