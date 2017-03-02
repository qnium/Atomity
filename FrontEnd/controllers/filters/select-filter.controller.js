(function() {
    angular
        .module('CommonFrontend')
        .controller('SelectFilterController', SelectFilterController);
    
    /**
     * Controller for select field that is used to filter grid data.
     */
    function SelectFilterController($scope, $attrs, DataProviderService) {
        
        /**
         * Contains list of items displayed in select element.
         */
        $scope.itemList = [];
        /**
         * Filter object that is provided to grid controller.
         */
        $scope.filter = null;
        /**
         * Currently selected item. Used for accessing properties
         * and putting them in filter
         */
        $scope.selectedItem = null;

        $scope.changeItem = function() {
            $scope.refresh();
        };
        
        function initOptions(entity, defaultOption, additionalFilters, readAction) {
            DataProviderService.executeAction(entity, readAction, { filter: additionalFilters })
            .then(function(response){
                $scope.itemList = defaultOption ? [defaultOption] : []; 
                $scope.itemList = $scope.itemList.concat(response.data);
            });
        }
        
        function initFilter(field, valueExpression) {
            var filter = $scope.filter = {
                field: field,
                operation: 'eq',
                value: null
            };

            $scope.filters[filter.field + filter.operation] = filter;
            
            $scope.$watch('selectedItem', function(oldVal, newVal){
                filter.value = $scope.$eval(valueExpression);
            });
        }

        function init() {
            var field = $attrs.field;
            var entity = $attrs.entity;
            var readAction = $attrs.readAction || 'read';
            var additionalFilters = $scope.$eval($attrs.additionalFilters);
            var defaultOptionName = $attrs.defaultOptionName || '';
            var defaultOption = null;
            if (defaultOptionName) {
                defaultOption = {
                    id: null,
                    name: defaultOptionName
                };
            }
            else {
                defaultOption = $scope.$eval($attrs.defaultOption|| 'null');
            }
            var valueExpression = $attrs.filterValue || 'selectedItem';

            if(defaultOption && !$scope.selectedItem) {
                $scope.selectedItem = defaultOption; 
            }

            initOptions(entity, defaultOption, additionalFilters, readAction);
            initFilter(field, valueExpression);
        }
        
        $scope.$on('LoggedInEvent', function (e, arg) {
            init();
        });
    }
})();