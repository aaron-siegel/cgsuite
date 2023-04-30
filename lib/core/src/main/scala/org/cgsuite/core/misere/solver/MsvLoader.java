package org.cgsuite.core.misere.solver;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import javax.xml.parsers.*;

import org.cgsuite.core.impartial.TBCode;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class MsvLoader
{
    public String code;
    public TBCode rules;
    public int maxHeap;
    public Pretension pretension;
    public List<Integer> expansionHeaps;
    public int period = -1;
    public int normalPeriod, normalPpd;

    public MsvLoader(InputStream in, boolean zip) throws SAXException, IOException
    {
        if (zip)
        {
            ZipInputStream zin = new ZipInputStream(in);
            zin.getNextEntry();
            in = zin;
        }
        processInput(in);
    }

    private void processInput(InputStream in) throws SAXException, IOException
    {
        Document document;
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(in);
        }
        catch (ParserConfigurationException exc)
        {
            throw new RuntimeException(exc);
        }

        Element presentation = (Element) document.getElementsByTagName("Presentation").item(0);
        pretension = readPresentation(presentation);
        code = presentation.getAttribute("rules");
        rules = TBCode.apply(code);
        maxHeap = Integer.parseInt(presentation.getAttribute("toHeap"));
        expansionHeaps = parseIntList(document.getElementsByTagName("ExpansionHeaps").item(0).getFirstChild().getNodeValue());
    }

    private Pretension readPresentation(Element presentation)
    {
        return new Pretension(
            readQuotient((Element) presentation.getElementsByTagName("Quotient").item(0)),
            readPrefn((Element) presentation.getElementsByTagName("PretendingFunction").item(0))
            );
    }

    private BipartiteMonoid readQuotient(Element quotient)
    {
        return new BipartiteMonoid(
            readMonoid((Element) quotient.getElementsByTagName("Actions").item(0)),
            readPPortion((Element) quotient.getElementsByTagName("PPortion").item(0))
            );
    }

    private ExplicitFiniteMonoid readMonoid(Element actions)
    {
        return new ExplicitFiniteMonoid(parseIntArrayArray(actions.getFirstChild().getNodeValue()));
    }

    private BitSet readPPortion(Element pportion)
    {
        return parseBitSet(pportion.getFirstChild().getNodeValue());
    }

    private List<Integer> readPrefn(Element prefn)
    {
        return parseIntList(prefn.getFirstChild().getNodeValue());
    }

    private int[][] parseIntArrayArray(String str)
    {
        str = str.replace("], [", "]:[");
        String[] arrays = str.split(":");
        int[][] intarrays = new int[arrays.length][];
        for (int i = 0; i < arrays.length; i++)
        {
            intarrays[i] = parseIntArray(arrays[i]);
        }
        return intarrays;
    }

    private int[] parseIntArray(String str)
    {
        str = str.replace(", ", "X");
        str = str.replace("[", "");
        str = str.replace("]", "");
        String[] values = str.split("X");
        int[] array = new int[values.length];
        for (int i = 0; i < values.length; i++)
        {
            array[i] = Integer.parseInt(values[i]);
        }
        return array;
    }

    private BitSet parseBitSet(String str)
    {
        str = str.replace(", ", "X");
        str = str.replace("{", "");
        str = str.replace("}", "");
        String[] values = str.split("X");
        BitSet bitset = new BitSet();
        for (String v : values)
        {
            bitset.set(Integer.parseInt(v));
        }
        return bitset;
    }

    private List<Integer> parseIntList(String str)
    {
        int[] array = parseIntArray(str);
        List<Integer> list = new ArrayList<Integer>();
        for (int i : array)
        {
            list.add(i);
        }
        return list;
    }
}
