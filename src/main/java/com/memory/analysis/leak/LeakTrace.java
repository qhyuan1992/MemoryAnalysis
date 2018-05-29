package com.memory.analysis.leak;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * A chain of references that constitute the shortest strong reference path from a leaking instance
 * to the GC roots. Fixing the leak usually means breaking one of the references in that chain.
 */
public final class LeakTrace implements Serializable {
  private static final int MAX_DEPTH = 20;

  public final List<LeakTraceElement> elements;

  LeakTrace(List<LeakTraceElement> elements) {
    this.elements = unmodifiableList(new ArrayList<>(elements));
  }

  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < elements.size() && i < MAX_DEPTH; i++) {
      LeakTraceElement element = elements.get(i);
      if (i == 0) {
        sb.append("GC ROOT ");
      } else if (i == elements.size() - 1) {
        blank(sb, i);
        sb.append("|- ");
      } else {
        blank(sb, i);
        sb.append("|- ");
      }
      sb.append(element).append("\n");
    }
    return sb.toString();
  }

  public void blank(StringBuilder sb, int num) {
    for (int i = 0; i < num; i++) {
      sb.append(" ");
    }
  }

  public String toDetailedString() {
    String string = "";
    for (LeakTraceElement element : elements) {
      string += element.toDetailedString();
    }
    return string;
  }
}
