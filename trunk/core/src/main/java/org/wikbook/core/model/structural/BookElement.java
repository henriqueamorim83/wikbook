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

package org.wikbook.core.model.structural;

import org.w3c.dom.DocumentFragment;
import org.wikbook.core.model.DocbookElement;
import org.wikbook.core.model.ElementContainer;
import org.wikbook.core.model.content.ContentElementContainer;
import org.wikbook.core.model.content.ContentElement;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BookElement extends StructuralElement
{

   /** . */
   private final ElementContainer<ComponentElement> chapters = new ElementContainer<ComponentElement>(ComponentElement.class);

   /** . */
   private ContentElementContainer preface = new ContentElementContainer();

   /** . */
   private String prefaceTitle;

   /** . */
   private boolean omitRootNode;

   /** An XML fragment that is inserted before the body generated by this element. */
   private DocumentFragment beforeBodyXML;

   /** An XML fragment that is inserted after the body generated by this element. */
   private DocumentFragment afterBodyXML;

   public BookElement()
   {
      this.omitRootNode = false;
      this.prefaceTitle = "Preface";
   }

   public ElementContainer<ComponentElement> getChapters()
   {
      return chapters;
   }

   public ContentElementContainer getPreface()
   {
      return preface;
   }

   public String getPrefaceTitle()
   {
      return prefaceTitle;
   }

   public void setPrefaceTitle(String prefaceTitle)
   {
      this.prefaceTitle = prefaceTitle;
   }

   public boolean getOmitRootNode()
   {
      return omitRootNode;
   }

   public void setOmitRootNode(boolean omitRootNode)
   {
      this.omitRootNode = omitRootNode;
   }

   public DocumentFragment getBeforeBodyXML()
   {
      return beforeBodyXML;
   }

   public void setBeforeBodyXML(DocumentFragment beforeBodyXML)
   {
      this.beforeBodyXML = beforeBodyXML;
   }

   public DocumentFragment getAfterBodyXML()
   {
      return afterBodyXML;
   }

   public void setAfterBodyXML(DocumentFragment afterBodyXML)
   {
      this.afterBodyXML = afterBodyXML;
   }

   @Override
   public boolean append(DocbookElement elt)
   {
      if (elt instanceof ContentElement)
      {
         return preface.append(elt);
      }
      else if (elt instanceof ComponentElement)
      {
         return chapters.append(elt);
      }
      return false;
   }
}
