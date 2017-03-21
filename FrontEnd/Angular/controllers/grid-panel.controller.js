(function() {
    angular.module('CommonFrontend')
        .controller('GridPanelController', GridPanelController);
    
    GridPanelController.$inject = ['$rootScope', '$scope', '$attrs', '$uibModal', 'DataProviderService', 'DialogService'];
    
    /**
     * Universal controller for grid panels that allows to view data
     * and manipulate it.
     */
    function GridPanelController($rootScope, $scope, $attrs, $uibModal, DataProviderService, DialogService) {    
        $scope.updateInProgress = false;
        $scope.currentPage = 1;
        $scope.pageCount = 1;
        $scope.selectedRecord = null;
        $scope.modalParameters = {};
        $scope.progressMessage = '';
        $scope.waitLoggedInEvent = false;
        $scope.pageLength = 10;
        $scope.validationError = null;
        
        $scope.pageButtons = [];
        $scope.entitiesToUpdate = null;        

        $scope.filters = {};
        $scope.sorting = null;
        $scope.onAfterRead = null;
        
        var entity = null;
        var activatedFilters = [];
        var activatedSorting = [];
        var defaultData = {};
        var pageButtonsCount = 10;
        /**
         * Request action for reading data. Default is read.
         */
        var readAction = 'read';

        var useDummyRows = true;

        /**
         * Container for custom grid data. Allows to set grid data
         * from child scopes. 
         */
        $scope.gridData = {};

        $scope.pageData = [];

        /**
         * Contains grid row models. Each row model contains an object from
         * pageData which is presented by that row, and other row-specific
         * things (e.g. selection flag, dummy flag).
         */
        $scope.pageRows = [];

        /**
         * Similar to 'updateInProgress' but true only for the first grid data
         * update after filter changes. Not true during auto-refresh.
         */
        $scope.filtersUpdated = false;

        /**
         * ID for auto-refresh interval process.
         */
        var autoRefreshIntervalId = null;

        $scope.editRecord = function(record) {
            $scope.selectedRecord = record;
            $scope.validationError = null;
            showDialog(record, $scope.modalParameters.editTemplateUrl)
            .then(function(result){
                $scope.sendRefreshEvent($scope.entitiesToUpdate);
            });
        };
        
        $scope.createRecord = function() {
            $scope.validationError = null;
            showDialog(defaultData, $scope.modalParameters.addTemplateUrl)
            .then(function(result){
                $scope.sendRefreshEvent($scope.entitiesToUpdate);
            });
        };

        $scope.deleteRecord = function(record) {
            $scope.validationError = null;
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
                    $scope.sendRefreshEvent($scope.entitiesToUpdate);
                }
           });
        };
        
        $scope.showCustomDialog = function(record, templateUrl) {
            showDialog(record, templateUrl)
            .then(function(result){
                $scope.sendRefreshEvent($scope.entitiesToUpdate);
                $scope.refresh();
            });
        };

        $scope.executeCustomAction = function(action, payload, params) {
            $scope.progressMessage = "Executing custom action...";
            $scope.updateInProgress = true;
            $scope.validationError = null;
            DataProviderService.executeAction(entity, action, payload)
            .then(function(result){
                $scope.updateInProgress = false;
                if(result.error){
                    $scope.validationError = result.error;
                }
                if(params) {
                    if(params.entitiesToUpdate){
                        $rootScope.$broadcast('UpdateEntityEvent', params.entitiesToUpdate);
                    }
                    if(params.dialogUrl){
                        showDialog(result, params.dialogUrl)
                        .then(function(result){
                            $scope.refresh();
                        });
                    }
                    if(params.noRefresh != true){
                        $scope.refresh();
                    }
                    if(params.closeOnSuccess == true && !$scope.validationError){
                        $scope.cancel();
                    }
                } else{
                    $scope.refresh();
                }
            });
        };

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
            
            return DataProviderService.executeAction(entity, readAction, {
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
                    $scope.totalCounter = result.totalCounter;
                    $rootScope.$broadcast('ReadCompletedEvent', entity);
                    if($scope.onAfterRead){
                        $scope.$eval($scope.onAfterRead);
                    }
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

        $scope.filterDataByDay = function(startFieldName, endFieldName, date, count) {
            var minTime = new Date(date);
            minTime.setHours(0);
            minTime.setMinutes(0);
            minTime.setSeconds(0);
            var maxTime = new Date(date);
            maxTime.setDate(date.getDate() + 1);
            maxTime.setHours(0);
            maxTime.setMinutes(0);
            maxTime.setSeconds(0);
            return $scope.filterDataByTime(startFieldName, endFieldName, minTime, maxTime, count);
        };

        $scope.filterDataByTime = function(startFieldName, endFieldName, minTime, maxTime, count) {
            var filteredData = $scope.pageData.filter(function(item){
                return item[endFieldName] > minTime && item[startFieldName] < maxTime; 
            });
            if (count) {
                filteredData = filteredData.slice(0, count);
            }
            return filteredData;
        };

        $scope.setSelectionForAll = function(selected) {
            return $scope.pageRows
                .forEach(function(r) {r.selected = selected; });
        };
        
        $scope.getSelectedData = function() {
            return $scope.pageRows
                .filter(function(r) { return r.selected; })
                .map(function(r) { return r.data; });
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
                    DialogData: function() {
                        return dialogData;
                    },
                    EntityName: function() {
                        return entity;
                    }
                }
            });

            return modalInstance.result;
        }

        function updatePageCount(totalCount) {
            $scope.pageCount = Math.ceil(totalCount / $scope.pageLength);
        }
        
        function fillPage(data) {
            if(!angular.equals($scope.pageData, data)) {
                var dummyRecords = $scope.pageLength - data.length;
                $scope.pageData = data;
                if (useDummyRows) {
                    for (var i = 0; i < dummyRecords; i++) {
                        $scope.pageData.push({id: i+0.1, dummy: true});
                    }
                }

                $scope.pageRows = $scope.pageData.map(generatePageRow);
            }
        }

        function generatePageRow(data) {
            return {
                selected: false,
                dummy: data.dummy,
                data: data
            };
        }
        
        function refreshIfChanged(newVal, oldVal) {
            if (newVal != oldVal) {
                initGridRefresh();
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

        function initGridRefresh() {
            if (autoRefreshIntervalId) {
                clearInterval(autoRefreshIntervalId);
            }
            initAutorefresh();

            $scope.filtersUpdated = true;
            //$scope.pageData = [];
            $scope.refresh()
            .then(function(){
                $scope.filtersUpdated = false;
            }, function(){
                $scope.filtersUpdated = false;
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
            useDummyRows = $attrs.useDummyRows ? $scope.$eval($attrs.useDummyRows) : true;
            $scope.onAfterRead = $attrs.onAfterRead;
            
            var defaultFilters = $attrs.defaultFilters ?
                $scope.$eval($attrs.defaultFilters) :
                [];
            initDefaultFilters(defaultFilters);
            
            readAction = $attrs.readAction ? $attrs.readAction : 'read';
            $scope.entitiesToUpdate = eval($attrs.entitiesToUpdate);
            
            fillPage([]);
            initSorting(sortingField);
            initFilterWatch();
            if($scope.waitLoggedInEvent !== "true"){
                initGridRefresh();
            }
        }

        function initAutorefresh() {
            var refreshInterval = parseInt($attrs.autoRefreshInterval, 10);
            if(!!refreshInterval) {
                autoRefreshIntervalId = setInterval(function() {
                    if(!$scope.waitLoggedInEvent || $scope.waitLoggedInEvent && $scope.isLoggedIn){
                        $scope.refresh();
                    }
                }, refreshInterval * 1000);
            }
        }

        $scope.sendRefreshEvent = function(entitiesToUpdate) {
            $rootScope.$broadcast('UpdateEntityEvent', [entity]);
            if(entitiesToUpdate) {
                $rootScope.$broadcast('UpdateEntityEvent', entitiesToUpdate);
            }
        }
        
        $scope.$on('LoggedInEvent', function (e, arg) {
            initGridRefresh();
        });
        
        $scope.$on('UpdateEntityEvent', function (e, arg) {
            if(arg) {
                for(i = 0; i < arg.length; i++) {
                    if(arg[i] == entity) {
                        $scope.refresh();
                    }
                }
            }            
        });

        init();
    }
})();