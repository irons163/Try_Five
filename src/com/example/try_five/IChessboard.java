package com.example.try_five;

import java.util.List;

//棋盤介面，這裡其實不需要用介面，直接在Chessboard中設計即可
public interface IChessboard {
	//取得棋盤最大橫坐標
	public int getMaxX();
	//最大縱座標
	public int getMaxY();
	//取得當前所有空白點，這些點才可以下棋
	public List<Point> getFreePoints();
}
