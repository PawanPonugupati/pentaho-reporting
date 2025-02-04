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


package org.pentaho.reporting.designer.core.settings;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerInfo;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

public class SettingsUtil {
  private static Log LOG = LogFactory.getLog( SettingsUtil.class );

  private SettingsUtil() {
  }


  private static boolean isValidConfiguration( final File configDirectory ) {
    final File initMarkerFile = new File( configDirectory, ".init-config-marker" ); // NON-NLS
    if ( initMarkerFile.exists() == false ) {
      return false;
    }

    final ReportDesignerInfo reportDesignerInfo = ReportDesignerInfo.getInstance();
    final String currentVersion = reportDesignerInfo.getVersion();
    if ( StringUtils.isEmpty( currentVersion ) || currentVersion.startsWith( "TRUNK-SNAPSHOT" ) ) // NON-NLS
    {
      return true;
    }
    final Integer[] currentVersionArray = ObjectUtilities.parseVersions( currentVersion );

    try {
      final byte[] buffer = new byte[ 500 ];
      final FileInputStream fin = new FileInputStream( initMarkerFile );
      try {
        final int length = IOUtils.getInstance().readSafely( fin, buffer, 0, 500 );
        final byte[] data = new byte[ length ];
        System.arraycopy( buffer, 0, data, 0, length );
        final String versionString = new String( data, "ISO-8859-1" );
        if ( StringUtils.isEmpty( versionString ) ) {
          return true;
        }

        final Integer[] versionArray = ObjectUtilities.parseVersions( versionString );
        if ( ObjectUtilities.compareVersionArrays( versionArray, currentVersionArray ) >= 0 ) {
          return true;
        }
      } finally {
        fin.close();
      }
    } catch ( IOException e ) {
      return false;
    }
    return false;
  }

  private static void writeVersionTag( final File configDirectory ) throws IOException {
    final File initMarkerFile = new File( configDirectory, ".init-config-marker" ); // NON-NLS
    final FileOutputStream fout = new FileOutputStream( initMarkerFile );
    final ReportDesignerInfo reportDesignerInfo = ReportDesignerInfo.getInstance();
    final String currentVersion = reportDesignerInfo.getVersion();
    if ( StringUtils.isEmpty( currentVersion ) == false
      && currentVersion.startsWith( "TRUNK-SNAPSHOT" ) == false ) // NON-NLS
    {
      try {
        fout.write( currentVersion.getBytes( "ISO-8859-1" ) );
      } finally {
        fout.close();
      }
    } else {
      fout.close();
    }
  }

  public static void createInitialConfiguration() {
    try {
      final Configuration theConfiguration = ReportDesignerBoot.getInstance().getGlobalConfig();
      final String homeDirectory = theConfiguration.getConfigProperty( "user.home" ); // NON-NLS
      final File configDirectory = new File( homeDirectory + File.separatorChar + ".pentaho" ); // NON-NLS

      if ( configDirectory.exists() == false ) {
        if ( configDirectory.mkdir() == false ) {
          return;
        }
      }
      if ( isValidConfiguration( configDirectory ) ) {
        return;
      }

      final File installDirFile = computeInstallationDirectory();
      if ( installDirFile != null ) {
        final File configTemplateDir = new File( installDirFile, "configuration-template" ); // NON-NLS
        if ( configTemplateDir.exists() == false ) {
          return;
        }

        final File configTemplateJndi = new File( configTemplateDir, "simple-jndi" );
        final File configDirJndi = new File( configDirectory, "simple-jndi" );
        if ( configDirJndi.exists() == false ) {
          FileUtils.copyDirectory( configTemplateJndi, configDirJndi );
        }

        final File configTemplateConf = new File( configTemplateDir, "report-designer" );
        final File configDirConf = new File( configDirectory, "report-designer" );
        if ( configTemplateConf.exists() && configTemplateConf.isDirectory() ) {
          FileUtils.copyDirectory( configTemplateConf, configDirConf );
        }
        writeVersionTag( configDirectory );
      }
    } catch ( IOException e ) {
      LOG.debug( "createInitialConfiguration: IO Error", e ); // NON-NLS
    }
  }

  public static File computeInstallationDirectory() throws IOException {
    final URL location = WorkspaceSettings.class.getProtectionDomain().getCodeSource().getLocation();
    LOG.debug( "InstallationDirectory: Protection-Domain: " + location ); // NON-NLS
    if ( location == null ) {
      return null;
    }
    if ( "file".equals( location.getProtocol() ) == false ) {
      LOG.debug( "InstallationDirectory: Protection-Domain: Protocol failure." ); // NON-NLS
      return null;
    }
    try {
      File jarPositon = new File( location.getFile() );
      if ( jarPositon.isFile() == false ) {
        final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
        final String file = URLDecoder.decode( location.getFile(),
          configuration.getConfigProperty( "file.encoding", "ISO-8859-1" ) ); // NON-NLS
        jarPositon = new File( file );
      }
      LOG.debug( "InstallationDirectory: JAR file: " + jarPositon ); // NON-NLS
      if ( jarPositon.isFile() ) {
        // secret knowledge here: We know all jars are in the lib-directory.
        final File libDirectory = jarPositon.getCanonicalFile().getParentFile();
        if ( libDirectory == null ) {
          LOG.debug( "InstallationDirectory: No lib directory." ); // NON-NLS
          return null;
        }
        LOG.debug( "InstallationDirectory: Work directory: " + libDirectory.getParentFile() ); // NON-NLS
        return libDirectory.getParentFile();
      }
    } catch ( IOException ioe ) {
      // ignore, but log.
      LOG.debug( "InstallationDirectory: Failed to decode URL: ", ioe ); // NON-NLS
    }

    // a directory, so we are running in an IDE.
    // hope for the best by using the current working directory.
    LOG.debug( "InstallationDirectory: Work directory: Defaulting to current work directory." ); // NON-NLS
    return new File( "." );
  }
}
