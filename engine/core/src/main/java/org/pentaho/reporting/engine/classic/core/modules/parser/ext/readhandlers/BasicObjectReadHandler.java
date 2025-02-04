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


package org.pentaho.reporting.engine.classic.core.modules.parser.ext.readhandlers;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.AbstractPropertyXmlReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.PropertyStringReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectDescription;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BasicObjectReadHandler extends AbstractPropertyXmlReadHandler {
  private ObjectDescription objectDescription;
  private PropertyStringReadHandler stringReadHandler;
  private ClassFactory classFactory;

  /**
   * @param objectDescription
   *          may be null.
   */
  public BasicObjectReadHandler( final ObjectDescription objectDescription ) {
    this.stringReadHandler = new PropertyStringReadHandler();
    this.objectDescription = objectDescription;
  }

  /**
   * Initialises the handler.
   *
   * @param rootHandler
   *          the root handler.
   * @param tagName
   *          the tag name.
   */
  public void init( final RootXmlReadHandler rootHandler, final String uri, final String tagName ) throws SAXException {
    super.init( rootHandler, uri, tagName );
    this.classFactory = (ClassFactory) getRootHandler().getHelperObject( ReportDefinitionReadHandler.CLASS_FACTORY_KEY );
  }

  protected ObjectDescription getObjectDescription() {
    return objectDescription;
  }

  protected void setObjectDescription( final ObjectDescription objectDescription ) {
    this.objectDescription = objectDescription;
  }

  protected ClassFactory getClassFactory() {
    return classFactory;
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes attrs ) throws SAXException {
    handleStartParsing( attrs );
    getRootHandler().delegate( stringReadHandler, getUri(), getTagName(), attrs );
  }

  protected void handleStartParsing( final Attributes attrs ) throws ParseException {
    final String name = attrs.getValue( getUri(), "name" );
    if ( name == null ) {
      throw new ParseException( "Required attribute 'name' is missing.", getLocator() );
    }

    final String attrClass = CompatibilityMapperUtil.mapClassName( attrs.getValue( getUri(), "class" ) );
    if ( attrClass != null ) {
      try {
        final ClassLoader loader = ObjectUtilities.getClassLoader( BasicObjectReadHandler.class );
        final Class clazz = Class.forName( attrClass, false, loader );
        objectDescription = ObjectFactoryUtility.findDescription( classFactory, clazz, getLocator() );
      } catch ( ClassNotFoundException e ) {
        throw new ParseException( "Value for given 'class' attribute is invalid", getLocator() );
      }
    }
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final String value = stringReadHandler.getResult();
    objectDescription.setParameter( "value", value );
    super.doneParsing();
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() {
    objectDescription.configure( getRootHandler().getParserConfiguration() );
    return objectDescription.createObject();
  }
}
