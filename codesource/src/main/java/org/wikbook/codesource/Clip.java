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

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class Clip
{

   public static Clip get(Coordinate from, Coordinate to)
   {
      return new Clip(from, to);
   }

   public static Clip get(int fromLine, int fromColumn, int toLine, int toColumn)
   {
      return new Clip(Coordinate.get(fromLine, fromColumn), Coordinate.get(toLine, toColumn));
   }

   /** . */
   private final Coordinate from;

   /** . */
   private final Coordinate to;

   private Clip(Coordinate from, Coordinate to)
   {
      if (from == null)
      {
         throw new NullPointerException();
      }
      if (to == null)
      {
         throw new NullPointerException();
      }
      this.from = from;
      this.to = to;
   }

   public boolean contains(Coordinate position)
   {
      if (position == null)
      {
         throw new NullPointerException();
      }
      if (position.line < from.line || position.line > to.line)
      {
         return false;
      }
      else if (position.line == from.line)
      {
         return position.column >= from.column;
      }
      else if (position.line == to.line)
      {
         return position.column < to.column;
      }
      else
      {
         return true;
      }
   }

   public Coordinate getFrom()
   {
      return from;
   }

   public Coordinate getTo()
   {
      return to;
   }
}
