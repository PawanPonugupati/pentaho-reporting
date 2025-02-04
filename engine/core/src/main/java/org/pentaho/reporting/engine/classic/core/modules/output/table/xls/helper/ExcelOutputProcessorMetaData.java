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


package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.MasterReportType;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfiguration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfigurationWrapper;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class ExcelOutputProcessorMetaData extends AbstractOutputProcessorMetaData {
  public static final int PAGINATION_NONE = 0;
  public static final int PAGINATION_MANUAL = 1;
  public static final int PAGINATION_FULL = 2;
  private int paginationMode;

  public ExcelOutputProcessorMetaData( final int paginationMode ) {
    this.paginationMode = paginationMode;
  }

  public void initialize( final Configuration configuration ) {
    super.initialize( configuration );
    final String localStrict =
        configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.StrictLayout" );
    if ( localStrict != null ) {
      if ( "true".equals( localStrict ) ) {
        addFeature( AbstractTableOutputProcessor.STRICT_LAYOUT );
      }
    } else {
      final String globalStrict =
          configuration
              .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.base.StrictLayout" );
      if ( "true".equals( globalStrict ) ) {
        addFeature( AbstractTableOutputProcessor.STRICT_LAYOUT );
      }
    }

    final String emulatePadding =
        configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.EmulateCellPadding" );
    if ( "true".equals( emulatePadding ) == false ) {
      addFeature( OutputProcessorFeature.DISABLE_PADDING );
    } else {
      addFeature( OutputProcessorFeature.EMULATE_PADDING );
    }

    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.base.UsePageBands" ) ) ) {
      addFeature( OutputProcessorFeature.PAGE_SECTIONS );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.UsePageBands" ) ) ) {
      addFeature( OutputProcessorFeature.PAGE_SECTIONS );
    }
    if ( "true"
        .equals( configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.base.TreatEllipseAsRectangle" ) ) ) {
      addFeature( AbstractTableOutputProcessor.TREAT_ELLIPSE_AS_RECTANGLE );
    }
    if ( "true"
        .equals( configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.TreatEllipseAsRectangle" ) ) ) {
      addFeature( AbstractTableOutputProcessor.TREAT_ELLIPSE_AS_RECTANGLE );
    }

    if ( paginationMode == ExcelOutputProcessorMetaData.PAGINATION_FULL ) {
      addFeature( OutputProcessorFeature.PAGEBREAKS );
    } else if ( paginationMode == ExcelOutputProcessorMetaData.PAGINATION_MANUAL ) {
      addFeature( OutputProcessorFeature.PAGEBREAKS );
      addFeature( OutputProcessorFeature.ITERATIVE_RENDERING );
      addFeature( OutputProcessorFeature.UNALIGNED_PAGEBANDS );
    } else {
      addFeature( OutputProcessorFeature.ITERATIVE_RENDERING );
      addFeature( OutputProcessorFeature.UNALIGNED_PAGEBANDS );
    }

    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.AssumeOverflowX" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_X );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.AssumeOverflowY" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_Y );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ShapeAsContent" ) ) ) {
      addFeature( AbstractTableOutputProcessor.SHAPES_CONTENT );
    }

    final ExtendedConfiguration extendedConfig = new ExtendedConfigurationWrapper( configuration );
    final double deviceResolution =
        extendedConfig.getIntProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.DeviceResolution", 0 );
    if ( deviceResolution > 0 ) {
      setNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION, deviceResolution );
    }

    final double sheetRowLimit =
        extendedConfig.getIntProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.SheetRowLimit", 1048576 );
    if ( getNumericFeatureValue( OutputProcessorFeature.SHEET_ROW_LIMIT ) <= 0 && sheetRowLimit > 0 ) {
      setNumericFeatureValue( OutputProcessorFeature.SHEET_ROW_LIMIT, sheetRowLimit );
    }
  }

  /**
   * The export descriptor is a string that describes the output characteristics. For libLayout outputs, it should start
   * with the output class (one of 'pageable', 'flow' or 'stream'), followed by '/liblayout/' and finally followed by
   * the output type (ie. PDF, Print, etc).
   *
   * @return the export descriptor.
   */
  public String getExportDescriptor() {
    return "table/excel";
  }

  /**
   * Checks whether this element provides some extra content that is not part of the visible layout structure. This can
   * be embedded scripts, anchors etc.
   *
   * @param style
   * @param attributes
   * @return
   */
  public boolean isExtraContentElement( final StyleSheet style, final ReportAttributeMap attributes ) {
    if ( isFeatureSupported( OutputProcessorFeature.DETECT_EXTRA_CONTENT ) == false ) {
      return false;
    }
    final Object o = attributes.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.ELEMENT_TYPE );
    if ( o instanceof MasterReportType ) {
      return false;
    }
    if ( super.isExtraContentElement( style, attributes ) ) {
      return true;
    }

    if ( StringUtils.isEmpty( (String) attributes.getAttribute( AttributeNames.Excel.NAMESPACE,
        AttributeNames.Excel.FIELD_FORMULA ) ) == false ) {
      return true;
    }
    return false;
  }

  @Override
  protected boolean getAutoCorrectFontMetrics() {
    return true;
  }
}
