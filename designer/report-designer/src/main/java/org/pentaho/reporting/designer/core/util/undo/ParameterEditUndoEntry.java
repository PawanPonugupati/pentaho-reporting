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


package org.pentaho.reporting.designer.core.util.undo;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.parameters.ModifiableReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;

/**
 * Handles insert, remove and replacement of elements. Insert: old is null, remove: new is null.
 *
 * @author Thomas Morgner
 */
public class ParameterEditUndoEntry implements UndoEntry {
  private int position;
  private ParameterDefinitionEntry oldElement;
  private ParameterDefinitionEntry newElement;

  public ParameterEditUndoEntry( final int position,
                                 final ParameterDefinitionEntry oldElement,
                                 final ParameterDefinitionEntry newElement ) {
    this.position = position;
    this.oldElement = oldElement;
    this.newElement = newElement;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final AbstractReportDefinition abstractReportDefinition = renderContext.getReportDefinition();
    if ( abstractReportDefinition instanceof MasterReport == false ) {
      return;
    }
    final MasterReport report = (MasterReport) abstractReportDefinition;
    final ReportParameterDefinition definition = report.getParameterDefinition();
    if ( definition instanceof ModifiableReportParameterDefinition == false ) {
      return;
    }

    final ModifiableReportParameterDefinition mdef = (ModifiableReportParameterDefinition) definition;
    if ( newElement != null ) {
      mdef.removeParameterDefinition( position );
      clearParameterValues( report );
      report.notifyNodeChildRemoved( newElement );
    }
    if ( oldElement != null ) {
      mdef.addParameterDefinition( position, oldElement );
      clearParameterValues( report );
      report.notifyNodeChildAdded( oldElement );
    }
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final AbstractReportDefinition abstractReportDefinition = renderContext.getReportDefinition();
    if ( abstractReportDefinition instanceof MasterReport == false ) {
      return;
    }
    final MasterReport report = (MasterReport) abstractReportDefinition;
    final ReportParameterDefinition definition = report.getParameterDefinition();
    if ( definition instanceof ModifiableReportParameterDefinition == false ) {
      return;
    }

    final ModifiableReportParameterDefinition mdef = (ModifiableReportParameterDefinition) definition;
    if ( oldElement != null ) {
      mdef.removeParameterDefinition( position );
      clearParameterValues( report );
      report.notifyNodeChildRemoved( oldElement );
    }
    if ( newElement != null ) {
      mdef.addParameterDefinition( position, newElement );
      clearParameterValues( report );
      report.notifyNodeChildAdded( newElement );
    }
  }

  private void clearParameterValues( final MasterReport report ) {
    final String[] columnNames = report.getParameterValues().getColumnNames();
    for ( int i = 0; i < columnNames.length; i++ ) {
      final String columnName = columnNames[ i ];
      report.getParameterValues().put( columnName, null );
    }
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    return null;
  }
}
