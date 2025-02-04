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


package org.pentaho.reporting.designer.core.inspections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;

import javax.swing.event.EventListenerList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class AutoInspectionRunner implements InspectionResultListener, InspectionRunner {
  private static final Log logger = LogFactory.getLog( AutoInspectionRunner.class );

  private static class InspectionTask extends TimerTask {
    private Runnable runnable;

    private InspectionTask( final Runnable runnable ) {
      this.runnable = runnable;
    }

    public void run() {
      final Runnable r = this.runnable;
      if ( r == null ) {
        return;
      }
      synchronized( this ) {
        this.runnable = null;
      }

      try {
        r.run();
      } catch ( Throwable t ) {
        UncaughtExceptionsModel.getInstance().addException( t );
      }
    }

    public synchronized boolean cancel() {
      runnable = null;
      return super.cancel();
    }
  }

  private class InspectionRunnable implements Runnable {
    private InspectionRunnable() {
    }

    public void run() {
      // this clears the table ..
      notifyInspectionStarted();
      logger.debug( "Inspection started" );//NON-NLS
      final Inspection[] inspections = InspectionsRegistry.getInstance().getInspections();
      for ( int i = 0; i < inspections.length; i++ ) {
        final Inspection inspection = inspections[ i ];
        if ( inspection.isInlineInspection() ) {
          logger.debug( "Running " + inspection );//NON-NLS
          try {
            inspection.inspect( null, getReportRenderContext(), AutoInspectionRunner.this );
          } catch ( Exception e ) {
            UncaughtExceptionsModel.getInstance().addException( e );
          }
        }
      }
    }
  }

  private final Timer timer;
  private AutoInspectionRunner.InspectionTask lastTask;
  private ReportDocumentContext reportRenderContext;
  private EventListenerList eventListeners;

  public AutoInspectionRunner( final ReportDocumentContext reportRenderContext ) {
    if ( reportRenderContext == null ) {
      throw new NullPointerException();
    }
    this.reportRenderContext = reportRenderContext;
    this.timer = new Timer( "Inspection-Manager", true );//NON-NLS
    this.eventListeners = new EventListenerList();

    this.lastTask = new InspectionTask( new InspectionRunnable() );
    this.timer.schedule( lastTask, 1000 );
  }

  public void nodeChanged( final ReportModelEvent event ) {
    startTimer();
  }

  public void startTimer() {
    synchronized( timer ) {
      if ( lastTask != null ) {
        lastTask.cancel();
      }

      lastTask = new InspectionTask( new InspectionRunnable() );
      this.timer.schedule( lastTask, 1000 );
    }
  }

  public void dispose() {
    this.timer.cancel();
  }

  public void notifyInspectionResult( final InspectionResult result ) {
    final InspectionResultListener[] resultListeners =
      eventListeners.getListeners( InspectionResultListener.class );
    for ( int i = 0; i < resultListeners.length; i++ ) {
      final InspectionResultListener listener = resultListeners[ i ];
      listener.notifyInspectionResult( result );
    }
  }

  public void notifyInspectionStarted() {
    final InspectionResultListener[] resultListeners =
      eventListeners.getListeners( InspectionResultListener.class );
    for ( int i = 0; i < resultListeners.length; i++ ) {
      final InspectionResultListener listener = resultListeners[ i ];
      listener.notifyInspectionStarted();
    }
  }

  protected ReportDocumentContext getReportRenderContext() {
    return reportRenderContext;
  }

  public void addInspectionListener( final InspectionResultListener listener ) {
    this.eventListeners.add( InspectionResultListener.class, listener );
  }

  public void removeInspectionListener( final InspectionResultListener listener ) {
    this.eventListeners.remove( InspectionResultListener.class, listener );
  }
}
