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


package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.operators.EqualOperator;
import org.pentaho.reporting.libraries.formula.typing.Sequence;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;
import org.pentaho.reporting.libraries.formula.typing.sequence.RecursiveSequence;

import java.util.ArrayList;

public class ArrayContainsFunction implements Function {
  public ArrayContainsFunction() {
  }

  public String getCanonicalName() {
    return "ARRAYCONTAINS";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final ArrayList<TypeValuePair> needles = new ArrayList<TypeValuePair>( 512 );
    final int parameterCount = parameters.getParameterCount();

    if ( parameterCount == 0 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    for ( int paramIdx = 1; paramIdx < parameterCount; paramIdx++ ) {
      try {
        final Object value = parameters.getValue( paramIdx );
        final Sequence sequence = new RecursiveSequence( value, context );

        while ( sequence.hasNext() ) {
          final Object o = sequence.next();
          needles.add( new TypeValuePair( AnyType.TYPE, o ) );
        }
      } catch ( EvaluationException e ) {
        if ( e.getErrorValue() == LibFormulaErrorValue.ERROR_NA_VALUE ) {
          needles.add( new TypeValuePair( AnyType.TYPE, null ) );
        } else {
          throw e;
        }
      }
    }

    if ( needles.size() == 0 ) {
      return new TypeValuePair( LogicalType.TYPE, Boolean.TRUE );
    }


    final Object value = parameters.getValue( 0 );
    final Sequence sequence = new RecursiveSequence( value, context );
    final EqualOperator equalOperator = new EqualOperator();

    while ( sequence.hasNext() ) {
      final Object o = sequence.next();
      final TypeValuePair sequenceValue = new TypeValuePair( AnyType.TYPE, o );
      for ( int i = needles.size() - 1; i >= 0; i -= 1 ) {
        final TypeValuePair needle = needles.get( i );
        if ( needle.getValue() == o ) {
          needles.remove( i );
        } else if ( o != null ) {
          final TypeValuePair evaluate = equalOperator.evaluate( context, sequenceValue, needle );
          if ( Boolean.TRUE.equals( evaluate.getValue() ) ) {
            needles.remove( i );
          }
        }
      }
    }

    return new TypeValuePair( LogicalType.TYPE, needles.isEmpty() );
  }
}
