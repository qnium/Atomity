(function() {
    angular.module('CommonFrontend')
        .controller('GridPanelController', GridPanelController);
    
    GridPanelController.$inject = ['$scope', '$attrs', '$uibModal', 'DataProviderService', 'DialogService'];
    
    /**
     * Universal controller for grid panels that allows to view data
     * and manipulate it.
     */
    function GridPanelController($scope, $attrs, $uibModal, DataProviderService, DialogService) {    
        $scope.updateInProgress = false;
        $scope.currentPage = 1;
        $scope.pageCount = 2;
        $scope.selectedRecord = null;
        $scope.modalParameters = {};
        $scope.progressMessage = '';
        $scope.waitLoggedInEvent = false;
        $scope.pageLength = 10;
        
        $scope.pageButtons = [];
        

        $scope.filters = {};
        $scope.sorting = null;
        
        var entity = null;
        var activatedFilters = [];
        var activatedSorting = [];
        var defaultData = {};
        var pageButtonsCount = 10;
        /**
         * Request action for reading data. Default is read.
         */
        var readAction = 'read';

        $scope.pageData = [];

        $scope.editRecord = function(record) {
            $scope.selectedRecord = record;
            showDialog(record, $scope.modalParameters.editTemplateUrl)
            .then(function(result){
                $scope.refresh();
            });
        };
        
        $scope.createRecord = function() {
            showDialog(defaultData, $scope.modalParameters.addTemplateUrl)
            .then(function(result){
                $scope.refresh();
            });
        };

        $scope.deleteRecord = function(record) {
           DialogService.confirm("Delete record?")
           .then(function() {
                $scope.progressMessage = "Deleting data...";
                $scope.updateInProgress = true;
                return DataProviderService.delete(entity, [record]);
           })
           .then(function(result) {
                $scope.updateInProgress = false;
                updatePageCount(result.totalCount);
                if (result.error) {
                    DialogService.alert(result.error, 'Error');
                } else {
                    $scope.refresh();
                }
           });
        };
        
        $scope.showCustomDialog = function(record, templateUrl) {
            showDialog(record, templateUrl)
            .then(function(result){
                $scope.refresh();
            });
        }

        function updatePageButtons()
        {
            var availableButtonsCount = $scope.pageCount > pageButtonsCount ? pageButtonsCount : $scope.pageCount;
            var middleButtonIndex = Math.ceil(availableButtonsCount / 2);
            var firstButtonPageNumber = $scope.currentPage - middleButtonIndex;
            if(firstButtonPageNumber < 0 ){
                firstButtonPageNumber = 0;
            }
            var lastButtonPageNumber = firstButtonPageNumber + availableButtonsCount - 1;
            if(lastButtonPageNumber > $scope.pageCount-1){
                firstButtonPageNumber -= lastButtonPageNumber - ($scope.pageCount-1);
            }
            $scope.pageButtons = [];
            for(var i = 1; i <= availableButtonsCount; i++){
                var pageButton = {
                    page: i + firstButtonPageNumber,
                    isCurrent: $scope.currentPage == i + firstButtonPageNumber
                }
                $scope.pageButtons.push(pageButton);
            }            
        }
        
        $scope.pageButtonClick = function(pageButton){
            if($scope.currentPage != pageButton.page){
                $scope.currentPage = pageButton.page;
                $scope.refresh();
            }
        }
                
        $scope.refresh = function() {
            fixCurrentPage();
            $scope.progressMessage = "Loading data...";
            $scope.updateInProgress = true;
            
            DataProviderService.executeAction(entity, readAction, {
                order: null,
                filter: getArray($scope.filters),
                startIndex: $scope.pageLength * ($scope.currentPage - 1),
                count: $scope.pageLength
            })
            .then(function(result){
                $scope.updateInProgress = false;
                if (result.error) {
                    DialogService.alert(result.error, 'Error');
                }
                else {
                    updatePageCount(result.totalCounter);
                    updatePageButtons();
                    fillPage(result.data);
                }
            });
        };

        $scope.nextPageAvailable = function(){
            return $scope.currentPage < $scope.pageCount;
        };

        $scope.prevPageAvailable = function(){
            return $scope.currentPage > 1;
        };

        $scope.previous = function() {
            if ($scope.prevPageAvailable()) {
                $scope.currentPage--;
                $scope.refresh();
            }
        };

        $scope.next = function() {
            if ($scope.nextPageAvailable()) {
                $scope.currentPage++;
                $scope.refresh();
            }
        };
        
        $scope.previousPages = function() {
            if ($scope.prevPageAvailable()) {
                $scope.currentPage -= pageButtonsCount;
                $scope.refresh();
            }
        };

        $scope.nextPages = function() {
            if ($scope.nextPageAvailable()) {
                $scope.currentPage += pageButtonsCount;
                $scope.refresh();
            }
        };
        
        /**
         * Updates current sorting with new field and direction.
         * @param {String}    newField     Field to use for sorting.
         * @param [{Boolean}] newDirection New direction. Default is the opposite
         *                                 of the previous direction or "ascending"
         *                                 if field has been changed.
         */
        $scope.toggleSorting = function(newField, newDirection) {
            var oldField = $scope.sorting.field;
            $scope.sorting.field = newField;
            if (typeof newDirection !== 'undefined') {
                $scope.sorting.value = newDirection;
            }
            else {
                var fieldChanged = oldField != newField;
                $scope.sorting.value = fieldChanged ? false : !$scope.sorting.value;
            }
            $scope.refresh();
        };
        
        function getArray(obj) {
            var result = [];
            for (var key in obj) {
                result.push(obj[key]);
            }
            return result;
        }
        
        function fixCurrentPage() {
            if ($scope.currentPage > $scope.pageCount) {
                $scope.currentPage = $scope.pageCount;
            }
            if ($scope.currentPage < 1) {
                $scope.currentPage = 1;
            }
            if ($scope.pageCount < 1) {
                $scope.pageCount = 1;
            }
        }

        function showDialog(dialogData, templateUrl) {
            var modalInstance = $uibModal.open({
                templateUrl: templateUrl,
                controller: 'GridModalController',
                size: 'md',
                resolve: {
                    DialogData: dialogData
                }
            });

            return modalInstance.result;
        }

        function updatePageCount(totalCount) {
            $scope.pageCount = Math.ceil(totalCount / $scope.pageLength);
        }
        
        function fillPage(data) {
            var dummyRecords = $scope.pageLength - data.length;
            $scope.pageData = data;
            for (var i = 0; i < dummyRecords; i++) {
               $scope.pageData.push({id: i+0.1, dummy: true});
            }
        }
        
        function refreshIfChanged(newVal, oldVal) {
            if (newVal != oldVal) {
                $scope.refresh();
            }
        }
        
        function initFilterWatch() {
            $scope.$watchCollection('filters', function(newVal, oldVal) {
                for (var key in newVal) {
                    if(activatedFilters.indexOf(newVal[key]) == -1) {
                        activatedFilters.push(newVal[key]);
                        $scope.$watch('filters["' + key + '"].value', refreshIfChanged);
                    }
                }
            });
        }
        
        function initSorting(sortingField)
        {
            var sorting = {
                field: 'id',
                operation: 'sort',
                value: false
            };
            
            if(sortingField != undefined) {
                if(sortingField.field == undefined) {
                    sortingField.field = sorting.field;
                }
                if(sortingField.operation == undefined) {
                    sortingField.operation = sorting.operation;
                }
                if(sortingField.value == undefined) {
                    sortingField.value = sorting.value;
                }
                sorting = sortingField;
            }
            
            $scope.sorting = $scope.filters.sort = sorting;
            activatedFilters.push($scope.sorting);
        }
        
        function initDefaultFilters(filters) {
            filters = Array.isArray(filters) ? filters : [filters];
            filters.forEach(function(f){
                f.operation = f.operation || 'eq';
                $scope.filters[f.field + f.operation] = f; 
            });
        }
        
        function init() {
            entity = $attrs.entity;
            $scope.modalParameters.addTemplateUrl = $attrs.addTemplateUrl;
            $scope.modalParameters.editTemplateUrl = $attrs.editTemplateUrl;
            var sortingField = $scope.$eval($attrs.defaultSortingField);
            defaultData = $scope.$eval($attrs.defaultData) || {};
            var pl = $attrs.pageLength;
            $scope.pageLength = pl ? parseInt(pl, 10) : 10;
            var pb = $attrs.pageButtonsCount;
            pageButtonsCount = pb ? parseInt(pb, 10) : 10;
            $scope.pageButtonsCount = pageButtonsCount;
            $scope.waitLoggedInEvent = $attrs.waitLoggedInEvent;
            
            var defaultFilters = $attrs.defaultFilters ?
                $scope.$eval($attrs.defaultFilters) :
                [];
            initDefaultFilters(defaultFilters);
            
            readAction = $attrs.readAction ? $attrs.readAction : 'read';
            
            fillPage([]);
            initSorting(sortingField);
            initFilterWatch();
            if($scope.waitLoggedInEvent !== "true"){
                $scope.refresh();
            }
        }
        
        $scope.$on('LoggedInEvent', function (e, arg) {
            $scope.refresh();
        });
        
        init();
    }
})();