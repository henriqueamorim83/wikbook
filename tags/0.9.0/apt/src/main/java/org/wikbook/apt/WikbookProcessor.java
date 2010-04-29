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

package org.wikbook.apt;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import org.wikbook.apt.annotations.Documented;
import org.wikbook.apt.model.Catalog;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@SupportedSourceVersion(SourceVersion.RELEASE_5)
@SupportedAnnotationTypes({"org.wikbook.apt.annotations.Documented"})
public class WikbookProcessor extends AbstractProcessor
{

   /** . */
   private Trees trees;

   /** . */
   private ProcessingEnvironment pe;

   /** . */
   private int count;

   /** . */
   private Set<StringPair> processed;

   public void init(ProcessingEnvironment pe)
   {
      super.init(pe);

      //
      this.trees = Trees.instance(pe);
      this.pe = pe;
      this.processed = new HashSet<StringPair>();
   }

   @Override
   public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment)
   {
      if (count++ > 0)
      {
         return true;
      }

      //
      ArrayList<Element> originatingElts = new ArrayList<Element>();

      //
      CatalogBuilder builder = new CatalogBuilder();
      for (Element e : roundEnvironment.getElementsAnnotatedWith(Documented.class))
      {
         originatingElts.add(e);
         Documented documented = e.getAnnotation(Documented.class);
         TreePath tp = trees.getPath(e);
         CompilationUnitTree cut = tp.getCompilationUnit();
         JavaFileObject src = cut.getSourceFile();

         //
         String fragmentId = documented.id();
         if (fragmentId.length() == 0)
         {
            Element p = e;
            while (fragmentId.length() == 0)
            {
               switch (p.getKind())
               {
                  case CLASS:
                  case INTERFACE:
                  case ENUM:
                  case ANNOTATION_TYPE:
                     fragmentId = ((TypeElement)p).getQualifiedName().toString();
                     break;
                  default:
                     break;
               }
               p = p.getEnclosingElement();
            }
         }

         //
         String annotationId = documented.id();

         //
         StringPair a = new StringPair(src.getName(), annotationId);
         if (!processed.contains(a))
         {
            try
            {
               builder.build(annotationId, fragmentId, src);
            }
            catch (Exception ex)
            {
               ex.printStackTrace();
            }
            processed.add(a);
         }
      }

      // Serialize catalog
      ObjectOutputStream oos = null;
      try
      {
         Filer filer = processingEnv.getFiler();
         FileObject file = filer.createResource(StandardLocation.SOURCE_OUTPUT, "", "catalog.ser", originatingElts.toArray(new Element[originatingElts.size()]));
         oos = new ObjectOutputStream(file.openOutputStream());
         Catalog catalog = builder.getCatalog();
         oos.writeObject(catalog);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      finally
      {
         try
         {
            if (oos != null)
            {
               oos.close();
            }
         }
         catch (IOException ignore)
         {
         }
      }

      //
      return true;
   }
}