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
import org.wikbook.core.xml.XMLEmitter;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ExampleElement extends ContentElement
{

   /** . */
   private ElementContainer<ContentElement> content;

   /** . */
   private final String title;

   public ExampleElement(String title)
   {
      this.content = new ElementContainer<ContentElement>(ContentElement.class);
      this.title = title;
   }

   @Override
   public boolean append(DocbookElement elt)
   {
      return content.append(elt);
   }

   @Override
   public void writeTo(XMLEmitter xml)
   {
      if (title == null)
      {
         xml = xml.element("informalexample");
      }
      else
      {
         xml = xml.element("example");
         xml.element("title").content(title);
      }
      content.writeTo(xml);
   }
}