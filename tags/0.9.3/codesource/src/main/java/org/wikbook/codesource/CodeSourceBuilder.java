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

package org.wikbook.codesource;

import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class CodeSourceBuilder 
{
   public TypeSource buildClass(String fqn) throws CodeSourceException
   {
      Collection<TypeSource> types = buildCompilationUnit(fqn.replace(".", "/") + ".java");
      for (TypeSource type : types)
      {
         if (type.getName().equals(fqn))
         {
            return type;
         }
      }
      return null;
   }

   public Collection<TypeSource> buildCompilationUnit(String compilationUnitPath) throws CodeSourceException
   {
      if (compilationUnitPath == null)
      {
         throw new NullPointerException();
      }
      try
      {
         CompilationUnitVisitor visitor = new CompilationUnitVisitor();
         CompilationUnitVisitor.Visit visit = visitor.visit(compilationUnitPath);
         return visit.types;
      }
      catch (Exception e)
      {
         throw new CodeSourceException(e);
      }
   }
}
