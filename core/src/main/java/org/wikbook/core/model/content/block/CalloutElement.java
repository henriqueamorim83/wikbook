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

package org.wikbook.core.model.content.block;

import org.wikbook.core.model.DocbookElement;
import org.wikbook.core.model.ElementContainer;
import org.wikbook.core.model.content.ContentElement;
import org.wikbook.core.xml.ElementEmitter;
import org.wikbook.core.xml.XMLEmitter;
import org.wikbook.text.Position;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class CalloutElement extends ContentElement
{

   /** . */
   private final ElementContainer<ContentElement> content;

   /** . */
   private LinkedHashMap<String, Position> ids;

   /** . */
   private final String text;

   public CalloutElement(LinkedHashMap<String, Position> ids, String text)
   {
      this.content = new ElementContainer<ContentElement>(ContentElement.class);
      this.ids = ids;
      this.text = text;
   }

   public LinkedHashMap<String, Position> getIds()
   {
      return ids;
   }

   @Override
   public boolean append(DocbookElement elt)
   {
      return content.append(elt);
   }

   @Override
   public void writeTo(XMLEmitter xml)
   {
      ElementEmitter calloutXML = xml.element("callout");

      //
      StringBuffer sb = new StringBuffer();
      for (String coId : ids.keySet())
      {
         if (sb.length() > 0)
         {
            sb.append(" ");
         }
         sb.append(coId).append("-co");
      }

      //
      calloutXML.withAttribute("arearefs", sb.toString());

      //
      calloutXML.element("para").content(text, true);
//      content.writeTo(xml);
   }
}
