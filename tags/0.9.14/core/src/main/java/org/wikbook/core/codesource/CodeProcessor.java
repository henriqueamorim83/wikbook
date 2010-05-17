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

package org.wikbook.core.codesource;

import org.wikbook.codesource.BodySource;
import org.wikbook.codesource.CodeSourceBuilder;
import org.wikbook.codesource.CodeSourceBuilderContext;
import org.wikbook.codesource.MethodSource;
import org.wikbook.codesource.TypeSource;
import org.wikbook.text.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class CodeProcessor
{
   /** . */
   public static final String WHITE_NON_CR = "[ \t\\x0B\f\r]";

   /** . */
   public static final Pattern CALLOUT_ANCHOR_PATTERN = Pattern.compile(
      "//" + WHITE_NON_CR + "*" + "<([0-9]+)>" + "(.*)$", Pattern.MULTILINE);

   /** . */
   public static final Pattern CALLOUT_DEF_PATTERN = Pattern.compile(
      "^" + WHITE_NON_CR + "*//" + WHITE_NON_CR + "*" + "=([0-9]+)=" + WHITE_NON_CR + "(\\S.*)$", Pattern.MULTILINE);

   /** . */
   public static final Pattern BEGIN_CHUNK_PATTERN = Pattern.compile(
      "^" + WHITE_NON_CR + "*//" + WHITE_NON_CR + "*" + "-([0-9]+)-" + WHITE_NON_CR + "*" + "$");

   /** . */
   public static final Pattern BLANK_LINE_PATTERN = Pattern.compile(
      "^" + WHITE_NON_CR + "*" + "$");

   /** . */
   public static final Pattern BILTO = Pattern.compile(
      "^(?:" +
         "(?:" + WHITE_NON_CR + "*//" + WHITE_NON_CR + "*" + "-([0-9]+)-" + WHITE_NON_CR + "*)" +
         "|" +
         "(?:" + WHITE_NON_CR +  "*)" +
      ")$", Pattern.MULTILINE);

   /** . */
   public static final Pattern JAVA_INCLUDE_PATTERN = Pattern.compile(
      "\\{" + "@(include|javadoc)" + "\\s+" + "([^\\s]+)" + "\\s*" + "(\\{[0-9]+(?:,[0-9]+)*\\})?" + "\\s*" + "\\}"
   );

   private void printJavaSource(String s, CodeContext ctx)
   {
      // Process all callout definitions
      Matcher coDefMatcher = CALLOUT_DEF_PATTERN.matcher(s);
      int pre = 0;
      StringBuilder buf = new StringBuilder();
      while (coDefMatcher.find())
      {
         String id = coDefMatcher.group(1);
         String text = coDefMatcher.group(2).trim();

         //
         buf.append(s, pre, coDefMatcher.start());

         //
         ctx.setCallout(id, text);

         //
         pre = coDefMatcher.end();
      }
      buf.append(s, pre, s.length());
      s = buf.toString();

      //
      TextArea ta = new TextArea(s);
      Matcher matcher = CALLOUT_ANCHOR_PATTERN.matcher(s);
      int prev = 0;
      while (matcher.find())
      {
         String id = matcher.group(1);

         //
         ctx.writeContent(ta.clip(ta.position(prev), ta.position(matcher.start())));

         ctx.writeCallout(id);

         // Determine if we have callout text associated
         String text = matcher.group(2);
         if (!text.matches("\\s*"))
         {
            ctx.setCallout(id, text.trim());
         }

         // Iterate to next
         prev = matcher.end();
      }
      ctx.writeContent(ta.clip(ta.position(prev)));
   }

   private void printJavaSource(
      MethodSource source,
      CodeContext ctx,
      Set<String> chunkIds)
   {
      final String s = source.getClip();

      // Find the method curly braces
      int a = s.indexOf('{');
      int b = s.lastIndexOf('}');

      // Split lines
      String[] lines = s.substring(a + 1, b).split("\\r?\\n");
      boolean matches = false;
      for (String line : lines)
      {
         if (BLANK_LINE_PATTERN.matcher(line).matches())
         {
            matches = false;
         }
         else
         {
            Matcher matcher = BEGIN_CHUNK_PATTERN.matcher(line);
            if (matcher.matches())
            {
               String chunkId = matcher.group(1);
               matches = chunkIds.contains(chunkId);
            }
            else
            {
               if (matches)
               {
                  printJavaSource(line + "\n", ctx);
               }
            }
         }
      }
   }

   public void parse(String s, final CodeContext ctx)
   {
      int prev = 0;
      Matcher matcher = JAVA_INCLUDE_PATTERN.matcher(s);
      while (matcher.find())
      {
         JavaCodeLink l = JavaCodeLink.parse(matcher.group(2));
         CodeSourceBuilder builder = new CodeSourceBuilder(new CodeSourceBuilderContext()
         {
            public InputStream getResource(String path)
            {
               try
               {
                  return ctx.resolveResources(path);
               }
               catch (IOException e)
               {
                  e.printStackTrace();
               }
               return null;
            }
         });
         TypeSource typeSource = builder.buildClass(l.getFQN());
         BodySource source;
         if (l.getMember() != null)
         {
            source = typeSource.findMember(l.getMember());
         }
         else
         {
            source = typeSource;
         }

         //
         printJavaSource(s.substring(prev, matcher.start()), ctx);

         //
         if ("include".equals(matcher.group(1)))
         {
            if (matcher.group(3) != null)
            {
               String subset = matcher.group(3);
               String a = subset.substring(1, subset.length() - 1);
               String[] ids = a.split(",");
               printJavaSource((MethodSource)source, ctx, new HashSet<String>(Arrays.asList(ids)));
            }
            else
            {
               printJavaSource(source.getClip(), ctx);
            }
         }
         else if ("javadoc".equals(matcher.group(1)) && source.getJavaDoc() != null)
         {
            String javadoc = source.getJavaDoc();
            ctx.writeContent(javadoc);
         }

         //
         prev = matcher.end();
      }

      //
      printJavaSource(s.substring(prev), ctx);
   }
}
