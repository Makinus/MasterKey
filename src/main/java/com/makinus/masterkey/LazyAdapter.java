
package com.makinus.masterkey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.makinus.masterkey.utils.MasterKeyUtils;
import com.makinus.masterkey.model.Card;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter { // implements Filterable {

	protected static final String SHOW = "Show";
	protected static final String HIDE = "Hide";
	private Activity activity;
	private List<Card> cardList;
    private ArrayList<Card> filteredCardList;
    private LazyFilter lazyFilter;
	private static LayoutInflater inflater=null;
	protected MasterKeyUtils mKeyUtil;

	public LazyAdapter(Activity a, List<Card> d) {
        this.activity = a;
        this.cardList =d;
        filteredCardList = new ArrayList<Card>();
        filteredCardList.addAll(cardList);
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mKeyUtil = MasterKeyUtils.getInstance(a.getApplicationContext());
    }
 
    public int getCount() {
        return cardList.size();
    }
 
    public Object getItem(int position) {
        return cardList.get(position);
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
        final ImageView showHide = (ImageView)vi.findViewById(R.id.imgShowHide);
        TextView imgText = (TextView)vi.findViewById(R.id.imgText);
        ImageView imgRemindMe = (ImageView)vi.findViewById(R.id.imgRemindMe);
        
        Card card = cardList.get(position);
        cardId.setText(""+card.getId());
        account.setText(card.getAccount());
        username.setText(card.getUsername());
        password.setText("********");
        password.setTextColor(activity.getResources().getColor(R.color.textColor));
        //showHide.setText(SHOW);
        imgText.setText(card.getAccount().substring(0,1));
        setCircleView(imgText, card.getColor());
        
        if(card.getHidePwd().equals("Y")) {
        	showHide.setVisibility(View.INVISIBLE);
        	showHide.setBackgroundResource(R.drawable.ic_action_visibility_off);
        	//showHide.setTextColor(activity.getResources().getColor(R.color.darkgray));
        }
        else if(card.getHidePwd().equals("N")) {
        	showHide.setVisibility(View.VISIBLE);
        	showHide.setBackgroundResource(R.drawable.ic_visibility);
        	//showHide.setTextColor(activity.getResources().getColor(R.color.linkcolor));
        }
        
        /*showHide.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				password.setText(mKeyUtil.decrypt(cardList.get(position).getPassword()));
				return false;
			}
		});
        
        showHide.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(showHide.getText().equals(SHOW)) {
					//showHide.setText(HIDE);
				} 
				else {
					//showHide.setText(SHOW);
					password.setText("********");
				}
				
				//password.setText("********");
			}
		});*/
        
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
    private static void setCircleView(View v, int bgColor)
    {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        //shape.setCornerRadii(new float[] { 8, 8, 8, 8, 0, 0, 0, 0 });
        shape.setSize(110, 110);
        shape.setColor(bgColor);
        //shape.setStroke(1, Color.rgb(219 , 219 , 219 ));
        v.setBackground(shape);
        
    }

    public void filter(String charText) {

        /*for(Card c: cardList) {
            filteredCardList.add(c);
        }*/
        cardList.clear();
        if (charText.isEmpty()) {
            cardList.addAll(filteredCardList);
        } else {
            charText = charText.toLowerCase(Locale.getDefault());

            for (Card c : filteredCardList) {
                if (c.getAccount().toLowerCase().contains(charText.toString().toLowerCase())) {
                    cardList.add(c);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void sort(String sortBy) {

        if(cardList.size() > 0) {
            Collections.sort(cardList);
            if (sortBy.equals("ztoa"))
                Collections.reverse(cardList);

            notifyDataSetChanged();
        }
    }

    /*@Override
    public Filter getFilter() {
        if(lazyFilter == null) {
            return new LazyFilter();
        }
        else {
            return lazyFilter;
        }
    }*/

    /**
     * Custom filter for cards list
     * Filter content in cards list according to the search text
     */
    private class LazyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<Card> tempList = new ArrayList<Card>();

                // search content in cards list
                for (Card card : cardList) {
                    if (card.getAccount().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(card);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = cardList.size();
                filterResults.values = cardList;
            }

            return filterResults;
        }

        /**
         * Notify about filtered list to ui
         * @param constraint text
         * @param results filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredCardList = (ArrayList<Card>) results.values;
            notifyDataSetChanged();
        }
    }

}
