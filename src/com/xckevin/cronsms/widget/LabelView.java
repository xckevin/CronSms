package com.xckevin.cronsms.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xckevin.cronsms.R;
import com.xckevin.cronsms.util.ContactsQuery;
import com.xckevin.cronsms.util.ContactsQuery.Data;

public class LabelView extends ViewGroup implements View.OnClickListener {

	private int space;

	private int viewGroupHeight;

	private float downPosition;

	private List<ContactsQuery.Data> contacts;
	
	public OnLabelActionListener onLabelActionListener;

	public LabelView(Context context) {
		super(context);
		init(context);
	}

	public LabelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		space = 24;
	}

	public void setOnLabelActionListener(OnLabelActionListener onLabelActionListener) {
		this.onLabelActionListener = onLabelActionListener;
	}

	public void setContacts(List<ContactsQuery.Data> list) {
		this.contacts = list;
		reLayout();
	}

	public void appendLabel(ContactsQuery.Data data) {
		if(this.contacts == null) {
			this.contacts = new ArrayList<ContactsQuery.Data>();
		}
		if(!this.contacts.contains(data)) {
			this.contacts.add(data);
			reLayout();
		}
	}
	
	public List<ContactsQuery.Data> getContacts() {
		return this.contacts;
	}

	private void reLayout() {
		List<ContactsQuery.Data> list = this.contacts;
		if(list == null || list.size() <= 0) {
			return ;
		}
		removeAllViews();
		final LayoutInflater inflater = LayoutInflater.from(getContext());
		final int size = list.size();
		for(int i = 0; i < size; i ++) {
			ContactsQuery.Data label = list.get(i);
			View view = inflater.inflate(R.layout.view_label_item, null);
			TextView text = (TextView) view.findViewById(R.id.text);
			View delete = view.findViewById(R.id.delete);
			text.setText(label.getName());
			text.setTag(label);
			delete.setTag(label);
			text.setOnClickListener(this);
			delete.setOnClickListener(this);
			this.addView(view);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int childCount = getChildCount();
		int arg0 = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.UNSPECIFIED);
		int arg1 = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.UNSPECIFIED);
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				child.measure(arg0, arg1);
			}
		}

		int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);

		setMeasuredDimension(width, calculateHeight(width));
	}

	private int calculateHeight(int width) {
		final int childCount = getChildCount();
		final int parentWidth = width;
		int currentX = space;
		int currentY = space;
		int finalChildHeight = 0;
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			int childWidth = child.getMeasuredWidth() + child.getPaddingLeft() + child.getPaddingRight();
			int childHeight = child.getMeasuredHeight() + child.getPaddingTop() + child.getPaddingBottom();
			if (currentX + childWidth + space > parentWidth) {
				currentX = space;
				currentY += childHeight + space;
			}
			currentX += childWidth + space;
			finalChildHeight = childHeight;
		}

		return currentY + finalChildHeight + space;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int childCount = getChildCount();
		final int parentWidth = r - l;
		int currentX = space;
		int currentY = space;
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			int childWidth = child.getMeasuredWidth() + child.getPaddingLeft() + child.getPaddingRight();
			int childHeight = child.getMeasuredHeight() + child.getPaddingTop() + child.getPaddingBottom();
			if (currentX + childWidth + space > parentWidth) {
				currentX = space;
				currentY += childHeight + space;
			}
			child.layout(currentX, currentY, currentX + childWidth, currentY + childHeight);
			currentX += childWidth + space;
			viewGroupHeight = currentY;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downPosition = event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			float transLength = event.getRawY() - downPosition;
			int height = getHeight();
			if (transLength != 0) {
				float position = -transLength;
				if (position < 0) {
					position = 0;
				} else if (position > viewGroupHeight - height) {
					position = viewGroupHeight - height;
				}
				position = position < 0 ? 0 : position;
				this.scrollTo(0, (int) position);
			}
			downPosition = event.getRawY();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		onTouchEvent(ev);
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.text:
			// TODO 
			break;
		case R.id.delete:
			ContactsQuery.Data data = (Data) v.getTag();
			contacts.remove(data);
			this.removeView((View) v.getParent());
			if(onLabelActionListener != null) {
				onLabelActionListener.onDeleted(data);
			}
			break;
		default:
			break;
		}
	}
	
	public interface OnLabelActionListener {
		
		public void onDeleted(ContactsQuery.Data data);
	}

}
