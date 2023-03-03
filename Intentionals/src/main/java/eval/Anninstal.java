package eval;

import java.lang.reflect.*;
//import java.lang.reflect.Field;

/**
 * class Anninstal - used to process Intended annotations
 *
 * @author Raf Marcoen/Tim Hermans under supervision of Kris Aerts
 */
public class Anninstal
{
   public static void processAnn(Object obj)
   {
      try
      {
         Class<?> cl = obj.getClass();
         for(Field f : cl.getDeclaredFields())
         {
            //Intented i = f.getAnnotation(Intented.class);
            Intentional i = f.getAnnotation(Intentional.class);
            if(i!= null)
            {
               //i.equals
            }
         }
      }
      catch(Exception e)
      {
      }
   }
}
