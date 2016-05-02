angular.module('ngPuzzle', []).controller('PuzzleBoard', function($scope, $timeout, $window)
{
	var BOARD_SIZE = 5;

	function setupBoard()
	{
		$scope.board = [];
		for (var i = 0; i < BOARD_SIZE; i++)
		{
			$scope.board[i] = [];
			for (var j = 0; j < BOARD_SIZE; j++)
			{
				$scope.board[i][j] =
					i<=BOARD_SIZE/2		&& j<=BOARD_SIZE/2-1	? "white" :
					i<=BOARD_SIZE/2-1	&& j<=BOARD_SIZE/2		? "white" :
					i>=BOARD_SIZE/2		&& j>=BOARD_SIZE/2-1	? "red" :
					i>=BOARD_SIZE/2-1	&& j>=BOARD_SIZE/2		? "red" : "_";
			}
		}
	}
	setupBoard();
});