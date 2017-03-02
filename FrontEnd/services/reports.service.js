(function() {
    angular.module('CommonFrontend')
        .service('ReportsService', ReportsService);

    ReportsService.$inject = ['$rootScope', 'base64'];
    
    /**
     * Provides report generation features.
     */
    function ReportsService($rootScope, base64) {
        
        /**
         * Reports API endpoint.
         */
        var apiEndpoint = null;
        
        /**
         * Session key for API requests
         */
        var sessionKey = null;
        
        function initSessionKey() {
            $rootScope.$on('LoggedInEvent', function (e, data) {
                sessionKey = data.key;
            });
        }
        
        /**
         * Initializes service with application config.
         * @param {Object} config application config.
         */
        this.init = function(config) {
            if (!config.reportsEndpoint) {
                throw new Error("Reports endpoint is not set in configuration");
            }
            else {
                apiEndpoint = config.reportsEndpoint;
            }
            initSessionKey();
        };
        
        /**
         * Creates link to report from the given request options.
         * @param   {Object} request Request object. Should contain
         *                         entity/action/format and filtering data.
         * @returns {String} URL to report.
         */
        this.getReportUrl = function(request) {
            request.sessionKey = sessionKey;
            return apiEndpoint + '?request=' + base64.urlencode(JSON.stringify(request));
        };
    }
})();