'use strict';

var app = angular.module('searchbrew', []);

app.controller('SearchCtrl', function ($scope, $http, $timeout, $location) {
  $scope.showAll = false;
  $scope.loading = false;

  if ($location.search().q) {
    $scope.searchQuery = $location.search().q;
  }

  function doSearch(){
    if (!$scope.searchQuery) return;
    $scope.loading = true;
    $scope.showAll = false;
    $http.get('/search?q='+$scope.searchQuery).
      success(function(data){
        if(data.query === $scope.searchQuery){
          $scope.loading = false;
          resetFilter();
          $scope.searchResults = data.data;
        }
      });
  }

  var timeout;
  $scope.$watch('searchQuery', function(){
    if (timeout) $timeout.cancel(timeout);
    if ($scope.searchQuery && $scope.searchQuery.length > 0){
      timeout = $timeout(doSearch, 150);
    } else {
      $scope.searchResults = [];
    }
  });

  function resetFilter(){
    $scope.predicate = null;
    $scope.reverse = null;
  }

  $scope.getAll = function(){
    $scope.showAll = true;
    $scope.searchQuery = "";
    $scope.loading = true;
    $http.get('/search').
      success(function(data){
        $scope.loading = false;
        $scope.searchResults = data.data;
      });
  };
});