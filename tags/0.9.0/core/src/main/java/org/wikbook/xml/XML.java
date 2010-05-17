/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wikbook.xml;

import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class XML
{

   public static String serialize(Document document) throws TransformerException
   {
      Transformer transformer = createTransformer(new OutputFormat(2, true));
      StringWriter writer = new StringWriter();
      StreamResult result = new StreamResult(writer);
      transformer.transform(new DOMSource(document), result);
      return writer.toString();
   }

   public static TransformerHandler createTransformerHandler(
      OutputFormat doctype) throws TransformerConfigurationException
   {
      SAXTransformerFactory factory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();

      //
      if (doctype.getIndent() != null)
      {
         // This is proprietary, so it's a best effort
         factory.setAttribute("indent-number",  doctype.getIndent());
      }

      //
      TransformerHandler handler = factory.newTransformerHandler();
      Transformer transformer = handler.getTransformer();

      //
      if (doctype.getIndent() != null)
      {
         // This is proprietary, so it's a best effort
         transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(doctype.getIndent()));
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      }
      else
      {
         transformer.setOutputProperty(OutputKeys.INDENT, "no");
      }

      //
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");

      //
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

      //
      if (doctype.getPublicId() != null)
      {
         transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
      }

      //
      if (doctype.getSystemId() != null)
      {
         transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
      }

      //
      handler.getTransformer().setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, doctype.isOmitDeclaration() ? "yes" : "no");

      //
      return handler;
   }

   public static Transformer createTransformer(OutputFormat format) throws TransformerConfigurationException
   {
      return createTransformerHandler(format).getTransformer();
   }

}