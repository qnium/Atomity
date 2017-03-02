(function() {
    angular.module('CommonFrontend')
        .controller('GridModalController', GridModalController);
    
    GridModalController.$inject = ['$scope', '$uibModalInstance', 'DialogData', 'EntityName'];
    
    /**
     * Controls entity-related modal dialogs called from grid panel controller.
     */
    function GridModalController($scope, $uibModalInstance, DialogData, EntityName) {

        $scope.entityName = EntityName;
        
        // Copying dialog data here to avoid editing real data.
        $scope.dialogData = DialogData ? angular.copy(DialogData) : {};

        $scope.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        };
        
        $scope.onFormSuccess = function() {
            $uibModalInstance.close($scope.dialogData);
        };
    }
})();