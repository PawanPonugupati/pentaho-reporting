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


package org.pentaho.reporting.engine.classic.core.modules.parser.extwriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.ExtParserModule;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A parser configuration writer.
 *
 * @author Thomas Morgner
 */
public class ParserConfigWriter extends AbstractXMLDefinitionWriter {
  private static final Log logger = LogFactory.getLog( ParserConfigWriter.class );

  /**
   * The 'stylekey-factory' tag name.
   */
  public static final String STYLEKEY_FACTORY_TAG = "stylekey-factory";

  /**
   * The 'template-factory' tag name.
   */
  public static final String TEMPLATE_FACTORY_TAG = "template-factory";

  /**
   * The 'object-factory' tag name.
   */
  public static final String OBJECT_FACTORY_TAG = "object-factory";

  /**
   * The 'datadefinition-factory' tag name.
   */
  public static final String DATADEFINITION_FACTORY_TAG = "datadefinition-factory";

  /**
   * The 'datasource-factory' tag name.
   */
  public static final String DATASOURCE_FACTORY_TAG = "datasource-factory";

  /**
   * The 'element-factory' tag name.
   */
  public static final String ELEMENT_FACTORY_TAG = "element-factory";

  /**
   * Creates a new writer.
   *
   * @param reportWriter
   *          the report writer.
   */
  public ParserConfigWriter( final ReportWriterContext reportWriter, final XmlWriter xmlWriter ) {
    super( reportWriter, xmlWriter );
  }

  /**
   * Writes the XML.
   *
   * @throws java.io.IOException
   *           if there is an I/O problem.
   */
  public void write() throws IOException {
    final XmlWriter xmlWriter = getXmlWriter();
    xmlWriter
        .writeTag( ExtParserModule.NAMESPACE, AbstractXMLDefinitionWriter.PARSER_CONFIG_TAG, XmlWriterSupport.OPEN );

    writeFactory( ParserConfigWriter.OBJECT_FACTORY_TAG, filterFactories( getReportWriter().getClassFactoryCollector()
        .getFactories() ) );
    writeFactory( ParserConfigWriter.ELEMENT_FACTORY_TAG, filterFactories( getReportWriter()
        .getElementFactoryCollector().getFactories() ) );
    writeFactory( ParserConfigWriter.STYLEKEY_FACTORY_TAG, filterFactories( getReportWriter()
        .getStyleKeyFactoryCollector().getFactories() ) );
    writeFactory( ParserConfigWriter.TEMPLATE_FACTORY_TAG, filterFactories( getReportWriter().getTemplateCollector()
        .getFactories() ) );
    writeFactory( ParserConfigWriter.DATASOURCE_FACTORY_TAG, filterFactories( getReportWriter()
        .getDataSourceCollector().getFactories() ) );

    xmlWriter.writeCloseTag();
  }

  /**
   * Filters the given factories iterator and removes all duplicate entries.
   *
   * @param it
   *          the unfiltered factories iterator.
   * @return a cleaned version of the iterator.
   */
  private Iterator filterFactories( final Iterator it ) {
    final ReportWriterContext writer = getReportWriter();
    final ArrayList factories = new ArrayList();
    while ( it.hasNext() ) {
      final Object o = it.next();
      if ( o.equals( writer.getClassFactoryCollector() ) ) {
        continue;
      }
      if ( o.equals( writer.getDataSourceCollector() ) ) {
        continue;
      }
      if ( o.equals( writer.getElementFactoryCollector() ) ) {
        continue;
      }
      if ( o.equals( writer.getStyleKeyFactoryCollector() ) ) {
        continue;
      }
      if ( o.equals( writer.getTemplateCollector() ) ) {
        continue;
      }
      if ( factories.contains( o ) == false ) {
        factories.add( o );
      }
    }
    // sort them ?
    return factories.iterator();
  }

  /**
   * Writes a factory element.
   *
   * @param tagName
   *          the tag name.
   * @param it
   *          an iterator over a collection of factories, which should be defined for the target report.
   * @throws java.io.IOException
   *           if there is an I/O problem.
   */
  public void writeFactory( final String tagName, final Iterator it ) throws IOException {
    while ( it.hasNext() ) {
      final Object itObject = it.next();
      final Class itClass = itObject.getClass();
      if ( AbstractXMLDefinitionWriter.hasPublicDefaultConstructor( itClass ) == false ) {
        final StringBuffer message = new StringBuffer( 100 );
        message.append( "FactoryClass " );
        message.append( itObject.getClass() );
        message.append( " has no default constructor. This class will be ignored" );
        ParserConfigWriter.logger.warn( message.toString() );
        continue;
      }

      final String className = itObject.getClass().getName();
      getXmlWriter().writeTag( ExtParserModule.NAMESPACE, tagName, AbstractXMLDefinitionWriter.CLASS_ATTRIBUTE,
          className, XmlWriterSupport.CLOSE );
    }
  }

}
