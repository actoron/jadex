/*   Copyright 2004 BEA Systems, Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.bea.xml.stream.util;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;

/**
 * Maintains a table for namespace scope
 *
 * 
 * values = map from strings to stacks
 * [a]->[value1,0],[value2,0]
 *
 * table = a stack of bindings
 */
public class SymbolTable {
  private int depth;
  private Stack table;
  private Map values;
    
  public SymbolTable() {	
    depth = 0;
    table = new Stack();
    values = new HashMap();
  }
  
  public void clear() {
    depth = 0; 
    table.clear();
    values.clear();
  }
  
  //
  // Gets the current depth
  //
  public int getDepth() {
    return depth;
  }

  public boolean withinElement() {
    return depth > 0;
  }

  //
  // Adds a name/value pair
  //
  public void put (String name, String value) {
    table.push(new Symbol(name,value,depth));
    if (!values.containsKey(name)) {
      Stack valueStack = new Stack();
      valueStack.push(value);
      values.put(name,valueStack);
    } else {
      Stack valueStack = (Stack) values.get(name);
      valueStack.push(value);
    }
  }
    
  //
  // Gets the value for a variable
  //
  public String get (String name) 
  {
    Stack valueStack = (Stack) values.get(name);
    if (valueStack==null || valueStack.isEmpty()) 
      return null;
    return (String) valueStack.peek();
  }

  public Set getAll(String name) 
  {
    HashSet result = new HashSet();
    Iterator i = table.iterator();
    while (i.hasNext()) {
      Symbol s = (Symbol) i.next();
      if (name.equals(s.getName()))
        result.add(s.getValue());
    }
    return result;
  }

  //
  // add a new highest level scope to the table
  // 
  public void openScope() {
    depth++;
  }

  //
  // remove the highest level scope from the table
  // 

  
  public void closeScope() {
    // Get the top binding
    Symbol symbol = (Symbol) table.peek();
    int symbolDepth = symbol.depth;

    // check if it needs to be popped of the table
    while (symbolDepth == depth && !table.isEmpty()) {
      symbol = (Symbol) table.pop();

      // pop its value as well
      Stack valueStack = (Stack) values.get(symbol.name);
      valueStack.pop();

      // check the next binding
      if (!table.isEmpty()) {
        symbol = (Symbol) table.peek();
        symbolDepth = symbol.depth;
      } else break;
    }
    depth--;
  }

  public String toString() {
    Iterator i = table.iterator();
    String retVal="";
    while(i.hasNext()) {
      Symbol symbol = (Symbol) i.next();
      retVal = retVal + symbol + "\n";
    }
    return retVal;
  }

  public static void main(String args[]) 
    throws Exception
  {
    SymbolTable st = new SymbolTable();
    st.openScope();
    st.put("x","foo");
    st.put("y","bar");
    System.out.println("1 x:"+st.get("x"));
    System.out.println("1 y:"+st.get("y"));
    st.openScope();
    st.put("x","bar");
    st.put("y","foo");
    st.openScope();
    st.put("x","barbie");
    st.openScope();
    st.closeScope();
    
    System.out.println("3 x:"+st.get("x"));
    st.closeScope();
    System.out.println("2 x:"+st.get("x"));
    System.out.println("2 y:"+st.get("y"));
    System.out.print(st);
    st.closeScope();
    System.out.println("1 x:"+st.get("x"));
    System.out.println("1 y:"+st.get("y"));
    st.closeScope();
    System.out.print(st);
  }
}
