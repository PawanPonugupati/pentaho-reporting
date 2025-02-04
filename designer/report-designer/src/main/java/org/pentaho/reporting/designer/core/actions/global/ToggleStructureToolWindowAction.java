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


package org.pentaho.reporting.designer.core.actions.global;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerView;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.IconLoader;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Ezequiel Cuellar
 */
public final class ToggleStructureToolWindowAction extends AbstractToolWindowStateAction implements ToggleStateAction {
  public ToggleStructureToolWindowAction() {
    putValue( Action.NAME, ActionMessages.getString( "ToggleStructureToolWindowAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "ToggleStructureToolWindowAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "ToggleStructureToolWindowAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY,
      ActionMessages.getOptionalKeyStroke( "ToggleStructureToolWindowAction.Accelerator" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getGenericSquareDisabled() );
    putValue( Action.SELECTED_KEY, Boolean.FALSE );
  }

  public boolean isSelected() {
    return Boolean.TRUE.equals( getValue( Action.SELECTED_KEY ) );
  }

  public void setSelected( final boolean selected ) {
    putValue( Action.SELECTED_KEY, selected );
  }

  public void settingsChanged() {
    putValue( Action.SELECTED_KEY, WorkspaceSettings.getInstance().isShowGrid() );
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext reportDesignerContext1 = getReportDesignerContext();
    if ( reportDesignerContext1 == null ) {
      return;
    }
    reportDesignerContext1.getView().setStructureVisible(
      reportDesignerContext1.getView().isStructureVisible() == false );
  }

  protected String getPropertyName() {
    return ReportDesignerView.STRUCTURE_VISIBLE_PROPERTY;
  }

  protected boolean recomputeEnabled() {
    final ReportDesignerContext reportDesignerContext1 = getReportDesignerContext();
    if ( reportDesignerContext1 == null ) {
      return false;
    }
    setSelected( true );
    return true;
  }

}
