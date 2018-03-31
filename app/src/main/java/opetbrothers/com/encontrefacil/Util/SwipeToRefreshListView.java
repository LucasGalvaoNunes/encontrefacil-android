package opetbrothers.com.encontrefacil.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class SwipeToRefreshListView extends ListView {
	
	private float REFRESH_THRESHOLD = 100;
	
	private SwipeToRefreshListener refreshListener;
	private boolean refresh = false;
	private boolean allowRefresh = false;
	private float startY = 0;

	public SwipeToRefreshListView(Context context) {
		super(context);
	}

	public SwipeToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SwipeToRefreshListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setRefreshListener(SwipeToRefreshListener listener){
		refreshListener = listener;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//Pega a posição atual do Y
		float y = event.getY();
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			//Armazena a posição Y no momento em que a tela é tocada
			startY = y;
			//Permite a atualização somente quando o primeiro item da lista
			//for arrastado
			allowRefresh = (getFirstVisiblePosition() == 0);
			break;
			
		case MotionEvent.ACTION_MOVE:
			if(allowRefresh){
				if((y - startY) > REFRESH_THRESHOLD){
					refresh = true;
				}
				else{
					refresh = false;
				}
			}
			
			break;
		case MotionEvent.ACTION_UP:
			//Executa a atualização
			if(refresh &&
					refreshListener != null){
				//Chama o método que executará a atulização
				refreshListener.onRefresh();
			}
			refresh = false;
		}

		return super.onTouchEvent(event);
	}

}
