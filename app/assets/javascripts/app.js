'use strict';

var app = angular.module('searchbrew', []);

app.controller('SearchCtrl', function ($scope, $http, $timeout) {
        $scope.showAll = false;

        function doSearch(){
            $scope.showAll = false;
            if (!$scope.searchString) return;
            $http.get('/search?q='+$scope.searchString).
                success(function(data){
                    if(data.query === $scope.searchString){
                        $scope.results = data;
                    }
                });
        };

        var timeout;
        $scope.$watch('searchString', function(){
            if (timeout) $timeout.cancel(timeout);
            if ($scope.searchString && $scope.searchString.length > 0){
                timeout = $timeout(doSearch, 150);
            } else {
                $scope.results = {};
            }
        });

        $scope.getAll = function(){
            $scope.showAll = true;
            $http.get('/search?size=10000').
                success(function(data){
                    $scope.results = data;
                    $scope.searchResults = data.hits.hits;
                });
        };
    });