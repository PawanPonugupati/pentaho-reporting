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


package org.pentaho.reporting.engine.classic.core.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.layout.process.util.BlockLevelPaginationShiftState;
import org.pentaho.reporting.engine.classic.core.layout.process.util.InitialPaginationShiftState;
import org.pentaho.reporting.engine.classic.core.layout.process.util.RowLevelPaginationShiftState;

public class PaginationShiftStateTest extends TestCase {
  // Fictional example run. Block-level boxes contained in each other:
  // [ INIT [ CH1 ] [ CH2 ] [ CH3 [ CC1 ][ CC2 ]] [ CH4 ] ]
  // each box is assumed to have a size of 1 px, but shall be aligned to 5 px positions, starting at 5
  public void testBlockExample() {
    // represents outside box.
    InitialPaginationShiftState init = new InitialPaginationShiftState();

    BlockLevelPaginationShiftState stateCH1 = new BlockLevelPaginationShiftState();
    stateCH1.reuse( null, init, null );
    stateCH1.increaseShift( 5 );
    stateCH1.pop( null );

    assertEquals( 5, init.getShiftForNextChild() );

    BlockLevelPaginationShiftState stateCH2 = new BlockLevelPaginationShiftState();
    stateCH2.reuse( null, init, null );

    assertEquals( 5, stateCH2.getShiftForNextChild() );
    stateCH2.increaseShift( 5 );
    assertEquals( 10, stateCH2.getShiftForNextChild() );
    stateCH2.pop( null );

    assertEquals( 10, init.getShiftForNextChild() );

    BlockLevelPaginationShiftState stateCH3 = new BlockLevelPaginationShiftState();
    stateCH3.reuse( null, init, null );
    BlockLevelPaginationShiftState stateCC1 = new BlockLevelPaginationShiftState();
    stateCC1.reuse( null, stateCH3, null );
    stateCC1.increaseShift( 5 );
    stateCC1.pop( null );

    assertEquals( 15, stateCH3.getShiftForNextChild() );

    BlockLevelPaginationShiftState stateCC2 = new BlockLevelPaginationShiftState();
    stateCC2.reuse( null, stateCH3, null );
    stateCC2.increaseShift( 5 );
    stateCC2.pop( null );

    stateCH3.pop( null );
    assertEquals( 20, init.getShiftForNextChild() );

  }

  public void testRowShifting() {
    InitialPaginationShiftState init = new InitialPaginationShiftState();
    RowLevelPaginationShiftState stateR1 = new RowLevelPaginationShiftState();
    stateR1.reuse( null, init, null );
    stateR1.increaseShift( 10 );
    stateR1.pop( null );

    assertEquals( 10, init.getShiftForNextChild() );

    RowLevelPaginationShiftState stateR2 = new RowLevelPaginationShiftState();
    stateR2.reuse( null, init, null );
    stateR2.increaseShift( 10 );

    BlockLevelPaginationShiftState stateCC1 = new BlockLevelPaginationShiftState();
    stateCC1.reuse( null, stateR2, null );
    assertEquals( 20, stateCC1.getShiftForNextChild() );
    stateCC1.increaseShift( 3 );
    stateCC1.pop( null );

    assertEquals( 20, stateR2.getShiftForNextChild() );

    BlockLevelPaginationShiftState stateCC2 = new BlockLevelPaginationShiftState();
    stateCC2.reuse( null, stateR2, null );
    assertEquals( 20, stateCC2.getShiftForNextChild() );
    stateCC2.increaseShift( 7 );
    stateCC2.pop( null );

    assertEquals( 20, stateR2.getShiftForNextChild() );

    stateR2.pop( null );
    assertEquals( 27, init.getShiftForNextChild() );
  }
}
