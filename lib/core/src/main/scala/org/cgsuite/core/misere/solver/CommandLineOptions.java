/*
 * CommandLineOptions.java
 *
 * Created on October 19, 2006, 11:19 AM
 * $Id: CommandLineOptions.java,v 1.3 2007/02/20 20:17:50 asiegel Exp $
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

package org.cgsuite.core.misere.solver;

import java.util.*;

/**
 * Manages command-line options.
 *
 * @author  Aaron Siegel
 * @version $Revision: 1.3 $ $Date: 2007/02/20 20:17:50 $
 * @since   0.7
 */
public class CommandLineOptions
{
    private final SortedSet<Option> options;
    private final Map<String,Option> optionsByStr;
    private final int maxFreeArgs;

    private Map<Option,List<String>> results;

    public CommandLineOptions(int maxFreeArgs)
    {
        this.maxFreeArgs = maxFreeArgs;
        options = new TreeSet<>();
        optionsByStr = new HashMap<>();
    }

    public void addOption(String str, int nArgs)
    {
        addOption(str, null, nArgs, null);
    }

    public void addOption(String str, int nArgs, String description)
    {
        addOption(str, null, nArgs, description);
    }

    public void addOption(String str, String alias, int nArgs)
    {
        addOption(str, alias, nArgs, null);
    }

    public void addOption(String str, String alias, int nArgs, String description)
    {
        addOption(new Option(str, alias, nArgs, description));
    }

    public void addOption(Option option)
    {
        if (optionsByStr.containsKey(option.getStr()) ||
            optionsByStr.containsKey(option.getAlias()))
        {
            throw new IllegalArgumentException("Collision!");
        }

        options.add(option);
        optionsByStr.put(option.getStr(), option);
        if (option.getAlias() != null)
        {
            optionsByStr.put(option.getAlias(), option);
        }
    }

    public void printHelpMessage(java.io.PrintStream out)
    {
        for (Option opt : options)
        {
            out.printf(
                "  %-20s%s\n",
                "--" + opt.getStr() + (opt.getAlias() == null ? "" : ", -" + opt.getAlias()),
                opt.getDescription() == null ? "" : opt.getDescription().replace("\n", "\n                      ")
                );
        }
    }

    public void parse(String[] args)
    {
        results = new HashMap<>();
        List<String> freeArgs = new ArrayList<>();
        for (int i = 0; i < args.length; )
        {
            i = parseArgument(args, freeArgs, i);
        }
        results.put(null, freeArgs);
    }

    public List<String> getFreeArguments()
    {
        return Collections.unmodifiableList(results.get(null));
    }

    public List<String> getOptionArguments(String name)
    {
        if (!optionsByStr.containsKey(name))
        {
            throw new IllegalArgumentException("Unregistered option.");
        }
        List<String> arg = results.get(optionsByStr.get(name));
        return arg == null ? null : Collections.unmodifiableList(arg);
    }

    public boolean isSpecified(String name)
    {
        if (!optionsByStr.containsKey(name))
        {
            throw new IllegalArgumentException("Unregistered option.");
        }
        return results.containsKey(optionsByStr.get(name));
    }

    private int parseArgument(String[] args, List<String> freeArgs, int i)
    {
        if (args[i] == null || args[i].length() == 0)
        {
            throw new IllegalArgumentException(args[i]);
        }

        if (args[i].charAt(0) == '-')
        {
            if (args[i].length() == 1)
            {
                throw new IllegalArgumentException(args[i]);
            }
            String str;
            if (args[i].charAt(1) == '-')
            {
                if (args[i].length() == 2)
                {
                    throw new IllegalArgumentException(args[i]);
                }
                str = args[i].substring(2);
            }
            else
            {
                if (args[i].length() != 2)
                {
                    throw new IllegalArgumentException(args[i]);
                }
                str = args[i].substring(1);
            }
            Option opt = optionsByStr.get(str);
            if (opt == null || args.length < i + 1 + opt.getNArgs())
            {
                throw new IllegalArgumentException(args[i]);
            }
            List<String> optArgs = new ArrayList<String>(opt.getNArgs());
            for (int j = 0; j < opt.getNArgs(); j++)
            {
                optArgs.add(args[i + 1 + j]);
            }
            results.put(opt, optArgs);
            return i + 1 + opt.getNArgs();
        }
        else
        {
            if (freeArgs.size() >= maxFreeArgs)
            {
                throw new IllegalArgumentException(args[i]);
            }
            freeArgs.add(args[i]);
            return i + 1;
        }
    }

    public static class Option implements Comparable<Option>
    {
        private final String str;
        private final String alias;
        private final int nArgs;
        private final String description;

        public Option(String str, String alias, int nArgs, String description)
        {
            if (str == null)
            {
                throw new IllegalArgumentException("str == null");
            }
            this.str = str;
            this.alias = alias;
            this.nArgs = nArgs;
            this.description = description;
        }

        public String getStr()
        {
            return str;
        }

        public String getAlias()
        {
            return alias;
        }

        public int getNArgs()
        {
            return nArgs;
        }

        public String getDescription()
        {
            return description;
        }

        public @Override boolean equals(Object obj)
        {
            return obj instanceof Option &&
                str.equals(((Option) obj).str) &&
                nArgs == ((Option) obj).nArgs &&
                (alias == null && ((Option) obj).alias == null ||
                 alias != null && alias.equals(((Option) obj).alias));
        }

        public @Override int hashCode()
        {
            return str.hashCode() ^ (127 * nArgs);
        }

        public @Override String toString() { return str; }

        public int compareTo(Option other)
        {
            return str.compareTo(other.str);
        }
    }
}
