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


package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.bundle.BundledResourceEditor;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document me!
 * <p/>
 * Date: 18.08.2009 Time: 17:16:07
 *
 * @author Thomas Morgner.
 */
public class EditResourcesAction extends AbstractReportContextAction {
  /**
   * Defines an <code>Action</code> object with a default description string and default icon.
   */
  public EditResourcesAction() {
    putValue( Action.NAME, ActionMessages.getString( "EditResourcesAction.Text" ) );
    putValue( Action.DEFAULT, ActionMessages.getString( "EditResourcesAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "EditResourcesAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "EditResourcesAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    final ReportDesignerContext context = getReportDesignerContext();
    final Component parent = context.getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    final BundledResourceEditor dialog;
    if ( window instanceof JDialog ) {
      dialog = new BundledResourceEditor( (JDialog) window, getReportDesignerContext() );
    } else if ( window instanceof JFrame ) {
      dialog = new BundledResourceEditor( (JFrame) window, getReportDesignerContext() );
    } else {
      dialog = new BundledResourceEditor( getReportDesignerContext() );
    }


    if ( dialog.editResources() ) {
      final MasterReport masterReportElement = activeContext.getContextRoot();
      masterReportElement.fireModelLayoutChanged
        ( masterReportElement, ReportModelEvent.NODE_PROPERTIES_CHANGED, masterReportElement.getBundle() );
    }
  }
}
