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


package org.pentaho.reporting.libraries.css.parser.stylehandler.line;

import org.pentaho.reporting.libraries.css.keys.line.DropInitialAfterAdjust;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 28.11.2005, 18:12:27
 *
 * @author Thomas Morgner
 */
public class DropInitialAfterAdjustReadHandler extends OneOfConstantsReadHandler {
  public DropInitialAfterAdjustReadHandler() {
    super( true );
    addValue( DropInitialAfterAdjust.AFTER_EDGE );
    addValue( DropInitialAfterAdjust.ALPHABETIC );
    addValue( DropInitialAfterAdjust.CENTRAL );
    addValue( DropInitialAfterAdjust.IDEOGRAPHIC );
    addValue( DropInitialAfterAdjust.MATHEMATICAL );
    addValue( DropInitialAfterAdjust.MIDDLE );
    addValue( DropInitialAfterAdjust.TEXT_AFTER_EDGE );
  }

  protected CSSValue lookupValue( final LexicalUnit value ) {
    CSSValue constant = super.lookupValue( value );
    if ( constant != null ) {
      return constant;
    } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_PERCENTAGE ) {
      return CSSNumericValue.createValue( CSSNumericType.PERCENTAGE,
        value.getFloatValue() );
    }

    return CSSValueFactory.createLengthValue( value );

  }
}
