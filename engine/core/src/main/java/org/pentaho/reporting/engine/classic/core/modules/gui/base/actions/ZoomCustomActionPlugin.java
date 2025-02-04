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


package org.pentaho.reporting.engine.classic.core.modules.gui.base.actions;

import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.SwingPreviewModule;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.internal.NumericInputDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.AbstractActionPlugin;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

/**
 * Creation-Date: 16.11.2006, 18:52:30
 *
 * @author Thomas Morgner
 */
public class ZoomCustomActionPlugin extends AbstractActionPlugin implements ControlActionPlugin {
  private ResourceBundleSupport resources;

  private static final Log logger = LogFactory.getLog( ZoomCustomActionPlugin.class );
  private PaginatedUpdateListener updateListener;

  public ZoomCustomActionPlugin() {
    updateListener = new PaginatedUpdateListener( this );
  }

  public void deinitialize( final SwingGuiContext swingGuiContext ) {
    super.deinitialize( swingGuiContext );
    swingGuiContext.getEventSource().removePropertyChangeListener( updateListener );
  }

  public boolean initialize( final SwingGuiContext context ) {
    super.initialize( context );
    resources =
        new ResourceBundleSupport( context.getLocale(), SwingPreviewModule.BUNDLE_NAME, ObjectUtilities
            .getClassLoader( SwingPreviewModule.class ) );
    context.getEventSource().addPropertyChangeListener( updateListener );
    setEnabled( context.getEventSource().isPaginated() );
    return true;
  }

  protected String getConfigurationPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.base.zoom-custom."; //$NON-NLS-1$
  }

  /**
   * Returns the display name for the export action.
   *
   * @return The display name.
   */
  public String getDisplayName() {
    return resources.getString( "action.zoomCustom.name" ); //$NON-NLS-1$
  }

  /**
   * Returns the short description for the export action.
   *
   * @return The short description.
   */
  public String getShortDescription() {
    return resources.getString( "action.zoomCustom.description" ); //$NON-NLS-1$
  }

  /**
   * Returns the small icon for the export action.
   *
   * @return The icon.
   */
  public Icon getSmallIcon() {
    final Locale locale = getContext().getLocale();
    return getIconTheme().getSmallIcon( locale, "action.zoomCustom.small-icon" ); //$NON-NLS-1$
  }

  /**
   * Returns the large icon for the export action.
   *
   * @return The icon.
   */
  public Icon getLargeIcon() {
    final Locale locale = getContext().getLocale();
    return getIconTheme().getLargeIcon( locale, "action.zoomCustom.icon" ); //$NON-NLS-1$
  }

  /**
   * Returns the accelerator key for the export action.
   *
   * @return The accelerator key.
   */
  public KeyStroke getAcceleratorKey() {
    return resources.getOptionalKeyStroke( "action.zoomCustom.accelerator" ); //$NON-NLS-1$
  }

  /**
   * Returns the mnemonic key code.
   *
   * @return The code.
   */
  public Integer getMnemonicKey() {
    return resources.getOptionalMnemonic( "action.zoomCustom.mnemonic" ); //$NON-NLS-1$
  }

  public boolean configure( final PreviewPane reportPane ) {
    final Integer result =
        NumericInputDialog.showInputDialog( getContext().getWindow(), JOptionPane.QUESTION_MESSAGE, resources
            .getString( "dialog.zoom.title" ), //$NON-NLS-1$
            resources.getString( "dialog.zoom.message" ), //$NON-NLS-1$
            25, 400, (int) ( reportPane.getZoom() * 100 ), false );

    if ( result == null ) {
      return false;
    }
    try {
      final double zoom = result.doubleValue();
      reportPane.setZoom( zoom / 100.0 );
      return true;
    } catch ( Exception ex ) {
      ZoomCustomActionPlugin.logger.info( resources.getString( "ZoomCustomActionPlugin.INFO_EXCEPTION_SWALLOWED" ) ); //$NON-NLS-1$
    }
    return false;
  }

}
