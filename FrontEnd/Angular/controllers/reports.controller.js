(function() {
    angular.module('CommonFrontend')
        .controller('ReportsController', ReportsController);
    
    ReportsController.$inject = ['$scope', '$attrs', 'ReportsService'];
    
    /**
     * Controller that provides client side of reporting features
     * (generating reports, getting links to reports).
     */
    function ReportsController($scope, $attrs, ReportsService) {
        /*
         * Filters can be reused from grid controller or stored here
         * if there's no parent grid.
         */
        if (!$scope.filters) {
            $scope.filters = {};
        }
        
        /**
         * URL of current report.
         */
        $scope.reportUrl = null;
        
        /**
         * Current report action (used for generating current report).
         */
        $scope.reportAction = null;
        /**
         * Current report format (used for generating current report).
         * May be set to PDF, CSV etc.
         */
        $scope.reportFormat = null;
        
        /**
         * Generates link to report based on current filter/formatting settings
         * and sets it as current report URL.
         */
        $scope.generateReport = function() {
            $scope.reportUrl = $scope.generateReportUrl($scope.reportFormat, false);
        }
        
        /**
         * Generates link to report based on current filter settings.
         * @param {String}   format      Report format. May be set to PDF, CSV, XLS, DOC.
         * @param [{Boolean}]downloadReport If true, report content, will be downloaded,
         *                                 otherwise it's returned as binary stream.
         *                                 Default is true.
         * @returns {String} URL to report file.
         */
        $scope.generateReportUrl = function(format, downloadReport) {
            if (typeof downloadReport === 'undefined') {
                downloadReport = true;
            }
            else {
                downloadReport = !!downloadReport;
            }
            var request = {
                entityName: 'reports',
                action: $scope.reportAction,
                reportFormat: format,
                downloadReport: downloadReport,
                asd12: 0,
                //asd12: String(new Date().getTime()),
                data: {
                    filter: getArray($scope.filters)
                }
            };
            
            //request.asd12 = String(new Date().getTime());
            //request.asd12 = Math.random();
            //debugger

            return ReportsService.getReportUrl(request);
        }
        
        $scope.getDateWithOffset = function(offsetSeconds) {
            return new Date(new Date().getTime() + offsetSeconds * 1000);
        };
        
        function init() {
            $scope.reportAction = $attrs.action;
            $scope.reportFormat = $attrs.format || 'PDF';
        }
        
        function getArray(obj) {
            var result = [];
            for (var key in obj) {
                result.push(obj[key]);
            }
            return result;
        }
        
        init();
    }
})();