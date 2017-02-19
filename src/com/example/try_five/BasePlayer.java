package com.example.try_five;

import java.util.ArrayList;
import java.util.List;

//抽象類別(不過應該直接使用，不實作介面)
public abstract class BasePlayer implements IPlayer {
	//�����µ�����
	protected List<Point> myPoints = new ArrayList<Point>(200);
	//����
	protected Chessboard chessboard;
	//�������������ݱ꣬
	protected int maxX;
	protected int maxY;
	
	//���пհ�����
	protected List<Point> allFreePoints;

	@Override
	public final List<Point> getMyPoints() {
		return myPoints;
	}

	@Override
	public void setChessboard(Chessboard chessboard) {
		this.chessboard = chessboard;
		allFreePoints = chessboard.getFreePoints();
		maxX = chessboard.getMaxX();
		maxY = chessboard.getMaxY();
		myPoints.clear();
	}
	
	private final Point temp = new Point(0, 0); //創建一個暫時點
	//該玩家是否贏了
	public final boolean hasWin(){
		if(myPoints.size()<5){ //該玩家所有點不到5個(不可能贏)
			return false;
		}
		Point point = myPoints.get(myPoints.size()-1); //最新點
		int count = 1; //連線(橫縱斜)的點個數(最新點自己表示1個)
		int x=point.getX(),y=point.getY(); //點的橫縱編號
		temp.setX(x).setY(y);//將最新點的資訊賦予暫存點
		//首先進行最新點的左邊點個數判斷:
		//該玩家下的所有點中，是否含有暫存點(最新點)橫軸左邊一格的點 且 這個左邊一個的點是否編號不為負 且 連線的點個數小於5
		//這裡的temp.getX()>=0無意義，因為若編號為負，myPoints.contains的判斷中就為false了
		//count<5意義不大，只是讓程式快上一點點，但還是可以加上
		while (myPoints.contains(temp.setX(temp.getX()-1)) && temp.getX()>=0 && count<5) {
			count ++;//若左邊有點，則連線的點個數+1
		}
		if(count>=5){ //若最新點左邊(包含最新點)有5個點則贏
			return true;
		}
		temp.setX(x).setY(y);//重新將最新點的資訊賦予暫存點
		//再來進行最新點的右邊點個數判斷:
		while (myPoints.contains(temp.setX(temp.getX()+1)) && temp.getX()<maxX && count<5) {
			count ++;
		}
		if(count>=5){ //若最新點右邊與上面計算左邊點個數大於等於5點則贏
			return true;
		}
		//橫軸判斷完了，改判斷縱軸，連線的點個數還原為0(橫軸個數的不能加到縱軸中)
		count = 1;
		temp.setX(x).setY(y);
		//縱軸上方判斷
		while (myPoints.contains(temp.setY(temp.getY()-1)) && temp.getY()>=0) {
			count ++;//若上方有點則+1
		}
		if(count>=5){ //最新點+上方大於等於5則贏
			return true;
		}
		//再來進行下方判斷
		temp.setX(x).setY(y);
		while (myPoints.contains(temp.setY(temp.getY()+1)) && temp.getY()<maxY && count<5) {
			count ++;//下方有點則+1
		}
		if(count>=5){ //上下共5點則贏
			return true;
		}
		//改判斷斜軸 / (右上左下)
		count =1;
		temp.setX(x).setY(y);
		while (myPoints.contains(temp.setX(temp.getX()-1).setY(temp.getY()+1)) && temp.getX()>=0 && temp.getY()<maxY) {
			count ++;//左下
		}
		if(count>=5){
			return true;
		}
		temp.setX(x).setY(y);
		while (myPoints.contains(temp.setX(temp.getX()+1).setY(temp.getY()-1)) && temp.getX()<maxX && temp.getY()>=0 && count<6) {
			count ++;//右上
		}
		if(count>=5){
			return true;
		}
		//斜軸 \ (左上右下)
		count = 1;
		temp.setX(x).setY(y);
		while (myPoints.contains(temp.setX(temp.getX()-1).setY(temp.getY()-1)) && temp.getX()>=0 && temp.getY()>=0) {
			count ++;//左上
		}
		if(count>=5){
			return true;
		}
		temp.setX(x).setY(y);
		while (myPoints.contains(temp.setX(temp.getX()+1).setY(temp.getY()+1)) && temp.getX()<maxX && temp.getY()<maxY && count<5) {
			count ++;//右下
		}
		if(count>=5){
			return true;
		}
		return false; //橫縱斜都沒有5點，就是沒有贏
	}
}
