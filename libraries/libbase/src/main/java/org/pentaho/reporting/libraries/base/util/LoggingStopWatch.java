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


package org.pentaho.reporting.libraries.base.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoggingStopWatch extends StopWatch implements PerformanceLoggingStopWatch {
  private transient Log logger;
  private Object message;
  private String tag;
  private long loggingThreshold;
  private long firstStartTime;
  private long restartCount;

  public LoggingStopWatch( final String tag ) {
    ArgumentNullException.validate( "tag", tag );

    this.tag = tag;
  }

  public LoggingStopWatch( final String tag, final Object message ) {
    this( tag );
    this.message = message;
  }

  public static PerformanceLoggingStopWatch startNew( final String tag, final Object message ) {
    PerformanceLoggingStopWatch loggingStopWatch = new LoggingStopWatch( tag, message );
    loggingStopWatch.start();
    return loggingStopWatch;
  }

  public static PerformanceLoggingStopWatch startNew( final String tag, final String pattern,
                                                      final Object... message ) {
    return startNew( tag, new FormattedMessage( pattern, message ) );
  }

  public static PerformanceLoggingStopWatch startNew( final String tag ) {
    return startNew( tag, null );
  }

  public long getLoggingThreshold() {
    return loggingThreshold;
  }

  public void setLoggingThreshold( final long loggingThreshold ) {
    this.loggingThreshold = loggingThreshold;
  }

  public String getTag() {
    return tag;
  }

  public Object getMessage() {
    return message;
  }

  public void setMessage( final Object message ) {
    this.message = message;
  }

  public void start() {
    if ( isStarted() ) {
      return;
    }

    super.start();
    if ( firstStartTime == 0 ) {
      firstStartTime = super.getStartTime();
    }
    restartCount += 1;
  }

  public void stop( boolean pause ) {
    super.stop();
    if ( pause ) {
      return;
    }

    if ( getElapsedMilliseconds() < loggingThreshold ) {
      return;
    }

    if ( firstStartTime == 0 ) {
      // this stopwatch was never started ..
      return;
    }

    String logMessage;
    if ( message == null ) {
      logMessage = String.format( "start[%d] time[%d] tag[%s] count[%d]", getStartTime(), getElapsedTime(), getTag(),
        getRestartCount() );
    } else {
      logMessage = String
        .format( "start[%d] time[%d] tag[%s] count[%d] message[%s]", getStartTime(), getElapsedTime(), getTag(),
          getRestartCount(), getMessage() );
    }
    doLog( logMessage );
    reset();
  }

  public long getRestartCount() {
    return restartCount;
  }

  public void reset() {
    super.reset();
    firstStartTime = 0;
  }

  public long getStartTime() {
    return firstStartTime;
  }

  public void stop() {
    stop( false );
  }

  protected void doLog( String message ) {
    if ( logger == null ) {
      // no need to syncronize, if logger is null in a race-condition, the logging system will sort it out.
      logger = LogFactory.getLog( LoggingStopWatch.class.getName() + "." + tag );
    }
    if ( logger.isInfoEnabled() ) {
      logger.debug( message );
    }
  }
}
