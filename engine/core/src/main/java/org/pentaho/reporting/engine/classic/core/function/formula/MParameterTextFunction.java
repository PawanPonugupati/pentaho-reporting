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


package org.pentaho.reporting.engine.classic.core.function.formula;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.libraries.base.util.URLEncoder;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeUtil;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

import java.io.UnsupportedEncodingException;

public class MParameterTextFunction implements Function {
  private static final Log logger = LogFactory.getLog( MParameterTextFunction.class );

  public MParameterTextFunction() {
  }

  public String getCanonicalName() {
    return "MPARAMETERTEXT";
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 2 || parameterCount > 4 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    Object rawValue = parameters.getValue( 0 );
    if ( rawValue instanceof Object[] == false ) {
      rawValue = TypeUtil.normalize( context.getTypeRegistry().convertToSequence( parameters.getType( 0 ), rawValue ) );
    }
    final String parameterName =
        context.getTypeRegistry().convertToText( parameters.getType( 1 ), parameters.getValue( 1 ) );

    final String encodingResult;
    final boolean urlEncode;
    if ( parameterCount > 2 ) {
      urlEncode =
          !( Boolean.FALSE.equals( context.getTypeRegistry().convertToLogical( parameters.getType( 2 ),
              parameters.getValue( 2 ) ) ) );

      if ( parameterCount == 4 ) {
        final Type encodingType = parameters.getType( 3 );
        final Object encodingValue = parameters.getValue( 3 );
        encodingResult = context.getTypeRegistry().convertToText( encodingType, encodingValue );
        if ( encodingResult == null ) {
          throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
        }
      } else {
        encodingResult =
            context.getConfiguration().getConfigProperty( "org.pentaho.reporting.libraries.formula.URLEncoding",
                "UTF-8" );
      }
    } else {
      urlEncode = true;
      encodingResult =
          context.getConfiguration().getConfigProperty( "org.pentaho.reporting.libraries.formula.URLEncoding", "UTF-8" );
    }

    try {
      final Object[] value = (Object[]) rawValue;
      final StringBuffer b = new StringBuffer( 1000 );
      for ( int i = 0; i < value.length; i++ ) {
        if ( i != 0 ) {
          b.append( "&" );
          b.append( URLEncoder.encode( parameterName, encodingResult ) );
          b.append( "=" );
        }

        final String s;
        try {
          final Object o = value[i];
          s = ConverterRegistry.toAttributeValue( o );
        } catch ( BeanException e ) {
          // ok, so what. Log and return error
          logger.warn( "MPARAMETERTEXT: Failed to convert value " + rawValue + " (" + rawValue.getClass() + ")", e );
          throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
        }

        if ( s == null ) {
          continue;
        }

        if ( urlEncode ) {
          b.append( URLEncoder.encode( s, encodingResult ) );
        } else {
          b.append( s );
        }
      }

      return new TypeValuePair( TextType.TYPE, b.toString() );

    } catch ( final UnsupportedEncodingException use ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }
  }

}
