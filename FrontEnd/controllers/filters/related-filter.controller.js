(function(){
    angular
        .module('CommonFrontend')
        .controller('RelatedFilterController', RelatedFilterController);
    
    /**
     * Controller for simple one-value field that is used to filter grid data.
     */
    function RelatedFilterController($scope, $attrs, DataProviderService) {
        
        var field = '';
        var op = 'in';
        
        function createFilter(field, operation, defaultValue) {
            var filter = $scope.filter = {
                field: field,
                operation: operation,
                value: defaultValue
            };
            $scope.filters[field + operation] = filter;
        }
        
        function init() {
            $scope.relatedEntityName = $attrs.relatedEntityName;
            $scope.readRelatedEntitiesAction = $attrs.readRelatedEntitiesAction;
            $scope.relatedEntityField = $attrs.relatedEntityField;
            
            field = $attrs.field;
            createFilter(field, op);
            
            $scope.$on('$destroy', function(){
                removeFilter(field, op);
            });
        }
        
        function removeFilter(field, operation) {
            delete $scope.filters[field + operation];
        }
        
        $scope.$watch('filterValue', function(oldVal, newVal)
        {
            if(oldVal !== newVal)
            {
                var filter = {
                    field: $scope.relatedEntityField,
                    operation: 'like',
                    value: $scope.filterValue == "" ? undefined : $scope.filterValue
                }
                DataProviderService.executeAction($scope.relatedEntityName, 
                                                  $scope.readRelatedEntitiesAction,
                                                  filter)
                .then(function(response){
                    $scope.filters[$scope.filter.field + $scope.filter.operation].value = response.result;
                });
            }
        });
        
        
        $scope.$on('LoggedInEvent', function (e, arg) {
            init();
        });
    }
})();