(function() {
    angular.module('CommonFrontend')
        .service('DialogService', DialogService);

    DialogService.$inject = ['$uibModal'];
    
    /**
     * Service for calling common dialogs (alert, confirm, prompt)
     * with custom templates. Template path is set using 
     * setTemplatePath() function.
     */
    function DialogService($uibModal) {
        
        var templatePath = '';
        
        this.setTemplatePath = function(path) {
            templatePath = path;
        };
        
        this.alert = function(message, title) {
            var ctrl = function($scope, $uibModalInstance) {
                $scope.title = title || '';
                $scope.message = message;

                $scope.ok = function() {
                  $uibModalInstance.close();
                };
            };
            
            return $uibModal.open({
               templateUrl: templatePath + '/message.html',
               controller: ctrl
            }).result;   
        };
        
        this.confirm = function(message, title) {
            var ctrl = function($scope, $uibModalInstance) {        
                $scope.title = title || '';
                $scope.message = message;

                $scope.ok = function() {
                  $uibModalInstance.close();
                };

                $scope.cancel = function() {
                    $uibModalInstance.dismiss('cancel');
                };
            };
            
            return $uibModal.open({
               templateUrl: templatePath + '/confirmation.html',
               controller: ctrl
            }).result;
        };
        
        this.prompt = function (options) {
            options = options || {};
            var size = options.size || 'md';
            var ctrl = function($scope, $uibModalInstance) {
                $scope.title = options.title || '';
                $scope.description = options.description || '';
                $scope.value = options.initialValue || '';
                $scope.acceptLabel = options.acceptLabel || 'OK';
                $scope.cancelLabel = options.cancelLabel || 'Cancel';

                $scope.ok = function() {
                  $uibModalInstance.close($scope.value);
                };

                $scope.cancel = function() {
                    $uibModalInstance.dismiss('cancel');
                };
            };
            
            return $uibModal.open({
                templateUrl: templatePath + '/text-input.html',
                controller: ctrl,
                size: size
            }).result;
        };
    }
})();