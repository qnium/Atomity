/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.validation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.qnium.common.validation.data.ValidatorItem;
import com.qnium.common.validation.exceptions.ValidationException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author nbv
 */
public class ValidationManager {
    
    private ValidationManager() {
    }
    
    public static ValidationManager getInstance() {
        return ValidationManagerHolder.INSTANCE;
    }
    
    private static class ValidationManagerHolder {

        private static final ValidationManager INSTANCE = new ValidationManager();
    }
    
    public void init()
    {
    }
    
    public static String serializeObject(Object object) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        mapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        mapper.registerModule(new JavaTimeModule());
        return mapper.writeValueAsString(object);
    }
    
    public static Object deserializeObject(String data, Class objectClass) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        mapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        mapper.registerModule(new JavaTimeModule());
        return mapper.readValue(data, objectClass);
    }
    
    public List<ValidatorItem> getEntityJSValidators(Class entity)
    {
        ArrayList<ValidatorItem> validatorsJS = new ArrayList<>();
        List<Field> fields = Arrays.stream(entity.getDeclaredFields()).filter( f -> f.isAnnotationPresent(FieldValidator.class) || f.isAnnotationPresent(FieldValidators.class)).collect(Collectors.toList());
        for (Field f : fields)
        {
            ValidatorItem validatorJS = new ValidatorItem();
            validatorJS.fieldName = f.getName();
            FieldValidator[] validators = f.getAnnotationsByType(FieldValidator.class);
            
            StringBuilder jsCodeBuilder = new StringBuilder();
            jsCodeBuilder.append("function (object) { var err = null;");
            
            for (FieldValidator validator: validators)
            {
                try{
                    IValidator valInstance = (IValidator) validator.validator().newInstance();
                    valInstance.init(validator.param(), validator.errorMessage());
                    jsCodeBuilder.append( String.format("if ( %s ) err = null; else err=\"%s\"; if (err != null) return err;", valInstance.getJSCode(), valInstance.getError().replace("\"", "\\\"")));
                } catch(Exception ex)
                {
                    
                }
            }
            
            jsCodeBuilder.append("return null;}");
            
            validatorJS.validationCode = jsCodeBuilder.toString();
            validatorsJS.add(validatorJS);
        }
        
        return validatorsJS;
    }
    
    public void validateObject(Object object) throws ValidationException
    {
        for (Field f : object.getClass().getDeclaredFields())
        {
            if ( f.isAnnotationPresent(FieldValidator.class) || f.isAnnotationPresent(FieldValidators.class))
            {
                FieldValidator[] validators = f.getAnnotationsByType(FieldValidator.class);            
                
                for (FieldValidator validator: validators)
                {
                    try{
                        IValidator valInstance = (IValidator) validator.validator().newInstance();
                        valInstance.init(validator.param(), validator.errorMessage());
                        
                        Object value = null;
                        
                        value = f.get(object);
                  
                        valInstance.validate(value != null ? value.toString() : "");
                    } catch(ValidationException exVal)
                    {
                        throw exVal;
                    } catch(Exception ex)
                    {
                        throw new ValidationException(ex.getMessage());
                    } 
                }
            }
        }
    }
    
}
