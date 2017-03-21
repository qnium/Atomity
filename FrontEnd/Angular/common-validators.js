var CommonValidators = {
    'required': function(value) {
        var valid = typeof value !== 'undefined' && value !== null;
        return {
            valid: valid,
            error: valid ? '' : 'Value is required'
        };
    },
    
    'maxLength': function(value, options) {
        var max = options.max || Infinity;
        var valid = value.length <= max;
        return {
            valid: valid,
            error: valid ? '' : 'Length must be less or equal to ' + max
        };
    },
    
    'minLength': function(value, options) {
        var min = options.min || -Infinity;
        var valid = value.length >= min;
        return {
            valid: valid,
            error: valid ? '' : 'Length must be at least ' + min
        };
    },
    
    'maxValue': function(value, options) {
        var max = options.max || Infinity;
        var valid = value <= max;
        return {
            valid: valid,
            error: valid ? '' : 'Value must be less or equal to ' + max
        };
    },
    
    'minValue': function(value, options) {
        var min = options.min || -Infinity;
        var valid = value >= min;
        return {
            valid: valid,
            error: valid ? '' : 'Value must be at least ' + min
        };
    },
};