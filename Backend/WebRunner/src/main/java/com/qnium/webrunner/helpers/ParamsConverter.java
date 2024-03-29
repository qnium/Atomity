/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.webrunner.helpers;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 * @author admin
 */
public class ParamsConverter {

    static final HashMap<Class<?>, Function<String, ?>> _converters = new HashMap();

    static final Function<String, Integer> intConverter = (param) -> Integer.parseInt(param);
    static final Function<String, Long> longConverter = (param) -> Long.parseLong(param);
    static final Function<String, Boolean> boolConverter = (param) -> Boolean.parseBoolean(param);
    static final Function<String, Instant> instantConverter = (param) -> OffsetDateTime.parse(param).toInstant();

    static {
         _converters.put(Integer.class, intConverter);
         _converters.put(int.class, intConverter);

         _converters.put(Long.class, longConverter);
         _converters.put(long.class, longConverter);

         _converters.put(Boolean.class, boolConverter);
         _converters.put(boolean.class, boolConverter);

         _converters.put(Instant.class, instantConverter);
    }



    public static Object convert(Map<String, String[]> params, Class<?> clazz) throws Exception
    {
        Object result = clazz.getDeclaredConstructor().newInstance();

        for( String key : params.keySet())
        {
            Field field = clazz.getField(key);

            //Field with the name same as parameter not found
            if (field == null)
            {
                throw new Exception(String.format("No such parameter: %s", key));
            } else {

                //We save the params that came from request as a variable for performance
                String[] paramStrings = params.get(key);

                //If class property is array
                if (field.getType().isArray())
                {

                    if (field.getType() == String[].class)
                    {
                        field.set(result, paramStrings);
                        continue;
                    }

                    Function<String, ?> converter = _converters.get(field.getType().getComponentType());

                    Object array = Array.newInstance(field.getType().getComponentType(), paramStrings.length);

                    for(int i = 0; i < paramStrings.length; i ++ )
                    {
                        Array.set(array, i, converter.apply(paramStrings[i]));
                    }

                    field.set(result, array);
                } else {
                    //if (field)
                    if (paramStrings.length > 1)
                        throw new Exception(String.format("Multiple parameters '%s'", key));

                    if (field.getType() == String.class)
                    {
                        field.set(result, paramStrings[0]);
                        continue;
                    }

                    Function<String, ?> converter = _converters.get(field.getType());
                    field.set(result, converter.apply(paramStrings[0]));
                }

            }
        }

        return result;
    }

}
