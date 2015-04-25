/*
 * APChecker.java
 *
 * Created on November 14, 2006, 9:07 AM
 *
 * $Id: APChecker.java,v 1.7 2007/02/20 23:52:29 malbert Exp $
 */

/* ****************************************************************************
 * 
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

package org.cgsuite.lang.impartial;

import java.util.List;

/**
 * Class defining testing methods for determining whether a sequence of values represents an
 * arithmeto-periodic (AP) sequence, subject to certain test criteria.
 *
 * @author malbert
 * @version $Revision: 1.7 $ $Date: 2007/02/20 23:52:29 $
 */
public class APChecker {
  
  static int[] suffixPointers = new int[1000];
  
  private int maxPeriod = Integer.MAX_VALUE;
  private int maxPreperiod = Integer.MAX_VALUE;
  private int maxSaltus = Integer.MAX_VALUE;
  private int preperiodMultiplier = 1;
  private int periodMultiplier = 1;
  private int offset = 0;
  
  /**
   * Constructs a new APChecker object. Owing to the complexity of the potential data fields
   * for the periodicity criteria, these are all to be set using methods rather than at
   * construction. By default, if unset, any AP behaviour with at least one full repetition of 
   * ther period will be recognised.
   */
  public APChecker() {};
  
  /**
   * Sets the maximum allowed period.
   * @param maxPeriod The maximum allowed period.
   */
  public void setMaxPeriod(int maxPeriod) {
    this.maxPeriod = maxPeriod;
  }
  
  /**
   * Sets the maximum allowed preperiod.
   * @param maxPreperiod The maximum allowed preperiod.
   */
  public void setMaxPreperiod(int maxPreperiod) {
    this.maxPreperiod = maxPreperiod;
  }
  
  /**
   * Sets the maximum allowed absolute value of the saltus.
   * @param maxSaltus The maximum allowed absolute value of the saltus.
   */
  public void setMaxSaltus(int maxSaltus) {
    this.maxSaltus = maxSaltus;
  }
  
  /**
   * Sets the preperiod multiplier for the linear combination test.
   * @param preperiodMultiplier The preperiod multiplier.
   */
  public void setPreperiodMultiplier(int preperiodMultiplier) {
    this.preperiodMultiplier = preperiodMultiplier;
  }
  
  /**
   * Sets the period multiplier for the linear combination test.
   * @param periodMultiplier The period multiplier.
   */
  public void setPeriodMultiplier(int periodMultiplier) {
    this.periodMultiplier = periodMultiplier;
  }
  
  /**
   * Sets the offset for the linear combination test.
   * @param offset The offset.
   */
  public void setOffset(int offset) {
    this.offset = offset;
  }
  
  /**
   * Sets all three parameters for the linear combination test.
   * @param preperiodMultiplier The preperiod multiplier.
   * @param periodMultiplier The period multiplier.
   * @param offset The offset.
   */
  public void setLinearCriteria(int preperiodMultiplier, int periodMultiplier, int offset) {
    this.preperiodMultiplier = preperiodMultiplier;
    this.periodMultiplier = periodMultiplier;
    this.offset = offset;
  }
  
  
  /**
   * Determines whether the given <code>int[]</code> of values exhibits AP behaviour subject
   * to the criteria of this checker.  This is equivalent to
   * <code>checkSequence(values, values.length)</code>.
   * @param values The sequence to be tested.
   * @return An <CODE>APInfo</CODE> object representing the result of this test. If
   * the test is unsuccessful the return will be <CODE>null</CODE>.
   */
  public APInfo checkSequence(int[] values) {
    return checkSequence(values, values.length);
  }
  
  /**
   * Determines whether the given <code>int[]</code> of values exhibits AP behaviour subject
   * to the criteria of this checker.  <code>values</code> will be treated as an array
   * of length <code>endIndex</code>.
   * @param values The sequence to be tested.
   * @param endIndex The last index to examine (exclusive).
   * @return An <CODE>APInfo</CODE> object representing the result of this test. If
   * the test is unsuccessful the return will be <CODE>null</CODE>.
   */
  public APInfo checkSequence(int[] values, int endIndex) {
    if (suffixPointers.length < endIndex) {
      suffixPointers = new int[(endIndex*5)/4 + 1];
    }
    int top = endIndex-1;
    suffixPointers[top] = top+1;
    int i;
    for(i = top-1; i >= 0; --i) {
      int p = suffixPointers[i+1];
      while (p < endIndex) {
        if (values[i] - values[i+1] == values[p-1] - values[p]) {
          break;
        } else {
          p = suffixPointers[p];
        }
      }
      suffixPointers[i] = p-1;
      
      if (suffixPointers[i] > suffixPointers[i+1]) {
        int prep = i + 1;
        if (suffixPointers[prep] - prep <= top - suffixPointers[prep]+1) {
          int per = suffixPointers[prep] - prep;
          int saltus = values[top] - values[top-per];
          if (meetsCriteria(prep, per, saltus, endIndex)) {
            return new APInfo(prep, per, saltus);
          }
        }
      }
    }
    
   // Possible complete periodicity
    int per = suffixPointers[0];
    if (per < top) {
      int saltus = values[top] - values[top - per];
      if (meetsCriteria(0, per, saltus, endIndex)) {
        return new APInfo(0, per, saltus);
      }
    }
    
    
    return null;
  }
  
  /**
   * Determines whether the given <code>short[]</code> of values exhibits AP behaviour subject
   * to the criteria of this checker.  This is equivalent to
   * <code>checkSequence(values, values.length)</code>.
   * @param values The sequence to be tested.
   * @return An <CODE>APInfo</CODE> object representing the result of this test. If
   * the test is unsuccessful the return will be <CODE>null</CODE>.
   */
  public APInfo checkSequence(short[] values) {
    return checkSequence(values, values.length);
  }
  
  /**
   * Determines whether the given <code>short[]</code> of values exhibits AP behaviour subject
   * to the criteria of this checker.  <code>values</code> will be treated as an array
   * of length <code>endIndex</code>.
   * @param values The sequence to be tested.
   * @param endIndex The last index to examine (exclusive).
   * @return An <CODE>APInfo</CODE> object representing the result of this test. If
   * the test is unsuccessful the return will be <CODE>null</CODE>.
   */
  public APInfo checkSequence(short[] values, int endIndex) {
    if (suffixPointers.length < endIndex) {
      suffixPointers = new int[(endIndex*5)/4 + 1];
    }
    int top = endIndex-1;
    suffixPointers[top] = top+1;
    for(int i = top-1; i >= 0; --i) {
      int p = suffixPointers[i+1];
      while (p < endIndex) {
        if (values[i] - values[i+1] == values[p-1] - values[p]) {
          break;
        } else {
          p = suffixPointers[p];
        }
      }
      suffixPointers[i] = p-1;
      if (suffixPointers[i] > suffixPointers[i+1]) {
        int prep = i +1;
        if (suffixPointers[prep] - prep <= top - suffixPointers[prep]+1) {
          int per = suffixPointers[prep] - prep;
          int saltus = values[top] - values[top-per];
          if (meetsCriteria(prep, per, saltus, endIndex)) {
            return new APInfo(prep, per, saltus);
          }
        }
      }
    }
    
     // Possible complete periodicity
    int per = suffixPointers[0];
    if (per < top) {
      int saltus = values[top] - values[top - per];
      if (meetsCriteria(0, per, saltus, endIndex)) {
        return new APInfo(0, per, saltus);
      }
    }
    
    return null;
  }
  
  /**
   * Determines whether the given <code>List<Integer></code> of values exhibits AP behaviour subject
   * to the criteria of this checker.
   * @param values The sequence of values to be tested.
   * @return An <CODE>APInfo</CODE> object representing the result of this test. If
   * the test is unsuccessful the return will be <CODE>null</CODE>.
   */
  public APInfo checkSequence(List<Integer> values) {
    if (suffixPointers.length < values.size()) {
      suffixPointers = new int[(values.size()*5)/4 + 1];
    }
    int top = values.size()-1;
    suffixPointers[top] = top+1;
    for(int i = top-1; i >= 0; --i) {
      int p = suffixPointers[i+1];
      while (p < values.size()) {
        if (values.get(i) - values.get(i+1) == values.get(p-1) - values.get(p)) {
          break;
        } else {
          p = suffixPointers[p];
        }
      }
      suffixPointers[i] = p-1;
      if (suffixPointers[i] > suffixPointers[i+1]) {
        int prep = i +1;
        if (suffixPointers[prep] - prep <= top - suffixPointers[prep]+1) {
          int per = suffixPointers[prep] - prep;
          int saltus = values.get(top) - values.get(top-per);
          if (meetsCriteria(prep, per, saltus, values.size())) {
            return new APInfo(prep, per, saltus);
          }
        }
      }
    }
    
     // Possible complete periodicity
    int per = suffixPointers[0];
    if (per < top) {
      int saltus = values.get(top) - values.get(top - per);
      if (meetsCriteria(0, per, saltus, values.size())) {
        return new APInfo(0, per, saltus);
      }
    }
    
    return null;
  }
  
  private boolean meetsCriteria(int prep, int per, int saltus, int len) {
    return  prep <= maxPreperiod && 
      per <= maxPeriod &&
      Math.abs(saltus) <= maxSaltus &&
      preperiodMultiplier*prep + periodMultiplier*per + offset <= len;
  }
  

}
