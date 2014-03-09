'use strict';

var app = angular.module('searchbrew', ['pasvaz.bindonce']);

app.controller('SearchCtrl', function ($scope, $http, $timeout) {
        $scope.showAll = false;
        $scope.loading = false;

        function doSearch(){
            if (!$scope.searchQuery) return;
            $scope.loading = true;
            $scope.showAll = false;
            $http.get('/api/search?q='+$scope.searchQuery).
                success(function(data){
                    if(data.query === $scope.searchQuery){
                        $scope.loading = false;
                        $scope.searchResults = data.hits.hits;
                    }
                });
        };

        var timeout;
        $scope.$watch('searchQuery', function(){
            if (timeout) $timeout.cancel(timeout);
            if ($scope.searchQuery && $scope.searchQuery.length > 0){
                timeout = $timeout(doSearch, 150);
            } else {
                $scope.searchResults = [];
            }
        });

        $scope.getAll = function(){
            $scope.showAll = true;
            $scope.searchQuery = "";
            $scope.loading = true;
            $http.get('/api/search?size=10000').
                success(function(data){
                    $scope.loading = false;
                    $scope.searchResults = data.hits.hits;
                });
        };
    });