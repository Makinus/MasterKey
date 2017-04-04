package com.makinus.masterkey;

import java.util.List;

import com.makinus.masterkey.utils.MasterKeyUtils;
import com.makinus.model.Card;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter{

	protected static final String SHOW = "Show";
	protected static final String HIDE = "Hide";
	private Activity activity;
	private List<Card> data;
	private static LayoutInflater inflater=null;
	protected MasterKeyUtils mKeyUtil;
	
	public LazyAdapter(Activity a, List<Card> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mKeyUtil = MasterKeyUtils.getInstance(a.getApplicationContext());
    }
 
    public int getCount() {
        return data.size();
    }
 
    public Object getItem(int position) {
        return data.get(position);
    }
 
    public long getItemId(int position) {
        return position;
    }
 
    @SuppressLint("InflateParams")
	public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);
 
        TextView cardId = (TextView)vi.findViewById(R.id.txtCardId);
        TextView account = (TextView)vi.findViewById(R.id.lAccount);
        TextView username = (TextView)vi.findViewById(R.id.txtUsername);
        final TextView password = (TextView)vi.findViewById(R.id.txtPassword);
        final TextView showHide = (TextView)vi.findViewById(R.id.txtShowHide);
        TextView imgText = (TextView)vi.findViewById(R.id.imgText);
        ImageView imgRemindMe = (ImageView)vi.findViewById(R.id.imgRemindMe);
        
        Card card = data.get(position);
        cardId.setText(""+card.getId());
        account.setText(card.getAccount());
        username.setText(card.getUsername());
        password.setText("********");
        showHide.setText(SHOW);
        imgText.setText(card.getAccount().substring(0,1));
        //setCircleView(imgText, card.getColor());
        
        if(card.getHidePwd().equals("Y")) {
        	showHide.setEnabled(false);
        	showHide.setTextColor(activity.getResources().getColor(R.color.darkgray));
        }
        else if(card.getHidePwd().equals("N")) {
        	showHide.setEnabled(true);
        	showHide.setTextColor(activity.getResources().getColor(R.color.linkcolor));
        }
        
        showHide.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(showHide.getText().equals(SHOW)) {
					showHide.setText(HIDE);
					password.setText(mKeyUtil.decrypt(data.get(position).getPassword()));
				} 
				else {
					showHide.setText(SHOW);
					password.setText("********");
				}
			}
		});
        
        if(card.getRemindMe().equals("Y")) {
        	if(mKeyUtil.isExpired(card.getUpdatedOn()))
        		imgRemindMe.setImageResource(R.drawable.ic_reminder_expired);
        	else
        		imgRemindMe.setImageResource(R.drawable.ic_reminder_active);
        }
        else if(card.getRemindMe().equals("N")) {
        	imgRemindMe.setImageResource(R.drawable.ic_reminder_inactive);
        }
        
        return vi;
    }
    
    @SuppressWarnings("unused")
	@Deprecated
    private static void setCircleView(View v, int bgColor)
    {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        //shape.setCornerRadii(new float[] { 8, 8, 8, 8, 0, 0, 0, 0 });
        shape.setSize(60, 60);
        shape.setColor(bgColor);
        shape.setStroke(1, Color.rgb(219 , 219 , 219 ));
        v.setBackground(shape);
        
    }
}
