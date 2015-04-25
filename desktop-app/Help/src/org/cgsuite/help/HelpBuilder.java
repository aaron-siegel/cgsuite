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

package org.cgsuite.help;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelpBuilder
{
    private final static String[] CONVERT_PATHS =
    {
        "",
        "tutorials",
        "tutorials/advancedfeatures",
        "tutorials/gettingstarted",
        "tutorials/usingcgscript"
    };
    
    private final static FileFilter HTML_FILTER = new FileFilter()
    {
        @Override
        public boolean accept(File file)
        {
            return file.getName().endsWith(".html");
        }
    };
    
    private final static FileFilter CGSH_FILTER = new FileFilter()
    {
        @Override
        public boolean accept(File file)
        {
            return file.getName().endsWith(".cgsh");
        }
    };
    
    private File srcRoot;
    private File targetRoot;
    private List<HelpInfo> info;
    
    public static void main(String[] args) throws Exception
    {
        new HelpBuilder(new File(args[0]), new File(args[1])).run();
    }
    
    private HelpBuilder(File srcRoot, File targetRoot)
    {
        this.srcRoot = srcRoot;
        this.targetRoot = targetRoot;
        this.info = new ArrayList<HelpInfo>();
    }
    
    private void run() throws Exception
    {
        System.out.println("Running CGSuite Help Builder");
        System.out.println("Source root: " + srcRoot);
        System.out.println("Target root: " + targetRoot);
        
        for (String relPath : CONVERT_PATHS)
        {
            convertDir(relPath);
        }
        
        writeMasterToc();
    }
    
    private void convertDir(String relPath) throws Exception
    {
        File srcDir = new File(srcRoot, relPath);
        File targetDir = new File(targetRoot, relPath);
        
        System.out.println("Converting dir: " + srcDir);
        
        targetDir.mkdirs();
        
        for (File file : srcDir.listFiles(CGSH_FILTER))
        {
            convert(file, targetDir, relPath);
        }
        
        for (File file : srcDir.listFiles(HTML_FILTER))
        {
            addHelpInfo(relPath, file);
        }
    }
    
    private void convert(File file, File targetDir, String relPath) throws Exception
    {
        File targetFile = new File(targetDir, file.getName().replace(".cgsh", ".html"));
        
        System.out.println("Converting file: " + file.getName() + " -> " + targetFile);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        
        StringBuilder str = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null)
        {
            str.append(line);
            str.append('\n');
        }
        in.close();
        
        String title = file.getName().substring(0, file.getName().lastIndexOf('.'));
        String input = str.toString();
        List<String> tocItems = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\\+\\+(.*)\\+\\+");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find())
        {
            tocItems.add(matcher.group(1));
        }
        
        String markedUp = markup(input, tocItems);
        
        PrintStream out = new PrintStream(new FileOutputStream(targetFile));
        
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
        out.println("<html><head>");
        out.println("<link rel=\"stylesheet\" href=\"nbdocs:/org/cgsuite/help/docs/cgsuite.css\" type=\"text/css\">");
        out.println("<style type=\"text/css\">");
        out.println("code { font-size: 13pt; }");
        out.println("ul { list-style-type: disc; list-style-image: none; list-style-position: outside; }");
        out.println("</style>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        out.println("<title>" + title + "</title>");
        out.println("</head><body>");
        
        out.println("<h2>" + title + "</h2>");
        
        out.println("<p>");
        out.println(markedUp);
        out.println("</body></html>");
        out.close();
        
        addHelpInfo(relPath, targetFile);
    }
    
    private void addHelpInfo(String relPath, File targetFile)
    {
        String title = targetFile.getName().substring(0, targetFile.getName().lastIndexOf('.'));
        if ("".equals(relPath))
        {
            info.add(new HelpInfo(targetFile.getName(), title, title));
        }
        else
        {
            String targetRelPath = relPath + "/" + targetFile.getName();
            info.add(new HelpInfo(targetRelPath, relPath.replaceAll("/", ".") + "." + title, title));
        }
    }
    
    private static String toAnchor(String str)
    {
        return str.replaceAll("\\:|\\,|\\ ", "");
    }
    
    private static String markup(String input, List<String> tocItems)
    {
        String markup = input;
        markup = markup.replaceAll("\\\\\\>", "&gt;");
        markup = markup.replaceAll("\\\\\\<", "&lt;");
        markup = markup.replaceAll("\\\\\\^", "&renderascaret;");
        markup = markup.replaceAll("\\\\\"", "&renderasquote;");
        markup = markup.replaceAll("\\\\\\$", "&renderasdollar;");
        markup = markup.replaceAll("\\\\u", "&uarr;");
        markup = markup.replaceAll("\\\\d", "&darr;");
        markup = markup.replaceAll("\\\"", "&quot;");
        markup = replaceAllCode(markup);
        markup = markup.replaceAll("\\_(.*?)\\_", "<sub>$1</sub>");
        markup = markup.replaceAll("\\^(.*?)\\^", "<sup>$1</sup>");
        markup = markup.replaceAll("\\~(.*?)\\~", "<em>$1</em>");
        markup = replaceAllSectionHeadings(markup);
        markup = markup.replaceAll("(\n)*##TOC##(\n)*", makeToc(tocItems));
        markup = markup.replaceAll("\n\n", "\n\n<p>");
        markup = markup.replaceAll("\\[\\[(.*?)\\]\\[(.*?)\\]\\]", "<a href=\"$2.html\">$1</a>");
        markup = markup.replaceAll("\\[\\[(.*?)\\]\\]", "<a href=\"$1.html\">$1</a>");
        markup = markup.replaceAll("&renderascaret;", "^");
        markup = markup.replaceAll("&renderasquote;", "\"");
        markup = markup.replaceAll("&renderasdollar;", "\\$");
        return markup;
    }
    
    private static String makeToc(List<String> tocItems)
    {
        StringBuilder str = new StringBuilder();
        str.append("<ul>\n");
        
        for (String tocItem : tocItems)
        {
            str.append("<li><a href=\"#");
            str.append(toAnchor(tocItem));
            str.append("\">");
            str.append(tocItem.trim());
            str.append("</a>\n");
        }
        
        str.append("</ul>\n");
        
        return str.toString();
    }
    
    private static String replaceAllCode(String input)
    {
        Pattern pattern = Pattern.compile("\\$(.*?)\\$", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);
        StringBuffer buf = new StringBuffer();
        while (matcher.find())
        {
            String codeBlock = matcher.group(1);
            codeBlock = codeBlock.replaceAll("\n", "<br>\n");
            codeBlock = codeBlock.replaceAll("\\ ", "&nbsp;");
            matcher.appendReplacement(buf, "<code>" + codeBlock + "</code>");
        }
        matcher.appendTail(buf);
        return buf.toString();
    }
    
    private static String replaceAllSectionHeadings(String input)
    {
        Pattern pattern = Pattern.compile("\n*\\+\\+(.*?)\\+\\+");
        Matcher matcher = pattern.matcher(input);
        StringBuffer buf = new StringBuffer();
        while (matcher.find())
        {
            matcher.appendReplacement(buf, "<a name=\"" + toAnchor(matcher.group(1)) + "\"></a><h3>$1</h3>");
        }
        matcher.appendTail(buf);
        return buf.toString();
    }
    
    private void writeMasterToc() throws Exception
    {
        /*
        File tocFile = new File(targetRoot, "help-toc.xml");
        
        System.out.println("Writing ToC: " + tocFile);
        
        PrintStream out = new PrintStream(new FileOutputStream(tocFile));
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<!DOCTYPE toc PUBLIC \"-//Sun Microsystems Inc.//DTD JavaHelp TOC Version 2.0//EN\" \"http://java.sun.com/products/javahelp/toc_2_0.dtd\">");
        out.println("<toc version=\"2.0\">");
        
        for (HelpInfo hi : info)
        {
            out.println("<tocitem text=\"" + hi.text + "\" target=\"" + hi.target + "\"/>");
        }
        
        out.println("</toc>");
        out.close();
        */
        File mapFile = new File(targetRoot, "help-map.xml");
        
        System.out.println("Writing Map: " + mapFile);
        
        PrintStream out = new PrintStream(new FileOutputStream(mapFile));
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<!DOCTYPE map PUBLIC \"-//Sun Microsystems Inc.//DTD JavaHelp Map Version 2.0//EN\" \"http://java.sun.com/products/javahelp/map_2_0.dtd\">");
        out.println("<map version=\"2.0\">");
        
        for (HelpInfo hi : info)
        {
            out.println("<mapID target=\"" + hi.target + "\" url=\"" + hi.relPath + "\"/>");
        }
        
        out.println("</map>");
        out.close();
    }
    
    private static class HelpInfo
    {
        String relPath;
        String target;
        String text;

        HelpInfo(String relPath, String target, String text)
        {
            this.relPath = relPath;
            this.target = target;
            this.text = text;
        }
    }
    
    /*
    public static void generateHelpPages(File inputXml, File outputDir) throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException, java.io.IOException
    {
        Document document = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputXml);
        NodeList methodElements = document.getElementsByTagName("method");
        for (int i = 0; i < methodElements.getLength(); i++)
        {
            Element methodElement = (Element) methodElements.item(i);
            String name = methodElement.getAttribute("name");
            String category = methodElement.getAttribute("category");
            NodeList parameters = methodElement.getElementsByTagName("argument");
            File categoryDir = new File(outputDir, category);
            categoryDir.mkdir();
            File outputfile = new File(categoryDir, name + ".html");
            java.io.PrintStream out = new java.io.PrintStream(new java.io.FileOutputStream(outputfile));
            out.print("<html>\n<head>\n<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
            out.print("<title>" + name + "</title>\n</head>\n<body>\n");
            out.print("<h2>" + category + "</h2>\n");
            out.print("<h1><font face=\"monospaced\">" + name + "(");
            for (int j = 0; j < parameters.getLength(); j++)
            {
                out.print(markup(((Element) parameters.item(j)).getAttribute("name")));
                if (j+1 < parameters.getLength())
                {
                    out.print(",");
                }
            }
            out.print(")</font></h1>\n");
            out.print("<p>" + markup(methodElement.getElementsByTagName("description").item(0).getTextContent()));
            out.print("<p><b>Parameters</b></p>\n");
            for (int j = 0; j < parameters.getLength(); j++)
            {
                Element paramElement = (Element) parameters.item(j);
                out.print("<br><code>" + markup(paramElement.getAttribute("name")) + " - " + paramElement.getAttribute("type") + "</code>\n");
            }
            NodeList returns = methodElement.getElementsByTagName("returns");
            if (returns.getLength() > 0)
            {
                out.print("<p><b>Returns</b></p>\n");
                out.print("<p><code>" + ((Element) returns.item(0)).getAttribute("type") + "</code></p>\n");
            }
            NodeList examples = methodElement.getElementsByTagName("examples");
            if (examples.getLength() > 0)
            {
                out.print("<p><b>Examples</b></p>\n");
                out.print("<p>" + markup(examples.item(0).getTextContent()));
            }
            NodeList seeMethod = methodElement.getElementsByTagName("see-method");
            if (seeMethod.getLength() > 0)
            {
                out.print("<p><b>See Also</b></p><p>\n");
                for (int j = 0; j < seeMethod.getLength(); j++)
                {
                    Element seeMethodElement = (Element) seeMethod.item(j);
                    out.print("<code><a href=\"../" + seeMethodElement.getAttribute("category") + "/" + seeMethodElement.getAttribute("name") + ".html\">"
                              + seeMethodElement.getAttribute("name") + "</a></code>");
                    if (j+1 < seeMethod.getLength())
                    {
                        out.print(",");
                    }
                    out.println();
                }
                out.print("</p>\n");
            }
            
            out.print("</body>\n</html>\n");
            out.close();
        }
    }
     */
}