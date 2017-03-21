(function() {
    angular.module('CommonFrontend')
        .controller('UniversalFormController', UniversalFormController);
    
    UniversalFormController.$inject = ['$rootScope', '$scope', '$attrs', '$uibModal', '$location', '$interpolate', 'DataProviderService'];
    
    
    /**
     * Universal controller for forms with validation based on server-provided
     * rules. Type of managed entity defines the rules and fields of data 
     * object.
     */
    function UniversalFormController($rootScope, $scope, $attrs, $uibModal, $location, $interpolate, DataProviderService) {
        /**
         * Name of entity that is used for all requests needed for the form.
         */
        var entityName = null;
        
        /**
         * Loaded validation rules for the entity.
         */
        var validationRules = {};
        
        $scope.validationData = new Array();
        
        var validationOn = true;
        
        
        $scope.$location = $location;
        
        /**
         * Object that stores all data from form fields. All ng-model bindings
         * should be within this object.
         */
        $scope.formData = {};
        
        /**
         * Contains validation results for all fields affected by validation
         * rules. Field is validated only after first change.
         */
        //$scope.validationData = {};
        var validators = {};
        
        $scope.validationError = null;
        /**
         * Stores error received after sending request to server
         * (if any error occurred).
         */
        $scope.serverError = null;
        
        $scope.$watch('serverError', function(newVal, oldVal) { if (newVal !=null || newVal != 'undefined') $scope.validationError = newVal; });
        
        var createAction = '';
        var updateAction = '';
        
        /**
         * Set of available actions for the form.
         */
        $scope.actions = {
            create: function() {
                return executeFormAction(createAction, { entity: $scope.formData });
            },
            
            update: function() {
                return executeFormAction(updateAction, { entities: [$scope.formData] });
            },
            
            delete: function() {
                return executeFormAction('delete', { entities: [$scope.formData] });
            },
            
            register: function() {
                return executeFormAction('register', $scope.formData);
            },
            
            'login': function() {
                return executeFormAction('login');
            },
            
            'cancel': function() {
                if (typeof $scope.cancel === 'function') {
                    $scope.cancel();
                }
            },
            
            'customAction': function(action, dialogUrl, dialogData) {
                return executeFormAction(action, $scope.formData, dialogUrl, dialogData);
            }
        };
        
        /**
         * Indicates whether form action has already been called.
         */
        $scope.actionDone = false;

        $scope.actionAllowed = true;
        
        /**
         * Indicates whether form action is in progress.
         */
        $scope.actionInProgress = false;
        
        /**
         * Sends request with the specified action and data.
         * Entity is the same as form controller's entity.
         * @param   {String}  action Action for request.
         * @param   {Object}  data   Request payload. If not specified,
         *                         $scope.formData is used.
         * @returns {Promise} Promise that is resolved with server response.
         */
        function executeFormAction(action, data, dialogUrl, dialogData) {
            $scope.actionInProgress = true;
            $scope.actionDone = false;
            $scope.serverError = undefined;
            data = typeof data !== 'undefined' ? data : $scope.formData;
            if(action)
            {
                return DataProviderService.executeAction(entityName, action, data)
                .then(function(response){
                    $scope.actionInProgress = false;
                    $scope.actionDone = true;
                    if (response.error) {
                        $scope.serverError = response.error;
                        $scope.actionAllowed = true;
                    }
                    else {
                        $rootScope.$broadcast('UpdateEntityEvent', $scope.entitiesToUpdate);
                        if (typeof $scope.onFormSuccess === 'function') {
                            $scope.onFormSuccess();
                            if(dialogUrl != undefined) {
                                showDialog(dialogData || {}, dialogUrl);
                            }
                        }
                    }
                    return response;
                });
            } else {
                if(dialogUrl != undefined) {
                    $scope.actionInProgress = false;
                    $scope.actionDone = true;
                    showDialog(dialogData || {}, dialogUrl)
                    .then(function(result){
                        $scope.onFormSuccess();
                    });
                } else{
                    $scope.onFormSuccess();                    
                }
            }            
        }
        
        function showDialog(dialogData, templateUrl) {
            var modalInstance = $uibModal.open({
                templateUrl: templateUrl,
                controller: 'GridModalController',
                size: 'md',
                resolve: {
                    DialogData: dialogData,
                    EntityName: function() {
                        return entityName;
                    }
                }
            });

            return modalInstance.result;
        }
        
        /**
         * Loads validation rules for the specified entity name
         * and saves them as controller data.
         * @param {String} entity Name of validated entity.
         * @returns {Promise<ValidationRule[]>} Rules of validation.                       
         */
        function loadValidationRules(entity) {
            return DataProviderService.executeAction(entityName, 'validators')
            .then(function(response) {
                if (response.error) {
                    console.log('Error while loading validation rules for ' + entityName, response.error);
                }
                return response.result || null;
            });
        }
        
        /**
         * Adds watching for formData that applies validation rules on change of
         * formData fields.
         */
        function enableFormValidation(validationRules) {
            if (validationRules == null)
            return;
            
            validationRules.forEach( function (field) {
                
                var func;
                var code = 'func = ' + field.validationCode;
                eval(code);
                
                validators[field.fieldName] = func;
                
                $scope.$watch('formData.' + field.fieldName, function(newVal, oldVal) {
                    if (oldVal != newVal) {
                        
                        $scope.validationData[field.fieldName] = applyValidationRules(field, newVal);
                        
                        checkFormValid();
                    }
                });
            } );
        }
        
        /**
         * Applies loaded validation rules for the specified field and value.
         * @param   {String} field Name of the field to validate.
         * @param   {Object} value Value to validate.
         * @returns {Object} Boolean result of validation and list of errors
         *                   if any.
         */
        function applyValidationRules(field, value) {
           
           validator = validators[field.fieldName];
           if (  validator != undefined )
           {
               var err = validator(value);
               return err;
           }
        }
        
        function checkFormValid()
        {
            $scope.validationError = null;
            Object.keys($scope.validationData).forEach(function(key){
               if ($scope.validationData.hasOwnProperty(key) && $scope.validationData[key] != null){
                  $scope.validationError = $scope.validationData[key];
               }
            });
            $scope.actionAllowed = $scope.validationError ? false : true;
        }
        
        /**
         * Initialization logic for controller. May be called
         * as ng-init method in template.
         */
        function init() {
            if ($attrs.entity) {
                entityName = $interpolate($attrs.entity)($scope);
            }
            else {
                throw new Error("Entity must be set for the form controller");
            }
            
            if ($attrs.formData) {
                try {
                    $scope.formData = $attrs.formData ? $scope.$eval($attrs.formData) : {};
                }
                catch (ex) {
                    throw new Error("Failed to parse initial form data", ex);
                }
            }
            
            createAction = $attrs.createAction || 'create';
            updateAction = $attrs.updateAction || 'update';
            
            if( $attrs.updateAction != undefined)
                updateAction = $attrs.updateAction;
            
            if ($attrs.formValidate != undefined)
            {
                validationOn = eval($attrs.formValidate);
            }
            
            if (validationOn)
            {
                loadValidationRules(entityName)
                .then(function(rules){
                    
                    return enableFormValidation(rules);
                });
            }

            $scope.entitiesToUpdate = eval($attrs.entitiesToUpdate);

            if ($attrs.formSuccess) {
                $scope.onFormSuccess = function() {
                    $scope.$eval($attrs.formSuccess);
                };
            }
            
        }
        
        $scope.pwdGen = function(length) 
        {
            var pwd = "";
            var ints = ['0','1','2','3','4','5','6','7','8','9'];
            var chars = ['a','b','c','d','e','f','g','h','i','j','k','l','m','n',
                        'o','p','q','r','s','t','v','u','w','x','y','z'];
            
            var max = chars.length;
            for(i = 0; i < length; i++) {
                var isNum = Math.random() < 0.5;
                if(isNum){
                    pwd = pwd + ints[Math.round(Math.random() * (ints.length - 1))];
                } else {
                    var isUpper = Math.random() < 0.5;
                    var char = chars[Math.round(Math.random() * (chars.length - 1))];
                    if(isUpper){
                        char = ("" + char).toUpperCase();
                    }
                    pwd = pwd + char;
                }                
            }
            
            return pwd;
        }
        
        init();
    }
})();
    
