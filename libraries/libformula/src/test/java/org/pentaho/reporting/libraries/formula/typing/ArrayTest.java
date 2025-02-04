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


package org.pentaho.reporting.libraries.formula.typing;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaBoot;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.common.TestFormulaContext;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.parser.ParseException;

/**
 * @author Cedric Pronzato
 */
public class ArrayTest extends TestCase {
  private FormulaContext context;

  public ArrayTest() {
  }

  public ArrayTest( final String s ) {
    super( s );
  }

  public void setUp() {
    context = new TestFormulaContext( TestFormulaContext.testCaseDataset );
    LibFormulaBoot.getInstance().start();
  }

  public void testRowsInlineArrays() throws Exception {
    final Formula formula = new Formula( "{3|2|1}" );
    formula.initialize( context );
    final TypeValuePair evaluation = formula.evaluateTyped();
    assertNotNull( evaluation );
    assertTrue( evaluation.getType().isFlagSet( Type.ARRAY_TYPE ) );

    final ArrayCallback table = (ArrayCallback) evaluation.getValue();
    assertEquals( table.getColumnCount(), 1 );
    assertEquals( table.getRowCount(), 3 );
  }

  public void testColumnsInlineArrays() throws Exception {
    final Formula formula = new Formula( "{3;2;1}" );
    formula.initialize( context );
    final TypeValuePair evaluation = formula.evaluateTyped();
    assertNotNull( evaluation );
    assertTrue( evaluation.getType().isFlagSet( Type.ARRAY_TYPE ) );

    final ArrayCallback table = (ArrayCallback) evaluation.getValue();
    assertEquals( table.getColumnCount(), 3 );
    assertEquals( table.getRowCount(), 1 );
  }

  public void testInlineArrays() throws Exception {
    final Formula formula = new Formula( "{3;2;1|2;4;6}" );
    formula.initialize( context );

    final TypeValuePair evaluation = formula.evaluateTyped();
    assertNotNull( evaluation );
    assertTrue( evaluation.getType().isFlagSet( Type.ARRAY_TYPE ) );

    final ArrayCallback table = (ArrayCallback) evaluation.getValue();
    assertEquals( table.getColumnCount(), 3 );
    assertEquals( table.getRowCount(), 2 );
  }

  public void testInvalidInlineArrays() throws Exception {
    final Formula formula = new Formula( "{3;2;1|2;6}" );
    formula.initialize( context );
    final Object evaluate = formula.evaluate();
    assertEquals( evaluate, LibFormulaErrorValue.ERROR_ILLEGAL_ARRAY_VALUE );

  }

  public void testInvalidInlineArrays2() throws EvaluationException, ParseException {
    final Formula formula = new Formula( "{3;1|2;6;5;6}" );
    formula.initialize( context );
    final Object evaluate = formula.evaluate();
    assertEquals( evaluate, LibFormulaErrorValue.ERROR_ILLEGAL_ARRAY_VALUE );

  }

  public void testEmptyArray() throws EvaluationException, ParseException {
    final Formula formula = new Formula( "{}" );
    formula.initialize( context );
    final Object evaluate = formula.evaluate();
    assertTrue( evaluate instanceof StaticArrayCallback );
    StaticArrayCallback sc = (StaticArrayCallback) evaluate;
    assertEquals( 0, sc.getColumnCount() );
    assertEquals( 0, sc.getRowCount() );

  }
}
