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


package org.pentaho.reporting.libraries.formula.function.datetime;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class WeekDayFunctionTest extends FormulaTestBase {
  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "WEEKDAY(DATE(2006;5;21))", new BigDecimal( 1 ) },
        { "WEEKDAY(DATE(2005;1;1))", new BigDecimal( 7 ) },
        { "WEEKDAY(DATE(2005;1;1);1)", new BigDecimal( 7 ) },
        { "WEEKDAY(DATE(2005;1;1);2)", new BigDecimal( 6 ) },
        { "WEEKDAY(DATE(2005;1;1);3)", new BigDecimal( 5 ) }, };
  }

  private Number[][] createTypeDataTest() {
    return new Number[][]
      {
        { new BigDecimal( 1 ), new BigDecimal( 7 ), new BigDecimal( 6 ) },
        { new BigDecimal( 2 ), new BigDecimal( 1 ), new BigDecimal( 0 ) },
        { new BigDecimal( 3 ), new BigDecimal( 2 ), new BigDecimal( 1 ) },
        { new BigDecimal( 4 ), new BigDecimal( 3 ), new BigDecimal( 2 ) },
        { new BigDecimal( 5 ), new BigDecimal( 4 ), new BigDecimal( 3 ) },
        { new BigDecimal( 6 ), new BigDecimal( 5 ), new BigDecimal( 4 ) },
        { new BigDecimal( 7 ), new BigDecimal( 6 ), new BigDecimal( 5 ) },
      };
  }

  public void testAllTypes() {
    final WeekDayFunction function = new WeekDayFunction();
    final Number[][] dataTest = createTypeDataTest();
    for ( int i = 0; i < dataTest.length; i++ ) {
      final Number[] objects = dataTest[ i ];
      final Number type1 = objects[ 0 ];
      final Number type2 = objects[ 1 ];
      final Number type3 = objects[ 2 ];
      assertEquals( "Error with Type 1 conversion", function.convertType( type1.intValue(), 1 ), type1.intValue() );
      assertEquals( "Error with Type 2 conversion", function.convertType( type1.intValue(), 2 ), type2.intValue() );
      assertEquals( "Error with Type 3 conversion", function.convertType( type1.intValue(), 3 ), type3.intValue() );
    }
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }


}
