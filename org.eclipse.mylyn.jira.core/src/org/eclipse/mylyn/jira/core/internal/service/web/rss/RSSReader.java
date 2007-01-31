/*******************************************************************************
 * Copyright (c) 2005 Jira Dashboard project.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Brock Janiczak - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.jira.core.internal.service.web.rss;

import java.io.InputStream;

import javax.xml.parsers.SAXParserFactory;

import org.eclipse.mylar.jira.core.internal.model.filter.IssueCollector;
import org.eclipse.mylar.jira.core.internal.service.JiraServer;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

class RSSReader {
	private final JiraServer server;

	private final IssueCollector collector;

	public RSSReader(JiraServer server, IssueCollector collector) {
		this.server = server;
		this.collector = collector;
	}

	public void readRssFeed(InputStream feed) {
		try {
			// TODO this only seems to work in J2SE 5.0
			// XMLReader reader = XMLReaderFactory.createXMLReader();

			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XMLReader reader = factory.newSAXParser().getXMLReader();
			reader.setContentHandler(new RSSContentHandler(server, collector));
			reader.parse(new InputSource(feed));
		} catch (ParseCancelledException e) {
			// User requested this action, so don't log anything
			collector.done();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			collector.done();
		}
	}
}