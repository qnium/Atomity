(function(){
    angular
        .module('CommonFrontend')
        .controller('SimpleFilterController', SimpleFilterController);
    
    /**
     * Controller for simple one-value field that is used to filter grid data.
     */
    function SimpleFilterController($scope, $attrs) {
        
        $scope.filter = null;
        
        function createFilter(field, operation, defaultValue) {
            var filter = $scope.filter = {
                field: field,
                operation: operation,
                value: defaultValue
            };
            $scope.filters[field + operation] = filter;
        }
        
        function init() {
            var field = $attrs.field;
            var op = $attrs.operation;
            var defaultValue = $attrs.defaultValue ? $scope.$eval($attrs.defaultValue) : null;
            createFilter(field, op, defaultValue);
            $scope.$on('$destroy', function(){
                removeFilter(field, op);
            });
        }
        
        function removeFilter(field, operation) {
            delete $scope.filters[field + operation];
        }
        
        $scope.$on('LoggedInEvent', function (e, arg) {
            init();
        });
    }
})();