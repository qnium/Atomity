/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation.common;

import com.qnium.common.validation.ValidationManager;
import com.qnium.common.validation.data.ValidatorItem;
import java.util.List;
import java.util.Optional;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import validation.testData.TestEntityMax;

/**
 *
 * @author Kirill Zhukov
 */
public class JSValidationTester {
    
    private JSValidationTester() {
    }
    
    public static JSValidationTester getInstance() {
        return JSValidationTesterHolder.INSTANCE;
    }
    
    private static class JSValidationTesterHolder {

        private static final JSValidationTester INSTANCE = new JSValidationTester();
    }
    
    public String validateData(Class entity, String field, String data) throws Exception
    {
        String result = null;
        
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        
        List<ValidatorItem> entityJSValidators = ValidationManager.getInstance().getEntityJSValidators(entity);
        Optional<ValidatorItem> fieldValidator = entityJSValidators.stream().filter( v -> v.fieldName.equals(field) ).findFirst();
        
        StringBuilder testCode = new StringBuilder("var validate = ");
        testCode.append(fieldValidator.get().validationCode);
        testCode.append(";");
        
        engine.eval(testCode.toString());
        Invocable invocable = (Invocable) engine;
        result = (String) invocable.invokeFunction("validate", data);
        
        return result;
    }
}
