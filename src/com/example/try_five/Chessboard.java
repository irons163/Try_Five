package com.example.try_five;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
//繼承View(複寫其建構子與onDraw)，實作IChessboard
public class Chessboard extends View {
	//
	private static final int GREEN = 0;//綠色點(綠色棋子)
	private static final int NEW_GREEN = 1;//最近下的綠色棋子
	private static final int RED = 2;//紅色點(紅色棋子)
	private static final int NEW_RED = 3;//最近下的紅色棋子
	
	private final Paint paint = new Paint();//畫筆物件
	
	//螢幕右下角的座標值，即最大座標值
    private static int maxX;
    private static int maxY;
    
	// 所有未下的空白点
	private final List<Point> allFreePoints = new ArrayList<Point>();
	
	//點大小
    private static int pointSize = 20;
    
	//點(4種類)的Bigmap陣列
	private Bitmap[] pointArray = new Bitmap[4];
	
    //用于提示输赢的文本控件
	private TextView textView = null;
    //當前狀態。預設為可開局狀態
    private int currentMode = READY;
    
	//遊戲狀態常數:
    private static final int READY = 1;//可開局
    private static final int RUNNING = 2;//已開局
    private static final int PLAYER_TWO_LOST = 3;//已結束
    private static final int PLAYER_ONE_LOST = 4;
    
	//兩個玩家
	private IPlayer player1 = new HumanPlayer();//第一個玩家預設為人類玩家
	private IPlayer player2;//第二個則根據選擇人機對戰或是雙人對戰來初始化
	
	//預先初始兩個玩家，再根據選擇人機對戰或是雙人對戰來分配，ex:player2=computer。此方法因為先初始化，因此速率較好，但是佔記憶體。
	private static final IPlayer computer = AiFactory.getInstance(2);//電腦玩家
	private static final IPlayer human = new HumanPlayer();//人類玩家
    //第一点偏离左上角从像数，为了棋盘居中
	private static int yOffset;
	private static int xOffset;
	
    public Chessboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        
        //把兩個顏色的點(四種類型)準備好，並放入陣列中
        Resources r = this.getContext().getResources();
        fillPointArrays(GREEN,r.getDrawable(R.drawable.green_point));
        fillPointArrays(NEW_GREEN,r.getDrawable(R.drawable.new_green_point));
        fillPointArrays(RED,r.getDrawable(R.drawable.red_point));
        fillPointArrays(NEW_RED,r.getDrawable(R.drawable.new_red_point));
        
        //設置畫線時的顏色(棋盤的格子線)
        paint.setColor(Color.LTGRAY);
   }
    //實作IChessboard的getMaxX()
	public int getMaxX() {
		return maxX;
	}
	//實作IChessboard的getMaxY()
	public int getMaxY() {
		return maxY;
	}
	//實作IChessboard的getFreePoints()
	public List<Point> getFreePoints() {
		return allFreePoints;
	}
    
	//初始化好紅綠兩點
    public void fillPointArrays(int color,Drawable drawable) {
    	//新建一個bitmap，長寬20，使用ARGB_8888設定，此bitmap現在空白bitmap但非null。
        Bitmap bitmap = Bitmap.createBitmap(pointSize, pointSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap); //新建畫布，用空白bitmap當畫布
        drawable.setBounds(0, 0, pointSize, pointSize);//設定drawable的邊界(原圖片有自己的長寬)
        drawable.draw(canvas); //在畫布上畫上此drawable(此時bitmap已經被畫上東西，不是空白了)
        pointArray[color] = bitmap; //將此bitmap存入點陣列中(共4種點)
    }
    
	//设置提示控件
	public void setTextView(TextView textView) {
		this.textView = textView;
	}
	
	//複寫View的鍵盤監聽事件
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
		//判斷當前狀態(currentMode)，若為可開局狀態(ready)，且使用者按下方向鍵右或左
        if (currentMode == READY && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
        	if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){//向右鍵，人機對戰
        		player2 = computer; //第二個玩家為電腦玩家
        	}else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){//向左鍵，雙人對戰
        		player2 = human; //第二個玩家為人類玩家
        	} 
        	restart(); //重新開始
        	setMode(RUNNING); //設置運行狀態(設置為RUNNING)
        }else if(currentMode==RUNNING && keyCode == KeyEvent.KEYCODE_DPAD_DOWN){//重新開局
        	restart(); 
        	setMode(READY); //當前狀態為可開局
        }else{
        	return false;
        }
        return true;
	}
	
	//重新開始
	private void restart() {
		createPoints();//初始化空白點
		player1.setChessboard(this);
		player2.setChessboard(this);
		setPlayer1Run();
		//刷新一下
		refressCanvas();
	}
	
    //設置運行狀態
	public void setMode(int newMode) {
		currentMode = newMode; //當前狀態為傳入的狀態
		if(currentMode==PLAYER_TWO_LOST){ //當前狀態(傳入的狀態)為PLAYER_TWO_LOST
			textView.setText(R.string.player_two_lost);//提示玩家2輸了
			currentMode = READY;//將當前狀態設為READY(因為勝負已分)
		}else if(currentMode==RUNNING){//若當前狀態為RUNNING
			textView.setText(null);//開局中，不顯示任何文字
		}else if(currentMode==READY){//若當前狀態為READY
			textView.setText(R.string.mode_ready);//提示選擇模式(方向鍵選擇人機或雙人)
		}else if(currentMode==PLAYER_ONE_LOST){
			textView.setText(R.string.player_one_lost);//提示玩家1輸了
			currentMode = READY;//將當前狀態設為READY(因為勝負已分)
		}
	}
	
	//初始化空白點集合
	private void createPoints(){
		allFreePoints.clear(); //所有空白點集合先清空(因為重新開始了)
		for (int i = 0; i < maxX; i++) { //比線少1
			for (int j = 0; j < maxY; j++) {
				allFreePoints.add(new Point(i, j));//空白點集合
			}
		}
	}
	
	//預設第一個玩家先行
	private int whoRun = 1;
	//輪到第一個玩家下棋
	private void setPlayer1Run(){
		whoRun = 1;
	}
	//下棋處理中(思考)
	private void setOnProcessing(){
		whoRun = -1;
	}
	//刷新一下
	private void refressCanvas(){
        Chessboard.this.invalidate();//View.invalidate()會觸發onDraw
	}
	//複寫View的onDraw事件，最好在onDraw之前就先把要畫的元件準備，否則會LAG，尤其是快速繪圖時，更會卡卡的
    @Override
    protected void onDraw(Canvas canvas) {
    	drawChssboardLines(canvas);
    	drawPlayer1Point(canvas);//畫玩家1所下的所有棋子
    	drawPlayer2Point(canvas);//畫玩家2所下的所有棋子
    }
    
    //畫棋盤
    private List<Line> lines = new ArrayList<Line>();//此Line集合在onSizeChange時已被初始化，內有數條線(EX:25)
    private void drawChssboardLines(Canvas canvas){
    	for (Line line : lines) {
    		//在View本身的畫布上畫線
    		canvas.drawLine(line.xStart, line.yStart, line.xStop, line.yStop, paint);
		}
    }
    
    //線類別
    class Line{
    	float xStart,yStart,xStop,yStop;
    	//建構子
		public Line(float xStart, float yStart, float xStop, float yStop) {
			//onSizeChange初始化時，把各個座標傳入(開始的xy座標到結束的xy座標)
			this.xStart = xStart; 
			this.yStart = yStart;
			this.xStop = xStop;
			this.yStop = yStop;
		}
    }
  //畫玩家1所下的所有棋子
	private void drawPlayer1Point(Canvas canvas){
		int size = player1.getMyPoints().size()-1;//玩家1所下的舊棋子(所有棋子扣除最新棋子)
		if(size<0){ // -1就是沒有下任何棋子
			return ;
		}
		for (int i = 0; i < size; i++) { //畫所有舊棋子
			//畫點，傳入View的canvas與該舊棋子(point物件)與顏色種類(玩家1為綠色)
			drawPoint(canvas, player1.getMyPoints().get(i), GREEN);
		}
		//畫點，最後下的棋子(最新棋子)
		drawPoint(canvas, player1.getMyPoints().get(size), NEW_GREEN);
	}
	//畫玩家2所下的所有棋子
	private void drawPlayer2Point(Canvas canvas){
		//這裡要特別注意，因為玩家2在一開始還沒有決定是電腦還是人類，因此一開始是Null。
		if(player2==null){
			return ;
		}
		//以下同drawPlayer1Point
		int size = player2.getMyPoints().size()-1;
		if(size<0){
			return ;
		}
		for (int i = 0; i < size; i++) {
			drawPoint(canvas, player2.getMyPoints().get(i), RED);
		}
		drawPoint(canvas, player2.getMyPoints().get(size), NEW_RED);
	}
	
    //畫點(畫棋子)
    private void drawPoint(Canvas canvas,Point p,int color){
    	canvas.drawBitmap(pointArray[color],p.x*pointSize+xOffset,p.y*pointSize+yOffset,paint);
    }
    
    //複寫View的onSizeChanged，此方法在建構子之後執行，之後才會執行onDraw，這裡用來初始橫線與縱線的數目
    //View元件有自己的size預設值，這裡在layout時把此View設為match_parent，因此會觸發此onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        maxX = (int) Math.floor(w / pointSize); // 取比此值大的最小整數  480/20=24 470/20=23.5 >> 24
        maxY = (int) Math.floor(h / pointSize);

        //设置X、Y座标微调值，目的整个框居中
        xOffset = ((w - (pointSize * maxX)) / 2); // (480 - 20*24)/2=0  (470-20*24)/2=-10/2=-5
        yOffset = ((h - (pointSize * maxY)) / 2);
        //創建棋盤上的線條
        createLines();
        //初始化棋盤上所有的空白點
        createPoints();
    }
    
    //產生棋盤上所有的線
    private void createLines(){
    	for (int i = 0; i <= maxX; i++) {//豎線 0-24 共25條
    		//(-5+0-10) (240+20-10) (-5+480-10)
    		lines.add(new Line(xOffset+i*pointSize-pointSize/2, yOffset, xOffset+i*pointSize-pointSize/2, yOffset+maxY*pointSize));
		}
    	for (int i = 0; i <= maxY; i++) {//橫線
    		lines.add(new Line(xOffset, yOffset+i*pointSize-pointSize/2, xOffset+maxX*pointSize, yOffset+i*pointSize-pointSize/2));
		}
    }
    
	//處理觸摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//還沒有開局就不處理觸摸事件。此外只處理彈起事件。
		if(!hasStart() || event.getAction()!=MotionEvent.ACTION_UP){
			return true;
		}
		//若為已開局且為彈起事件，則執行以下程式
		if(onProcessing()){ //是否正在處理中(對方尚未下棋)
			return true;
		}
		
		playerRun(event); //執行下棋步驟
		
		return true;
	}
	
	private synchronized void playerRun(MotionEvent event){
		if(isPlayer1Run()){//玩家1下棋
			player1Run(event);
		}else if(isPlayer2Run()){//玩家2下棋
			player2Run(event);
		}
	}
	
	
	private void player1Run(MotionEvent event){
		Point point = newPoint(event.getX(), event.getY());//取得觸摸的XY座標
		if(allFreePoints.contains(point)){//此棋是否可下
			setOnProcessing();//思考中
			player1.run(player2.getMyPoints(),point);
			//playerOnePoints.add(point);
			//刷新一下棋盘
			refressCanvas();
			//判断第一个玩家是否已经下了
			if(!player1.hasWin()){//我还没有赢
				if(player2==computer){//如果第二玩家是电脑
					//10豪秒后才给玩家2下棋
					refreshHandler.computerRunAfter(10);
				}else{
					setPlayer2Run();
				}
			}else{
				//否则，提示游戏结束
				setMode(PLAYER_TWO_LOST);
			}
		}
	}
	
	private void player2Run(MotionEvent event){
		Point point = newPoint(event.getX(), event.getY());
		if(allFreePoints.contains(point)){//此棋是否可下
			setOnProcessing();
			player2.run(player1.getMyPoints(),point);
//			playerTwoPoints.add(point);
			//刷新一下棋盘
			refressCanvas();
			//判断我是否赢了
			if(!player2.hasWin()){//我还没有赢
				setPlayer1Run();
			}else{
				//否则，提示游戏结束
				setMode(PLAYER_ONE_LOST);
			}
		}
	}
	
	//根據觸摸點座標找到對應點
	private Point newPoint(Float x, Float y){
		Point p = new Point(0, 0);//創建橫軸編號為0(橫軸的第一個點)，縱軸編號也為0(縱軸的第一個點)的點
		for (int i = 0; i < maxX; i++) {//0-23 共24點
			//(0-5)<0 0<(20-5)
			if ((i * pointSize + xOffset) <= x
					&& x < ((i + 1) * pointSize + xOffset)) {
				p.setX(i);//設定p的x為i，也就是橫軸第i+1個點
			}
		}
		for (int i = 0; i < maxY; i++) {//跟上面橫軸差不多，這裡是處理縱軸
			if ((i * pointSize + yOffset) <= y
					&& y < ((i + 1) * pointSize + yOffset)) {
				p.setY(i);
			}
		}
		return p; //回傳 ponit p
	}
	
	//是否已開局
	private boolean hasStart(){
		return currentMode==RUNNING;
	}
	
    //是否對方(電腦)正在思考下棋，主要是電腦下棋時需要較長的思考時間，這期間不可以響應觸摸事件
	private boolean onProcessing() {
		return whoRun == -1;
	}
	
	//是否輪到玩家1下棋
	private boolean isPlayer1Run(){
		return whoRun==1;
	}
	
	//是否輪到玩家2下棋
	private boolean isPlayer2Run(){
		return whoRun==2;
	}
	
	private RefreshHandler refreshHandler = new RefreshHandler();
	class RefreshHandler extends Handler {

		//这个方法主要在指定的时刻发一个消息
        public void computerRunAfter(long delayMillis) {
        	this.removeMessages(0);
        	//发消息触发handleMessage函数
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
        
        //收到消息
        @Override
        public void handleMessage(Message msg) {
        	//电脑走一步棋子
    		player2.run(player1.getMyPoints(),null);
    		//刷新一下
    		refressCanvas();
    		if(!player2.hasWin()){
    			//人下
    			setPlayer1Run();
    		}else{//第二个玩家赢了
    			setMode(PLAYER_ONE_LOST);
    		}
        }
    };
    
	private void setPlayer2Run(){
		whoRun = 2;
	}
}
