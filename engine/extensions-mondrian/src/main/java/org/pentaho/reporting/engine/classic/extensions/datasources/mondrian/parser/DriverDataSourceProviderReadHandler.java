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


package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.PasswordPropertiesReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DataSourceProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DriverDataSourceProvider;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class DriverDataSourceProviderReadHandler extends AbstractXmlReadHandler
  implements DataSourceProviderReadHandler {
  private StringReadHandler driverReadHandler;
  private StringReadHandler urlReadHandler;
  private PasswordPropertiesReadHandler propertiesReadHandler;
  private DriverDataSourceProvider driverConnectionProvider;

  public DriverDataSourceProviderReadHandler() {
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( "driver".equals( tagName ) ) {
      driverReadHandler = new StringReadHandler();
      return driverReadHandler;
    }
    if ( "url".equals( tagName ) ) {
      urlReadHandler = new StringReadHandler();
      return urlReadHandler;
    }
    if ( "properties".equals( tagName ) ) {
      propertiesReadHandler = new PasswordPropertiesReadHandler();
      return propertiesReadHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final DriverDataSourceProvider provider = new DriverDataSourceProvider();
    if ( driverReadHandler != null ) {
      provider.setDriver( driverReadHandler.getResult() );
    }
    if ( urlReadHandler != null ) {
      provider.setUrl( urlReadHandler.getResult() );
    }
    if ( propertiesReadHandler != null ) {
      final Properties p = (Properties) propertiesReadHandler.getObject();
      final Iterator it = p.entrySet().iterator();
      while ( it.hasNext() ) {
        final Map.Entry entry = (Map.Entry) it.next();
        provider.setProperty( (String) entry.getKey(), (String) entry.getValue() );
      }
    }
    driverConnectionProvider = provider;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if there is a parsing error.
   */
  public Object getObject() throws SAXException {
    return driverConnectionProvider;
  }

  public DataSourceProvider getProvider() {
    return driverConnectionProvider;
  }
}
