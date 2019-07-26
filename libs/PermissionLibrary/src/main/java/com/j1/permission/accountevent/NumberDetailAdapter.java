package com.j1.permission.accountevent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.j1.permission.R;

import java.util.List;

/**
 * Created by wenjing.liu on 2019/6/17 in J1.
 * <p>
 * 联系人信息列表的适配器
 *
 * @author wenjing.liu
 */
public class NumberDetailAdapter extends BaseAdapter {
    private List<Number> numbers;
    private Context context;
    private LayoutInflater inflater;

    public NumberDetailAdapter(Context context, List<Number> contacts) {
        this.context = context;
        this.numbers = contacts;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return numbers.size();
    }

    @Override
    public Number getItem(int position) {
        return numbers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Number number = numbers.get(position);
        ContactsViewHolder holder;
        if (convertView == null) {
            holder = new ContactsViewHolder();
            convertView = inflater.inflate(R.layout.item_contact_detail, null);
            holder.selected = (ImageView) convertView.findViewById(R.id.iv_contacts_item_selected);
            holder.phoneType = (TextView) convertView.findViewById(R.id.tv_contacts_item_phone_type);
            holder.number = (TextView) convertView.findViewById(R.id.tv_contacts_item_number);
            convertView.setTag(holder);
        } else {
            holder = (ContactsViewHolder) convertView.getTag();
        }
        if (number.getIsSelected()) {
            holder.selected.setImageResource(R.drawable.icon_contacts_pick);
        }
        holder.selected.setVisibility(number.getIsSelected() ? View.VISIBLE : View.INVISIBLE);
        holder.phoneType.setText(number.getName());
        holder.number.setText(number.getNumber());
        return convertView;
    }

    private class ContactsViewHolder {
        private ImageView selected;
        private TextView phoneType;
        private TextView number;
    }
}
