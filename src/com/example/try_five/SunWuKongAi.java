package com.example.try_five;

//�����
public class SunWuKongAi extends BaseComputerAi {

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
		
		mostBest = getBestPoint(human4HalfAlives, computerSencodResults);
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
		
		mostBest = getBestPoint(humanDouble2Alives, computerSencodResults);
		if(mostBest!=null)
			return mostBest;
		
		mostBest = getBestPoint(human2Alives, computerSencodResults);
		if(mostBest!=null)
			return mostBest;
		
		mostBest = getBestPoint(human3HalfAlives, computerSencodResults);
		return mostBest;
	}
}
