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


package org.pentaho.reporting.engine.classic.extensions.legacy.charts.writer;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.AbstractElementWriteHandler;
import org.pentaho.reporting.engine.classic.extensions.legacy.charts.LegacyChartElementModule;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

@Deprecated
public class LegacyChartWriteHandler extends AbstractElementWriteHandler {
  public LegacyChartWriteHandler() {
  }

  /**
   * Writes a single element as XML structure.
   *
   * @param bundle    the bundle to which to write to.
   * @param state     the current write-state.
   * @param xmlWriter the xml writer.
   * @param element   the element.
   * @throws IOException           if an IO error occured.
   * @throws BundleWriterException if an Bundle writer.
   */
  public void writeElement( final WriteableDocumentBundle bundle,
                            final BundleWriterState state,
                            final XmlWriter xmlWriter,
                            final Element element )
    throws IOException, BundleWriterException {
    final AttributeList attList = createMainAttributes( element, xmlWriter );
    if ( xmlWriter.isNamespaceDefined( LegacyChartElementModule.NAMESPACE ) == false ) {
      attList.addNamespaceDeclaration( "legacy-charts", LegacyChartElementModule.NAMESPACE );
    }
    xmlWriter.writeTag( LegacyChartElementModule.NAMESPACE, "legacy-chart", attList, XmlWriter.OPEN );
    writeElementBody( bundle, state, element, xmlWriter );
    xmlWriter.writeCloseTag();
  }

}
