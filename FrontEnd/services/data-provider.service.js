(function() {
    angular.module('CommonFrontend')
        .factory('DataProviderService', DataProviderService);
    
    DataProviderService.$inject = ['$http'];
    
    /**
     * Provides access to backend API.
     */
    function DataProviderService($http) {
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
            return $http.post(providerConfig.apiEndpoint, req)
            .then(function(response){
                return response.data;
            });
        }
        
        function getEntityData(entityName, options) {
            return executeAction(entityName, 'read', options);
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