package com.example.try_five;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//�㷨�����࣬�㷨������˼���������裬
//��һ�������˫���ĵ�ǰ������ѭ���ؼ����Եķֱ���Լ��ͶԷ���һ�ӣ���ĳ����Χ�����ӣ������жϴ������ܴ����������ϵı仯�����ܲ��ܳ�4���ܲ����γ��ҷ���з�˫3�ȣ�
//�ڶ����������һ��������ÿһ����������������н����ĳһ�����ӿ����γ��ҷ�1����3��1����4���ҽ�����4���ȣ��������з����ҷ��ġ�
//�������û���Ĺ������һ�����������򣬲�ѡ�ӣ��н��Ρ������ι���
public class BaseComputerAi extends BasePlayer {

	// �ĸ����򣬺�- ����| ����б/ ����б\
	private static final int HENG = 0;
	private static final int ZHONG = 1;
	private static final int ZHENG_XIE = 2;
	private static final int FAN_XIE = 3;
	//��ǰ���
	private static final boolean FORWARD = true;
	private static final boolean BACKWARD = false;
	

	
	//��ʾ�������ǰ��λ����ͷͨ��ALIVE������ֻ��һͷͨ��HALF_ALIVE�������������ӷ�������Զ����Σ�����Ϊ��ѡ����
	private static final int ALIVE = 1;
	private static final int HALF_ALIVE = 0;
	//private static final int DEAD = -1;
	
	//���㷶Χ��̫��ķ�Χ������������
	private class CalcuteRange{
		int xStart,yStart,xStop,yStop;
		private CalcuteRange(int xStart, int yStart, int xStop, int yStop) {
			this.xStart = xStart;
			this.yStart = yStart;
			this.xStop = xStop;
			this.yStop = yStop;
		}
	}
	
	
	//�޶����Լ��㷶Χ�����������̼��㣬����̫�Ŀǰ�Ǹ���������µ����ӵı߽�ֵ��RANGE_STEPֵ�γɣ�ĿǰΪ1
	private static final int RANGE_STEP = 1;
	CalcuteRange currentRange = new CalcuteRange(0, 0, 0, 0);
	private void initRange(List<Point> comuters, List<Point> humans){
		currentRange.xStart = humans.get(0).getX()-RANGE_STEP;
		currentRange.yStart = humans.get(0).getY()-RANGE_STEP;
		currentRange.xStop = humans.get(0).getX()+RANGE_STEP;
		currentRange.yStop = humans.get(0).getY()+RANGE_STEP;
		for (Point point : humans) {
			if(point.getX()-RANGE_STEP<currentRange.xStart){
				currentRange.xStart = point.getX()-RANGE_STEP;
			}else if(point.getX()+RANGE_STEP>currentRange.xStop){
				currentRange.xStop = point.getX()+RANGE_STEP;
			}
			if(point.getY()-RANGE_STEP<currentRange.yStart){
				currentRange.yStart = point.getY()-RANGE_STEP;
			}else if(point.getY()+RANGE_STEP>currentRange.yStop){
				currentRange.yStop = point.getY()+RANGE_STEP;
			}
		}
		for (Point point : comuters) {
			if(point.getX()-RANGE_STEP<currentRange.xStart){
				currentRange.xStart = point.getX()-RANGE_STEP;
			}else if(point.getX()+RANGE_STEP>currentRange.xStop){
				currentRange.xStop = point.getX()+RANGE_STEP;
			}
			if(point.getY()-RANGE_STEP<currentRange.yStart){
				currentRange.yStart = point.getY()-RANGE_STEP;
			}else if(point.getY()+RANGE_STEP>currentRange.yStop){
				currentRange.yStop = point.getY()+RANGE_STEP;
			}
		}
		
		//���Χ����󳬹������̣����������
		currentRange.xStart=currentRange.xStart<0?0:currentRange.xStart;
		currentRange.yStart=currentRange.yStart<0?0:currentRange.yStart;
		currentRange.xStop=currentRange.xStop>=maxX?maxX-1:currentRange.xStop;
		currentRange.yStop=currentRange.yStop>=maxY?maxY-1:currentRange.yStop;
	}

	// ������ǰ��ʽ����ڷ����������ܹ���������裬��������������Ԥ�����Ѷȿ���
	private Point doAnalysis(List<Point> comuters, List<Point> humans) {
		if(humans.size()==1){//��һ��
			return getFirstPoint(humans);
		}
		
		//��ʼ�����㷶Χ
		initRange(comuters, humans);
		
		//�����ǰ�Ľ��
		initAnalysisResults();
		// ��ʼ������ɨ�����пհ׵㣬�γɵ�һ�η������
		Point bestPoint = doFirstAnalysis(comuters, humans);
		if(bestPoint!=null){
			//System.out.println("�����������Ҫ��ֻ�����������");
			return bestPoint;
		}
		// ������һ�ν���ҵ��Լ�����ѵ�λ
		bestPoint = doComputerSencondAnalysis(computerFirstResults,computerSencodResults);
		if(bestPoint!=null){
			//System.out.println("��ҪӮ�ˣ������������");
			return bestPoint;
		}
		computerFirstResults.clear();
		System.gc();
		// ������һ�ν���ҵ����˵���ѵ�λ
		bestPoint = doHumanSencondAnalysis(humanFirstResults,humanSencodResults);
		if(bestPoint!=null){
			//System.out.println("�ٲ���������Ӿ�����");
			return bestPoint;
		}
		humanFirstResults.clear();
		System.gc();
		//û�ҵ���ɱ�㣬����ν�����
		return doThirdAnalysis();
	}
	

	//�µ�һ�����ӣ�����Ҫ���ӵļ��㣬��������һ������Xֵ��1���
	private Point getFirstPoint(List<Point> humans) {
		Point point = humans.get(0);
		if(point.getX()==0 || point.getY()==0 || point.getX()==maxX && point.getY()==maxY)
			return new Point(maxX/2, maxY/2);
		else{
			return new Point(point.getX()-1,point.getY());
		}
	}

//	private int debugx,debugy;//����DEBUG

	// ��ʼ������ɨ�����пհ׵㣬�γɵ�һ�η������
	private Point doFirstAnalysis(List<Point> comuters, List<Point> humans){
		int size = allFreePoints.size();
		Point computerPoint = null;
		Point humanPoint = null;
		int x,y;
		FirstAnalysisResult firstAnalysisResult;
		for (int i = 0; i < size; i++) {
			computerPoint = allFreePoints.get(i);
			//�Ȱ�X��Y������������Ϊ�ڷ�������л�ı�ԭ���Ķ���
			x = computerPoint.getX();
			y = computerPoint.getY();
			if(x<currentRange.xStart || x>currentRange.xStop || y<currentRange.yStart || y>currentRange.yStop){
				continue;
			}
			
//			if(x==debugx && y==debugy){
//				System.out.println("sssssssssssss");
//			}
			
			//�����ڴ�λ������һ�����ӣ��������ڡ���������������ҷ����γɵ�״̬�����4����3�����4����2������״̬
			firstAnalysisResult = tryAndCountResult(comuters,humans, computerPoint, HENG);
			computerPoint.setX(x).setY(y);//�ظ���λ��ԭֵ���Թ��´η���
			if(firstAnalysisResult!=null){//�޷��ؽ��˷����ϲ����ܴﵽ������ӣ�
				if(firstAnalysisResult.count==5)//����5��ʾ�ڴ˵��������Ӽ�������5����ʤ���ˣ��������½��з���
					return computerPoint;
				//��¼��һ�η������
				addToFirstAnalysisResult(firstAnalysisResult,computerFirstResults);
			}
			
			//�ڡ���������������ظ�����Ĳ���
			firstAnalysisResult = tryAndCountResult(comuters,humans, computerPoint, ZHONG);
			computerPoint.setX(x).setY(y);
			if(firstAnalysisResult!=null){//���壬����
				if(firstAnalysisResult.count==5)
					return computerPoint;
				
				addToFirstAnalysisResult(firstAnalysisResult,computerFirstResults);
			}
			
			//��б��
			firstAnalysisResult = tryAndCountResult(comuters,humans, computerPoint, ZHENG_XIE);
			computerPoint.setX(x).setY(y);
			if(firstAnalysisResult!=null){//���壬����
				if(firstAnalysisResult.count==5)
					return computerPoint;
				
				addToFirstAnalysisResult(firstAnalysisResult,computerFirstResults);
			}
			
			//��б��
			firstAnalysisResult = tryAndCountResult(comuters,humans, computerPoint, FAN_XIE);
			computerPoint.setX(x).setY(y);
			if(firstAnalysisResult!=null){//���壬����
				if(firstAnalysisResult.count==5)
					return computerPoint;
				
				addToFirstAnalysisResult(firstAnalysisResult,computerFirstResults);
			}
			
			//�ڡ������Ϸ��������ӿ��ڵз��γ����״̬����з��Ļ�3�����4��
			firstAnalysisResult = tryAndCountResult(humans,comuters, computerPoint, HENG);
			computerPoint.setX(x).setY(y);
			if(firstAnalysisResult!=null){//���壬����
				if(firstAnalysisResult.count==5)
					humanPoint = computerPoint;
				
				addToFirstAnalysisResult(firstAnalysisResult,humanFirstResults);
			}
			
			//������
			firstAnalysisResult = tryAndCountResult(humans,comuters, computerPoint, ZHONG);
			computerPoint.setX(x).setY(y);
			if(firstAnalysisResult!=null){//���壬����
				if(firstAnalysisResult.count==5)
					humanPoint = computerPoint;
				
				addToFirstAnalysisResult(firstAnalysisResult,humanFirstResults);
			}
			
			//����б��
			firstAnalysisResult = tryAndCountResult(humans,comuters, computerPoint, ZHENG_XIE);
			computerPoint.setX(x).setY(y);
			if(firstAnalysisResult!=null){//���壬����
				if(firstAnalysisResult.count==5)
					humanPoint = computerPoint;
				
				addToFirstAnalysisResult(firstAnalysisResult,humanFirstResults);
			}
			
			//����б��
			firstAnalysisResult = tryAndCountResult(humans,comuters, computerPoint, FAN_XIE);
			computerPoint.setX(x).setY(y);
			if(firstAnalysisResult!=null){//���壬����
				if(firstAnalysisResult.count==5)
					humanPoint = computerPoint;
				
				addToFirstAnalysisResult(firstAnalysisResult,humanFirstResults);
			}
		}
		//���û�о�ɱ���ӣ���һ�η�������Ҫ���ؽ��
		return humanPoint;
	}
	
	//�ڶ��η�����������һ���γɵĽ���һ�η��������һ�������ĸ������Ͽ��γɵĽ���������ĸ�FirstAnalysisResult���󣨵��Ҹ��ģ�
	//����Ҫ�����ĸ�������ϳ�һ��SencondAnalysisResult����
	private Point doComputerSencondAnalysis(Map<Point,List<FirstAnalysisResult>> firstResults,List<SencondAnalysisResult> sencodResults) {
		List<FirstAnalysisResult> list = null;
		SencondAnalysisResult sr = null;
		for (Point p : firstResults.keySet()) {
			sr = new SencondAnalysisResult(p);
			list = firstResults.get(p);
			for (FirstAnalysisResult result : list) {
				if(result.count==4){
					if(result.aliveState==ALIVE){//����ǰ��Ĺ��ˣ�˫�����ų��˾�ɱ�壬�л�4������һ���ˣ�����һ����Ӯ��
						return result.point;//����о�ɱ����һ���ѷ��أ��ڴ��ֻ�4�Ѿ��Ǻõ����ӣ�ֱ�ӷ��أ��������·���
					}else{
						sr.halfAlive4 ++;
						computer4HalfAlives.add(sr);
					}
				}else if(result.count==3){
					if(result.aliveState==ALIVE){
						sr.alive3++;
						if(sr.alive3==1){
							computer3Alives.add(sr);
						}else{
							computerDouble3Alives.add(sr);
						}
					}else{
						sr.halfAlive3++;
						computer3HalfAlives.add(sr);
					}
				}else{//���2�ڵ�һ�׶��ѱ��ų��ٴ���
					sr.alive2++;
					if(sr.alive2==1){
						computer2Alives.add(sr);
					}else{
						computerDouble2Alives.add(sr);
					}
				}
			}
			sencodResults.add(sr);
		}
		//û���ҵ���4
		return null;
	}
	
	//�������������Ļ�һ��Ϊ�����ܣ����������жϣ�������͵��Եķֿ���
	private Point doHumanSencondAnalysis(Map<Point,List<FirstAnalysisResult>> firstResults,List<SencondAnalysisResult> sencodResults) {
		List<FirstAnalysisResult> list = null;
		SencondAnalysisResult sr = null;
		for (Point p : firstResults.keySet()) {
			sr = new SencondAnalysisResult(p);
			list = firstResults.get(p);
			for (FirstAnalysisResult result : list) {
				if(result.count==4){
					if(result.aliveState==ALIVE){
						human4Alives.add(sr);
					}else{
						sr.halfAlive4 ++;
						human4HalfAlives.add(sr);
					}
				}else if(result.count==3){
					if(result.aliveState==ALIVE){
						sr.alive3++;
						if(sr.alive3==1){
							human3Alives.add(sr);
						}else{
							humanDouble3Alives.add(sr);
						}
					}else{
						sr.halfAlive3++;
						human3HalfAlives.add(sr);
					}
				}else{
					sr.alive2++;
					if(sr.alive2==1){
						human2Alives.add(sr);
					}else{
						humanDouble2Alives.add(sr);
					}
				}
			}
			sencodResults.add(sr);
		}
		//û���ҵ���4
		return null;
	}
	
	private void sleep(int miniSecond){
		try {
			Thread.sleep(miniSecond);
		} catch (InterruptedException e) {
		}
	}
	
	
	//����η�����˫���������������4����˫��3���ӣ����о��Ұ��4���ٲ��о��ҵ���3��˫��2
	private Point doThirdAnalysis() {
		if(!computer4HalfAlives.isEmpty()){
			return computer4HalfAlives.get(0).point;
		}
		System.gc();
		sleep(300);
		Collections.sort(computerSencodResults);
		System.gc();
		
		//��������4������û�а��4���ϵģ�ֻ�ܶ�
		Point mostBest = getBestPoint(human4Alives, computerSencodResults);
		if(mostBest!=null)
			return mostBest;
		
		Collections.sort(humanSencodResults);
		System.gc();
		
		mostBest = getBestPoint();
		if(mostBest!=null)
			return mostBest;
		
		//�ó������ŵ�һ�ģ�˭�þ���˭
		return computerSencodResults.get(0).point;
	}
	
	//����ʵ��������������ı���˳�����ʵ�ַ���Ϊ�������͹�
	protected Point getBestPoint(){
		//��������4������û�а��4���ϵģ�ֻ�ܶ�
		Point mostBest = getBestPoint(computerDouble3Alives, humanSencodResults);
		if(mostBest!=null)
			return mostBest;
		
		mostBest = getBestPoint(computer3Alives, humanSencodResults);
		if(mostBest!=null)
			return mostBest;
		
		mostBest = getBestPoint(humanDouble3Alives, computerSencodResults);
		if(mostBest!=null)
			return mostBest;
		
		mostBest = getBestPoint(human3Alives, computerSencodResults);
		if(mostBest!=null)
			return mostBest;

		mostBest = getBestPoint(computerDouble2Alives, humanSencodResults);
		if(mostBest!=null)
			return mostBest;
		
		mostBest = getBestPoint(computer2Alives, humanSencodResults);
		if(mostBest!=null)
			return mostBest;
		
		mostBest = getBestPoint(computer3HalfAlives, humanSencodResults);
		if(mostBest!=null)
			return mostBest;
		
		mostBest = getBestPoint(human4HalfAlives, computerSencodResults);
		if(mostBest!=null)
			return mostBest;
		
		mostBest = getBestPoint(humanDouble2Alives, computerSencodResults);
		if(mostBest!=null)
			return mostBest;
		
		mostBest = getBestPoint(human2Alives, computerSencodResults);
		if(mostBest!=null)
			return mostBest;
		
		mostBest = getBestPoint(human3HalfAlives, computerSencodResults);
		return mostBest;
	}
	
	
	//����η��������һ�����ڶ��ν���Ѿ��������ڴ˿��Դ�ǰ��ѡ����õ�����
	protected Point getBestPoint(List<SencondAnalysisResult> myBest,List<SencondAnalysisResult> yourSencodResults){
		if(!myBest.isEmpty()){
			if(myBest.size()>1){
				for (SencondAnalysisResult your : yourSencodResults) {
					if(myBest.contains(your)){
						return your.point;
					}
				}
				return myBest.get(0).point;
			}else{
				return myBest.get(0).point;
			}
		}
		return null;
	}
	
	
	//��һ�η������
	private final Map<Point,List<FirstAnalysisResult>> computerFirstResults = new HashMap<Point,List<FirstAnalysisResult>>();
	private final Map<Point,List<FirstAnalysisResult>> humanFirstResults = new HashMap<Point,List<FirstAnalysisResult>>();
	//�ڶ����ܽ��
	protected final List<SencondAnalysisResult> computerSencodResults = new ArrayList<SencondAnalysisResult>();
	protected final List<SencondAnalysisResult> humanSencodResults = new ArrayList<SencondAnalysisResult>();
	//�ڶ��ηֽ�����
	protected final List<SencondAnalysisResult> computer4HalfAlives = new ArrayList<SencondAnalysisResult>(2);
	protected final List<SencondAnalysisResult> computerDouble3Alives = new ArrayList<SencondAnalysisResult>(4);
	protected final List<SencondAnalysisResult> computer3Alives = new ArrayList<SencondAnalysisResult>(5);
	protected final List<SencondAnalysisResult> computerDouble2Alives = new ArrayList<SencondAnalysisResult>();
	protected final List<SencondAnalysisResult> computer2Alives = new ArrayList<SencondAnalysisResult>();
	protected final List<SencondAnalysisResult> computer3HalfAlives = new ArrayList<SencondAnalysisResult>();
	
	//�ڶ��ηֽ������
	protected final List<SencondAnalysisResult> human4Alives = new ArrayList<SencondAnalysisResult>(2);
	protected final List<SencondAnalysisResult> human4HalfAlives = new ArrayList<SencondAnalysisResult>(5);
	protected final List<SencondAnalysisResult> humanDouble3Alives = new ArrayList<SencondAnalysisResult>(2);
	protected final List<SencondAnalysisResult> human3Alives = new ArrayList<SencondAnalysisResult>(10);
	protected final List<SencondAnalysisResult> humanDouble2Alives = new ArrayList<SencondAnalysisResult>(3);
	protected final List<SencondAnalysisResult> human2Alives = new ArrayList<SencondAnalysisResult>();
	protected final List<SencondAnalysisResult> human3HalfAlives = new ArrayList<SencondAnalysisResult>();
	
	//��һ�η���ǰ�����һ�����ӵķ������
	private void initAnalysisResults(){
		computerFirstResults.clear();
		humanFirstResults.clear();
		//�ڶ����ܽ��
		computerSencodResults.clear();
		humanSencodResults.clear();
		//�ڶ��ηֽ��
		computer4HalfAlives.clear();
		computerDouble3Alives.clear();
		computer3Alives.clear();
		computerDouble2Alives.clear();
		computer2Alives.clear();
		computer3HalfAlives.clear();
		
		//�ڶ��ηֽ������
		human4Alives.clear();
		human4HalfAlives.clear();
		humanDouble3Alives.clear();
		human3Alives.clear();
		humanDouble2Alives.clear();
		human2Alives.clear();
		human3HalfAlives.clear();
		System.gc();
	}
	
	//���뵽��һ�η��������
	private void addToFirstAnalysisResult(FirstAnalysisResult result,Map<Point,List<FirstAnalysisResult>> dest){
		if(dest.containsKey(result.point)){
			dest.get(result.point).add(result);
		}else{
			List<FirstAnalysisResult> list = new ArrayList<FirstAnalysisResult>(1);
			list.add(result);
			dest.put(result.point, list);
		}
	}
	
	
	//��һ�η��������
	private class FirstAnalysisResult{
		//������
		int count;
		//��λ
		Point point;
		//����
		int direction;
		//״̬
		int aliveState;
		private FirstAnalysisResult(int count, Point point, int direction) {
			this(count, point, direction, ALIVE);
		}
		
		private FirstAnalysisResult(int count, Point point, int direction,int aliveState) {
			this.count = count;
			this.point = point;
			this.direction = direction;
			this.aliveState = aliveState;
		}
		

		
		private FirstAnalysisResult init(Point point,int direction,int aliveState){
			this.count = 1;
			this.point = point;
			this.direction = direction;
			this.aliveState = aliveState;
			return this;
		}
		
		private FirstAnalysisResult cloneMe(){
			return new FirstAnalysisResult(count, point, direction,aliveState);
		}
		
	}
	
	//�ڶ��η��������
	class SencondAnalysisResult implements Comparable<SencondAnalysisResult>{
		int alive4 = 0;
		//��3����
		int alive3 = 0;
		//���4��һͷ���
		int halfAlive4 = 0;
		//���3��һͷ���
		int halfAlive3 = 0;
		//��2����
		int alive2 = 0;
		//��λ
		Point point;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((point == null) ? 0 : point.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			SencondAnalysisResult other = (SencondAnalysisResult) obj;
			if (point == null) {
				if (other.point != null)
					return false;
			} else if (!point.equals(other.point))
				return false;
			return true;
		}

		private SencondAnalysisResult(Point point) {
			this.point = point;
		}
		
		
		//����η���ʱ���Եڶ��η������������򣬴�Ϊ����ص�����
		@Override
		public int compareTo(SencondAnalysisResult another) {
			return compareTowResult(this, another);
		}
			
	}
	
	//����-1���һ���������ȣ�1��ڶ����������ȣ�0��ԭ��˳��
	private int compareTowResult(SencondAnalysisResult oneResult,SencondAnalysisResult another){
		if(oneResult.alive4>another.alive4){
			return -1;
		}
		if(oneResult.alive4<another.alive4){
			return 1;
		}
		if(oneResult.halfAlive4>another.halfAlive4){
			return -1;
		}
		if(oneResult.halfAlive4<another.halfAlive4){
			return 1;
		}
		if(oneResult.alive3>another.alive3){
			return -1;
		}
		if(oneResult.alive3<another.alive3){
			return 1;
		}
		if(oneResult.alive2>another.alive2){
			return -1;
		}
		if(oneResult.alive2<another.alive2){
			return 1;
		}
		if(oneResult.halfAlive3>another.halfAlive3){
			return -1;
		}
		if(oneResult.halfAlive3>another.halfAlive3){
			return 1;
		}
		return 0;
	}
	
	
	//һ����ʱ���󣬹���һ�η���ʱ��ʱ��ŷ������ʹ�ã����������л�1���ϣ��������Ľ���������cloneMe������ý���������˽��
	private final FirstAnalysisResult far = new FirstAnalysisResult(1, null, HENG);
	// ��������ڵ�ǰλ��һ�ӣ����γ�ĳ�������϶��ٸ��ӣ�����ǰ�������µ����е㣬��ǰҪ����ĵ㣬��Ҫ�жϵķ���
	private FirstAnalysisResult tryAndCountResult(List<Point> myPoints,List<Point> enemyPoints, Point point,int direction) {
		int x = point.getX();
		int y = point.getY();
		FirstAnalysisResult fr = null;
		
		int maxCountOnThisDirection = maxCountOnThisDirection(point, enemyPoints, direction, 1);
		if(maxCountOnThisDirection<5){
			//�����������
			return null;//�˷����������λ�����ų����µ�����
		}else if(maxCountOnThisDirection==5){
			//����״̬������һͷͨ
			fr = far.init(point, direction,HALF_ALIVE);
		}else{
			//��ͷ��ͨ
			fr = far.init(point, direction,ALIVE);
		}
		
		//��ǰ�ͺ�ķ����ϼ���һ��
		countPoint(myPoints,enemyPoints,point.setX(x).setY(y),fr,direction,FORWARD);
		countPoint(myPoints,enemyPoints,point.setX(x).setY(y),fr,direction,BACKWARD);
		
		
		if(fr.count<=1 || (fr.count==2 && fr.aliveState==HALF_ALIVE)){//��1�����2�������½������
			return null;
		}
		//���ظ��ƵĽ��
		return fr.cloneMe();
	}
	
	//���ӳ���ǽ
	private boolean isOutSideOfWall(Point point,int direction){
		if(direction==HENG){
			return point.getX()<0 || point.getX()>=maxX;//����X��Yֵ����ǽ�������õȺ�
		}else if(direction==ZHONG){
			return point.getY()<0 || point.getY()>=maxY;
		}else{//�������������
			return point.getX()<0 || point.getY()<0 || point.getX()>=maxX || point.getY()>=maxY;
		}
	}
	
	private Point pointToNext(Point point,int direction,boolean forward){
		switch (direction) {
			case HENG:
				if(forward)
					point.x++;
				else
					point.x--;
				break;
			case ZHONG:
				if(forward)
					point.y++;
				else
					point.y--;
				break;
			case ZHENG_XIE:
				if(forward){
					point.x++;
					point.y--;
				}else{
					point.x--;
					point.y++;
				}
				break;
			case FAN_XIE:
				if(forward){
					point.x++;
					point.y++;
				}else{
					point.x--;
					point.y--;
				}
				break;
		}
		return point;
	}
	
	//��ĳ�����򣨰˸��е�һ�������¶������ӣ���������ǵ�һ�����еĺ��ķ���
	private void countPoint(List<Point> myPoints, List<Point> enemyPoints, Point point, FirstAnalysisResult fr,int direction,boolean forward) {
		if(myPoints.contains(pointToNext(point,direction,forward))){
			fr.count ++;
			if(myPoints.contains(pointToNext(point,direction,forward))){
				fr.count ++;
				if(myPoints.contains(pointToNext(point,direction,forward))){
					fr.count ++;
					if(myPoints.contains(pointToNext(point,direction,forward))){
						fr.count ++;
					}else if(enemyPoints.contains(point) || isOutSideOfWall(point,direction)){
						fr.aliveState=HALF_ALIVE;
					}
				}else if(enemyPoints.contains(point) || isOutSideOfWall(point,direction)){
					fr.aliveState=HALF_ALIVE;
				}
			}else if(enemyPoints.contains(point) || isOutSideOfWall(point,direction)){
				fr.aliveState=HALF_ALIVE;
			}
		}else if(enemyPoints.contains(point) || isOutSideOfWall(point,direction)){
			fr.aliveState=HALF_ALIVE;
		}
	}
	
	

	//��ĳ���������Ƿ����µ����������
	private int maxCountOnThisDirection(Point point,List<Point> enemyPoints,int direction,int count){
		int x=point.getX(),y=point.getY();
		switch (direction) {
		//����
		case HENG:
			while (!enemyPoints.contains(point.setX(point.getX()-1)) && point.getX()>=0 && count<6) {
				count ++;
			}
			point.setX(x);
			while (!enemyPoints.contains(point.setX(point.getX()+1)) && point.getX()<maxX && count<6) {
				count ++;
			}
			break;
		//����
		case ZHONG:
			while (!enemyPoints.contains(point.setY(point.getY()-1)) && point.getY()>=0) {
				count ++;
			}
			point.setY(y);
			while (!enemyPoints.contains(point.setY(point.getY()+1)) && point.getY()<maxY && count<6) {
				count ++;
			}
			break;
		//��б�� /
		case ZHENG_XIE:
			while (!enemyPoints.contains(point.setX(point.getX()-1).setY(point.getY()+1)) && point.getX()>=0 && point.getY()<maxY) {
				count ++;
			}
			point.setX(x).setY(y);
			while (!enemyPoints.contains(point.setX(point.getX()+1).setY(point.getY()-1)) && point.getX()<maxX && point.getY()>=0 && count<6) {
				count ++;
			}
			break;
		//��б /
		case FAN_XIE:
			while (!enemyPoints.contains(point.setX(point.getX()-1).setY(point.getY()-1)) && point.getX()>=0 && point.getY()>=0) {
				count ++;
			}
			point.setX(x).setY(y);
			while (!enemyPoints.contains(point.setX(point.getX()+1).setY(point.getY()+1)) && point.getX()<maxX && point.getY()<maxY && count<6) {
				count ++;
			}
			break;
		}
		return count;
	}
	
	//�����ӣ�����ӿ�
	@Override
	public void run(List<Point> humans,Point p) {
		//�������µ����һ������ȥ��
		allFreePoints.remove(humans.get(humans.size()-1));
		//���Կ����µ�һ������
		Point result = doAnalysis(myPoints, humans);
		//ȥ������µ�����
		allFreePoints.remove(result);
		//���뵽���������У�������
		myPoints.add(result);
	}
}
