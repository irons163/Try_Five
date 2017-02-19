package com.example.try_five;

import java.util.List;

//玩家動作介面(不管是人類玩家或電腦都有相同的動作)
//這裡其實不適合用介面，因為HumanPlayer和BaseComputerAi都繼承BasePlayer，因此直接在BasePlayer中實作即可。
public interface IPlayer {
	//執行下棋，傳入對手已經下的棋子集合與自己想下的位置進行分析判斷
	public void run(List<Point> enemyPoints, Point point);
	//該玩家是否贏了
	public boolean hasWin();
	
	public void setChessboard(Chessboard chessboard);
	
	public List<Point> getMyPoints();
}
