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


package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public abstract class FormValidator {

  private class FormTextfieldListener implements DocumentListener, PropertyChangeListener {
    private FormTextfieldListener() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt
     *          A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( FormValidator.DOCUMENT_PROPERTY_NAME.equals( evt.getPropertyName() ) ) {
        final Document olddoc = (Document) evt.getOldValue();
        olddoc.removeDocumentListener( this );
        final Document newdoc = (Document) evt.getOldValue();
        newdoc.addDocumentListener( this );
      }
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e
     *          the document event
     */
    public void changedUpdate( final DocumentEvent e ) {
      handleValidate();
    }

    /**
     * Gives notification that there was an insert into the document. The range given by the DocumentEvent bounds the
     * freshly inserted region.
     *
     * @param e
     *          the document event
     */
    public void insertUpdate( final DocumentEvent e ) {
      handleValidate();
    }

    /**
     * Gives notification that a portion of the document has been removed. The range is given in terms of what the view
     * last saw (that is, before updating sticky positions).
     *
     * @param e
     *          the document event
     */
    public void removeUpdate( final DocumentEvent e ) {
      handleValidate();
    }
  }

  private class FormActionListener implements ActionListener {
    private FormActionListener() {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      handleValidate();
    }
  }

  private class FormItemListener implements ItemListener {
    private FormItemListener() {
    }

    /**
     * Invoked when an item has been selected or deselected by the user. The code written for this method performs the
     * operations that need to occur when an item is selected (or deselected).
     */
    public void itemStateChanged( final ItemEvent e ) {
      handleValidate();
    }
  }

  private FormTextfieldListener formTextfieldListener;
  private FormActionListener actionListener;
  private static final String DOCUMENT_PROPERTY_NAME = "document"; //$NON-NLS-1$
  private FormItemListener itemListener;
  private boolean enabled;

  protected FormValidator() {
    this.formTextfieldListener = new FormTextfieldListener();
    this.actionListener = new FormActionListener();
    this.itemListener = new FormItemListener();
  }

  public void registerTextField( final JTextComponent textField ) {
    textField.getDocument().addDocumentListener( formTextfieldListener );
    textField.addPropertyChangeListener( FormValidator.DOCUMENT_PROPERTY_NAME, formTextfieldListener );
  }

  public void registerButton( final AbstractButton bt ) {
    bt.addActionListener( actionListener );
  }

  public void registerComboBox( final JComboBox bt ) {
    bt.addItemListener( itemListener );
  }

  public abstract Action getConfirmAction();

  public final void handleValidate() {
    final Action confirmAction = getConfirmAction();
    if ( confirmAction == null || enabled == false ) {
      return;
    }

    if ( performValidate() == false ) {
      confirmAction.setEnabled( false );
    } else {
      confirmAction.setEnabled( true );
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled( final boolean enabled ) {
    this.enabled = enabled;
  }

  public abstract boolean performValidate();
}
