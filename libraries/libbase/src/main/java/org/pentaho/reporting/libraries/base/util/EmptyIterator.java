/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.base.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An helper class to implement an empty iterator. This iterator will always return false when <code>hasNext</code> is
 * called.
 */
public final class EmptyIterator<T> implements Iterator<T> {
  public static final Iterator INSTANCE = new EmptyIterator();

  public static <T> Iterator<T> emptyIterator() {
    return (Iterator<T>) INSTANCE;
  }

  /**
   * DefaultConstructor.
   */
  private EmptyIterator() {
  }

  /**
   * Returns <tt>true</tt> if the iteration has more elements. (In other words, returns <tt>true</tt> if <tt>next</tt>
   * would return an element rather than throwing an exception.)
   *
   * @return <tt>true</tt> if the iterator has more elements.
   */
  public boolean hasNext() {
    return false;
  }

  /**
   * Returns the next element in the iteration.
   *
   * @return the next element in the iteration.
   * @throws java.util.NoSuchElementException iteration has no more elements.
   */
  public T next() {
    throw new NoSuchElementException( "This iterator is empty." );
  }

  /**
   * Removes from the underlying collection the last element returned by the iterator (optional operation).  This method
   * can be called only once per call to <tt>next</tt>.  The behavior of an iterator is unspecified if the underlying
   * collection is modified while the iteration is in progress in any way other than by calling this method.
   *
   * @throws UnsupportedOperationException if the <tt>remove</tt> operation is not supported by this Iterator.
   * @throws IllegalStateException         if the <tt>next</tt> method has not yet been called, or the <tt>remove</tt>
   *                                       method has already been called after the last call to the <tt>next</tt>
   *                                       method.
   */
  public void remove() {
    throw new UnsupportedOperationException( "This iterator is empty, no remove supported." );
  }
}
