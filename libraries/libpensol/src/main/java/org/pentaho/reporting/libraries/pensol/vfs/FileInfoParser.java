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


package org.pentaho.reporting.libraries.pensol.vfs;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FileInfoParser extends DefaultHandler {
  private FileInfo root;
  private FileInfo currentFileInfo;
  private String majorVersion;
  private String minorVersion;
  private String releaseVersion;
  private String buildVersion;
  private String milestoneVersion;

  public FileInfoParser() {
  }

  public String getMajorVersion() {
    return majorVersion;
  }

  public String getMinorVersion() {
    return minorVersion;
  }

  public String getReleaseVersion() {
    return releaseVersion;
  }

  public String getBuildVersion() {
    return buildVersion;
  }

  public String getMilestoneVersion() {
    return milestoneVersion;
  }

  /**
   * Receive notification of the beginning of an element. <p/> <p>The Parser will invoke this method at the beginning of
   * every element in the XML document; there will be a corresponding {@link #endElement endElement} event for every
   * startElement event (even when the element is empty). All of the element's content will be reported, in order,
   * before the corresponding endElement event.</p> <p/> <p>This event allows up to three name components for each
   * element:</p> <p/> <ol> <li>the Namespace URI;</li> <li>the local name; and</li> <li>the qualified (prefixed)
   * name.</li> </ol> <p/> <p>Any or all of these may be provided, depending on the values of the
   * <var>http://xml.org/sax/features/namespaces</var> and the <var>http://xml.org/sax/features/namespace-prefixes</var>
   * properties:</p> <p/> <ul> <li>the Namespace URI and local name are required when the namespaces property is
   * <var>true</var> (the default), and are optional when the namespaces property is <var>false</var> (if one is
   * specified, both must be);</li> <li>the qualified name is required when the namespace-prefixes property is
   * <var>true</var>, and is optional when the namespace-prefixes property is <var>false</var> (the default).</li> </ul>
   * <p/> <p>Note that the attribute list provided will contain only attributes with explicit values (specified or
   * defaulted): #IMPLIED attributes will be omitted.  The attribute list will contain attributes used for Namespace
   * declarations (xmlns* attributes) only if the <code>http://xml.org/sax/features/namespace-prefixes</code> property
   * is true (it is false by default, and support for a true value is optional).</p> <p/> <p>Like {@link #characters
   * characters()}, attribute values may have characters that need more than one <code>char</code> value.  </p>
   *
   * @param uri       the Namespace URI, or the empty string if the element has no Namespace URI or if Namespace
   *                  processing is not being performed
   * @param localName the local name (without prefix), or the empty string if Namespace processing is not being
   *                  performed
   * @param qName     the qualified name (with prefix), or the empty string if qualified names are not available
   * @param atts      the attributes attached to the element.  If there are no attributes, it shall be an empty
   *                  Attributes object.  The value of this object after startElement returns is undefined
   * @throws org.xml.sax.SAXException any SAX exception, possibly wrapping another exception
   * @see #endElement
   * @see org.xml.sax.Attributes
   * @see org.xml.sax.helpers.AttributesImpl
   */
  public void startElement( final String uri,
                            final String localName,
                            final String qName,
                            final Attributes atts ) throws SAXException {
    if ( "repository".equals( qName ) ) {
      root = new FileInfo();
      currentFileInfo = root;

      majorVersion = atts.getValue( "version-major" );
      minorVersion = atts.getValue( "version-minor" );
      releaseVersion = atts.getValue( "version-release" );
      buildVersion = atts.getValue( "version-build" );
      milestoneVersion = atts.getValue( "version-milestone" );
    } else if ( "file".equals( qName ) ) {
      currentFileInfo = new FileInfo( currentFileInfo, atts );
    }
  }

  /**
   * Receive notification of the end of an element. <p/> <p>The SAX parser will invoke this method at the end of every
   * element in the XML document; there will be a corresponding {@link #startElement startElement} event for every
   * endElement event (even when the element is empty).</p> <p/> <p>For information on the names, see startElement.</p>
   *
   * @param uri       the Namespace URI, or the empty string if the element has no Namespace URI or if Namespace
   *                  processing is not being performed
   * @param localName the local name (without prefix), or the empty string if Namespace processing is not being
   *                  performed
   * @param qName     the qualified XML name (with prefix), or the empty string if qualified names are not available
   * @throws org.xml.sax.SAXException any SAX exception, possibly wrapping another exception
   */
  public void endElement( final String uri, final String localName, final String qName ) throws SAXException {
    if ( "repository".equals( qName ) ) {
    } else if ( "file".equals( qName ) ) {
      currentFileInfo = currentFileInfo.getParent();
    }
  }

  public FileInfo getRoot() {
    return root;
  }
}
