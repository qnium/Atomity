(function() {
    angular.module('CommonFrontend')
        .factory('DataProviderService', DataProviderService);
    
    DataProviderService.$inject = ['$http', '$rootScope'];
    
    /**
     * Provides access to backend API.
     */
    function DataProviderService($http, $rootScope) {
        /**
         * Session key for requests that need authorization.
         */
        var sessionKey = null;
        
        /**
         * Configuration for data provider.
         */
        var providerConfig = null;  
        
        return {
            getEntityData: getEntityData,
            create: createEntity,
            update: updateEntity,
            delete: deleteEntity,
            executeAction: executeAction,
			setSessionKey: setSessionKey,
            init: init
        };
        
        function executeAction(entityName, action, data) {
            var req = {
                entityName: entityName,
                action: action,
                sessionKey: sessionKey,
                data: data
            };

            req = angular.copy(req);
            if(req.data && req.data.filter)
            {
                for(var i = 0; i < req.data.filter.length; i++){
                    var val = req.data.filter[i].value;
                    if(val) {
                        try {
                            var newVal = dateTimeToIso8601(val);
                            req.data.filter[i].value = newVal;
                        } catch (e) { }
                    }
                }
            }            

            return $http.post(providerConfig.apiEndpoint, req)
            .then(function(response){
                if(response.data.errorCode === 2){
                    $rootScope.$broadcast('UnauthorizedAccessAttempt');
                    response.data.error = undefined;
                    response.data.data = [];
                }
                return response.data;
            });
        }
        
        function getEntityData(entityName, options) {
            return executeAction(entityName, 'read', options);
        }
        
        function dateTimeToIso8601(dateTime) {
            
            var tzOffset = 0 - dateTime.getTimezoneOffset();
            var sign = tzOffset >= 0 ? '+' : '-';
            tzOffset = Math.abs(tzOffset);
            var offsetHours = parseInt(tzOffset / 60);
            var offsetMinutes = tzOffset % 60;
            
            var formattedDateTime = moment(dateTime).format('YYYY-MM-DDTHH:mm:ss');
            formattedDateTime = formattedDateTime + sign + ("0" + offsetHours).slice(-2) + ":" + ("0" + offsetMinutes).slice(-2);
            
            return formattedDateTime;
        }
        
        function createEntity(entityName, record) {
            return executeAction(entityName, 'create', {
                entity: record
            });
        }
        
        function updateEntity(entityName, records) {
            return executeAction(entityName, 'update', {
                entities: records
            });
        }
        
        function deleteEntity(entityName, records) {
            return executeAction(entityName, 'delete', {
                entities: records
            });
        }
        
        function setSessionKey(key) {
            sessionKey = key;
        }
        
        function init(config) {
            providerConfig = config;
        }
    }
})();